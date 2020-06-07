## android 视频播放器框架 [饺子播放器](https://github.com/Jzvd/JiaoZiVideoPlayer) 源码解析与评估 

### 前言

使用的源码版本为`7.3.0`,饺子播放器源码设计精良,是个狠人,非常值得学习,在看源码的过程中觉得有些地方非常值得记录一下; 

代码挺多,本着不求甚解的解读原则,把代码中的亮点或者常用功能点的代码详细摘录下来;

---
### Library 主要类

> JZDataSource 配置类;

主要是包括 视频播放配置,播放列表是否续播等数据; 

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

	...
	
```

- 设置资源,等待播放;

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200509140154423.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L01ySmFydmlzRG9uZw==,size_16,color_FFFFFF,t_70)

主要设置参数: `screen屏幕状态`,`JZDataSource封装数据`,`mediaInterfaceClass播放引擎策略`;

**主要关键点:**

1.static 容器列表

`public static LinkedList<ViewGroup> CONTAINER_LIST = new LinkedList<>();`
主要用于判断横竖屏和屏幕转化的判断;

2.static 静态强引用的播放主类

`public static Jzvd CURRENT_JZVD;` 可自由控制播放实现类;

3.播放参数对象

`public JZDataSource jzDataSource;` 

4.播放引擎使用类名作为策略

```

	//使用反射生成播放引擎 策略类;
	public Class mediaInterfaceClass;
    public JZMediaInterface mediaInterface;

```

5.播放视图view

`public JZTextureView textureView;` 使用textureView承载播放视频, 后期可使用`surfaceView`代替;

6.使用TimeTask获取progress

`protected ProgressTimerTask mProgressTimerTask;` 实现进度播放;

7.统一销毁jzvd播放类

```

	public static void releaseAllVideos() {
        Log.d(TAG, "releaseAllVideos");
        if (CURRENT_JZVD != null) {
            CURRENT_JZVD.reset();
            CURRENT_JZVD = null;
        }
    }

	public void reset() {
        Log.i(TAG, "reset " + " [" + this.hashCode() + "] ");
		//销毁前保存当前播放进度;
        if (state == STATE_PLAYING || state == STATE_PAUSE) {
            long position = getCurrentPositionWhenPlaying();
            JZUtils.saveProgress(getContext(), jzDataSource.getCurrentUrl(), position);
        }
        cancelProgressTimer();
        dismissBrightnessDialog();
        dismissProgressDialog();
        dismissVolumeDialog();
        onStateNormal();
		//播放视图view的容器,TextureView or SurfaceView;
        textureViewContainer.removeAllViews();
		
		//audio解绑;
        AudioManager mAudioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        mAudioManager.abandonAudioFocus(onAudioFocusChangeListener);
        JZUtils.scanForActivity(getContext()).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		//播放引擎的释放;
        if (mediaInterface != null) mediaInterface.release();
    }

```

8.返回操作

```

	public static boolean backPress() {
        Log.i(TAG, "backPress");
        if (CONTAINER_LIST.size() != 0 && CURRENT_JZVD != null) {//判断条件，因为当前所有goBack都是回到普通窗口
            CURRENT_JZVD.gotoScreenNormal();
            return true;
        } else if (CONTAINER_LIST.size() == 0 && CURRENT_JZVD != null && CURRENT_JZVD.screen != SCREEN_NORMAL) {//退出直接进入的全屏
            CURRENT_JZVD.clearFloatScreen();
            return true;
        }
        return false;
    }

