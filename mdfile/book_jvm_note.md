# Jvm 内存管理,GC,类文件架构

关键字:  
`HotSpot VM`,`两级即时编译器`, `编译器和解释器混合工作模式`,`模块化`,
`混合语言`,`多核并行`,`函数式编程`,

-----

## 自动内存管理

>jvm 运行时数据区 (JVM栈,本地方法栈,程序计数器,堆,方法区)

- 线程私有
	- Jvm栈 (JVM Stack) 
		- 生命周期与线程相同;
		- 描述的是java方法执行的内存模型:每个方法在执行的同时都会创建一个`栈帧(Stack Frame)`用于存储`局部变量表`,操作数栈,动态链接,方法出口等;
		- `局部变量表` 存放编译期可知的各种基本数据类型(boolean,byte,short,int,long,float,double,char),对象引用(reference类型,可能是一个指向对象起始地址的引用指针,也可能是指向一个代表对象的句柄或其他与此对象相关的位置)和returnAddress类型(指向一条字节码指令的地址);
		- `局部变量表` 64位长度的long和double类型的数据会占用2个局部变量空间(Slot),其余数据类型只占用1个;局部变量表所需的内存空间在编译期间完成分配,方法运行期间不会改变局部变量表大小,进入一个方法时,这个方法在栈帧中分配多大的空间已经是确定的;
		- jvm 规定两种异常: 线程请求的栈深度大于虚拟机所允许的深度,抛出`StackOverflowError`异常; 线程扩展时无法申请到足够的内存,抛出`OutOfMemoryError`异常;
		
	- 本地方法栈 (Native Method Stack)
		- 与Jvm栈的区别是Jvm栈为执行java方法服务,此为使用的Native方法服务;
	- 程序计数器 (Program counter Register)
		- 当前线程所执行的字节码的`行号指示器`
		- 线程执行java方法, 计数器记录的是正在执行的虚拟机字节码指令的地址; 线程执行native方法,计数器则为Undefined,
		- 是唯一一个在jvm中没有规定任何OOM情况的区域;


- 线程共享
	- java 堆 (Heap)
		- JVM启动时创建,目的为存放对象实例;
		- GC管理的主要区域,分代收集算法;
		- 线程共享的堆中可划分出多个线程私有的分配缓冲区(Thread Local Allocation Buffer);
		- 可以抛出 `OutOfMemoryError`异常;
	- 方法区 (Method Area)
		- 用于存储已被JVM加载的类信息,常量,静态变量,即时编译器编译后的代码等数据;
		- 可以抛出 `OutOfMemoryError` 异常;
		- `运行时常量池`(Runtime Constant Pool) 方法区的一部分,存放编译期生成的各种字面量和符号引用,这部分内容将在类加载后进入方法区的运行时常量池中存放;
			- 具备动态性,不一定是编译期才能产生,也就是并非预置于Class文件中常量池的内容才能进入方法区运行时常量池,运行期间也可能将新的变量放入池中,利用最多的就是String的`intern()`方法;
		- 可以抛出 `OutOfMemoryError`异常;
	- 直接内存 (Direct Memory)
		- 不是Jvm运行时数据区一部分;可抛出`OutOfMemoryError`异常;
		- Nio(New Input/Output), 引入基于通道Channel和缓冲区Buffer的I/O方式; 可使用native函数库分配堆外内存,通过存储在java堆中的DirectByteBuffer对象作为这块内存的引用进行操作,避免java堆和native堆中来回复制数据;

----

>对象的创建 (JVM层面的对象创建)

- `new指令 检查`;检查这个指令的参数是否能在常量池中定位一个类的符号引用,检查这个符号引用代表的类是否已被加载,解析,初始化过,如果没有执行类的类加载过程;
- `新生对象分配内存`;对象所需大小在类加载完成后即可完全确定;
	- `指针碰撞`: 分配内存将指针向空闲空间那边挪动一段与对象大小相等的距离;
	- `空闲列表`: jvm维护一个记录可用内存的列表,分配时从列表中找到一块足够大的空间划分给对象实例,并更新列表上的记录;
	- 并发情况下的分配内存分两种方案:
		- jvm采用CAS(compare and swap)加上失败重试的方式保证更新操作的原子性;
		- 将内存分配的动作按照线程划分在不同的空间中,每个线程在java堆中预先分配一小块内存,即(Thread Local Allocation Buffer ,TLAB),那个线程需要分配内存,就在那个线程的tlab上分配,只有tlab用完并分配新的tlab时,才需要同步锁定;
- `初始化零值(不包括对象头)`;
- `设置对象头信息`(Object Header); 从jvm角度看一个新的对象已经产生了,但从java程序看,对象创建刚刚开始(<init>方法还没有执行,所有字段都为零,只有new指令之后接着执行<init>方法,真正可用的对象才算完全产生出来;)

-----

>对象的内存布局 (对象头,实例数据,对齐填充)

- 对象头 (Header)
	- 存储对象自身的运行时数据,哈希吗,GC分代年龄,锁状态标志,线程持有的锁,偏向时间戳等,长度为在32位和64位jvm中分别为32bit和64bit;
	- 类型指针,对象指向它的类元数据的指针,jvm通过这个指针确定这个对象是哪个类的实例,非必须;
	- 如果是java数组,还有记录数组长度的数据;
- 实例数据 
	- 对象真正存储的信息;
	- 存储顺序收到虚拟机分配策略参数(fieldsAllocationStyle)和字段在java源码中定义顺序的影响; 默认顺序为 longs/doubles,ints,shorts/chars,bytes/booleans,oops;
- 对齐填充
	- 占位符,hotSpot vm要求对象起始地址必须是8字节的整数倍,对象的大小必须是8字节的整数倍;

-----

> 对象的访问定位

通过jvm栈上的reference数据来操作堆上的具体数据,reference类型 在jvm规范中定义了一个指向对象的引用; 对象访问方式取决于jvm :

- 句柄访问;
	- java堆中划分出一块内存作为句柄池,reference中存储的是对象的句柄地址;句柄中包含了对象实例数据和类型数据的具体地址信息;
	- 优点在于reference存储的是稳点的句柄地址,垃圾回收时只会改变句柄中的实例数据指针,reference本身不需要改变;
		
- 直接指针;
	- java堆对象的布局中放置对象的类型数据,reference存储的直接就是对象地址;
	- 优点在于速度更快,节省一次指针定位的时间开销;

-----

## 垃圾收集器与内存分配策略

程序计数器,jvm栈,本地方法栈都是线程私有的,每一个栈帧中分配多少内存已经在类结构确定下来就已知了,栈中的栈帧随着方法的进入和退出,内存得以回收;而堆内存和方法区不一样,GC主要针对这部分内存;

> 对象已死判定

- 引用计数算法
	- 对象中添加引用计数器,无法解决循环引用问题;

- 可达性分析算法
	- 通过一系列的`GC Roots`对象作为起始点,从这些节点开始向下搜索,搜索走过的路径就是引用链(Reference Chain) ,当一个对象到GC Roots没有任何引用链相连时,则证明此对象是不可用的;
	- 可作为GC Roots 的对象包括下面几种:
		- jvm栈(栈帧中的本地变量表) 中引用的对象;
		- 方法区中类静态属性,常量引用的对象;
		- 本地方法栈中jni(native方法)引用的对象;

> 引用分类

- 强引用 StrongReference
	- 类似`var obj = Object()`这类的引用,垃圾回收器永远不会回收掉被引用的对象;
	
- 软引用 SoftReference
	- 有用但非必须对象;
	- 在系统将要发生内存溢出异常之前,将会把这些对象列进回收范围之中进行第二次回收;

- 弱引用 WeakReference
	- 非必须对象;
	- 只能生存到下一次垃圾收集发生之前,当垃圾收集器工作时,无论当前内存是否足够,都会回收掉只被弱引用关联的对象;

- 虚引用 PhantomReference
	- 最弱的引用关系,一个对象是否有虚引用,完全不会对其生存时间构成影响;
	- 无法通过虚引用来取得一个对象实例;
	- 为一个对象设置虚引用的唯一目的就是能在这个对象被收集器回收时收到一个系统通知;

> 对象的生存死亡

真正宣告一个对象,至少要经历两次标记过程;

- 对象在经过可达性分析后发现没有与GCRoots相连接的引用链,那它会被第一次标记,并且经过一次筛选,筛选条件为 此对象是否有必要进行`finalize`方法;
	- 当对象没有覆盖finalize方法或finalize已经被jvm调用过,jvm视为没有必要执行,则直接进行GC;
	- 如果jvm视为有必要执行finalize方法,会将此对象放置在一个叫做`F-Queue`队列之中,稍后有一个jvm自动建立的低优先级的Finalizer线程去执行它(触发,不承诺等待它结束);稍后GC将对`F-Queue`中的对象进行第二次小规模的标记,
		- 如果对象在finalize中重新与引用链上的任何一个对象建立关联,那么在第二次标记时移除出"即将回收"的集合;
		- 如果对象在此时还没有建立关联,则被真的回收了;

>方法区的回收

方法区的回收效率较低,主要回收两部分内容: `废弃常量` 和`无用的类`;

判断一个类是无用的类,jvm可以对无用的类进行回收;
	
- 该类的所有实例都已经被回收,java堆中不存在该类实例;
- 加载该类的ClassLoader已经被回收;
- 改类对应的java.lang.Class对象没有在任何地方被引用,无法在任何地方通过反射访问该类的方法;

>垃圾收集算法

- 标记-清除算法 Mark-Sweep
	- 首先标记处所有需要回收的对象,在标记完成后统一回收所有被标记的对象;
	- 不足: 效率低下;清除后空间产生大量不连续的内存碎片;

- 复制算法 Copying
	- 将可用内存划分为容量大小相等的两块,每次只使用其中一块,当着一块用完之后,就将还存活的对象复制到另一块内存上面,然后把已使用的内存空间一次清理掉;
	- 每次都是对整个半区进行回收,运行高效,内存缩小一半代价高;
	- 具体比例分配不需要1:1分配,内存可分为一块较大的Eden空间和两块较小的Survivor空间,每次使用Eden和一块Survivor空间;当回收时,将Eden和Survivor中还存活的对象一次性的复制到另外一块Survivor空间上,最后清理掉Eden和刚才使用的Survivor空间;
	- HotSpot默认Eden和Survivor的比例为8:1,只有10%的内存会被'浪费',当Survivor空间不够用时,需要依赖其他(老年代)内存进行`分配担保` Handle Promotion;
		- 内存的分配担保,如果另外一块Survivor空间没有足够的空间存放上一次新生代收集下来的存活对象,这些对象直接通过分配担保进入老年代;

- 标记-整理算法 Mark-Compact
	- 主要针对于老年代,类似于标记清除算法,但后续步骤不是直接对对象进行清理,而是让存活的对象都向一边移动,然后直接清理掉端边界以外的内存;

- 分代收集算法 Generational Collection
	- 根据对象存活周期的不同将内存划分为几块,一般是将java堆分为新生代和老年代,根据各个年代的特点采用最适当的收集算法;
		- 在新生代,每次垃圾回收只有少量存活,选用复制算法;
		- 在老年代,因为对象存活率高,没有额外空间对它进行分配担保,必须使用'标记-清理'或'标记-整理'算法;

> HotSpot的算法实现

