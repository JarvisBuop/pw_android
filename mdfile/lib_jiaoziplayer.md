## android 视频播放器框架 [饺子播放器](https://github.com/Jzvd/JiaoZiVideoPlayer) 源码解析与评估 

### 前言

使用的源码版本为`7.3.0`,饺子播放器源码设计精良,是个狠人,非常值得学习,在看源码的过程中觉得有些地方非常值得记录一下; 

代码挺多,本着不求甚解的解读原则,把代码中的亮点或者常用功能点的代码详细摘录下来;

---
### Library 主要类

> JZDataSource 配置类;

主要是包括 视频播放配置,播放列表续播等数据; 

```

	public static final String URL_KEY_DEFAULT = "URL_KEY_DEFAULT";

    public int currentUrlIndex; 
    public LinkedHashMap urlsMap = new LinkedHashMap();
    public String title = ""; //配置title
    public HashMap<String, String> headerMap = new HashMap<>();
    public boolean looping = false; //播放全局配置;
    public Object[] objects;

```

> JZMediaInterface 播放引擎抽象

作为可切换播放引擎的抽象, 使用TextureView 作为显示容器; 

```

	xxx implements TextureView.SurfaceTextureListener

	public static SurfaceTexture SAVED_SURFACE; //textureture的显示参数;
	//使用发送消息到子线程的handerThread;
    public HandlerThread mMediaHandlerThread; 
	//绑定handerThread的handler,发送消息到子线程的消息队列;
    public Handler mMediaHandler;
	//发送消息到主线程的消息队列;
    public Handler handler;
    public Jzvd jzvd;

```

> JZMediaSystem 使用系统默认播放引擎实现 

该类作为MediaPlayer的代理,使用系统提供的MediaPlayer作为视频的播放;

```

	xxx extends JZMediaInterface

	//prepare方法;
	@Override
    public void prepare() {
        release();
		//新建子线程HandlerThread 搭配 Handler,发送消息至子线程的消息队列;
        mMediaHandlerThread = new HandlerThread("JZVD");
        mMediaHandlerThread.start();
        mMediaHandler = new Handler(mMediaHandlerThread.getLooper());//主线程还是非主线程，就在这里
		//主线程建立的Handler,发送至主线程的消息队列;
        handler = new Handler();

        mMediaHandler.post(() -> {
			//sub-thread 设置默认参数
            try {
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.setLooping(jzvd.jzDataSource.looping);
                mediaPlayer.setOnPreparedListener(JZMediaSystem.this);
                mediaPlayer.setOnCompletionListener(JZMediaSystem.this);
                mediaPlayer.setOnBufferingUpdateListener(JZMediaSystem.this);
                mediaPlayer.setScreenOnWhilePlaying(true);
                mediaPlayer.setOnSeekCompleteListener(JZMediaSystem.this);
                mediaPlayer.setOnErrorListener(JZMediaSystem.this);
                mediaPlayer.setOnInfoListener(JZMediaSystem.this);
                mediaPlayer.setOnVideoSizeChangedListener(JZMediaSystem.this);
				//调用Mediaplayer的setDataSource方法设置视频源;
                Class<MediaPlayer> clazz = MediaPlayer.class;
                Method method = clazz.getDeclaredMethod("setDataSource", String.class, Map.class);
                method.invoke(mediaPlayer, jzvd.jzDataSource.getCurrentUrl().toString(), jzvd.jzDataSource.headerMap);
                mediaPlayer.prepareAsync();
                mediaPlayer.setSurface(new Surface(SAVED_SURFACE));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

	//release
	@Override
    public void release() {//not perfect change you later
        if (mMediaHandler != null && mMediaHandlerThread != null && mediaPlayer != null) {//不知道有没有妖孽
            HandlerThread tmpHandlerThread = mMediaHandlerThread;
            MediaPlayer tmpMediaPlayer = mediaPlayer;
            JZMediaInterface.SAVED_SURFACE = null;

            mMediaHandler.post(() -> {
                tmpMediaPlayer.setSurface(null);
                tmpMediaPlayer.release();
                tmpHandlerThread.quit();
            });
            mediaPlayer = null;
        }
    }

	//在texture 建立时生成surfaceTexture后,与mediaplayer建立连接;
	@Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        if (SAVED_SURFACE == null) {
            SAVED_SURFACE = surface;
            prepare();
        } else {
            jzvd.textureView.setSurfaceTexture(SAVED_SURFACE);
        }
    }
	
	
```

> JZTextureView 自定义视频显示界面;