```

9.播放操作判断;

```

	//参数合法性判断;normal(setup)下,判断是否需要弹wifi提示;
	private void clickStart() {
        Log.i(TAG, "onClick start [" + this.hashCode() + "] ");
        if (jzDataSource == null || jzDataSource.urlsMap.isEmpty() || jzDataSource.getCurrentUrl() == null) {
            Toast.makeText(getContext(), getResources().getString(R.string.no_url), Toast.LENGTH_SHORT).show();
            return;
        }
        if (state == STATE_NORMAL) {
            if (!jzDataSource.getCurrentUrl().toString().startsWith("file") && !
                    jzDataSource.getCurrentUrl().toString().startsWith("/") &&
                    !JZUtils.isWifiConnected(getContext()) && !WIFI_TIP_DIALOG_SHOWED) {//这个可以放到std中
                showWifiDialog();
                return;
            }
            startVideo();
        } else if (state == STATE_PLAYING) {
            Log.d(TAG, "pauseVideo [" + this.hashCode() + "] ");
            mediaInterface.pause();
            onStatePause();
        } else if (state == STATE_PAUSE) {
            mediaInterface.start();
            onStatePlaying();
        } else if (state == STATE_AUTO_COMPLETE) {
            startVideo();
        }
    }

	//开始播放视频;
	public void startVideo() {
        Log.d(TAG, "startVideo [" + this.hashCode() + "] ");
		//static变量引用当前jzvd;
        setCurrentJzvd(this);
        try {
			//播放引擎构建实现类对象;
            Constructor<JZMediaInterface> constructor = mediaInterfaceClass.getConstructor(Jzvd.class);
            this.mediaInterface = constructor.newInstance(this);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
		//添加textureView后,进行prepare操作;
        addTextureView();
        mAudioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        mAudioManager.requestAudioFocus(onAudioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
        JZUtils.scanForActivity(getContext()).getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		//改变状态;
        onStatePreparing();
    }

	//jzvd(FrameLayout)中提供的视图View的容器;
	public void addTextureView() {
        Log.d(TAG, "addTextureView [" + this.hashCode() + "] ");
        if (textureView != null) textureViewContainer.removeView(textureView);
        textureView = new JZTextureView(getContext().getApplicationContext());
		//JZMediaInterface 实现 TextureView.SurfaceTextureListener ,当textureView添加到容器中,会调用available方法,初始化成功可进行prepare操作;
        textureView.setSurfaceTextureListener(mediaInterface);

        FrameLayout.LayoutParams layoutParams =
                new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        Gravity.CENTER);
        textureViewContainer.addView(textureView, layoutParams);
    }
	
	public interface SurfaceTextureListener {
        void onSurfaceTextureAvailable(SurfaceTexture var1, int var2, int var3);

        void onSurfaceTextureSizeChanged(SurfaceTexture var1, int var2, int var3);

        boolean onSurfaceTextureDestroyed(SurfaceTexture var1);

        void onSurfaceTextureUpdated(SurfaceTexture var1);
    }	

```

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200531173454286.png)

当textureView 添加到view中,attached window后,会调用 `onSurfaceTextureAvailable`方法; 


### 横竖屏切换

>点击直接全屏播放功能;

```
	//添加到当前activity 的 decorview中;
	public static void startFullscreenDirectly(Context context, Class _class, JZDataSource jzDataSource) {
		//设置全屏flag;
        JZUtils.hideStatusBar(context);
		//设置屏幕方向;
        JZUtils.setRequestedOrientation(context, FULLSCREEN_ORIENTATION);
		//设置沉浸式状态栏;
        JZUtils.hideSystemUI(context);
		
		//获取当前activity的Decorview;
        ViewGroup vp = (ViewGroup) JZUtils.scanForActivity(context).getWindow().getDecorView();
        try {
			//获取播放Jzvd实现类的构造器;
            Constructor<Jzvd> constructor = _class.getConstructor(Context.class);
			//构建Jzvd实现类对象;
            final Jzvd jzvd = constructor.newInstance(context);
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
			//提交到decorView中,覆盖于原页面上;
            vp.addView(jzvd, lp);
			//设置jzvd主要参数并播放;
            jzvd.setUp(jzDataSource, JzvdStd.SCREEN_FULLSCREEN);
            jzvd.startVideo();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

```

> 横竖屏

`CONTAINER_LIST`:  手动全屏时 Jzvd parentView 会remove当前jzvd, 并添加parentView到此容器. 
在恢复竖屏时会使用保存的parentView将remove的jzvd重新添加回来,达到横竖屏切换的目的;

```

	//全屏,添加到jzvd自定义view的父View中;
	public void gotoScreenFullscreen() {
        gotoFullscreenTime = System.currentTimeMillis();
        jzvdContext = ((ViewGroup) getParent()).getContext();
		//自定义播放View 的直接父类,一般会套一个LinearLayout;
        ViewGroup vg = (ViewGroup) getParent();
        vg.removeView(this);
        cloneAJzvd(vg);
        CONTAINER_LIST.add(vg);
        vg = (ViewGroup) (JZUtils.scanForActivity(jzvdContext)).getWindow().getDecorView();

        vg.addView(this, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

		//全屏flag,屏幕方向,沉浸式状态栏;
        setScreenFullscreen();
        JZUtils.hideStatusBar(jzvdContext);
        JZUtils.setRequestedOrientation(jzvdContext, FULLSCREEN_ORIENTATION);
        JZUtils.hideSystemUI(jzvdContext);//华为手机和有虚拟键的手机全屏时可隐藏虚拟键 issue:1326

    }

	// 重新clone Jzvd对象,用于全屏jzvd在偶然错误情况下返回没有播放视频的容器了;
	public void cloneAJzvd(ViewGroup vg) {
        try {
			//获取当前实现类构造器,new对象,设置原参数;
            Constructor<Jzvd> constructor = (Constructor<Jzvd>) Jzvd.this.getClass().getConstructor(Context.class);
            Jzvd jzvd = constructor.newInstance(getContext());
            jzvd.setId(getId());
            vg.addView(jzvd);
            jzvd.setUp(jzDataSource.cloneMe(), SCREEN_NORMAL, mediaInterfaceClass);
        } catch ...
    }

	//全屏恢复,添加原先remove的jzvd;
	public void gotoScreenNormal() {//goback本质上是goto
        gobakFullscreenTime = System.currentTimeMillis();//退出全屏
		//先尝试去除添加在decorview中的jzvd;
        ViewGroup vg = (ViewGroup) (JZUtils.scanForActivity(jzvdContext)).getWindow().getDecorView();
        vg.removeView(this);
		//取上次全屏时添加的vg引用,就是jzvd的父View,然后去除clone的jzvd,并添加当前的jzvd;
        CONTAINER_LIST.getLast().removeAllViews();
        CONTAINER_LIST.getLast().addView(this, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		//弹出上次添加的jzvd的parentView;
        CONTAINER_LIST.pop();
		
        setScreenNormal();//这块可以放到jzvd中
        JZUtils.showStatusBar(jzvdContext);
        JZUtils.setRequestedOrientation(jzvdContext, NORMAL_ORIENTATION);
        JZUtils.showSystemUI(jzvdContext);
    }

```


### 传感器监听

`ScreenRotateUtils`  加速度传感器,通过手机的转动控制播放器的横竖屏;

```

	//传感器的监听;
	class OrientationSensorListener implements SensorEventListener {

        @Override
        public void onSensorChanged(SensorEvent event) {
            float[] values = event.values;
            int orientation = ORIENTATION_UNKNOWN;
            float x = -values[DATA_X];
            orientationDirection = -x;
            float y = -values[DATA_Y];
            float z = -values[DATA_Z];
            float magnitude = x * x + y * y;
            if (magnitude * 4 >= z * z) {
                float oneEightyOverPi = 57.29577957855f;
                float angle = (float) (Math.atan2(-y, x) * oneEightyOverPi);

                orientation = 90 - Math.round(angle);
                // normalize to 0 - 359 range
                while (orientation >= 360) {
                    orientation -= 360;
                }
                while (orientation < 0) {
                    orientation += 360;
                }
            }

            /**
             * 获取手机系统的重力感应开关设置，这段代码看需求，不要就删除
             * screenchange = 1 表示开启，screenchange = 0 表示禁用
             * 要是禁用了就直接返回
             */
            if (isEffectSysSetting) {
                try {
                    int isRotate = Settings.System.getInt(mActivity.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION);
                    // 如果用户禁用掉了重力感应就直接return
                    if (isRotate == 0) {
                        return;
                    }
                } catch (Settings.SettingNotFoundException e) {
                    e.printStackTrace();
                }
                // 判断是否要进行中断信息传递
                if (!isOpenSensor) {
                    return;
                }
                changeListener.orientationChange(orientation);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    }

```

### 视频控制条触摸实现

jzvd 横屏下实现了3种dialog弹框控制视频;

- 快进/快退左右滑动 dialog;
- 左半部分上下滑动 dialog;
- 右半不不封上下滑动 dialog;

```

	//touch move 事件;
	private void touchActionMove(float x, float y) {
        Log.i(TAG, "onTouch surfaceContainer actionMove [" + this.hashCode() + "] ");
        float deltaX = x - mDownX;
        float deltaY = y - mDownY;
        float absDeltaX = Math.abs(deltaX);
        float absDeltaY = Math.abs(deltaY);
        if (screen == SCREEN_FULLSCREEN) {
            //拖动的是NavigationBar和状态栏
            if (mDownX > JZUtils.getScreenWidth(getContext()) || mDownY < JZUtils.getStatusBarHeight(getContext())) {
                return;
            }
            if (!mChangePosition && !mChangeVolume && !mChangeBrightness) {
                if (absDeltaX > THRESHOLD || absDeltaY > THRESHOLD) {
                    cancelProgressTimer();
					//x方向移动距离触发mChangePosition flag, 显示快进/快退dialog;
                    if (absDeltaX >= THRESHOLD) {
                        // 全屏模式下的CURRENT_STATE_ERROR状态下,不响应进度拖动事件.
                        // 否则会因为mediaplayer的状态非法导致App Crash
                        if (state != STATE_ERROR) {
                            mChangePosition = true;
                            mGestureDownPosition = getCurrentPositionWhenPlaying();
                        }
                    } else {
                        //如果y轴滑动距离超过设置的处理范围，那么进行滑动事件处理
                        if (mDownX < mScreenWidth * 0.5f) {//左侧改变亮度
                            mChangeBrightness = true;
                            WindowManager.LayoutParams lp = JZUtils.getWindow(getContext()).getAttributes();
                            if (lp.screenBrightness < 0) {
                                try {
                                    mGestureDownBrightness = Settings.System.getInt(getContext().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
                                    Log.i(TAG, "current system brightness: " + mGestureDownBrightness);
                                } catch (Settings.SettingNotFoundException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                mGestureDownBrightness = lp.screenBrightness * 255;
                                Log.i(TAG, "current activity brightness: " + mGestureDownBrightness);
                            }
                        } else {//右侧改变声音
                            mChangeVolume = true;
                            mGestureDownVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                        }
                    }
                }
            }
        }
		//一旦进入一个dialog 模式,就一直改变这个dialog;
        if (mChangePosition) {
            long totalTimeDuration = getDuration();
            mSeekTimePosition = (int) (mGestureDownPosition + deltaX * totalTimeDuration / mScreenWidth);
            if (mSeekTimePosition > totalTimeDuration)
                mSeekTimePosition = totalTimeDuration;
            String seekTime = JZUtils.stringForTime(mSeekTimePosition);
            String totalTime = JZUtils.stringForTime(totalTimeDuration);

            showProgressDialog(deltaX, seekTime, mSeekTimePosition, totalTime, totalTimeDuration);
        }
        if (mChangeVolume) {
            deltaY = -deltaY;
            int max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            int deltaV = (int) (max * deltaY * 3 / mScreenHeight);
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mGestureDownVolume + deltaV, 0);
            //dialog中显示百分比
            int volumePercent = (int) (mGestureDownVolume * 100 / max + deltaY * 3 * 100 / mScreenHeight);
            showVolumeDialog(-deltaY, volumePercent);
        }

        if (mChangeBrightness) {
            deltaY = -deltaY;
            int deltaV = (int) (255 * deltaY * 3 / mScreenHeight);
            WindowManager.LayoutParams params = JZUtils.getWindow(getContext()).getAttributes();
            if (((mGestureDownBrightness + deltaV) / 255) >= 1) {//这和声音有区别，必须自己过滤一下负值
                params.screenBrightness = 1;
            } else if (((mGestureDownBrightness + deltaV) / 255) <= 0) {
                params.screenBrightness = 0.01f;
            } else {
                params.screenBrightness = (mGestureDownBrightness + deltaV) / 255;
            }
            JZUtils.getWindow(getContext()).setAttributes(params);
            //dialog中显示百分比
            int brightnessPercent = (int) (mGestureDownBrightness * 100 / 255 + deltaY * 3 * 100 / mScreenHeight);
            showBrightnessDialog(brightnessPercent);
//                        mDownY = y;
        }
    }

```


### Gif的生成

`GifCreateHelper`: 创建Gif,使用FFmpeg 截取视频中的图片,然后拼接成gif;

`AnimatedGifEncoder` : 使用bitmaps生成一张gif; (这个类太长了,就不放了,有兴趣的可以在github中查看)

```
	
	//需要引入ffmpeg的包;
	implementation 'com.github.wseemann:FFmpegMediaMetadataRetriever-core:1.0.15'
    implementation 'com.github.wseemann:FFmpegMediaMetadataRetriever-native:1.0.15'
	
	/**
     * @param bitmapFromTime gif图在视频中的开始时间
     * @param vedioUrl       视频链接
     */
    public void startGif(long bitmapFromTime, String vedioUrl) {
		//设置gif的时长和帧间隔时长,计算总共需要截取的张数;
        int bitmapCount = mGifPeriod / mDelay;
        String[] picList = new String[bitmapCount];
        isDownloadComplete = false;
        FFmpegMediaMetadataRetriever mmr = prepareFFmpegMediaMetadataRetriever(vedioUrl);
        for (int i = 0; i < bitmapCount; i++) {
            final int index = i;
			//`ExecutorService executorService = Executors.newCachedThreadPool();` 使用多个非核心线程池加载;
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    //先缓存到本地，全放入内存占用空间太大
                    String path = saveBitmap(mmr.getScaledFrameAtTime((bitmapFromTime + index * mDelay)*1000,FFmpegMediaMetadataRetriever.OPTION_CLOSEST,gifWidth,gifHeight),
                            cacheImageDir + "/" + System.currentTimeMillis() + "index-" + index + ".png");
                    boolean isCurrentSuccess = true;
                    if (!TextUtils.isEmpty(path)) {
                        picList[index] = path;
                    } else {
						//标志错误的string;
                        picList[index] = completeButNoImageTag;
                        isCurrentSuccess = false;
                    }

                    checkCompleteAndDoNext(picList, isCurrentSuccess);
                    if(isDownloadComplete){
                        mmr.release();
                    }
                }
            });
        }
    }

	private FFmpegMediaMetadataRetriever prepareFFmpegMediaMetadataRetriever(String vedioUrl){
        FFmpegMediaMetadataRetriever mmr = new FFmpegMediaMetadataRetriever();
        mmr.setDataSource(vedioUrl);
        mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_ALBUM);
        mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_ARTIST);

        return mmr;
    }

	private void checkCompleteAndDoNext(String[] picList, boolean isCurrentSuccess) {
		//线程安全;
        synchronized (GifCreateHelper.class) {
            if (isDownloadComplete) {
                return;
            }

            if (picList == null || picList.length == 0) {
                combinePicToGif(picList);
            }

            int emptyCount = 0;
            for (String path : picList) {
                if (TextUtils.isEmpty(path)) {
                    emptyCount++;
                }
            }
			
			//listener 显示进度;
            mJzGifListener.process(picList.length - emptyCount, picList.length, isCurrentSuccess ? "下载成功" : "下载失败");

            if (emptyCount == 0) {
                isDownloadComplete = true;
                mPlayer.post(new Runnable() {
                    @Override
                    public void run() {
                        combinePicToGif(picList);
                    }
                });
            }
        }
    }

	private void combinePicToGif(String[] picList) {
		//设置输出流;
        File gifFile = ensureFile(new File(mGifPath));
        ArrayList<String> rightPic = new ArrayList<>();
        for (String picItem : picList) {
            if (!TextUtils.isEmpty(picItem) && !completeButNoImageTag.equals(picItem)) {
                rightPic.add(picItem);
            }
        }

        if (rightPic.size() > 2) {
            if (createGif(gifFile, rightPic, mDelay, mSampleSize, mSmallScale)) {
                mJzGifListener.result(true, gifFile);
            } else {
                mJzGifListener.result(false, null);
            }
        } else {
            mJzGifListener.result(false, null);
        }
        deleteDirWihtFile(new File(cacheImageDir));//清除缓存的图片
    }

	//create gif (AnimatedGifEncoder)核心方法
	/**
     * 生成gif图
     *
     * @param file         保存的文件路径，请确保文件夹目录已经创建
     * @param pics         需要转化的bitmap本地路径集合
     * @param delay        每一帧之间的延时
     * @param inSampleSize 采样率，最小值1 即：每隔inSampleSize个像素点，取一个读入到内存。越大处理越快
     * @param smallScale   缩小倍数，越大处理越快
     */
    public boolean createGif(File file, List<String> pics, int delay, int inSampleSize, int smallScale) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        AnimatedGifEncoder localAnimatedGifEncoder = new AnimatedGifEncoder();
        localAnimatedGifEncoder.start(baos);
        localAnimatedGifEncoder.setRepeat(0);//设置生成gif的开始播放时间。0为立即开始播放
        localAnimatedGifEncoder.setDelay(delay);
        for (int i = 0; i < pics.size(); i++) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = inSampleSize;
            options.inJustDecodeBounds = true; // 先获取原大小
            BitmapFactory.decodeFile(pics.get(i), options);
            double w = (double) options.outWidth / smallScale;
            double h = (double) options.outHeight / smallScale;
            options.inJustDecodeBounds = false; // 获取新的大小
            Bitmap bitmap = BitmapFactory.decodeFile(pics.get(i), options);
            Bitmap pic = ThumbnailUtils.extractThumbnail(bitmap, (int) w, (int) h);
            localAnimatedGifEncoder.addFrame(pic);
            bitmap.recycle();
            pic.recycle();
            mJzGifListener.process(i, pics.size(), "组合中");
        }
        localAnimatedGifEncoder.finish();//finish
        try {
            FileOutputStream fos = new FileOutputStream(file.getPath());
            baos.writeTo(fos);
            baos.flush();
            fos.flush();
            baos.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

```

### 播放引擎策略实现

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200602094802201.png) 

暂时有 `JZMediaSystem`,`JZMediaExo`,`JZMediaIjk`,`JZMediaSystemAssertFolder` 四种播放引擎,其实是3种,`JZMediaSystemAssertFolder`只是数据源不同;

 抽取播放常用接口,通过textureView attach 到window后 调用 `onSurfaceTextureAvailable`进行音视频的`prepare`过程,其中使用四种具体策略抽象出播放引擎接口;

- JZMediaSystem 

主要使用 `public MediaPlayer mediaPlayer;` 作为音视频播放器; 核心是`prepare`过程;

```

	//使用 HandlerThread 搭配 Handler 进行子线程下的mediaplayer的初始化过程;
	public void prepare() {
        release();
        mMediaHandlerThread = new HandlerThread("JZVD");
        mMediaHandlerThread.start();
        mMediaHandler = new Handler(mMediaHandlerThread.getLooper());//主线程还是非主线程，就在这里
        handler = new Handler();

        mMediaHandler.post(() -> {
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
                Class<MediaPlayer> clazz = MediaPlayer.class;
				//通过反射方式调用setDataSource设置数据源;
                Method method = clazz.getDeclaredMethod("setDataSource", String.class, Map.class);
                method.invoke(mediaPlayer, jzvd.jzDataSource.getCurrentUrl().toString(), jzvd.jzDataSource.headerMap);
                mediaPlayer.prepareAsync();
                mediaPlayer.setSurface(new Surface(SAVED_SURFACE));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

	JZMediaSystemAssertFolder 不同于数据源设置不同;
	//two lines are different
    AssetFileDescriptor assetFileDescriptor = jzvd.getContext().getAssets().openFd(jzvd.jzDataSource.getCurrentUrl().toString());
    mediaPlayer.setDataSource(assetFileDescriptor.getFileDescriptor(), assetFileDescriptor.getStartOffset(), assetFileDescriptor.getLength());

```

- JZMediaExo

```

	public void prepare() {
        Log.e(TAG, "prepare");
        Context context = jzvd.getContext();

        release();
        mMediaHandlerThread = new HandlerThread("JZVD");
        mMediaHandlerThread.start();
        mMediaHandler = new Handler(mMediaHandlerThread.getLooper());//主线程还是非主线程，就在这里
        handler = new Handler();
        mMediaHandler.post(() -> {
            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
            TrackSelection.Factory videoTrackSelectionFactory =
                    new AdaptiveTrackSelection.Factory(bandwidthMeter);
            TrackSelector trackSelector =
                    new DefaultTrackSelector(videoTrackSelectionFactory);

            LoadControl loadControl = new DefaultLoadControl(new DefaultAllocator(true, C.DEFAULT_BUFFER_SEGMENT_SIZE),
                    360000, 600000, 1000, 5000,
                    C.LENGTH_UNSET,
                    false);

            // 2. Create the player

            RenderersFactory renderersFactory = new DefaultRenderersFactory(context);
            simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(context, renderersFactory, trackSelector, loadControl);
            // Produces DataSource instances through which media data is loaded.
            DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context,
                    Util.getUserAgent(context, context.getResources().getString(R.string.app_name)));

            String currUrl = jzvd.jzDataSource.getCurrentUrl().toString();
            MediaSource videoSource;
            if (currUrl.contains(".m3u8")) {
                videoSource = new HlsMediaSource.Factory(dataSourceFactory)
                        .createMediaSource(Uri.parse(currUrl), handler, null);
            } else {
                videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                        .createMediaSource(Uri.parse(currUrl));
            }
            simpleExoPlayer.addVideoListener(this);

            Log.e(TAG, "URL Link = " + currUrl);

            simpleExoPlayer.addListener(this);
            Boolean isLoop = jzvd.jzDataSource.looping;
            if (isLoop) {
                simpleExoPlayer.setRepeatMode(Player.REPEAT_MODE_ONE);
            } else {
                simpleExoPlayer.setRepeatMode(Player.REPEAT_MODE_OFF);
            }
            simpleExoPlayer.prepare(videoSource);
            simpleExoPlayer.setPlayWhenReady(true);
            callback = new onBufferingUpdate();

            if (jzvd.textureView != null) {
                SurfaceTexture surfaceTexture = jzvd.textureView.getSurfaceTexture();
                if (surfaceTexture != null) {
                    simpleExoPlayer.setVideoSurface(new Surface(surfaceTexture));
                }
            }
        });

    }

```

- JZMediaIjk


```

	public void prepare() {

        release();
        mMediaHandlerThread = new HandlerThread("JZVD");
        mMediaHandlerThread.start();
        mMediaHandler = new Handler(mMediaHandlerThread.getLooper());//主线程还是非主线程，就在这里
        handler = new Handler();

        mMediaHandler.post(() -> {

            ijkMediaPlayer = new IjkMediaPlayer();

            ijkMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            ////1为硬解 0为软解
            ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 0);
            ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-auto-rotate", 1);
            ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-handle-resolution-change", 1);
            //使用opensles把文件从java层拷贝到native层
            ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "opensles", 0);
            //视频格式
            ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "overlay-format", IjkMediaPlayer.SDL_FCC_RV32);
            //跳帧处理（-1~120）。CPU处理慢时，进行跳帧处理，保证音视频同步
            ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "framedrop", 1);
            //0为一进入就播放,1为进入时不播放
            ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "start-on-prepared", 0);
            ////域名检测
            ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "http-detect-range-support", 0);
            //设置是否开启环路过滤: 0开启，画面质量高，解码开销大，48关闭，画面质量差点，解码开销小
            ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip_loop_filter", 48);
            //最大缓冲大小,单位kb
            ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "max-buffer-size", 1024 * 1024);
            //某些视频在SeekTo的时候，会跳回到拖动前的位置，这是因为视频的关键帧的问题，通俗一点就是FFMPEG不兼容，视频压缩过于厉害，seek只支持关键帧，出现这个情况就是原始的视频文件中i 帧比较少
            ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "enable-accurate-seek", 1);
            //是否重连
            ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "reconnect", 1);
            //http重定向https
            ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "dns_cache_clear", 1);
            //设置seekTo能够快速seek到指定位置并播放
            ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "fflags", "fastseek");
            //播放前的探测Size，默认是1M, 改小一点会出画面更快
            ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "probesize", 1024 * 10);


            ijkMediaPlayer.setOnPreparedListener(JZMediaIjk.this);
            ijkMediaPlayer.setOnVideoSizeChangedListener(JZMediaIjk.this);
            ijkMediaPlayer.setOnCompletionListener(JZMediaIjk.this);
            ijkMediaPlayer.setOnErrorListener(JZMediaIjk.this);
            ijkMediaPlayer.setOnInfoListener(JZMediaIjk.this);
            ijkMediaPlayer.setOnBufferingUpdateListener(JZMediaIjk.this);
            ijkMediaPlayer.setOnSeekCompleteListener(JZMediaIjk.this);
            ijkMediaPlayer.setOnTimedTextListener(JZMediaIjk.this);

            try {
                ijkMediaPlayer.setDataSource(jzvd.jzDataSource.getCurrentUrl().toString());
                ijkMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                ijkMediaPlayer.setScreenOnWhilePlaying(true);
                ijkMediaPlayer.prepareAsync();

                ijkMediaPlayer.setSurface(new Surface(jzvd.textureView.getSurfaceTexture()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }

```

### 其他功能亮点

> AudioManager 播放焦点监听事件

```
	
	public static AudioManager.OnAudioFocusChangeListener onAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {//是否新建个class，代码更规矩，并且变量的位置也很尴尬
        @Override
        public void onAudioFocusChange(int focusChange) {
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_GAIN:
                    break;
                case AudioManager.AUDIOFOCUS_LOSS:
					//长时间失去了Audio Focus,直接销毁;
                    releaseAllVideos();
                    Log.d(TAG, "AUDIOFOCUS_LOSS [" + this.hashCode() + "]");
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
					//暂时失去了Audio Focus,是播放状态继续播放;
                    try {
                        Jzvd player = CURRENT_JZVD;
                        if (player != null && player.state == Jzvd.STATE_PLAYING) {
                            player.startButton.performClick();
                        }
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    }
                    Log.d(TAG, "AUDIOFOCUS_LOSS_TRANSIENT [" + this.hashCode() + "]");
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    break;
            }
        }
    };

```

> wifi 网络状态监听 

start 按钮点击会判断当前网络的状态,也是经常有的功能;

```

	//手动注册wifi监听;
	public BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
                boolean isWifi = JZUtils.isWifiConnected(context);
                if (mIsWifi == isWifi) return;
                mIsWifi = isWifi;
                if (!mIsWifi && !WIFI_TIP_DIALOG_SHOWED && state == STATE_PLAYING) {
                    startButton.performClick(); //pause
                    showWifiDialog();
                }
            }
        }
    };


