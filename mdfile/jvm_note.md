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
		- 不过jvm会生成一个直接继承于java.lang.Object的子类,创建动作由字节码指令newarray触发;
	-  常量(final , ConstantValue属性)在编译阶段会存入调用类的常量池中,本质上并没有直接引用到定义常量的类,不会触发定义常量类的初始化;

- 接口的加载过程与类加载过程稍有不同
	- 接口在初始化时,并不要求父接口全部都完成了初始化,只有在真正使用到父接口(引用接口中定义的常量)采用初始化;

>类加载的过程

- 加载
	- 通过一个类的全限定名获取定义此类的二进制字节流;
	- 将这个字节流所代表的静态存储结构转化为方法区的运行时数据结构;
	- 在内存中生成一个代表这个类的java.lang.Class对象,作为方法区这个类的各种数据的访问入口;

加载阶段完成后,jvm外部的二进制字节流就按照jvm所需格式存储在方法区之中,然后在内存中实例化一个Class类对象,作为程序访问方法区中的这些类型数据的外部接口;

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

- 解析
	- jvm 将常量池内的符号引用替换为直接引用的过程;
	- 引用区分:
		- 符号引用 (Symbolic References): 以一组符号来描述所引用的目标;
		- 直接引用 (Direct References): 直接引用可以是直接指向目标的指针,相对偏移量或者一个能间接定位到目标的句柄; 
	- jvm规范中并未规定解析发生的具体时间,要求在执行`anewarray,checkcast,getfield,getstatic,instanceof,invokedynamic,invokeinterface,invokespecial,invokestatic,invokevirtual,ldc,ldc_w,multianewarray,new,putfield,putstatic`16个用于操作符号引用的字节码指令之前,先对他们所使用的符号引用进行解析;
	- invokedynamic 指令必须等到程序实际运行到这条指令的时候,解析动作才能进行,且不缓存;其余符号指令可以在完成加载阶段,还没开始执行就进行解析,也可对第一次解析结果进行缓存;