- 枚举根节点
	- gc时需要进行可达性分析,可作为GCRoots的节点主要是全局性的引用(例如常量或类静态属性)与执行上下文(例如栈帧中的本地变量表)中,这项工作必须在一个能确保`一致性`的快照中进行
		- 一致性 指在整个分析期间整个执行系统看起来就像是冻结在某个时间点上,不可以出现分析过程中对象引用关系还在不断变化的情况,该点不满足的话分析结果准确性就无法得到保证;  导致GC进行时必须停顿所有的java执行线程 (Stop The World) ,即时在号称不会停顿的CMS 收集器中,枚举根节点也是需要停顿的;

	- jvm 通过一组称为`OopMap`的数据结构得知哪些地方存放着对象引用;
		- 在类加载完成的时候,hotspot把对象内什么偏移量上是什么类型的数据计算出来,在jit编译过程中,也会在特定的位置记录下栈和寄存器中哪些位置是引用; GC在扫描时就可以直接得知这些信息;

- 安全点 SafePoint
	- 在oopmap的协助下,hotspot可以快速完成GcRoots的枚举,但是oopmap内容变化的指令非常多,可能导致引用关系变化;
	- hotspot并没有为每条指令都生成OopMap,只会在特定的位置记录这些信息,这些位置称为`安全点`; 即程序在执行时并非在所有的地方都能停顿下来GC,只有到达安全点时才能暂停;
	- 安全点的选定 是以程序'是否具有让程序长时间执行的特征'为标准;'长时间'的最明显特征是`指令序列复用`,例如 方法调用,循环调用,异常跳转等;具有这些功能的指令才会产生SafePoint;
	- 在GC发生时,让所有线程(不包括执行jni调用的线程)都跑到最近的安全点在停顿下来,两种类型:
		- 抢先式中断 deprecated
			- Gc时,首先把所有的线程全部中断,如果发现有线程中断的地方不在安全点上,就恢复线程,跑到安全点上;
		- 主动式中断
			- Gc时需要中断线程时,不直接对线程操作,仅仅简单的设置一个标志(可读,不可读),各个线程执行时主动去轮询这个标志,发现中断标志为真时就自己中断挂起;轮询标志的地方和安全点是重合的,另外再加上创建对象需要分配内存的地方;

- 安全区域 SafeRegion
	- safepoint 机制保证了程序执行时,在不太长的时间内就会遇到可进入GC的safepoint; 但是线程处于sleep或者blocked状态,线程无法响应jvm的中断请求,到安全地地方去中断挂起;
	- `安全区域`指在一段代码片段之中,引用关系不会发生变化;在这个区域中的任意地方开始GC都是安全的;
	- 在线程执行到SafeRegion中的代码时,首先标志进入到safeRegion,当jvm发起GC时,不用管标识自己为safeRegion状态的线程了; 在线程要离开safeRegion时,要检查系统是否已经完成了根节点枚举(或者Full GC),如果完成了,线程就继续执行;否则它就必须等待直到收到可以安全离开safeRegion的信号为止;

> 垃圾收集器