```

> 电池电量监听

```

	//广播接受者
	public BroadcastReceiver battertReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Intent.ACTION_BATTERY_CHANGED.equals(action)) {
                int level = intent.getIntExtra("level", 0);
                int scale = intent.getIntExtra("scale", 100);
                int percent = level * 100 / scale;
                LAST_GET_BATTERYLEVEL_PERCENT = percent;
                setBatteryLevel();
                try {
                    getContext().unregisterReceiver(battertReceiver);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };

```

> 视频清晰度切换

记录当前播放位置,更换JZDataSource播放数据,release 当前jzvd, 重新startVideo;

这里可以搭配另一个视频缓存库使用: [android videocache](https://github.com/danikula/AndroidVideoCache)

这个库也是非常值得学习的一个库,晚点再介绍这个库;

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200601222114351.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L01ySmFydmlzRG9uZw==,size_16,color_FFFFFF,t_70)

```

	protected void initData() {
        super.initData();
        LinkedHashMap map = new LinkedHashMap();
		//在本地缓存当前视频,高清的意思就是本地视频;
        String proxyUrl = ApplicationDemo.getProxy(mContext).getProxyUrl(Urls.videoUrls[0][9]);
        map.put("高清", proxyUrl);
        map.put("标清", Urls.videoUrls[0][6]);
        map.put("普清", Urls.videoUrlList[0]);
        JZDataSource jzDataSource = new JZDataSource(map, "饺子不信");
        jzDataSource.looping = true;
        jzDataSource.currentUrlIndex = 2;
        jzDataSource.headerMap.put("key", "value");//header
        mJzvdStd.setUp(jzDataSource
                , JzvdStd.SCREEN_NORMAL);
        Glide.with(this).load(Urls.videoPosterList[0]).into(mJzvdStd.posterImageView);
    }	

```

> 本地记录进度seek

```
	
	public void onStatePlaying() {
        Log.i(TAG, "onStatePlaying " + " [" + this.hashCode() + "] ");
        if (state == STATE_PREPARED) {//如果是准备完成视频后第一次播放，先判断是否需要跳转进度。
			//更换url时,记录上次播放位置,prepare后seek到此播放位置;
            if (seekToInAdvance != 0) {
                mediaInterface.seekTo(seekToInAdvance);
                seekToInAdvance = 0;
            } else {
				//reset时存储在本地的位置;
                long position = JZUtils.getSavedProgress(getContext(), jzDataSource.getCurrentUrl());
                if (position != 0) {
                    mediaInterface.seekTo(position);//这里为什么区分开呢，第一次的播放和resume播放是不一样的。 这里怎么区分是一个问题。然后
                }
            }
        }
        state = STATE_PLAYING;
        startProgressTimer();
    }

```

> 双击播放与暂停

```

	//doublClick 这两个全局变量只在ontouch中使用，就近放置便于阅读
    private long lastClickTime = 0;
    private long doubleTime = 200;
    private ArrayDeque<Runnable> delayTask = new ArrayDeque<>();

	//在jzvd 的 实现类 JzvdStd 中实现;
	public boolean onTouch(View v, MotionEvent event) {
        int id = v.getId();
        if (id == R.id.surface_container) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_MOVE:
                    break;
                case MotionEvent.ACTION_UP:
                    startDismissControlViewTimer();
                    if (mChangePosition) {
                        long duration = getDuration();
                        int progress = (int) (mSeekTimePosition * 100 / (duration == 0 ? 1 : duration));
                        bottomProgressBar.setProgress(progress);
                    }

                    //加上延时是为了判断点击是否是双击之一，双击不执行这个逻辑
                    Runnable task = () -> {
                        if (!mChangePosition && !mChangeVolume) {
                            onClickUiToggle();
                        }
                    };
                    v.postDelayed(task, doubleTime + 20);
                    delayTask.add(task);
					//只要连续的两个up事件
                    while (delayTask.size() > 2) {
                        delayTask.pollFirst();
                    }

                    long currentTimeMillis = System.currentTimeMillis();
                    if (currentTimeMillis - lastClickTime < doubleTime) {
						//若是连续的双击,则去除单击事件;
                        for (Runnable taskItem : delayTask) {
                            v.removeCallbacks(taskItem);
                        }
                        if (state == STATE_PLAYING || state == STATE_PAUSE) {
                            Log.d(TAG, "doublClick [" + this.hashCode() + "] ");
							//实现连续双击控制视频的暂停和播放;
                            startButton.performClick();
                        }
                    }
                    lastClickTime = currentTimeMillis;
                    break;
            }
        } else if (id == R.id.bottom_seek_progress) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    cancelDismissControlViewTimer();
                    break;
                case MotionEvent.ACTION_UP:
                    startDismissControlViewTimer();
                    break;
            }
        }
        return super.onTouch(v, event);
    }

