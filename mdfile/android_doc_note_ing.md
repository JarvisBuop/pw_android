# android 官网系统知识查漏补缺

# 引言

做开发也有几年了,android官网中太多知识,有挺多的知识并不熟悉; 要知道android最好的学习资料就是 android官网,官方demo,android源码了; 

so, 计划把常用android 知识捋一遍;

-------------------

## 官方网站 [android gov](https://developer.android.com/)

## 官方网站中文 [android gov cn](https://developer.android.google.cn/guide)

# 常见设计应用架构原则

- 分离关注点
- 通过模型驱动界面 (持久性模型)

>推荐应用架构

![](https://developer.android.google.cn/topic/libraries/architecture/images/final-architecture.png)

# 应用基础知识相关

每个 Android 应用都处于各自的安全沙盒中，并受以下 Android 安全功能的保护：

- Android 操作系统是一种多用户 Linux 系统，其中的每个应用都是一个不同的用户；
- 默认情况下，系统会为每个应用分配一个**唯一的 Linux 用户 ID**（该 ID 仅由系统使用，应用并不知晓, 即uid喽）。系统会为应用中的所有文件设置权限，使得只有分配给该应用的用户 ID 才能访问这些文件；
- 每个进程(即pid喽)都拥有自己的**虚拟机 (VM)**，因此应用代码独立于其他应用而运行。
- 默认情况下，每个应用都在其自己的 **Linux 进程内**运行。Android 系统会在需要执行任何应用组件时启动该进程，然后当不再需要该进程或系统必须为其他应用恢复内存时，其便会关闭该进程。

Android 系统实现了`最小权限原则`,每个应用只能访问执行其工作所需的组件，而不能访问其他组件。 这样便能创建非常安全的环境，在此环境中，应用无法访问其未获得权限的系统部分。不过，应用仍可通过一些途径与其他应用共享数据以及访问系统服务：

- 可以安排两个应用**共享同一 Linux 用户 ID**，在此情况下，二者便能访问彼此的文件。为节省系统资源，也可安排拥有相同用户 ID 的应用在**同一 Linux 进程中运行，并共享同一 VM**。应用还必须使用相同的证书进行**签名**。
- 应用可以请求访问设备数据（如用户的联系人、短信消息、可装载存储装置（SD 卡）、相机、蓝牙等）的权限。用户必须明确授予这些权限。如需了解详细信息，请参阅[使用系统权限](https://developer.android.google.cn/training/permissions)。

##应用组件

详细链接:

- [Activity](#Activity)
- [Service](#Service)
- [BroadcastReceiver](#BroadcastReceiver)
- [ContentProvider](#ContentProvider)

### [Activity](https://developer.android.google.cn/reference/android/app/Activity)

Activity 是与用户交互的入口点,拥有界面的单个屏幕;

###[Service](https://developer.android.google.cn/reference/android/app/Service)

服务是一个通用入口点，用于因各种原因使应用在后台保持运行状态。它是一种在后台运行的组件，用于执行长时间运行的操作或为**远程进程**执行作业;服务不提供界面。

```

**注意**：如果您的应用面向 Android 5.0（API 级别 21）或更高版本，请使用 JobScheduler 类来调度操作。

JobScheduler 的优势在于，它能通过优化作业调度来降低功耗，以及使用 [Doze API](https://developer.android.google.cn/training/monitoring-device-state/doze-standby)，从而达到省电目的。如需了解有关使用此类的更多信息，请参阅 [JobScheduler](https://developer.android.google.cn/reference/android/app/job/JobScheduler) 参考文档。

```
###[BroadcastReceiver](https://developer.android.google.cn/reference/android/content/BroadcastReceiver)

借助广播接收器组件，系统能够在常规用户流之外向应用传递事件，从而允许应用响应系统范围内的广播通知。

###[ContentProvider](https://developer.android.google.cn/reference/android/content/ContentProvider)

内容提供程序管理一组共享的应用数据，您可以将这些数据存储在文件系统、SQLite 数据库、网络中或者您的应用可访问的任何其他持久化存储位置。

###[Intent](hhttps://developer.android.google.cn/guide/components/intents-filters)

`Activity,Service,BroadcastReceiver` 均通过异步消息 Intent 进行启动。 Intent 会在运行时对各个组件进行互相绑定,可向android系统传递消息,系统会启动其他组件。

### 清单文件

>[软件包名称和应用 ID](https://developer.android.google.cn/studio/build/application-id)

在将应用构建为最终的应用软件包 (APK) 时，Android 构建工具会使用 `package` 属性完成两件事情：

- 将此名称用作应用所生成 R.java 类（用于访问应用资源）的命名空间。
- 使用此名称解析清单文件中声明的任何相关类名称。

APK 编译完成后，package 属性还可表示应用的通用唯一应用 ID。关于`package`与`项目 build.gradle applicationId `的区别: 

- 如果一致,则无需担心任何问题;
- 软件包名称（由项目目录结构定义）应始终与 AndroidManifest.xml 文件中的 package 属性匹配; 
- 在 APK 构建流程快要结束时，构建工具会使用 build.gradle 文件（Android Studio 项目使用的文件）中的 applicationId 属性替换 package 名称。

tips: 如果您想在发布应用后更改软件包名称，可以这样做，但您必须保持 applicationId 不变。applicationId 定义了应用在 Google Play 上的唯一身份。因此，如果您对其进行更改，则该 APK 就会被视为其他应用，而且使用之前版本的用户将不会收到更新。

>应用组件

四大组件的注册;

>常用 xml 元素

- `<uses-feature>` 声明应用使用的单个硬件或软件功能。通知任何外部实体应用所依赖的硬件和软件功能集。request true 则无法正常工作了; false 下可用 `PackageManager.hasSystemFeature()`判断可获取;
- `<uses-sdk>` <uses-sdk> 元素中的这些属性会被 build.gradle 文件中的相应属性覆盖。

```

	<manifest ... > 
		//表示Android 2.1(api 7) 以下,且没有相机功能的设备不能安装应用; request = false,不要求必须使用;
	    <uses-feature android:name="android.hardware.camera.any"
	                  android:required="true" />
	    <uses-sdk android:minSdkVersion="7" android:targetSdkVersion="19" />
	    ...
	</manifest>

```

- [activity xml元素](https://developer.android.google.cn/guide/topics/manifest/activity-element)
	- `android:allowTaskReparenting` 当下一次将启动 Activity 的任务转至前台时，Activity 是否能从该任务转移至与其有相似性的任务;仅支持启动模式为 `standard,singleTop`;
	- `android:autoRemoveFromRecents` 由具有该属性的 Activity 启动的任务是否一直保留在概览屏幕中，直至任务中的最后一个 Activity 完成为止。 
	- `android:clearTaskOnLaunch` 每当从主屏幕重新启动任务时，是否都从该任务中移除根 Activity 之外的所有 Activity;
	- `android:configChanges` 在运行时发生配置变更时，默认情况下会关闭 Activity 并将其重启，但使用该属性声明配置将阻止 Activity 重启。相反，Activity 会保持运行状态，并且系统会调用其 onConfigurationChanged() 方法。
	- `android:documentLaunchMode` 指定每次启动任务时，应如何向其添加新的 Activity 实例。该属性允许用户让多个来自同一应用的文档出现在概览屏幕中。
	- `android:exported` 此元素设置 Activity 是否可由其他应用的组件启动 
	- `android:finishOnTaskLaunch` 每当用户再次启动 Activity 的任务（在主屏幕上选择任务）时，是否应关闭（完成）现有的 Activity 实例
	- `android:launchMode`  有关应如何启动 Activity 的指令,确定在调用 Activity 处理 Intent 时应执行的操作。
	- `android:parentActivityName` Activity 逻辑父项的类名称。支持导航功能;
	- `android:screenOrientation` 不支持多窗口模式下
		- `sensorLandscape` 屏幕方向为横向，但可根据设备传感器调整为正常或反向的横向。即使用户锁定基于传感器的旋转，系统仍可使用传感器。`sensorPortrait` 同理;
	- `android:taskAffinity` (重要) 与 Activity 有着相似性的任务,即任务栈。任务的相似性由其根 Activity 的相似性确定。相似性确定两点内容:
		- Activity 更改父项后的任务（请参阅 allowTaskReparenting 属性）
		- 以及通过 FLAG_ACTIVITY_NEW_TASK 标记启动 Activity 时，用于容纳该 Activity 的任务。
	- `android:windowSoftInputMode` Activity 的主窗口与包含屏幕软键盘的窗口之间的交互方式。
		- 当 Activity 成为用户注意的焦点时，软键盘的状态为隐藏还是可见。
		- 对 Activity 主窗口所做的调整 — 是否将其尺寸调小，为软键盘腾出空间；或当软键盘遮盖部分窗口时，是否平移其内容以使当前焦点可见。
	- `android:alwaysRetainTaskState` 系统是否始终保持 Activity 所在任务的状态 —“true”表示是;该属性只对任务的根 Activity 有意义;
- [application xml 元素](https://developer.android.google.cn/guide/topics/manifest/application-element)
	- `android:networkSecurityConfig`  [网络安全配置](https://developer.android.google.cn/training/articles/security-config) 可用于抓包配置;
	- `android:usesCleartextTraffic` 指示应用是否打算使用明文网络流量，如明文 HTTP。对于目标 API 级别为 27 或更低级别的应用，默认值为 "true"。对于目标 API 级别为 28 或更高级别的应用，默认值为 "false"。也是与抓包有关;
	- `android:process` activity同,默认进程名为清单包名,需要注意:
		- 如果为此属性分配的名称以冒号（“:”）开头，则会在需要时创建一个应用专用的新进程。
		- 如果进程名称以小写字符开头，则会创建一个采用该名称的全局进程。
	`android:testOnly` 指示此应用是否仅用于测试目的。此类 APK 只能通过 adb 安装，您不能将其发布到 Google Play。点击Run时,as会自动添加此属性;
	- `android:theme` [样式和主题](https://developer.android.google.cn/guide/topics/ui/themes)
	- `android:vmSafeMode` 指示应用是否希望虚拟机在安全模式下运行。默认false;
		- 此属性是在 API 级别 8 中添加的，最初添加时，如果值为“true”，会停用 Dalvik 即时 (JIT just in time) 编译器。
		- 此属性在 API 级别 22 中进行了调整，调整后，如果值为“true”，会停用 ART 预先 (AOT ahead of time) 编译器。

- [intent-filter data xml元素](https://developer.android.google.cn/guide/topics/manifest/data-element)
- `<meta-data>`可以向父组件提供的其他任意数据项的名称值对。一个组件元素可以包含任意数量的 <meta-data> 子元素。所有这些子元素的值收集到一个 `Bundle` 对象，并且可作为 `PackageItemInfo.metaData` 字段提供给组件。

- [permission xml元素](https://developer.android.google.cn/guide/topics/manifest/permission-element) 声明可用于限制对此应用或其他应用的特定组件或功能的访问权限的安全权限。可自定义权限;
	- `android:name` 这是将在代码中（例如，在 <uses-permission> 元素和应用组件的 permission 属性中）用于引用权限的名称。因系统不允许同名权限的存在,建议使用反向域名式命名;
	- `android:permissionGroup` 组权限,对应于` <permission-group> `
	- `android:protectionLevel` 说明权限中隐含的潜在风险，并指示系统在确定是否将权限授予请求授权的应用时应遵循的流程。
		- `normal` 默认值,较低风险,安装时自动授权;
		- `dangerous` 较高风险,6.0后需要申请运行时权限;
		- `signature` 只有在请求授权的应用 使用与声明权限的应用 相同的证书进行签名时系统才会授予的权限。如果证书匹配,系统会自动授予权限;
		- `signatureOrSystem` 即`signature|privileged` 多个供应商将应用内置到一个系统映像中，并且需要明确共享特定功能，因为这些功能是一起构建的。很少使用;

- [provider xml 元素](https://developer.android.google.cn/guide/topics/manifest/provider-element) 声明内容提供程序组件。内容提供程序是 ContentProvider 的子类，可提供对由应用管理的数据的结构化访问机制。<br/>Android 系统根据**授权方**字符串（提供程序的**内容 URI **的一部分）来存储对内容提供程序的引用。
	- 结构: `<scheme>://<host>:<port>[<path>|<pathPrefix>|<pathPattern>]`  <br/>如 `content://com.example.project.healthcareprovider/nurses/rn`
		- `content:` 架构 将 URI 标识为指向 Android 内容提供程序的内容 URI。
		- `host` 标识提供程序本身,Android 系统会在已知提供程序及其授权方的列表中查询该授权方。
		- `path` 是一个`路径`,内容提供程序可使用它来标识提供程序数据的子集。 
	- `android:authorities`(必须)一个或多个 URI 授权方的列表，这些 URI 授权方用于标识内容提供程序提供的数据。 多个授权方用";"分隔; 通常，它是实现提供程序的 ContentProvider 子类的名称。
	- `android:exported` (api >17)内容提供程序是否可供其他应用使用： 默认值取决于服务是否包含 Intent 过滤器。
		- true  提供程序可供其他应用使用。任何应用均可使用提供程序的内容 URI 来访问它，但需依据为提供程序指定的权限进行访问。
		- false 仅限您的应用访问提供程序。只有与提供程序具有相同的用户 ID (UID) 的应用或者通过 android:grantUriPermissions 元素被临时授予对提供程序的访问权限的应用才能访问提供程序。
	- `android:grantUriPermissions` 是否可以向一般无权访问内容提供程序的数据的组件授予访问这些数据的权限，从而暂时克服由 `readPermission、writePermission、permission 和 exported `属性施加的限制。true 则可以授予权限; false 则只能授予`<grant-uri-permission>` 子元素中列出的数据子集(如果有)的权限;
	- `android:initOrder` 顺序实例化优先级,数值越高,初始化越靠前;
	- `android:multiprocess` 决定了是否会创建内容提供程序的多个实例。 将此标志设为 true 可以通过减少进程间通信的开销来提高性能，但也会增加每个进程的内存占用量。
	- `android:name` 实现内容提供程序的类的名称，它是 ContentProvider的子类,全限定名; 另外如果使用`.` 表示`<manifest>`元素中指定的软件包名称;
	- `android:permission,android:readPermission,android:writePermission` 客户端为了读取或写入内容提供程序的数据而必须具备的权限的名称。 readPermission、writePermission 和 grantUriPermissions 属性优先于此属性。

- [receiver xml元素](https://developer.android.google.cn/guide/topics/manifest/receiver-element) 广播接收者的静态申请方法;
	- `android:exported` 广播接收器是否可以接收来自其应用外部来源的消息 ,false 只能接收同一应用或具有相同用户 ID 的应用的组件发送的消息。
	- `android:name` BroadCastReceiver的全限定名;

- [service xml元素](https://developer.android.google.cn/guide/topics/manifest/service-element)将服务（Service 子类）声明为应用的一个组件。
	- `android:exported` 其他应用的组件是否能调用服务或与之交互
	- `android:foregroundServiceType` 阐明服务是满足特定用例要求的前台服务。
	- `android:isolatedProcess` 设置为 true，则此服务将在与系统其余部分隔离的特殊进程下运行。此服务自身没有权限，只能通过 Service API 与其进行通信（绑定和启动）。
	- `android:name` 实现服务的 Service 子类的名称。
	- `android:permission` 实体启动服务或绑定到服务所必需的权限的名称。先获取到权限后才能启动此服务;

- [uses-permission xml 元素](https://developer.android.google.cn/guide/topics/manifest/uses-permission-element)  
	- `android:name` 权限的名称。可以是应用通过 <permission> 元素定义的权限、另一个应用定义的权限，或者一个标准系统权限 ,通常以软件包名称为前缀。
	- `android:maxSdkVersion` 此权限应授予应用的最高 API 级别。
	- 注意 可使用 `<uses-permission-sdk-23>`指明应用需要特定权限，但仅当应用在 Android 6.0（API 级别 23）或更高版本的设备上安装时才需要。

#### [概览屏幕](https://developer.android.google.cn/guide/components/recents)

概览屏幕（也称为最新动态屏幕、最近任务列表或最近使用的应用）是一个系统级别 UI，其中列出了最近访问过的 Activity 和任务。 用户可以浏览该列表并选择要恢复的任务，也可以通过滑动清除任务将其从列表中移除。

- 使用 Activity 属性添加任务 `android:documentLaunchMode`
	- `intoExisting` 该 Activity 会对文档重复使用现有任务。这与不设置 FLAG_ACTIVITY_MULTIPLE_TASK 标志、但设置 FLAG_ACTIVITY_NEW_DOCUMENT 标志所产生的效果相同; 即`如果未找到任务或者 Intent 包含 FLAG_ACTIVITY_MULTIPLE_TASK 标志，则会以该 Activity 作为其根创建新任务。如果找到的话，则会将该任务转到前台并将新 Intent 传递给 onNewIntent()。且新 Activity 将获得 Intent 并在概览屏幕中创建新文档;`
	- `always` 该 Activity 为文档创建新任务，即便文档已打开也是如此。使用此值与同时设置 FLAG_ACTIVITY_NEW_DOCUMENT 和 FLAG_ACTIVITY_MULTIPLE_TASK 标志所产生的效果相同。
	- `none`该 Activity 不会为文档创建新任务。概览屏幕将按其默认方式对待此 Activity：为应用显示单个任务，该任务将从用户上次调用的任意 Activity 开始继续执行。
	- `never`  该 Activity 不会为文档创建新任务。设置此值会替代 FLAG_ACTIVITY_NEW_DOCUMENT 和 FLAG_ACTIVITY_MULTIPLE_TASK 标志的行为（如果在 Intent 中设置了其中一个标志），并且概览屏幕将为应用显示单个任务，该任务将从用户上次调用的任意 Activity 开始继续执行。
- 对于除 none 和 never 以外的值，必须使用 launchMode="standard" 定义 Activity。如果未指定此属性，则使用 documentLaunchMode="none"。

#### [启动模式 || 使用Intent标记](https://developer.android.google.cn/guide/components/activities/tasks-and-back-stack)

- 使用“standard”或“singleTop”启动模式的 Activity 可多次进行实例化。“singleTask”和“singleInstance”Activity 只能启动任务且始终位于 Activity 堆栈的根位置。此外，设备一次只能保留一个 Activity 实例，即一次只允许一个此类任务。
- “standard”和“singleTop”模式只有一处不同: <br/>`standard` 创建新的类实例来响应该 Intent; <br/>`singleTop` 栈顶复用,位于栈顶调用`onNewIntent()`否则创建新的;
- “singleTask”和“singleInstance”模式同样只有一处不同:<br/>`singleTask` Activity 允许其他 Activity 成为其任务的一部分。该 Activity 始终位于其任务的**根位置**，但其他 Activity（必然是“standard”和“singleTop”Activity）可以启动到该任务中。Activity一次只能有一个实例 <br/>
`singleInstance` 与“singleTask"”相同，只是系统不会将任何其他 Activity 启动到包含实例的任务中。它是任务中唯一的 Activity。
- `singletask` **自己理解**就是创建时,先查找其他的任务栈中是否已存在该ActivityA的实例,如果存在将该task置于前台,调用onNewIntent(),也会清除该task中ActivityA上层其他act; 如果没有查找到则创建新的task,并实例化新任务的根 Activity; 
-  **疑问点在于** 官网中描述`FLAG_ACTIVITY_NEW_TASK`等同于`singleTask` ,**个人倾向于** `singletask`等同与`FLAG_ACTIVITY_CLEAR_TOP|FLAG_ACTIVITY_NEW_TASK` intent标记将 singletask 分为两种功能;

存在这样一个情况需要注意, 用户按**返回**按钮都会回到上一个 Activity。 </br>
已存在两个task,如果启动指定`singleTask`启动模式中的某个activity,处于后台的task已存在该activity的实例,系统会将该后台任务**整个**转到前台运行; 如果按返回键,返回的是前台任务中堆栈的act;

![](https://developer.android.google.cn/images/fundamentals/diagram_backstack_singletask_multiactivity.png)

- `FLAG_ACTIVITY_NEW_TASK` : 在新任务中启动 Activity,如果您现在启动的 Activity 已经有任务在运行，则系统会将该任务转到前台并恢复其最后的状态，而 Activity 将在 onNewIntent() 中收到新的 intent。相当于`singleTask`;(**作者注** 官网描述如此,个人觉得有误,`singleTask`觉得更像`FLAG_ACTIVITY_CLEAR_TOP|FLAG_ACTIVITY_NEW_TASK`);
- `FLAG_ACTIVITY_SINGLE_TOP` : 堆栈顶部的 Activity,相当于`singleTop`;
- `FLAG_ACTIVITY_CLEAR_TOP` : 如果要启动的 Activity 已经在当前任务中运行，则不会启动该 Activity 的新实例，而是会销毁位于它之上的所有其他 Activity，并通过 onNewIntent() 将此 intent 传送给它的已恢复实例（现在位于堆栈顶部）。


## 应用资源

### 备用资源

1.在 `res/` 中创建以 `<resources_name>-<config_qualifier>` 形式命名的新目录。 

- `<resources_name>` 是相应默认资源的目录名称
- `<qualifier>` 是指定要使用这些资源的各个配置的名称,可追加多个;

2.将相应的备用资源保存在此新目录下。这些资源文件必须与默认资源文件完全同名。

#### [常用限定符配置及顺序](https://developer.android.google.cn/guide/topics/resources/providing-resources)

- `语言和区域 ` 
	- `en`,`fr`,`en-rUS`加区域码,`b+en`
- `smallestWidth sw<N>dp` 屏幕的基本尺寸,可用屏幕区域的最小尺寸指定;设备的 smallestWidth 是屏幕可用高度和宽度的最小尺寸（您也可将其视为屏幕的“最小可能宽度”）。无论屏幕的当前方向如何，您均可使用此限定符确保应用界面的可用宽度至少为 <N> dp。
	- `sw<N>dp`,`sw600dp`
	- 320,适用于屏幕配置如下的设备,
		- 240x320 ldpi（QVGA 手机）
		- 320x480 mdpi（手机）
		- 480x800 hdpi（高密度手机）

当应用为多个资源目录提供不同的 smallestWidth 限定符值时，系统会使用最接近（但未超出）设备 smallestWidth 的值。

- `可用宽度/可用高度 w<N>dp h<N>dp` 指定资源应使用的最小可用屏幕宽度/高度（以 dp 为单位，由 <N> 值定义）。 屏幕横向和纵向切换时,此配置值也会随之变化，以匹配当前的实际宽度。
	- `w<N>dp`,`w1024dp`,`h1024dp`

- `屏幕尺寸` 
	- small 尺寸类似于低密度 VGA 屏幕的屏幕。小屏幕的最小布局尺寸约为 320x426 dp。
	- normal 尺寸类似于中等密度 HVGA 屏幕的屏幕。标准屏幕的最小布局尺寸约为 320x470 dp。
	- large 尺寸类似于中等密度 VGA 屏幕的屏幕。大屏幕的最小布局尺寸约为 480x640 dp。
	- xlarge 明显大于传统中等密度 HVGA 屏幕的屏幕。超大屏幕的最小布局尺寸约为 720x960 dp。 最常见的是平板式设备。

- `屏幕纵横比`  完全基于屏幕的纵横比（宽屏较宽），并且与屏幕方向无关。
	- `long` 宽屏 ,`notlong` 非宽屏;

- `屏幕方向` 如果用户旋转屏幕，此配置可能会在应用生命周期中发生变化。
	- `port`,`land`

- `界面模式` 
	- car：设备正在车载手机座上显示
	- desk：设备正在桌面手机座上显示
	- television：设备正在通过电视显示内容，通过将界面投影到离用户较远的大屏幕上，为用户提供“十英尺”体验。主要面向遥控交互或其他非触控式交互
	- appliance：设备正在用作没有显示屏的装置
	- watch：设备配有显示屏，并且可戴在手腕上
	- vrheadset：设备正在通过虚拟现实耳机显示内容

- `屏幕像素密度 (dpi)`
	- ldpi：低密度屏幕；约为 120dpi。
	- mdpi：中等密度（传统 HVGA）屏幕；约为 160dpi。
	- hdpi：高密度屏幕；约为 240dpi。
	- xhdpi：超高密度屏幕；约为 320dpi。此项为 API 级别 8 中的新增配置
	- xxhdpi：绝高密度屏幕；约为 480dpi。此项为 API 级别 16 中的新增配置
	- xxxhdpi：极高密度屏幕使用（仅限启动器图标，请参阅支持多种屏幕中的注释）；约为 640dpi。此项为 API 级别 18 中的新增配置
	- nodpi：可用于您不希望为匹配设备密度而进行缩放的位图资源。
	- tvdpi：密度介于 mdpi 和 hdpi 之间的屏幕；约为 213dpi。此限定符并非指“基本”密度的屏幕。它主要用于电视，且大多数应用都不使用该密度 — 大多数应用只会使用 mdpi 和 hdpi 资源，而且系统将根据需要对这些资源进行缩放。此项为 API 级别 13 中的新增配置
	- anydpi：此限定符适合所有屏幕密度，其优先级高于其他限定符。这非常适用于矢量可绘制对象。此项为 API 级别 21 中的新增配置
	- nnndpi：用于表示非标准密度，其中 nnn 是正整数屏幕密度。此限定符不适用于大多数情况。使用标准密度存储分区，可显著减少因支持市场上各种设备屏幕密度而产生的开销。

六个基本密度之间的缩放比为 3:4:6:8:12:16（忽略 tvdpi 密度）;

```

[头条适配方案](https://juejin.im/post/5cf869aaf265da1b8b2b4e14)
	
	相关计算公式
	
	dpi(每英寸像素,屏幕密度) = Math.hypot(w,y)/size
	
	density = dpi /160;
	
	px = dp * density;

	dp(密度无关像素) - 基于屏幕物理密度的抽象单位。这些单位相对于 160 dpi（每英寸点数）屏幕确立，在该屏幕上 1dp 大致等于 1px。

	sp(缩放无关像素) - 这和 dp 单位类似，但它也会根据用户的字体大小偏好设置进行缩放。

```

- 平台版本（API 级别） 设备支持的 API 级别。
	- `v3`

####限定符命名规则

- 您可以为单组资源指定多个限定符，并使用短划线分隔。例如，drawable-en-rUS-land 适用于屏幕方向为横向的美国英语设备。
- 这些限定符必须遵循表 2 中列出的顺序。例如：
	- 错误：drawable-hdpi-port/
	- 正确：drawable-port-hdpi/
- 不能嵌套备用资源目录。
- 值不区分大小写。
- 每种限定符类型仅支持一个值。


### 引用资源/样式格式

代码中访问资源格式

`[<package_name>.]R.<resource_type>.<resource_name>`

- `R.drawable.myimage` 如果引用的资源来自您自己的资源包，则不需要;
- `android.R.layout.simple_list_item_1` 

 XML 资源中引用资源的语法

`@[<package_name>:]<resource_type>/<resource_name>` 

- `@color/red` 引用资源来自相同资源包，则不需要package_name;
- `@android:color/secondary_text_dark`  

引用样式属性的语法

`?[<package_name>:][<resource_type>/]<resource_name>`

- `?android:textColorSecondary` 资源类型为可选项;
- `?android:attr/textColorSecondary`

### Android 如何查找最佳匹配资源

- 淘汰与设备配置冲突的资源文件
	- 屏幕像素密度是唯一一个未因冲突而被淘汰的限定符。
- 根据限定符优先级,选中优先级最高的限定符;
	- 如果问题中的限定符是屏幕像素密度，则 Android 会选择最接近设备屏幕密度的选项。
- 是否有资源目录包含此限定符,淘汰不含此限定符的资源目录。
- 重复第2,3步,直到仅剩一个目录为止。

tips: 在根据屏幕尺寸限定符选择资源时，如果没有更好的匹配资源，则系统将使用专为小于当前屏幕的屏幕而设计的资源(例如，必要时，大尺寸屏幕将使用标准尺寸的屏幕资源) 但是，如果唯一可用的资源大于当前屏幕，则系统**不会**使用这些资源，并且如果没有其他资源与设备配置匹配，应用将会**崩溃**（例如，如果所有布局资源均用 xlarge 限定符标记，但设备是标准尺寸的屏幕）。 **简而言之,就是大屏幕能用小屏幕的资源,小屏幕不能用大屏幕的资源;**

### 处理配置变更

某些设备配置可能会在运行时发生变化（例如屏幕方向、键盘可用性，以及当用户启用多窗口模式时）。发生这种变化时，Android 会重启正在运行的 Activity（先后调用 **onDestroy() 和 onCreate()**）。

- 在配置变更期间保留对象 (可同时使用 onSaveInstanceState()、ViewModel 对象以及持久存储，以在配置变更时保存并恢复 Activity 的界面状态)
- 自行处理配置变更 (`android:configChanges="orientation|keyboardHidden"`)
	- orientation : 屏幕方向发生变更时阻止重启。
	- screenSize : 屏幕方向发生变更时阻止重启，API>=13;
	- keyboardHidden : 可在键盘可用性发生变更时阻止重启。
	- activity 会接受到`onConfigurationChanged()`调用信息; Configuration 代表所有当前配置,且 Activity 的 Resources 对象会相应地进行更新，并根据新配置返回资源;

### 资源类型

#### 可绘制对象

- [属性动画|视图动画](https://developer.android.google.cn/guide/topics/resources/animation-resource)  以及使用方式和属性详解;
- [ColorStateList ](https://developer.android.google.cn/guide/topics/resources/color-list-resource) 颜色状态信息,跟据View的状态匹配不同颜色,系统将应用状态列表中与对象的当前状态匹配的第一项
- [可绘制对象资源](https://developer.android.google.cn/guide/topics/resources/drawable-resource)
	- 位图文件 BitmapDrawable 
		- 在构建过程中，可通过 aapt 工具自动优化位图文件，对图像进行无损压缩。
		- 如果需要以比特流形式读取图片,不需要系统对其优化,改为将图像放在 res/raw/ 文件夹中;
		- xml位图, 将 `<bitmap>` 元素用作 `<item>` 元素的子项。可指定位图的其他属性;
	- 九宫格文件 NinePatchDrawable
		- 可伸缩区域,wrap_content
		- xml九宫格, `<nine-patch>`,可设置图像抖动;
	- 图层列表 LayerDrawable 可绘制对象阵列
		- `<layer-list>`
	- 状态列表  StateListDrawable 不同状态引用不同位图
		- `<selector>`
		- 系统将应用状态列表中与对象的当前状态匹配的第一项
	- 级别列表 LevelListDrawable 备选
		- `<level-list>` `setLevel()`设置drawable的级别值,显示加载级别列表中 `android:maxLevel`值大于等于传递至方法的值的可绘制对象资源;
	- 转换可绘制对象 TransitionDrawable
		- `<transition>`可在两种可绘制对象资源之间交错淡出的可绘制对象。
		- 向前/向后转换 `startTransition()/reverseTransition()`
	- 插入可绘制对象 InsetDrawable
		- 指定距离插入其他可绘制对象的可绘制对象。当视图需要小于视图实际边界的背景时，此类可绘制对象很有用。
		- `<inset>` 
	- 裁剪可绘制对象 ClipDrawable
		- `<clip>` 裁剪可绘制对象。这必须是根元素。
		- 通过改变`level`,增减裁剪量; 默认级别为 0，即完全裁剪，使图像不可见。当级别为 10,000 时，图像不会裁剪，而是完全可见。
	- 缩放可绘制对象 ScaleDrawable
		-`<scale>`
	- 形状可绘制对象 GradientDrawable
		- `<shape>`
	- 动画资源  AnimationDrawable

#### 字体相关

[字体资源 捆绑式字体|可下载字体](https://developer.android.google.cn/guide/topics/resources/font-resource)

####  类型化数组

` TypedArray` 您可以使用这种资源创建其他资源（例如可绘制对象）的数组。 自定义组件使用自定义属性用的比较多;

类型化数组是使用 name 属性中提供的值（而不是 XML 文件的名称）引用的简单资源。因此，您可以在一个 XML 文件中将类型化数组资源与其他简单资源合并到一个 <resources> 元素下。

```

	<?xml version="1.0" encoding="utf-8"?>
    <resources>
        <array name="icons">
            <item>@drawable/home</item>
            <item>@drawable/settings</item>
            <item>@drawable/logout</item>
        </array>
        <array name="colors">
            <item>#FFFF0000</item>
            <item>#FF00FF00</item>
            <item>#FF0000FF</item>
        </array>
    </resources>

	--------------------
	val icons: TypedArray = resources.obtainTypedArray(R.array.icons)
    val drawable: Drawable = icons.getDrawable(0)

    val colors: TypedArray = resources.obtainTypedArray(R.array.colors)
    val color: Int = colors.getColor(0,0)

```

## 应用权限

每款 Android 应用都在访问受限的沙盒中运行。如果应用需要使用其自己的沙盒外的资源或信息，则必须请求相应权限。

- 需在清单文件中使用 `<uses-permission>` 声明需要的权限;
	- 正常权限会自动授予权限;
	- 危险权限需用户明确同意授权;
- 运行时权限 (Android 6.0 (API level 23) or higher)
	- 需要用户一个一个授权或取消;
- 安装时权限 (Android 5.1.1 and below)
	- 接收则全部授权,否则取消安装;
- activity,service,contentprovider中定义的`android:permission` 属性,如果强制执行会抛出`SecurityException`异常; broadcast接收不到消息;
- 特殊权限
	-  `SYSTEM_ALERT_WINDOW and WRITE_SETTINGS` 如果需要使用需要清单中声明,且发送需要用户授权的Intent,系统会响应一个现实详细管理页面的屏幕给用户;
- 权限组注意, 需要明确请求它需要的每项权限，即使用户已授予同一组中的其他权限。

### 请求应用权限

> 权限检查

` ContextCompat.checkSelfPermission(@NonNull Context context, @NonNull String permission)`检查是否有某项权限;
返回值为 `PERMISSION_GRANTED` 或者`PERMISSION_DENIED`

>权限请求

`ActivityCompat.requestPermissions(...)` 会显示一个无法自定义的标准android对话框; 方法异步执行;

> 解释为什么应用需要权限

只在用户之前拒绝过该权限请求的情况下才提供解释。当拒绝第一次系统的权限弹框后,再次申请权限会出现`下次不再出现`的checkbox;

`ActivityCompat.shouldShowRequestPermissionRationale(...)`如果用户之前拒绝了该请求，该方法将返回 true；如果用户之前拒绝了某项权限并且选中了权限请求对话框中的不再询问选项，或者如果设备政策禁止该权限，该方法将返回 false。

```

	// Here, thisActivity is the current activity
    if (ContextCompat.checkSelfPermission(thisActivity,
            Manifest.permission.READ_CONTACTS)
            != PackageManager.PERMISSION_GRANTED) {

        // Permission is not granted
        // Should we show an explanation?
        if (ActivityCompat.shouldShowRequestPermissionRationale(thisActivity,
                Manifest.permission.READ_CONTACTS)) {
            // Show an explanation to the user *asynchronously* -- don't block
            // this thread waiting for the user's response! After the user
            // sees the explanation, try again to request the permission.
        } else {
            // No explanation needed, we can request the permission.
            ActivityCompat.requestPermissions(thisActivity,
                    arrayOf(Manifest.permission.READ_CONTACTS),
                    MY_PERMISSIONS_REQUEST_READ_CONTACTS)

            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.
        }
    } else {
        // Permission has already been granted
    }

```

>处理权限请求响应

`onRequestPermissionsResult()` 用户响应应用的权限请求时,提供会调用此回调;

```

 	override fun onRequestPermissionsResult(requestCode: Int,
            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_READ_CONTACTS -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return
            }

            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }

```
## [应用兼容性](https://developer.android.google.cn/guide/practices/compatibility)

> 设备功能限制

`<users-feature>` 元素阻止安装不具备特定功能的应用;

> 平台版本限制

`<uses-sdk>` 一般gradle中配置;

>屏幕配置适配

- 屏幕尺寸 屏幕物理尺寸 (ppi px per inch)
	- 小,标准,大,特大
	- 灵活布局,避免对界面组件的位置和大小进行硬编码
	- 备用布局,使用备用布局资源;
	- .9图 可拉伸图像;
		- 左上边缘黑线的交点为可以拉伸的位图区域;
		- 右下边缘黑线是定义内容在视图内应进入的安全区域;
	- 使用最小宽度限定符
		- 最小宽度限定符指定屏幕两侧的最小尺寸，而不考虑设备当前的屏幕方向，因此这是一种指定布局可用的整体屏幕尺寸的简单方法。
		- 限定符的尺寸是activity的窗口可用的宽度或高度,dp为单位;
	- 可用宽度限定符
	- 屏幕方向限定符
	- 使用fragment将界面组件模块化

```

    res/layout/main_activity.xml           # For handsets (smaller than 600dp available width)
    res/layout-sw600dp/main_activity.xml   # For 7” tablets (600dp wide and bigger)

    res/layout-land/main_activity.xml           # For handsets in landscape
    res/layout-sw600dp/main_activity.xml        # For 7” tablets
    res/layout-sw600dp-land/main_activity.xml   # For 7” tablets in landscape
    

```

![不同屏幕 dp 宽度与不同屏幕尺寸和方向的一般对应关系](https://developer.android.google.cn/images/screens_support/layout-adaptive-breakpoints_2x.png)

图:建议的宽度断点以支持不同的屏幕尺寸

![](https://developer.android.google.cn/images/ninepatch_raw.png)

- 屏幕密度 dpi (dot per inch)
	- mdpi(中),hdpi(高),xhdpi(超高),xxhdpi(超超高)
	- 密度独立像素 dp (dip dependence px),1 dp 约等于中密度屏幕 mdpi（160dpi；“基准”密度）上的 1 像素。
	- `DisplayMetrics.density` 字段根据当前像素密度指定将 dp 单位转换为像素时所必须使用的缩放系数。 `px = density * dp = dpi/160 * dp`
	- 提供备用位图,以相应的分辨率在应用中提供每个位图的多个版本;
	- 将应用图标放在 mipmap 目录中,某些应用启动器显示的应用图标会比设备的密度级别所要求的大差不多 25%。
	- 使用矢量图形;

- 不常见密度适配
	- 预缩放资源（如可绘制位图资源）;
		- 根据当前屏幕的密度，系统会使用您的应用中特定于密度的任何资源。如果没有针对相应密度的资源可用，系统会加载默认资源，并根据需要将其放大或缩小。
		- 要避免预缩放，最简单的方法就是将资源放在带有 nodpi 配置限定符的资源目录中。
	- 自动缩放像素尺寸和坐标;
		- 可以停用预缩放，具体方法是：将清单中的 android:anyDensity 设置为 "false"；或者针对 Bitmap 以编程方式将 inScaled 设置为 "false"。 这样,像素定义的屏幕元素的显示尺寸与其在基准像素密度 (mdpi) 屏幕上的物理尺寸大致相同。

- [刘海屏的适配](https://developer.android.google.cn/guide/topics/display-cutout)

- 受限屏幕支持
	- 声明最大宽高比:应用布局无法适应宽高比过大的屏幕，可以通过设置最大宽高比显式强行要求在所有 Android 操作系统级别上采用宽屏显示。我们建议使用 2.4 (12:5) 的比例。
		- 需要设置`android:resizeableActivity false`,否则宽高比没有任何作用;
```

	//Android 8.0（API 级别 26）和更高版本设置最大宽高比
	<!-- Render on full screen up to screen aspect ratio of 2.4 -->
    <!-- Use a letterbox on screens larger than 2.4 -->
    <activity android:maxAspectRatio="2.4">
     ...
    </activity>

	//Android 7.1 及更低版本，请在 <application> 元素中添加一个名为 android.max_aspect 的 <meta-data> 元素
	<!-- Render on full screen up to screen aspect ratio of 2.4 -->
    <!-- Use a letterbox on screens larger than 2.4 -->
    <meta-data android:name="android.max_aspect" android:value="2.4" />
    
```

--------------
--------------

# Activity

## 生命周期

- onCreate() : 系统创建 Activity 时触发。
- onStart() : 对用户可见,应用会为 Activity 进入前台并支持互动做准备。
- onResume() : 系统会在 Activity 开始与用户互动之前调用此回调。
- onPause() : 当 Activity 失去焦点并进入“已暂停”状态时，系统就会调用 onPause()。此种状态也可以更新页面;
	- onPause 不可执行耗时操作,**不**应使用 onPause() 来保存应用或用户数据、进行网络调用或执行数据库事务。
- onStop() : 当 Activity 对用户不再可见时，系统会调用 onStop()。
	- 应用应释放或调整在应用对用户不可见时的无用资源。执行 CPU 相对密集的关闭操作,保存数据库等;
	- Activity 对象会继续驻留在内存中, 该对象将维护所有状态和成员信息，但不会附加到窗口管理器。
- onRestart() : 当处于“已停止”状态的 Activity 即将重启时，系统就会调用此回调。
- onDestroy() : 系统会在销毁 Activity 之前调用此回调。
	- Activity 即将结束（由于用户彻底关闭 Activity 或由于系统为 Activity 调用 finish()），或者
	- 由于配置变更（例如设备旋转或多窗口模式），系统暂时销毁 Activity

![Activity 生命周期的简化图示。](https://developer.android.google.cn/guide/components/images/activity_lifecycle.png)


### [生命周期感知型组件](https://developer.android.google.cn/topic/libraries/architecture/lifecycle)

`Lifecycle`  用于存储有关组件（如 Activity 或 Fragment）的生命周期状态的信息，并允许其他对象观察此状态。

- 事件 : 从框架和 Lifecycle 类分派的生命周期事件。这些事件映射到 Activity 和 Fragment 中的回调事件。
- 状态 : 由 Lifecycle 对象跟踪的组件的当前状态。

可以将状态看作图中的节点，将事件看作这些节点之间的边。

![构成 Android Activity 生命周期的状态和事件](https://developer.android.google.cn/images/topic/libraries/architecture/lifecycle-states.svg)

### 管理任务和返回堆栈

任务是用户在执行某项工作时与之互动的一系列 Activity 的集合。

![](https://developer.android.google.cn/images/fundamentals/diagram_backstack.png)


![](https://developer.android.google.cn/images/fundamentals/diagram_multitasking.png)

android的多任务管理,当用户开始一个新任务或通过主屏幕按钮进入主屏幕时，任务可移至“后台”。任务是一个整体单元,任务的返回堆栈保持不变;

>管理任务栈

**清单文件** `<activity>` 属性包括：

- taskAffinity 默认亲和性为包名,亲和性可在两种情况下发挥作用;
	- 当启动 Activity 的 intent 包含 FLAG_ACTIVITY_NEW_TASK 标记时。
	- 当 Activity 的 allowTaskReparenting 属性设为 "true" 时。一旦和 Activity 有亲和性的任务进入前台运行，Activity 就可从其启动的任务转移到该任务。
- launchMode
- allowTaskReparenting
- clearTaskOnLaunch
- alwaysRetainTaskState
- finishOnTaskLaunch

**使用intent标记(优先)** `intent` 标记包括：

- FLAG_ACTIVITY_NEW_TASK
- FLAG_ACTIVITY_CLEAR_TOP
- FLAG_ACTIVITY_SINGLE_TOP

## 进程和应用生命周期

进程类型排序: 

- 前台进程 用户目前执行操作所需的进程,一下任意条件成立即为前台:
	- 它正在用户的互动屏幕上运行一个 Activity（其 onResume() 方法已被调用）。
	- 它有一个 BroadcastReceiver 目前正在运行（其 BroadcastReceiver.onReceive() 方法正在执行）。
	- 它有一个 Service 目前正在执行其某个回调（Service.onCreate()、Service.onStart() 或 Service.onDestroy()）中的代码。

- 可见进程  正在进行用户当前知晓的任务;
	- 它正在运行的 Activity 在屏幕上对用户可见，但不在前台（其 onPause() 方法已被调用）。举例来说，如果前台 Activity 显示为一个对话框，而这个对话框允许在其后面看到上一个 Activity，则可能会出现这种情况。
	- 它有一个 Service 正在通过 Service.startForeground()（要求系统将该服务视为用户知晓或基本上对用户可见的服务）作为前台服务运行。
	- 系统正在使用其托管的服务实现用户知晓的特定功能，例如动态壁纸、输入法服务等。

- 服务流程(后台进程) 包含一个已使用 startService() 方法启动的 Service。例如后台网络数据上传或下载;已经运行了很长时间（例如 30 分钟或更长时间）的服务的重要性可能会降位，以使其进程降至下文所述的缓存 LRU 列表。这有助于避免超长时间运行的服务因内存泄露或其他问题占用大量内存，进而妨碍系统有效利用缓存进程。
- 缓存进程 运行良好的系统将始终有多个缓存进程可用（为了更高效地切换应用），并根据需要定期终止最早的进程。
	- 通常包含用户当前不可见的一个或多个 Activity 实例（onStop() 方法已被调用并返回）
	- 这些进程保存在伪 LRU 列表中,列表中的最后一个进程是为了回收内存而终止的第一个进程。

## Fragment

### 创建fragment

- onCreate() 系统会在创建片段时调用此方法。
- onCreateView() 系统会在片段首次绘制其界面时调用此方法。返回的 View 必须是片段布局的根视图。
- onPause() 系统会将此方法作为用户离开片段的第一个信号（但并不总是意味着此片段会被销毁）进行调用。

对于 Activity 生命周期与片段生命周期而言，二者最显著的差异是在其各自返回栈中的存储方式。默认情况下，Activity 停止时会被放入由系统管理的 Activity 返回栈中。不过，只有当您在移除片段的事务执行期间通过调用 addToBackStack() 显式请求保存实例时，系统才会将片段放入由宿主 Activity 管理的返回栈;

![](https://developer.android.google.cn/images/fragment_lifecycle.png)

![](https://developer.android.google.cn/images/activity_fragment_lifecycle.png)

>[fragmentmanager](https://developer.android.google.cn/reference/androidx/fragment/app/FragmentManager)

- 通过 findFragmentById()（针对在 Activity 布局中提供界面的片段）或 findFragmentByTag()（针对提供或不提供界面的片段）获取 Activity 中存在的片段。
- 通过 popBackStack()（模拟用户发出的返回命令）使片段从返回栈中弹出。
- 通过 addOnBackStackChangedListener() 注册侦听返回栈变化的侦听器。

>执行片段事务

` addToBackStack()`  以将事务添加到片段事务返回栈,该返回栈由 Activity 管理，允许用户通过按返回按钮返回上一片段状态

- 可以将替换事务`replace`保存到返回栈，以便用户能够通过按返回按钮撤消事务并回退到上一片段。 FragmentActivity 会自动通过 onBackPressed() 从返回栈检索片段。
- 如果您向事务添加多个更改（如又一个 `add() 或 remove()`），并调用 addToBackStack()，则调用 commit() 前应用的所有更改都将作为单一事务添加到返回栈，并且返回按钮会将它们一并撤消。

```

	val newFragment = ExampleFragment()
	val transaction = supportFragmentManager.beginTransaction()
	transaction.replace(R.id.fragment_container, newFragment)
	transaction.addToBackStack(null)
	transaction.commit()

```

`commit() ` 不会立即执行事务，而是在 Activity 的界面线程（“主”线程）可执行该操作时，再安排该事务在线程上运行。 也可以从界面线程调用 `executePendingTransactions()`，以立即执行 commit() 提交的事务。 (FragmentTransaction.commitNow() 在提交一个单一交易不改变fragment 回退栈的情况下,用这个替代)

只能在Activity 保存其状态（当用户离开 Activity）之前使用 commit() 提交事务。 不然会发生异常;  对于丢失提交无关紧要的情况，请使用 `commitAllowingStateLoss()`。