采用Android系统的VideoView的onMeasure方法;支持`VIDEO_IMAGE_DISPLAY_TYPE_ADAPTER (默认情况),
VIDEO_IMAGE_DISPLAY_TYPE_FILL_PARENT(拉伸全屏),
VIDEO_IMAGE_DISPLAY_TYPE_FILL_SCROP (crop 裁剪至全屏)
VIDEO_IMAGE_DISPLAY_TYPE_ORIGINAL (原图大小)` 四种模式;

```
	
	@Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.i(TAG, "onMeasure " + " [" + this.hashCode() + "] ");
        int viewRotation = (int) getRotation();
		//原图大小,onVideoSizeChanged 返回大小;
        int videoWidth = currentVideoWidth;
        int videoHeight = currentVideoHeight;

		//父view大小,jzvd的大小;
        int parentHeight = ((View) getParent()).getMeasuredHeight();
        int parentWidth = ((View) getParent()).getMeasuredWidth();
        if (parentWidth != 0 && parentHeight != 0 && videoWidth != 0 && videoHeight != 0) {
			//全屏设置模式: 
            if (Jzvd.VIDEO_IMAGE_DISPLAY_TYPE == Jzvd.VIDEO_IMAGE_DISPLAY_TYPE_FILL_PARENT) {
				//如果是90度或者270度时,交换父view宽高,设置预览页面大小;
                if (viewRotation == 90 || viewRotation == 270) {
                    int tempSize = parentWidth;
                    parentWidth = parentHeight;
                    parentHeight = tempSize;
                }
                /**强制充满**/
                videoHeight = videoWidth * parentHeight / parentWidth;
            }
        }

        // 如果判断成立，则说明显示的TextureView和本身的位置是有90度的旋转的，所以需要交换宽高参数。
        if (viewRotation == 90 || viewRotation == 270) {
            int tempMeasureSpec = widthMeasureSpec;
            widthMeasureSpec = heightMeasureSpec;
            heightMeasureSpec = tempMeasureSpec;
        }

		// 获取不同模式下的测量高度	
        int width = getDefaultSize(videoWidth, widthMeasureSpec);
        int height = getDefaultSize(videoHeight, heightMeasureSpec);
		//在传入原图大小后的调整;
        if (videoWidth > 0 && videoHeight > 0) {

            int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
            int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
            int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
            int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

            Log.i(TAG, "widthMeasureSpec  [" + MeasureSpec.toString(widthMeasureSpec) + "]");
            Log.i(TAG, "heightMeasureSpec [" + MeasureSpec.toString(heightMeasureSpec) + "]");

			//如果宽高参数都设置的是精准模式
            if (widthSpecMode == MeasureSpec.EXACTLY && heightSpecMode == MeasureSpec.EXACTLY) {
                // the size is fixed
                width = widthSpecSize;
                height = heightSpecSize;
                // for compatibility, we adjust size based on aspect ratio
                if (videoWidth * height < width * videoHeight) {
					//测量的宽高比 > 原图的宽高比,调整测量的宽度;
                    width = height * videoWidth / videoHeight;
                } else if (videoWidth * height > width * videoHeight) {
                    height = width * videoHeight / videoWidth;
                }
            } else if (widthSpecMode == MeasureSpec.EXACTLY) {
                // only the width is fixed, adjust the height to match aspect ratio if possible
                width = widthSpecSize;
                height = width * videoHeight / videoWidth;
				//在高度为自适应且调整后的高度大于测量的高度,则高度固定,调整宽度;
                if (heightSpecMode == MeasureSpec.AT_MOST && height > heightSpecSize) {
                    // couldn't match aspect ratio within the constraints
                    height = heightSpecSize;
                    width = height * videoWidth / videoHeight;
                }
            } else if (heightSpecMode == MeasureSpec.EXACTLY) {
                // only the height is fixed, adjust the width to match aspect ratio if possible
                height = heightSpecSize;
                width = height * videoWidth / videoHeight;
                if (widthSpecMode == MeasureSpec.AT_MOST && width > widthSpecSize) {
                    // couldn't match aspect ratio within the constraints
                    width = widthSpecSize;
                    height = width * videoHeight / videoWidth;
                }
            } else {
                // neither the width nor the height are fixed, try to use actual video size
                width = videoWidth;
                height = videoHeight;
                if (heightSpecMode == MeasureSpec.AT_MOST && height > heightSpecSize) {
                    // too tall, decrease both width and height
                    height = heightSpecSize;
                    width = height * videoWidth / videoHeight;
                }
                if (widthSpecMode == MeasureSpec.AT_MOST && width > widthSpecSize) {
                    // too wide, decrease both width and height
                    width = widthSpecSize;
                    height = width * videoHeight / videoWidth;
                }
            }
        } else {
            // no size yet, just adopt the given spec sizes
        }
        if (parentWidth != 0 && parentHeight != 0 && videoWidth != 0 && videoHeight != 0) {
			//原图设置模式
            if (Jzvd.VIDEO_IMAGE_DISPLAY_TYPE == Jzvd.VIDEO_IMAGE_DISPLAY_TYPE_ORIGINAL) {
                /**原图**/
                height = videoHeight;
                width = videoWidth;
            } else if (Jzvd.VIDEO_IMAGE_DISPLAY_TYPE == Jzvd.VIDEO_IMAGE_DISPLAY_TYPE_FILL_SCROP) {
				//裁切设置模式
                if (viewRotation == 90 || viewRotation == 270) {
                    int tempSize = parentWidth;
                    parentWidth = parentHeight;
                    parentHeight = tempSize;
                }
                /**充满剪切**/
                if (((double) videoHeight / videoWidth) > ((double) parentHeight / parentWidth)) {
                    height = (int) (((double) parentWidth / (double) width * (double) height));
                    width = parentWidth;
                } else if (((double) videoHeight / videoWidth) < ((double) parentHeight / parentWidth)) {
                    width = (int) (((double) parentHeight / (double) height * (double) width));
                    height = parentHeight;
                }
            }
        }
        setMeasuredDimension(width, height);
    }

```