```

> 视频点击平滑到另一个页面播放 `ListViewToDetailActivity`

列表平滑进入详情页

1.获取当前播放的JZVD添加到详情页中;

2.获取列表中JZVD的坐标，宽高，获取详情页JZVD坐标，宽高，借助ViewMoveHelper实现平移;

**现象**是:

 点击位于列表中的jzvd,平滑到另一个act的页面,jzvd继续播放,很多直播平台有这种效果;

**重点原理**是: 

使用一个static的变量强引用了Jzvd 对象(此种思想也可以做全局播放,不过要注意内存泄露问题,需要控制释放), 上一个页面remove调当前jzvd,防止下一个页面的容器添加jzvd时出现`父类已经有孩子 parent has a child`之类的异常; 然后 下一个页面的容器(FrameLayout)添加此static的jzvd,其中在添加属性动画,播放不中断,没有违和感; 退出时同理;

```

	//首先 Intent 跳转页面时 去除默认过渡效果;
	Intent intent = new Intent(ListViewToDetailActivity.this,
                        DetailListViewActivity.class);
    intent.putExtra("attr", viewAttr);
    startActivity(intent);
	overridePendingTransition(0, 0);


	//onbindView中兼容详情返回列表的逻辑,用于添加详情页中去除的jzvd;
	@Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        JzvdStdRv jzvdStdRv;
		//上次播放的jzvd(点击的item),需要parentview去除jzvd,防止添加出现异常;
        if (JzvdStdRv.CURRENT_JZVD != null && AutoPlayUtils.positionInList == position) {
            ViewParent parent = JzvdStdRv.CURRENT_JZVD.getParent();
            if (parent != null) {
                ((ViewGroup) parent).removeView(JzvdStdRv.CURRENT_JZVD);
            }
            holder.container.removeAllViews();
            holder.container.addView(JzvdStdRv.CURRENT_JZVD, new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            jzvdStdRv = (JzvdStdRv) JzvdStdRv.CURRENT_JZVD;
        } else {
			//如果其他的item,某个容器中没有jzvd(特殊情况下),则new一个jzvd;
            if (holder.container.getChildCount() == 0) {
                jzvdStdRv = new JzvdStdRv(holder.container.getContext());
                holder.container.addView(jzvdStdRv,
                        new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT));
            } else {
                jzvdStdRv = (JzvdStdRv) holder.container.getChildAt(0);
            }
            jzvdStdRv.setUp(
                    Urls.videoUrls[0][position],
                    Urls.videoTitles[0][position], Jzvd.SCREEN_NORMAL);
            Glide.with(holder.container.getContext()).load(Urls.videoPosters[0][position])
                    .into(jzvdStdRv.posterImageView);
        }
        jzvdStdRv.setId(R.id.jzvdplayer);
        jzvdStdRv.setAtList(true);
        jzvdStdRv.setClickUi(new JzvdStdRv.ClickUi() {
            @Override
            public void onClickUiToggle() {
                AutoPlayUtils.positionInList = position;
                jzvdStdRv.setAtList(false);
				//封装target大小,xy位置数据传递到下一个页面;
                ViewAttr attr = new ViewAttr();
                int[] location = new int[2];
                holder.container.getLocationInWindow(location);
                attr.setX(location[0]);
                attr.setY(location[1]);
                attr.setWidth(holder.container.getMeasuredWidth());
                attr.setHeight(holder.container.getMeasuredHeight());
                if (onVideoClick != null) onVideoClick.videoClick(holder.container, attr, position);
                jzvdStdRv.setClickUi(null);
            }

            @Override
            public void onClickStart() {
                AutoPlayUtils.positionInList = position;
            }
        });
    }

	//设置第二个页面的 addOnPreDrawListener 事件,在绘制之前播放动画;
	container.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                container.getViewTreeObserver().removeOnPreDrawListener(this);
				//获取当前正在播放的jzvd,并取出来,填充到当前页面的容器vg中,继续播放;
                ViewParent parent = JzvdStdRv.CURRENT_JZVD.getParent();
                if (parent != null) {
                    ((ViewGroup) parent).removeView(JzvdStdRv.CURRENT_JZVD);
                }
                container.addView(JzvdStdRv.CURRENT_JZVD, new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                currentAttr = new ViewAttr();
                int[] location = new int[2];
                container.getLocationInWindow(location);
                currentAttr.setX(location[0]);
                currentAttr.setY(location[1]);
                currentAttr.setWidth(container.getMeasuredWidth());
                currentAttr.setHeight(container.getMeasuredHeight());
				//属性动画工具类;
                new ViewMoveHelper(container, attr, currentAttr, DURATION).startAnim();

                AlphaAnimation animation = new AlphaAnimation(0, 1);
                animation.setDuration(DURATION);
                llContent.setAnimation(animation);
                animation.start();
                return true;
            }
        });

	//返回时,先开始动画移至原位,刷新上个页面同样去除过渡动画,通过adapter判断添加当前播放的jzvd;
	private void backAnimation() {
        new ViewMoveHelper(container, currentAttr, attr, DURATION).startAnim();
        llContent.setVisibility(View.GONE);
        container.postDelayed(new Runnable() {
            @Override
            public void run() {
                ListViewToDetailActivity.listViewToDetailActivity.animateFinish();
                finish();
                overridePendingTransition(0, 0);
            }
        }, DURATION);

    }

