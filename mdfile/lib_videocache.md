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

#### 代理缓存服务类 HttpProxyCacheServer

```

	private static final Logger LOG = LoggerFactory.getLogger("HttpProxyCacheServer");
	//本地ip地址,用于构建本地socket;
    private static final String PROXY_HOST = "127.0.0.1";

	//锁对象;
    private final Object clientsLock = new Object();
	//固定线程数线程池;
    private final ExecutorService socketProcessor = Executors.newFixedThreadPool(8);
	//线程安全容器;
    private final Map<String, HttpProxyCacheServerClients> clientsMap = new ConcurrentHashMap<>();
	//服务端socket,用于阻塞等待socket连入;
    private final ServerSocket serverSocket;
	//端口
    private final int port;
	//等待socket连接子线程;
    private final Thread waitConnectionThread;
	//server 构建配置;
    private final Config config;
	//todo
    private final Pinger pinger;
	
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
            LOG.info("Proxy cache server started. Is it alive? " + isAlive());
        } catch (IOException | InterruptedException e) {
			//中断时直接shutdown线程池;
            socketProcessor.shutdown();
            throw new IllegalStateException("Error starting local proxy server", e);
        }
    }
	
	private final class WaitRequestsRunnable implements Runnable {

        private final CountDownLatch startSignal;

        public WaitRequestsRunnable(CountDownLatch startSignal) {
            this.startSignal = startSignal;
        }

		//线程运行时,countDownLatch打开,死循环等待外部socket接入;
        @Override
        public void run() {
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

	
	private void processSocket(Socket socket) {
        try {
            GetRequest request = GetRequest.read(socket.getInputStream());
            LOG.debug("Request to cache proxy:" + request);
            String url = ProxyCacheUtils.decode(request.uri);
            if (pinger.isPingRequest(url)) {
                pinger.responseToPing(socket);
            } else {
                HttpProxyCacheServerClients clients = getClients(url);
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


```

#### **ProxySelector **

{@link ProxySelector} that ignore system default proxies for concrete host. 
ProxySelector 用于为具体的host忽略系统默认的代理; 

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
	        return ignored ? NO_PROXY_LIST : defaultProxySelector.select(uri);
	    }
	
	    @Override
	    public void connectFailed(URI uri, SocketAddress address, IOException failure) {
	        defaultProxySelector.connectFailed(uri, address, failure);
	    }
	}

```

#### Pinger


#### 配置类及构造者 Config

```

	class Config {

	    public final File cacheRoot; //自定义缓存目录;
	    public final FileNameGenerator fileNameGenerator;//自定义文件名称生成器;
	    public final DiskUsage diskUsage; //自定义缓存设置;
	    public final SourceInfoStorage sourceInfoStorage;
	
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

	// 其中默认缓存路径cacheRoot由`StorageUtils.getIndividualCacheDirectory(context)`获取;
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

	//fileNameGenerator默认配置为:
	public class Md5FileNameGenerator implements FileNameGenerator {

	    private static final int MAX_EXTENSION_LENGTH = 4;
	
		//md5加密的string + 后缀名
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

	//diskUsage默认配置为: TotalSizeLruDiskUsage(总大小限制,默认为512M) ;
	// 继承于`LruDiskUsage`,使用Lru算法;
	public abstract class LruDiskUsage implements DiskUsage {
	    ...
	    private final ExecutorService workerThread = Executors.newSingleThreadExecutor();//单一线程线程池;
	
	    @Override
	    public void touch(File file) throws IOException {
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
	
	//sourceInfoStorage 的默认配置:由简单工厂`SourceInfoStorageFactory.newSourceInfoStorage(context)`获取
	
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
	 
	

```

1.注意 `Files.setLastModifiedNow(file);` 思想; 
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




### 其他亮点展示


### 评估

基础功非常扎实,