![可组合的垃圾收集器](https://img-blog.csdnimg.cn/20191023145050501.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L01ySmFydmlzRG9uZw==,size_16,color_FFFFFF,t_70)

- Serial 收集器
	- 单线程收集器,进行垃圾回收时必须暂停其他的工作线程;
	- 新生代采用复制算法,老年代采用标记整理算法,都是暂停所有用户线程;
	- Serial / Serial Old 收集器

- ParNew 收集器
	- serial的多线程的版本;
	- 只能它能与CMS收集器(收集老年代,新生代只能选择PaNew或者Serial中一个)配合工作;

```

	并行(Parallel): 多条垃圾收集线程并行工作,此时用户线程仍然处于等待状态;

	并发(Coucurrent): 用户线程与垃圾收集线程同时执行(但不一定是并行的,可能会交替执行),用户程序在继续运行,而垃圾收集程序运行在另一个cpu上;

```

- Parallel Scavenge 收集器
	- 是一个新生代收集器,复制算法的收集器,并行多线程收集器;
	- 吞吐量优先; 关注点不同;CMS关注点是尽可能的缩短垃圾收集时用户线程的停顿时间;Parallel Scavenge收集器的目标则是达到一个可控制的吞吐量;
		- 吞吐量(Throughput): cpu用于运行用户代码的时间与cpu总消耗时间得比值; </br>吞吐量 = 运行用户代码时间 / (运行用户代码时间 +垃圾收集时间)
		- 高吞吐量可高效率利用cpu时间,尽快完成运算任务,主要适合在后台运行而不需要太多交互的任务; 停顿时间越短越适合需要与用户交互的程序,提高用户体验;

- Serial Old 收集器
	- Serial收集器的老年代版本,单线程收集器;
	- jdk1.5前可与Parallel Scavenge收集器搭配使用;可作为CMS收集器的后背预案,并发收集发生ConcurrentModeFailure时使用;

- Parallel Old 收集器
	- Parallel Scavenge 收集器的老年代版本;多线程并行收集器;
	
- CMS 收集器
	- 多线程并发收集器
	- Concurrent Mark Sweep 获取最短回收停顿时间为目标的收集器;
	- 使用标记-清除算法,上述收集器大多是标记-整理算法;

- G1收集器
	- 并行与并发;
	- 分代收集;
	- 空间整合;(标记-整理 + 复制)
	- 可预测的停顿,垃圾收集上的时间不得超过N毫秒;
		- java堆的内存布局与其他收集器有很大区别,G1将整个java堆划分为多个大小相等的独立区域(region)
		- Remembered Set 限定堆GC根节点枚举范围,可以不对全堆扫描;

>内存分配与回收策略 

- java的自动内存管理归结为: `给对象分配内存`以及`回收分配给对象的内存`;
- 给对象分配内存,大方向上说,就是在堆上分配(也可能经过JIT编译后被拆散为标量类型并间接的栈上分配),对象主要分配在新生代的Eden区,如果启动了本地线程分配缓冲,将按线程优先在TLAB(Thread Local Allocation Buffer)上分配;少数情况也可能会直接分配在老年代中,分配的规则并不是一定是固定的,细节取决于当前使用的是哪一种垃圾收集器组合,还有jvm中与内存有关的参数设置;
- 对象优先在Eden区中分配,当Eden区没有足够空间进行分配时,虚拟机将发起一次Minor GC;
	- 新生代GC (Minor Gc): 指发生在新生代的垃圾收集动作,因新生代java对象大多都是朝生夕死的特性,所以Minor GC非常频繁,回收速度也快;
	- 老年代GC (Major GC/Full GC): 发生在老年代的GC,Major GC的速度一般会比Minor GC慢10倍以上;(通常 Major GC 会随后发生Minor GC,与收集器的实现有关)

- 大对象直接进入老年代
	- 需要大量连续内存空间的java对象,如很长的字符串以及数组;
	- jvm参数可设置大于某个阈值直接在老年代分配,避免在新生代Eden区和两个Survivor区之间发生大量的内存复制;

- 长期存活的对象将进入老年代
	- jvm给每个对象定义了一个对象年龄(Age)计数器;
	- 如果对象在Eden出生并经过第一次的Minor GC后仍然存活,并且能被Survivor容纳的话,将被移动到Survivor空间中,并且对象年龄设为1;对象在Survivor每熬过Minor GC,年龄就增加一岁;
	- 年龄默认阈值为15岁,对象就会被晋升到老年代中;

- Survivor空间中相同年龄所有对象大小的总和大于Survivor空间的一半,年龄大于等于该年龄的对象就可以直接进入老年代;

- 空间分配担保
	- 在发生minor GC之前,jvm会检查老年代最大可用的连续空间是否大于新生代所有对象总空间;
		- 如果大于,minor GC可以确保是安全地;
		- 如果不成立,检查HandlerPromotionFailure设置是否允许担保失败;
			- 允许,继续检查老年代可用的连续空间是否大于历次晋升到老年代对象的平均大小;如果大于,则尝试一次minor GC,尽管这次minor GC是有风险的;
			- 如果小于或者设置不允许冒险,这时进行一次Major GC;
			- 允许担保失败的冒险: 
				- 新生代使用复制算法,为了内存利用率,只用其中一个Survivor空间作为轮换备份,在minorGC后仍然存活的情况下,需要老年代进行分配担保;但是有多少对象会活下来在实际完成内存回收之前是无法明确知道的,只好取之前每一次回收晋升到老年代对象容量的平均大小值作为经验值,与老年代的剩余空间进行比较,决定是否进行fullGC让老年代腾出更多空间;  如果出现HandlerPromotioinFailure 失败,那就只好在失败后重新发起一次FullGc,虽然担保失败绕的圈子是最大的,大部分还是会打开开关,避免fullGc过于频繁;
			- jdk 6后,规则变为只要老年代的连续空间大于新生代对象总大小或者历次晋升的平均大小就会进行minorGC,否则进行FullGC;

-------

## 类文件结构

语言无关性: Jvm + 字节码存储格式;

![语言无关性](https://img-blog.csdnimg.cn/20191028112857840.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L01ySmFydmlzRG9uZw==,size_16,color_FFFFFF,t_70)

> class 类文件结构

![class文件格式](https://img-blog.csdnimg.cn/20191028162438531.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L01ySmFydmlzRG9uZw==,size_16,color_FFFFFF,t_70)

- 概述 
	- 任何一个class文件都对应着唯一一个类或接口的定义信息; class文件是一组以8位字节为基础单位的二进制流;

	- 根据jvm规范,class文件格式采用一种类似于C语言结构体的伪结构来存储数据,各个项目严格按照顺序紧凑地排列在Class文件之中,中间没有添加任何分隔符,只有两种数据类型: `无符号数`和`表`;
	
		- 无符号数: 属于基本的数据类型,以u1,u2,u4,u8来代表1个字节,2个字节,4个字节,8个字节的无符号数,可用来描述数字,索引引用,数量值,或者按照UTF-8编码构成字符串值;
		- 表: 多个无符号数或者其他表作为数据项构成的复合数据类型,所有表都习惯性的以`_info`结尾;用于描述有层次关系的复合结构的数据,整个class文件本质上就是一张表;

- 魔数 `Magic Number` 身份识别
	- 每个Class文件的头4个字节称为魔数,作用是确定这个文件是否为一个能被虚拟机接受的class文件; class文件魔数为: oxCAFEBABY (流弊大气!)
	- 第5,6字节代表次版本号 `minor version`;
	- 第7,8字节代表主版本号 `major version`;
	- 主版本号之后的是常量池入口;
- 常量池 `constant_pool_count` | `constant_pool`
	- Class文件中的资源仓库,每一项常量都是一个表,
	- 由于常量池中常量数量不固定,在常量池的入口需要放置一项u2类型的数据,表示有多少常量,索引从1开始;
	- 常量池主要存放两大类:
		- 字面量 Literal
			- 文本字符串,声明为final的常量值;
		- 符号引用 Symbolic References
			- 类和接口的全限定名 Fully Qualified Name;
			- 字段的名称和描述符 Descriptor;
			- 方法的名称和描述符;
	- 常量池中每一个常量(表)开始的第一位是一个u1类型的标志位,代表当前这个常量属于哪种常量类型;
		- 如,`Constant_Utf8_info` 代表Utf-8编码的字符串,`CONSTANT_Integer_info` 代表整形字面量,`CONSTANT_Methodref_info` 代表类中方法的符号引用;`CONSTANT_Class_info` 类或接口的符号引用;

	- javap 输出常量表 (-verbose)
		- 自动生成常量,会用于后面的字段表(`field_info`),方法表(`method_info`),属性表(`attribute_info`)引用到,用来描述一些不方便使用"固定字段"进行表述的内容;
			- 因java的类是无穷无尽的,无法通过简单的无符号字节来描述一个方法用到了什么类,描述方法的这些信息时,需要引用常量表中的符号引用进行表述;

![常量池](https://img-blog.csdnimg.cn/20191031182937537.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L01ySmFydmlzRG9uZw==,size_16,color_FFFFFF,t_70)

- 访问标志 `access_flags`

	- 常量池结束后,后面两个字节代表访问标志(access_flags),用于识别一些类或者接口层次的访问信息;
	- 如: 这个class是类还是接口;是否定义为public类型;是否定义为Abstract类型;如果是类,是否被声明为final等;
	- access_flags 一共有16个标志位可以使用,当前只定义了其中8个,没有使用到的标志位一律为0;

![访问标志](https://img-blog.csdnimg.cn/20191028175823238.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L01ySmFydmlzRG9uZw==,size_16,color_FFFFFF,t_70)

- 类索引,父类索引,接口索引集合; `this_class,super_class,interface_class,interfaces`

	- `this_class`,`super_class` 都是u2类型的数据,`interfaces`是一组u2类型的数据的集合,class文件中由这三项确定这个累的继承关系;
	- 类索引用于确定这个类的全限定名,父索引用于确定这个类的父类的全限定名(因此,java并不支持多继承),除java.lang.Object外,所有的java 的父类索引都不为0;各自指向一个类型为`CONSTANT_Class_info`的类描述符常量,在通过`CONSTANT_Class_info`类型的常量中的索引值找到定义在`CONSTANT_Utf8_info`类型的常量中的全限定名字符串;
	- 接口索引集合用来描述这个类实现了哪些接口,都是按照顺序排列在访问标志之后;

- 字段表集合 `field_info`
	- 字段表包含u2类型的`access_flags`,u2类型`name_index`,u2类型`descriptor_index`,u2类型的`attributes_count`, 表类型的`attribute_info`;
		- access_flags 用于获取字段访问标志,表示字段的修饰符或者是什么类型;
		- `name_index , descriptor_index` 用于对常量池的引用,分别代表字段的简单名称以及字段和方法的描述符;
	- 用于描述接口或类中声明的变量,字段(field)包括类级变量和实例级变量,不包括在方法内部声明的局部变量;
	- `全限定名` : `org/xxx/xxx/TestClass;`;
	- `简单名称` : 指没有类型和参数修饰的方法或者字段名称;
	- 常量池记录着描述符,方法和字段的`描述符`作用: 描述字段的数据类型,方法的参数列表(数量,类型以及顺序)和返回值; 
		- 基本数据类型和代表无返回值的void类型都用一个大写字符来表示,而对象则用字符L加对象的全限定名来表示;
		- 对于数组类型的描述符,每一维度使用一个前置的`[`字符描述
			- 如二维字符串数组 -> `[[Ljava/lang/String;`一维Int数组 -> `[I`;
		- 用描述符来描述方法时,先参数列表,后返回值的顺序描述;参数列表按照参数的严格顺序放在一组小括号中;
			- 如tostring 方法 -> `()Ljava/lang/String;`
			- int indexOf(char[]source,int sourceOffset,int sourceCount,char[] target,int targetOffset,int targetCount,int fromIndex) -> `([CII[CIII)I`
	- 字段表中之后跟随一个属性表集合用于存储一些额外的信息,字段都可以在属性表中描述零至多项的额外信息; 可以描述字段的默认值;
	- 字段表集合不会列出从超类或者父接口中继承的字段,但可能列出原java代码中不存在的字段,如在内部类中为了保持对外部类的引用,自动添加指向外部类实例的字段(联想反射内部类时,第一参数默认是外部实例)
	- 在字节码中,如果字段的描述符不一致,字段重名就是合法的;

![字段访问标志](https://img-blog.csdnimg.cn/20191028182003121.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L01ySmFydmlzRG9uZw==,size_16,color_FFFFFF,t_70)

![描述符](https://img-blog.csdnimg.cn/20191028183134384.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L01ySmFydmlzRG9uZw==,size_16,color_FFFFFF,t_70)

- 方法表集合 `method_info`
	- 方法表集合 包括u2类型 `access_flags`,u2 `name_index`,u2 `descriptor_index`, u2 `attributes_count`, attribute_info 表类型 `attributes`;
	- 方法中的代码经过编译器编译成字节码指令后,存放在方法属性表集合中一个名为`Code`的属性里,属性表作为class文件格式中最具扩展性的一种数据项目;
	- 父类方法在子类中没有被Override,方法表集合中就不会出现来自父类的方法信息;但有可能出现编译器自动添加的方法,如 类构造器 `<clinit>`,和实例构造器`<init>`方法;
	- 在字节码中,方法如果返回值不同也是可以重载的;

![方法访问标志](https://img-blog.csdnimg.cn/20191030172252215.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L01ySmFydmlzRG9uZw==,size_16,color_FFFFFF,t_70)

- 属性表集合 `attribute_info`
	- 每个属性的名称需要从常量池中引用一个`CONSTANT_Utf8_info`类型的常量来表示;属性表集合的结构完全是自定义的,只需要通过一个u4的长度属性去说明属性值所占用的位数;
	- Code属性  [重要] java代码编译成的字节码指令
		- java程序方法中的代码经过javac编译器处理后,变成字节码指令存储在Code属性内;但接口和抽象类中的方法不存在code属性;
		- `max_stack` 代表操作数栈(Operand Stacks)深度的最大值,在方法执行的任意时刻,操作数栈都不会超过这个深度,jvm 运行的时候需要根据这个值来分配栈帧(Stack Frame)中的操作栈深度;
		- `max_locals` 代表局部变量表所需的存储空间;单位是`Slot`,Slot是jvm为局部变量分配内存所使用的最小单位;其中byte,char,float,int,short,boolean,returnAddres等长度不超过32位的数据类型占用一个Slot,double,long64位的数据类型需要两个Slot存放;局部变量所占的Slot可以被重用;
		- `code`和`code_length` 用来存储java源程序编译后生成的字节码指令;  分别代表存储字节码指令的一系列字节流和字节码长度; `code_length` 因jvm限制一个方法不允许超过65536条字节码指令,理论上u4类型的长度值,实际上只有u2的长度; 
			- 实例方法的局部变量表中至少会存在一个指向当前对象实例的局部变量,局部变量表中也会预留出第一个Slot为来存放对象实例的引用;
		- `exception_info` 异常表
	- Exceptions 属性 方法抛出的异常
		- 列举出方法中可能抛出的受查异常(Checked Exceptions),也就是方法描述时在`throws` 关键字后面列举的异常;
	- LineNumberTable 属性  Java源码的行号与字节码行号(字节码的偏移量)之间的对应关系;
	- LocalVariableTable 属性 栈帧中局部变量表中的变量与java源码中定义的变量之间的关系;
	- SourceFile 属性 记录生成这个Class文件的源码文件名称;
	- ConstantValue 属性 通知jvm 自动为静态变量赋值;只有static修饰的变量可以使用这项属性;
		- int x = 123; 实例变量的赋值是在实例构造器`<init>`方法中进行的;
		- static int x = 123; 对于类变量有两种方式可以选择;sun javac选择是: 如果同时使用final和static修饰一个变量且此变量的数据类型是基本类型或者java.lang.String,就使用ConstantValue属性来初始化;  否则使用`<clinit>`方法中进行初始化;
			- 使用类构造器`<clinit>`方法
			- 使用ConstantValue属性,字面量;
	- InnerClasses 属性 记录内部类和宿主类之间的关联
		- 如果一个类定义了内部类,编译器会为它及它的内部类生成InnerClasses; 
	- Deprecated 及 Synthetic属性 属于标志类型的布尔属性 
		- Deprecated : 用于表示某个类,字段或者方法,定为不再推荐使用;
		- Synthetic : 代表字段或者方法并不是由java源码直接产生的,而是由编译器自行添加的;
	- StackMapTable 属性 jvm类加载的字节码验证阶段被新类型检查验证器(Type Checker)使用,代替以前比较消耗性能的基于数据流分析的类型推导验证器;
	- Signature 属性 任何类,接口,初始化方法或成员的泛型签名如果包含了类型变量(Type Variables)或参数化类型(Parameterized Types),则Signature属性会为他记录泛型签名信息; 
		- java 使用的泛型是擦除法实现的伪泛型,在字节码(Code属性)中,泛型信息编译(类型变量,参数化类型)之后都统统被擦除掉; 
		- 好处是实现简单,节省内存; 坏处是无法将泛型类型和用户定义的普通类型同等对待,运行期间做反射时无法获得到泛型信息;Signature就是弥补此缺陷;
	- BootstrapMethods 属性 用于保存invokedynamic	指令引用的引导方法限定符;

![Code属性表的结构](https://img-blog.csdnimg.cn/20191030181611660.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L01ySmFydmlzRG9uZw==,size_16,color_FFFFFF,t_70)

![异常表运作](https://img-blog.csdnimg.cn/20191031100159558.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L01ySmFydmlzRG9uZw==,size_16,color_FFFFFF,t_70)


> 指令码指令简介

- jvm 的指令由一个字节长度的,代表着某种特定操作含义的数字(称为操作码Opcode)以及跟随其后的零至多个代表此操作所需参数(称为操作数Operands)构成;

- 字节码与数据类型
	- 大多数的指令都包含了其操作所对应的数据类型信息;
		- iload指令用于从局部变量表中加载int型的数据到操作数栈中;fload指令加载的则是float类型,l-long,s-short,b-byte,c-char,f-float,d-double,a-reference;

- 加载和存储指令
	- 加载和存储指令用于将栈帧中的局部变量表和操作数栈之间来回传输;
		- 将一个局部变量加载到操作栈: `Tload`,`Tload_<n>` T表示i,l,f,d,a; n->slot
		- 将一个数值从操作数栈存储到局部变量表: `Tstore`,`Tstore_<n>` T表示i,l,f,d,a;
		- 将一个常量加载到操作数栈 : `bipush,sipush,ldc,ldc_w,ldc2_w,aconst_null,iconst_m1,iconst_<i>,lconst_<l>,fconst_<f>,dconst_<d>`
		- 扩充局部变量表的访问索引的指令: wide;

- 运算指令
	- 用于对两个操作数栈上的值进行某种特定运算,并把结果重新存入到操作栈顶;
		- 加法: Tadd
		- 减法: Tsub
		- 乘法: Tmul
		- 除法: Tdiv
		- 取余: Trem
		- 取反: Tneg
		- 位移: Tshl,Tshr,Tushr
		- 按位或: Tor
		- 按位与: Tand
		- 按位异或: Txor
		- 局部变量自增: Tinc
		- 比较:Tcmpg

- 类型转换指令
	- 可以将两种不同的数值类型进行相互转换,jvm支持以下数值类型的宽化类型转换(小范围向大范围类型的安全转换)
		- int类型到long,float或者double类型;
		- long类型到float,double类型;
		- float类型到double类型;
	- 处理窄化类型转换,显示试用转换指令完成 T2T
		- i2b,i2c,i2s..
	- 将浮点型转换为整数类型(int,long)
		- 如果浮点数是NaN,转换结果为int或long类型的0;
		- 如果浮点型不是无穷大,试用IEEE 754向0舍入模式取整,获得整数v,如果v在目标类型T的表示范围内,则为V;
		- 否则根据v的符号,转换为T所能表示的最大或者最小正数;
	- 数值类型的窄化类型不会导致jvm抛出运行时异常

- 对象创建和访问指令
	- jvm对类实例和数组的创建操作使用不同的字节码指令
		- 创建类实例的指令 new
		- 创建数组的指令: newarray,anewarray,multianewarray
		- 访问类字段(static)和实例字段的指令: getfield,putfield,getstatic,putstatic;
		- 将一个数组元素加载到操作数栈的指令: Taload;
		- 将一个操作数栈的值存储到数组 Tastore
		- 取数组长度指令 arraylength
		- 检查类实例类型指令 instanceof,checkcast;

- 操作数栈管理指令
	- 直接操作操作数栈指令
		- 将操作数栈的栈顶一个或两个元素出栈: pop,pop2;
		- 复制栈顶一个或两个数值并将复制值或双份的复制值重新压入栈顶: `dup,dup2,dup_x1,dup2_x1,dup2_x2`;
		- 将栈最顶端的两个数值互换 : swap;
		
- 控制转移指令
	- 让jvm从指定的位置指令而不是控制转移指令的下一条指令继续执行程序;
		- 条件分支: `ifeq,iflt,ifle,ifne,ifgt,ifnull,ifnonnull,if_icmpeq,if_icmpne,if_icmplt,if_icmpgt,if_cmple,if_icmpge,if_acmpeq,if_acmpne`;
		- 复合条件分支: tableswitch,lookupswitch
		- 无条件分支: `goto,goto_w,jsr,jsr_w,ret`;
	- boolean,byte,char,short 都是使用int类型的指令完成,对于long,float,double先执行对应类型的指令,再返回整型值到操作数栈,再执行int指令;各种类型的比较都会转化为int类型的比较操作;

- 方法调用和返回指令
	- 方法调用
		- invokevirtual 调用对象的实例方法,根据对象的实际类型进行分派(虚方法分派);
		- invokeinterface 调用接口的方法;
		- invokespecial 调用一些需要特殊处理的实例方法,包括实例初始化方法,私有方法和父类方法;
		- invokestatic 调用类方法;
		- invokedynamic 用于在运行时动态解析出调用点限定符所引用的方法;
	- 返回指令 Treturn

- 异常处理指令
	- 显示抛出异常的操作 (throw) 由`athrow`指令实现,在jvm中,处理异常由异常表完成;

- 同步指令
	- 管程(Monitor) 支持同步;
	- 方法级的同步实现在方法调用和返回操作中,jvm可从方法常量池方法表结构中的同步访问标志得知一个方法是否是同步方法;  
		- 方法调用时,执行线程先成功持有管程,然后才能执行方法,方法完成时释放管程;
	- 同步一段指令集序列通常是由java语言中的synchronized块表示;jvm指令集中有 monitorenter和monitorexit支持synchronized关键字的语义;

![同步字节码指令](https://img-blog.csdnimg.cn/20191031151939748.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L01ySmFydmlzRG9uZw==,size_16,color_FFFFFF,t_70)

-----

## Jvm类加载机制

代码编译的结果是从本地机器码转变为字节码,jvm把描述类的数据从Class文件(二进制字节流)加载到内存,并对数据进行校验,解析和初始化,最终可以被jvm直接使用的java类型; 

>类加载的时机

![类的生命周期](https://img-blog.csdnimg.cn/20191031160441427.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L01ySmFydmlzRG9uZw==,size_16,color_FFFFFF,t_70)

加载(Loading) -> [验证(Verification) -> 准备(Preparation) -> 解析(Resolution)] -> 初始化 (Initialization) -> 使用(Using) -> 卸载(Unloading)

- 加载,验证,准备,初始化,卸载的顺序确定的;解析阶段不一定,为了支持java的运行时绑定(动态绑定,晚期绑定); 这些阶段通常都是相互交叉混合式的进行的;
- 有且只有以下情况,没有类初始化需要进行类的初始化,简称对一个`类的主动引用`:
	- 遇到 new,getstatic,putstatic或invokestatic 字节码指令;分别对应 实例化对象,读取设置一个类的静态字段(被final修饰,已在编译器吧结果放入常量池的静态字段除外,因为使用的是ConstantValue初始化而不是<clinit>方法),以及调用类的静态方法;
	- 使用java.lang.reflect 包的方法对类进行反射调用的时候
	- 当初始化一个类时,发现其父类还没有进行过初始化,需要先触发器父类的初始化;
	- 当jvm启动时,用户需要指定一个要执行的主类(包含main方法的类),jvm会先初始化这个主类;
	- 使用jdk 1.7动态语言支持时,一个java.lang.invoke.MethodHandle实例最后的解析结果`REF_getStatic,REF_putStatic,REF_invokeStatic`的方法句柄;

- 被动引用不会导致类初始化;
	- 子类引用父类的静态字段,不会导致子类初始化;
	- 通过数组定义来引用类,不会触发此类的初始化;
		- 不过jvm会生成一个直接继承于java.lang.Object的子类,创建动作由字节码指令newarray触发(可否理解为可加载解析,不会初始化);
	-  常量(final , ConstantValue属性)在编译阶段会存入调用类的常量池中,本质上并没有直接引用到定义常量的类,不会触发定义常量类的初始化;

- 接口的加载过程与类加载过程稍有不同
	- 接口在初始化时,并不要求父接口全部都完成了初始化,只有在真正使用到父接口(引用接口中定义的常量)采用初始化;

>类加载的过程

- 加载
	- 通过一个类的全限定名获取定义此类的二进制字节流;
	- 将这个字节流所代表的静态存储结构转化为方法区的运行时数据结构;
	- 在内存中生成一个代表这个类的java.lang.Class对象,作为方法区这个类的各种数据的访问入口;

加载阶段完成后,jvm外部的二进制字节流就按照jvm所需格式存储在方法区之中,然后在内存中(hotspot->方法区)实例化一个Class类对象,作为程序访问方法区中的这些类型数据的外部接口;

```

	- 实际上,jvm规范的这3条是非常灵活的
	
	非数组类的加载过程(加载阶段获取类的二进制字节流的动作)是开发人员可控性最强的;
	因为加载阶段既可以使用系统提供的引导类加载器完成,也可以使用用户自定义的类加载器去完成,开发人员可以通过定义自己的类加载器去控制字节流的获取方法(重写一个类加载器的loadClass方法)

	数组类而言,数组类本身不通过类加载器创建,由jvm直接创建的;但是数组类的元素类型(ElementType 数组去掉所有维度的类型)最终是要靠类加载器去创建;数组类的创建: 
		- 如果数组的组件类型(Component Type 数组去掉一个维度的类型)是引用类型,递归加载组件类型;数组C在加载改组件类型的类加载器的类名称空间上被标志;
		(一个类必须与类加载器一起确定唯一性)
		- 如果数组的组件类型不是引用类型(int[]数组),jvm会把数组C标记为与引导类加载器关联;
		- 数组类的可见性与它的组件类型的可见性一致,组件类型不是引用类型,数组类的可见性默认为public;
```

- 验证   链接阶段的第一步,确保Class文件的字节流中包含的信息符合jvm的要求;

	- 文件格式验证
		- 验证`字节流`是否符合Class文件格式的规范,并且能被jvm处理;通过此阶段后,字节流才会进入内存的方法区中进行存储;
	- 元数据验证
		- 对字节码描述信息进行语义分析,符合java语言规范;
	- 字节码验证
		- 确定程序语义是合法的,符合逻辑的,jdk1.6后Code属性添加`StackMapTable`节省时间;
	- 符号引用验证
		- 验证jvm将符号引用转换为直接引用的时候,动作在解析阶段中发生;对类自身以外(常量池中的各种符号引用)的信息进行匹配性校验;
		- 无法通过,会抛出`IncompatibleClassChangeError`异常的子类,如IllegalAccessError,NoSuchFieldError,NoSuchMethodError;

- 准备
	- 正式为类变量分配内存并设置类变量初始值的阶段,这些变量所使用的内存都将在方法区中进行分配;
		- 内存分配的仅包括类变量,不包括实例变量;
		- 通常情况下赋初值指的是java的默认初始值,但是类字段的字段属性表中存在ConstantValue(final标记)属性,准备阶段的变量会被初始化ConstantValue属性所指定的值;

```

	public static int value = 123;

	准备阶段初始值为0,而不是123,赋值123是putstatic字节码指令被编译后,存放在类构造器<clinit>方法之中,所以赋值123是在初始化阶段才会执行;

	public static final int value =123;

	准备阶段初始值即为123,因为final修饰的字段,字段表中存在ConstantValue属性,在编译时javac将会为value生成ConstantValue属性,在准备阶段jvm根据ConstantValue的值将value赋值为123;
```

- 解析
	- jvm将常量池内的符号引用替换为直接引用的过程;
	- 引用区分:
		- 符号引用 (Symbolic References): 以一组符号来描述所引用的目标,可以是任何形式的字面量,与jvm实现的内存布局无关;
		- 直接引用 (Direct References): 直接引用可以是直接指向目标的指针,相对偏移量或者一个能间接定位到目标的句柄; 如果有直接引用,则引用目标必定在内存中存在;
	- jvm规范中并未规定解析发生的具体时间,要求在执行`anewarray,checkcast,getfield,getstatic,instanceof,invokedynamic,invokeinterface,invokespecial,invokestatic,invokevirtual,ldc,ldc_w,multianewarray,new,putfield,putstatic`16个用于操作符号引用的字节码指令之前,先对他们所使用的符号引用进行解析;
	- invokedynamic 指令必须等到程序实际运行到这条指令的时候,解析动作才能进行,且不缓存;其余符号指令可以在完成加载阶段,还没开始执行就进行解析,也可对第一次解析结果进行缓存,避免解析重复进行;
	- 解析动作主要针对接口,字段,类方法,接口方法,方法类型,方法句柄和调用点限定符7种符号引用,对应于常量池的`CONSTANT_Class_info,CONSTANT_Fieldref_info,CONSTANT_Methodref_info,CONSTANT_InterfaceMethodref_info,CONSTANT_MethodType_info,CONSTANT_MethodHandle_info,CONSTANT_invokeDynamic_info`7中常量类型;
		- 类或接口的解析 (D:当前代码所处的类; N:从未解析过的符号引用;C:类或接口的直接引用;)
			- 如果不是一个数组类型,jvm将N的全限定名传递给D的类加载器加载这个类C;
			- 是一个数组类型, N的描述符类型添加[,按照1加载数组元素类型;
		- 字段解析 (C: 字段所属的类或接口) 先解析字段表;
			- C本身包含简单名称和字段描述符都匹配,返回字段直接引用,查找结束;
			- 如果C中实现了接口,按照继承关系从下往上递归搜索各个接口和它的父接口,接口中包含简单名称和字段描述符都匹配的字段,返回字段直接引用,查找结束
			- 否则,C不是`java.lang.Object`,按照继承关系从下往上递归搜索其父类,找到简单名称和描述符都匹配的字段,返回字段直接引用,查找结束;
			- NoSuchFieldError
		- 类方法解析 (C: 类) 	先解析类方法表;
			- 类方法和接口方法符号引用的常量类型是分开定义的,发现定义不同抛出IncompatibleClassChangeError异常;
			- 在C中查找简单名称和描述符都匹配的方法,如果有返回直接引用,查找结束;
			- 在C的父类中递归查找...
			- 在C实现的接口列表及父接口中递归查找匹配的方法,存在说明C为抽象类,抛出`AbstractMethodError`异常(接口中存在static方法,不支持);
			- NoSuchMethodError
		- 接口方法解析 (C:类)  接口方法表;                                                  
			= 与类方法解析1相同;
			- 在接口C中查找是否有简单名称和描述符都匹配的方法,如果有返回直接引用,查找结束;
			- 在接口C的父接口递归查找,直到`java.lang.Object`类为止,有则返回,结束查找;
			- NoSuchMethodError

- 初始化

类加载的最后一步,初始化阶段是执行类构造器`<clinit>`方法的过程;
	
- `<clinit> 方法特点`
	- `<clinit>方法` 是由编译器自动收集类中的所有类变量的赋值动作和静态语句块(static{}块)中的语句合并生成的,收集顺序是由语句在源文件中出现的顺序决定;
		- 静态语句块中只能访问到定义在静态语句块之前的变量,定义在它之后的变量,在前面的静态语句块中可以赋值,但是不能访问;
	- `<clinit>()方法`与类的构造函数(实例构造器`<init>()`方法) 不同,不需要显示的调用父类构造器;jvm会保证在子类的`<clinit>()`方法执行之前,父类的`<clinit>()`方法已经执行完毕;
		- 因此 jvm中第一个被执行的`<clinit>()`方法的类肯定是`java.lang.Object`;
		- 由于父类的`<clinit>()`方法先执行,父类中定义的静态语句块要优先于子类的变量赋值操作; 
	- `<clinit>()`方法对于类或接口不是必须的;
	- 接口中不能使用静态语句块,但仍然有变量初始化的赋值操作,接口和类都会生成`<clinit>方法`; 其中,执行接口的`<clinit>`方法不需要先执行父接口的`<clinit>`方法,只有到父接口中定义的变量使用时,父接口才会初始化;
	- jvm保证一个类的`<clinit>()`方法在多线程环境中被正确的加锁,同步,多个线程同时去初始化一个类,只有一个线程去执行这个类的`<clinit>`方法,其他线程阻塞等待,当线程退出`<clinit>`方法后,其他线程唤醒后也不会再次进入到`<clinit>`方法,因为同一个类加载器下,一个类型只会初始化一次;

> 类加载器

加载过程中 `通过一个类的全限定名获取类的类的二进制字节流` 可以让应用程序决定如何去获取所需的类 ,实现这个动作的代码模块为类加载器;

- 类名称空间: 对于任意一个类,都需要由加载它的类加载器和这个类本身一同确立其在jvm中的唯一性; 每一个类加载器都拥有一个独立的`类名称空间`;
	- 即两个类相等,只有两个类由同一个类加载器加载的前提下才有意义;
	- 相等指的是代表类Class对象的equals,isAssignableFrom,isInstance,instanceOf等方法;
- 双亲委派模式 Parents Delegation Model
	- 在jvm的角度说,只存在两种类加载器 
		- 启动类(引导类)加载器(Bootstrap ClassLoader);
		- 其他类加载器(继承于java.lang.ClassLoader)
	- java开发人员的角度,分为3中
		- 启动类(引导类)加载器(Bootstrap ClassLoader) 
			- C++实现,负责加载 `<JAVA_HOME>\lib`目录 或者被`-Xbootclasspath`参数所指定路径,并且是jvm识别的类库(仅按照文件名识别,如rt.jar)加载到jvm内存中;
			- 不可被java直接引用,用户在编写自定义类加载器,需要把加载请求委派给引导类加载器,直接使用null即可;
		- 扩展类加载器 (Extension ClassLoader)
			- `sun.misc.Launcher$ExtClassLoader`实现,负责加载`<JAVA_HOME>\lib\ext`目录中的,或者被java.ext.dirs系统变量指定的路径中的所有类库;
			- 可被直接使用;
		- 应用程序类(系统类)加载器 (Application ClassLoader)
			- `sun.misc.Launcher$AppClassLoader`实现,负责加载用户类路径(ClassPath)上所指定的类库;
			- 可直接使用,如果没有自定义类加载器,一般就是程序默认类加载器;
		- BootstrapClassLoader  <- ExtensionClassLoader <- ApplicationClassLoader <- CustomClassLoader  组合关系;
		- 如果一个类加载器收到类加载的请求,先委派给父类加载器完成,只有到父类加载器无法完成加载请求(搜索范围中没有找到所需的类),子加载器尝试自己加载;

- 破坏双亲委派模型

双亲委派模型并不是一个强制性的约束模型,双亲委派的破坏:

- 第一次破坏: jdk 1.2之后添加 findClass方法;自定义类加载器逻辑写入findClass中,在loadClass方法的逻辑如果父类加载失败,则会调用自己的findClass方法完成加载,保证新写出来的类加载器是符合双亲委派规则的;
- 第二次破坏: 模型自身的缺陷,双亲委派虽然能很好的解决各个加载器基础类的统一问题(越基础的类由越上层的加载器加载);但是问题是基础类调回用户的代码时, 比如JNDI由启动类加载器加载(rt.jar),Jndi调用某些需要由独立厂商实现并部署在应用程序的代码,启动类加载器加载的不能认识这些代码(还有JDBC等,因为是不同的类加载器加载,低层的可以认识顶层的,但顶层的不认识低层的);
	- 使用线程上下文类加载器兼容(Thread Context ClassLoader),通过Thread类setContextClassLoader方法设置,如果创建线程时还未设置,将会从父线程中继承一个,如果在应用程序的全局范围内都没有设置过,默认类加载器就是应用程序类加载器;
	- JNDI服务使用线程上下文类加载器加载所需要的SPI代码(jndi接口提供者),也就是父类加载器请求子类加载器完成类加载的动作(我的理解是原本是启动类加载器加载jndi,spi由应用类加载器加载,导致不能访问;现在是设置上下文类加载器,jndi也是使用应用类加载器加载,spi由应用类加载器加载,同一个类加载器加载,可以访问;)
- 第三次破坏: 用户对程序动态性的追求导致;如hotswap,热部署等;
	- OSGi java模块化标准,关键在于自定义的类加载器机制; 每一个程序模块(OSGi成为Bundle)都有一个自己的类加载器,需要更换一个Bundle时,把Bundle连同类加载器一起换掉实现代码的热部署;
	- OSGi为网状结构,不同于双亲的树状结构;

-----

### jvm字节码执行引擎

> 运行时栈帧结构

- 栈帧 Stack Frame
	- 用于支持jvm进行方法调用和方法执行的数据结构,是jvm运行时数据区中的jvm栈的栈元素; 存储方法的局部变量表,操作数栈,动态连接,方法返回地址和一些额外的附加信息等信息;
	- 每一个方法从调用开始至执行完成的过程,都对应着一个栈帧在jvm栈里面从入栈到出栈的过程;
	- 在编译程序代码时,栈帧中需要多大的局部变量表,多深的操作数栈都已经完全确定了,并且写入到方法表的Code属性中,一个栈帧需要分配多少内存,仅仅取决于具体的jvm实现;
	- 在活动线程中,位于栈顶的栈帧才是有效的,称为当前栈帧(CurrentStackFrame),与这个栈帧相关联的方法称为当前方法(CurrentMethod),执行引擎运行的所有字节码指令都只针对当前栈帧进行操作;

![在这里插入图片描述](https://img-blog.csdnimg.cn/20191112142859292.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L01ySmFydmlzRG9uZw==,size_16,color_FFFFFF,t_70)

- 局部变量表 Local Variable Table
	- 一组变量值存储空间,用于存放方法参数和方法内部定义的局部变量,写入Code属性确定最大容量max_locals;
	- 以变量槽(Variable Slot) 为最小单位;每个slot都应该能存放`boolean,char,byte,short,int,float,reference,returnAddress`类型的数据;
		- reference 表示对一个对象实例的引用,直接或间接查找对象在java堆中数据存放的起始地址索引和在方法区中的存储的类型信息;
		- returnAddress 为jsr,jsr_w,ret服务,执行字节码指令的地址,实现异常跳转,现已经被异常表代替;
	- 对于64位的数据类型,jvm以高位对齐的方式分配两个连续的Slot空间; long和double都是64位数据类型(reference可能是32位也可能是64位),不能单独方位其中的某一个slot;
	- jvm 使用索引定位的方式使用局部变量表,索引值的范围从0开始至局部变量表的最大slot数量;
	- 在方法执行时,jvm使用局部变量表完成参数值到参数变量列表的传递过程的,如果执行的是实例方法(非static方法),局部变量表中第0位索引的slot默认是用于传递方法所属对象实例的引用,可以使用this来访问到这个隐含的参数;
	- slot是可以重用的,方法体中定义的变量,作用域并不一定覆盖整个方法体,如果当前字节码pc计数器的值已经超出了某个变量的作用域,那这个变量对应的slot就可以交给其他变量使用; 
		- `不使用的对象手动赋值null` 此处注意局部变量表slot如果在变量所处作用域之后,手动对对象设置null值并不是一个无意义的操作,因为可以去除slot对对象的引用,使对象提前被GC回收,而不是等到其他变量重用slot时在回收; 但是实际开发中并不需要赋值null;

- 操作数栈 Operand Stack
	- 操作栈,后入先出(LIFO)栈,最大的写入Code属性确定最大容量max_stacks;
	- 操作数栈的每一个元素可以是任意的java数据类型,包括long和double;32位数据类型占栈容量为1,64位占有栈容量为2;
	- 当方法刚刚开始执行的时候,操作数栈是空的,执行过程中会有各种字节码指令往操作数栈中出栈/入栈; eg: 整数加法的字节码指令iadd运行时操作数栈最接近栈顶的两个元素已经存入了两个int型的数值,当执行这个指令时,会将两个int值出栈并相加,然后将相加结果入栈;
	- jvm的解释执行引擎称为基于栈的执行引擎,栈就是操作数栈;

![在这里插入图片描述](https://img-blog.csdnimg.cn/20191113150407511.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L01ySmFydmlzRG9uZw==,size_16,color_FFFFFF,t_70)

- 动态链接 Dynamic Linking
	- 每个栈帧都包含一个指向运行时常量池中该栈帧所属方法的引用,持有这个引用是为了支持方法调用过程中的动态连接;
	- Class文件的常量池存在大量符号引用,字节码中的方法调用指令以常量池中指向方法的符号引用作为参数;
		- 静态解析: 这些符号引用一部分会在类加载阶段或者第一次使用的时候转化为直接引用
		- 动态连接: 另外一部分将在每一次运行期间转化为直接引用;

- 方法返回地址 
	- 当一个方法执行后,只有两种方法可以退出这个方法;
		- 执行引擎遇到任意一个方法返回的字节码指令,这时候可能会有返回值传递给上层的方法调用者(调用当前方法的方法称为调用者),是否有返回值和返回值的类型将根据遇到何种方法返回指令来决定,这种退出方法的方式称为正常完成出口(Normal Method Invocation Completion);
		- 在方法执行过程遇到了异常,并且这个异常在方法体内没有得到处理; 无论是jvm内部产生的异常还是代码中使用athorw字节码指令产生的异常,只要在本方法的异常表中没有搜索到匹配的异常处理器,就会导致方法退出,称为异常完成出口(Abrupt Method Invocation Completion);

	- 方法退出的过程实际上就等同于把当前栈帧出栈,因此退出时可能执行的操作是: 恢复上层方法的局部变量表和操作数栈,把返回值(如果有)压入调用者栈帧的操作数栈中,调整pc计数器的值以指向方法调用指令后面的一条指令;

- 附加信息 (实际开发中,一般将动态连接,方法返回地址,其他附加信息统称为栈帧信息;)

> 方法调用

方法调用不等同于方法执行,方法调用阶段的唯一任务是确定被调用方法的版本(即调用哪一个方法),暂时还不涉及方法内部的具体运行过程; java中Class文件存储的是符号引用,而不是具体地址,需要到类加载甚至到运行期间才能确定目标方法的直接引用;

- 解析
	- 在类加载的解析阶段,会将一部分符号引用转化为直接引用,这种解析成立的前提是: 方法在程序真正运行之前就有一个可确定的调用版本,并且这个方法的调用版本在运行期是不可改变的; 也就是说,调用目标在程序代码写好,编译器进行编译时就必须确定下来的这类调用称为解析; 
	- java提供5种方法调用字节码指令:
		- invokestatic: 调用静态方法;
		- invokespecial: 调用实例构造器`<init>`方法,私有方法,父类方法;
		- invokevirtual: 调用所有的虚方法(除final方法);
		- invokeinterface: 调用接口方法,会在运行时在确定一个实现此接口的对象;
		- invokedynamic: 先在运行时动态解析出调用点限定符所引用的方法,然后在执行该方法,此条指令时由用户所设定的引导方法决定的,其他是固化在jvm内部;
		- 非虚方法有5类: 
			- `invokestatic,invokespecial` 指令调用的方法,都可以在解析阶段中确定唯一的调用版本,符合这个条件的有静态方法,私有方法,实例构造器,父类方法4类,在类加载的时候就会把符号引用解析为直接引用,这类方法称为`非虚方法`;
			- 非虚方法还有一类是被final修饰的方法,即使`final方法`是使用invokevirtual指令调用;其他的为虚方法;
		- 解析调用一定是个静态的过程,在编译期间就完全确定,类加载的解析阶段就会把符号引用转为直接引用,而分派可能是静态的也可能是动态的;
- 分派 Dispatch
	- 多态性(重载&重写)
	- 静态分派 [编译阶段编译器的选择过程]
		- 静态类型&实际类型: 
			- `Human man = new Man()`  Human称为变量的静态类型(Static Type),或者叫外观类型(Apparent Type),后面的Man称为变量的实际类型(Actual Type);
			- 静态类型的变化仅仅在使用时发生,变量本身的静态类型不会被改变,并且最终的静态类型是在编译期间可知的; 而实际类型变化的结果在运行期才可确定,编译器在编译时并不知道一个对象的实际类型是什么;
		- 编译器在重载选择方法时,通过参数的静态类型而不是实际类型作为判定依据的;
		- 所有依赖静态类型来定位方法执行版本的分派动作称为`静态分派`,典型应用是`方法重载(Overload)`; 静态分派发生在编译阶段,因此确定静态分派的动作实际上不是jvm执行的;
	- 动态分派  [运行阶段jvm的选择过程]
		- 重写(Override) ,可通过字节码指令invokevirtual执行多态方法,执行的第一步就是在运行期间确认接收者(将要执行方法的所有者)的`实际类型`,把常量池中的类方法符号引用解析到不同的直接引用上,这个过程就是java方法重写的本质;
	- 单分派和多分派
		- 方法的接收者与方法的参数统称为方法宗量;根据分派基于多少种宗量,可划分为单分派和多分派两种;
			- 单分派: 根据一个宗量对目标方法进行选择;
			- 多分派: 根据多于一个宗量对目标方法进行选择;
		- java是一门静态多分派(重载时根据调用者和参数),动态单分派(重写时根据调用者)的语言;
		
- 动态类型语言支持 Dynamically Type Language
	- 动态类型语言 类型检查的主体过程是在运行期间而不是编译期(如js,python,kotlin);而编译期间进行类型检查过程的语言(java,C++)就是静态类型语言;动态语言变量无类型而变量值才有类型;
	- java.lang.invoke包支持动态编程:
		- MethodHandle;
		- MethodType :方法类型,包含方法的返回值(methodType()的第一个参数)和具体参数(methodType()第二个及以后的参数)
		- MethodHandles.lookup() : 在指定类中查找符合给定的方法名称,方法类型,并且符合调用权限的方法句柄;
		- 与反射的区别是反射是重量级的;
	- invokedynamic 字节码指令 为了解决原有4条'invoke*'指令方法分派规则固化在jvm之中的问题,把如何查找目标方法的决定权从jvm中转嫁到具体用户代码中,让用户有更高的自由度;invokedynamic的分派逻辑不是由jvm决定的,而是由程序员决定的;
	- 使用动态语言调用父类的父类的方法: 输出为: i am grandfather;
![在这里插入图片描述](https://img-blog.csdnimg.cn/20191114173902884.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L01ySmFydmlzRG9uZw==,size_16,color_FFFFFF,t_70)

> 基于栈的字节码解释执行引擎

- 解释执行,半独立编译;
- 基于栈的指令集与基于寄存器的指令集
	- java编译器输出的指令流,基本上是一种基于栈的指令集架构(Instruction Set Architecture),指令流中的指令大部分都是零地址指令,依赖操作数栈进行工作;而x86的二地址指令集就是基于寄存器的指令集;
	- 区别 (1+1):
		- 基于栈 `iconst_1  iconst_1  iadd  istore_0`
			- 可移植,代码紧凑,编译器实现简单,但相同功能指令数量更多,更频繁的内存访问,执行速度慢;
		- 基于寄存器  `mov eax, 1  add eax, 1`
			- 性能好,实现简单
	
-------

### 类加载及执行子系统的案例与实战

程序进行操作的主要是`字节码生成`与`类加载器`这两部分的功能;

>tomcat: 正统的类加载器架构

Common类加载器能加载的类都可以被Catalina和Shared使用,双方可以相互隔离;各个WebApp类加载器实例之间相互隔离;Jsp类加载器就是为了被丢弃实现HotSwap功能;

![在这里插入图片描述](https://img-blog.csdnimg.cn/20191114181821675.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L01ySmFydmlzRG9uZw==,size_16,color_FFFFFF,t_70)

> OSGi: 灵活的类加载器架构

Open Service Gateway Initiative 基于java语言的动态模块化规范; 运行时才能确定的网状结构;Eclipse IDE 就是OSGi的应用案例;

>字节码生成技术与动态代理的实现

`javac,javassist,CGLib,ASM ,Proxy.newProxyInstance`;动态代理的优势实现了在原始类和接口还未知的时候,就确定了代理类的代理行为,当代理类与原始类脱离直接联系后,就可以很灵活的重用于不同的应用场景中;

------

### 程序编译与代码优化

- 编译器分类: 
	- 前端编译器: 将*.java转变为 *.class文件的过程; sun的javac;
	- JIT编译器(Just in Time 后端运行期编译器): 把字节码转变为机器码的过程;hotspotVm的c1,c2编译器;
	- AOT编译器 (Ahead of Time 静态提前编译器): 直接把*.java文件编译成本地机器码的过程; GCJ;
- java语法糖:
	- 泛型与类型擦除  参数化类型的应用,也就是说所操作的数据类型被指定为一个参数;这种参数类型可以用在类,解口,方法的创建中,分别被称为泛型类,泛型接口,泛型方法;
		- java中的泛型只在程序源码中存在,在编译后的字节码文件中,就已经替换为原来的原生类型(Raw Type 裸类型),并且在相应的地方插入了强制转型代码; 因此在运行期间的java来说,ArrayList<int>和ArrayList<String>就是同一个类,所以是一个语法糖;java语言中的泛型实现方法称为类型擦除,基于这种方法实现的泛型称为伪泛型;
		- Signature,LocalVariableTypeTable等新属性用于解决伴随泛型而来的参数类型的识别问题;
	- 自动装箱,拆箱,遍历循环;
		- 自动拆箱的陷阱
![在这里插入图片描述](https://img-blog.csdnimg.cn/2019111515110652.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L01ySmFydmlzRG9uZw==,size_16,color_FFFFFF,t_70)
			- Integer 内有提供的数缓存只有-128 ~ 127,超过这个范围重新创建新的空间存储这个数;所以第一个第二个为true,false;
			- `==` 判断两个类型的地址,在不遇到算术运算的情况下不会自动拆箱;所以第三个第四个都是返回true;
			- equals方法不处理数据转型的关系;所以第五个第六个返回true,false;

- 注解处理器
	- 实现的注解处理器需要继承抽象类`javax.annotation.processing.AbstractProcessor` ,
		-  覆盖抽象方法 `public abstract boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv)` 它是javac编译器在执行注解处理器代码时要调用的过程
			- 第一个参数 获取到此注解器所要处理的注解集合;
			- 第二个参数 访问到当前这个Round中的语法树节点,每个语法树节点再这里表示为一个Element;
			- JDK 1.6 javax.lang.model 定义了16类Element
				- 包 package;
				- 枚举 enum;
				- 类 class;
				- 注解 annotation_type;
				- 接口 interface;
				- 枚举值 enum_constant;
				- 字段 field;
				- 参数 parameter;
				- 本地变量 local_variable;
				- 异常 exception_parameter;
				- 方法 method;
				- 构造函数 constructor;
				- 静态语句块 static_init
				- 实例语句块 instance_init
				- 参数化类型 type_parameter;
				- 其他语法树节点 other;
		- 常用的实例变量 `protected ProcessingEnvironment processingEnv;` 初始化的时候创建,代表注解处理器框架提供的一个上下文环境,要创建新的代码,向编译器输出信息,获取其他工具类等都需要这个实例变量;

	- 注解处理器除了process()方法及其参数之外,还有两个可以配合使用的Annotations 
		- @SupportedAnnotationTypes 注解处理器对哪些注解感兴趣,可以使用星号*通配对所有注解都感兴趣;
		- @SupportSourceVersion 指出这个注解处理器可以处理哪些版本的java代码;
	- 每一个注解处理器在运行时都是单例的,如果不需要改变或生成语法树的内容,process()方法就可以返回一个值为false的布尔值,通知编译器这个round中的代码未发生变化,无需构造新的javaCompiler实例;

- 晚期(运行时)优化

 - java最初是通过解释器进行解释执行的,当jvm发现某个方法或者代码块执行频繁,就会把这些代码认定为'热点代码(HotSpot)',提高热点代码的效率,运行时,jvm将会把这些代码编译成与本地平台相关的机器码,并进行层次的优化,完成这个任务的编译器为即时编译器(Jit)
 - java hotspot jvm是解释器与编译器并存的架构, 当程序需要快速启动和执行时,解释器可以首先发挥作用,省去编译的`时间`,立即执行;当程序运行后,随着时间得推移,编译器逐渐发挥作用,把越来越多的代码编译成本地代码之后,可以获取较高的执行`效率`;
 - hotspot jvm内置两个即时编译器,分别称为 Client Compiler,Server Compiler 简称 C1编译器,C2编译器; 分别分为 `混合模式`,`编译模式`,`解释模式`;
 - 编译优化技术 (太复杂,选几点记录一下)
	 - 方法内联 (Method Inlining) 去除方法调用的成本(如建立栈帧);为其他优化建立良好的基础;非虚方法可以直接内联;
	 - 冗余访问消除 ,公共子表达式消除;
	 - 复写传播
	 - 无用代码消除;
	 - 逃逸分析(如果一个对象不会逃逸到方法或线程之外,也就是别的方法或线程无法通过任何途径访问到这个对象,可能进行一些高效的优化(是否说明少用形式参数?))

这块挺复杂的,只是粗浅的看了下,有兴趣的可以看原书;

-------


## 高效并发

由于计算机的存储设备和处理器的运算速度有几个数量级的差距,所以加入一层读写速度竟可能能接近处理器运算速度的`高速缓存(Cache)`来作为内存和处理器之间的缓冲: 将运算需要使用的数据复制到缓存中,让运算能快速进行;当运算结束后再从缓存同步到内存中,这样处理器 就无须等待缓慢的内存读写了;

同是带来一个问题 : `缓存一致性(Cache Coherence)`: 每个处理器都有自己的高速缓存,而他们又共享同一主内存(Main Memory);
![在这里插入图片描述](https://img-blog.csdnimg.cn/20191118174726700.png)

所以各个处理器访问缓存时都要遵循一些协议,如MSI,MESI等; `内存模型`:可以理解为在特定的操作协议下,对特定的内存或高速缓存进行读写访问的过程抽象; 不同架构的物理机器拥有不同的内存模式; 

>java 内存模型 java memory model JMM

- 主内存与工作内存
	- 主要目标: 定义程序中各个变量的访问规则,即在jvm中将变量存储在内存和从内存中取出变量的底层细节; 此处的变量包括 实例字段,静态字段和构成数组对象的元素等存在竞争关系的,不包括局部变量和方法参数,因为后者是线程私有的;
	- jmm规定所有变量都存储在主内存(Main Memory)中,每条线程还有自己的工作内存(Working Memory),线程的工作内存保存了被该线程使用到的变量的主内存副本拷贝,线程对变量的所有操作(读取,赋值)都必须在工作内存中进行,而不能直接读写主内存的变量;不同的线程间也不能直接访问对方工作内存中的变量,线程间变量值得传递都需要通过主内存来完成;
	![在这里插入图片描述](https://img-blog.csdnimg.cn/20191118180611298.png)

- 内存间交互操作
	- jmm定义8中操作完成,保证每一种操作都是原子的,不可在分的(对于double,long类型的变量,load,store,read,write操作在某些平台允许有例外)
		- lock 锁定,作用于主内存的变量,把一个变量标识为一条线程独占的状态;
		- unlock 解锁,作用于主内存的变量,把一个处于锁定状态的变量释放出来,释放后的变量才可以被其他线程锁定;
		- read 读取, 作用于主内存的变量,把一个变量的值从主内存传输到线程的工作内存中,以便随后的load动作使用;
		- load 载入,作用于工作内存的变量,把read操作从主内存中得到的变量值放入工作内存的变量副本中;
		- use 使用,作用于工作内存的变量,把工作内存中一个变量的值传递给执行引擎,每当jvm遇到一个需要使用到变量的值的字节码指令时将会执行这个操作;
		- assign 赋值,作用于工作内存的变量,把一个从执行引擎接受到的值赋给工作内存的变量,每当jvm遇到一个给变量赋值的字节码指令时执行这个操作;
		- store 存储,作用于工作内存的变量,把工作内存中一个变量的值传送到主内存中,以便随后的write操作使用;
		- write 写入,作用于主内存的变量,把store操作从工作内存中得到的变量的值放入主内存的变量中;
	- 其中必须要满足的规则: 
		- 其中,read load ,store write 必须顺序成对执行;
		- 不允许一个线程丢弃它的最近的assgin操作,变量在工作内存改变了之后必须把改变化同步会主内存;
		- 没有发生assgin操作,不允许一个线程无原因的把数据同步回主内存;
		- 一个新的变量只能在主内存中诞生,即对一个变量实施use,store操作之前,必须先执行过assgin和load操作;
		- 一个变量在同一时刻只允许一条线程对其进行lock操作,但lock操作可以被同一线程重复执行多次,并执行同样多的unlock才能解锁;
		- 如果对一个变量执行lock操作,那将会清空工作内存中此变量的值,在执行引擎使用这个变量前,需要重新执行load或assgin操作初始化变量的值;
		- 如果一个变量事先没有被lock操作锁定,那就不允许对它执行unlock操作,不允许去unlock一个别其他线程锁定住的变量;
		- 对一个变量执行unlock操作之前,必须先把此变量同步会主内存中(执行store,write操作)

- volatile 
	- jvm提供的最轻量级的同步机制;一个变量被定义为volatile后,保证此变量对所有线程的`可见性`,指当一条线程修改了这个变量的值,新值对于其他线程来说是可以立即得知的;保证有序性,内存屏障禁止重排序;
	- volatile变量的第一个语义为`可见性`,volatile变量值保证可见性,在不符合下列两种规则的运算场景中,仍然要通过加锁(synchronized 或java.util.concurrent中的原子类)来保证原子性;
		- 运算结果并不依赖变量的当前值,或者能够确保只有单一的线程修改变量的值;
		- 变量不需要与其他的状态变量共同参与不变约束;
	- 使用volatile变量的第二个语义是禁止`指令重排序`优化,即保证有序性;
		- 字节码指令中多了一个`lock`,lock作用: 提供一个内存屏障(Memory Barrier 或Memory Fence,指令重排序时不能把后面的指令重排序到内存屏障之前的位置;) lock 使得本cpu的cache写入内存,该写入动作也会引起别的cpu或者别的内核无效化(Invalidate)其Cache,相当于对Cache中的变量做了一个jmm中的store和write操作;
	- i++的分析 并发混乱分析
		- getstatic 取字段值; iconst_1 将一个int型常量加载到操作数栈; iadd 加; putstatic 回值; return 记录返回值;  操作不是原子性,getstatic 取得值可能是其他线程改变后的值,操作数栈的值就是过期的数据;
	- 对于long和double型变量的特殊规则
		- JMM 对于lock,unlock,read,load,use,assign,store,write 8个操作都具有原子性,对于64位的数据类型,定义一条相对宽松的规定:
			- 允许jvm将没有被volatile修饰的64位数据的读写操作划分为两次32位的操作进行;即允许jvm实现选择可以不保证64位数据类型的load,store,read,write者4个操作,这就是long和double的非原子性协定;
			- 现象就是如果多个线程共享一个并未声明为volatile的long或double的变量,并且同时对它进行读取和修改,可能某些线程会读到一个既非原值,也不是其他线程修改的半个变量;
	- 并发三大特性: 原子性,可见性,有序性
		- 原子性 (Atomicity) : 基本数据类型的访问读写是具备原子性的;jmm还提供了lock和unlock操作保证原子性,对应更高层次的字节码指令monitorenter和monitorexit,这两个字节码指令反映到java代码中就是同步块(synchronize关键字),因此在synchronize块中的操作也具有原子性;
		- 可见性 (Visibility) : 一个线程修改了共享变量的值,其他线程能够立即得知这个修改; jmm是通过在变量修改后将新值同步回主内存,在变量读取前从主内存刷新变量值这种依赖主内存作为传递媒介的方式来实现可见性的; 无论是普通变量还是volatile变量都是如此,区别就是volatile的特殊规则保证了新值能`立即`同步到主内存,以及每次使用前`立即`从主内存刷新; 因此,可以说volatile保证了多线程操作时变量的可见性,而普通变量则不能保证这点;
			- 除volatile之外,java还有两个关键字能实现可见性,即synchronized 和 final; 
			- 同步块的可见性是对一个变量执行unlock操作之前,必须先把此变量同步会主内存中(执行store,write操作)
			- final关键字可见性是被final修饰的字段在构造器中一旦初始化完成,并且构造器没有把this的引用传递出去(this引用逃逸是一件很危险的事情,其他线程可能通过这个引用访问到初始化了一半的对象),那在其他线程中就能看见final字段的值;
		- 有序性 (Ordering) : 
			- java程序天然的有序性可总结为 如果在本线程内观察,所以的操作都是有序的;如果在一个线程中观察另一个线程,所以的操作都是无序的; 对应于'线程内表现为串行的语义'和'指令重排序,工作内存和主内存同步延迟'
			- java 语言提供了volatile 和synchronized 保证线程之间的操作的有序性; volatile本身通过内存屏障禁止指令重排序,sychronized由一个变量在同一时刻只允许一条线程对其进行lock操作;决定了持有同一个锁的两个同步块只能串行的进入;
	- 先行发生原则 happens-before
		- 判断数据是否存在竞争,线程是否安全的主要依据; jmm中定义的两项操作之间的偏序关系;
		- 默认先行发生关系 (无任何同步手段保障的先行发生规则下):
			- 程序次序规则 Program Order Rule: 在一个线程内,按照程序代码顺序(控制流顺序),书写在前面的操作先行发生于书写在后面的操作;
			- 管程锁定规则 Monitor Lock Rule: 一个unlock操作先行发生于后面对同一个锁的lock操作;
			- Volatile变量规则 : 一个volatile变量的写操作先行发生于后面对这个变量的读操作;
			- 线程启动规则 Thread Start Rule : Thread对象的start方法先行发生于此线程的每一个动作;
			- 线程终止规则 Thread Termination Rule : 线程中的所有操作都先行发生于对此线程的终止检测,可使用Thread.join()方法结束(阻塞当前线程,等待join的线程执行完毕),Thread.isAlive()的返回值等手段检测线程已经终止执行;
			- 线程中断规则 Thread Interuption Rule : 对线程interrupt的方法调用先行发生于被中断线程的代码检测到中断事件的发生;可使用Thread.interrupted()方法检测到是否有中断发生;
			- 对象终结规则 Finalizer Rule : 一个对象的初始化完成(构造函数执行结束)先行发生于它的finalize方法的开始;
			- 传递性 Transitivity ;

> java与线程

线程的实现:  线程是最小的调度执行单位;每个已经执行start()且还未结束的java.lang.Thread类的实例就是代表了一个线程;

- 使用内核线程实现
	- 内核线程(Kernel-level Thread KLT)直接由操作系统内核支持的线程,这种线程由内核来完成线程切换,内核通过操作调度器(Scheduler)对线程进行调度,并负责将线程的任务映射到各个处理器上; 
	- 程序一般不会直接使用内核线程,而是使用内核线程的一种高级接口-轻量级进程(Light Weight Process,LWP),轻量级进程就是通常意义上的线程,每个轻量级进程都由一个内核线程支持;

![在这里插入图片描述](https://img-blog.csdnimg.cn/20191204181459720.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L01ySmFydmlzRG9uZw==,size_16,color_FFFFFF,t_70)

- 使用用户线程实现
	
![在这里插入图片描述](https://img-blog.csdnimg.cn/20191204182558278.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L01ySmFydmlzRG9uZw==,size_16,color_FFFFFF,t_70)

- 使用用户线程加轻量级进程混合实现

![在这里插入图片描述](https://img-blog.csdnimg.cn/20191204182751697.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L01ySmFydmlzRG9uZw==,size_16,color_FFFFFF,t_70)

- java线程的实现

目前的jdk版本中,操作系统支持怎样的线程模型,很大程度上决定了jvm的线程是怎样映射的;

> java线程调度

线程调度是指系统为线程分配处理器使用权的过程; 主要调度方法分为: 协同式线程调度(Cooperative Threads-Scheduling) 和抢占式线程调度 (Preemptive Threads-Scheduling);

- 协同式的多线程系统 :线程的执行时间由线程本身来控制,线程把自己的工作执行完了后,主动通知系统切换到另外一个线程上; 
- 抢占式的多线程系统 :每个线程将由系统来分配执行时间,线程的切换不由线程本身来决定;线程的执行时间可控,不会被一个线程导致整个进程阻塞;java使用的线程调度方式就是抢占式调度;(Thread 10个线程优先级可给某些进程多分配一点时间)

>状态转换

java定义5中线程状态,在任意一个时间点,一个线程只能有且只有其中一种状态;

- 新建 New : 创建后尚未启动的线程处于这种状态;
- 运行 Runnable : 包括了操作系统状态中的Running和Ready,也就是处于此状态的线程有可能正在执行,也有可能正在等待CPU为他分配时间;
- 无限期等待 Waiting : 处于这种状态的线程不会被分配CPU执行时间,他们要等待被其他线程显示的唤醒; 可将线程进入等待状态方法:
	- 没有设置Timeout参数的Object.wait()方法;
	- 没有设置Timeout参数的Thread.join()方法;
	- LockSupport.park()方法;
- 限期等待 Timed Waiting : 处于这种状态的线程也不会被分配CPU执行时间,不过无需等待被其他线程显示的唤醒,在一定的时间后它们会由系统自动唤醒; 可将线程进入限期等待方法: 
	- Thread.sleep()方法;
	- 设置Timeout参数的Object.wait()方法;
	- 设置Timeout参数的Thread.join()方法;
	- LockSupport.parkNanos()方法;
	- LockSupport.parkUntil()方法;
- 阻塞 Blocked : 线程被阻塞了,与等待状态的区别为: 阻塞状态在等待着获取到一个排他锁,这个事件将在另外一个线程放弃这个锁的时候发生; 而等待状态则是等待一段时间,或者唤醒动作的发生; 在程序等待进入同步区域的时候,线程将进入这种状态;
- 终结 Terminated : 已终止线程的线程状态,线程已经结束执行;

![在这里插入图片描述](https://img-blog.csdnimg.cn/20191205115808823.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L01ySmFydmlzRG9uZw==,size_16,color_FFFFFF,t_70)

----

## 线程安全和锁优化

- 线程安全 : 当多个线程访问同一个对象时,如果不用考虑这些线程在运行时环境下的调度和交替执行,也不需要进行额外的同步,或者在调用方进行任何其他的协调操作,调用这个对象的行为都可以获得正确的结果,那这个对象是线程安全的; 即代码封装了所有必要的正确性保障手段(互斥同步)
	- java操作共享数据分类:
		- 不可变 Immutable : 不可变对象一定是线程安全的; java中如果共享数据是一个基本数据类型,只要在定义时使用final关键字修饰可保证它是不可变的; 如果共享数据是一个对象,需要保证对象的行为不会对其状态产生影响才行; 如java.lang.String类 AtomicLong等;
		- 绝对线程安全
		- 相对线程安全 : 通常意义上的线程安全;
		- 线程兼容 
		- 线程对立 无论调用端是否采取同步措施,都无法在多线程环境中并发使用的代码;

- 线程安全的实现方法
	- 互斥同步 Mutual Exclusion & Synchronization (阻塞同步): 同步是指在多个线程并发访问共享数据时,保证共享数据在同一个时刻只被一个线程使用(或者是一些,使用信号量的时候);互斥是实现同步的一种手段 ,临界区(Critical Section),互斥量(Mutex),信号量(Semaphore)都是主要的互斥实现方式;
		- java中 最基本的互斥同步手段就是`sychronized` 关键字, sychronized经过编译后,会在同步块的前后分别形成monitorenter和monitorexit这两个字节码指令,这两个字节码都需要一个reference类型的参数来指明要锁定和解锁的对象;
		- 如果sychronized明确指定了对象参数,那就是这个对象的reference,如果没有明确指令,那就是根据sychronized修饰的是实例方法还是类方法,取对应的对象实例或者Class对象作为锁对象;
		- jvm规范 sychronized同步块对同一条线程来说是可重入的,不会出现自己把自己锁死的问题; 同步块在已进入的线程执行完之前,会阻塞后面的其他线程的进入; 而java的线程是映射在操作系统的原生线程之上的,如果要阻塞或者唤醒一个线程,都需要操作系统来帮忙完成,需要从用户态转换到内核态中,因此状态转换需要耗费很多的处理器时间; 所以 sychronized 是java中的一个重量级的操作;
		- 除了sychronized之外,还可以使用java.util.concurrent(JUC)包中的重入锁(ReentrantLock)来实现同步; 增加一些高级功能: 等待可中断,可实现公平锁,锁可绑定多个条件;
			- 等待可中断, 当持有锁的线程长期不释放锁的时候,正在等待的线程可以选择放弃等待,改为处理其他事情;
			- 公平锁, 多个线程在等待同一个锁时必须按照申请锁的时间顺序来依次获得锁; 非公平锁不保证这一点,在锁释放时,任何一个等待锁的线程都有机会获得锁;
			- 锁绑定多个条件,ReentrantLock可以同时绑定多个Condition对象,而在Sychronized中,锁对象的wait()和notify()或notifyAll()方法可以实现一个隐含的条件,如果要和多于一个的条件关联的时候,就不得不得额外的添加一个锁,而ReentrantLock无需这样做;
	- 非阻塞同步 Non-Blocking Synchronization : 互斥同步的主要问题是进行线程阻塞和唤醒锁带来的性能问题,也被成为阻塞同步;互斥同步属于一种`悲观`的并发策略;随着硬件指令集的发展,还有另外一个选择: 基于冲突检测的`乐观`并发策略;
		- 冲突检测需要靠硬件实现,常用的指令有:
			- 测试并设置 Test and Set
			- 获取并增加 Fetch and Increment
			- 交换 Swap
			- 比较并交换 Compare and Swap (CAS)
			- 加载链接/条件存储 Load Linked /Store Conditional (LL/SC)
		- CAS 指令 有3个操作数,分别为内存位置(Java中可理解为变量的内存地址,用V表示),旧的预期值(用A表示),和新值(用B表示); CAS指令执行时,当且仅当V符合旧预期值A时,处理器用新值B更新V的值,否则它就不执行更新,但是无论是否更新了V的值,都会返回V的旧值,上述的处理过程是一个原子操作;
			- sun.misc.Unsafe类里的compareAndSwapInt()和compareAndSwapLong()等几个方法包装提供,Unsafe类不是提供给用户程序调用的类(Unsafe.getUnsafe()的代码中限制了只有启动类加载器(Bootstrap Classloader)加载的Class才能访问它),因此,不采用反射手段,只能通过其他的api去使用它,如JUC的整数原子类的compareAndSet()使用Unsafe类的CAS操作;
			- CAS存在ABA问题,就是A先改为B,在改为A,CAS操作认为它没有被改变过;可使用传统的互斥同步;
	- 无同步方法 不可变保证线程安全
		- 可重入代码 Reentrant Code : 纯代码Pure Code,可以在代码执行的任何时刻中断它,转而执行另外一段代码(包括递归调用它本身),而在控制权返回后,原来的程序不会出现任何错误; 如果一个方法,它的返回结果是可以预测的,只要输入了相同的数据,就都能返回相同的结果,那它就满足可重入性的要求,当然也是线程安全的;
		- 线程本地存储 Thread Local Storage : 如果一段代码中所需要的数据必须与其他代码共享,如果能保证这些共享数据的代码在同一个线程中执行,就可以把共享数据的可见范围限制在同一个线程之内,这样无需同步也能保证线程间不出现数据争用的问题;
			- 可使用java.lang.ThreadLocal类来实现线程本地存储的功能,代表为某个线程独享,每个Thread对象中都有一个ThreadLocalMap对象,这个对象存储了一组以ThreadLocal.threadLocalHashCode为键,以本地线程变量为值的K-V值对; 

```

	ReentrantLock 的用法

 	public ArrayBlockingQueue(int capacity, boolean fair) {
        if (capacity <= 0)
            throw new IllegalArgumentException();
        this.items = new Object[capacity];
        lock = new ReentrantLock(fair);
        notEmpty = lock.newCondition();
        notFull =  lock.newCondition();
    }
	...
	public boolean offer(E e) {
        Objects.requireNonNull(e);
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            if (count == items.length)
                return false;
            else {
                enqueue(e);
                return true;
            }
        } finally {
            lock.unlock();
        }
    }

```

> 锁优化

- 自旋锁和自适应自旋
	- 因为互斥同步需要阻塞,挂起线程和恢复线程都需要转入内核态完成,而某些共享数据的锁定状态只会持续很短的一段时间;如果物理机器有一个以上的处理器,能让两个或以上的线程同时并行执行,让后面请求锁的线程不放弃处理器的执行时间,让线程执行一个忙循环(自旋),这就是自旋锁;
	- 默认的自旋次数为10次(循环),自适应的自旋时间不在固定,由上一次在同一个锁上的自旋时间及锁的拥有者的状态来决定;

- 锁消除 : jvm 在运行时,对一些代码上要求同步,但是被检测到不可能存在共享数据竞争的锁进行消除;
- 锁粗化 : 循环体反复加锁,加锁同步的范围扩展到整个操作序列的外部;
- 轻量级锁 : 为了没有多线程竞争的前提下,减少传统的重量级锁使用操作系统互斥量产生的性能消耗;
	- hotspot jvm 的对象头(Object Header)分为两部分信息,第一部分存储对象自身的运行时数据,如hashcode,GC age等mark word; 这部分是实现轻量级锁和偏向锁的关键;另外一部分存储的是指向方法区对象类型数据的指针,如果是数组对象还有用于存储数组长度; 如果有两条以上的线程争用同一个锁,轻量级锁要膨胀为重量级锁;

![在这里插入图片描述](https://img-blog.csdnimg.cn/20191205161408402.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L01ySmFydmlzRG9uZw==,size_16,color_FFFFFF,t_70)

- 偏向锁 : 消除数据在无竞争情况下的同步原语,提高程序的运行性能;如果轻量级锁时无竞争的情况下使用CAS去消除同步使用的互斥量;偏向锁就是无竞争情况下把整个同步都消除掉CAS都不做了;
	- 锁会偏向于第一个获得它的线程,如果在接下来的执行过程中,该锁没有被其他的线程获取,则持有偏向锁的线程将永远不需要在进行同步;如果有其他线程尝试获取这个锁,偏向模式宣告结束;

![在这里插入图片描述](https://img-blog.csdnimg.cn/20191205162408496.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L01ySmFydmlzRG9uZw==,size_16,color_FFFFFF,t_70)

-----