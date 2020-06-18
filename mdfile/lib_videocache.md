@[TOC]

## android视频缓存框架 [AndroidVideoCache](https://github.com/danikula/AndroidVideoCache) 源码解析与评估 


### 引言

android中许多视频播放框架都会有切换清晰度的选项, 而最佳的播放清晰度和流畅度无非是本地播放视频了; AndroidVideoCache 允许添加缓存支持 `VideoView/MediaPlayer,ExoPlayer,或其他单行播放器`; 

基本原理为: 通过在本地构建一个服务器,再使用socket连接,通过socket读取流数据;


特征:

- 在加载流时缓存至本地中;
- 缓存资源离线工作;
- 部分加载;
- 自定义缓存限制;
- 同一个url多客户端支持;

该项目仅支持 `直接url` 媒体文件,并不支持如 `DASH, SmoothStreaming, HLS`等流媒体;

本次 代码解析版本为 `com.danikula:videocache:2.7.1`

### 使用方式

其中的一个使用方式 

![](https://img-blog.csdnimg.cn/20200601222114351.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L01ySmFydmlzRG9uZw==,size_16,color_FFFFFF,t_70)

然后通过 `String proxyUrl = ApplicationDemo.getProxy(mContext).getProxyUrl(VIDEO_URL);`  获取代理后url用于视频播放;

### 关键类解析

####  HttpProxyCacheServer 代理缓存服务类
提供配置构造者,系统入口及功能整合;

```

	private static final Logger LOG = LoggerFactory.getLogger("HttpProxyCacheServer");
	//本地ip地址,用于构建本地socket;
    private static final String PROXY_HOST = "127.0.0.1";

	//client 的锁对象;
    private final Object clientsLock = new Object();
	//固定线程数线程池;
    private final ExecutorService socketProcessor = Executors.newFixedThreadPool(8);
	//client 的 线程安全容器,key 为 url;
    private final Map<String, HttpProxyCacheServerClients> clientsMap = new ConcurrentHashMap<>();
	//服务端socket,用于阻塞等待socket连入;
    private final ServerSocket serverSocket;
	//端口
    private final int port;
	//等待socket连接子线程;
    private final Thread waitConnectionThread;
	//server 构建配置;
    private final Config config;
	//ping 系统,用于判断是否连接;
    private final Pinger pinger;
	
	//>>>>>>>> 这里是初始化的入口: 
	public HttpProxyCacheServer(Context context) {
		//使用默认的配置构建server;
        this(new Builder(context).buildConfig());
    }

    private HttpProxyCacheServer(Config config) {
        this.config = checkNotNull(config);
        try {
            InetAddress inetAddress = InetAddress.getByName(PROXY_HOST);
			//todo 使用本地ip地址建立服务端socket;
            this.serverSocket = new ServerSocket(0, 8, inetAddress);
			//服务端端口;
            this.port = serverSocket.getLocalPort();
			//ProxySelector 关键类:为当前的socket的host和端口忽略默认代理;
            IgnoreHostProxySelector.install(PROXY_HOST, port);
			//信号量 (门闩),阻塞当前线程,收到通知后继续执行;
            CountDownLatch startSignal = new CountDownLatch(1);
            this.waitConnectionThread = new Thread(new WaitRequestsRunnable(startSignal));
            this.waitConnectionThread.start();
            startSignal.await(); // freeze thread, wait for server starts
			//Pinger 关键类:
            this.pinger = new Pinger(PROXY_HOST, port);
			//使用pinger 去判断ServerSocket是否存活;
            LOG.info("Proxy cache server started. Is it alive? " + isAlive());
        } catch (IOException | InterruptedException e) {
			//中断时直接shutdown线程池;
            socketProcessor.shutdown();
            throw new IllegalStateException("Error starting local proxy server", e);
        }
    }
	
	//子线程运行
	private final class WaitRequestsRunnable implements Runnable {

        private final CountDownLatch startSignal;

        public WaitRequestsRunnable(CountDownLatch startSignal) {
            this.startSignal = startSignal;
        }

		//线程运行时,countDownLatch打开,死循环等待外部socket接入;
        @Override
        public void run() {
			//notify freezed thread;
            startSignal.countDown();
            waitForRequest();
        }
    }


	private void waitForRequest() {
        try {
			//中断时结束循环
            while (!Thread.currentThread().isInterrupted()) {
				//阻塞当前子线程(waitConnectionThread)
                Socket socket = serverSocket.accept();
                LOG.debug("Accept new socket " + socket);
				//已接入一个外部socket,线程池运行runnable,调用`processSocket(socket);`
                socketProcessor.submit(new SocketProcessorRunnable(socket));
            }
        } catch (IOException e) {
            onError(new ProxyCacheException("Error during waiting connection", e));
        }
    }

	//线程池运行
	private void processSocket(Socket socket) {
        try {
			//读取socket中输入流; 记录range 和 url 等请求数据;
            GetRequest request = GetRequest.read(socket.getInputStream());
            LOG.debug("Request to cache proxy:" + request);
			//url Decode, 此url 为 URL中定位的资源,ping或者videoUrl;
            String url = ProxyCacheUtils.decode(request.uri);
			
			//如果输入流中url 为`ping`,则返回连接状态ok;
            if (pinger.isPingRequest(url)) {
                pinger.responseToPing(socket);
            } else {
				//建立client,响应请求;
                HttpProxyCacheServerClients clients = getClients(url);
				//使用与url绑定的client处理socket输入流; 此处获取真实加载的videoUrl处理;
                clients.processRequest(request, socket);
            }
        } catch (SocketException e) {
            // There is no way to determine that client closed connection http://stackoverflow.com/a/10241044/999458
            // So just to prevent log flooding don't log stacktrace
            LOG.debug("Closing socket… Socket is closed by client.");
        } catch (ProxyCacheException | IOException e) {
            onError(new ProxyCacheException("Error processing request", e));
        } finally {
            releaseSocket(socket);
            LOG.debug("Opened connections: " + getClientsCount());
        }
    }

	//获取HttpProxyCacheServerClients 对象;
	private HttpProxyCacheServerClients getClients(String url) throws ProxyCacheException {
        synchronized (clientsLock) {
            HttpProxyCacheServerClients clients = clientsMap.get(url);
            if (clients == null) {
                clients = new HttpProxyCacheServerClients(url, config);
                clientsMap.put(url, clients);
            }
            return clients;
        }
    }

	// >>>>>>>> 2.代理videoUrl的方法入口;
	public String getProxyUrl(String url) {
        return getProxyUrl(url, true);
    }

	public String getProxyUrl(String url, boolean allowCachedFileUri) {
		//isCached 使用url 和命名生成器 判断本地是否存在缓存文件;
        if (allowCachedFileUri && isCached(url)) {
            File cacheFile = getCacheFile(url);
			//如果存在,尝试用diskUsage 的lru算法保存文件;
            touchFileSafely(cacheFile);
			//此处意为,如果已经下载完成后,直接用本地缓存文件路径播放;
            return Uri.fromFile(cacheFile).toString();
        }
		//如果serverSocket存活状态, 拼接代理VideoUrl; 加载时触发 `processSocket `方法
        return isAlive() ? appendToProxyUrl(url) : url;
    }

	//使用ping-ping ok 系统判断本地ip是否能成功连通;
	private boolean isAlive() {
		//最大尝试数3次,每次重新尝试会翻倍timeout时间;
        return pinger.ping(3, 70);   // 70+140+280=max~500ms
    }

	//>>>>>>>>> 2. 核心处理videourl,使用本地代理ip; 请求时,获取GET 的包头信息 即videoUrl或者ping;
	private String appendToProxyUrl(String url) {
        return String.format(Locale.US, "http://%s:%d/%s", PROXY_HOST, port, ProxyCacheUtils.encode(url));
    }


```

#### **java.net.ProxySelector **  代理选择
{@link ProxySelector} that ignore system default proxies for concrete host. 
ProxySelector 用于为具体的host忽略系统默认的代理; 

IgnoreHostProxySelector extends ProxySelector  修改系统默认proxySelector 忽略本地ip;

```

	//ProxySelector.java 静态代码块中会进行初始化
	public abstract class ProxySelector {
		...
		static {
	        try {
	            Class var0 = Class.forName("sun.net.spi.DefaultProxySelector");
	            if (var0 != null && ProxySelector.class.isAssignableFrom(var0)) {
	                theProxySelector = (ProxySelector)var0.newInstance();
	            }
	        } catch (Exception var1) {
	            theProxySelector = null;
	        }
	
	    }

		public static ProxySelector getDefault() {
	        SecurityManager var0 = System.getSecurityManager();
	        if (var0 != null) {
	            var0.checkPermission(SecurityConstants.GET_PROXYSELECTOR_PERMISSION);
	        }
	
	        return theProxySelector;
	    }	
	
		public static void setDefault(ProxySelector var0) {
	        SecurityManager var1 = System.getSecurityManager();
	        if (var1 != null) {
	            var1.checkPermission(SecurityConstants.SET_PROXYSELECTOR_PERMISSION);
	        }
	
	        theProxySelector = var0;
	    }	
	}
	

	class IgnoreHostProxySelector extends ProxySelector {

	    private static final List<Proxy> NO_PROXY_LIST = Arrays.asList(Proxy.NO_PROXY);
	
	    private final ProxySelector defaultProxySelector;
	    private final String hostToIgnore;
	    private final int portToIgnore;
	
	    IgnoreHostProxySelector(ProxySelector defaultProxySelector, String hostToIgnore, int portToIgnore) {
	        this.defaultProxySelector = checkNotNull(defaultProxySelector);
	        this.hostToIgnore = checkNotNull(hostToIgnore);
	        this.portToIgnore = portToIgnore;
	    }
	
	    static void install(String hostToIgnore, int portToIgnore) {
			//获取已经在静态代码块中初始化的`theProxySelector:ProxySelector`
	        ProxySelector defaultProxySelector = ProxySelector.getDefault();
			//使用本地服务端socket的host和端口 建立ProxySelector的代理类;
	        ProxySelector ignoreHostProxySelector = new IgnoreHostProxySelector(defaultProxySelector, hostToIgnore, portToIgnore);
			//设置系统默认的代理ProxySelector对象;
	        ProxySelector.setDefault(ignoreHostProxySelector);
	    }
	
	    @Override
	    public List<Proxy> select(URI uri) {
	        boolean ignored = hostToIgnore.equals(uri.getHost()) && portToIgnore == uri.getPort();
			//如果是serverSocket的host和port则直接忽略,否则交给默认处理
	        return ignored ? NO_PROXY_LIST : defaultProxySelector.select(uri);
	    }
	
	    @Override
	    public void connectFailed(URI uri, SocketAddress address, IOException failure) {
	        defaultProxySelector.connectFailed(uri, address, failure);
	    }
	}

```

#### Pinger  判断本地serverSocket是否存活

Pings {@link HttpProxyCacheServer} to make sure it works.  类似ping-pong 系统;如果请求是ping,则返回ping ok表示连接成功;

```

	//测试连接使用,判断是否正确;
	private static final String PING_REQUEST = "ping";
    private static final String PING_RESPONSE = "ping ok";

    private final ExecutorService pingExecutor = Executors.newSingleThreadExecutor();
	//记录serverSocket的host和port;
    private final String host;
    private final int port;

	//输入流为ping时的响应,向socket输出 ping ok 表示连接成功;
	void responseToPing(Socket socket) throws IOException {
        OutputStream out = socket.getOutputStream();
        out.write("HTTP/1.1 200 OK\n\n".getBytes());
        out.write(PING_RESPONSE.getBytes());
    }

	//检查serversocket 是否存活; ping- ping ok
	boolean ping(int maxAttempts, int startTimeout) {
        checkArgument(maxAttempts >= 1);
        checkArgument(startTimeout > 0);

        int timeout = startTimeout;
        int attempts = 0;
        while (attempts < maxAttempts) {
            try {
				//多线程设计 - 凭据设计; 开启异步执行`pingServer()`方法,保存结果;
                Future<Boolean> pingFuture = pingExecutor.submit(new PingCallable());
				//执行callback call方法, 取得执行结果;
                boolean pinged = pingFuture.get(timeout, MILLISECONDS);
                if (pinged) {
                    return true;
                }
            } catch (TimeoutException e) {
                LOG.warn("Error pinging server (attempt: " + attempts + ", timeout: " + timeout + "). ");
            } catch (InterruptedException | ExecutionException e) {
                LOG.error("Error pinging server due to unexpected error", e);
            }
            attempts++;
            timeout *= 2;
        }
        String error = String.format(Locale.US, "Error pinging server (attempts: %d, max timeout: %d). " +
                        "If you see this message, please, report at https://github.com/danikula/AndroidVideoCache/issues/134. " +
                        "Default proxies are: %s"
                , attempts, timeout / 2, getDefaultProxies());
        LOG.error(error, new ProxyCacheException(error));
        return false;
    }

	private boolean pingServer() throws ProxyCacheException {
		//String.format(Locale.US, "http://%s:%d/%s", host, port, PING_REQUEST);
		//http://127.0.0.1:port/ping
        String pingUrl = getPingUrl();
        HttpUrlSource source = new HttpUrlSource(pingUrl);
        try {
            byte[] expectedResponse = PING_RESPONSE.getBytes();
			//通过HttpUrlConnection 请求本地ip建立的服务器 serverSocket; 并记录信息;
            source.open(0);
            byte[] response = new byte[expectedResponse.length];
			//读取固定的大小数据;
            source.read(response);
			//如果返回ping ok 表示存活状态;
            boolean pingOk = Arrays.equals(expectedResponse, response);
            LOG.info("Ping response: `" + new String(response) + "`, pinged? " + pingOk);
            return pingOk;
        } catch (ProxyCacheException e) {
            LOG.error("Error reading ping response", e);
            return false;
        } finally {
            source.close();
        }
    }

```

#### GetRequest  封装用于获取请求中信息;
Model for Http GET request.   

http get请求,处理socket输入流,存储range 和 url中定位的资源地址; 
range 为了在 HttpUrlConnection  中使用offset 获取数据;

```
	//正则表达式匹配
	private static final Pattern RANGE_HEADER_PATTERN = Pattern.compile("[R,r]ange:[ ]?bytes=(\\d*)-");
    private static final Pattern URL_PATTERN = Pattern.compile("GET /(.*) HTTP");
	
	
    public final String uri;
    public final long rangeOffset;
    public final boolean partial;

	//read方法读取socket的输入流,拼接Stringbuilder,构建GetRequest对象;
	public GetRequest(String request) {
        checkNotNull(request);
		//使用 RANGE_HEADER_PATTERN 进行正则匹配, 并返回group(1), 即第一个`()`中的内容,即 bytes内容;
        long offset = findRangeOffset(request);
        this.rangeOffset = Math.max(0, offset);
        this.partial = offset >= 0;
		//使用 URL_PATTERN 进行正则匹配, 同样返回group(1),即第一个()中的内容; 
		//获取URL中定位的资源; 如真实的videoUrl或者ping;
        this.uri = findUri(request);
    }

```

#### HttpProxyCacheServerClients  以url为key绑定的客户端处理类
本地serverSocket服务器,接受到客户端socket接入后, 对不同的url(请求行中URL字段;ping或videoUrl),使用容器单例对不同url新建client 处理请求; 

其实大部分核心逻辑在`HttpProxyCache`中,此处提供回调和封装;

```
	
	//原子int,防止并发;
	private final AtomicInteger clientsCount = new AtomicInteger(0);
    private final String url;
	//多线程;
    private volatile HttpProxyCache proxyCache;
	//缓存获取 回调接口 , 这里特地写了一个 `发布-订阅` 模型 ,采用观察者模式 扩展 回调;
    private final List<CacheListener> listeners = new CopyOnWriteArrayList<>();
	//这是一个集成`Handler`,实现CacheListener接口的类; 
	//作为callback传入`HttpProxyCache`内,收到回调后在回调给注册的观察者listeners;
    private final CacheListener uiCacheListener;
    private final Config config;

	//如果socket输入不是ping请求,则new client 处理;
	public void processRequest(GetRequest request, Socket socket) throws ProxyCacheException, IOException {
		//建立proxyCache;
        startProcessRequest();
        try {
			//只要socket连接进入,++,跟 clientsMap 不同;
            clientsCount.incrementAndGet();
			//proxyCache (HttpProxyCache 具体类) 处理请求;
            proxyCache.processRequest(request, socket);
        } finally {
            finishProcessRequest();
        }
    }
	
	//volatile ,多线程并发处理;
	private synchronized void startProcessRequest() throws ProxyCacheException {
        proxyCache = proxyCache == null ? newHttpProxyCache() : proxyCache;
    }

	
	private HttpProxyCache newHttpProxyCache() throws ProxyCacheException {
		//建立HttpUrlSource,使用配置类中自定义属性;
        HttpUrlSource source = new HttpUrlSource(url, config.sourceInfoStorage, config.headerInjector);
		//添加缓存类,使用自定义url本地文件命名器和存储管理类,默认为(cacheRoot+md5后的url+url后缀)和lru算法;
        FileCache cache = new FileCache(config.generateCacheFile(url), config.diskUsage);
        HttpProxyCache httpProxyCache = new HttpProxyCache(source, cache);
        httpProxyCache.registerCacheListener(uiCacheListener);
        return httpProxyCache;
    }

	//继承Handler,loop为主线程轮询器;
	private static final class UiListenerHandler extends Handler implements CacheListener {
        private final String url;
        private final List<CacheListener> listeners;

        public UiListenerHandler(String url, List<CacheListener> listeners) {
            super(Looper.getMainLooper());
            this.url = url;
            this.listeners = listeners;
        }

		//HttpProxyCache 回调后, 发送message,然后通知观察者;
        @Override
        public void onCacheAvailable(File file, String url, int percentsAvailable) {
            Message message = obtainMessage();
            message.arg1 = percentsAvailable;
            message.obj = file;
            sendMessage(message);
        }

        @Override
        public void handleMessage(Message msg) {
			//通知所有注册的观察者;
            for (CacheListener cacheListener : listeners) {
                cacheListener.onCacheAvailable((File) msg.obj, url, msg.arg1);
            }
        }
    }

```

#### **HttpProxyCache** extend ProxyCache (数据处理核心类,响应数据构造及数据存储控制;)

{@link ProxyCache} that read http url and writes data to {@link Socket}

使用 HttpUrlSource 和 FileCache  进行读http url和写回数据到socket中;

```

	//>>>>>>>>  此处运行完后,初始化就告一段落了;
	//处理接入的Socket的数据,并写出数据;
	public void processRequest(GetRequest request, Socket socket) throws IOException, ProxyCacheException {
        OutputStream out = new BufferedOutputStream(socket.getOutputStream());
        String responseHeaders = newResponseHeaders(request);
        out.write(responseHeaders.getBytes("UTF-8"));

        long offset = request.rangeOffset;
		//是否使用缓存返回数据;
        if (isUseCache(request)) {
            responseWithCache(out, offset);
        } else {
            responseWithoutCache(out, offset);
        }
    }

	//响应头的构建
	private String newResponseHeaders(GetRequest request) throws IOException, ProxyCacheException {
		//HttpUrlSource获取mime,如果为空则 调用 `fetchContentInfo` 获取mime,或者length为默认值时也会触发此方法;
        String mime = source.getMime();
        boolean mimeKnown = !TextUtils.isEmpty(mime);
		//如果cache中已经完成了直接返回,否则length为默认值调用 `fetchContentInfo`
        long length = cache.isCompleted() ? cache.available() : source.length();
        boolean lengthKnown = length >= 0;
		//range offset 不为0,部分下载;
        long contentLength = request.partial ? length - request.rangeOffset : length;
        boolean addRange = lengthKnown && request.partial;
        return new StringBuilder()
                .append(request.partial ? "HTTP/1.1 206 PARTIAL CONTENT\n" : "HTTP/1.1 200 OK\n")
                .append("Accept-Ranges: bytes\n")
                .append(lengthKnown ? format("Content-Length: %d\n", contentLength) : "")
                .append(addRange ? format("Content-Range: bytes %d-%d/%d\n", request.rangeOffset, length - 1, length) : "")
                .append(mimeKnown ? format("Content-Type: %s\n", mime) : "")
                .append("\n") // headers end
                .toString();
    }

	//判断是否使用本地缓存;
	private boolean isUseCache(GetRequest request) throws ProxyCacheException {
        long sourceLength = source.length();
        boolean sourceLengthKnown = sourceLength > 0;
        long cacheAvailable = cache.available();
        // do not use cache for partial requests which too far from available cache. It seems user seek video.
		// length未知 或者 不是部分请求 或者 部分请求偏移量 <= 已缓存量(RandomAccessFile)+ 固定offset 情况下使用缓存;
        return !sourceLengthKnown || !request.partial || request.rangeOffset <= cacheAvailable + sourceLength * NO_CACHE_BARRIER;
    }

	//使用本地缓存的情况下,写出给Socket;
	private void responseWithCache(OutputStream out, long offset) throws ProxyCacheException, IOException {
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];//8 * 1024
        int readBytes;
		//ProxyCache.read(), 每次取DEFAULT_BUFFER_SIZE的数据;
        while ((readBytes = read(buffer, offset, buffer.length)) != -1) {
            out.write(buffer, 0, readBytes);
            offset += readBytes;
        }
        out.flush();
    }

	//不使用本地缓存的情况
	private void responseWithoutCache(OutputStream out, long offset) throws ProxyCacheException, IOException {
        HttpUrlSource newSourceNoCache = new HttpUrlSource(this.source);
        try {
			//打开inputStream,获取source信息,用于后面的read数据;
            newSourceNoCache.open((int) offset);
            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
            int readBytes;
			//向socket的输出流写出数据; HttpUrlSource.read(); 即正常下载了;
            while ((readBytes = newSourceNoCache.read(buffer)) != -1) {
                out.write(buffer, 0, readBytes);
                offset += readBytes;
            }
            out.flush();
        } finally {
            newSourceNoCache.close();
        }
    }

------------------

	//ProxyCache.java 
	public int read(byte[] buffer, long offset, int length) throws ProxyCacheException {
        ProxyCacheUtils.assertBuffer(buffer, offset, length);
		
		//临时文件没有完成 cache的RandomAccessFile大小< offset + buffer大小  没有被shutdown , 则一直在循环中;
        while (!cache.isCompleted() && cache.available() < (offset + length) && !stopped) {
			//开启一个线程异步下载数据到本地文件中,没下载完一直处于死循环中;
            readSourceAsync();
			//此处是线程池中运行,等待1s;
            waitForSourceData();
			//检查是否有错误;
            checkReadSourceErrorsCount();
        }
		//下载会把数据存入本地,读取本地中的数据;
        int read = cache.read(buffer, offset, length);
		//使用缓存的数据,回调监听;
        if (cache.isCompleted() && percentsAvailable != 100) {
            percentsAvailable = 100;
            onCachePercentsAvailableChanged(100);
        }
        return read;
    }

	//异步读取资源;
	private synchronized void readSourceAsync() throws ProxyCacheException {
        boolean readingInProgress = sourceReaderThread != null && sourceReaderThread.getState() != Thread.State.TERMINATED;
        if (!stopped && !cache.isCompleted() && !readingInProgress) {
			//每次read读取都会开启一个线程 ,Runnable 做 `readSource` 操作;
            sourceReaderThread = new Thread(new SourceReaderRunnable(), "Source reader for " + source);
            sourceReaderThread.start();
        }
    }

	//while 循环中,下载完才会退出;
	private void readSource() {
        long sourceAvailable = -1;
        long offset = 0;
        try {
			//RandomAccessFile 的 已获得大小
            offset = cache.available();
			// HttpUrlSource open 断点下载,open 打开inputStream,用于后面read;
            source.open(offset);
            sourceAvailable = source.length();
            byte[] buffer = new byte[ProxyCacheUtils.DEFAULT_BUFFER_SIZE];
            int readBytes;
			//用byte[]不断的读取inputStream;
            while ((readBytes = source.read(buffer)) != -1) {
                synchronized (stopLock) {
                    if (isStopped()) {
                        return;
                    }
					//FileCache 的 RandomAccessFile 拼接数据, 通过RandomAccessFile写到本地临时文件中作为缓存;
                    cache.append(buffer, readBytes);
                }
                offset += readBytes;
				//通知监听;
                notifyNewCacheDataAvailable(offset, sourceAvailable);
            }
			//下载完毕,调用FileCache complete(),去除临时文件名,更改file名称;
            tryComplete();
			//percent 100,通知监听;
            onSourceRead();
        } catch (Throwable e) {
            readSourceErrorsCount.incrementAndGet();
            onError(e);
        } finally {
            closeSource();
            notifyNewCacheDataAvailable(offset, sourceAvailable);
        }
    }

	private void notifyNewCacheDataAvailable(long cacheAvailable, long sourceAvailable) {
        onCacheAvailable(cacheAvailable, sourceAvailable);
		//notify ` waitForSourceData()`
        synchronized (wc) {
            wc.notifyAll();
        }
    }

	//获取进度回调;
	protected void onCacheAvailable(long cacheAvailable, long sourceLength) {
        boolean zeroLengthSource = sourceLength == 0;
        int percents = zeroLengthSource ? 100 : (int) ((float) cacheAvailable / sourceLength * 100);
        boolean percentsChanged = percents != percentsAvailable;
        boolean sourceLengthKnown = sourceLength >= 0;
		//正在下载中的时候,回调数据到 UiListenerHandler(Handler),发送message,启动观察者;
        if (sourceLengthKnown && percentsChanged) {
            onCachePercentsAvailableChanged(percents);
        }
        percentsAvailable = percents;
    }

	//开启下载异步线程后; 因为这里处于线程池中运行,这里等待1s;
	private void waitForSourceData() throws ProxyCacheException {
        synchronized (wc) {
            try {
                wc.wait(1000);
            } catch (InterruptedException e) {
                throw new ProxyCacheException("Waiting source data is interrupted!", e);
            }
        }
    }

```

##### HttpUrlSource   数据网络获取 (HttpURLConnection实现)

{@link Source} that uses http resource as source for {@link ProxyCache}.

提供 `open`, `length`,`read`,`close`等接口方法; 使用Config中定义的属性进行数据处理;


```

	//如果sourceInfo值为默认值,则触发此方法连接和更新SourceInfo; 建立连接,获取数据信息;
	private void fetchContentInfo() throws ProxyCacheException {
        LOG.debug("Read content info from " + sourceInfo.url);
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = openConnection(0, 10000);
			//Content-Length
            long length = getContentLength(urlConnection);	
			//Content-Type
            String mime = urlConnection.getContentType();
            inputStream = urlConnection.getInputStream();
            this.sourceInfo = new SourceInfo(sourceInfo.url, length, mime);
			//默认是存储在本地数据库;
            this.sourceInfoStorage.put(sourceInfo.url, sourceInfo);
            LOG.debug("Source info fetched: " + sourceInfo);
        } catch (IOException e) {
            LOG.error("Error fetching info from " + sourceInfo.url, e);
        } finally {
            ProxyCacheUtils.close(inputStream);
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }

	//使用HttpURLConnection 进行网络请求;
	private HttpURLConnection openConnection(long offset, int timeout) throws IOException, ProxyCacheException {
        HttpURLConnection connection;
        boolean redirected;
        int redirectCount = 0;
        String url = this.sourceInfo.url;
        do {
            LOG.debug("Open connection " + (offset > 0 ? " with offset " + offset : "") + " to " + url);
            connection = (HttpURLConnection) new URL(url).openConnection();
			//使用配置项中自定义Header;
            injectCustomHeaders(connection, url);
			//GetRequest 中的 Range 参数;
            if (offset > 0) {
                connection.setRequestProperty("Range", "bytes=" + offset + "-");
            }
            if (timeout > 0) {
                connection.setConnectTimeout(timeout);
                connection.setReadTimeout(timeout);
            }
            int code = connection.getResponseCode();
			//301||302||303 重定向处理;
            redirected = code == HTTP_MOVED_PERM || code == HTTP_MOVED_TEMP || code == HTTP_SEE_OTHER;
            if (redirected) {
                url = connection.getHeaderField("Location");
                redirectCount++;
                connection.disconnect();
            }
			//默认 最大重定向5次;
            if (redirectCount > MAX_REDIRECTS) {
                throw new ProxyCacheException("Too many redirects: " + redirectCount);
            }
        } while (redirected);
        return connection;
    }

	
	//Opens source. Source should be open before using {@link #read(byte[])} 
	 @Override
    public void open(long offset) throws ProxyCacheException {
        try {
			//从offset 处,断点下载;
            connection = openConnection(offset, -1);
            String mime = connection.getContentType();
			//建立连接,获取流数据 inputStream;
            inputStream = new BufferedInputStream(connection.getInputStream(), DEFAULT_BUFFER_SIZE);
			//如果是部分连接,则为ContentLength+offset; 否则ContentLength;
            long length = readSourceAvailableBytes(connection, offset, connection.getResponseCode());
			//更新数据;
            this.sourceInfo = new SourceInfo(sourceInfo.url, length, mime);
			//存储信息进本地;
            this.sourceInfoStorage.put(sourceInfo.url, sourceInfo);
        } catch (IOException e) {
            throw new ProxyCacheException("Error opening connection for " + sourceInfo.url + " with offset " + offset, e);
        }
    }

	//Read data to byte buffer from source with current offset.  每次默认大小的获取流数据;
	 @Override
    public int read(byte[] buffer) throws ProxyCacheException {
        ...
        try {
			//流中读取数据;
            return inputStream.read(buffer, 0, buffer.length);
        }...
    }

```

##### FileCache 数据缓存类 LRU

{@link Cache} that uses file for storing data. 

提供 `available,read,append,close,complete,isCompleted`等接口方法;

```
	
	//临时文件后缀;
	private static final String TEMP_POSTFIX = ".download";

    private final DiskUsage diskUsage;
	
    public File file;
    private RandomAccessFile dataFile;

	public FileCache(File file, DiskUsage diskUsage) throws ProxyCacheException {
        try {
            if (diskUsage == null) {
                throw new NullPointerException();
            }
            this.diskUsage = diskUsage;
			//file:  /cacheRoot/默认md5加密的文件名.后缀名; directory: /cacheRoot
            File directory = file.getParentFile();
			//建立目录;
            Files.makeDir(directory);
            boolean completed = file.exists();
			//建立文件 /cacheRoot/默认md5加密文件名.后缀名+ 临时文件后缀;
            this.file = completed ? file : new File(file.getParentFile(), file.getName() + TEMP_POSTFIX);
            this.dataFile = new RandomAccessFile(this.file, completed ? "r" : "rw");
        } catch (IOException e) {
            throw new ProxyCacheException("Error using file " + file + " as disc cache", e);
        }
    }

	@Override
    public synchronized void append(byte[] data, int length) throws ProxyCacheException {
        try {
            ...	
			//下载完数据,写入本地File (RandomAccessFile)
            dataFile.seek(available());
            dataFile.write(data, 0, length);
        } ...
    }

	@Override
    public synchronized void complete() throws ProxyCacheException {
        if (isCompleted()) {
            return;
        }

        close();
		//去除临时后缀名;
        String fileName = file.getName().substring(0, file.getName().length() - TEMP_POSTFIX.length());
        File completedFile = new File(file.getParentFile(), fileName);
		//重命名本地存储文件;
        boolean renamed = file.renameTo(completedFile);
        if (!renamed) {
            throw new ProxyCacheException("Error renaming file " + file + " to " + completedFile + " for completion!");
        }
        file = completedFile;
        try {
            dataFile = new RandomAccessFile(file, "r");
			//默认Lru算法存储文件;
            diskUsage.touch(file);
        } catch (IOException e) {
            throw new ProxyCacheException("Error opening " + file + " as disc cache", e);
        }
    }

	//读取缓存中的数据;
	@Override
    public synchronized int read(byte[] buffer, long offset, int length) throws ProxyCacheException {
        try {
            dataFile.seek(offset);
            return dataFile.read(buffer, 0, length);
        } catch (IOException e) {
            String format = "Error reading %d bytes with offset %d from file[%d bytes] to buffer[%d bytes]";
            throw new ProxyCacheException(String.format(format, length, offset, available(), buffer.length), e);
        }
    }


```

#### Config 配置类及构造者 

构造者模式,方便设置自定义参数;

```
	
	class Config {

	    public final File cacheRoot; //自定义缓存目录;
	    public final FileNameGenerator fileNameGenerator;//自定义文件名称生成器;
	    public final DiskUsage diskUsage; //自定义缓存管理设置 (存储至本地);
	    public final SourceInfoStorage sourceInfoStorage;//自定义数据信息存储(url,length,mime等数据);
		public final HeaderInjector headerInjector; //自定义添加请求头数据;
	
	    Config(File cacheRoot, FileNameGenerator fileNameGenerator, DiskUsage diskUsage, SourceInfoStorage sourceInfoStorage) {
	        this.cacheRoot = cacheRoot;
	        this.fileNameGenerator = fileNameGenerator;
	        this.diskUsage = diskUsage;
	        this.sourceInfoStorage = sourceInfoStorage;
	    }
	
	    File generateCacheFile(String url) {
	        String name = fileNameGenerator.generate(url);
	        return new File(cacheRoot, name);
	    }

	}

	//**默认参数获取:**

	// 其中默认缓存路径cacheRoot:由`StorageUtils.getIndividualCacheDirectory(context)`获取;
	// 定义缓存目录的方法如下:
	private static File getCacheDirectory(Context context, boolean preferExternal) {
        File appCacheDir = null;
        String externalStorageState;
        try {
            externalStorageState = Environment.getExternalStorageState();
        } catch (NullPointerException e) { // (sh)it happens
            externalStorageState = "";
        }
        if (preferExternal && MEDIA_MOUNTED.equals(externalStorageState)) {
			//sd卡存储路径/Android/data/[app_package_name]/cache/
            appCacheDir = getExternalCacheDir(context);
        }
        if (appCacheDir == null) {
			//手机 devices file system;
            appCacheDir = context.getCacheDir();
        }
        if (appCacheDir == null) {
			///data/data/[app_package_name]/cache/
            String cacheDirPath = "/data/data/" + context.getPackageName() + "/cache/";
            LOG.warn("Can't define system cache directory! '" + cacheDirPath + "%s' will be used.");
            appCacheDir = new File(cacheDirPath);
        }
		//然后在拼接 `/video-cache/` 路径;
        return appCacheDir;
    }

    private static File getExternalCacheDir(Context context) {
        File dataDir = new File(new File(Environment.getExternalStorageDirectory(), "Android"), "data");
        File appCacheDir = new File(new File(dataDir, context.getPackageName()), "cache");
        if (!appCacheDir.exists()) {
            if (!appCacheDir.mkdirs()) {
                LOG.warn("Unable to create external cache directory");
                return null;
            }
        }
        return appCacheDir;
    }

	//fileNameGenerator 本地存储文件命名管理默认配置为:
	public class Md5FileNameGenerator implements FileNameGenerator {

	    private static final int MAX_EXTENSION_LENGTH = 4;
	
		//md5加密的url + url后缀名
	    @Override
	    public String generate(String url) {
	        String extension = getExtension(url);
	        String name = ProxyCacheUtils.computeMD5(url);
	        return TextUtils.isEmpty(extension) ? name : name + "." + extension;
	    }

		//获取url的后缀名 ,如mp4;
	    private String getExtension(String url) {
	        int dotIndex = url.lastIndexOf('.');
	        int slashIndex = url.lastIndexOf('/');
	        return dotIndex != -1 && dotIndex > slashIndex && dotIndex + 2 + MAX_EXTENSION_LENGTH > url.length() ?
	                url.substring(dotIndex + 1, url.length()) : "";
	    }
	}

	//diskUsage 存储管理默认配置为: TotalSizeLruDiskUsage(总大小限制,默认为512M) ;
	// 继承于`LruDiskUsage`,使用Lru算法;
	public abstract class LruDiskUsage implements DiskUsage {
	    ...
	    private final ExecutorService workerThread = Executors.newSingleThreadExecutor();//单一线程线程池;
	
	    @Override
	    public void touch(File file) throws IOException {
			//异步提交,保存文件;
	        workerThread.submit(new TouchCallable(file));
	    }
	
	    private void touchInBackground(File file) throws IOException {
			//修改file 更改时间; 用于Lru算法判断最近使用;
	        Files.setLastModifiedNow(file);
			//获取指定文件夹中文件,按修改时间排序,时间小的放前面,按照从小到大排序;
	        List<File> files = Files.getLruListFiles(file.getParentFile());
	        trim(files);
	    }
	
		//抽象方法,用于判断以 文件大小 还是 文件数目 作为lru删除条件;
	    protected abstract boolean accept(File file, long totalSize, int totalCount);
	
	    private void trim(List<File> files) {
	        long totalSize = countTotalSize(files);
	        int totalCount = files.size();
	        for (File file : files) {
				//先遍历的是时间小的,就是比较旧的数据,可优先删除;
	            boolean accepted = accept(file, totalSize, totalCount);
	            if (!accepted) {
	                long fileSize = file.length();
	                boolean deleted = file.delete();
	                if (deleted) {
	                    totalCount--;
	                    totalSize -= fileSize;
	                    LOG.info("Cache file " + file + " is deleted because it exceeds cache limit");
	                } else {
	                    LOG.error("Error deleting file " + file + " for trimming cache");
	                }
	            }
	        }
	    }
	
	    private long countTotalSize(List<File> files) {
	        long totalSize = 0;
	        for (File file : files) {
	            totalSize += file.length();
	        }
	        return totalSize;
	    }
	
	    private class TouchCallable implements Callable<Void> {
	
	        private final File file;
	
	        public TouchCallable(File file) {
	            this.file = file;
	        }
	
	        @Override
	        public Void call() throws Exception {
	            touchInBackground(file);
	            return null;
	        }
	    }
	}
	
	//sourceInfoStorage 数据本地存储的默认配置:由简单工厂`SourceInfoStorageFactory.newSourceInfoStorage(context)`获取
	public interface SourceInfoStorage {
	    SourceInfo get(String url);
	    void put(String url, SourceInfo sourceInfo);
	    void release();
	}

	//使用sqlite 作为数据存储; 
	class DatabaseSourceInfoStorage extends SQLiteOpenHelper implements SourceInfoStorage{
		private static final String TABLE = "SourceInfo";
	    private static final String COLUMN_ID = "_id";
	    private static final String COLUMN_URL = "url";
	    private static final String COLUMN_LENGTH = "length";
	    private static final String COLUMN_MIME = "mime";
	    private static final String[] ALL_COLUMNS = new String[]{COLUMN_ID, COLUMN_URL, COLUMN_LENGTH, COLUMN_MIME};
	    private static final String CREATE_SQL =
	            "CREATE TABLE " + TABLE + " (" +
	                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
	                    COLUMN_URL + " TEXT NOT NULL," +
	                    COLUMN_MIME + " TEXT," +
	                    COLUMN_LENGTH + " INTEGER" +
	                    ");";
		...
	}
	 
	//headerInjector 添加请求头的默认配置: 
	public class EmptyHeadersInjector implements HeaderInjector {
	    @Override
	    public Map<String, String> addHeaders(String url) {
	        return new HashMap<>();
	    }
	
	}
	

```

**tips**:		注意 `Files.setLastModifiedNow(file);`  最后更改时间作为Lru的判断标准;  这里兼容时间更改的判断;
[RandomAccessFile mode](https://www.cnblogs.com/attilax/p/5274340.html)

```
	
	static void setLastModifiedNow(File file) throws IOException {
        if (file.exists()) {
            long now = System.currentTimeMillis();
			//更改最后修改时间;
            boolean modified = file.setLastModified(now); // on some devices (e.g. Nexus 5) doesn't work
            if (!modified) {
                modify(file);
                if (file.lastModified() < now) {
                    // NOTE: apparently this is a known issue (see: http://stackoverflow.com/questions/6633748/file-lastmodified-is-never-what-was-set-with-file-setlastmodified)
                    LOG.warn("Last modified date {} is not set for file {}", new Date(file.lastModified()), file.getAbsolutePath());
                }
            }
        }
    }
	//如果更改不成功,则采用`rwd`更新文件内容,写入文件最后一个byte;
    static void modify(File file) throws IOException {
        long size = file.length();
        if (size == 0) {
            recreateZeroSizeFile(file);
            return;
        }

        RandomAccessFile accessFile = new RandomAccessFile(file, "rwd");
        accessFile.seek(size - 1);
        byte lastByte = accessFile.readByte();
        accessFile.seek(size - 1);
        accessFile.write(lastByte);
        accessFile.close();
    }

```


### 数据展示Demo

代码逻辑理清之后,在看数据就会更为清晰了;


//ping 请求,serverSocket 收到的信息:
![在这里插入图片描述](https://img-blog.csdnimg.cn/2020061213361885.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L01ySmFydmlzRG9uZw==,size_16,color_FFFFFF,t_70)

//videoUrl 请求, serverSocket 收到的信息:
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200612133129343.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L01ySmFydmlzRG9uZw==,size_16,color_FFFFFF,t_70)


当启动ServerSocket后,本地发出ping请求检测是否存活,请求信息为:

```

	GET /ping HTTP/1.1
	User-Agent: Dalvik/2.1.0 (Linux; U; Android 5.1; m1 note Build/LMY47D)
	Host: 127.0.0.1:49361
	Connection: Keep-Alive
	Accept-Encoding: gzip

```

videoUrl请求信息为:

```
	
	GET /http%3A%2F%2Fjzvd.nathen.cn%2F342a5f7ef6124a4a8faf00e738b8bee4%2Fcf6d9db0bd4d41f59d09ea0a81e918fd-5287d2089db37e62345123a1be272f8b.mp4 HTTP/1.1
	User-Agent: stagefright/1.2 (Linux;Android 5.1)
	key: value
	Host: 127.0.0.1:59689
	Connection: Keep-Alive
	Accept-Encoding: gzip

```

测试中原videoUrl 路径为: 

`http://jzvd.nathen.cn/342a5f7ef6124a4a8faf00e738b8bee4/cf6d9db0bd4d41f59d09ea0a81e918fd-xxxxx.mp4`

isAlive 经过本地代理后的videoUrl(appendToProxyUrl方法) 路径为:

`http://127.0.0.1:43108/http%3A%2F%2Fjzvd.nathen.cn%2F342a5f7ef6124a4a8faf00e738b8bee4%2Fcf6d9db0bd4d41f59d09ea0a81e918fd-xxxxx.mp4`




### 评估

首先作者基础功非常扎实, 对http请求,proxy代理,以及流数据的处理封装都非常棒,非常值得学习的一个视频缓存框架;