```

>列表中自动播放  `AutoPlayUtils`

现象: 根据滑动多少距离判断停止播放, 当滑动停止后,自动播放第一个可见的item; 

```

	public static int positionInList = -1;//记录当前播放列表位置

	/**
		onScrolled 中调用

     * @param firstVisiblePosition 首个可见item位置
     * @param lastVisiblePosition  最后一个可见item位置
     * @param percent              当item被遮挡percent/1时释放,percent取值0-1
     */
    public static void onScrollReleaseAllVideos(int firstVisiblePosition, int lastVisiblePosition, float percent) {
        if (Jzvd.CURRENT_JZVD == null) return;
        if (positionInList >= 0) {
			//可见范围内,
            if ((positionInList <= firstVisiblePosition || positionInList >= lastVisiblePosition - 1)) {
                if (getViewVisiblePercent(Jzvd.CURRENT_JZVD) < percent) {
                    Jzvd.releaseAllVideos();
                }
            }
        }
    }

	/**
     * @param view
     * @return 当前视图可见比列
     */
    public static float getViewVisiblePercent(View view) {
        if (view == null) {
            return 0f;
        }
        float height = view.getHeight();
        Rect rect = new Rect();
        if (!view.getLocalVisibleRect(rect)) {
            return 0f;
        }
		//获取当前播放view可见高度; 返回可见高度和控件高度的比值;
        float visibleHeight = rect.bottom - rect.top;
        return visibleHeight / height;
    }


	/**
		newState == RecyclerView.SCROLL_STATE_IDLE 当滑动停止时,判断当前需要播放的item;

     * @param firstVisiblePosition 首个可见item位置
     * @param lastVisiblePosition  最后一个可见item位置
     */
    public static void onScrollPlayVideo(RecyclerView recyclerView, int jzvdId, int firstVisiblePosition, int lastVisiblePosition) {
        if (JZUtils.isWifiConnected(recyclerView.getContext())) {
            for (int i = 0; i <= lastVisiblePosition - firstVisiblePosition; i++) {
				//在recyclerview中可见item中;
                View child = recyclerView.getChildAt(i);
                View view = child.findViewById(jzvdId);
                if (view != null && view instanceof Jzvd) {
                    Jzvd player = (Jzvd) view;
					//如果可见item中, jzvd是全部可见;
                    if (getViewVisiblePercent(player) == 1f) {
						//如果不是上一个在播放的jzvd,则开始播放,不然会出现滑动停止暂停播放切换;
                        if (positionInList != i + firstVisiblePosition) {
                            player.startButton.performClick();
                        }
                        break;
                    }
                }
            }
        }
    }

