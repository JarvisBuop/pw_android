# android java 常用知识阶段总结:

- java 相关
- android 相关
- android framework
- 三方源码
- 算法与数据结构
- 项目&hr
- 计算机基础知识
- 杂项知识

---


[常用容器](https://blog.csdn.net/j550341130/article/details/80664826) 

[类加载](http://www.cnblogs.com/ityouknow/p/5603287.html)

[recyclerview](https://mp.weixin.qq.com/s/CzrKotyupXbYY6EY2HP_dA) 


---

# java 相关

### 容器（HashMap、HashSet、LinkedList、ArrayList、数组等）以及 Android中特有的数据结构与常见的java数据结构(熟悉那个说那个)，存储过程，源码与底层实现。spasearray & ArrayMap

> `ArrayMap` 可代替Map,implement Map,实现为object 的数组;

	
	int[] mHashs; // 保存hash;
	Object[] mArray; //保存key和value;
	put过程为: 
	先计算key对应的hash,使用二分查找hash,是否包含在容器中;
	已经包含替换容器中的值,不在时判断是否需要扩容;
	将key和value连续相邻的保存在mArray中;


>`ArraySet`

	与ArrayMap的数据结构一样,只保存value;

>`SparseArray`,SparseIntArray,SparseBooleanArray,SparseLongArray ,
可代替map,implement Cloneable ,实现为int和object的数组;

	private int[] mKeys; //保存的key值为int
	private Object[] mValues; //保存的value为obj;
	与arraymap不同的是用不同数组分别存储;

	//arraymap的扩容
	final int n = osize >= (BASE_SIZE*2) ? (osize+(osize>>1))
                    : (osize >= BASE_SIZE ? (BASE_SIZE*2) : BASE_SIZE);

	//sparseArray的扩容
	public static int growSize(int currentSize) {
        return currentSize <= 4 ? 8 : currentSize * 2;
    }
	

1. 避免对key的自动装箱,默认就是int,内部通过两个数组实现,一个存储key,一个存储object,内部对数据采取压缩的方式表示稀疏数组的数据,节约内存;<br>
2. 获取和添加数据时使用二分查找法比较int大小,按照顺序排好或判断元素位置;

spraseArray 可代替hashmap

 1.在数据量不大,千级以内;(二分法数据量大不明显);<br>
 2.key必须为int时

>ArrayList 

数组实现,需要扩容时,将已有数组数据复制到新的存储空间中,因为数组实现,遍历查找代价低,删除插入代价高;

临时的对象数组使用transient,不参与序列化,使用writeObje和readObj代替(内存流);

初始容器大小为10, `grow`方法扩容,扩容方法为`int newCapacity = oldCapacity + (oldCapacity >> 1);` 扩容1.5倍; 最大size 为`Integer.MAX_VALUE -8` 超过Int的最大值成为负数则抛出oom; 再通过`System.arraycopy`将origin数组考进来,将额外的扩容数据赋值;

>LinkList 

链表结构存储数据,遍历查找代价高,删除插入代价低; 可当做堆栈,队列和双向队列使用;

	public class LinkedList<E>
	extends AbstractSequentialList<E>
    implements List<E>, Deque<E>, Cloneable, java.io.Serializable


实现由双向链表实现`E item;
        Node<E> next;
        Node<E> prev;`
应用类似于队列;
查找的时候先判断一半,接着将一半的数据从头遍历到尾;
增删避免了数组拷贝,所有插入的效率高;

>HashSet 哈希表

值不重复,对象的相等性本质是对象hashCode的值(java是根据对象的内存地址计算出的此序号)判断的,想要两个不同的对象视为相等,覆盖Object的hashCode方法和equals方法;

存储无序, HashSet 首先判断两个元素的哈希值，如果哈希值一样，接着会比较 equals 方法 如果 equls 结果为 true ， HashSet 就视为同一个元素。如果 equals 为 false 就不是同一个元素。 

内部包含HashMap,由hashmap key实现; 一个hashcode位置上也可以存放多个元素;

>TreeSet

使用二叉树的原理对新add的对象按照指定顺序排序;

Integer和String可使用默认的TreeSet排序,自定义对象需要使用Comparable接口,重写compareTo函数排序;

内部包含TreeMap实现;

>TreeMap

实现SortedMap接口,能够把它保存的记录根据键排序,默认是按键值的升序排序; 使用TreeMap时,key必须实现Comparable接口或者在构造Treemap传入自定义的Comparator;

[通过分析 JDK 源代码研究 TreeMap 红黑树算法实现](https://developer.ibm.com/zh/articles/j-lo-tree/)

>LinkHashMap 记录插入顺序

LinkedHashMap 是 HashMap 的一个子类，保存了记录的插入顺序，在用 Iterator 遍历
LinkedHashMap 时，先得到的记录肯定是先插入的

>`HashMap`

 内部是使用一个默认容量为16的数组存储数据的,而数组中每一个元素又是一个链表的头结点;
hashmap只允许一条记录的键为null,允许多条记录的值为null;
 
- java 7实现 数组 + 链表

 每一个节点都是Map.Entry类型,存储hash,key,value,和下一个节点的引用;
	

	final int hash;
	final K key;
	V value;
	Node<K,V> next;

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200805074847244.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L01ySmFydmlzRG9uZw==,size_16,color_FFFFFF,t_70)

大方向上,HashMap 里面是一个数组，然后数组中每个元素是一个单向链表。每个绿色的实体是静态内部类Entry的实例;

1.capacity: 默认的存储大小为16的数组,没有任何元素,也会占有空间; 始终保持2^n

2.根据负载因子(默认0.75)扩容时`(newCap = oldCap << 1)`;扩容的大小是乘以2,对内存空间消耗很大;扩容阈值 threshold = capacity * loadfactor;

- java 8实现 数组+链表或数组+红黑树

1.8之前的hashmap 采用的是`数组+链表`,缺点是元素分布不均匀;hash冲突导致某个链表可能会非常长,遍历的时间变长,时间复杂度为链表长度O(n);

1.8之后,采用的是`数组+链表或数组+红黑树`,添加元素,某一个链表(bin,某个箱子)的元素超过8个转换为红黑树,时间复杂度O(n)为O(logN);删除,扩容时元素个数少转换为链表结构,遍历提高性能;

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200805080123502.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L01ySmFydmlzRG9uZw==,size_16,color_FFFFFF,t_70)

hashmap有3种主流遍历方式;

1. `Map.Entry`(entrySet()),
2. `keySet`, ( 迭代器就不算了) 
3. 最新遍历方式(函数式遍历):`forEach(BiConsumer )` ;

>ConcurrentHashMap

简单理解就是， ConcurrentHashMap 是一个 Segment 数组， Segment 通过继承
ReentrantLock 来进行加锁，所以每次需要加锁的操作锁住的是一个 segment，这样只要保证每
个 Segment 是线程安全的，也就实现了全局的线程安全。

concurrencyLevel：并行级别、并发数,默认是 16，
也就是说 ConcurrentHashMap 有 16 个 Segments，所以理论上， 这个时候，最多可以同时支
持 16 个线程并发写，只要它们的操作分别分布在不同的 Segment 上。这个值可以在初始化的时
候设置为其他值，但是一旦初始化以后，它是不可以扩容的。再具体到每个 Segment 内部，其实
每个 Segment 很像之前介绍的 HashMap，不过它要保证线程安全，所以处理起来要麻烦些。

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200805215021560.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L01ySmFydmlzRG9uZw==,size_16,color_FFFFFF,t_70)

java8引入红黑树

![在这里插入图片描述](https://img-blog.csdnimg.cn/2020080521534848.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L01ySmFydmlzRG9uZw==,size_16,color_FFFFFF,t_70)

>HashTable与HashMap的区别。

继承不同,一个是Map,一个是Dictionary(已废弃); 对外提供的接口也不同;线程安全性不同,Hashmap 是线程不安全,效率高,hashtable相反; ConcurrentHashMap也是线程安全的,使用分段锁;null key 的支持不同,hashmap都支持,hashtable 都不支持;遍历方式内部不同; hashmap的iterator是`fail-fast`迭代器,有其他线程改变了hashmap的结构(增加,删除,修改),抛出concurrentmodificationException; 不建议在新代码中使用,可使用HashMap或者ConcurrentHashMap替换;


![](https://img-blog.csdn.net/20180306020714182?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvd2FuZ3hpbmcyMzM=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

![](https://img-blog.csdn.net/20180306020658482?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvd2FuZ3hpbmcyMzM=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

>Fail-Fast:

java 的错误检测机制,当多个线程对集合进行`结构`上的改变的操作时,有可能产生fail-fast机制;(例如 遍历remove时,遍历输入;或者多线程去remove item,抛出ConcurrentModificationException);

modCount != expectedModCount 判断两者的值不等则抛出错误;

解决方法: 

单线程环境remove数据时,使用`迭代器`,或者`remove后,i--`;

多线程环境下,使用copyOnWriteArrayList代替;

>并发容器

ConcurrentHashMap(数据结构和hashmap类似,使用synchronized);

CopyOnWriteArraySet

ArrayBlockingQueue (对象数组)

LinkBlockingQueue

`CopyWriteArrayList` 不可变对象设计模式,读写分离,用于并发情况,读的时候直接索引,写的时候System.arraycopy(); (COW 写时拷贝)

`Collections.synchronizedList()` 装饰着设计模式,增强list的方法,获取mutex对象相关联的monitor锁,用于并发;

![](https://img-blog.csdn.net/20180703141542253?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2o1NTAzNDExMzA=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)

> Atomic 保证高并发环境的原子指令; 硬件层面实现的原子操作;

```
AtomicBoolean, AtomicInteger, AtomicLong, AtomicReference提供对相应类型的单个变量的原子访问/更新;
updater类AtomicReferenceFieldUpdater, AtomicIntegerFieldUpdater, AtomicLongFieldUpdater 是基于反射的工具类, 可以提供对相关类型字段的访问, 主要用于对数据结构中的volatile字段单独进行原子操作, 在更新的方式和时间上更加灵活, 弊端请见官方文档;
AtomicIntegerArray, AtomicLongArray, AtomicReferenceArray 进一步提供了这些类型在集合中的原子操作, 特别是提供了普通集合不具备的元素volatile访问语义.
```

![](https://img-blog.csdn.net/20180705150230861?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2o1NTAzNDExMzA=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)

###  JVM 相关 - GC回收算法,java内存结构,JVM的引用树，什么变量能作为GCRoot？

>线程

这里所说的线程指程序执行过程中的一个线程实体。 JVM 允许一个应用并发执行多个线程。
Hotspot JVM 中的 Java 线程与原生操作系统线程有直接的映射关系。 `当线程本地存储、缓
冲区分配、同步对象、栈、程序计数器等准备好以后，就会创建一个操作系统原生线程。
Java 线程结束，原生线程随之被回收。操作系统负责调度所有线程，并把它们分配到任何可
用的 CPU 上。当原生线程初始化完毕，就会调用 Java 线程的 run() 方法。当线程结束时,
会释放原生线程和 Java 线程的所有资源`

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200806074627990.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L01ySmFydmlzRG9uZw==,size_16,color_FFFFFF,t_70)

> jvm 内存区域 

`线程私有`: Jvm栈,本地方法栈,程序计数器;<br>
栈中的栈帧的内存分配在编译器间已经确定;
栈帧用于存储局部变量表,操作数栈,动态链接,方法出口等,每一个方法从调用直至执行完成的过程,对应一个栈帧在jvm中入栈到出栈的过程;
生命周期与线程相同;

`线程共享`: 方法区,堆;<br>
生命周期随jvm;

运行时常量池（Runtime Constant Pool）是方法区的一部分。 Class 文件中除了有类的版
本、字段、方法、接口等描述等信息外，还有一项信息是常量池
（Constant Pool Table），用于存放编译期生成的各种字面量和符号引用，这部分内容将在类加
载后存放到方法区的运行时常量池中。

`直接内存`: 并不是jvm运行时数据区的一部分;
但也会被频繁的使用: 在 JDK 1.4 引入的 NIO 提
供了基于 Channel 与 Buffer 的 IO 方式, 它可以使用 Native 函数库直接分配堆外内存, 然后使用
DirectByteBuffer 对象作为这块内存的引用进行操作(详见: Java I/O 扩展), 这样就避免了在 Java
堆和 Native 堆中来回复制数据, 因此在一些场景中可以显著提高性能。

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200806074017115.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L01ySmFydmlzRG9uZw==,size_16,color_FFFFFF,t_70)

>`Gc检测算法 - 确定垃圾`:

1.引用计数算法 

典型ios,循环引用;

2.可达性分析算法

>通过GCRoots 对象作为起点,从根节点开始向下搜索,走过的路径为引用链,如果一个对象处于空岛状态,则可以gc的对象;
可以作为GC Roots的对象:

1.虚拟机栈(栈帧中的本地变量表)中引用的对象;

2.方法区中的类静态属性引用的对象或者常量引用的对象;

3.本地方法栈中jni引用的对象;

要注意的是，不可达对象不等价于可回收对象， 不可达对象变为可回收对象至少要经过两次标记
过程。两次标记后仍然是可回收对象，则将面临回收。

### java内存模型 jmm

>线程之间的通信和同步

通信:线程间以何种机制交换信息;

- `共享内存`; 线程之间共享程序的公共状态,通过读写公共状态来隐式进行通信,典型的共享通信就是通过`共享对象`进行通信;
- `消息传递`; 线程间没有公共状态,通过明确的发送消息显示进行通信,在java中典型的就是`wait() notify()`;

在共享内存并发模型里，同步是显式进行的。程序员必须显式指定某个方法或某段代码需要在线程之间互斥执行。
在消息传递的并发模型里，由于消息的发送必须在消息的接收之前，因此同步是隐式进行的。

>JMM

java并发采用的是`共享内存模型`,这里的共享内存模型就是java内存模型(JMM),

JMM **只是一个`抽象概念`,定义线程和主内存之间的抽象关系**;决定线程对共享变量的写入何时对其他线程可见,以及在必须时如何同步的访问共享变量;

**JMM决定了一个线程对共享变量的写入何时对另一个线程可见**,

线程间的抽象关系为: **线程间的共享变量存储在主内存(Main Memory),每个线程都有一个私有的本地内存(Local Memory),本地内存中存储该线程以读写共享变量的副本;** 

本地内存是JMM的一个抽象概念，并不真实存在。它涵盖了缓存，写缓冲区，寄存器以及其他的硬件和编译器优化。

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200806082803331.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L01ySmFydmlzRG9uZw==,size_16,color_FFFFFF,t_70)

[理解JMM](https://zhuanlan.zhihu.com/p/29881777)


线程a和线程b要通信需要2个步骤:

1.线程A 把本地内存A中更新过的共享变量刷新到主内存中去;

2.线程B 到主内存中读取线程A之前已更新过的共享变量;

> jvm对java内存模型的实现

jvm内部,jmm分为`线程栈区`(调用栈),`堆区`;

![](https://imgconvert.csdnimg.cn/aHR0cDovL2ltZy5ibG9nLmNzZG4ubmV0LzIwMTYwOTIxMTgyODM3Njk3?x-oss-process=image/format,png)

![](https://imgconvert.csdnimg.cn/aHR0cDovL2ltZy5ibG9nLmNzZG4ubmV0LzIwMTYwOTIxMTgyOTQ4NjAx?x-oss-process=image/format,png)

> 硬件内存架构

![](https://imgconvert.csdnimg.cn/aHR0cDovL2ltZy5ibG9nLmNzZG4ubmV0LzIwMTYwOTIxMTgzMDEzNTcw?x-oss-process=image/format,png)

当一个cpu访问主存时,会先读取一部分主存数据到cpu缓存,进而在读取cpu缓存到寄存器; 当cpu需要写数据到主存时,同样会先flush寄存器到cpu缓存,然后在某些节点把缓存数据flush到主存;

>java内存模型和硬件架构间的桥接

Java内存模型和硬件内存架构并不一致。硬件内存架构中并没有区分栈和堆，从硬件上看，不管是栈还是堆，大部分数据都会存到主存中，当然一部分栈和堆的数据也有可能会存到CPU寄存器中，如下图所示，Java内存模型和计算机硬件内存架构是一个交叉关系：

![](https://imgconvert.csdnimg.cn/aHR0cDovL2ltZy5ibG9nLmNzZG4ubmV0LzIwMTYwOTIxMTgzMTQ0OTk1?x-oss-process=image/format,png)

当对象和变量存储在计算机的各个内存区域,会遇到`共享对象对各个线程的可见性`,`共享对象的竞争现象`等问题

- 共享对象的可见性; 解决共享对象可见性,可使用`volatile`关键字,可保证并发`可见性`和`有序性`,保证变量会直接从主内存读取,而对变量的更新也会直接写到主存; `volatile` 基于cpu内存屏障指令实现的; 
- 竞争现象; 可使用`synchronized`代码块;可保证并发`原子性`和`有序性`和`可见性`,保证同一时刻只能有一个线程进入代码竞争区,也能保证代码块中所有变量都将会从主存中读,当线程退出代码块时,对所有变量的更新将会flush到主存,不管这些变量是不是volatile类型的; 

>支撑jmm的基础原理

指令重排序: 

在执行程序时，为了提高性能，编译器和处理器会对指令做重排序。但是，JMM确保在不同的编译器和不同的处理器平台之上，通过插入特定类型的`Memory Barrier`来禁止特定类型的编译器重排序和处理器重排序，为上层提供一致的内存可见性保证。 如果两个操作访问同一个变量，其中一个为写操作，此时这两个操作之间存在`数据依赖性`。编译器和处理器不会改变存在数据依赖性关系的两个操作的执行顺序，即不会重排序。

`as-if-serial` (串行)不管怎么重排序,单线程下的执行结果不能被改变,编译器,runtime和处理器都必须遵守此语义;

`Memory Barrier` (内存屏障)是一个cpu指令

- 保证特定操作的执行顺序
- 影响某些数据(或者是某条指令的执行结果)的内存可见性;

如果一个变量是volatile修饰的,jmm会在写入这个字段之后插进一个`write-barrier`指令,并在读这个字段之前插入一个`read-barrier`指令,意味着写入一个volatile变量,可以保证:

- 一个线程写入变量a后,任何线程访问该变量都会拿到新值;
- 在写入变量a之前的写入操作,其更新的数据对其他线程是可见的; 因为memory barrier 会强制刷出cache中的所有先前的写入;

`happerns-before` 要求 前一个操作的执行结果,对于后一个操作是可见的,且前一个操作排在后一个操作之前;

volatile 可有效解决`缓存一致性问题`和`指令重排序问题`


### volatile 和synchronized 

并发三大特性: 原子性,可见性,有序性;

根据jmm的实现,线程在具体执行时,会先拷贝主存数据到本地内存(cpu缓存),操作完成后将结果刷回主存;

- synchronized 解决有序性和原子性和可见性,会阻止其他线程获取当前对象的监控锁(原理是jvm指令monitor enter和monitor exit排他的串行化方式,加锁就是在竞争Monitor对象); 还会创建一个`内存屏障`,保证所有cpu操作结果都会直接刷到主存中,从而保证操作的内存可见性;

- volatile 解决可见性(缓存一致性)和有序性(禁止指令重排序),会使得所有对volatile变量的读写都会直接刷到主存,保证可见性;
	- 仅能对原始变量(基本数据类型)操作的原子性,不能保证复合操作的原子性; (i++ = read,inc,write)

- 其他区别
	- volatile本质是在告诉jvm当前变量在寄存器（工作内存）中的值是不确定的，需要从主存中读取； synchronized则是锁定当前变量，只有当前线程可以访问该变量，其他线程被阻塞住。
	- volatile仅能使用在变量级别；synchronized则可以使用在变量、方法、和类级别的
	- volatile标记的变量不会被编译器优化；synchronized标记的变量可以被编译器优化



### 垃圾回收算法 GC算法（JVM）

1.标记-清除算法<br> 
适用于存活对象较多的垃圾回收;
缺点: 效率低,产生大量不连续的内存碎片;

2.复制算法:<br>
使用于存活对象较少的gc
缺点: 每次对整个半区内存回收,将内存缩小一半;<br>
新生代比例占比: eden:from survivor: to survivor = 8: 1: 1,如果10%的to survivor空间不够时需要老年代进行分配担保(直接进入老年代);

3.标记-整理算法<br>
标记回收的对象,将存活对象移至一端,清理需要回收的对象;

4.分代回收算法<br>
根据对象存活周期分为新生代和老年代,根据每个年代特点使用合适的回收算法;
如: 新生代存活对象少使用复制算法,老年代存活对象多并且没有分配担保必须使用标记-清除,和标记-整理回收算法;

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200806075834156.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L01ySmFydmlzRG9uZw==,size_16,color_FFFFFF,t_70)

新生代分为Eden,SurvivorFrom,SurvivorTo,频繁触发MinorGc;

MinorGc过程 : 复制 -> 清空 -> 互换

- eden+survivorFrom 复制到 survivorTo ,年龄加1 (存活下来的够多,直接进入老年代; 存活下来的对象年龄达到老年标准,进入老年代)
- 清空Eden,servivorFrom;
- SurvivorFrom 和 SurvivorTo 互换;

MajorGc前一般都会进行一次MinorGc,MajorGc采用`标记清除/整理算法`;

java8中永久代被移出,被一个称为“元数据区”（元空间）的区域所取代。元空间
的本质和永久代类似，元空间与永久代之间最大的区别在于： `元空间并不在虚拟机中，而是使用
本地内存。`

### Java 四大引用

- 强引用 new object;常用;
- 软引用 SoftReference 内存不足会被回收;
- 弱引用 WeakReference 垃圾回收机制一运行即回收;
- 虚引用 PhantomReference 不能单独使用,与引用队列联合使用,主要是跟踪对象被垃圾回收的状态;

### 垃圾回收机制和调用 System.gc()的区别？

system.gc() 是建议进行垃圾回收,但不一定回收;

### java IO 知识 (NIO暂不了解)

### java的类加载过程（需要多看看，重在理解，对于热修复和插件化比较重要）

>类的生命周期: 

![在这里插入图片描述](https://img-blog.csdnimg.cn/20191031160441427.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L01ySmFydmlzRG9uZw==,size_16,color_FFFFFF,t_70)

 类加载->连接->初始化->使用->卸载 <br>
 连接分为: 验证->准备->解析 <br>

> 1.类加载: 
 
 用全限定名获取类的.class文件中的二进制字节流;<br>
 将字节流所代表的静态存储结构转化为方法区的运行时数据结构;<br>
 在java堆中生成一个java.lang.Class对象,作为方法区中数据的访问入口;

 加载阶段,使用了双亲委托机制,可以使用自定义的类加载器加载

**类加载的方式:**


- 命令行启动应用时候由jvm初始化加载;

- 通过class.forName()动态加载;
字节码文件加载到jvm中

- ClassLoader.loadClass()动态加载;
 
> 2.连接:
 
- 验证: 确保被加载类的正确性; 文件格式验证,元数据验证,字节码验证,符号引用验证;
 
- 准备: 为类的`静态变量`分配内存,并将其初始化为默认值;

 内存分配的仅包括类变量(static),不包括实例变量, 实例变量在对象实例化时随着对象被初始化; 这里所设置的初始值通常情况下是数据类型默认的零值;
 
 **不同的是:** 注意`static final` 是准备阶段赋值,理解为编译时将其放入常量池中;


而静态变量的赋值的初始值 put static指令编译后是放在类构造器`<clinit>`方法中;
 
- 解析: 将类中的符号引用转换为直接引用;
	- 符号引用: 不一定加载到内存中,可以是任何形式的字面量,需要符合jvm的class文件规范;
	- 直接引用: 指向目标的指针,可为相对编译量 或 一个能间接定位到目标的句柄; 如果有了直接引用,引用的目标必定已经在内存中了;
 
 
> 3.初始化

为类的静态变量赋予正确的初始值,主要对类变量进行初始化;<br>
对类的`主动使用`会导致类的初始化<br>
包括new;访问类或接口的静态变量;调用类的静态方法;反射;初始化某个类的子类,则其父类也会被初始化;启动类main;

执行`<clinit>` (class initial)函数,包含所有类变量的赋值和静态语句块的执行代码;

jvm会保证子类的`<clinit>`函数执行之前,父类的`<clinit>`方法已经执行完毕;

不会执行类初始化的几种情况:

- 子类引用父类的静态字段,只会触发父类的初始化过程,不会触发子类的初始化;
- 定义对象数组,不会触发该类的初始化;(并没有直接使用)
- 常量在编译期间会存入类的常量池中,不会触发定义常量所在类初始化,static final;
- 通过类名获取Class对象;
- 通过`Class.forName`加载指定类,指定参数为`initialize`为false时,也不会触发类初始化,这个参数告诉jvm,是否要对类进行初始化;
- 通过`ClassLoader`默认的`loadClass`方法,也不会触发初始化动作;

### 类加载器

>启动类加载器(Bootstrap ClassLoader,rt.jar),扩展类加载器(ext,java.ext.dirs),应用程序类加载器(application,加载用户路径classpath上的类库),自定义类加载器

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200808075218252.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L01ySmFydmlzRG9uZw==,size_16,color_FFFFFF,t_70)

> 全盘委托机制(双亲委托机制)

一个类收到类加载请求,先把这个请求委托给父类去完成,只有父类加载器反馈自己无法完成这个请求的时候(加载路径下没有找到所需加载的Class),子类加载器才会尝试自己去加载;  可以保证使用不同的类加载器最终得到的都是同样的一个Object对象;

> 破坏双亲委托机制

双亲委托机制很好的解决各个类加载器基础类的统一问题(越基础的类由越上层的加载器进行加载),但是如果基础类需要调用回用户的代码,就需要破坏双亲委托机制了; 

典型的例子就是 JNDI服务;它的代码由启动类加载器加载(rt.jar),但是它需要由独立厂商实现并部署在应用程序的ClassPath下的代码,而启动类加载器不认识这些代码; 这里使用到了`线程上下文类加载器`

线程上下文类加载器:

`setContextClassLoader`(Thread) ,这个就是父类加载器请求子类加载器去完成类加载的动作;

程序动态性的追求也破坏双亲委托机制,`OSGI`实现模块化热部署的关键则是它自定义的类加载器机制的实现;每哥程序模块(Bundle)都有一个自己的类加载器,需要更换Bundle时,将Bundle连同类加载器一起换掉以实现代码的热替换; osgi 不再是双亲委托机制的树状结构,而是网状结构;

>使用自定义加载器方式:

- 继承于`ClassLoader`,重写findClass方法;
- 可绕过应用程序类加载器,直接用自定义加载器加载; 
	- 第一种方式是将扩展类加载器(应用程序类加载器的父类加载器)指定为自定义类加载器的父加载器; 这样启动类加载器和扩展类加载器都不能加载类文件;
	- 第二种方式是构造自定义类加载器时指定父类加载器为null,会使用启动类加载器加载,启动类加载器加载路径找不到会使用当前类加载器加载;


### 多线程和线程池

>Runnable,Callable,Future,FutureTask

Runnable 没有返回值,可将耗时操作写在里面,使用线程池去运行; Callable 有返回值; Future 类似于凭据,对于具体的runnable,callable任务的执行结果进行取消查询是否取消,获取结果,设置结果等操作; futuretask是future,runnable,还可以包装callable,增强型的future;

>java中的线程: 

主要使用Thread和Executor来实现多线程,使用`线程池`和`锁`和`多线程的多种设计模式`(生产者消费者,latch,future,EDA,BUS,读写锁分离,不可变对象设计) wait(Object) notify实现 (wait set);
	- 悲观锁(synchronized)和乐观锁(cas)	

>ExecutorService (interface ExecutorService extends Executor) 线程池接口

- 多线程空指针解决方法

>多线程中运行时异常,UncaughtExceptionHandler,线程在执行单元中是不允许抛出check异常(编译时异常),所以需要`try..catch`,或者包装成RuntimeException,使用UncaughtExceptionHandler接受;

- java线程池创建方式,线程池工作原理: new ThreadPoolExecutor(...)

 四种线程池,Executor;
 
 - corePoolSize: 核心线程数,创建保存到阻塞队列; 
 - maximumPoolSize: 允许的最大线程数,指定可创建非核心线程数量;
 - keepAliveTime: 非核心线程的空闲时的存活时间;
 - unit: 单位;
 - workQueue: 保存到被执行任务的阻塞队列,LinkedBlockingQueue 链表队列,ArrayBlockingQueue 数组队列;
 - threadFactory: 创建线程工厂;
 - handler: 阻塞队列饱和策略;AbortPolicy:抛出异常策略; CallerRunsPolicy: 用调用者原来的线程执行任务;DiscardOldestPolicy: 丢弃阻塞队列中靠前的(最老的)任务,执行当前任务; DiscardPolicy: 直接丢弃任务; 自定义扩展`RejectedExecutiondHander`


newFixedThreadPool : 固定核心线程数量;

newCachedThreadPool: 没有核心线程数量,非核心线程数量整数最大值;

newSingleThreadPool: 一个核心线程数量,支持重启;

newScheduleThreadPool: 固定核心线程数量,总线程数为整数最大值;


> 线程间通信,线程同步和线程调度相关的方法:sleep,wait ,yield,join 等方法区别?

- sleep: Thread 的静态本地方法; 休眠当前线程指定时间,`释放cpu资源`,`不释放对象锁`,休眠时间到自动苏醒继续执行;  给其他线程执行机会的最佳方式;

sleep使线程进入`Timed_Waiting`状态;

- wait: Object的成员本地方法; 只能在同步方法或同步代码块中使用; 如果wait,`放弃持有的对象锁`,将thread加入到锁对象的`wait set` 等待池中; `释放cpu资源,让锁资源`;当该对象调用notify/notifyAll后才有机会竞争获取对象锁,进入运行状态;

wait()会释放锁,释放锁的前提是获得锁,所以必须在同步方法/代码块中使用;

使调用方法的线程进入到`Waiting`状态;

- yield: 给其他线程执行的机会;由运行状态进入到就绪状态,`释放cpu`,`不释放已经持有的锁`; 让相同优先级的线程轮流执行,但不保证一定会轮流执行,不阻塞;

- notify: 唤醒一个处于等待状态的线程,notifyall 唤醒所有处于等待的线程,竞争锁资源;

- join: 先执行join的线程的内容,后执行当前线程的内容,`释放已经持有的锁`,`抢占cpu`;

- interrupt: 中断一个线程,给这个线程一个通知信号,会影响这个线程内部的一个中断标志位,这个线程本身并不会因此而改变状态(阻塞,终止等);

调用这个方法并不会中断一个正在运行的线程,仅仅改变了标志位;

若调用sleep处于Timed-Waiting状态,此时调用interrupt,抛出InterruptedException,从而使线程提前结束Timed-Waiting状态;

线程内部可使用`thread.isInterrupted()`终止线程;

> ThreadLocal是什么？Looper中的消息死循环为什么没有ANR？ (这个被问到的频率还挺高,后面单独详细介绍)

线程本地变量,使用ThreadLocal创建的变量,只能被当前线程访问,其他线程无法访问和修改;

ThreadLocalMap 可将线程自己的对象保持在其中;

android中Handler切换线程就是因为ThreadLocal 中存放了Looper (messageQueue);

InheritableThreadLocal() 创建子线程时,将某个线程的threadlocal传递过去;

>线程的状态变化

![](https://img-blog.csdnimg.cn/20181120173640764.jpeg?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3BhbmdlMTk5MQ==,size_16,color_FFFFFF,t_70)

- New 新建
- Runnable 运行状态 java线程中将就绪和运行中两种状态笼统的称为运行;
	- 线程对象创建后，其他线程(比如main线程）调用了该对象的start()方法。该状态的线程位于可运行线程池中，等待被线程调度选中，获取CPU的使用权，此时处于就绪状态（ready）。就绪状态的线程在获得CPU时间片后变为运行中状态（running）。
- Blocked 阻塞状态,锁;
- Waitting 等待状态; 
- Timed_Waitting 超时等待状态;指定时间后返回;
- Terminated 终止;


### 为什么主线程Looper.loop死循环不会导致应用卡死

[主线程Loop死循环为什么不会导致卡死](https://zhuanlan.zhihu.com/p/106730344)

这里就涉及到Linux pipe/epoll机制，简单说就是在主线程的MessageQueue没有消息时，便阻塞在Looper.loop()的queue.next()中的`nativePollOnce()`方法，此时主线程会释放CPU资源进入休眠状态，直到下个消息到达或者有事务发生，通过往pipe管道写端写入数据来唤醒主线程工作。这里采用的epoll机制，是一种IO多路复用机制，可以同时监控多个描述符，当某个描述符就绪(读或写就绪)，则立刻通知相应程序进行读或写操作，本质同步I/O，即读写是阻塞的。 所以说，`主线程大多数时候都是处于休眠状态，并不会消耗大量CPU资源`。
当收到不同Message时则采用相应措施：一旦退出消息循环，那么你的程序也就可以退出了。 从消息队列中取消息可能会阻塞，取到消息会做出相应的处理。如果某个消息处理时间过长，就可能会影响UI线程的刷新速率，造成卡顿的现象
在子线程中，如果手动为其创建了Looper，那么在所有的事情完成以后应该调用quit()方法来终止消息循环，否则这个子线程就会一直处于等待（阻塞）状态，而如果退出Looper以后，这个线程就会立刻（执行所有方法并）终止，因此建议不需要的时候终止Looper;

总结一下: 当没有消息时,native层的方法做了阻塞处理,所以Looper.loop死循环不会卡死应用;

### java锁

> 死锁产生条件和场景

互斥条件;请求和保持条件;不剥夺条件,环路等待;

1.共享资源的竞争;2.请求和释放资源顺序不当;

`多线程多锁`吃面问题,多个线程使用刀和叉两个同步锁去吃面,就会发生死锁; a线程持有刀锁,等待叉锁;b线程持有叉锁,等待2刀锁;

`单线程重复申请锁` ,`忘记释放锁`, `环形锁`;

避免: 资源有序分配; 银行家算法; 将多个互斥锁对象封装成一个类作为锁对象;

一般解决方法

- 避免嵌套锁,将多个锁对象封装成一个对象作为总的锁对象;
- 设置锁的超时时间;

> 线程安全问题

序列化访问临时资源(同步互斥访问),同一时刻,只能有一个线程访问临时资源;

- 悲观锁: synchronized 和 lock ;

写多,并发可能性很高; ReentrantLock 是先尝试cas乐观锁去获取锁,获取不到,才会转换为悲观锁;

- 乐观锁: 版本号机制或CAS算法(compare and swap);

	- 版本号机制(有点像令牌机制)  无锁算法,version++后就不能更新;
 
	- CAS算法 (非阻塞同步,Atomic包下类) 适合写比较少的情况

读写内存值V,进行比较的值A,拟写入的新值B;
 
当且仅当V的值等于A时,CAS通过原子方式用新值B更新V值(比较和替换是一个原子操作),自旋操作,不断重试;

cas缺点: 1.ABA问题(a b a,无法检测,使用版本号机制解决);2.循环时间长开销大;3.只能保证一个共享变量的原子操作(AtomicReference,多个变量放在一起);


- 自旋锁: 如果持有锁的线程能在很短时间内释放锁资源，那么那些等待竞争锁
的线程就不需要做内核态和用户态之间的切换进入阻塞挂起状态，它们只需要等一等（自旋,占据cpu资源），
等持有锁的线程释放锁后即可立即获取锁，这样就避免用户线程和内核的切换的消耗。


>ReentrantLock extends Lock 可重入锁;

处理能完成synchronized所能完成的所有工作外,还提供 可响应中断锁,可轮询锁请求,定时锁等避免多线程死锁的方法;

- ReentrantLock 通过方法`lock()与unlock()`进行加锁和解锁,与synchronized会被jvm自动解锁不同,Reentrantlock加锁后需要手动解锁; finally块中解锁;
- 优势是可中断,公平锁,多个锁;

```

	public class MyService {
	private Lock lock = new ReentrantLock();
	//Lock lock=new ReentrantLock(true);//公平锁
	//Lock lock=new ReentrantLock(false);//非公平锁
	private Condition condition=lock.newCondition();//创建 Condition
	public void testMethod() {
	try {
	lock.lock();//lock 加锁
	//1： wait 方法等待：
	//System.out.println("开始 wait");
	condition.await();
	//通过创建 Condition 对象来使线程 wait，必须先执行 lock.lock 方法获得锁
	//:2： signal 方法唤醒
	condition.signal();//condition 对象的 signal 方法可以唤醒 wait 线程
	for (int i = 0; i < 5; i++) {
	System.out.println("ThreadName=" + Thread.currentThread().getName()+ (" " + (i + 1)));
	}
	} catch (InterruptedException e) {
	e.printStackTrace();
	}
	finally
	{
	lock.unlock();
	}
	}
	}

```

Condition 类和 Object 类锁方法区别区别

1. Condition 类的 awiat 方法和 Object 类的 wait 方法等效
2. Condition 类的 signal 方法和 Object 类的 notify 方法等效
3. Condition 类的 signalAll 方法和 Object 类的 notifyAll 方法等效
4. ReentrantLock 类可以唤醒指定条件的线程，而 object 的唤醒是随机的

>Semaphore 信号量 (android用的还挺多)

是一种基于计数的信号量;控制同时访问的线程个数; 

- acquire 获取一个许可,若无许可可以获取,则会一直等待,直到获得许可;

- release 释放许可,释放许可之前必须先获得许可; 

acquire和release 都会引起阻塞;想立即得到执行结果:

- boolean tryAcquire 立即获取执行结果

```

	//看到过的用法如下
	public Bitmap capture() throws InterruptedException {
		//新建阈值为0的信号量; 意为只有0个线程能同时访问,即acquire就阻塞;
        final Semaphore waiter = new Semaphore(0);

        final int width = surfaceView.getMeasuredWidth();
        final int height = surfaceView.getMeasuredHeight();

        // Take picture on OpenGL thread
        final Bitmap resultBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        gpuImage.runOnGLThread(new Runnable() {
            @Override
            public void run() {
                GPUImageNativeLibrary.adjustBitmap(resultBitmap);
				//这里可用finally块释放,释放许可,被阻塞的地方继续运行;
                waiter.release();
            }
        });
        requestRender();
		//申请访问,阈值为0阻塞;
        waiter.acquire();

        return resultBitmap;
    }

```

> CountDownLatch

java.util.concurrent包下,实现类似计数器功能;

```

	final CountDownLatch latch = new CountDownLatch(1);
	new Thread(){public void run() {
	System.out.println("子线程"+Thread.currentThread().getName()+"正在执行");
	Thread.sleep(3000);
	System.out.println("子线程"+Thread.currentThread().getName()+"执行完毕");
	//减1,为0通知被阻塞的地方继续运行;
	latch.countDown();
	};}.start();
	
	System.out.println("等待 1 个子线程执行完毕...");
	//阻塞当前线程;
	latch.await();
	System.out.println("1 个子线程已经执行完毕");
	System.out.println("继续执行主线程");

```

> CyclicBarrier 回环栅栏-等待至barrier状态在全部同时执行;

实现让一组线程等待至某个状态之后在全部执行;当所有等待线程都被释放以后,CyclicBarrier可以被重用; 调用wait后,线程就处于barrier;

await(...) 方法用来挂起当前线程,直至所有线程都到达barrier状态在同时执行后续任务; 有时间参数的表示等待一定的时间,如果有线程没有到barrier,直接让到达barrier的线程执行后续任务; 


>SemaPhore 与 ReenTrantLock ,CountDownLatch 等区别

- Semaphone基本能完成ReenTrantLock的所有工作;
- Semaphone.acquire()方法默认为可响应中断锁，与 ReentrantLock.lockInterruptibly()作用效果一致,等待临界资源的过程中可被Thread.interrupt方法中断;
- CountDownLatch和Cyclicbarrier 都能够实现线程之间的等待,只不过它们侧重点不同,COuntDownLatch一般用于线程A等待若干个其他线程执行完任务之后,它才执行;  CyclicBarrier一般用于一组线程互相等待至某个状态,然后这一组线程在同时执行; CountDownLatch不可重用,CyclicBarrier可以重用;
- Semaphore 和锁类似,用于控制对某组资源的访问权限; 没有许可的情况下会阻塞,这点和CountDownLatch最大区别;


### 网络相关 HTTP、HTTPS、TCP/IP、Socket通信、 

> socket连接,tcp,http 请求和响应

请求行: 请求方法,请求统一资源标志符(URI:URL排除host剩下的部分,资源在服务器本地上的路径),http版本号;

请求头: content-Type

请求体: 真正发送的数据;

响应行: http版本号,状态码; 1XX:信息提示;2XX:成功;3XX:重定向; 4XX:客户端错误; 5XX: 服务端错误;

响应头,响应体: 


>网络分层:

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200809175000441.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L01ySmFydmlzRG9uZw==,size_16,color_FFFFFF,t_70)

7层模型: 

- 物理层 数模转换 0/1序列; 数据为比特;
- 数据链路层 交换机,MAC地址(网卡的地址)的封装和解封;数据为帧;
- 网络层 路由器,Ip地址的封装和解封; 数据为数据包;
	- ip协议
- 传输层 Tcp和Udp,传输数据的协议和端口号;数据为段;
	- Tcp/udp 
- 会话层 通过传输层建立的数据传输的通路;
- 表示层 对接受数据进行解释,加密与解密,压缩和解压缩(转换为人能够识别的东西)
- 应用层 Ftp,web,终端应用;
	- Http: 超文本传输协议
	- TELNET: 虚拟终端协议
	- FTP:文件传输协议
	- SMTP: 电子邮件传输协议
	- DNS:域名服务
	- NNTP:网上新闻传输协议;


应用层(http协议,FTP协议),传输层(Tcp/udp),网络层(ip协议),连接层(wifi,以太网),物理层(0/1);

http 是应用层的协议;tcp是传输层的协议;socket 传输层上抽象出来的一个抽象层,本质是接口,对tcp/ip协议的封装;

- `Http是基于Tcp`,客户端发送一个http请求第一步就是建立与服务器的tcp连接,就是三次握手;从http1.1开始支持持久连接,一次tcp可发送多次http请求;
- `Socket也基于Tcp`,一个socket可以基于tcp连接和可以基于Udp的连接,是一个接口;
- `长短连接,请求响应先后` http连接是短连接,socket连接基于Tcp的是长连接.socket一旦建立tcp三次握手连接,除非一方主动断开,否则一直保持; http采用请求响应机制,socket请求发送没有前后限制;

socket: 用于即时通讯;支持不同的传输层协议,当支持tcp连接时,就是一个tcp长连接;
> http用于不需要时刻在线,资源获取文件上传,短连接;


![](http://cc.cocimg.com/api/uploads/20160323/1458719461811413.png)

### tcp三次握手四次挥手过程


> tcp/ip 

tcp/ip协议是指因特网整个tcp/ip协议族, 而不是tcp和ip两个协议的合成; 从协议分层模型来说,有以下组成: 

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200809172559850.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L01ySmFydmlzRG9uZw==,size_16,color_FFFFFF,t_70)

![数据包说明](https://img-blog.csdnimg.cn/20200809173450163.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L01ySmFydmlzRG9uZw==,size_16,color_FFFFFF,t_70)

连接时:三次握手;

- 第一次握手,客户端发送syn包(syn = j;)到服务器,进入SYN_SEND状态,等待服务器确认;
- 第二次握手,服务器收到syn包,确认客户端的syn(ack = j+1),同时自己也发送一个syn包(syn = k),即syn+ack包,此时服务器进入SYN_RECV状态;
- 第三次握手,客户端收到服务器的syn+ack包,向服务器发送确认包ack(ack = k+1),此包发送完毕,客户端和服务器进入ESTABLISHED状态,完成3次握手;

断开时: 4此挥手;

- 第一次挥手,客户端发送fin包,关闭数据传送;
- 第二次挥手,服务器收到fin,发回一个ack;
- 第三次挥手,服务器关闭与客户端的连接,发送fin+ack给客户端;
- 第四次挥手,客户端发挥ack报文确认;

![](https://images0.cnblogs.com/blog/385532/201308/30193702-7287165c73e7440382207309e07fcbb5.png)

![](https://images0.cnblogs.com/blog/385532/201308/30193703-330b281cddc5439f99eb027ac1c9627c.png)

> 如何建立长链接。

tcp 使用心跳机制; 可以使用socket套接字实现;


###   讲讲 HTTPS 是如何做加密的

https其实是由两部分组成: http+ssl/tls,就是在http上又加了一层处理加密信息的模块;传输的都是加密后的信息;(非对称加密和对称加密联合使用,先非对称加密后对称加密;)

- 服务器把自己的公钥登录至`数字证书认证机构`;数字证书机构把自己的私钥向服务器的公开密码部署数字签名并颁发`公钥证书`;
- 客户端拿到服务器的公钥证书后,使用数字证书认证机构的公钥,向数字证书机构`验证`公钥证书上的数字签名,以确认服务器公钥的真实性;
- 客户端使用服务器的公钥对报文加密后发送; (客户端Rsa公私钥,会话秘钥)
- 服务器用私钥对报文解密;

---

- `认证服务器`,浏览器内置受信任的CA机构列表,保存这些CA机构的证书,如果是可信的,从服务器证书中取得服务器公钥,用于后续流程;
- `协商会话秘钥`,客户端认证完服务器,获取服务器公钥后,利用服务器公钥与服务器机密通信,协商两个会话秘钥,用于客户端>服务端和服务端>客户端的会话秘钥(对称加密的秘钥,节省资源);会话秘钥是随机生成;
- `加密通讯`,传输的http信息是通过会话秘钥加密,先使用会话秘钥得到机密信息;

![](https://img-blog.csdn.net/20180818150959428?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzMyOTk4MTUz/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)

###   post请求的数据格式定义在那里定义，手写出来

 请求方法有`Option`,`Get`,`Post`,`Head`,`Put`,`Delete`,`Trace`,`Connect`八大类型;

 http协议是ascii码传输,建立在tcp/ip协议之上的应用层规范,分为请求行,请求头,请求体;
 服务端通常是根据请求头headers的`Content-Type`字段类获知请求中的消息主体是何种编码方式,在对主体进行解析,包括content-type和消息主体编码方式两部分;

 `application/x-www-form-urlencoded;charset=utf-8` 浏览器的原生表单,不设置`enctype`属性,就是这种方式<br>
 `multipart/form-data` 浏览器原生支持,使用表单上传文件时的,使表单的`enctype`为这个;<br>
 `application/json` 响应头,也可以作为请求头,消息主体是序列化后的json字符串;<br>
 `text/xml` xml为编码方式;

###   http与https的理解与4层都是哪4层，在一个请求过程中都是什么时候走这些流程与各自的作用

http和https 都是属于应用层,https添加了ssl/tls对数据进行了加密,一个请求首先通过网络层ip协议找到目标,然后通过传输层的tcp(/udp)建立连接,然后通过应用层http协议发送消息;

> 传输流程

- 地址解析 schema:host:port/path; DNS解析域名得到主机ip地址;
- 封装Http请求数据包;
- 封装Tcp包并建立连接
- 客户机发送请求命令 (请求)
- 服务器响应 (响应)
- 服务器关闭tcp连接; `Connection:keep-alive` tcp连接在发送后仍然保持打开状态;

###   session与cookie的区别

 session 会话技术, session在服务器上,cookie 在客户端;第一次发送请求时会自动生成sessionId,返回到浏览器的cookie中,下次访问将cookie中的sessionId一并传过来判断当前用户登录;

### Java 的泛型

参数化类型

[kotlin 型变详解](https://www.kotlincn.net/docs/reference/generics.html)

`<? extends T>`表示该通配符所代表的类型是T类型的子类; 指定上限;
`<? super T>`T类型的父类; 指定下限;

泛型擦除, java中的泛型基本上都是在编译器这个层次来实现的,在生成的java字节码中是不包含泛型中的类型信息的; 在编译的时候会去除类型参数;

java 使用的是`使用处型变`,kotlin 是`声明处型变`; `Producter-Extends,Consumer-Super` ,`producter-out,consumer-in`

- 协变 : 带extends限定(上界)的通配符类型使得类型是协变的(covariant);

`Collection<? extends Object>  = Collection<String>` 此时 get读取集合没问题,限制了add(T)等方法(破坏类型安全),add(T)为消费者方法,只get成为单纯的生产者;

- 逆变 : 带super限定(下界); 

`List<? super String> = List<Object>`此时 add(T)添加String没问题,限制get等方法(因为get返回类型不确定),get为生产者方法,只add成为单纯的消费者;

kotlin中 当一个类C的类型参数T被声明为`out`(型变注解),表示C是T的生产者,即类C是在参数T上是协变的,T是一个协变的类型参数;

同理 `in`表示类型参数逆变,只可以被消费不可被生产;

### 反射(内省相关)

运行时获取这个类的信息; Class.forName(class全限定名),对象.getClass(),类名.class;

Class获取注解方法: `getAnnotation`,`isAnnotationPresent 是否被注解修饰`,`getAnnotations`,`getDeclaredAnnotation 本元素的指定注解`,`getDeclareAnnotations`;

- 内省 `Introspector`: java对Bean类属性,事件的一种`缺省处理方法`;一般步骤:

>通过Introspector获取某个对象的BeanInfo信息,通过BeanInfo获取属性描述器(propertyDescriptor),获取某个属性对应的getter/setter方法,通过反射获取属性;操作javaBean;

![](https://img-blog.csdn.net/20160422101525368)

### final、finally、finalize 的区别

final 修饰变量表示常量,方法表示不可重写,类表示不可继承;

finally trycatch方法体运行完后一定会运行的块;

finalize 垃圾回收调用的方法;


### 注解 框架,原理,annotation

元数据,一种代码级别的说明,重点在于解析;

元注解(注解在注解之上)

- @Target : 注解的作用类型 ElementType 

说明Annotation所修饰的对象范围;

- @Retention: 注解的生命周期,被保留的时间长短;

Source 源文件保留,编译字节码时丢弃; (apt 生成文件;可标记类型,限制参数取值,替代枚举;)

Class 字节码保留,加载到jvm时丢弃; (aspectJ,动态代理修改字节码文件)

Runtime 运行时保留,不丢弃; (可反射)

- @Documented : 注解是否应当被包含在JavaDoc文档中;

- @Inherited: 标记注解,某个被标记的类型是被继承的,是否允许子类继承该注解;

>注解的解析方式: 

个人理解有两种,<br>

一种是直接`反射`获取Annotation对象(Runtime),接着通过注解的值处理逻辑; 如retrofit;

一种是继承AbstractProcessor接口(可以是Source),如butterknife+apt+javapoet生成代码(butterknife是Runtime,还是反射);

>注解的本质:

继承Annotation接口的接口,jdk通过动态代理机制生成一个实现我们注解(接口)的代理类;<br>
1.键值对的形式可以注解属性赋值;

2.注解修饰某个元素,编译期扫描注解和检查,写入元素的属性表;

3.反射的时候,jvm将所有runtime的注解取出来放到一个map(memberValues 注解属性名: 属性值)中,创建一个AnnotaionInvocationHandler实例,把这个map传递给他;

4.jvm将采用jdk动态代理机制生成一个目标注解的代理类,初始化好处理器;invoke中处理方法名,通过方法名返回注解属性值;

### Error 和 Exception 区别？

 都是继承Throwable类;

1.error 是系统中的错误,修改程序才可,指java运行时系统的内部错误和资源耗尽错误;  oomerror 可以捕获;

exception 是可以捕获处理, 分为编译时异常(CheckedException)和运行时异常(RuntimeException),RuntimeException不需要捕获,需要处理;


### Java的修饰符的使用

- public: 都可以调用;
- protected: 包权限和继承权限(继承->同包)
- default: 同包下可访问;
- private: 本类中访问;

protected修饰符的修饰的成员变量和方法,如果是继承于父类不管是不是同一个包都可访问,同包下也可以被访问。

kotlin中加入一个`internal` 模块内访问;

static 类变量或方法,类初始化时运行,方法区静态属性作为Gcroots起点; final 属性不可改变,方法不可重载,类不可继承;

static final 变量不可修改,类方法,属性,存在于方法区的运行时数据区,类连接-准备阶段初始化;


###   equals() 和 hashCode() 的区别是什么？平时有重写过它们么？什么情况下会去重写

equals 反映的是对象或变量具体的值; hashCode 计算出对象实例的哈希吗,散列函数,每个对象的哈希值都是唯一的;

两个obj,如果equals相等,hashcode 一定相等;
如果hashcode相等,equals不一定相等(hash冲突)

都是从Object的方法;

重写equals ,可判断对象的某个值相同

重写hashcode,hashmap就可以看出来,为了减少hash冲突;

###   java中是否有无符号整数，如果没有怎么实现

无<br>

java位运算: 负数转为原码(补码+1)
位与 &oxFF;

使用java8的 Stream,readUnsignedByte();

###  加密 ,解密

md5 摘要算法,不可逆; sha1 用于客户端的用户密码加密;

对称加密: AES,DES ,3DES

非对称加密: RSA

Rsa 客户端使用公钥加密,服务端使用私钥解密;注意：使用RSA加密之前必须在AndroidStudio的libs目录下导入bcprov-jdk的jar包

###   编码, 解码

Base64,UrlEncoder

### 数据库知识

- 左连接和右连接,各连接的区别

 左连接where只影响右表,右连接where只影响左表;  左连接后的结果是显示左表的所有数据和右表中满足where条件的数据,没有则null(交叉的部分);
 右连接显示的结果是右表中的所有数据和左表中满足where条件的数据,不满足null(交叉的部分); 内连接是交集,其他置null; 联合查询UNION;

 左连接只影响左表,右连接只影响右表

- 数据库的ACID

 原子性,一致性,隔离性,持久性;

 隔离级别(悲观锁和乐观锁): 脏读,不可重复读,虚读; (read_uncommited,read_commit,repeatable_read,serializable)

- 用过数据库么？如何防止数据库读写死锁？ ContentProvider && 单例实现。

 多个SqliteOpenHelper,多数据库操作,读写竞争; 
 
 - 1.创建一个SqliteOpenHelper单例;
 - 2.使用contentprovider,声明android:multiprocess="false",多进程单实例;
 - 3.单进程情况下,使用greendao(objectBox) 因为只有一个sqliteopenhelper,多进程下还是使用contentprovider;



-----------


# android 相关

### android 架构图

![](https://upload-images.jianshu.io/upload_images/9087029-c0662f8aa3987a04.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/465)

	1.Linux内核(Linux Kernel)底层Linux核心的工作,安全管理、内存管理、进程管理、电源管理、硬件驱动
	2.核心类库(Libraries)
	核心类库中包含了系统库及Android运行环境。系统库这一层主要是通过C/C++库来为Android系统提供主要的特性支持。
	Libraries：c代码库
	    OpenGL：图形快速显示，游戏开发
	     webkit：浏览器内核
	     Android Runtime
	    Dalvik VM：虚拟机，android代码运行在此虚拟机
	    运行时调用Libraries C代码库
	3.应用程序层(Application Framework)
	Application Framework中间层，java代码，调用底层c代码
	4.应用层
	Applications原生的应用程序：浏览器、桌面、联系人等
	相比以前的架构，多了一个HAL（硬件抽象层）

### Android中存储类型;

>Android中存储类型;

- SharedPreference; `/data/data/shared_prefes`
- sqlite,sqliteopenhelper
- 内部存储; 应用私有文件,其他应用不能访问这些文件;卸载时会被删除;`/data/data/files`
- 外部存储; 设备共享;

获取方法3种:

1. context类的getsharedpreference();
2. activit的getpreference();
3. preferenceManger类中的getDefaultSharedPreference()方法,使用当前应用程序的包名作为前缀来命名sharedpreferences文件;


SharedPrefrences的apply和commit有什么区别

commit为同步提交,apply为异步提交; commit()有返回值,apply无返回值;

> 如何将sqlite数据库(dictionary.db文件)和apk文件一起发布?:

将db文件复制到res raw文件夹中,此文件夹的文件不会压缩,可以直接提取该目录中的文件; (且此raw文件夹不会限制大小,会生成id,asserts中限制大小1M,且只能通过AssetManager访问;)

> 如何打开res raw 目录中的数据库文件?

在安卓中不能打开res raw 中的数据库文件,需要在程序第一次启动的时候将该文件复制到手机磁盘上(rom,sd卡)的某个目录上,然后在打开数据库文件;<br>
getResource().openRawResource()获得raw的inputStream对象,写入其他的目录相应文件,使用android sdk的`SQLiteDatabase.OpenOrCreateDatabase()`方法打开任意目录的sqlite数据库文件;

> SQLite的数据库升级用过么

SQLiteOpenHelper继承类 onUpdata();不升级的走onCreate方法,其他的就是sql语句了;


### fragment 相关

依存于act, 生命周期共11个方法;

oncreate : : onattach- oncreate- oncreateView- onActivityCreated (onViewCreated常用)

 onStart :: onStart

 onResume :: onResume

 onPause :: onPause

 onStop :: onStop

 onDestory :: onDestoryView -  onDestory -  onDetach

### Application生命周期

onCreate: 程序创建的时候运行;
  
  onTerminate: 程序终止的情况运行,不能保证一定调用;
  
  onLowMemory: 低内存的时候运行; 与level一样TRIM_MEMORY_COMPLETE;
  
  onTrimMemory: 内存清理的时候运行;当前内存状态;
  
  onConfigurationChange:  配置改变;

 调用OnTrimMemory区别在于 如果应用占有内存较小,可以增加不被杀掉的几率,从而快速的回复(如果不被干掉,启动的时候就是热启动,否则就是冷启动,速度差2~3倍;)

 onTrimMemory等级TRIM_MEMORY_UI_HIDDEN ,表示应用即将进入后台; 应用切换至前台 使用app的registerActivityLifecycleCallbacks() 在acticity onresumed方法得到状态 ;

![](https://img-blog.csdn.net/20180716122506349?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3hvdHR5/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)

### 资源管理,屏幕适配

### 问了下昼夜模式、多语种、屏幕适配的问题，追问了下，如果要关闭昼夜模式功能怎么办？很多类的话，一个个去关吗？

  换肤功能:
  
  - 定义两套主体resource / style;其中定义attr自定义属性,用于指定某些控件和布局的属性;
  - 切换主题的时候setTheme方法在setContentView之前; 
     - 1.当前页面主题的切换,调用Activity.recreate()方法; 其他页面主题切换,设置回到主页面清除所有页面达到换肤目的;
     - 2.其他页面在oncreate之前,设置主题activity.setTheme(R.style.xxx);

 使用 插件化动态加载技术实现换肤,aop编程,

 values中加strings_zh的string;

### Android两种虚拟机区别与联系 (dvm,art)

![](https://upload-images.jianshu.io/upload_images/4064751-1d2e001a9133ae9a.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/964)

![](https://upload-images.jianshu.io/upload_images/4064751-e63fc95fb46afc38.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1000)


 .java - .class - .jar ; 基于栈的架构,每次访问数据cpu都要到内存中取到数据;
 
 .java- .class- .dex; 基于寄存器的架构,cpu直接从寄存器中读取数据,寄存器是cpu的一块存储空间;

 `dalvik jit`(just in time)编译器,在运行时,实时将一部分的dalvik字节码翻译成机器码,jit只翻译一部分,内存小;

 5.0后 使用`ART(AOT)`代替DALVIK(JIT)编辑器 ,ahead of time编译器,应用的安装期间,将dex字节码翻译成机器码并存储在设备上,不在需要jit编辑,执行速度快,但需要更多的空间;  新的编译器可支持预先编译(AOT),即时编译(JIT)和解释代码;

### 性能优化 （讲讲你自己项目中做过的性能优化）(apk瘦身,电量优化,网络优化)

>减少apk的体积

- 使用`android size analyzer`大小分析插件;
- 缩减资源数量和大小;
- 减少原生和java代码

>了解apk的结构:

apk文件由一个Zip压缩文件组成,其中包含构成应用的所有文件;

- `META-INF` 包含CERT.SF 和 CERT.RSA签名文件,以及MANIFEST.MF清单文件;
- `assets/` 包含应用的资源; 可使用assetManger获取;
- `res/` 包含未编译到resources.arsc 中的资源;
- `lib/` 包含特定于处理器软件层的已编译代码,包含每种平台类型的子目录; 
- androidmanifest.xml 包含核心android清单文件;
- resources.arsc 包含已编译的资源,包含res/value中的xml内容;
- classes.dex 包含以Dalvik/ART虚拟机可理解的DEX文件格式编译的类;

体积占有大的是`lib`,`res`,`dex`; 减小apk体积主要是缩减so包,资源图片,控制代码质量;

- 使用`progruard 混淆`,不过注意反射机制会被混淆破坏,应该充分回归测试;
- 使用`Android lint` ,剔除没有使用的资源,analyze- inspecting Code
- 清理Assert文件夹, 不编译的文件夹;
- 用`代码代替图片`,shape,RotateDrawable 一张图片,属性动画;layer-list 背景图; 重用资源tint;
- `放弃一些图片资源`, defaultConfig 配置 resConfigs 指定只打包某些资源;
- `压缩图片` [pngquant](http://www.cnblogs.com/soaringEveryday/p/5148881.html)
- `so的优化`,defaultConfig 配置 ndk{} abiFilters 
- 对三方库重新定制,重新打jar包
- 动态加载技术(插件化);

>Android Vitals(google推出的一项改善android设备的稳定性和性能计划):  

包括4大核心指标,其他指标`wifi扫描次数多`,`后台网络使用量高`,`应用启动时间`,`呈现速度缓慢`,`冻结的帧`,`权限遭拒`

- 崩溃率
- ANR
- 唤醒次数过多
- 唤醒锁定被卡住等指标

>唤醒锁定被卡住等指标 

`PowerManager` 可让开发者在设备的显示屏关闭后继续保持cpu运行; 可使用`Partial_WAKE_LOCK`标记的acquire()获取部分唤醒锁定,如果在后台保持了较长时间,则会变成卡住状态(用户看不到应用的任何部分);

- 使用新版`WorkManager` 替换; 
- 确保会释放其获取的所有唤醒锁定;finally;

最佳做法:

- 确保应用的某个部分保留在前台; 启动前台服务,会直观的向用户表名应用在运行;
- 确保获取和释放唤醒锁定的逻辑尽可能简单;

>过多唤醒

`AlarmManager` 可让开发者设置闹钟以在指定的时间唤醒设备; 可使用 `AlarmManager`中某个带有`RTC_WAKEUP`或`ELAPSED_REALTIME_WAKEUP` 标记的set()方法,当唤醒闹钟时,设备会在执行闹钟的onReceive()或onAlarm()方法期间退出低功耗模式并保持`部分唤醒锁定`;

- 使用新版`WorkManager`替换;
- 降低闹钟触发频率;

最佳做法:

- 使用`WorkManger` 替换 AlarmManager 调度后台任务;优势如下: 
	- 批处理 将work合并一起,减少耗电量;
	- 持久性 如果重新启动设备,则调度的workmanager作业会在重新启动后运行;
	- 条件 作业可以根据条件(wlan)运行;
- 使用`Handler`替换AlarmManager调度仅在应用运行期间有效的定时操作;

> ANR

如果应用在界面线程处于阻塞状态的事件过长,会触发ANR(application not response)错误;

排查anr

- 主线程上的耗时操作,跨进程操作耗时长,死锁;
- 启动严格模式
- 开发者选项中启动后台anr对话框;
- TraceView + adb调试;
	- `/data/anr/anr_*`

 查看方法: adb shell cat /data/anr/traces.txt  /mnt/sdcard/traces.txt; adb pull /data/anr/traces.txt /local/traces.txt;

解决问题:

- 主线程上执行速度缓慢的代码 ,io
	- 应该将耗时操作移至工作线程; 线程处理的辅助类;
- 锁争用 1.子线程持有对资源的锁,主线程也获取锁;2. 等待来自工作线程的结果;
	- 确保持有锁的时间降到最低,或者是否需要持有锁
	- AsyncTask;
- 死锁 线程间相互持有对方所需资源的锁;
- 执行速度缓慢的广播接收器 (1.未执行完onReceive;2.PendingResule对象调完goAsync(),未能调用finish())
	- 使用IntentService执行长时间的操作;
	- goAsync()表明需要更多的时间处理消息,不过需要用PendingResult调用finish()回收广播;

>崩溃

排查

- 读取堆栈轨迹;
- 重现bug;

>呈现速度缓慢

低于60帧/s ,即为卡顿;

- 目视检查
	- 运行发布版本;
	- 启动gpu渲染模式分析功能;
- Systrace
	- 显示整个设备在做什么,可识别卡顿;
	- android cpu profiler
- 自定义性能监控
	- 三方库 firebase;

常见卡顿来源

- 可滚动列表;
	- recyclervie 全局刷新
	- 嵌套recyclerview
		- 使用共享回收池
		- layoutmanager `setInitialPrefetchItemCount(int)`表示某个水平行即将显示在屏幕上时,如果界面线程中有空余时间,执行预取该行内容;
	- recyclerview 膨胀过多,创建时间过长  / 布局绘制用时过长
		- 合并视图类型
		- itemview使用ConstraintLayout减少结构视图;
	- recyclervie 绑定时间过长
		- onBindView中执行少量的工作;
	- ListView 扩充
		- 检查viewholder重用机制
- 布局性能
	-  在层次结构的所有叶节点(最低叶节点除外)中避免使用RelativeLayout,避免使用LinearLayout的权重功能;
	-  约束布局

- 渲染性能
	- android cpu profiler
	- 代码优化

- 线程调度延迟
	- binder调用
		- 避免调用
		- 缓存相应值,将工作转移至后台线程;

- 对象分配和垃圾收集
	- android memory profiler

> 应用启动时间

应用有三种启动状态,每种状态都会影响应用向用户显示所需的时间: 冷启动(5s),温启动(2s)或热启动(1.5s);

冷启动: 从头开始启动,系统进程在冷启动后才创建应用进程;

- Zygote进程fork创建一个新的应用进程
- 创建和初始化application,创建入口类activity;
- inflate布局,oncreate/onstart/onresume方法走完;
- 调用setContentView方法,将view添加到Decorview中,调用view的measure/layout/draw方法显示到界面上;

![](https://developer.android.google.cn/topic/performance/images/cold-launch.png)

在 `应用创建`和`Activity创建`可能出现性能问题;

热启动: 系统将activity带入前台; 如果某些内存因`onTrimMemory`被清理,需要创建相应的对象;

温启动: 介于两者之间;
	
- 用户在退出应用后又重新启动应用; 从头开始创建activity.oncreate
- 系统将应用从内存中逐出,重新启动; 进程和activity需要重启,传递到onCreate的已保存的实例state bundle 可用;

检测方式:

- logcat 包含名为`Displayed`的值,代表从启动进程到在屏幕上完成对activity的绘制所用的时间; 包括`启动进程->初始化对象->创建初始化activity ->扩充布局->首次绘制引用` 
- `reportFullyDrawn()` 测量从应用启动到完全显示所有资源和视图层次结构所用的时间;  手动调用: 创建应用对象到调用方法所用的时间;

常见问题优化

- 密集型应用application初始化
	- 延迟初始化对象,仅初始化立即需要的对象。单例和依赖注入;
		- 减少application的oncreate中逻辑或者延迟加载;
			- 可以异步的使用异步,不能异步的尽量延迟;
 			- 耗时任务可以使用异步加载IntentService;
- 密集型activity初始化
	-  布局优化,viewstub
	-  减少onCreate方法工作量,可转移至工作线程或者使用懒加载方式,onWindowFocusChanged方法;
- 带主题背景的启动屏幕,使用主题防止白屏
	- 使用 windowBackground 主题属性预先设置一个启动图片,oncreate之前使用setTheme设置或在清单中设置;

>内存优化 RAM

- onTrimMemory 中 释放内存以响应事件;
- 使用效率更高的代码结构;
	- 使用经过优化的数据容器; sparseArray -> hashMap;
	- 谨慎使用服务,jobScheduler,workManger,IntentService优化;
	- 谨慎抽象,ram映射内存;
	- 避免内存抖动; 内存在短时间内频繁分配和回收
		- 
- 移除会占用大量内存的资源和库
	- 缩减apk大小;
	- 依赖注入dagger2 DI;
	- 谨慎外部库;

- 内存溢出bitmap 
	- bitmap内存及时回收,一般硬件加速或者截屏类的需要recycle()方法回收;recycle() 只回收native部分的内存;
		-  recylce主要是释放`native分配`的内存,但一般情况下图像数据是在jvm中分配的,调用recycle并不会释放这部分内存;如果使用createBitmap创建的bitmap且没有别硬件加速过,recycle产生的意义就比较小,可以不主动调用;而象被硬件加速draw的和截屏这种在jvm中分配内存的需要调用方法来释放图像数据; 
	- 计算下采样 insamplesize 进行图片压缩,修改Config;
	- lru算法;
	- 三级缓存(解决频繁gc导致的stw引起的卡顿)

- 图片优化extra
	- [bitmap占用内存大小计算](https://juejin.im/post/6844904110416723976)
	- 长图加载 使用`BigmapRegionDecoder`加载指定区域; 三方库[BigImageViewer](https://github.com/Piasy/BigImageViewer)
	- 三方库 [glide](https://github.com/bumptech/glide)
	- 压缩算法,[鲁班压缩](https://github.com/Curzibn/Luban);


> 内存泄露,OOM的原因和排查方法

 oom (outofmemoryerror),原因是无法分配内存导致的错误,可由于内存泄漏导致内存溢出;
 
 - 内存泄露
   - static静态变量的持有强引用;
   - handler等非静态内部类的泄露,或者线程类的泄露,activity关闭,handler还有消息循环,内部类默认持有外部类的引用,MessageQueue - Message - Handler - Activity; (使用static 静态类, 弱引用, ondestroy中释放); 及时的cancel;
 	- 资源没有释放;
 
 - 内存优化:
 	- 防止内存泄露;
 	- 使用轻量的数据结构;
 	- bitmap压缩使用,luban压缩; 
 	- bitmap三级缓存,softreference,lrucache,disklrucache;
 	- bitmap内存及时回收,一般硬件加速或者截屏类的需要recycle()方法回收;
 	- 提高对象的重复利用率;
 	- String 的拼接使用Stringbuilder;
 	- 复用系统资源,线程池等;
 
lrucache主要原理是把最近使用的对象用强引用存储在linkhashMap中,将使用少的对象在缓存值达到预设值前从内存中移出;
 
 - 布局优化
   - 较少过渡重绘;
   - 较少xml的inflate时间,使用new View()方式创建;
   - 公用的布局尽可能的重用;

### 如何实现进程保活

  进程有优先级,手机杀进程有内存阈值 `low memory killer`, oom_adj值 越大占用物理内存越多越先被杀,降低oom_adj值就可以保活;

 - Application 中的 `onTrimMemory`中清除对象可提升存活率;
 - 开启一个1像素的Actvity, 系统一般不会杀前台进程,锁屏时开启一个activity,大小1像素透明无切换动画,监听系统锁屏广播,开屏关闭;
 - 前台service,前台进程保活,api<18: startForeground(ID,new Notification())发送空的notification,图标不会显示; api 18,需要提升优先级的serviceA,必须有smallIcon, 启动一个InnerService,两个服务同时startForeground,绑定同一个ID,然后stop innerService,cancel调通知栏图标;
 - 进程相互唤醒;
 - JobScheduler ,workManager,系统自带的;
 - NotificationListenerService 使用系统服务,不过需要权限`BIND_NOTIFICATION_LISTENER_SERVICE`;
 - 后台播放无声音频mediaplayer;

### 性能优化工具  (Android profiler)

> 优化工具TraceView 

 做热点分析,得到单次执行最耗时的方法,执行次数最多的方法;
 
 代码中添加: Debug.startMethodTracing(), Debug.stopMethodTracing();
 
 打开profile- cpu- record- stop- 查看top down/bottom up;

> adb 调试命令;

查看模拟器的sp和sqlist文件,布局嵌套层数,加载时间等;

查看文件: adb shell 进入linux的命令行,data/data/databases;

> layout inspector工具查看嵌套层数;

> mat分析方法以及原理

andrdid studio 采用的是`android profiler` 

- 反复操作,堆的大小一直增大,则内存泄露;
- 保存为hprof文件,使用tools中的 hprof-conv 工具转换;
- 使用android MAT工具检测泄露;

> 使用库leakcanary检测泄露;


### 缓存自己如何实现（LRUCache 原理）

last recently used 最近很少使用;

- 使用LruCache类
	- 内部主要实现为 `private final LinkedHashMap<K, V> map;`
	- key,value都不能为空;
	- put 方法 `trimToSize`方法,如果大于maxSize,开启死循环,遍历移出map的最后一个元素;
		- 主要使用的是LinkedHashMap的`accessOrder`属性,true代表使用最近使用顺序,false表示插入顺序;

![LinkedHashMap整体结构图](https://images2015.cnblogs.com/blog/249993/201612/249993-20161215143120620-1544337380.png)

![循环双向链表](https://images2015.cnblogs.com/blog/249993/201612/249993-20161215143544401-1850524627.jpg) 

注意该循环双向链表的头部存放的是最久访问的节点或最先插入的节点，尾部为最近访问的或最近插入的节点，迭代器遍历方向是从链表的头部开始到链表尾部结束，在链表尾部有一个空的header节点，该节点不存放key-value内容，为LinkedHashMap类的成员属性，循环双向链表的入口。

[linkedHashMap](https://www.cnblogs.com/xiaoxi/p/6170590.html)


- 自定义lrucache算法;  典型例子acache;
	- 使用`<File,Long>`的键值对,记录最后更新的时间,put时先比较limit,大于limit先进行remove时间最老的对象;get更新时间;

### 图形图像相关：OpenGL ES 管线流程、EGL 的认识、Shader 相关

- `GlSurfaceView` 
	- 是一个View,可捕获触摸屏事件,实现触摸监听;
- `GlSurfaceView.Renderer` 类
	- 将Render类设置到GlSurfaceView中使用
		- onSurfaceCreated(): 创建GlSurfaceView调用初始化的方法;
		- onDrawFrame() : 每次重新绘制时调用此方法;
		- onSurfaceChanged: surfaceView大小,设备屏幕方向发生变化时调用的方法;

基本原理: 首先,openGl自身是一个`状态机`, 它的绘制一般都是由一个个三角形构成, 核心是`Shader`着色器; 一般流程为: 顶点数据 -> 顶点着色器 -> 形状(图元)装配 ->  几何着色器 ->光栅化 -> 片段着色器 ->测试和混合 ; 其中 `顶点着色器,几何着色器,片段着色器`可自定义;

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200715114656737.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L01ySmFydmlzRG9uZw==,size_16,color_FFFFFF,t_70)

>gpuImage的使用一般流程:  

GlSurfaceView 主要用于显示; GlSurfaceView.Renderer 用于surfaceview的渲染; GpuImageFilter 用于使用`glsl(opengl shader language)`语言处理着色器;  (GlSurfaceView -> GlSurfaceView.Renderer -> GpuImagerFilter)

### SurfaceView、TextureView、GLSurfaceView,SurfaceTexture 区别及使用场景

[博客: SurfaceView、TextureView、GLSurfaceView,SurfaceTexture 区别及使用场景](https://blog.csdn.net/jinzhuojun/article/details/44062175)

[体系框架-窗口管理子系统](https://blog.csdn.net/jinzhuojun/article/details/37737439)

> SurfaceView extends MockView( extends FrameLayout)

![](https://img-blog.csdn.net/20150304164219975?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvamluemh1b2p1bg==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

[简:] surfaceView是一个有自己的Surface的View; 它的渲染可以放在单独线程中而不是主线程中; 缺点是不能做变形和动画;

>GlSurfaceView extends SurfaceView

surfaceVIew的扩展,加入EGL的管理,自带渲染线程;

> SurfaceTexture  (Captures frames from an image stream as an OpenGL ES texture.)

[简:] SurfaceTexture 可以用作非直接输出的内容流,提供二次处理机会;与SurfaceView直接输出相比，这样会有若干帧的延迟。同时，由于它本身管理BufferQueue，因此内存消耗也会稍微大一些。

> TextureView extends View 

[简:] TextureView是一个可以把内容流作为外部纹理输出在上面的View;本身需要是一个硬件加速层; 事实上TextureView本身也包含了SurfaceTexture; 可以完成SurfaceView+SurfaceTexture类似的功能(内容流上的图像->纹理输出,在单独的Surface上做绘制,可以是用户提供的线程; 另外可以用`Hardware overlay`进行显示); 区别在于TextureView在View hierachy 上做绘制,一般在主线程上做的(5.0后引入渲染线程,在渲染线程上做的); 

### Anr 监测机制

anr 检测监测机制:  android对不同的anr类型 (Broadcast,service,inputEvent)都有一套检测机制;

anr 报告机制机制: 检测到anr以后,需要显示anr对话框,输出日志(发生anr时的进程函数调用栈,cpu使用情况等)

- app层: 应用主线程的处理逻辑;

- framework层 anr机制的核心,主要有ams,broadcastqueue,activeservices,inputmanagerService,inputMonitor,inputChannel,ProcessCpuTracker等;

- Native层 inputDisPatcher.cpp;


##### input系统 的anr

![](https://img-blog.csdnimg.cn/20190115171718338.png)

[android anr](https://www.jianshu.com/p/ad1a84b6ec69)

**anr检测**

其中事件分发5s限制定义在`InputDispatcher.cpp`；InputDispatcher::handleTargetsNotReadyLocked（）方法中如果事件5s之内还没有分发完毕，则调用`InputDispatcher::onANRLocked()`提示用户应用发生ANR；

Activity.onCreate执行耗时操作，不管用户如何操作都不会发生ANR，因为输入事件相关监听机制还没有建立起来；InputChannel通道还没有建立这时是不会响应输入事件，InputDispatcher还不能事件发送到应用窗口，ANR监听机制也还没有建立，所以此时是不会报告ANR的。

**anr报告**

最终都是通过`AppErrors.appNotResponding()`方法向用户报anr;弹出一个对话框,告诉用户当前某个程序没响应和日志输出;

### 音视频(音频解码器,audiotrack,视频解码,opengl绘制,视频编辑转码,视频滤镜)

[//TODO]()

### 动画、差值器、估值器（Android中的View动画和属性动画 - 简书、Android 动画 介绍与使用）

帧动画,补间动画,属性动画,item布局动画,过渡动画;

### Gradle用法（Groovy 语法、Gradle 插件开发基础）

release打包,签名,加固,渠道包配置productFlavors;

 - 统一库的版本号,配置签名信息;
 - 自定义设置打包apk的输出名称;
 - 设置apk的渠道包;

>多渠道打包;

 - gradle中设置 productflavors 和 buildType 结合,类型和渠道不同组合包;
 - androidmanifest文件中加入manifestPlaceHOlder元数据修改和存储当前打包渠道;

 美团打多种渠道包设置,meta-info中建立空文件,不用重新签名,标志一个渠道;

### 热修复 (class dex,classloader原理),热修复(asm插桩,类加载方式,底层替换)

####热修复分类

开源的修复框架: 

- native hook
	- Dexposed | Andfix: 阿里 实时修复;
- Java
	- QFix : 手q 冷启动修复;
	- Robust: 美团 实时修复;
	- Nuwa : 大众点评 冷启动修复;
	- RocooFix : 百度金融 冷启动修复;
	- Aceso: 美丽说蘑菇街 实时修复;
	- Amigo: 饿了吗 冷启动修复;
	- Tinker: 微信 冷启动修复;
- native+java混合
	- Sophix 阿里 收费 实时修复+冷启动修复;

![](https://images2018.cnblogs.com/blog/823551/201803/823551-20180313230542094-924942701.png)


####热修复原理 [热修复原理](https://www.cnblogs.com/popfisher/p/8543973.html)

> **Native Hook**

阿里dexposed,andFix;

- 直接在native层进行方法的结构体信息对换,从而实现完美的方法新旧替换,从而实现热修复功能;
	- 来源于Xposed框架,Aop编程;  在native层获取到指定方法的结构体,然后改变他的nativeFunc字段值,而这个值就是可以指定这个方法对应的native函数指针,所以先从java层跳到native层,改变指定方法的nativeFunc值,然后在改变之后的函数中调用java层的回调即可;实现了方法的拦截功能;
- 基于开源框架xposed实现,是一种aop的解决方案;
- 只hook app本身的进程,不需要root权限;

![](https://images2018.cnblogs.com/blog/823551/201803/823551-20180311132636957-1552751766.jpg)

dexposed优点:  即时生效; 无额外开销; 

dexposed缺点: 不支持art,dalvik上可以,5.0后不能用;无法增加变量和类等限制,无法做到功能发布级别;

andFix优点: 即时生效;支持dalvik和art;

andFix缺点: 面临稳定性和兼容性问题;不支持新增方法,新增类,新增field;

![](https://images2018.cnblogs.com/blog/823551/201803/823551-20180311132712953-354839170.png)

> **Dex插桩方案(java multidex)**

qq空间;

![](https://img-blog.csdn.net/20160314140715580)

`dexElements[]`位于`DexPathList`中, `BaseDexClassLoader.findClass() -> DexPathList.findClass()`;

- 原理是hook了`ClassLoader.pathList.dexElements[]`; 因为ClassLoader的findClass是通过遍历dexElements[]中的dex类寻找类的; 当然为了支持4.x的机型,需要打包的时候进行插桩;
- 越靠前的dex优先被系统使用,基于类级别的修复;

![](https://images2018.cnblogs.com/blog/823551/201803/823551-20180311132727379-906293657.jpg)

优点: 不需要考虑dalvik虚拟机和art虚拟机适配; 代码非侵入式,apk体积影响不大;

缺点: 冷启动有效; dalvik平台存在插桩导致的性能损耗;art平台由于地址偏移导致补丁包可能过大; 虚拟机 在安装期间会为类打上`预校验``CLASS_ISPREVERIFIED`标志是为了提高性能的,但有此flag的类不能引用其他的dex了,否则会抛出`IllegalAccessError`,我们强制防止类被打上标志会影响性能; 但是大项目中拆分dex的问题已经比较严重了,很多类都没有打上这个标记;


 Android系统是通过`PathClassLoader`加载系统类和已安装的应用的。 
  `DexClassLoader`则可以从一个jar包或者未安装的apk中加载dex ;
 
  (**简**: 注意: apk安装时会将dex优化成odex拿去执行,会执行**预校验**`CLASS_ISPREVERIFIED`,具体的校验是a和b类都处于同一个dex,并且直接引用了b,a类别加上此flag,不能引用其他dex的类,否则`IllegalAccessError`; 换句话说，只要在static方法，构造方法，private方法，override方法中直接引用了其他dex中的类，那么这个类就不会被打上CLASS_ISPREVERIFIED标记。
 让所有的类都不要被打上预校验的标记,防止不能跨dex调用,所以使用aop技术修改class文件,在gradle中通过transform 修改,在所有的类的构造函数中加入一行打印逻辑,为了能用其他dex的类,所以最终的dex是二种dex拼接的数组, 一个是补丁包dex,不过补丁包dex中包含一个空类,修改原app的dex调用此空类为了防止预校验的标记的类dex,一种是原app的dex)

>**Instant Run 热插拔原理(java hook)**

美团 Robust;

- Robust插件对每个产品代码的每个函数都在编译打包阶段自动的插入一段代码,插入过程对业务开发完全透明的;
- 编译打包阶段自动为每个class都增加一个类型为`ChangeQuickRedirect`的静态成员,而在每个方法前都插入了使用`ChangeQuickRedirect`相关的逻辑;当 ChangeQuickRedirect不为null时,可能会执行到`accessDispatch`从而替换掉之前老的逻辑,达到fix 的目的;

![](https://images2018.cnblogs.com/blog/823551/201803/823551-20180311132821935-182982828.png)

优点: 几乎不会影响性能(方法调用,冷启动); 高兼容性(只是正常的使用DexClassLoader),高稳定性;补丁实时生效;支持方法级别的修复,包括静态方法;支持增加方法和类; 支持proguard的混淆,内联,优化等操作;

缺点: 代码是侵入式的,会在原有类中加入相关逻辑;so和资源替换不支持;增大api体积;会增加少量方法数,使用Robust插件后,原来能被ProGuard内联的函数不能被内联了;
 
> **Dex替换**

微信Tinker;

- 服务端做dex差量,将差量包下发到客户端,在art模式的机型上本地跟原apk中的classes.dex做merge,merge成为一个新的merge.dex后将merge.dex插入到pathClassLoader 的dexElements,原理类同Q-zone; 为了实现差量包的最小化,Tinker自研了DexDiff/DexMerge算法;Tinker还支持资源和so包的更新,so补丁包使用bsDiff来生成,资源补丁包直接使用文件md5对比来生成;针对资源比较大的(默认大于100kb是大文件)会使用bsDiff来对文件生成差量补丁;

![](https://images2018.cnblogs.com/blog/823551/201803/823551-20180311132842593-173785053.png)

优点: 支持动态下发代码;支持替换so库以及资源;

缺点: 不能即时生效,需要下次启动; 

- Tinker已知问题：

Tinker不支持修改AndroidManifest.xml，Tinker不支持新增四大组件(1.9.0支持新增非export的Activity)；
由于Google Play的开发者条款限制，不建议在GP渠道动态更新代码；
在Android N上，补丁对应用启动时间有轻微的影响；
不支持部分三星android-21机型，加载补丁时会主动抛出"TinkerRuntimeException:checkDexInstall failed"；
对于资源替换，不支持修改remoteView。例如transition动画，notification icon以及桌面图标。

- Tinker性能痛点：

Dex合并内存消耗在vm head上，容易OOM，最后导致合并失败。
如果本身app占用内存已经比较高，可能容易导致app被系统杀掉。

> **混合/优化**

阿里Sophix; 

![](https://images2018.cnblogs.com/blog/823551/201803/823551-20180311132855847-745121352.png)

>>优化Andfix (突破底层结构差异,解决稳定性问题)

由替换内部变量兼容性不好 改为 整体替换的方案,兼容稳定性;

>>突破qq和tinker的缺陷;

![](https://images2018.cnblogs.com/blog/823551/201803/823551-20180311132924162-1647000593.png)

解决方案:

- Dalvik下采用阿里自研的全量dex方案: 不是插桩,而是在原dex中删除(知识删除类的定义)补丁dex中存在的类,这样让系统查找类的时候找不到,只有在补丁中的dex加载到系统中,系统自然就会从补丁包中找到对应的类;
- Art虚拟机下支持多dex的加载,仅仅是把补丁dex作为主dex(classes.dex)而已,相当于重新组织了所有的dex文件: 把补丁dex改名为classes.dex,以前apk的所有dex一次改为classes2.dex,classes3.dex...;

![](https://images2018.cnblogs.com/blog/823551/201803/823551-20180311132939270-1909427180.png)

>>> 资源修复(常用方案 Instant Run)

![常用方案](https://images2018.cnblogs.com/blog/823551/201803/823551-20180311132949831-1148740126.png)

![](https://images2018.cnblogs.com/blog/823551/201803/823551-20180311133010134-1945128534.png)

>> so修复

![](https://images2018.cnblogs.com/blog/823551/201803/823551-20180311133020815-466711993.png)

###  组件化架构思路 (页面路由,cc组件化)

 [CC组件化](https://github.com/luckybilly/CC)

**相同点:** 需要将分布在不同组件module中的某些类按照一定规则生成映射表(数据结构通常是map,key为一个字符串,value为类或对象),然后在需要用到的时候从映射表中根据字符串取出类或对象;

**不同点:**

> 路由机制: 路由的本质是类的查找;

工作机制类似于仓库保管员:  先把类全部放到仓库中,需要的时候,仓库保管员根据所提供的字符串找出存放在仓库中的类;

查找的类主要分为3种: activity子类,fragment子类,自定义接口实现类;

- activity子类: 路由库提供startActivity(startActivityForResult)的封装,并根据字符串从映射表中获取对应的activity类,跳转到该activity页面;
- fragment子类: 路由库根据字符串从映射表中获取对应的fragmnet类并创建一个对象返回给调用方;
- 自定义接口实现类: 路由库根据字符串(注解的字符串或者接口类名字符串)从映射表中获取对应接口的实现类,并创建一个对象返回给调用方;

特点: 调用方便;本质是类查找,需要通信的组件必须要打包在同一个app内部才能获取到; 组件间的服务调用,调用方需要持有接口类,需要将接口类定义下沉到base层,面向接口编程;  单组件运行时,添加依赖后 DDComponentForAndroid 通过插件做代码隔离;


> 组件总线: 组件总线的本质是转发调用请求

工作原理类似于电话接线员(中介者模式): 组件总线负责收集所有组件类并形成映射表(key为字符串,value为组件类的对象); 调用组件时,总线根据字符串找到对应的组件类并将调用信息转发给该组件类,组件执行完成户在通过组件总线将结果返回给调用方;

优点: 组件总线只负责通信,即转发调用请求和返回执行结果; 不需要下沉接口,面向通信协议编程(类似于client 调用server端接口的通信协议);由于组件总线的本质是转发请求,可以通过跨进程通信方式将调用请求转发给其他app,实现跨进程调用; 作为单组件运行时,无需与其他组件一起打包运行,所有的组件都是平行的,无依赖关系(避免组件间的依赖,框架层面做到完全的代码隔离; 组件独立运行更快;); 


### 插件化原理,插件化框架学习

//TODO

就是apk未安装,通过反射,aapt直接获取apk上的字节码和资源,启动act;


###如何判断一个 APP 在前台还是后台？

- application 监听lifecycle监听事件,onStart count++; onstop count--; count为0 时处于后台; 
- 监听home键广播;

###混合开发(rn,fuchsia+flutter+dart,weex,js引擎,渲染引擎)

//TODO

### aop(aspectJ,apt,javassist)

AOP与APT ([aop ](https://brucezz.itscoder.com/use-apt-in-android))

aop 切面编程,oop将问题划分为单个模块;aop就是把涉及到众多模块的某一类问题进行统一管理; 
 
 android aop 就是通过`预编译方式`和`运行期动态代理`实现程序功能的统一维护的一种方式; 
 
 - apt(Annotation processing tool): dagger2,dataBinding,butterknife;
 
 步骤: 1.定义编译期间的注解; 2.继承AbstractProcessor的接口实现编译期间代码逻辑; 3.gradle添加apt的插件以及依赖,使用此注解的地方重新编译即可;
 
 - acpectJ: `advice 通知`,`joint point 连接点`,`pointcut 切点`,`aspect 切面(pointcut+advice)`,`weaving 织入`; 主要原理是动态代理;
 
 - javassist: hotfix,Savior(instantRun); 编译期间修改class文件; 还有就是ASM(组件化CC框架使用的就是这个aop),直接修改编译后的class文件,class- dex文件时处理;
 
 `Transfrom` 也是Task,在项目打包成dex之前会添加到Task执行队列中,刚好可以在Transform api处理 修改class 文件,转化为dex,达到切面编程的目的; 
 
 - 在Transfrom这个api出来之前，想要在项目被打包成dex之前对class进行操作，必须自定义一个Task，然后插入到predex或者dex之前，在自定义的Task中可以使用javassist或者asm对class进行操作。

 - 而Transform则更为方便，Transfrom会有他自己的执行时机，不需要我们插入到某个Task前面。Tranfrom一经注册便会自动添加到Task执行序列中，并且正好是项目被打包成dex之前。

 
 步骤: 1.定义一个插件module; 2.自定义Transform(gradle api中的类);3.在tramsform里处理task,input拿到上一个task处理的结果,处理完毕后输出outputs给下一个task处理;4.transform使用javassist或者asm操作字节码,添加新的逻辑或者修改原有逻辑;5.gradle中添加插件的使用,打包时候运行此task对class进行处理;


![](https://upload-images.jianshu.io/upload_images/751860-0641778f0bc265ad.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/540)

### NDK 相关(cmake,ndkmake)

//TODO

### 监控(apm性能检测,webview性能检测,leakcanary内存泄露检测)

//TODO

### webwiew了解？怎么实现和javascript的通信？相互双方的通信。@JavascriptInterface在？版本有  bug，除了这个还有其他调用android方法的方案吗？

api 17情况下,js 调用java: `webview.addJavascriptInterface(Object obj,String name)` 添加类,方法由@JavascriptInterface注解; 
 js中直接使用window.name.getXX() obj中定义的方法使用;
 
  java调用js: webiview.setWebChromeClient(new WebChromeClient(){});` webview.loadUrl("javascript:toast()");`直接使用loadUrl调用script中的方法;
  
 ----------
 api <17时, 使用上述方法会引起漏洞,17以上通过@JavaScriptInterface修改,17以下使用消息框函数解决;

 `webview.setWebChromeClient(new WebChromeClient(){ 重写onJsPrompt 方法通信})`; 其中,`onJsAlert`,`onJsComfirm`,`onJsPrompt`前两个带boolean,后一个带string,可以传递json数据;

 - 定义json格式的字符串;
 - js中封装一个方法,通过prompt方法`window.prompt(text,defaultText)`实现将string传给java层的onJsPrompt方法中,java对string 进行json 解析,通过反射调用逻辑;
 - java执行完毕,定义回传json,java通过JsPromptResult传给js`result.confirm("callable")`;

### Android权限管理

 android使用 `<user-permission ` 声明权限,6.0以后需要申请动态权限(Runtime Permission);一般分为正常权限和危险权限;

 正常权限:系统会自动向应用授予权限;

 危险权限:涉及用户隐私信息的数据或资源或者可能对用户存储的数据或者其他应用的操作产生影响的区域; 其中所有危险的权限都属于权限组;如果只申请了权限组其中一个权限,另一个权限也会立即授予;

 使用系统的api contextCompat.checkSelfPermission()检查权限,使用ActivityCompat.requestPermission()请求权限;

 ActivityCompat.shouldShowRequestPermissionRationale 返回true:拒绝权限,可以在此弹框;返回false:拒绝权限,不能再次弹框,回调onRequestPermissionsResult方法;可重写判断拒绝或者接受后的逻辑;

### jenkins 持续集成

//TODO

### adb 命令:
  
 - adb devices ;指定设备
 - adb -s 设备id install -r XX.apk ;指定设备号替换安装;
 - adb install -d ;降级安装;
 - adb push/pull 推拉文件;

### android EDA

 Activity.runOnUiThread(Runnable)
 
 View.post(Runnable)
 
 View.postDelayed(Runnable, long)
 
 Handler

### handler原理 (ThreadLocal)

简单来说就是每个线程Loop轮询器,loop中又有一个消息队列, loop处于死循环一直轮询消息队列; 而Thread中有ThreadLocal,存储着该Loop对象,只要把消息发送到ThreadLocal中的loop中的消息队列中,就可以运行消息,注意ThreadLocal特性;

> handler机制原理,为什么内存泄露,继承handler就不会内存泄露;

handler是线程通信机制,`Message`,`MessageQueue`,`Handler`,`Looper`;

Message: 消息分为硬件产生的消息(按钮,触摸),和软件生成的消息;

MessageQueue: 投入和取出消息(mq.enqueueMessage,mq.next)

Handler: 消息辅助类,发送消息和处理相应消息;(sengMessage,handlerMessage)

Looper: 不断循环执行(loop()),按分发机制将消息分发给目标处理者;

在同一线程中android.Handler和android.MessaegQueue的数量对应关系是怎样的？N(Handler)：1(MessageQueue)

分析两套handler消息机制,jni层的handler;c++层的native消息机制是为了epoll机制唤醒消息获取;不阻塞了java层的消息机制也不阻塞;

- android应用程序的消息处理机制由消息循环,消息发送,消息处理三个部分组成:
- android应用程序的主线程在进入消息循环过程前,会在内部创建一个Linux管道(pipe),这个管道的作用是使android应用程序主线程在消息队列为空时可以进入空闲等待状态(pipe是一个文件,两个文件描述符读写,分别是读内容时没有内容等待wait,写内容notify线程),并且使当前应用程序的消息队列有消息需要处理时唤醒应用程序主线程,即linux的epoll机制;(native层基于管道和epoll,java层基于链表)
- 当往android应用程序的消息队列中加入新的消息时,会同时往管道中的写端写入内容,唤醒正在等待消息到来的应用程序主线程;
- 当应用程序主线程在进入空闲等待前,会认为当前线程处于空闲状态,调用那些已经注册了的IdleHandler接口,是应用程序有机会再空闲的时候处理一些事情;(IdleHandler为了Handler线程执行完所有的Message消息时会wait,进行阻塞,提供用来空闲时运行);

>关于IdleHandler:

- 只有当前所有message已经处理完或者待处理的Message还没到时间处理的时候,才会执行IdleHandler一次;
- 通过MeassageQueue.addIdleHandler添加IdleHandler;


 handler是非静态内部类所以其持有当前activity的隐式引用,handler没有被释放,所持有的外部引用也不能被释放;
 MessageQueue-message-handler-activity

>处理方式:

- onDestroy中handler.removeCallBack();
- 将handler声明为静态类,使用弱应用持有activity;静态类不持有外部类的引用,内部类默认持有外部类的引用;

![](http://static.oschina.net/uploads/space/2015/0814/214918_USvs_174429.png)

总结:

 - android消息机制是EDA的机制,即事件驱动机制,也是work-thread机制;主要分为消息分发,消息循环和消息处理三个部分;
 - 消息分发: 通过handler的post或者send方法调用enqueueMessage方法将封装的Message对象放入messagequeue中进行消息循环;
 - 消息循环,在应用进程的入口ActivityThread的main方法中启动looper的循环;android的 消息机制其实是有两套looper机制,一个在jni层,一个在java层,jni层通过linux pipe和epoll机制实现,epoll机制就是在写时notify,读无会wait;java层通过链表实现;当消息分发到消息队列中,会同时向管道中写入内容唤醒handler所处线程处理,当管道中没有消息会wait处于空闲状态,可以执行IdleHandler的逻辑;
 - 消息处理,通过loop循环中的handlermessage处理,运行run方法或者通过what去处理不同逻辑;

总结extra: 

 - ActivityThread 启动时,Looper.loop()启动主looper,开启死循环从messagequeue中获取message,`queue.next()  might block`
   - next方法也是开启死循环(阻塞),调用`nativePollOnce`启动jni层的通信,当jni层消息机制处理完毕,处理java层的消息机制取出`Message`; 此处 如果Message都处理完毕后,会判断是否有IdleHandler ,若有则处理任务; 也就是说Java层的消息循环是由for(;;) 死循环处理的; jni层的消息循环是pipe和epoll机制处理的;
 	 - MessageQueue中`enqueueMessage` 投递`Message`时, 先处理java层的消息机制拼接链表,然后在唤醒jni层的消息机制管道epoll机制处理; 分发`Message`时,先处理jni层的消息机制,然后在处理java层的消息机制;
 	 - 使用取出的 `Message` 中target引用(Handler)调用 `dispatchMessage()`方法分发消息处理;

 - new Handler() 中调用Looper.myLooper() 获取主线程的Looper,获取到 MessageQueue,用于接受message; 
 - 使用post或者send系列方法将Runnable封装成Message或者直接sendMessage 通过`enqueueMessage`将 message(含Handler引用,用于回调) 加入到主线程looper中的MessageQueue(实现为Message的链表,新加的message添加到链表的尾端),调用`nativeWake` 唤醒jni层的消息机制循环; ->回到Looper中的循环;

### Asynctask 原理,AsyncTask是顺序执行么，for循环中执行200次new AsyncTask并execute，会有异常吗

默认使用 `SERIAL_EXECUTOR` Executer,使用队列,顺序执行; 默认线程池为`THREAD_POOL_EXECUTOR`

```
	
	//最小2个线程,最多4个线程,最好是比cpu count 小1的线程数;
	private static final int CORE_POOL_SIZE = Math.max(2, Math.min(CPU_COUNT - 1, 4));
	//总线程数为 cpu 处理器数*2 +1;
	private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;


	/**
     * An {@link Executor} that can be used to execute tasks in parallel.
     */
    public static final Executor THREAD_POOL_EXECUTOR;

    static {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_SECONDS, TimeUnit.SECONDS,
                sPoolWorkQueue, sThreadFactory);
        threadPoolExecutor.allowCoreThreadTimeOut(true);
        THREAD_POOL_EXECUTOR = threadPoolExecutor;
    }

```

可以使用`executeOnExecutor`使用自定义的Executor,可以实现并行运行;

### handlerThread

HandlerThread 继承Thread,内部使用Handler的Thread,有自己的轮询器和消息队列; 使用quit或quitSafety退出消息循环;

```

	 @Override
    public void run() {
        mTid = Process.myTid();
		//初始化当前线程的looper; 内部实现是 sThreadLocal.set(new Looper(quitAllowed))
        Looper.prepare();
        synchronized (this) {
			//返回当前线程的looper,内部实现是 sThreadLocal.get();
            mLooper = Looper.myLooper();
            notifyAll();
        }
        Process.setThreadPriority(mPriority);
        onLooperPrepared();
		//在当前线程中运行消息队列;
        Looper.loop();
        mTid = -1;
    }

```

### intentService,IntentService生命周期是怎样的，使用场合等

内部使用 HandlerThread, oncreate时启动一个HandlerThread子线程运行,使用HandlerThread的looper构建Handler,启动服务时,将任务发送给handler,回调`onHandlerIntent(Intent)`方法,所以运行在子线程,且`handlerMessage`运行完后自动调用`stopSelf`关闭服务;

### view体系(事件分发,滑动冲突,嵌套滑动,自定义动画)

> view的事件传递机制,以及涉及到的设计模式;

activity->phonewindow->decorview->ContentView->viewgroup->view

activty(window) > viewgroup > view <br>
`dispatchTouchEvent` 分发<br>
`onInterceptTouchEvent` 拦截<br>
`onTouchEvent` 触摸事件<br>

onTouch 的执行高于 onClick() 

setOnTouchListener 高于view的onTouchEvent; 

采用组合模式;

### measure,layout,draw 绘制入口流程

[博客: 绘制分析比较好的一篇](https://blog.csdn.net/qq_15893929/article/details/86188240)

ActivityThread 作为应用的入口, 绘制的流程 开始于`ActivityThread.handleLaunchActivity()` -> 调用 `ActivityThread.handleResumeActivity` -> 使用windowmanager(实现类WindowManagerGlobal)的addView方法,addView方法中创建ViewRootImpl类, 调用`setView`方法 -> ViewRootImpl 调用`ViewRootImpl.scheduleTraversals()`,发送`mTraversalRunnable`给主线程hander(FrameHandler 位于编舞者Choreographer) -> 运行run方法中的doTraversal()调用`ViewRootImpl.performTraversals()` -> 此方法封装了measure,layout,draw的绘制流程,分别调用 `performMeasure() 调用 View.measure方法`; `perfromLayout() 调用View.layout()方法`;`performDraw()最终调用 View.draw()方法`; -> 最终调用View的 onMeasure,onLayout,onDraw;


> post 的绘制

在 API Level 24 之前，通过 View.post() 任务被直接添加到 ViewRootImpl 中，在 24 及以后，每个 View 自行维护待执行的 post() 任务，它们要依赖于 dispatchAttachedToWindow 方法，如果 View 未添加到窗口视图，post() 添加的任务将永远得不到执行; 


### 说下Activity的启动方式，生命周期，两个Activity跳转的生命周期，如果一个Activity跳转另一个;  Activity再按下Home键在回到Activity的生命周期是什么样的;

onCreate: 设置布局以及初始化操作; 

onStart:  可见,不可交互;

onResume: 可见,可交互

onPause: 部分可见,不可交互,不能做耗时操作;

onStop: 完全不可见;

onDestroy: 销毁;

onRestart:

a->b <br>

全部不可见:
a oncreate,onstart,onresume,onpause, onstop; a onrestart,onstart,onresume;

部分遮盖:
a oncreate,onstart,onresume,onpause; a onresume;

b oncreate,onstart,onresume; onpause,onstop,ondestry;

按home: 
a oncreate,onstart,onresume,onpause,onstop,onrestart-onstart-onresume;

back键:
a oncreate,onstart,onresume,onpause,onstop,ondestroy;

### 说下Activity的横竖屏的切换的生命周期，用那个方法来保存数据，两者的区别。触发在什么时候在那个方法里可以获取数据等,以及configchange的具体区别;

旋转时的生命周期: 

不设置`android:configchanges`时,切屏会重新调用各个生命周期,切横屏会执行一次,切竖屏会运行两次;<br>
设置Activity的`android:configChange="oriention"`,切屏会重新调用生命周期,横屏和竖屏都会只执行一次;<br>
设置activity的`android:configChange="orientation|keyboardHidden"` 时,切屏不会重新调用生命周期,只会执行onConfigurationChanged()方法;

> 设置一个act为窗口的模式;

android: theme ="@android:style/Theme.Dialog" <br>
android:theme="@android:style/Theme.Translucent"设置透明;

> activity, finish调用后其他声明周期还会走吗;

在oncreate中finish : oncreate->ondestory
onstart方法: oncreate,onstart, onstop,ondestroy;

### 任务栈启动模式详解,Activity的onNewIntent;
   
- 使用“standard”或“singleTop”启动模式的 Activity 可多次进行实例化。“singleTask”和“singleInstance”Activity 只能启动任务且始终位于 Activity 堆栈的根位置。此外，设备一次只能保留一个 Activity 实例，即一次只允许一个此类任务。
- “standard”和“singleTop”模式只有一处不同: <br/>`standard` 创建新的类实例来响应该 Intent; <br/>`singleTop` 栈顶复用,位于栈顶调用`onNewIntent()`否则创建新的;
- “singleTask”和“singleInstance”模式同样只有一处不同:<br/>
	- `singleTask` Activity 允许其他 Activity 成为其任务的一部分。该 Activity 始终位于其任务的**根位置**，但其他 Activity（必然是“standard”和“singleTop”Activity）可以启动到该任务中。Activity一次只能有一个实例; affinity 与 taskAffinity相同的任务栈不存在的情况下新建任务栈;
	- `singleInstance` 与“singleTask"”相同，只是系统不会将任何其他 Activity 启动到包含实例的任务中。它是任务中唯一的 Activity。
- `singletask` **自己理解**就是创建时,先查找其他的任务栈中是否已存在该ActivityA的实例,如果存在将该task置于前台,调用onNewIntent(),也会清除该task中ActivityA上层其他act; 如果没有查找到则创建新的task,并实例化新任务的根 Activity; 
-  **疑问点在于** 官网中描述`FLAG_ACTIVITY_NEW_TASK`等同于`singleTask` ,**个人倾向于** `singletask`等同与`FLAG_ACTIVITY_CLEAR_TOP|FLAG_ACTIVITY_NEW_TASK` intent标记将 singletask 分为两种功能;

存在这样一个情况需要注意, 用户按**返回**按钮都会回到上一个 Activity。 </br>
已存在两个task,如果启动指定`singleTask`启动模式中的某个activity,处于后台的task已存在该activity的实例,系统会将该后台任务**整个**转到前台运行; 如果按返回键,返回的是前台任务中堆栈的act;

![](https://developer.android.google.cn/images/fundamentals/diagram_backstack_singletask_multiactivity.png)

- `FLAG_ACTIVITY_NEW_TASK` : 在新任务中启动 Activity,如果您现在启动的 Activity 已经有任务在运行，则系统会将该任务转到前台并恢复其最后的状态，而 Activity 将在 onNewIntent() 中收到新的 intent。相当于`singleTask`;(**作者注** 官网描述如此,个人觉得有误,`singleTask`觉得更像`FLAG_ACTIVITY_CLEAR_TOP|FLAG_ACTIVITY_NEW_TASK`);
- `FLAG_ACTIVITY_SINGLE_TOP` : 堆栈顶部的 Activity,相当于`singleTop`;
- `FLAG_ACTIVITY_CLEAR_TOP` : 如果要启动的 Activity 已经在当前任务中运行，则不会启动该 Activity 的新实例，而是会销毁位于它之上的所有其他 Activity，并通过 onNewIntent() 将此 intent 传送给它的已恢复实例（现在位于堆栈顶部）。

>`affinity` 属性设置activity的任务栈所属;

 注意singleTask,singleInstance的onActivtyResult方法失效 startActivityForResult resultCode = RESULT_CANCELED; 

失败原因: 使用SingleTask设置taskAffinity属性或者使用SingleInstance 启动模式,因为不是处于同一个任务栈,startActivityForResult失效;

### requestLayout，invalidate，postInvalidate区别与联系

 requestLayout: measure,layout,draw; 触发scheduleTraversal,因为最终调用ViewRootImpl的performTraversal方法;

 invalidate: draw; 触发scheduleTraversal,但是没有设置measure,layout的标记位,不会measure,layout;

 postInvalidate: 非UI线程,异步执行draw;


### 广播 (本地广播)

静态注册,动态注册;
 
 本地广播

  abortBroadcast setResultExtras()

###  service

>生命周期: 
 
- `onStartCommand()` startService()启动无限期运行服务-> onStartCommand(设置的值可让系统终止服务后重启服务)-> stopSelf/stopService 
- `onBind` 绑定服务  bindService 返回一个与客户端通信的IBinder接口; 解绑后销毁
- `onCreate` 首次创建服务;
- `onDestroy` 销毁时回调;


onStartCommand 的返回值: 

`START_NOT_STICKY`: 如果系统在 onStartCommand() 返回后终止服务，则除非有待传递的挂起 Intent，否则系统不会重建服务。

`START_STICKY`: 如果系统在 onStartCommand() 返回后终止服务，则其会重建服务并调用 onStartCommand()，但不会重新传递最后一个 Intent。播放器

`START_REDELIVER_INTENT`: 如果系统在 onStartCommand() 返回后终止服务，则其会重建服务，并通过传递给服务的最后一个 Intent 调用 onStartCommand()。 下载

绑定的生命周期:

`startService -> oncreate(没有运行情况下) ->onStartCommand ` 通信通过Intent; 服务返回结果可通过PengingIntent,传递给服务,服务可通过广播传递结果;

绑定服务

`bindservice -> oncreate -> onbind  -> onUnBind -> onDestroy`

先启动后绑定 `oncreate-> onstartcommand ->onbind -onunbind->onDestroy`
先绑定后启动 `oncreate ->onbind ->onStartCommand ->onUnbind ->onDestroy`

![](https://developer.android.google.cn/images/service_lifecycle.png)

>在service创建子线程优于act

act很难控制,当activity被销毁后,就不能获取到之前创建的子线程的实例,service可以获取方便获取binder实例和子线程等;

>保持service不被杀死: 

1 service设置 START_STICKY; kill后会被重启,但没有intent;

2 提升service的进程优先级 startForeground;

3 ondestory 重启service+ broadcast方式;

### contentprovider

实现数据共享,mContext.getcontentResolver(),提供curd操作;

内容Uri建立唯一标志符,`authority`和`patch` 

 authority 对不同的应用程序做区分; patch 对同一应用程序中不同的表做区分;

所有的curd都一定匹配到`UriMatcher`相应Uri格式才能进行的,隐私数据无法匹配到就无法访问;

>contentProvider ,cotentResolver和contentObserver关系??!

contentprovider 是实现各个应用程序间的跨应用的数据共享,比如日历和联系人信息;

一个应用实现contentprovider来提供内容给别的应用来操作,通过contentResolver操作别的应用数据;

contentObserver 内容观察者,目的是观察特定uri引起的数据库的变化,继而做一些相应的处理;


### intent

intent 同时匹配action类别,category类别,data类别;

### sp  dp ,屏幕适配相关

dpi = 长平方加宽平方的根号/对角线英寸; 每英寸像素

density= dpi/160; 屏幕密度

px = dp * density; 独立像素密度

>sp 独立像素缩放,文字大小;

今日头条屏幕适配方案; 动态修改density值,达到修改dp的值;360dp的效果图作为参考;

 - 图片放在不同像素密度的文件夹`layout`中;
 - 使用不同的限定符,尺寸限定符: 大小会自己适配对应布局; 最小宽度限定符(3.2): 最小宽度大于等于某值的时候,系统选择对应布局;(也可以使用values 将布局分开;) 单面板|双面板;
 - 屏幕方向`限定符`: 布局别名;
 - 自动拉伸位图,drawpatch图;
 - 使用dp,sp单位;
 - 使用`dimens适配`; 建立多个dimens文件放在不同的values中,使用不同的像素/dp适配,缺点就是google推荐使用的是dp,但这个还是px适配;
 - 使用百分比布局,约束布局等新布局方式;


### 启动页缓存设计 白屏问题

 系统启动过程中,首先加载theme资源,加载application,然后加载Activity;

主题中设置style  android:background="@drawable/login"

 主体设置为透明: windowBackground=@color/translucent;
 windowIsTranslucent=true;windowAnimationStyle=android:style/Animation.Translucent;

### recyclerview 四级缓存+局部刷新 RecyclerView与ListView(缓存原理，区别联系，优缺点),RecyclerView缓存原理，局部刷新原理

享元模式,观察者模式

- ListView是两级缓存,RecyclerView是四级缓存; listview通过`mActiveView`和`mScrapViews`实现两级缓存; recyclerview通过`mAttachedScrap`,`mCacheViews`,`mViewCacheExtension`,`mRecyclerPool`;  

 mActiveViews和mAttachScrap功能相似, 在于快速重用屏幕上可见的列表项itemView,不需要重新createView和bindView;
 mScrapView和mCacheViews+mRecyclerPool功能相似,在于缓存离开屏幕的view,让即将进入屏幕的itemview重用,bindView;
 recycelrview的优势在于mCacheView的使用,屏幕外的itemview进入屏幕时无需bindview快速重用; mRecyclerViewPool可以供多个RecycelrView共同使用;

![](./knowjpg/640.png)
![](./knowjpg/641.png)
![](https://blog-10039692.file.myqcloud.com/1502175323258_9041_1502175323346.png)
![](https://blog-10039692.file.myqcloud.com/1502175348747_4325_1502175348864.png)

 - 缓存不同,RList 缓存RecyclerView.ViewHolder(view,viewholder,flag) ,List缓存的是View;

 - 局部刷新,数据源改变时的缓存的处理逻辑,listview是将所有的mActiveViews都移入了二级缓存mScrapViews,Rlist对每个View修改标志位,区分是否重新bindView; Rlist从Recycler中获取合适的view;

 ListView回收机制`RecyclerBin`, 实现两级缓存机制,
 
 - `View[] mActiveViews` 缓存屏幕上的view,在该缓存上的view不需要调用getView; 
 - `ArrayList<View [] mScrapViews` 每个Item Type对应一个列表作为回收站(itemview不能为负),缓存由于滚动而消失的view,此处的view如果被复用,会以参数的形式传给getview重新复用;  布局函数为`layoutChildren()`,fillXXX()对itemview填充,内存调用`makeAndAddView`判断使用哪种缓存view; 

 recyclerView回收机制`Recycler`,实现四级缓存机制,
 
 - `mAttachedScrap` 缓存屏幕上的ViewHolder,该缓存不需要bindView;
 - `mCacheViews` 缓存屏幕外的ViewHolder,默认为2个,不会bindview,会被缓存至mRecyclerPool中;
 - `mViewCacheExtensions` 用户定制,可bindView;
 - `mRecyclerPool`缓存池,可以多个RecycelrView公用,需要bindview重用;默认上限5;  布局函数入口`onLayoutChildren()`,getViewForPosition 判断使用哪种缓存holder; mAttachScrap和mCacheViews就是`ScrapHeap`

 recyclerView 组成 : Adapter ,LayoutManager, ItemAnimator, ItemDecoration;

ItemDecoration : onDraw: 绘制item之前调用绘制分割线;getItemOffset: onMeasure中调用,measureChild,每个item的大小加上装饰的大小; onDrawOver: 绘制item之后调用;

 ItemAnimator : 防闪屏动画 `((SimpleItemAnimator)rv.getItemAnimator()).setSupportsChangeAnimations(false)` 禁用change动画;DefaultItemAnimator 提供多种动画,remove,move,change,add ,顺序执行remove,move+change,add;

 拖拽,侧滑删除: ItemTouchHelper : onSwiped,onMove,getMovementFlags,onSelectedChanged,clearView,isLongPressDragEnabled;

 嵌套滑动机制: 子View实现`NestedScrollingChild`,父View实现`NestedScrollingParent`,RecyclerView实现了`NestScrollingCHild`,CooridnatorLayout 实现了`NestScrollingParent`接口;

###recyclerview的源码及优化:

绘制:

  - measure 中设置 `match_parent` 节省measure的测量工作;
  - layout中,`dispatchLayoutStep` 分三步
  	- dispatchLayoutStep1: pre,选择动画相关fill方法; dispatchLayoutStep2: 真正的布局;fill方法; dispatchLayoutStep3: 触发动画;
  	- `fill`: 有滑动进行一次回收,填充子view,不断添加子view直到 `没有剩余空间`或者`添加的view是focusable且设置stopOnfocusable为true`停止填充; 其中,主要调用`layoutChunk`方法填充,计算剩余空间,有滑动的情况进行回收;
  	- `layoutChunk` 子view处理方法流程: 创建子View -  子View添加到RecyclerView中- 测量子View- 对子View进行布局操作;
  		- 创建子View : `layoutState.next()` 调用 `Recycler.getViewForPosition`方法,而Recycler 就是rlist的缓存关键类,从四大缓存中获取ViewHolder; 没有获取vh,那么调用了`onCreateViewHolder`创建view;接下来调用`bindViewHolder`;设置布局参数;
  		- 添加子View: 回调`onViewAttachedToWindow`方法;
  
  ```
  LinearLayoutManager.layoutChunk() -  Recycler.getViewForPosition() -  Adapter.onCreateViewHolder() -  设置ownerRecyclerView -  Adapter.onBindViewHolder() -  设置LayoutParams -  RecyclerView.addView() -  Adapter.onViewAttachedToWindow() -  LayoutManager.measureChildWithMargins() -  LayoutManager.layoutDecorated()
  ```

核心在于 RecyclerVIew的 `recycler.getViewForPosition`方法,里面定义了适配器的抽象方法的调用;

回收

  用户触发滑动,recyclerview进行一次view的回收,往rlist填充子View,又在进行了一次回收;
  
  滚动的方法调用: 
  
  `onTouchEvent -  scrollByInternal -  LinearLayoutManager.scrollVerticallyBy -  LinearLayoutManager.scrollBy -  LinearLayoutManager.fill`

优化: 

- viewholder 的onCreateViewHolder和onBindViewHolder方法对时间敏感,需要将耗时的操作在recyclerview.setAdapter()之前运行,运行后后传入到adapter里刷新;
- 滑动的onBindView中不处理太多耗时任务,可以在setAdapter之前处理耗时操作,传值到adapter中显示;

- 新增和删除数据通过`DiffUtil`,局部数据刷新;
- `共用recyclerViewPool`,在recyclerview具有相同的adapter,或者相同adaper的子RecycelrView嵌套;
- 处理刷新闪烁,adapter.setHasStableIds(ture);
- getExtraLayoutSpace ,一个itemView就占了一屏大小的recyclerview;

- `onBindviewholder方法不做耗时操作`,只负责视图加载,可以先算好后在,在直接进行显示;
- `点击事件的设置时机`:
   - 给item设置点击事件,onBindViewHolder中设置点击事件滑动时会重复设置,创建大量的匿名对象,所以实现`OnItemTouchListener` 使用`GestureDetectorCompat`这个类实现点击事件和长按事件; 
   - 设置view.setTag保存数据,oncreateViewHolder时通过tag设置onclicklistener,不用在bindview中设置点击事件,只需设置tag中数据,如position; 实现onclicklistener接口,保存处理的值;

- `减少布局嵌套`;
- 设置缓存大小,setItemViewCacheSize();
- 设置Prefetch,预取;

  convertView的复用 对应于 recyclerView的onBindView的item复用;
  
  ViewHolder的使用,findViewById会影响速度,对应于recycleView的ViewHolder;
  
 - 加载图片时,在`onScroll`滑动的时候停止加载图片;

 - 使用分段加载,防止oom; 先加载部分,滑动到底部加载部分;

 - 使用`分页加载`,总的数据太多的情况,分成几页,每一页在使用分段加载;

 思路: 先根据后台分页加载,当page数据大于多少页时,remove先前加载的item至数据库中,保持recyclerview中数据在一定的数量,
 上滑的时候在从数据库中取出显示,remove下方的item至数据库中,但是一旦删除了某个item后,所有数据库的item都要变;

### ViewPager的缓存实现

核心在于viewpager的`populate`方法; 

先使用`offScreenPageLimit`设置startPos,endPos; -> 然后进行两次循环,将不处于startpos-endPos范围内的item移除且进行pageradapter的destroyItem操作; -> 在范围内的调用`addNewItem`,调用适配器pageAdapter的`instantiateItem()`方法,调用fragmentmanager的add方法添加fragment到fragmentmanager中; -> 适配器`setPrimaryItem` 将instantiateItem返回的Object(一般是childView)返回出去,如果我们需要获取ViewPager当前的childView时，我们可以在Adapter中可以复写setPrimaryItem方法将curItem.object保存起来;

```
	
	startPos = Math.max(0, mCurItem - pageLimit);
	endPos = Math.min(N - 1, mCurItem + pageLimit);

	默认pageLimit为1; 
	总共10个item;
	当前处于第5个,position = 4;
	startPos = 3
	endPos = 5;
	缓存数为3;

	pageLimit为2;
	共10个; 当前mCurItem = 4
	startPos = 2
	endPos = 6
	缓存数为5;

	缓存数为 mCurItem + 2*PageLimit

	for (int pos = mCurItem - 1; pos >= 0; pos--)   3..0  移除2..0的item和destroy;
	for (int pos = mCurItem + 1; pos < N; pos++)  5..9  移除6..9的item和destroy;

	
````

1、根据mOffscreenPageLimit计算mItems将要储存ViewPager中startPos~endPos对应的ItemInfo；
2、从mItems中获取当前mCurItem对应的ItemInfo，若没有则通过addNewItem()方法调用Adapter.instantiateItem()为ViewPager添加mCurItem位置的childView。同时创建该位置对应的ItemInfo并添加到mItems中；
3、循环遍历当前item左边的所有item，若不在leftWidthNeeded与startPos-endPos决定范围内，则从mItems中删除，同时调用Adapter.destroyItem()方法来通知Adapter回收itemInfo对应的view。若在该范围内且不存在时则通过addNewItem()方法调用Adapter.instantiateItem()方法 往ViewPager中添加对应mCurItem的childView,同时根据Adapter.instantiateItem()的返回值创建对应的ItemInfo并添加到mItems；
4、循环遍历当前item右边的所有item，逻辑与左循环基本一致；
5、通过以上流程整理好需要加载的mItems后，计算mItems里的每个ItemInfo的偏移量，用来决定layout时的位置
6、最后将以上更新的ItemInfo中的内容更新到对应childView的LayoutParams中



>fragmentpageradapter 和 fragmentstatepageradaper 

区别在于:

- instantiateItem(),state 内部有一个fragment列表,且是可以保存状态的;nostate 直接使用fragmentmanager去findfragment;
- destroyItem(), state会使用fragmentmanager remove掉fragment; nostate 会detach fragment,fragment还是在fragmentmanager中的;


### Android版本特性

 4.4(api 19):  
 
 - `沉浸式体验`;更新nfc,打印框架;转场动画; 
 - `支持两种新的蓝牙配置文件`(Bluetouth HID over GATT较短的延迟时间与低功耗(鼠标,手柄,键盘)连接,Bluetouch MAP与附近的设备(汽车,移动设备)交换信息),这两者作为对Bluetooth AVRCP 1.3的扩展; `HOGP`,`MAP`,`AVRCP`; 
 - `RenderScript,可以原生代码使用` 
 - 红外发射器; wifi TDLS;

 **改变最大** 5.0(api 21): `md设计,过渡动画`,`JobScheduler`
 
 - `material design`,三维视图z视图;
 - activity`共享元素`,无缝状态转换;波纹动画;
 - `引入ART(aot)作为运行时` 取代dalvik(jit) 编译
 - 通知栏改善可悬挂,锁定屏幕显示通知,访客模式,屏幕共享;
 - 新增`蓝牙低功耗BLE`执行并发操作api,实现扫描(中心模式)和广播(外设模式)
 - 全新camera api,可采集YUV和Bayer Raw原始格式;


 6.0(api 23): `运行时权限`
 
 - `运行时权限控制`;
 - 低电耗模式,待机模式; 低电耗: 拔下电源插头,屏幕关闭,休眠状态,定期短时间回复正常工作; 待机: 用户未使用,停用网络访问暂停同步;
 - 选中文本时的悬浮框;
 - 取消apache http,改用`httpUrlConnection`;
 - `硬件标志符访问权` 使用Wlan api 和bluetooth api 的应用,移出对设备本地硬件标志符的编程访问权; 必须拥有Access_fine_location和Access_coarse_location权限;
 - 通知栏改用`Notification.Builder`构建通知;
 ...

 7.0(api 24-25 N): `私有文件访问权限`,手机平板`多窗口支持`

 - `私有文件访问权限`
 - 手机平板`多窗口支持`,tv中是画中画模式;
 - `通知增强`,模板更新, 消息传递样式自定义,捆绑通知,直接回复,自定义视图;
 - 对 ART 添加了JIT即时编译器;
 - 低电耗模式随时随地可以省电,低电耗模式下不会触发android 5.1 或更低版本中的闹铃;提供新的api设置触发闹铃;
 - `后台优化`,android5.0发布以来,`JobScheduler`成为执行后台工作的首选方式;删除三个常用隐式广播`CONNECTIVITY_ACTION`,`ACTION_NEW_PICTURE`,`ACTION_NEW_VIDEO`,迁移到jobScheduler;
 - 优化SurfaceView,电池性能优于TextureView;
 - 流量节省程序,扩展ConnectivityManger,检索流量节省程序首选项
 - 自定义快速设置
 - webview可以选择chrome实现;
 - 网络安全性配置 https;
 - 新的签名方案 APK Signature Scheme v2; 可关闭 ` v2SigningEnabled false`
 - VR支持;
 - 虚拟文件;

 8.0(api 26): `通知渠道 NotificationChannel `,`自适应启动图标`,`刘海屏适配`

 - `通知增强`,增加通知渠道,通知标志,休眠,通知超时,通知设置,通知清除,背景颜色,消息样式;
 - 自动填充框架;
 - 手机可使用`画中画模式`PIP,多窗口生命周期;
 - 可下载字体;可使用xml的字体文件(font);
 - 自动调整textView大小
 - `自适应启动器图标`;颜色管理,使用`res/mipmap-anydpi-v26文件夹`
 - `webview增加多种api`,增强应用稳定性;
 - 7.1之前最大屏幕纵横比为1.86,8.0后无最大纵横比,可自定义maxAspectRatio;
 - 统一布局外边距和内边距; 
 - 声明应用类别;`android:appCategory`
 - `AnimatorSet` 支持寻道和倒播功能;反向播放同一个动画即可;
 - 内容提供者分页,ContentProviderClient;内容刷新,重写contentprovider的refresh()方法;
 - `JobScheduler `改进:
  - 将工作队列和计划作业关联.`enqueue`工作项添加到作业的队列中;作业运行时将待定工作处理,代替一些启动后台服务的情况;
  - JobInfo.BUilder.setClipData()的方式将ClipData与作业关联;
  - 计划作业支持多个新的约束条件,存储空间和电池电量和网络类型;
 
 - 媒体增强,VolumeShaper,MediaPlayer,MediaRecorder...;
 - WLAN感知,蓝牙支持AVRCP,BLE5.0,Sony LDAC编解码器集成到蓝牙的堆叠中;
 - 智能共享,智能文本选择,独立音量
 - `指纹手势`
 - 添加电话相关权限`ANSWER_PHONE_CALLS`;


 9.0(api 28): `显示屏缺口显示`,`通知渠道,通知notificationmanager`,`imageDecoder解码,AnimationImageDrawable 绘制webp`

 - wifi 室内定位;
 - `显示屏缺口支持`
 - 提升短信体验;
 - 渠道设置,广播和请勿打扰;
 - 多摄像头支持;
 -  ImageDecoder 取代BItmapFactory和Options api;
 - 动画 AnimatedImageDrawable ;

### 自定义view的一般流程;

 - 继承于view,viewgroup,重写构造方法,修改this,使用一个构造方法;
 - 在构造方法中,获取自定义属性attrs,设置默认值;
 - 重写onMeasure方法,测量大小;
 - 重写onLayout方法,布局位置;
 - 重写绘制方法onDraw,绘制;
 - 重写事件方法,dispatchTouchEvent,onInterceptTouchEvent,onTouchEvent;
 - 一般还会重写 onFinishInflate,及onSizeChanged

- ACTION_CANCEL什么时候触发，触摸button然后滑动到外部抬起会触发点击事件吗，在+ + 滑动 回去抬起会么

 滑出控件之外的时候手指离开触发cancel事件,可作为actionup事件看待;

- Canvas的底层机制，绘制框架，硬件加速是什么原理，canvas lock的缓冲区是怎么回事

### 获取view的宽高;

  1.通过设置view的MeasureSpec.UNSPECIFIED来测量;
  
```
//wrap_content: MeasureSpec.makeMeasureSpec((1<<30)-1),MeasureSpec.AT_MOST);
int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
view.measure(w, h);
//获得宽高
int viewWidth=view.getMeasuredWidth();
int viewHeight=view.getMeasuredHeight();
```

 2.通过ViewTreeObserver.addOnGlobalLayoutListener来获得宽高,当获取正确的宽高后,移除观察者,回调多次;
`view.getViewTreeObserver().addOnGloballayoutListener()`

 3.view.post(new runnable());  
 
 4.onWindowFocusChanged(0);

### 进程通信相关知识 Bindler

> Android 为啥会采用 Binder？Binder 的机制又是怎样的？binder进程的优点，为什么只copy一次数据; Binder机制，共享内存实现原理;

诸如此类问题都与binder相关;

- 为什么 Activity 间传递对象需要序列化？
- Activity 的启动流程是什么样的？
- 四大组件底层的通信机制是怎样的？
- AIDL 内部的实现原理是什么？
- 插件化编程技术应该从何学起？等等...


 linux 现有的所有进程ipc方式为: `管道,消息队列,共享内存,套接字socket,信号,信号量`;
 
 binder基于cs结构,指client-server结构 `结构稳定`,传输过程数据拷贝一次,`性能效率高`;为发送方添加Uid/Pid,`安全性高`;

![](https://pic2.zhimg.com/80/v2-30dce36be4e6617596b5fab96ef904c6_720w.jpg)
 
 [binder 机制原理](https://zhuanlan.zhihu.com/p/35519585)

- 传统linux 进程ipc:

 - 进程隔离;
 
进程和进程间的内存是不共享的,ab进行数据交互,需要ipc(inter-process communication);
 
 - 进程空间划分,相互隔离;
 	- 用户空间 User Space 用户程序运行的空间;
 	- 内核空间 Kernel Space 系统内核运行的空间;
 
 
 - 系统调用,用户空间访问内核空间资源唯一方式,linux 两级保护机制,0级供系统内核使用(内核态),3级供用户程序使用(用户态);
 	- 用户态  进程在执行用户自己的代码的时候,处理器处于3级;
 	- 内核态  进程执行系统调用而陷入内核代码中执行,处于0级;
 
`copy_from_user()` //将数据从用户空间拷贝到内核空间

`copy_to_user()` //将数据从内核空间拷贝到用户空间
 
 **传统的ipc通行原理**: 将发送方的数据存放在`内存缓存区`中,通过系统调用进入内核态,内核程序在`内核空间`分配内存,开辟内核缓存区,将数据从用户空间的内存缓存区拷贝到内核空间的缓存区;
 
 A进程内存缓存区- 内核缓存区- B进程内存缓存区;2次数据拷贝;
 
 ![](https://pic1.zhimg.com/80/v2-aab2affe42958a659ea8a517ffaff5a0_hd.jpg)


> **binder ipc 实现原理**:  

`动态内核可加载模块` && `内存映射`

 - 基于linux的`动态内核可加载模块`机制: android系统通过动态添加一个内核模块运行在内核空间,用户进程之间通过这个内核模块作为桥梁来实现通信;   在android系统中, 这个运行在内核空间,负责各个用户进程通过BInder实现通信的内核模块就是`Binder Driver`;
 
 - linux `内存映射`: `mmap()` 内存映射方法,将用户空间的一块内存区域映射到内核空间;映射建立后,用户对内存区域的修改可以直接反映到内核空间,反之也成立; 能有效减少拷贝次数;
 
>一次完整的binder ipc通信: 

 	1.binder驱动在内核空间创建一个`数据接收缓存区`;
 	2.内核空间开辟一块内核缓存区,建立`内核缓存区`和`内核中数据接收缓存区`之间的映射关系,以及`内核中数据接收缓存区`和`接收进程用户空间地址`的映射关系;
 	3.发送方进程通过调用copyfromuser() 将数据拷贝到内核中的`内核缓存区`,由于存在映射,相当于把数据发送到接收进程的用户空间,完成一次进程间的通信;

![](https://pic4.zhimg.com/80/v2-cbd7d2befbed12d4c8896f236df96dbf_hd.jpg)

> **binder 通信模型**

**Client Server ServiceManager BinderDriver**

 binder基于c/s结构; client进程和server进程和servicemanager都运行在用户空间,binder driver 运行在内核空间; 
 
 servicemanager (DNS)和binder driver(路由)是由系统提供,client 和server 是由应用程序实现;

- Binder驱动

负责进程间binder通信的建立;

- ServiceManager 和实名Binder

将字符形式的Binder名字转化为Client中对该Binder的引用,使得Client能够通过Binder的名字获取对Binder实体的引用; 注册了名字的 Binder 叫实名 Binder;server 创建binder 将这个BInder实体连同名字一起以数据包的形式通过BInder驱动发送给ServiceManager,注册一个binder,serviceManager相当于一个注册中心;

其中,servicemanager是一个进程,server是另一个进程 ,所以server注册binder时也是通过ipc方式; 这就好比鸡生蛋,蛋生鸡的问题一样;   servicemanager 与其他进程也是采用Binder通信;  servicemanager是Server端,所有其他的Server或者Client相对于ServiceManager都是一个Client;  servicemanager有自己的特殊的Binder,其他client,都需要通过这个Binder的引用来实现Binder的注册,查询和获取;

servicemanager的这个`特殊的Binder`,没有名字也不需要注册; 当一个进程使用`BINDERSETCONTEXT_MGR`命令将自己注册成servicemanager时,binder驱动会自动为它创建Binder实体(预先造好的鸡); 其次这个Binder实体的引用在所有Client中都固定为0而无需通过其他手段获得; 

也就是说: 一个 Server 想要向 ServiceManager 注册自己的 Binder 就必须通过这个 `0 号引用`和 ServiceManager 的 Binder 通信。
 
- Client 获得实名Binder的引用

server向servicemanager注册了Binder以后,Client 可以通过名字获得Binder的引用; Client 也是利用保留的`0号引用`向servicemanager请求访问某个Binder; servicemanager收到请求后从请求数据包中取出Binder名称,在查找表中找到对应的Binder引用作为响应发送给请求的client; 


![](https://pic3.zhimg.com/80/v2-729b3444cd784d882215a24067893d0e_hd.jpg)

> **binder 通信过程** (与Dubbo 好像!!)

- 一个进程使用 `BINDERSETCONTEXT_MGR`命令通过binder驱动将自己注册成为servicemanager; 

- server 通过binder驱动(0号引用的binder) 向 servicemanager 中注册Binder(`Server中的Binder实体`),binder driver 为binder创建内核中的实体节点和servicemanager对实体的引用;将名字和引用传给servicemanager查找表;

- client使用名字,通过binder驱动从servicemanager中获取到对Binder实体的引用,通过这个引用实现与Server的通信;

![](https://pic4.zhimg.com/80/v2-67854cdf14d07a6a4acf9d675354e1ff_hd.jpg)

> Binder通信中的代理模式

![](https://pic2.zhimg.com/80/v2-13361906ecda16e36a3b9cbe3d38cbc1_hd.jpg)

> Binder的完整定义

- 从进程通信的角度看,binder是一个进程间通信的机制;
- 从Server角度: binder是Server中的binder实体对象;
- 从Client角度: binder是对Binder代理对象,是Binder实体对象的远程代理;
- 从传输过程角度: binder是可跨进程传输的对象;

### Binder的构成有几部分？

进程空间中分为用户空间和内核空间,用户空间的内容不可享,内核空间的数据可共享,为了保证安全性和独立性,一个进程不能访问另一个进程,bindler就是充当两个进程间(内核空间)的通道.binder跨进程机制模型基于Client-Server模式;

定义四个角色: Server ,Client ,ServiceManger, Binder驱动;

server: 提供服务的进程;

client: 使用服务的进程;

serviceManger: 管理Service的注册和查询;

binder驱动: 虚拟设备驱动,连接3者的桥梁; 传递进程间的数据,实现线程控制;

binder驱动和servicemanager属于android基础架构,client进程属于android应用层(开发者实现),开发者只需自定义client和server进程并显示使用3个步骤(注册服务,获取服务,使用服务),最终借助android的基本架构功能就可完成进程间通信;

### binder的优点

 高效(数据拷贝一次),稳定(基于CS结构),安全性高(Uid/Pid)

 对比Linux的其他进程通行方式(管道,消息队列,共享内存,信号量,socket),binder机制优点: 
 
 高效 : 
 
 1. binder数据拷贝只需要一次;
 
 2.通过驱动在内核空间拷贝数据，不需要额外的同步处理;
 
 
 安全性高: 
 
 binder机制为每个进程分配了`UID/PID` 作为鉴别身份的标志,并且在binder通信是会根据UID/PID进行有效性检测;
 
 1. 传统的进程通信方式对于通信双方的身份并没有做出严格的验证,Socket通信 ip地址是客户端手动填入，容易出现伪造
 
 使用简单:
 
 1. 使用CS架构;
 
 2.实现 面向对象 的调用方式，即在使用Binder时就和调用一个本地对象实例一样
 Binder请求的线程管理

 server进程会创建很多进程来处理binder请求,管理binder模型的线程采用binder驱动的线程池,并由binder驱动自身进行管理,而不是由server进程管理;一个进程的binder线程数默认最大为16,超过的请求会被阻塞等待空闲的binder线程;所以在进程间通信处理并发问题时,如使用contentprovider时,它的curd方法只能同时有16个线程同时工作;

### aidl

  AIDL（Android Interface Definition Language,AIDL）是Android的一种接口描述语言;编译器可以通过aidl文件生成一段代码，通过预先定义的接口达到两个进程内部通信进程的目的(简单来说,就是在app里绑定一个其他app的service,交互);
  创建aidl步骤: 
 
 1.创建一个aidl文件

 2.在aidl文件中定义一个我们要提供的接口

 3.新建一个service，在service里面建一个内部类，继承刚才创建的AIDL的stub类，并实现接口的方法，在onbind方法中返回内部类的实例。
 
 使用aidl步骤:
 
1.将aidl文件拷贝到src目录下，同时注意添加到的包名要求跟原来工程的包名严格一致
2.通过隐式意图绑定service
3.在onServiceConnected方法中通过Interface.stub.asinterface(service)获取Interface对象
4.通过interface对象调用方法

aidl通过binder实现的,生成相关的binder类,stub就是binder类;

### android 的几种进程

 前台进程: 即与用户正在交互的act和act用到的service,优先级最高,最后被杀死;

 可见进程: 处于暂停状态的act或者绑定的service,即被用户看见,但失去焦点不能交互;

 服务进程: 运行着startService方法启动的service,不可见,但是是用户关心的,音乐或者下载文件;

 后台进程: 其中运行着执行onStop方法而停止的程序,不是当前用户关心的,后台挂着的qq;

 空进程: 不包含任务应用程序和程序组件的进程;

避免被杀死:
 
 1. 调用`startForeground`,让service所在的线程成为前台进程;

 2.Service的onStartCommond返回`START_STICKY`(无Intent值,重启)或`START_REDELIVER_INTENT`(有intent值,重启)(还有一种是`START_NOT_STICKY`(不重启))

 3.Service的onDestroy里面重新启动自己


>使用服务而不是线程?

进程中运行着线程,android应用程序把所有界面关闭时,进程还没有被销毁,处于空进程状态,很容易被销毁; 服务不容易被销毁,如果非法状态下被销毁了,系统会在内存够用时,重新启动;

服务进程优先级比空进程优先级高,不容易被销毁,拥有service较高的优先级;


-----------

# android framework

###Linux的fork

 通过系统的调用创建一个与原来进程完全相同的进程;n次的fork循环,最终创建了2n-1个子线程;
  fork函数执行成功后,出现两个进程,fork返回的值判断是否是父子进程;fpid(父进程Id)!=0 为父进程(相当于链表);##

### android应用进程启动过程，涉及的进程，fork新进程(Linux);  手机进程的启动过程;  详细描述应用从点击桌面图标到首页Activity展示的流程(应用启动流程，Activity的Window创建过程) App 启动流程;

 android基于linux,基于linux的init进程创建出来;

 - Init进程 - 启动 Zygote进程- fork 出SystemServer进程- 开启 应用进程;
 - SystemServer进程中启动系统的各种服务(AMS,PMS,WMS...);
 - 所有进程的父进程为Zygote进程,fork出SystemServer进程;
 - systemServer 启动主looper,创建Context上下文,创建SystemServiceManger,依次调用startBootStrapService- startCoreService- startOtherService;


>Zygote 进程启动流程: ZygoteInit main方法;

- `init.rc`脚本启动zygote进程;
- 创建一个Server端的socket,开启loop等待;
- 等待systemServer进程启动后,在这个serversocket上等待systemserver的ams请求zygote进程来fork新的应用程序进程;


>SystemServer 进程启动流程: SystemServer main方法中调用run初始化:

- zygote进程fork 出systemServer进程;
- systemserver 启动Binder线程池,用于与应用进程进行ipc通信;
- 创建systemservermanager,运行`startBootStrapServices,startCoreServices,startOtherService`等方法启动各种manager,如ams,pms,wms;

![](https://upload-images.jianshu.io/upload_images/2156477-6f6ad71a21ff1d02.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/950)

>launcher启动过程

launcher应用程序 即为显示系统中已经安装的应用程序; 会请求pms已安装的应用程序信息,作为android系统的启动器;

- SystemServer进程会启动pms,pms启动后会将系统中的应用程序安装完成;
- 在此之前已经启动的Ams会将launcher启动起来;启动入口为ams的systemReady方法,最终会调用ams的`startActivity`方法;
- 最终就是启动了`com.android.launcher.Launcher`的activity;即android系统的桌面;

>android应用程序的启动过程

- ams发送启动应用程序进程请求;
- zygote接受请求并创建应用程序进程;

总的来说 Ams 再启动应用程序前会检查应用程序进程是否存在,不存在 systemserver进程使用socket与zygote进程通信通知zygote进程fork一个应用程序进程; Ams通过调用`startProcessLocked`方法向zygote进程发送请求; 启动应用进程的ActivityThread;

[startProcessLocked 的调用处](https://www.shennongblog.com/android-app-startprocesslocked/)

![](https://www.shennongblog.com/wp-content/uploads/2018/12/20190117131820233.png)

###   Activity的启动流程 Launcher-  ActivityManager, 及销毁; Activity 启动流程

>根activity 启动中涉及的进程

涉及到四个进程 `zygote`,`systemServer`,`launcher进程`,`应用程序进程`;

- launch进程 向 systemServer进程 请求创建根activity;
- systemServer进程的AMS 判断是否存在
	- 存在,systemserver进程与应用程序进程ipc,AMS<->ApplicationThread,请求创建根act;
	- 不存在,ams 通过`startProcessLocked`请求zygote fork应用程序进程; 创建完成后在Ams通过binder通知ActivityThread去启动;

>普通activity启动

- 调用context.startActivity()方法; 最终通过binderipc 交由AMS startActivity方法运行; ActivityMangerNative.getDefault()[即ActivityManger.getService(),也即AMSProxy,真正的实现类为AMS.startActivity();
- AMS 经过一系列的判断 最终交由`ActivityStackSupervisor`的realStartActivity方法,调用`ApplicationThread.scheduleLaunchActivity`方法; applicationThread 是ActivityThread内部类,负责与Ams的binder通信;
- 调用ScheduleLaunchActivity 后 发送到主线程Handler,调用`handleLaunchActivity`,这个方法即为启动act的方法;

![](https://upload-images.jianshu.io/upload_images/2156477-37faec1410fbe670?imageMogr2/auto-orient/strip%7CimageView2/2/w/856)
![](https://upload-images.jianshu.io/upload_images/2156477-b29c71ab414e6d02.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/678)

**总结:** 

android系统启动(1,2,3,4) -> android 应用程序启动{根activity的启动}(5,6) -> activity创建(分为两种,此处特指普通activity的启动)(7,8,9);


1 android基于linux,由linux的init进程通过android脚本`init.rc`启动zygote进程;

2 zygote(zygoteInit main方法)进程启动后,创建serversocket并开启loop循环; fork 一个systemServer进程,启动Binder线程池;

3 systemserver进程启动后,创建system context,创建systemservermanager,然后调用 `startBootstrapService,startCoreService,startotherservice`来启动一系列系统manager,如ams,pms,wms等; 

4 systemserver进程启动了pms,会将系统中的应用程序安装完成,ams 调用`systemReady`方法启动Launcher进程的主界面activity(这个过程和启动应用程序进程的activity类似);

5 启动根activity过程(启动应用程序进程),这里涉及到四个进程,分别是`launcher,systemserver,zygote,应用程序进程`;launch向systemServer(Ams) (startActivity)请求创建根Activity;

6 最终调用Ams的startActivty方法, Ams 判断不存在应用程序进程,调用`startProcessLocked`方法 请求zygote进程(socket通信) fork一个应用程序进程, 应用程序进程创建后,建立Ams和应用程序进程的binder通信,AMS <-> ApplicationThread(ActivityThread内部类),运行ActivityThread的main方法 -> 调用ams.attachApplication最终调用 `ActivityStackSupervisor.realStartActivityLocked`方法 调用applicationThread(ActivityThread)`scheduleLaunchActivity`方法执行生命周期方法和view绘制;

7 普通activity启动过程 就是调用`Activity.startActivity`方法, 实际上ipc通信调用的是AMS的startActivity方法,最终调用了`ActivityStackSupervisor.realStartActivitylocked`; 然后调用 与Ams通信的ApplicationThread(ActivityThread内部类)的`scheduleLaunchActivity`;

8 scheduleLaunchActivity 发送启动activity的消息到主线程handler中,调用`handleLaunchActivity`方法,handlelaunchactivity方法调用 performLaunchactivity 方法运行onCreate,onStart方法; handleResumeActivity方法运行onResume方法并负责添加windowmanager绘制流程;

9 handleResumeActivity 获取Activity的WindowManager(实际的实现是WindowManagerGlobel,被代理对象) 调用 addView方法,内部构建ViewRootImpl对象,会调用ViewRootImpl的`scheduleTraversals(requestLayout)`方法,发送TraversalRunnable到主线程handler中;这个runnable会调用performTraversal方法; 然后调用performMeasure,perfromLayout,performDraw方法,最终运行了View的measure,layout,draw方法,然后运行View的onMeasure,onLayout,onDraw方法; 
(windowmanager (windowManagerGlobal,其实准确来说这是桥接模式) 在addview时使用ViewRootImpl的requestLayout去绘制界面;接着通过windowSession 完成window的添加工作,windowSession <-> WMS的Session进程ipc通信; 内部交由wms去处理添加window;)


```

	//-------------------ActivityStackSupervisor.java----------------------
	void startSpecificActivityLocked(ActivityRecord r,
            boolean andResume, boolean checkConfig) {
        ...
        if (app != null && app.thread != null) {//applicationThread
            try {
                ...
				//开启activity生命中后期及渲染; 通过ipc ApplicationThread.scheduleLaunchActivity
                realStartActivityLocked(r, app, andResume, checkConfig);
                return;
            } catch (RemoteException e) {
                Slog.w(TAG, "Exception when starting activity "
                        + r.intent.getComponent().flattenToShortString(), e);
            }

            // If a dead object exception was thrown -- fall through to
            // restart the application.
        }
		//开启应用进程;
        mService.startProcessLocked(r.processName, r.info.applicationInfo, true, 0,
                "activity", r.intent.getComponent(), false, false, true);
    }

```

### activity view 的绘制流程(activity- phonewindow- decorview- titleView+contentView)

 ActivityThread ,main主要是 attach()方法 启动activity还是启动应用进程,启动当前应用进程的looper循环;

 - activitythread会继续和ams通信,发送message至主线程的handler H中,最终通过AMS(ActivityStackSupervisor类)发送`LAUNCH_ACTIVITY`消息调用ActivityThread `handleLaunchActivity()`方法启动activity;
 - 调用performlaunchActivity,最终调用Activity的`onCreate`方法;
 - `setContentView` 中建立DecorView,添加至PhoneWindow中;加载自定义的布局;
 - handleLaunchActivity中调用handleResumeActivity方法,调用windowmanager addView ,wm中建立`ViewRootImpl`,此类负责具体的绘制流程,调用requestLayout- scheduleTraversals- 发送TraversalRunnable- 最终调用`performTraversals`方法绘制;
 - 最终调用activity 的`makeVisble`显示在页面上;

 总结:athread 建立与systemserver的 AMS的ipc通信,启动looper循环; Ams调用方法发送`Launch_activity`消息至athread的handler消息队列中,启动activity的创建过程handlerlaunchactivity方法,运行oncreate ,onresume等方法; oncreate中调用setContentView方法设置自定义布局且建立decorview添加至phonewindow中;onresume中 调用windowmanager 的addView方法 ,其中生成`ViewRootImpl`,调用`performTraversal`方法负责具体的绘制功能measure,layout,draw;最终调用activity的makeVisible显示activity;

### View.post 为什么可以拿到宽高？

 1.注意,必须在`dispatchAttachedToWindow`后运行;内部是在`onAttachToWindow`方法后执行,否则oncreate子线程不会执行;

 2.在performTraversals()被执行的runnable事实上是被主线程的handlerpost到执行队列里了;

 android是`消息驱动的模式`(EDA),view.post的runnable会被加入任务队列,并且等待第一次的`TraversalRunnable`执行结束后才执行,此时已经执行过一次measure,layout过程了;所以在后面执行post的runnable是,已经有measure的结果,因此可以获取view的宽高;

 getRunQueue().executeActions(attachInfo.mHandler);在performTraversals()方法中,performTraversals是由另一个消息去驱动执行了,就是TraversalRunnable,我们post的runnable就是等待TraversalRunnable执行完后才去执行;而traversalRunnable进行measure,layout,draw,所以执行我们的runnable时,此时的view就是measure过了;

### ContentProvider的底层实现

在ContextImpl中创建,实现为applicationContentResolver (extend contentResolver) 

![](https://img-blog.csdn.net/20160608135556716)

contentprovider没有发布的化,启动contentprovider所在的那个进程;即从应用启动入口main方法进入;

![](https://img-blog.csdn.net/20160608143836329)

 contentprovider实现是跨进程调用,首先当前进程通过ActivityThread跨进程AMS通信获取contentprovider,如果contentprovider还没有被创建,则调用startProcessLocked方法,启动contentprovider所在的进程,获取到contentprovider去curd; 其中,启动contentprovder所在进程从main进入,也是通过AMS的RPC机制创建contentprovider;


###  AMS 、PMS , wms

AMS,pms,wms 都存在于systemServer进程中; 

ams 负责启动四大组件;  和zygote进程通信;

pms 负责安装软件;

wms 负责窗口管理;
 

###为什么使用 Parcelable，好处是什么？

parcelable 的作用: 永久保存对象,保存对象到本地文件中; 序列化对象在网络中传输;序列化在进程间传递对象;

serializable 是java的实现方式,两者都可以实现序列化;  serializable 可能会有频繁的io操作,产生大量临时对象频繁gc,parcelable效率更高;

一般内存的时候,使用parcelable; 存储在磁盘上,使用serialable,数据可能会持续变化;

### Android 图像显示相关流程，Vsync 信号等

### apk安装流程

![https://upload-images.jianshu.io/upload_images/2095550-431f514ca7e648a1.png?imageMogr2/auto-orient/strip|imageView2/2/w/949/format/webp](https://img-blog.csdnimg.cn/20200830142400207.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L01ySmFydmlzRG9uZw==,size_16,color_FFFFFF,t_70#pic_center)

[app安装流程](https://www.jianshu.com/p/39402a37f705)

context调用 getPackageManager方法返回packageManager,具体实现为 `applicationpackagemanager`对象,最终会调用 pms (systemserver)`installPackageAsUser()` 进行安装; ApplicationPackageManager <->PMS 


### contentprovide 全方位解析

### broadcastReceiver 全方位解析 

### window 和windowmanager (pop与dialog区别)

[window windowmanager](https://www.jianshu.com/p/7e589ddb634a)

window 是一个抽象类,phoneWindow是唯一实现类;

windowmanager 创建一个window需要通过windowmanager,window的具体实现在windowManagerService中,ipc通信;

每个window 都对应一个View和一个ViewRootImpl ,view和window通过ViewRootImpl连接,window实际上以view的形式存在; WindowManger也是一个接口,实现类为WindowMangerImpl,代理 windowmanagerGlobal; WindowManagerGlobal 是一个单例;  主要通过ViewRootImpl 来更新界面;

windowmanager (windowManagerGlobal,其实准确来说这是桥接模式) 在addview时使用ViewRootImpl的requestLayout去绘制界面;接着通过windowSession 完成window的添加工作,windowSession <-> WMS的Session进程ipc通信; 内部交由wms去处理添加window;

### Context 相关知识

>Context 继承树;

![](https://img-blog.csdn.net/20151022212109519?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQv/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

总共的context数量为 application + activity + service 的数量;

ContextWrapper的 `mBase`的Context 在 ActivityThread的performLaunchActivity中,构建ContextImpl ,并在创建Activity后 调用`activity.attach()`方法通过 `attachBaseContext` 设置ContextImpl 于ContextWrapper中保存; 所以ContextWrapper使用的就是ContextImpl对象;

> 为什么receiver 调用startActivity(),如果不加New_task标志 会抛异常,而activity没事?

receiver的Context和activity的Context都 继承于ContextWrapper,ContextWrapper.startActivity的方法真正的实现都是CotextImpl中; 该类对没有newtask标志intent会抛出运行时异常; 而activity 重写了ContextImpl的StartActivity方法,可以不用判断newtask标志; 

-----------

# 框架模式

### MVC、MVP、MVVM 简介

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200826215443678.png#pic_center)

> [clean architecture](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html) 


![在这里插入图片描述](https://img-blog.csdnimg.cn/20200826221001187.jpg?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L01ySmFydmlzRG9uZw==,size_16,color_FFFFFF,t_70#pic_center)

### ViewModel相关知识及原理

>ViewModel的特点

- 以注重生命周期的方式存储和管理界面相关数据; 
- 可以让数据在发生屏幕旋转等配置更改后继续保留;

>ViewModel的生命周期

viewmodel对象存在的时间范围是获取ViewModel时传递给ViewModelProvider的`LifeCycle`;vm在首次请求ViewModel一直留在内存中,直到限定其存在时间范围的LifeCycle永久消失;

![](https://developer.android.google.cn/images/topic/libraries/architecture/viewmodel-lifecycle.png?hl=zh_cn)

>常见使用

- 在Fragment之间共享数据;

两个Fragment 都使用activity 的ViewModelStoreOwner来构建VM,然后接受方 使用vm的liveData监听数据; 发送方 使用vm的set方法发布liveData;

>**VM源码分析**

- ViewModelProvider

构建 ViewModelProvider 的参数为 `ViewModelStore`和`Factory`; 

**fragment中使用VM通信原理:**

提供`get`方法获取VM的实例,注意ViewModelStore 存储的VM key为 `DEFAULT_KEY+vm类名`,所以不同fragment中若使用同一个ViewModelStore,获取同类名的VM,其实获取的是同一个ViewModel,达到fragment通信的目的;

- ViewModelStore + Factory

ViewModelStore 属于容器类,负责存储VM,内部只有一个`HashMap<String,ViewModel>`; 提供put,get,clear方法;

Factroy 为构造VM 的工厂,一般默认使用反射,注意配置混淆;

- ViewModelStoreOwner 只有一个获取`ViewModelStore`的接口方法;

**VM注重生命周期的方法保留数据原理**

ViewModel能在Activity(Fragmnet)由于配置重建时恢复数据的实现原理是: 

新版逻辑:

- ViewModelStore的获取

首先VMS是取自实现ViewModelStoreOwner接口(FragmentActivity),通过Activity的方法getLastConfigurationInstance() 

-> 此处要区分同名的两个类,分别是Activity 和 FragmentActivity 的 NonConfigurationInstances类,其中ViewModelStore是存储在FAct的NonConfigrationInstances,而FAct的NonConfigurationInstances存储在Act的同名类中; 

->Act的NonConfigurationInstances 是 Activity的`attach`方法赋值的,由performLaunchActivity(ActivityThread.handlerLaunchActivity)方法中,在onCreate方法之前,由`ActivityClientRecord`中保存的`lastNonConfigurationInstances`赋值, 这里涉及到activity的启动流程;

-> 在OnCreate中,通过ViewModelProvider获取到了上次保留的ViewModelStore,即Vm的对象;


- ViewModelStore的存储

由VMS的获取可知,activityClientRecord 保存了 lastNonConfigurationInstances (Activity的NonConfigurationInstances类对象)

-> 首先是在handlerReLaunchActivity()触发handleDestroyActivity(),因为保存的flag为true,普通的destroy不会触发储存逻辑;

-> 在performDestroyActivity(ActivityThread.handleDestroyActivity) 中 ,使用了Activity的`retainNonConfigrationInstances()`存储实例到ActivityClientRecord中; 

-> Activity的这个方法 存储了FragmentActivity重写的`onRetainNonConfigurationInstance()` ,这个方法中保存了 已经创建的ViewModelStore对象;


旧版逻辑:

是利用无ui的`HolderFragment` 和`ViewModelProviders`工具类实现;

源码一览: 

```
	
	//FragmentActivity.java
	@NonNull
    @Override
    public ViewModelStore getViewModelStore() {
        if (getApplication() == null) {
            throw new IllegalStateException("Your activity is not yet attached to the "
                    + "Application instance. You can't request ViewModel before onCreate call.");
        }
        if (mViewModelStore == null) {
            NonConfigurationInstances nc =
                    (NonConfigurationInstances) getLastNonConfigurationInstance();
            if (nc != null) {
                // Restore the ViewModelStore from NonConfigurationInstances
                mViewModelStore = nc.viewModelStore;
            }
            if (mViewModelStore == null) {
                mViewModelStore = new ViewModelStore();
            }
        }
        return mViewModelStore;
    }

	//Activity
	//注释中 `non-configuration instance data`来自于`onRetainNonConfigurationInstance()`可以在onCreate和onStart中获取对象; 注意此检索得到的数据仅仅用来作为处理配置改变的最佳做法;
	// 
	@Nullable
    public Object getLastNonConfigurationInstance() {
        return mLastNonConfigurationInstances != null
                ? mLastNonConfigurationInstances.activity : null;
    }

	//特别要注意同名的两个类
	//此为Activity.java的类; 其中activity保存的就是FragmentActivity的NonConfigurationInstances对象; 也是保存ViewModelStore的实体类;
	
	NonConfigurationInstances mLastNonConfigurationInstances;
	static final class NonConfigurationInstances {
        Object activity; // 这个就是FragmentActivity的NonConfigurationInstances对象;
        HashMap<String, Object> children;
        FragmentManagerNonConfig fragments;
        ArrayMap<String, LoaderManager> loaders;
        VoiceInteractor voiceInteractor;
    }
	//此为FragmentActivity.java的类;
	static final class NonConfigurationInstances {
        Object custom;
        ViewModelStore viewModelStore;
        FragmentManagerNonConfig fragments;
    }

```

### viewmodel + databinding + LiveData 

LiveData使用观察者模式;

dataBinding: 布局使用<layout>标签嵌套, 自动生成一系列文件, 使用DataBindingUtil去生成ViewDataBinding,基于观察者模式ObservableField 会自动更新布局;

-----------

# 设计模式

### 面向对象六大原则: 
 
 - 单一职责原则; 单一职责的功能放在一个类中;
 - 开闭原则; 一个类应该对于修改关闭,对于扩展开放;
 - 里氏替换原则; 引用基类的地方,可以透明的使用其子类的对象;
 - 依赖倒置原则; 高层模块和低层模块都应该依赖于抽象;模块间的依赖通过抽象发生;
 - 接口隔离原则;类间的依赖关系,建立在最小的接口上;

 - 最少知识原则(迪米特原则); 一个对象对其他对象有最少的了解;

创建型: 
 
- 单例 (饿汉,懒汉,dcl单例,静态内部类单例,容器单例,枚举单例)
- 构建者(统一组装过程类,构造产品)
- 原型 (保护性拷贝)
- 工厂 (简单工厂,静态方法构造对象)
- 抽象工厂(抽象工厂+多个抽象产品)

行为型:

- 策略 (强调可替换性)
- 状态 (强调处于某种状态,各种状态平行)
- 责任链 (强引用连接成一条责任链,由一个判断条件判断由哪个节点去处理; 纯责任链(可以被其中之一处理),不纯责任链)
- 解释器 (文法)
- 命令 (抽象出待执行的动作,methodInvoker)
- 观察者 (发布-订阅)
- 备忘录 (保存数据内部状态)
- 迭代器 (统一多数据的遍历接口,不暴露对象内部显示)
- 模板 (定义一套算法框架)
- 访问者 (数据结构稳定,数据操作和数据结构分离)
- 中介者 (松散耦合,将需要耦合的对象集中在中介者上,通过中介达到各对象间的相互作用)

结构型:

- 代理 (注重控制, 动态代理)
- 组合 (透明组合模式(相同结构),安全的组合模式(结构可以不一样))
- 适配器(将不兼容的类融合在一起,生成一个统一的接口)
- 装饰者 (注重扩展,增强功能,继承关系的替代)
- 享元 (对象池,减少重复对象的创建)
- 外观 (将一些功能组合,对外提供统一接口,封装api)
- 桥接(将抽象部分和实现部分分离,避免直接继承)

-----------

# 三方源码

### OkHttp (拦截器(责任链模式),超时重传&重定向,http缓存,socket连接池复用)

- 拦截器链,根据拦截器处理请求和响应逻辑;

###  Glide (生命周期控制,二级缓存,bitmappool复用)

- 采用责任链模式,将encode,decode等类append一起,判断多种情况的调用;
- 构建者构建快速调用方法
- 策略模式,提供多种不同缓存策略;
- 外观模式, Glide类封装多种功能;
- 二级缓存机制 
	- 内存缓存,弱引用 + LruCache ->网络 
	- 磁盘缓存, 原图 + 处理后的图

### EventBus

- 观察者模式,发布-订阅模型;

### 依赖注入DI ,butterknife,dagger2;

- apt abstractProcessor (javapoet)生成一系列java文件,初始化对象助于开发;

### LeakCanary

强引用,弱引用,软引用,虚引用

弱引用 + 引用队列,当弱引用被gc后,都会放入到引用队列中; 一个对象期望被回收,预期的时间没有出现在referenceQueue中,内存泄露了;

### ARouter

//TODO

### Rxjava （RxJava 的线程切换原理）

基于事件流,实现异步操作的库

- 观察者模式
- Handler

### Retrofit (Retrofit 在 OkHttp 上做了哪些封装？动态代理和静态代理的区别，是怎么实现的; 动态代理,运行时注解)

核心就是动态代理,为每个接口的每个接口方法,生成一个对应的ServiceMethod,并创建OkHttpCall,并使用`serviceMethod.callAdapter.adapt(okHttpCall)`调用并返回结果;

### acache ,room

-----------

# kotlin

### 基础知识

### 协程

### 高阶函数

-----------

# 算法与数据结构

### 算法基础知识

算法: 一系列程序指令,处理特定的运算和逻辑问题; 

数据结构: 数据的组织,管理和存储形式,为了高效的访问和修改数据;

**时间复杂度:** 对一个算法运行时间长短的量度,用大O表示,记做`T(n)=O(f(n))`

常见的时间复杂度从低到高的顺序: O(1) < O(logn) < O(n) < O(nlogn) < O(n^2)

**空间复杂度:** 对一个算法在运行过程中临时占用存储空间大小的量度,用大O表示,记做`S(n)=O(f(n))`

常见空间复杂度从低到高的排序: O(1) < O(n) < O(n^2) ,其中 递归算法的空间复杂度和递归深度成正比;

> 数组和链表

数组的时间复杂度(按照索引) 增O(n) 删O(n) 改O(1) 查O(1); 适合读操作多,写操作少的场景;

链表的时间复杂度  增O(1)  删O(1)  改O(1)  查O(n); 适合写操作多,读操作少的场景;

> 逻辑结构和物理结构

逻辑结构 是抽象的概念,




###  二叉排序树(二叉搜索树),查找下一个元素;

左子树所有节点值小于它的根节点值，且右子树所有节点值
大于它的根节点值，则这样的二叉树就是排序二叉树。

>插入操作

与当前节点比较,小于左子树中寻找,大于右子树中寻找;为空直接插入;

![在这里插入图片描述](https://img-blog.csdnimg.cn/2020081022215264.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L01ySmFydmlzRG9uZw==,size_16,color_FFFFFF,t_70)

>删除操作

1. 对于要删除的节点无子节点可以直接删除，即让其父节点将该子节点置空即可。
2. 对于要删除的节点只有一个子节点，则替换要删除的节点为其子节点。
3. 对于要删除的节点有两个子节点， 则首先找该节点的替换节点（即右子树中最小的节点），
接着替换要删除的节点为替换节点，然后删除替换节点

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200810222545957.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L01ySmFydmlzRG9uZw==,size_16,color_FFFFFF,t_70)

>查询操作

先从根节点比较,小于左子树中递归查找,大于根节点右子树中递归查找;

 前序和中序遍历二叉树（我写了递归和非递归）,了解二叉树的遍历

- 前序遍历: 根节点->左节点->右节点;
- 中序遍历: 左节点->根节点->右节点;
- 后序遍历: 左节点->右节点->根节点; 

**红黑树** 

- R-B Tree 特殊的二叉查找树; 每个节点是黑色或者红色;根节点是黑色; 每个null的叶子节点是黑色;如果一个节点是红色的,子节点必须是黑色的; 从一个节点到该节点的子孙节点的所有路径上包含相同数目的黑节点;
- 左旋: 将右孩子设为父亲节点,被旋转的节点将变成一个左节点;
- 右旋: 将左孩子设为父亲节点,被旋转的节点将变成一个右节点;

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200811072930929.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L01ySmFydmlzRG9uZw==,size_16,color_FFFFFF,t_70)

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200811073219302.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L01ySmFydmlzRG9uZw==,size_16,color_FFFFFF,t_70)

###  输出二叉树每层的最大值

###  完全二叉树和满二叉树的区别

完全二叉树: 除了最高层之外,其余层节点个数都达到最大值,并且最高层节点优先集中在最左边;<br>
满二叉树: 除了最高层有叶子节点,其余层无叶子,并且非叶子节点都有两个子节点;

###  单链表：反转、插入、删除

### 双链表：插入、删除

### 排序; 手写常见排序、归并排序、堆排序

```

	//选择排序
	int[] arr = new int[]{...}
	for(int i=0;i<arr.length;i++){
		for(int j=i+1;j<arr.length;j++){
			if(arr[i]>arr[j]){
				arr[i] = arr[i]^arr[j]
				arr[j] = arr[i]^arr[j]
				arr[i] = arr[i]^arr[j]			
			}
		}
	}

	//冒泡排序 (往后冒泡,先排好后面)
	for(int i=0;i<arr.length;i++){
		for(int j= 0;j<arr.length -1-i;j++){
			if(arr[j] > arr[j+1]){
				int tmp = arr[j+1]
				arr[j+1] = arr[j]
				arr[j] = tmp
			}
		}
	}

	//冒泡排序-逆 (往前冒泡,先排好前面)
	for(int i=0;i<arr.length;i++){
		for(int j = arr.length-1;j >i;j--){
			if(arr[j]<arr[j-1]){
				//交换;
			}
		}
	}

	//插入排序
	for(int i=1;i<arr.length;i++){
		for(int j=i;j>0;j--){
			if(arr[j] < arr[j-1]){
				//交换
			}
		}	
	}

```

> 快排；

思想: 先选择一个关键值作为基准数; 通过一次排序将数据分割为独立的两部分,其中一部分的所有数据都比另一部分的所有数据都要小(因为基准数),然后根据此方法对两部分的数据分别进行快排,递归;

 - 选择一个基准数,一般第一个数; 
 - 开始从后往前遍历,找到比基准数小的交换位置;找到这个值之后,从前往后遍历,找到比基准数大的交换位置; 直到前往后的比较索引 > 后往前比较的索引,第一遍比较结束; 对于基准值来说,左右两边都有顺序了;
 - 使用基准数分解数组,在选择基准数快排,直到不能再分为止;

```

	public void quickSort(int[] arr,int start,int end){
		int s = start;
		int e = end;
		int key = arr[s]
		while (s<e){
			while(e>s && arr[e]>=key){
				e--;
			}
			if(arr[e]<=key){
				//交换
			}
			while(e>s && arr[s]<=key){
				s++;
			}
			if(arr[s]>=key){
				//交换;
			}
		}

		//递归
		if(s>start) quickSort(arr,start,s-1);
		if(e<end) quickSort(arr,e+1,end)
	}


```

### 手写二分查找，并分析时间复杂度；

思想: left游标,right游标,middle游标,如果middle游标的值大于target,最终的值在前半部分,right = middle-1,middle = (left+right)/2;重新遍历计算;

```

	int start = 0
	int end = arr.length-1
	int mid;
	while(start<=end){
		mid = (start+end)/2
		if(arr[mid] <target){
			start = mid +1
		}else if(arr[mid] > target){
			end = mid-1
		}else {
			return mid;
		}
	}
	return -1;

```

### 数据结构 ~ 图 (V,E) V:顶点的集合;E:边的集合;

### 二叉树前序、中序、后序遍历

### 最大 K 问题

### 广度、深度优先搜索算法

### String 转 int。核心算法就三行代码，不过临界条件很多，除了判空，还需要注意负数、Integer 的最大最小值边界等；

### 如何判断一个单链表有环？

### 链表翻转；



### 100 亿个单词，找出出现频率最高的单词。要求几种方案；

###链表每 k 位逆序；

###镜像二叉树；

###找出一个无序数组中出现超过一半次数的数字；

###计算二叉树的最大深度，要求非递归算法。

###String 方式计算加法。

-----------

# 项目&hr

### 个人简介

###  项目开发中遇到的最大的一个难题和挑战，你是如何解决的。（95% 会问到）

### 说说你开发最大的优势点（95% 会问到）

###你为什么会离开上家公司

### 你对未来的职业规划？

### 你的缺点是什么？

### 你能给公司带来什么效益？

-----------

# 计算机基础知识



-----------

# 杂项,工具相关


### 团队代码管理方式:
 
  - **集中式工作流:**
  只有一个master,开发clone到本地,修改提交在本地仓库,本地代码`push`到远程,适合小团队开发;
  
  - **功能开发工作流:**
  不直接向master提交代码保证稳定干净,团队开发根据分工从master拉取feature(功能)分支进行不同的功能开发,完全隔离每个人的工作;当功能开发完成后,会向master分支发起`Pull Request`,通过审核的代码才真正允许合并入master,就是Code Review;适合中小型团队开发;
  
  - **gitflow工作流:** 
  `master和develop` 分支是一直存在的,master可视为稳定的分支,develop是相对稳定的分支, 特性开发会在feature分支上进行, 发布会在release分支进行,bug修复会在hotfix分支上进行;适合大型项目的发布维护;
  
  	- master: 主分支,保持稳定,不允许直接提交代码,只允许往此分支发起merge request;只允许release和hotfix分支进行合流;
  	- develop: 开发分支;相对稳定的分支,用于日常开发;
  	- feature: 特性分支;从develop分支拉取,用于下个迭代版本的功能特性开发;功能开发完毕并到develop分支;
  	- release: 发布分支,从develop分支拉取,用于回归测试,bug修复,发布完成后打tag并入到master和develop;
  	- hotfix: 热更新分支;从develop分支拉取;用于紧急修复上线版本的问题;修复后打tag并入master和develop;
 
  
  ![](https://img-blog.csdn.net/20170215234958484?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvd3dqXzc0OA==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)
  
  
  - **forking工作流:** 
  公开的中央仓库,其他成员`fork` 为私有仓库,然后`pull requst`合并到中央仓库;
  
  ![](https://img-blog.csdn.net/20170215235113109?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvd3dqXzc0OA==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

-----------

