## android 进阶解密 by liu

### android系统架构

- System Apps 应用层
- Java Api Framework 框架层 
- (Native)libraries + android runtion 库和运行时
	- C/C++程序库
		- openGl Es 
		- Media framework 
		- Sqlite 
	- android运行时库
		- ART +核心库 
			- android 5.0系统之后,Dalvik 虚拟机被ART取代; 旧版DVM 中的应用每次运行时,字节码都需要通过即时编译器(Jit Just in Time)转换为机器码,使得应用的运行效率降低;
			- 在ART中,系统在安装app时,会进行一次预编译(Aot Ahead of Time),将字节码预先编译成机器码并存储在本地,应用不用每次运行时编译,效率提升;
- Hardware Abstraction Layer 硬件抽象层
	- 操作系统内核与硬件电路之间的接口层,使其具有硬件无关性,可在多种平台上移植;
- Linux Kernel linux内核

> 源码获取

本地下载:

[百度网盘路径下载](https://pan.baidu.com/s/1ngsZs)

SourceInsight

在线阅读

[androidxref](http://androidxref.com)

------

### Android 系统启动


> android 启动的流程

- 启动电源以及系统启动
	- 引导芯片代码从预定义开始执行, bootloader 加载引导程序到ram执行;

- 引导程序 bootloader
	- `bootloader` 是android操作系统开始运行前的一个小程序,主要作用是把系统OS拉起走运行;

- Linux内核启动
	- 内核启动时,设置缓存,被保护存储器,计划列表,加载驱动; 在内核完成系统设置后,首先在系统文件中寻找`init.rc`文件,并启动init进程;

- init进程启动
	- 主要是初始化和启动属性服务,也用来启动`Zygote`进程;

- 简结: 按下启动电源 -> 加载引导程序bootloader -> linux内核启动 -> 启动init进程


> init进程启动过程

- android系统中用户空间的第一个进程,进程号为1; 作为第一个进程,有许多重要的任务,比如创建Zygote进程,属性服务等;
- init 进程由多个源文件共同组成,位于源码目录 `system/core/init` 中;
- 在linux 内核加载完成后,首先寻找`init.rc`文件,并启动init进程,入口main函数处于 `system/core/init/init.cpp` 中;
	- 在开始的时候启动和挂载所需的文件目录;
	- `property_init()` 对属性初始化,并启动属性服务; 设置 `signal_handler_init()` 函数用于设置子进程信号处理函数,防止init进程的子进程(Zygote进程)成为`僵尸进程`,为防止僵尸进程的出现,系统会在子进程暂停和终止的时候发出 SIGCHLD信号, 此函数就是为了接受 SIGCHLD信号 处理进程终止;
		- 僵尸进程: 在UNIX/Linux中,父进程使用fork创建子进程,在子进程终止之后,如果父进程并不知道子进程已经终止了,这时子进程虽然已经退出了,但是在系统进程表里还为它保留了一定的信息(进程号,退出状态,运行时间等),这个进程就是僵尸进程;系统进程表是一项有限资源,资源耗尽就无法创建新的进程了;
		- 属性服务: 类似window平台的注册管理器,采用键值对的形式记录用户,软件的一些使用信息,在系统或软件重启后,根据之前的注册表内容进行对应的初始化工作;
		- epolll linux内核为处理大批量文件扫描符,是linux下多路复用I/O接口,数据类型是红黑树;
		- 系统属性分类为 控制属性(ctl.开头)和普通属性,
	- 解析 `init.rc` 文件,并启动Zygote进程;
		- init.rc 是一由android 初始化语言 (Android Init Language) 编写的脚本,包含 Action,Command,Service,Option,Import 5种类型语句;
			- service格式: `service <name><pathname>[<argument>]*`
		- init进程启动zygote进程; 在脚本中,zygote 进程的classname 为 `main`,执行程序的路径为`system/bin/app_process64`,对应的文件为`app_main.cpp`,即为zygote的main函数;可以使用脚本通过classname 启动zygote进程;
- init进程启动简结: 
	- 创建和挂载启动所需的文件目录;
	- 初始化和启动属性服务;
	- 解析`init.rc`文件,并启动zygote进程;

> zygote进程启动过程

- android 系统中,Dvm,Art,应用程序进程以及运行系统的关键服务的SystemServer进程都是由 zygote 进程来创建的,也被成为孵化器;通过`fork`的形式来创建应用程序进程和SystemServer进程,由于Zygote进程在启动时会创建Dvm或者Art,因此通过fork而创建的应用程序进程和SystemServer进程可以在内部获取一个Dvm或者Art的实例副本;
- zygote进程是在init进程启动时创建的,最初始的进程名称为 app_process (Android.mk),后面重命名为 zygote; zygote进程 都是通过fork自身来创建子进程的;
- zygote启动过程: 
	- `init.rc` 中 启动classname 为main的进程(zygote进程),而执行路径app_process64 就对应了文件 app_main.cpp,进入了app_main.cpp的main函数中;
	- 通过Android Init Language脚本调用 `app_main.cpp` 的main函数中的 AppRuntime 的start方法来启动 zygote 进程;</br>  `app_main.cpp -start-> AndroidRuntime -main-> zygoteInit --> ZygoteServer`  </br>  关于 AppRuntim 和AndroidRuntime 因为C++暂时不太熟悉,猜测AndroidRuntime应该是AppRuntime的父类,所以app_main中调用AppRuntime的start相当于调用了AndroidRuntime的start方法 (疑问);
![androidRuntime & appRuntime](https://img-blog.csdnimg.cn/201910170804044.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L01ySmFydmlzRG9uZw==,size_16,color_FFFFFF,t_70)
	- AndroidRuntime(`frameworks/base/core/jni/AndroidRuntime.cpp`) 中 创建jvm ,注册jni方法,寻找 zygoteInit ,通过jni调用ZygoteInit的main方法; 此处使用jni调用main方法是因为 zygoteInit 的main方法是由 java语言写的,当前的运行逻辑在native中,需要通过jni 调用java; 也因此 Zygote 就从Native 层进入了java框架层;
	- ZygoteInit (`frameworks/base/core/java/com/android/internal/os/zygoteInit.java`) main中开始:
		- 创建一个 Server端的socket
			- `zygoteServer.registerServerSocket(socketName);` 创建服务端的socket LocalServerSocket,在zygote进程将systemserver进程启动后,在这个服务端socket上等待AMS请求zygote进程来创建新的应用程序进程;
		- 预加载类和资源
		- 启动SystemServer进程
			- zygote中 fork 一个子进程; `Runnable r = forkSystemServer(abiList, socketName, zygoteServer);`
		- 等待AMS请求创建新的应用程序进程;
			- 执行`runSelectLoop()`方法, 死循环等待ams请求zygote进程创建新的应用程序进程; 运行 ServerSocket的accept方法等待socket连接,收到的socket类型可分为`zygote进程和AMS建立连接`(i==0) 和 `AMS向zygote进程发送一个创建新的应用程序进程`(旧版通过ZygoteConnection的runOnce方法创建一个新的应用程序进程,新版是通过ZygoteConnection的processOneCommand方法实现);

![runSelectLoop](https://img-blog.csdnimg.cn/20191018080348334.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L01ySmFydmlzRG9uZw==,size_16,color_FFFFFF,t_70)

- zygote进程启动简结:
	- init.rc 脚本启动zygote进程, 进入到app_main.cpp 的main函数;
	- app_main.cpp创建AppRuntime, 并调用其start方法,创建jvm并注册jni方法;
	- 通过AppRuntime的jni方法调用ZygoteInit的main函数,从native层进入java层;
	- zygoteInit 通过(registerServerSocket)创建服务器端Socket,启动SystemServer进程,通过开启循环(runSelectLoop)等待AMS的请求来创建新的应用程序进程;

```

	//--------------------zygoteInit.java-----------------------

	public static void main(String argv[]) {
        ZygoteServer zygoteServer = new ZygoteServer();

        // Mark zygote start. This ensures that thread creation will throw
        // an error.
        ZygoteHooks.startZygoteNoThreadCreation();

        // Zygote goes into its own process group.
        try {
            Os.setpgid(0, 0);
        } catch (ErrnoException ex) {
            throw new RuntimeException("Failed to setpgid(0,0)", ex);
        }

        final Runnable caller;
        try {
            // Report Zygote start time to tron unless it is a runtime restart
            if (!"1".equals(SystemProperties.get("sys.boot_completed"))) {
                MetricsLogger.histogram(null, "boot_zygote_init",
                        (int) SystemClock.elapsedRealtime());
            }

            String bootTimeTag = Process.is64Bit() ? "Zygote64Timing" : "Zygote32Timing";
            TimingsTraceLog bootTimingsTraceLog = new TimingsTraceLog(bootTimeTag,
                    Trace.TRACE_TAG_DALVIK);
            bootTimingsTraceLog.traceBegin("ZygoteInit");
            RuntimeInit.enableDdms();

            boolean startSystemServer = false;
            String socketName = "zygote";
            String abiList = null;
            boolean enableLazyPreload = false;
            for (int i = 1; i < argv.length; i++) {
                if ("start-system-server".equals(argv[i])) {
                    startSystemServer = true;
                } else if ("--enable-lazy-preload".equals(argv[i])) {
                    enableLazyPreload = true;
                } else if (argv[i].startsWith(ABI_LIST_ARG)) {
                    abiList = argv[i].substring(ABI_LIST_ARG.length());
                } else if (argv[i].startsWith(SOCKET_NAME_ARG)) {
                    socketName = argv[i].substring(SOCKET_NAME_ARG.length());
                } else {
                    throw new RuntimeException("Unknown command line argument: " + argv[i]);
                }
            }

            if (abiList == null) {
                throw new RuntimeException("No ABI list supplied.");
            }

            zygoteServer.registerServerSocket(socketName);
            // In some configurations, we avoid preloading resources and classes eagerly.
            // In such cases, we will preload things prior to our first fork.
            if (!enableLazyPreload) {
                bootTimingsTraceLog.traceBegin("ZygotePreload");
                EventLog.writeEvent(LOG_BOOT_PROGRESS_PRELOAD_START,
                    SystemClock.uptimeMillis());
                preload(bootTimingsTraceLog);
                EventLog.writeEvent(LOG_BOOT_PROGRESS_PRELOAD_END,
                    SystemClock.uptimeMillis());
                bootTimingsTraceLog.traceEnd(); // ZygotePreload
            } else {
                Zygote.resetNicePriority();
            }

            // Do an initial gc to clean up after startup
            bootTimingsTraceLog.traceBegin("PostZygoteInitGC");
            gcAndFinalize();
            bootTimingsTraceLog.traceEnd(); // PostZygoteInitGC

            bootTimingsTraceLog.traceEnd(); // ZygoteInit
            // Disable tracing so that forked processes do not inherit stale tracing tags from
            // Zygote.
            Trace.setTracingEnabled(false, 0);

            // Zygote process unmounts root storage spaces.
            Zygote.nativeUnmountStorageOnInit();

            // Set seccomp policy
            Seccomp.setPolicy();

            ZygoteHooks.stopZygoteNoThreadCreation();

            if (startSystemServer) {
                Runnable r = forkSystemServer(abiList, socketName, zygoteServer);

                // {@code r == null} in the parent (zygote) process, and {@code r != null} in the
                // child (system_server) process.
                if (r != null) {
                    r.run();
                    return;
                }
            }

            Log.i(TAG, "Accepting command socket connections");

            // The select loop returns early in the child process after a fork and
            // loops forever in the zygote.
            caller = zygoteServer.runSelectLoop(abiList);
        } catch (Throwable ex) {
            Log.e(TAG, "System zygote died with exception", ex);
            throw ex;
        } finally {
            zygoteServer.closeServerSocket();
        }

        // We're in the child process and have exited the select loop. Proceed to execute the
        // command.
        if (caller != null) {
            caller.run();
        }
    }	

```

> SystemServer 处理过程

- ZygoteInit -> AndroidRuntime -> RuntimeInit ->  MethodAndArgsCaller -> SystemServer

- 主要用于创建系统服务,fork于zygote进程,也会得到zygote进程创建的socket,这个socket对于SystemServer需要关闭;

```

	//------------------zygoteInit.java---------------------

	/**
     * Prepare the arguments and forks for the system server process.
     *
     * Returns an {@code Runnable} that provides an entrypoint into system_server code in the
     * child process, and {@code null} in the parent.
     */
    private static Runnable forkSystemServer(String abiList, String socketName,
            ZygoteServer zygoteServer) {
        long capabilities = posixCapabilitiesAsBits(
            OsConstants.CAP_IPC_LOCK,
            OsConstants.CAP_KILL,
            OsConstants.CAP_NET_ADMIN,
            OsConstants.CAP_NET_BIND_SERVICE,
            OsConstants.CAP_NET_BROADCAST,
            OsConstants.CAP_NET_RAW,
            OsConstants.CAP_SYS_MODULE,
            OsConstants.CAP_SYS_NICE,
            OsConstants.CAP_SYS_PTRACE,
            OsConstants.CAP_SYS_TIME,
            OsConstants.CAP_SYS_TTY_CONFIG,
            OsConstants.CAP_WAKE_ALARM
        );
        /* Containers run without this capability, so avoid setting it in that case */
        if (!SystemProperties.getBoolean(PROPERTY_RUNNING_IN_CONTAINER, false)) {
            capabilities |= posixCapabilitiesAsBits(OsConstants.CAP_BLOCK_SUSPEND);
        }
        /* Hardcoded command line to start the system server */
        String args[] = {
            "--setuid=1000",
            "--setgid=1000",
            "--setgroups=1001,1002,1003,1004,1005,1006,1007,1008,1009,1010,1018,1021,1023,1032,3001,3002,3003,3006,3007,3009,3010",
            "--capabilities=" + capabilities + "," + capabilities,
            "--nice-name=system_server",
            "--runtime-args",
            "com.android.server.SystemServer",
        };
        ZygoteConnection.Arguments parsedArgs = null;

        int pid;

        try {
            parsedArgs = new ZygoteConnection.Arguments(args);
            ZygoteConnection.applyDebuggerSystemProperty(parsedArgs);
            ZygoteConnection.applyInvokeWithSystemProperty(parsedArgs);

            /* Request to fork the system server process */
            pid = Zygote.forkSystemServer(
                    parsedArgs.uid, parsedArgs.gid,
                    parsedArgs.gids,
                    parsedArgs.debugFlags,
                    null,
                    parsedArgs.permittedCapabilities,
                    parsedArgs.effectiveCapabilities);
        } catch (IllegalArgumentException ex) {
            throw new RuntimeException(ex);
        }

        /* For child process */
        if (pid == 0) {
            if (hasSecondZygote(abiList)) {
                waitForSecondaryZygote(socketName);
            }

            zygoteServer.closeServerSocket();
            return handleSystemServerProcess(parsedArgs);
        }

        return null;
    }


```

- `handleSystemServerProcess`(ZygoteInit) 方法启动SystemServer;创建PathClassLoader,调用`zygoteInit()`方法
	- `zygoteInit()`方法 调用jni层`nativeZygoteInit()`(位于frameworks/base/core/jni/AndroidRuntime.cpp)方法,native层代码最终调用AppRuntime的`onZygoteInit()`方法,最终`启动Binder线程池`,这样SystemServer进程就可以使用Binder与其他进程进行通信;
	- `applicationInit()`方法通过`findStaticMain()`方法使用反射获取`com.android.server.SystemServer`类;找到main方法并封装成`MethodAndArgsCaller (implement Runnable)`return出去稍后调用,由此进入到SystemServer的main方法中;

```

	//---------------------ZygoteInit.java ---------------------

	/**
     * Finish remaining work for the newly forked system server process.
     */
    private static Runnable handleSystemServerProcess(ZygoteConnection.Arguments parsedArgs) {
        ...

        if (parsedArgs.invokeWith != null) {
            ...
        } else {
            ClassLoader cl = null;
            if (systemServerClasspath != null) {
                cl = createPathClassLoader(systemServerClasspath, parsedArgs.targetSdkVersion);

                Thread.currentThread().setContextClassLoader(cl);
            }

            /*
             * Pass the remaining arguments to SystemServer.
             */
            return ZygoteInit.zygoteInit(parsedArgs.targetSdkVersion, parsedArgs.remainingArgs, cl);
        }

        /* should never reach here */
    }

	/**
     * The main function called when started through the zygote process. This
     * could be unified with main(), if the native code in nativeFinishInit()
     * were rationalized with Zygote startup.<p>
     *
     * Current recognized args:
     * <ul>
     *   <li> <code> [--] &lt;start class name&gt;  &lt;args&gt;
     * </ul>
     *
     * @param targetSdkVersion target SDK version
     * @param argv arg strings
     */
    public static final Runnable zygoteInit(int targetSdkVersion, String[] argv, ClassLoader classLoader) {
        if (RuntimeInit.DEBUG) {
            Slog.d(RuntimeInit.TAG, "RuntimeInit: Starting application from zygote");
        }

        Trace.traceBegin(Trace.TRACE_TAG_ACTIVITY_MANAGER, "ZygoteInit");
        RuntimeInit.redirectLogStreams();

        RuntimeInit.commonInit();
		//启动Binder线程池;
        ZygoteInit.nativeZygoteInit();
		//进入到SystemServer的main方法;
        return RuntimeInit.applicationInit(targetSdkVersion, argv, classLoader);
    }

	//-----------------------RuntimeInit.java----------------------------

	/**
     * Invokes a static "main(argv[]) method on class "className".
     * Converts various failing exceptions into RuntimeExceptions, with
     * the assumption that they will then cause the VM instance to exit.
     *
     * @param className Fully-qualified class name
     * @param argv Argument vector for main()
     * @param classLoader the classLoader to load {@className} with
     */
    private static Runnable findStaticMain(String className, String[] argv,
            ClassLoader classLoader) {
        Class<?> cl;

        try {
            cl = Class.forName(className, true, classLoader);
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException(
                    "Missing class when invoking static main " + className,
                    ex);
        }

        Method m;
        try {
            m = cl.getMethod("main", new Class[] { String[].class });
        } catch (NoSuchMethodException ex) {
            throw new RuntimeException(
                    "Missing static main on " + className, ex);
        } catch (SecurityException ex) {
            throw new RuntimeException(
                    "Problem getting static main on " + className, ex);
        }

        int modifiers = m.getModifiers();
        if (! (Modifier.isStatic(modifiers) && Modifier.isPublic(modifiers))) {
            throw new RuntimeException(
                    "Main method is not public and static on " + className);
        }

        /*
         * This throw gets caught in ZygoteInit.main(), which responds
         * by invoking the exception's run() method. This arrangement
         * clears up all the stack frames that were required in setting
         * up the process.
         */
        return new MethodAndArgsCaller(m, argv);
    }

```

- 解析SystemServer进程
	- 在SystemServer main方法中new一个SystemServer并调用run()方法;
	- 创建了SystemServiceManager ,启动android系统服务;
	- android 将系统服务分为`引导服务`,`核心服务`,`其他服务`;
		- 引导服务
			- Installer : 安装服务;
			- ActivityManagerService : 四大组件管理服务;
			- PackageManagerService : 包管理服务,安装,解析,删除,卸载;
			- ...
		- 核心服务
			- BatteryService : 电池相关服务;
			- UsageStatsService : 记录用户使用app的频率,时长信息;

		- 其他服务 (此方法源码太长就不拷贝观看了,有兴趣的直接去看源码)
			- VibratorService : 震动服务
			- Network相关服务;
			- WindowManagerService : 窗口管理事件
			- AlarmMangerService : 全局定时管理器
			- CameraService : 摄像头服务;
			- InputManagerService : 管理输入事件
			- BluetoothService : 蓝牙管理服务;
			- NotificationManagerService: 通知管理服务;
			- DeviceStorageMonitorService : 存储相关管理服务;
			- LocationManagerService : 定位管理服务;
			- AudioService : 音频服务;
			- StatusBarManagerService ,ClipboardService ,JobSchedulerService, VoiceInteractionManagerService

```

	//-----------------SystemServer.java-------------------

	private void run() {
        try {
			//时间判断
			//时区设置
			//语言设置
            ...

            // Here we go!
            Slog.i(TAG, "Entered the Android system server!");
            int uptimeMillis = (int) SystemClock.elapsedRealtime();
            EventLog.writeEvent(EventLogTags.BOOT_PROGRESS_SYSTEM_RUN, uptimeMillis);
            if (!mRuntimeRestart) {
                MetricsLogger.histogram(null, "boot_system_server_init", uptimeMillis);
            }

            //设置vmRuntime参数
			//设置Binder参数;
			...

            // Prepare the main looper thread (this thread).
            android.os.Process.setThreadPriority(
                android.os.Process.THREAD_PRIORITY_FOREGROUND);
            android.os.Process.setCanSelfBackground(false);
			//非常熟悉的东西 !!! 创建主线程轮询器 Looper
            Looper.prepareMainLooper();

            // Initialize native services. 
			//加载动态库	libandroid_servers.so
            System.loadLibrary("android_servers");

            // Check whether we failed to shut down last time we tried.
            // This call may not return.
            performPendingShutdown();

            // Initialize the system context.
			//创建系统的Context上下文;
            createSystemContext();

            // Create the system service manager.
			//对系统服务进行创建,启动和管理生命周期
            mSystemServiceManager = new SystemServiceManager(mSystemContext);
            mSystemServiceManager.setRuntimeRestarted(mRuntimeRestart);
            LocalServices.addService(SystemServiceManager.class, mSystemServiceManager);
            // Prepare the thread pool for init tasks that can be parallelized
            SystemServerInitThreadPool.get();
        } finally {
            traceEnd();  // InitBeforeStartServices
        }

        // Start services.
        try {
            traceBeginAndSlog("StartServices");
			//**核心处**
			//启动引导服务;
            startBootstrapServices();
			//启动核心服务;
            startCoreServices();
			//启动其他服务;
            startOtherServices();
            SystemServerInitThreadPool.shutdown();
        } catch (Throwable ex) {
            Slog.e("System", "******************************************");
            Slog.e("System", "************ Failure starting system services", ex);
            throw ex;
        } finally {
            traceEnd();
        }

		//analysis 相关
        ...

        // Loop forever. 开启主线程的消息循环;
        Looper.loop();
        throw new RuntimeException("Main thread loop unexpectedly exited");
    }

	//-----------------------SystemServer.java---------------------------
	/**
	引导服务: 包含AMS,PMS等重要的类;
     * Starts the small tangle of critical services that are needed to get
     * the system off the ground.  These services have complex mutual dependencies
     * which is why we initialize them all in one place here.  Unless your service
     * is also entwined in these dependencies, it should be initialized in one of
     * the other functions.
     */
    private void startBootstrapServices() {
        Slog.i(TAG, "Reading configuration...");
        final String TAG_SYSTEM_CONFIG = "ReadingSystemConfig";
        traceBeginAndSlog(TAG_SYSTEM_CONFIG);
        SystemServerInitThreadPool.get().submit(SystemConfig::getInstance, TAG_SYSTEM_CONFIG);
        traceEnd();

		//等待 installd 完成启动 为了有机会使用合适的权限去创建重要的目录,如`/data/user`.
		//我们需要在初始化其他服务之前完成此服务;
        traceBeginAndSlog("StartInstaller");
        Installer installer = mSystemServiceManager.startService(Installer.class);
        traceEnd();

        //在某些情况下，在启动应用程序后，我们需要访问设备标识符，
		//因此，在activity manager 之前注册设备标识符策略。
        traceBeginAndSlog("DeviceIdentifiersPolicyService");
        mSystemServiceManager.startService(DeviceIdentifiersPolicyService.class);
        traceEnd();

        // Activity manager runs the show.
        traceBeginAndSlog("StartActivityManager");
        mActivityManagerService = mSystemServiceManager.startService(
                ActivityManagerService.Lifecycle.class).getService();
        mActivityManagerService.setSystemServiceManager(mSystemServiceManager);
        mActivityManagerService.setInstaller(installer);
        traceEnd();

        // Power manager需要尽早启动，因为其他服务需要它。
		//本地守护进程可能等待它被注册，所以它必须准备好
		//立即去处理进来的binder调用(包括能验证这些调用的权限)
        traceBeginAndSlog("StartPowerManager");
        mPowerManagerService = mSystemServiceManager.startService(PowerManagerService.class);
        traceEnd();

        // Now that the power manager has been started, let the activity manager
        // initialize power management features.
        traceBeginAndSlog("InitPowerManagement");
        mActivityManagerService.initPowerManagement();
        traceEnd();

        // Bring up recovery system in case a rescue party needs a reboot
        if (!SystemProperties.getBoolean("config.disable_noncore", false)) {
            traceBeginAndSlog("StartRecoverySystemService");
            mSystemServiceManager.startService(RecoverySystemService.class);
            traceEnd();
        }

        // Now that we have the bare essentials of the OS up and running, take
        // note that we just booted, which might send out a rescue party if
        // we're stuck in a runtime restart loop.
        RescueParty.noteBoot(mSystemContext);

        // Manages LEDs and display backlight so we need it to bring up the display.
        traceBeginAndSlog("StartLightsService");
        mSystemServiceManager.startService(LightsService.class);
        traceEnd();

        // Display manager is needed to provide display metrics before package manager
        // starts up.
        traceBeginAndSlog("StartDisplayManager");
        mDisplayManagerService = mSystemServiceManager.startService(DisplayManagerService.class);
        traceEnd();

        // We need the default display before we can initialize the package manager.
        traceBeginAndSlog("WaitForDisplay");
        mSystemServiceManager.startBootPhase(SystemService.PHASE_WAIT_FOR_DEFAULT_DISPLAY);
        traceEnd();

        // Only run "core" apps if we're encrypting the device.
        String cryptState = SystemProperties.get("vold.decrypt");
        if (ENCRYPTING_STATE.equals(cryptState)) {
            Slog.w(TAG, "Detected encryption in progress - only parsing core apps");
            mOnlyCore = true;
        } else if (ENCRYPTED_STATE.equals(cryptState)) {
            Slog.w(TAG, "Device encrypted - only parsing core apps");
            mOnlyCore = true;
        }

        // Start the package manager.
        if (!mRuntimeRestart) {
            MetricsLogger.histogram(null, "boot_package_manager_init_start",
                    (int) SystemClock.elapsedRealtime());
        }
        traceBeginAndSlog("StartPackageManagerService");
        mPackageManagerService = PackageManagerService.main(mSystemContext, installer,
                mFactoryTestMode != FactoryTest.FACTORY_TEST_OFF, mOnlyCore);
        mFirstBoot = mPackageManagerService.isFirstBoot();
        mPackageManager = mSystemContext.getPackageManager();
        traceEnd();
        if (!mRuntimeRestart && !isFirstBootOrUpgrade()) {
            MetricsLogger.histogram(null, "boot_package_manager_init_ready",
                    (int) SystemClock.elapsedRealtime());
        }
        // Manages A/B OTA dexopting. This is a bootstrap service as we need it to rename
        // A/B artifacts after boot, before anything else might touch/need them.
        // Note: this isn't needed during decryption (we don't have /data anyways).
        if (!mOnlyCore) {
            boolean disableOtaDexopt = SystemProperties.getBoolean("config.disable_otadexopt",
                    false);
            if (!disableOtaDexopt) {
                traceBeginAndSlog("StartOtaDexOptService");
                try {
                    OtaDexoptService.main(mSystemContext, mPackageManagerService);
                } catch (Throwable e) {
                    reportWtf("starting OtaDexOptService", e);
                } finally {
                    traceEnd();
                }
            }
        }

        traceBeginAndSlog("StartUserManagerService");
        mSystemServiceManager.startService(UserManagerService.LifeCycle.class);
        traceEnd();

        // Initialize attribute cache used to cache resources from packages.
        traceBeginAndSlog("InitAttributerCache");
        AttributeCache.init(mSystemContext);
        traceEnd();

        // Set up the Application instance for the system process and get started.
        traceBeginAndSlog("SetSystemProcess");
        mActivityManagerService.setSystemProcess();
        traceEnd();

        // DisplayManagerService needs to setup android.display scheduling related policies
        // since setSystemProcess() would have overridden policies due to setProcessGroup
        mDisplayManagerService.setupSchedulerPolicies();

        // Manages Overlay packages
        traceBeginAndSlog("StartOverlayManagerService");
        mSystemServiceManager.startService(new OverlayManagerService(mSystemContext, installer));
        traceEnd();

        //传感器服务需要访问package manager service，app ops
		//service 和 permissions service，因此我们在它们之后启动它。
		//在单独的线程中启动传感器服务。在使用之前应检查完成情况
        mSensorServiceStart = SystemServerInitThreadPool.get().submit(() -> {
            TimingsTraceLog traceLog = new TimingsTraceLog(
                    SYSTEM_SERVER_TIMING_ASYNC_TAG, Trace.TRACE_TAG_SYSTEM_SERVER);
            traceLog.traceBegin(START_SENSOR_SERVICE);
            startSensorService();
            traceLog.traceEnd();
        }, START_SENSOR_SERVICE);
    }

	//---------------SystemServer.java-------------------

	/**
     * Starts some essential services that are not tangled up in the bootstrap process.
     */
    private void startCoreServices() {
        // Records errors and logs, for example wtf() ||~_~
        traceBeginAndSlog("StartDropBoxManager");
        mSystemServiceManager.startService(DropBoxManagerService.class);
        traceEnd();

        traceBeginAndSlog("StartBatteryService");
        // Tracks the battery level.  Requires LightService.
        mSystemServiceManager.startService(BatteryService.class);
        traceEnd();

        // Tracks application usage stats.
        traceBeginAndSlog("StartUsageService");
        mSystemServiceManager.startService(UsageStatsService.class);
        mActivityManagerService.setUsageStatsManager(
                LocalServices.getService(UsageStatsManagerInternal.class));
        traceEnd();

        // Tracks whether the updatable WebView is in a ready state and watches for update installs.
        traceBeginAndSlog("StartWebViewUpdateService");
        mWebViewUpdateService = mSystemServiceManager.startService(WebViewUpdateService.class);
        traceEnd();
    }

	/**
     * Starts a miscellaneous grab bag of stuff that has yet to be refactored
     * and organized.
     */
    private void startOtherServices() {
		//...			

		// We now tell the activity manager it is okay to run third party
        // code.  It will call back into us once it has gotten to the state
        // where third party code can really run (but before it has actually
        // started launching the initial applications), for us to complete our
        // initialization.
        mActivityManagerService.systemReady(() -> {
			Slog.i(TAG, "Making services ready");
            traceBeginAndSlog("StartActivityManagerReadyPhase");
            mSystemServiceManager.startBootPhase(
                    SystemService.PHASE_ACTIVITY_MANAGER_READY);
            traceEnd();
			//...
			
			//ActivityStackSupervisor 在ActivityManagerService构造函数中初始化(AMS由SystemServiceManager.startService函数反射初始化);
			mStackSupervisor.resumeFocusedStackTopActivityLocked();
            mUserController.sendUserSwitchBroadcastsLocked(-1, currentUserId);
		}
	}

```

- SystemServer进程简结:
	- zygote fork出 SystemServer进程;
	- systemserver 启动Binder线程池,就可以用于其他进程进行通信了;
	- 创建SystemServiceManager,用于对系统的服务进行创建,启动和生命周期管理; 
		- 除用`SystemServiceManager`的startService函数来启动系统服务外,也可以通过`ServiceManager.addService(String name, IBinder service)`创建完添加到容器中的方式启动;
	- 启动各种系统服务,包括引导服务,核心服务,其他服务;

> Launcher 启动过程

- 系统启动的最后一步是启动一个应用程序用来显示系统中已经安装的应用程序,这个应用程序叫做Launcher;
- Launcher 在启动过程中会请求PackageManagerService系统中已经安装的应用程序的信息,并将这些信息封装成一个快捷图标列表显示在系统屏幕上,用户可通过点击这些快捷图标来启动相应的应用程序;
	- 作为android系统的启动器,用于启动应用程序
	- 作为android系统的桌面,用于显示和管理应用程序的快捷图标或者其他桌面组件

- Launcher 启动过程介绍: 
	- SystemServer启动时会启动PackageManagerService ,PackageManagerService启动后会将系统中的应用程序安装完成;在此之前已经启动的AMS会将Launcher启动起来; </br> SystemServer --> Ams <--> ActivityStackSupervisor <--> ActivityStack 
	- 启动launcher的入口为AMS的systemReady方法,依次调用最后会由`ActivityStack`调用Ams的`startHomeActivityLocked`方法,
	- 最终启动`com.android.Launcher.launcher` 的activity;

```
	
	//-----------------ActivityManagerSerivce.java----------------------	

	boolean startHomeActivityLocked(int userId, String reason) {
        if (mFactoryTest == FactoryTest.FACTORY_TEST_LOW_LEVEL
                && mTopAction == null) {
            // We are running in factory test mode, but unable to find
            // the factory test app, so just sit around displaying the
            // error message and don't try to start anything.
            return false;
        }
        Intent intent = getHomeIntent();
        ActivityInfo aInfo = resolveActivityInfo(intent, STOCK_PM_FLAGS, userId);
        if (aInfo != null) {
            intent.setComponent(new ComponentName(aInfo.applicationInfo.packageName, aInfo.name));
            // Don't do this if the home app is currently being
            // instrumented.
            aInfo = new ActivityInfo(aInfo);
            aInfo.applicationInfo = getAppInfoForUser(aInfo.applicationInfo, userId);
            ProcessRecord app = getProcessRecordLocked(aInfo.processName,
                    aInfo.applicationInfo.uid, true);
            if (app == null || app.instr == null) {
                intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NEW_TASK);
                final int resolvedUserId = UserHandle.getUserId(aInfo.applicationInfo.uid);
                // For ANR debugging to verify if the user activity is the one that actually
                // launched.
				//如果没有启动Launcher,就会调用ActivityStarter的此方法启动Launcher,mActivityStarter 也在ActivityManagerService构造函数中初始化;
                final String myReason = reason + ":" + userId + ":" + resolvedUserId;
                mActivityStarter.startHomeActivityLocked(intent, aInfo, myReason);
            }
        } else {
            Slog.wtf(TAG, "No home screen found for " + intent, new Throwable());
        }

        return true;
    }

	//-----------------ActivityStarter.java-------------------

	 void startHomeActivityLocked(Intent intent, ActivityInfo aInfo, String reason) {
		//launcher 放入 HomeStack(ActivityStack)中;
        mSupervisor.moveHomeStackTaskToTop(reason);
		//类似于普通Activity的启动过程;
        mLastHomeActivityStartResult = startActivityLocked(null /*caller*/, intent,
                null /*ephemeralIntent*/, null /*resolvedType*/, aInfo, null /*rInfo*/,
                null /*voiceSession*/, null /*voiceInteractor*/, null /*resultTo*/,
                null /*resultWho*/, 0 /*requestCode*/, 0 /*callingPid*/, 0 /*callingUid*/,
                null /*callingPackage*/, 0 /*realCallingPid*/, 0 /*realCallingUid*/,
                0 /*startFlags*/, null /*options*/, false /*ignoreTargetSecurity*/,
                false /*componentSpecified*/, mLastHomeActivityStartRecord /*outActivity*/,
                null /*inTask*/, "startHomeActivity: " + reason);
        if (mSupervisor.inResumeTopActivity) {
            // If we are in resume section already, home activity will be initialized, but not
            // resumed (to avoid recursive resume) and will stay that way until something pokes it
            // again. We need to schedule another resume.
            mSupervisor.scheduleResumeTopActivities();
        }
    }

```


> android 系统启动流程简结

- 启动电源以及系统启动
	- 当电源按下时引导芯片代码从预定义的地方(固化在Rom)开始执行;加载引导程序bootloader到Ram,然后执行;

- 引导程序BootLoader
	- 引导程序BootLoader是在android操作系统开始运行前的一个小程序,主要作用是把系统os拉起来并运行;

- linux内核启动
	- 当内核启动时,设置缓存,被保护存储器,计划列表,加载驱动;当内核完成系统设置时,首先在系统文件中寻找`init.rc`文件,启动init进程;
	
- init进程启动
	- 初始化和启动属性服务,并且启动Zygote进程;

- Zygote进程启动
	- 创建Jvm并注册jni方法,创建服务器端Socket,启动SystemServer进程;

- SystemServer进程启动
	- 启动Binder线程池和SystemServiceManager,并启动各种系统服务;

- launcher程序启动
	- 被SystemServer进程启动的Ams会启动Launcher,Launcher启动后会将已安装应用的快捷图标显示在界面上;


------------------------------------