```



### Jzvd播放界面 主实现类

> JzvdStd 具体播放界面

具体的播放界面,ui界面扩展功能; Jzvd的控制音量,亮度,快进,快退的基础dialog也于此处实现;

列出几个有特色的实现类: 

- JzvdStdTinyWindow 可自定义小窗播放

```

	public void gotoScreenTiny() {
        Log.i(TAG, "startWindowTiny " + " [" + this.hashCode() + "] ");
        if (state == STATE_NORMAL || state == STATE_ERROR || state == STATE_AUTO_COMPLETE)
            return;
		//获取当前jzvd的parentview,移出当前jzvd,添加一个clonejzvd占位,防止出错,引用当前parentview;
        ViewGroup vg = (ViewGroup) getParent();
        vg.removeView(this);
        cloneAJzvd(vg);
        CONTAINER_LIST.add(vg);
		//获取当前decorview,添加刚移出的jzvd;
        ViewGroup vgg = (ViewGroup) (JZUtils.scanForActivity(getContext())).getWindow().getDecorView();//和他也没有关系
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(400, 400);
        lp.gravity = Gravity.RIGHT | Gravity.BOTTOM;
        //添加滑动事件等
        vgg.addView(this, lp);
		//进行一些显示隐藏的设置;
        setScreenTiny();
    }

```

- JzvdStdTikTok 仿抖音

- JzvdStdGetGif 支持生成gif

- JzvdStdLockScreen 支持播放时锁功能

- JzvdStdSpeed 变速播放

- AGVideo 仿爱奇艺等播放样式,选集,倍速播放;

- JzvdStdMp3  本质上就是播放的时候不隐藏缩略图

----
### 评估

首先这个三方android视频播放库的完成度已经非常高了,同时支持切换播放引擎,横竖屏切换,支持recyclerview,webview,传感器横屏,电量监听,保存gif等等功能;还有仿抖音和爱奇艺样式的demo, 非常值得学习;

说下改进点,其实是完善点吧[逃]

- `Jzvd`和`JzvdStd`部分逻辑视频播放抽象部分和实现部分的抽取更加清晰就更完美了; 
- 某些`功能类`如果能单独拆分就更好了;
- 某些`变量`也单独抽取;

----
