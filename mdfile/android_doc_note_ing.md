# android 官网系统知识查漏补缺

## 引言

## 官方网站 [android gov](https://developer.android.com/)

## 官方网站中文 [android gov cn](https://developer.android.google.cn/guide)

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

- Activity
- 服务 Service
- 广播接收器 BoardCaseReceiver
- 内容提供程序 ContentProvider

> [Activity](https://developer.android.google.cn/reference/android/app/Activity)

Activity 是与用户交互的入口点,拥有界面的单个屏幕;

>[Service](https://developer.android.google.cn/reference/android/app/Service)

服务是一个通用入口点，用于因各种原因使应用在后台保持运行状态。它是一种在后台运行的组件，用于执行长时间运行的操作或为**远程进程**执行作业;服务不提供界面。

```

**注意**：如果您的应用面向 Android 5.0（API 级别 21）或更高版本，请使用 JobScheduler 类来调度操作。

JobScheduler 的优势在于，它能通过优化作业调度来降低功耗，以及使用 [Doze API](https://developer.android.google.cn/training/monitoring-device-state/doze-standby)，从而达到省电目的。如需了解有关使用此类的更多信息，请参阅 [JobScheduler](https://developer.android.google.cn/reference/android/app/job/JobScheduler) 参考文档。

```
>[BroadcastReceiver](https://developer.android.google.cn/reference/android/content/BroadcastReceiver)

借助广播接收器组件，系统能够在常规用户流之外向应用传递事件，从而允许应用响应系统范围内的广播通知。

>[ContentProvider](https://developer.android.google.cn/reference/android/content/ContentProvider)

内容提供程序管理一组共享的应用数据，您可以将这些数据存储在文件系统、SQLite 数据库、网络中或者您的应用可访问的任何其他持久化存储位置。

>[Intent](hhttps://developer.android.google.cn/guide/components/intents-filters)

`Activity,Service,BroadcastReceiver` 均通过异步消息 Intent 进行启动。 Intent 会在运行时对各个组件进行互相绑定,可向android系统传递消息,系统会启动其他组件。

> 清单文件

声明应用要求: 

[设备兼容性](https://developer.android.google.cn/guide/practices/compatibility)

```

	<manifest ... > 
		//表示Android 2.1(api 7) 以下,且没有相机功能的设备不能安装应用; request = false,不要求必须使用;
	    <uses-feature android:name="android.hardware.camera.any"
	                  android:required="true" />
	    <uses-sdk android:minSdkVersion="7" android:targetSdkVersion="19" />
	    ...
	</manifest>

```

## 应用资源

### 备用资源

1.在 `res/` 中创建以 `<resources_name>-<config_qualifier>` 形式命名的新目录。 

- `<resources_name>` 是相应默认资源的目录名称
- `<qualifier>` 是指定要使用这些资源的各个配置的名称,可追加多个;

2.将相应的备用资源保存在此新目录下。这些资源文件必须与默认资源文件完全同名。

### [常用限定符配置及顺序](https://developer.android.google.cn/guide/topics/resources/providing-resources)

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
	
	dpi(每英寸像素) = Math.hypot(w,y)/size
	
	density = dpi /160;
	
	px = dp * density;

```

- 平台版本（API 级别） 设备支持的 API 级别。
	- `v3`

>限定符命名规则

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