> JZUtils 工具类

包括 时间格式化,wifi检测,设置横竖屏,本地数据保存,状态栏工具,宽高工具等;

> Jzvd 抽象播放界面

抽象类,主要功能实现类; 对视频功能的一系列封装,可自由继承此类,扩展视频功能和ui; 组合其他类的设计, 外观设计模式(或者中介者?);

- 状态常量

```

	屏幕状态
    public static final int SCREEN_NORMAL = 0; //普通播放状态
    public static final int SCREEN_FULLSCREEN = 1; //全屏播放状态;
    public static final int SCREEN_TINY = 2; //小屏播放状态;

	播放状态
	public static final int STATE_IDLE = -1; //空闲状态,构造布局init方法后
    public static final int STATE_NORMAL = 0; //设置参数后的状态;
    public static final int STATE_PREPARING = 1;//准备
    public static final int STATE_PREPARING_CHANGE_URL = 2;//改变资源,清晰度
    public static final int STATE_PREPARING_PLAYING = 3;//准备播放buffer
    public static final int STATE_PREPARED = 4;//准备完毕
    public static final int STATE_PLAYING = 5;//播放
    public static final int STATE_PAUSE = 6;//暂停
    public static final int STATE_AUTO_COMPLETE = 7;//完成
    public static final int STATE_ERROR = 8;//错误

	//视频显示类型
    public static final int VIDEO_IMAGE_DISPLAY_TYPE_ADAPTER = 0;//DEFAULT
    public static final int VIDEO_IMAGE_DISPLAY_TYPE_FILL_PARENT = 1;//拉伸
    public static final int VIDEO_IMAGE_DISPLAY_TYPE_FILL_SCROP = 2;//裁切
    public static final int VIDEO_IMAGE_DISPLAY_TYPE_ORIGINAL = 3;//原图

```

- 设置资源,等待播放;

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200509140154423.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L01ySmFydmlzRG9uZw==,size_16,color_FFFFFF,t_70)

主要设置参数: `screen屏幕状态`,`JZDataSource封装数据`,`mediaInterfaceClass播放引擎`;

> JzvdStd 具体播放界面

具体的播放界面,ui界面扩展功能; Jzvd的控制音量,亮度,快进,快退的dialog也于此处实现;


> AGVideo 新样式播放界面 

仿爱奇艺等播放样式,选集,倍速播放;

----
### 播放引擎实现


### 横竖屏切换及小窗实现

> 横竖屏

>小窗

> 重新clone Jzvd对象

### 传感器监听

ScreenRotateUtils

### 视频控制条触摸实现


### Gif的生成

GifCreateHelper

### 其他功能亮点

> AudioManager 监听焦点

> wifi 网络状态监听 

> 电池电量监听

> 视频清晰度切换

> 本地记录进度seek

> 双击播放与暂停

> 视频点击平滑到另一个页面播放

JzvdStdRv

>列表中自动播放

AutoPlayUtils 判断列表中完全可见;

>当前进入全屏播放,添加到Decorview中


----
### 评估

首先这个三方android视频播放库的完成度已经非常高了,同时支持切换播放引擎,横竖屏切换,支持recyclerview,webview,传感器横屏,电量监听,保存gif等等功能;还有仿抖音和爱奇艺样式的demo, 非常值得学习;

说下改进点,其实是完善点吧[逃],个人觉得`Jzvd`和`JzvdStd`部分逻辑视频播放抽象部分和实现部分的抽取更加清晰就更完美了;