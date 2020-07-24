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
	计算key对应的hash,是否包含在容器中;
	不在时判断是否需要扩容;
	将key和value连续保存在mArray中;

>`ArraySet`

	与ArrayMap的数据结构一样,只保存value;

>`SparseArray`,SparseIntArray,SparseBooleanArray,SparseLongArray ,
可代替map,implement Cloneable ,实现为int和object的数组;

	private int[] mKeys; //保存的key值为int
	private Object[] mValues; //保存的value为obj;
	与arraymap不同的是分别存储;

>`HashMap`

 内部是使用一个默认容量为16的数组存储数据的,而数组中每一个元素又是一个链表的头结点; 使用哈希表的`数组+链表`结构;
 
 每一个节点都是Entry类型,Entry属性;
 
 存储hash,key,value,和下一个节点的引用;
	

	final int hash;
	final K key;
	V value;
	Node<K,V> next;

Entry根据元素key的hash值对hashmap的数组长度取余得到元素的位置(1.8后是重新计算hash值,避免hash冲突,还是对长度取余),如果多>个value的hash值重复,则添加至node的链表后;(链地址法)

其中,处理hash冲突的方法有
`开放地址法`, `再哈希法`,`链地址法 `,`建立公共溢出区`

缺点: 

1.默认的存储大小为16的数组,没有任何元素,也会占有空间;

2.,根据负载因子(默认0.75)扩容时`(newCap = oldCap << 1)`;扩容的大小是乘以2,对内存空间消耗很大;

3.get的时候遍历Map.Entry[] 数组,数据量大的时候很慢;

>SparseArray:

1. 避免对key的自动装箱,默认就是int,内部通过两个数组实现,一个存储key,一个存储object,内部对数据采取压缩的方式表示稀疏数组的数据,节约内存;<br>
2. 获取和添加数据时使用二分查找法比较int大小,按照顺序排好或判断元素位置;

 注意: 

 spraseArray 可代替hashmap<br>
 1.在数据量不大,千级以内;(二分法数据量大不明显);<br>
 2.key必须为int时


>ArrayMap:

 也是两个数组存储数据,不同的是一个存储hash值,一个连续存储key和value值,也是通过二分法进行从小到大排序(二分查找index,然后插入末尾),curd也是先通过二分法找到index,与SparseArray一样,数据量大时,不推荐使用;



>HashTable与HashMap的区别与存储过程与遍历方式。

hashmap有3种主流遍历方式;

1. `May.Entry`(entrySet()),
2. `keySet`, ( 迭代器就不算了) 
3. 最新遍历方式(函数式遍历):`forEach(BiConsumer )` ;

>区别: 

1.继承不同,一个是Map,一个是Dictionary(已废弃); 对外提供的接口也不同;

2.线程安全性不同,Hashmap 是线程不安全,效率高,hashtable相反; ConcurrentHashMap也是线程安全的,使用分段锁;

3.null key 的支持不同,hashmap都支持,hashtable 都不支持;

4.遍历方式内部不同; hashmap的iterator是`fail-fast`迭代器,有其他线程改变了hashmap的结构(增加,删除,修改),抛出concurrentmodificationException;

5.初始容量大小和每次扩充容量大小不同,hashtable 默认大小11,每次扩充2n+1;hashmap默认16,每次扩充2n;

6.计算hash的方法不同(得到元素的位置,通过key的hash值计算得到最终的位置)<br>
hashtable直接使用对象的hashCode(hashCode是jdk根据对象的地址或者字符串或者数字算出来的int类型的数值.)在使用对数组长度求余获取最终的位置;<br>
hashmap 哈希表大小为2的幂,增加hash冲突,但效率提高,对hashCode重新计算,减少hash冲突;


![](https://img-blog.csdn.net/20180306020714182?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvd2FuZ3hpbmcyMzM=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

![](https://img-blog.csdn.net/20180306020658482?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvd2FuZ3hpbmcyMzM=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

>HashMap源码，JDK1.8前后详细区别，负载因子，Fail-Fast机制

1.8之前的hashmap 采用的是`数组+链表`,缺点是元素分布不均匀;hash冲突导致某个链表可能会非常长,遍历的时间变长;<br>
1.8之后,采用的是`数组+链表或数组+红黑树`,添加元素,某一个链表(bin,某个箱子)的元素超过8个转换为红黑树;删除,扩容时元素个数少转换为链表结构,遍历提高性能;

负载因子用于计算扩容阈值,扩容阈值是需要扩容的时候;

>Fail-Fast:

java 的错误检测机制,当多个线程对集合进行`结构`上的改变的操作时,有可能产生fail-fast机制;(例如 遍历remove时,遍历输入;或者多线程去remove item,抛出ConcurrentModificationException);

modCount != expectedModCount 判断两者的值不等则抛出错误;

>解决方法: 

单线程环境remove数据时,使用`迭代器`,或者`remove后,i--`;

多线程环境下,使用copyOnWriteArrayList代替;

>原java数据类型  List中 ArrayList 和 LinkedList 区别？

`ArrayList`  遍历块,插入慢;

实现由对象数组实现 `Object[]`,遍历用下标index更快,因为是数组;
临时的对象数组使用transient,不参与序列化,使用writeObje和readObj代替(内存流);

`LinkedList` 插入块,遍历慢;

实现由双向链表实现`E item;
        Node<E> next;
        Node<E> prev;`
应用类似于队列;
查找的时候先判断一半,接着将一半的数据从头遍历到尾;
增删避免了数组拷贝,所有插入的效率高;

`CopyWriteArrayList` 不可变对象设计模式,读写分离,用于并发情况,读的时候直接索引,写的时候System.arraycopy(); (COW 写时拷贝)

`Collections.synchronizedList()` 装饰着设计模式,增强list的方法,获取mutex对象相关联的monitor锁,用于并发;

![](https://img-blog.csdn.net/20180703141542253?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2o1NTAzNDExMzA=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)

> Set

 HashSet内部使用hashMap实现;将value设置固定;
 TreeSet 内部由treeMap实现;

> Queue

 LinkList, ArrayBlockingQueue,Deque,LinkBlockingQueue;

LinkHashMap 继承于Hashmap:

`LinkedHashMapEntry extend HashMap.Node` 在hashmap的红黑树中使用到;

> Atomic 保证高并发环境的原子指令; 硬件层面实现的原子操作;

```
AtomicBoolean, AtomicInteger, AtomicLong, AtomicReference提供对相应类型的单个变量的原子访问/更新;
updater类AtomicReferenceFieldUpdater, AtomicIntegerFieldUpdater, AtomicLongFieldUpdater 是基于反射的工具类, 可以提供对相关类型字段的访问, 主要用于对数据结构中的volatile字段单独进行原子操作, 在更新的方式和时间上更加灵活, 弊端请见官方文档;
AtomicIntegerArray, AtomicLongArray, AtomicReferenceArray 进一步提供了这些类型在集合中的原子操作, 特别是提供了普通集合不具备的元素volatile访问语义.
```

> 线程安全的集合

ConcurrentHashMap(数据结构和hashmap类似,使用synchronized);

CopyOnWriteArrayList

CopyOnWriteArraySet

ArrayBlockingQueue (对象数组)

LinkBlockingQueue


![](https://img-blog.csdn.net/20180705150230861?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2o1NTAzNDExMzA=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)

###  GC回收算法,java内存结构,JVM的引用树，什么变量能作为GCRoot？

`java的内存模型`: 

线程私有: Jvm栈,本地方法栈,程序计数器;<br>
栈中的栈帧的内存分配在编译器间已经确定;

线程共享: 方法区,堆;<br>
Gc回收主要针对堆内存;方法区垃圾回收的效率很低,主要是废弃的常量和无用的类;

>`Gc检测算法`:

1.引用计数算法(没有采用)

2.可达性分析算法

>通过GCRoots 对象作为起点,从根节点开始向下搜索,走过的路径为引用链,如果一个对象处于空岛状态,则可以gc的对象;
可以作为GC Roots的对象:

1.虚拟机栈(栈帧中的本地变量表)中引用的对象;

2.方法区中的类静态属性引用的对象或者常量引用的对象;

3.本地方法栈中jni引用的对象;

### 内存模型

>cpu cache模型 

cpu(寄存器)->CpuCache(CacheLine : L1 Cache(L1i,L1d),L2,L3)->RAM

定义线程与主内存之间的抽象关系,决定线程对共享变量的写入何时对其他线程可见;

- 共享变量存在于RAM中;(堆,方法区)
- 每个线程都有工作内存(jvm栈,本地方法栈)
- 工作内存值存储共享变量的副本;
- 线程通过工作内存操作主内存;

### volatile 关键字

 解决缓存不一致的情况,将cache line置为无效,从ram中重新获取值;

并发编程特性:<br>

- 原子性(atomic)
- 有序性;编写的顺序不一定就是运行的顺序,指令重排序;
- 可见性;一个线程对共享变量的修改,另一个线程立即看到修改的最新值;

volatile不保证原子性;
sychronized 可以保证原子性;

volatile 保证可见性(sychronized,lock也可以,但是原理是jvm指令monitor enter和monitor exit排他的串行化方式);
volatile 保证有序性,对一个变量的写操作要早于对这个变量的读操作;

>原理: volatile修饰的变量存在一个`lock;`的前缀,相当于一个内存屏障cpp;(偏硬件)

保证多线程中的可见性和有序性, 可见性基于JMM,(cpu register -> cpuCache l1,2,3 -> 主内存) cpu需要从主内存中取共享变量到cpucache,继而到register中,然后在处理逻辑, 加了volatile关键字的变量可将cpuCache中同样的副本置为无效,cpuregister从cpucache中获取数据时,如果数据无效,会重新向主内存取数据;保证了多线程操作下的可见性;  有序性是volatile 基于硬件层面的,`lock;`前缀,jvm不准打乱volatile的执行顺序;

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

### 垃圾回收机制和调用 System.gc()的区别？

### java的类加载过程（需要多看看，重在理解，对于热修复和插件化比较重要）

>类的生命周期: 

 类加载->连接->初始化->使用->卸载 <br>
 连接分为: 验证->准备->解析 <br>

> 1.类加载: 
 
 用全限定名获取类的.class文件中的二进制字节流;<br>
 将字节流所代表的静态存储结构转化为方法区的运行时数据结构;<br>
 在java堆中生成一个java.lang.Class对象,作为方法区中数据的访问入口;

 加载阶段,使用了双亲委托机制,可以使用自定义的类加载器加载,涉及初始化加载器类的访问权限,根加载器->扩展类加载器->系统类加载器->自定义加载器;

类加载的方式: <br>

- 命令行启动应用时候由jvm初始化加载;

- 通过class.forName()动态加载;
字节码文件加载到jvm中

- ClassLoader.loadClass()动态加载;
 
> 2.连接:
 
- 验证: 确保被加载类的正确性; 文件格式验证,元数据验证,字节码验证,符号引用验证;
 
- 准备: 为类的`静态变量`分配内存,并将其初始化为默认值;

 内存分配的仅包括类变量(static),不包括实例变量, 实例变量在对象实例化时随着对象被初始化; 这里所设置的初始值通常情况下是数据类型默认的零值;
 
 注意static final 是准备阶段赋值,理解为编译时将其放入常量池中;
 
- 解析: 将类中的符号引用转换为直接引用;
 
 
> 3.初始化

为类的静态变量赋予正确的初始值,主要对类变量进行初始化;<br>
对类的`主动使用`会导致类的初始化<br>
包括new;访问类或接口的静态变量;调用类的静态方法;反射;初始化某个类的子类,则其父类也会被初始化;启动类main;

执行<clinit> (class initial)函数,包含所有类变量的赋值和静态语句块的执行代码;

### 类加载器

>根加载器,扩展类加载器,系统类加载器,自定义类加载器

- 全盘委托机制

使用自定义加载器方式:

- 绕过系统类加载器,将扩展类加载器作为自定义类加载器的父加载器; 
- 构造自定义类加载器时指定父类加载器为null,会使用根加载器加载,根加载器加载路径找不到会使用当前类加载器加载;

- 初始类加载器

jvm会在每一个类加载器维护列表中添加class类型,classloader的class列表中的类可相互访问,否则不同的运行时包下的类不可相互访问;

上下文类加载器器:

setContextClassLoader 例如jdbc 是子委托机制;因为厂商的connection实现的是由自定义的类加载器加载的;

### 反射(内省相关)

运行时获取自身的信息 Class.forName(class全限定名),对象.getClass(),类名.class;

Class获取注解方法: `getAnnotation`,`isAnnotationPresent 是否被注解修饰`,`getAnnotations`,`getDeclaredAnnotation 本元素的指定注解`,`getDeclareAnnotations`;

- 内省 Introspector: java对Bean类属性,事件的一种`缺省处理方法`;一般步骤:<br>
>通过Introspector获取某个对象的BeanInfo信息,通过BeanInfo获取属性描述器(propertyDescriptor),获取某个属性对应的getter/setter方法,通过反射获取属性;操作javaBean;

![](https://img-blog.csdn.net/20160422101525368)

### 多线程和线程池

### HTTP、HTTPS、TCP/IP、Socket通信、

> socket连接,tcp,http

请求行: 请求方法,请求统一资源标志符(URI:URL排除host剩下的部分,资源在服务器本地上的路径),http版本号;

请求头: content-Type

请求体: 真正发送的数据;

响应行: http版本号,状态码; 1XX:信息提示;2XX:成功;3XX:重定向; 4XX:客户端错误; 5XX: 服务端错误;

响应头,响应体: 

-----

网络分层:应用层(http协议,FTP协议),传输层(Tcp/udp),网络层(ip协议),连接层(wifi,以太网),物理层(0/1);

http 是应用层的协议;tcp是传输层的协议;socket 传输层上抽象出来的一个抽象层,本质是接口,对tcp/ip协议的封装;

- `Http是基于Tcp`,客户端发送一个http请求第一步就是建立与服务器的tcp连接,就是三次握手;从http1.1开始支持持久连接,一次tcp可发送多次http请求;
- `Socket也基于Tcp`,一个socket可以基于tcp连接和可以基于Udp的连接,是一个接口;
- `长短连接,请求响应先后` http连接是短连接,socket连接基于Tcp的是长连接.socket一旦建立tcp三次握手连接,除非一方主动断开,否则一直保持; http采用请求响应机制,socket请求发送没有前后限制;

socket: 用于即时通讯;支持不同的传输层协议,当支持tcp连接时,就是一个tcp长连接;
> http用于不需要时刻在线,资源获取文件上传,短连接;


![](http://cc.cocimg.com/api/uploads/20160323/1458719461811413.png)

### 三次握手四次挥手过程

连接时:三次握手;

- 第一次握手,客户端发送syn包(syn = j;)到服务器,进入SYN_SEND状态,等待服务器确认;
- 第二次握手,服务器收到syn包,确认客户的syn(ack = j+1),同时自己也发送一个syn包(syn = k),即syn+ack包,此时服务器进入SYN_RECV状态;
- 第三次握手,客户端收到服务器的syn+ack包,象服务器发送确认包ack(ack = k+1),此包发送完毕,客户端和服务器进入ESTABLISHED状态,完成3次握手;

断开时: 4此挥手;

- 第一次挥手,客户端发送fin包,关闭数据传送;
- 第二次挥手,服务器收到fin,发回一个ack;
- 第三次挥手,服务器关闭与客户端的连接,发送fin+ack给客户端;
- 第四次挥手,客户端发挥ack报文确认;

![](https://images0.cnblogs.com/blog/385532/201308/30193702-7287165c73e7440382207309e07fcbb5.png)

![](https://images0.cnblogs.com/blog/385532/201308/30193703-330b281cddc5439f99eb027ac1c9627c.png)

###   如何建立长链接。

tcp 使用心跳机制;可以使用socket套接字实现;

###   讲讲 HTTPS 是如何做加密的

https其实是由两部分组成: http+ssl/tls,就是在http上又加了一层处理加密信息的模块;传输的都是加密后的信息;(非对称加密和对称加密联合使用,先非对称加密后对称加密;)

- 服务器把自己的公钥登录至数字证书认证机构;
- 数字证书机构把自己的私有秘钥向服务器的公开密码部署数字签名并颁发公钥证书;
- 客户端拿到服务器的公钥证书后,使用数字证书认证机构的公开秘钥,向数字证书机构验证公钥证书上的数字签名,以确认服务器公钥的真实性;
- 使用服务器的公钥对报文加密后发送;
- 服务器用私钥对报文解密;

---

- `认证服务器`,浏览器内置受信任的CA机构列表,保存这些CA机构的证书,如果是可信的,从服务器证书中取得服务器公钥,用于后续流程;
- `协商会话秘钥`,客户端认证完服务器,获取服务器公钥后,利用服务器公钥与服务器机密通信,协商两个会话秘钥,用于客户端>服务端和服务端>客户端的会话秘钥(对称加密的秘钥,节省资源);会话秘钥是随机生成;
- `加密通讯`,传输的http信息是通过会话秘钥加密,先使用会话秘钥得到机密信息;

![](https://img-blog.csdn.net/20180818150959428?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzMyOTk4MTUz/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)

###   post请求的数据格式定义在那里定义，手写出来

 请求方法有`Option`,`Get`,`Post`,`Head`,`Put`,`Delete`,`Trace`,`Connect`八大类型;</br>
 http协议是ascii码传输,建立在tcp/ip协议之上的应用层规范,分为请求行,请求头,请求体;
 服务端通常是根据请求头headers的`Content-Type`字段类获知请求中的消息主体是何种编码方式,在对主体进行解析,包括content-type和消息主体编码方式两部分;<br>
 `application/x-www-form-urlencoded;charset=utf-8` 浏览器的原生表单,不设置`enctype`属性,就是这种方式<br>
 `multipart/form-data` 浏览器原生支持,使用表单上传文件时的,使表单的`enctype`为这个;<br>
 `application/json` 响应头,也可以作为请求头,消息主体是序列化后的json字符串;<br>
 `text/xml` xml为编码方式;

###   http与https的理解与4层都是哪4层，在一个请求过程中都是什么时候走这些流程与各自的作用

http和https 都是属于应用成,https添加了ssl/tls对数据进行了加密,一个请求首先通过网络层ip协议找到目标,然后通过传输成的tcp(/udp)建立连接,然后通过http协议发送消息;

###   session与cookie的区别

 session 会话技术, session在服务器上,cookie 在客户端;第一次发送请求时会自动生成sessionId,返回到浏览器的cookie中,下次访问将cookie中的sessionId一并传过来判断当前用户登录;

###   HttpClient和HttpConnection的区别

都是通过http请求,android2.2前用httpclient,2.3后用httpconnection;

### 设计模式（六大基本原则、项目中常用的设计模式、手写单例等）

>面向对象六大原则: 

- 单一职责原则; 单一职责的功能放在一个类中;
- 开闭原则; 一个类应该对于修改关闭,对于扩展开放;
- 里氏替换原则; 引用基类的地方,可以透明的使用其子类的对象;
- 依赖倒置原则; 高层模块和低层模块都应该依赖于抽象;模块间的依赖通过抽象发生;
- 接口隔离原则;类间的依赖关系,建立在最小的接口上;

- 最少知识原则(迪米特原则); 一个对象对其他对象有最少的了解;

创建型: 

- 单例
- 构建者
- 原型
- 工厂
- 抽象工厂

行为型:

- 策略
- 状态
- 责任链
- 解释器
- 命令
- 观察者
- 备忘录
- 迭代器
- 模板
- 访问者
- 中介者

结构型:

- 代理
- 组合
- 适配器
- 装饰者
- 享元
- 外观
- 桥接

### Java 四大引用

### Java 的泛型

### final、finally、finalize 的区别

### 接口、抽象类的区别

 - 接口是interface,抽象类是abstract class;
 - 接口的方法都是抽象方法,抽象类中可定义非抽象方法;
 - 接口可以实现多个,抽象只能继承一个;
 - 接口的变量都是final的;

### 注解框架,原理,annotation

元注解(注解在注解之上)

@Target : 注解的作用类型 ElementType 

@Retention: 注解的生命周期,

- 源码保留,编译字节码时丢弃;
- 字节码保留,加载到jvm时丢弃;
- 运行时保留,不丢弃;

@Documented : 注解是否应当被包含在JavaDoc文档中;

@Inherited: 是否允许子类继承该注解;

>注解的解析方式: 

个人理解有两种,<br>

一种是直接反射获取Annotation对象,接着通过注解的值处理逻辑; 如retrofit;

一种是继承AbstractProcessor接口,如butterknife+apt+javapoet生成代码;

>注解的本质:

继承Annotation接口的接口,jdk通过动态代理机制生成一个实现我们注解(接口)的代理类;<br>
1.键值对的形式可以注解属性赋值;

2.注解修饰某个元素,编译期扫描注解和检查,写入元素的属性表;

3.反射的时候,jvm将所有runtime的注解取出来放到一个map(memberValues 注解属性名: 属性值)中,创建一个AnnotaionInvocationHandler实例,把这个map传递给他;

4.jvm将采用jdk动态代理机制生成一个目标注解的代理类,初始化好处理器;invoke中处理方法名,通过方法名返回注解属性值;

### Error 和 Exception 区别？

 都是继承Throwable类;

1.error 是系统中的错误,修改程序才可; exception 是可以捕获处理, 分为编译时异常和运行时异常,RuntimeException不需要捕获,需要处理;


### Java的修饰符的使用，static, final修饰原理

- public: 都可以调用;
- protected: 包权限和继承权限(继承->同包)
- default: 同包下可访问;
- private: 本类中访问;

protected修饰符的修饰的成员变量和方法,如果是继承于父类不管是不是同一个包都可访问,同包下也可以被访问。

kotlin中加入一个`internal` 模块内访问;

static 类变量或方法,类初始化时运行,Gcroots起点; final 属性不可改变,方法不可重载,类不可继承;

static final 变量不可修改,类方法,属性,存在于方法区的运行时数据区;


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

### 多线程相关知识

- android 多线程面试组织:

- 多线程特性: 原子性,有序性,可见性;
	- 原子性: 使用atomic包下的类;
	- 可见性: volatile 关键字保证可见性;
	- 有序性: volatile 保证有序性;硬件层面保持有序性,JMM: java内存模型,cpu 寄存器->cpucache l1,l2,l3-> 主内存;获取共享数据;

- android 的线程分为`UI线程`和`工作线程`: 
	- UI线程,应用启动的时候分配的进程,创建一个线程,即Ui线程;ui线程不能执行耗时操作,工作线程不能直接刷新UI线程的控件; 然后谈到handler机制等异步;
	- `handler消息机制`,主线程创建了一个looper,子线程投递消息到主线程的Queue中,这里还涉及了android的两套消息机制;

- java中的线程: 主要使用Thread和Executor来实现多线程,使用`线程池`和`锁`和`多线程的多种设计模式`(生产者消费者,latch,future,EDA,BUS,读写锁分离,不可变对象设计)wait(Object) notify实现 (wait set);
>	- 悲观锁(synchronized)和乐观锁(cas)
>	

> 多线程空指针解决方法

多线程中运行时异常,UncaughtExceptionHandler,线程在执行单元中是不允许抛出check异常(编译时异常),所以需要`try..catch`,或者包装成RuntimeException,使用UncaughtExceptionHandler接受;

> java线程池创建方式,线程池工作原理:

 四种线程池,Executor;
 
 - corePoolSize: 核心线程数,创建保存到阻塞队列; 
 - maximumPoolSize: 允许的最大线程数,指定可创建非核心线程数量;
 - keepAliveTime: 非核心线程的空闲时的存活时间;
 - unit: 单位;
 - workQueue: 保存到被执行任务的阻塞队列,LinkedBlockingQueue 链表队列,ArrayBlockingQueue 数组队列;
 - threadFactory: 创建线程工厂;
 - handler: 阻塞队列饱和策略;AbortPolicy:抛出异常策略; CallerRunsPolicy: 用调用者原来的线程执行任务;DiscardOldestPolicy: 丢弃阻塞队列中靠前的任务,执行当前任务; DiscardPolicy: 直接丢弃任务;
 
 -----
 
 - newFixedThreadPool : 固定核心线程数量;
 - newCachedThreadPool: 没有核心线程数量,非核心线程数量整数最大值;
 - newSingleThreadPool: 一个核心线程数量;
 - newScheduleThreadPool: 固定核心线程数量,总线程数为整数最大值;

> 线程间通信,线程同步和线程调度相关的方法:sleep,wait ,yield方法

线程间的通信使用 Object的方法`wait`,`notify`用于线程间通信关于资源锁;

sleep: 线程不会释放monitor锁资源,唤醒后自动恢复;cpu执行机会让给其他线程;
进入到block状态;恢复时持有锁,ready状态;

wait: 该线程持有与该对象相关的monitor锁,如果wait,将thread加入到锁对象的wait set 等待池中; 让cpu资源,让锁资源; 进入bolck状态,恢复时,重新获取锁;

yield: 给其他线程执行的机会;直接进入到ready状态;

notify: 唤醒一个处于等待状态的线程,notifyall 唤醒所有处于等待的线程,竞争锁资源;

join: 先执行join的线程的内容,后执行当前线程的内容;

> Synchronize关键字后面跟类或者对象有什么不同

一个是锁住的Class类对象,一个锁住的是Class的实例对象;

> ThreadLocal是什么？Looper中的消息死循环为什么没有ANR？

线程本地,使用ThreadLocal创建的变量,只能被当前线程访问,其他线程无法访问和修改;

InheritableThreadLocal() 创建子线程时,将某个线程的threadlocal传递过去;

> 死锁产生条件和场景

互斥条件;请求和保持条件;不剥夺条件,环路等待;

1.共享资源的竞争;2.请求和释放资源顺序不当;

`多线程多锁`吃面问题,多个线程使用刀和叉两个同步锁去吃面,就会发生死锁; a线程持有刀锁,等待叉锁;b线程持有叉锁,等待刀锁;

`单线程重复申请锁` ,`忘记释放锁`, `环形锁`;

>避免: 资源有序分配; 银行家算法; 将多个互斥锁对象封装成一个类作为锁对象;

- 一般解决方法
	- 避免嵌套锁,将多个锁对象封装成一个对象作为总的锁对象;
	- 设置锁的超时时间;

- 线程安全问题
 序列化访问临时资源(同步互斥访问),同一时刻,只能有一个线程访问临时资源;<br>

悲观锁: synchronized 和 lock ;<br>

乐观锁: cas,不可变对象;

> 线程的状态变化

 create,runnable<->runnning,blocked,teminated;
 
 
 创建一个线程对象,不可运行,只是对象;<br>
 就绪,调用start方法启动线程,线程进入线程队列排序,等待cpu服务;<br>
 运行,获取处理器资源,线程进入运行状态,调用run 方法;<br>
 阻塞,可中断方法;<br>
 终止,stop()或run()后;

-----------


# android 相关

### Android中存储类型;

>Android中存储类型;

1. Rom手机内存, SD卡; 都是本地磁盘
2. 存储SharePreferences
3. Sqlite 

>Ram 

所有文件都是存储在`/data/data/files`目录下;

>sp 文件存放在`/data/data/shared_prefes`目录下;
获取方法3种:

1. context类的getsharedpreference();
2. activit的getpreference();
3. preferenceManger类中的getDefaultSharedPreference()方法,使用当前应用程序的包名作为前缀来命名sharedpreferences文件;

> 如何将sqlite数据库(dictionary.db文件)和apk文件一起发布?:

将db文件复制到res raw文件夹中,此文件夹的文件不会压缩,可以直接提取该目录中的文件;

> 如何打开res raw 目录中的数据库文件?

在安卓中不能打开res raw 中的数据库文件,需要在程序第一次启动的时候将该文件复制到手机磁盘上(rom,sd卡)的某个目录上,然后在打开数据库文件;<br>
getResource().openRawResource()获得raw的inputStream对象,写入其他的目录相应文件,使用android sdk的`SQLiteDatabase.OpenOrCreateDatabase()`方法打开任意目录的sqlite数据库文件;


>SharedPrefrences的apply和commit有什么区别

commit为同步提交,apply为异步提交; commit()有返回值,apply无返回值;

### 进程通信相关知识 Bindler

- Android 为啥会采用 Binder？Binder 的机制又是怎样的？binder进程的优点，为什么只copy一次数据; Binder机制，共享内存实现原理;

linux 现有的所有进程ipc方式为: 管道,消息队列,共享内存,套接字,信号,信号量;

binder基于cs结构,指client-server结构 `结构稳定`,传输过程数据拷贝一次,`性能效率高`;为发送方添加Uid/Pid,`安全性高`;

[binder 机制原理](https://zhuanlan.zhihu.com/p/35519585)

- 传统linux 进程ipc:

- 进程隔离;

进程和进程间的内存是不共享的,ab进行数据交互,需要ipc;

- os将虚拟空间对进程空间划分,相互隔离;
	- 用户空间 User Space
	- 内核空间 Kernel Space


- 系统调用,用户空间访问内核空间资源唯一方式,linux 两级保护机制,0级供系统内核使用(内核态),3级供用户程序使用(用户态);
	- 用户态
	- 内核态

copy_from_user() //将数据从用户空间拷贝到内核空间
copy_to_user() //将数据从内核空间拷贝到用户空间

**传统的ipc通行原理**: 将发送方的数据存放在`内存缓存区`中,通过系统调用进入内核态,内核程序在`内核空间`分配内存,开辟内核缓存区,将数据从用户空间的内存缓存区拷贝到内核空间的缓存区;

A进程内存缓存区->内核缓存区->B进程内存缓存区;2次数据拷贝;

![](https://pic1.zhimg.com/80/v2-aab2affe42958a659ea8a517ffaff5a0_hd.jpg)

### android架构图

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

### fragment 相关

 oncreate : : onattach->oncreate->oncreateView-onActivityCreated (onViewCreated常用)

onStart :: onStart

onResume :: onResume

onPause :: onPause

onStop :: onStop

onDestory :: onDestoryView -> onDestory -> onDetach



### 资源管理,屏幕适配,昼夜模式

 换肤功能:
 
 - 定义两套主体resource / style;其中定义attr自定义属性,用于指定某些控件和布局的属性;
 - 切换主题的时候setTheme方法在setContentView之前; 
    - 1.当前页面主题的切换,调用Activity.recreate()方法; 其他页面主题切换,设置回到主页面清除所有页面达到换肤目的;
    - 2.其他页面在oncreate之前,设置主题activity.setTheme(R.style.xxx);

>使用 插件化动态加载技术实现换肤,aop编程,

values中加strings_zh的string;

>屏幕适配:

- dp = density * px; 独立像素密度;
- density = dpi / 160; 屏幕密度;
- dpi = (w平方+h平方)平方根/对角线英寸; 每英寸像素;

- 图片放在不同像素密度的文件夹`layout`中;
- 使用不同的限定符,尺寸限定符: 大小会自己适配对应布局; 最小宽度限定符(3.2): 最小宽度大于等于某值的时候,系统选择对应布局;(也可以使用values 将布局分开;) 单面板|双面板;
- 屏幕方向`限定符`: 布局别名;
- 自动拉伸位图,drawpatch图;
- 使用dp,sp单位;
- 使用`dimens适配`; 建立多个dimens文件放在不同的values中,使用不同的像素/dp适配,缺点就是google推荐使用的是dp,但这个还是px适配;
- 使用百分比布局,约束布局等新布局方式;

- 今日头条屏幕适配方案; 动态修改density值,达到修改dp的值;360dp的效果图作为参考;

> 多渠道打包是怎么做的？

- gradle中设置 productflavors 和 buildType 结合,类型和渠道不同组合包;
- androidmanifest文件中加入manifestPlaceHOlder元数据修改和存储当前打包渠道;

>美团打多种渠道包设置,meta-info中建立空文件,不用重新签名,标志一个渠道;

### 自定义 View

- 继承于view,viewgroup,重写构造方法,修改this,使用一个构造方法;
- 在构造方法中,获取自定义属性attrs,设置默认值;
- 重写onMeasure方法,测量大小;
- 重写onLayout方法,布局位置;
- 重写绘制方法onDraw,绘制;
- 重写事件方法,dispatchTouchEvent,onInterceptTouchEvent,onTouchEvent;
- 一般还会重写 onFinishInflate,及onSizeChanged

- ACTION_CANCEL什么时候触发，触摸button然后滑动到外部抬起会触发点击事件吗，在+ + 滑动 回去抬起会么

>滑出控件之外的时候手指离开触发cancel事件,可作为actionup事件看待;

- Canvas的底层机制，绘制框架，硬件加速是什么原理，canvas lock的缓冲区是怎么回事

- surfaceview， suface，surfacetexure等相关的，以及底层原理; SurfaceView与View

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

### View.post 为什么可以拿到宽高？

1.注意,必须在`dispatchAttachedToWindow`后运行;内部是在`onAttachToWindow`方法后执行,否则oncreate子线程不会执行;

2.在performTraversals()被执行的runnable事实上是被主线程的handlerpost到执行队列里了;

android是`消息驱动的模式`(EDA),view.post的runnable会被加入任务队列,并且等待第一次的`TraversalRunnable`执行结束后才执行,此时已经执行过一次measure,layout过程了;所以在后面执行post的runnable是,已经有measure的结果,因此可以获取view的宽高;

getRunQueue().executeActions(attachInfo.mHandler);在performTraversals()方法中,performTraversals是由另一个消息去驱动执行了,就是TraversalRunnable,我们post的runnable就是等待TraversalRunnable执行完后才去执行;而traversalRunnable进行measure,layout,draw,所以执行我们的runnable时,此时的view就是measure过了;



### 性能优化 （讲讲你自己项目中做过的性能优化）(apk瘦身,电量优化,网络优化)

减少apk的体积

体积占有大的是`lib`,`res`,`dex`; 减小apk体积主要是缩减so包,资源图片,控制代码质量;

- 使用`progruard 混淆`,不过注意反射机制会被混淆破坏,应该充分回归测试;
- 使用`Android lint` ,剔除没有使用的资源,analyze->inspecting Code
- 清理Assert文件夹, 不编译的文件夹;
- 用`代码代替图片`,shape,RotateDrawable 一张图片,属性动画;layer-list 背景图; 重用资源tint;
- `放弃一些图片资源`, defaultConfig 配置 resConfigs 指定只打包某些资源;
- `压缩图片` [pngquant](http://www.cnblogs.com/soaringEveryday/p/5148881.html)
- `so的优化`,defaultConfig 配置 ndk{} abiFilters 
- 对三方库重新定制,重新打jar包
- 动态加载技术(插件化);

###进程保活

 进程有优先级,手机杀进程有内存阈值 low memory killer, oom_adj值 越大占用物理内存越多越先被杀,降低oom_adj值就可以保活;

- 开启一个像素的Actvity, 系统一般不会杀前台进程,锁屏时开启一个activity,大小1像素透明无切换动画,监听系统锁屏广播,开屏关闭;
- 前台service,前台进程保活,api<18: startForeground(ID,new Notification())发送空的notification,图标不会显示; api>18,需要提升优先级的serviceA,必须有smallIcon, 启动一个InnerService,两个服务同时startForeground,绑定同一个ID,然后stop innerService,cancel调通知栏图标;
- 进程相互唤醒;
- JobScheduler ,系统自带的
- NotificationListenerService 使用系统服务,不过需要权限`BIND_NOTIFICATION_LISTENER_SERVICE`;
- 后台播放无声音频mediaplayer;

### 说下冷启动与热启动是什么，区别，如何优化，使用场景等。

>冷启动:

 启动应用时,后台没有该应用的进程,重新创建一个新的进程;<br>
重新创建一个新的进程,创建和初始化application,在创建MainActivity类; 

`冷启动白屏/黑屏`的原因: 打开一个app,如果application还没有启动,systemserver系进程会为activity调用zygote fork一个新的进程,会消耗一段时间,windowmanager会加载app里主题样式的windowBackground作为预览元素;然后才去真正的加载布局; (冷启动后,创建application,启动ActivityThread入口等一系列绘制)

设置Theme的windowBackground为logo图解决默认样式;

减少application参与业务的操作;

>热启动:

 后台已有该应用的进程,从已有的进程来启动应用;<br>
从已有的进程中启动,直接走MainActivity;

### anr原因,及如何排查

 anr(application not responding) 一般有三种类型,主线程中<br>
`KeyDispatchTimeout(5 s)` 类型按钮和触摸事件无响应

`BroadcastTimeout(10 s)` 无法完成;

`ServiceTimeout(20 s)` 无法完成;

高耗时操作,图像变化等; 磁盘读写,数据库读写等; 大量创建新对象等 容易引起anr;

>如何避免 

1. ui线程尽量只做Ui相关的工作;
2. 耗时的操作 放在单独的线程处理;
3. 尽量用handler来处理UiThread和别的Thread的交互;

查看方法: adb shell cat /data/anr/traces.txt >/mnt/sdcard/traces.txt; adb pull /data/anr/traces.txt /local/traces.txt;



### 性能优化工具

- 优化工具TraceView

做热点分析,得到单次执行最耗时的方法,执行次数最多的方法;

代码中添加: Debug.startMethodTracing(), Debug.stopMethodTracing();<br/>
>打开profile->cpu->record->stop->查看top down/bottom up;

- 启动优化加速:

优化application,activity创建和回调等过程;

- 使用主题防止白屏;
	- 使用 windowBackground 主题属性预先设置一个启动图片,oncreate之前使用setTheme设置或在清单中设置;
- 减少application的oncreate中逻辑或者延迟加载;
	- 可以异步的使用异步,不能异步的尽量延迟;
	- 耗时任务可以使用异步加载IntentService;
- multidex优化(5.0以上默认使用art(aot),而不是diavik(jit),art在安装时已将dex转换为oat了,无需优化)
	- 启动时开启一个进程进行multidex的第一次加载,即dex提取和dexopt操作;
	- 主线程在后台等待,优化进程执行完毕后通知主进程继续执行,此时执行到multidex是,则已经发现提取优化的dex,直接执行;

- 查看模拟器的sp和sqlist文件,布局嵌套层数,加载时间等;

查看文件: adb shell 进入linux的命令行,data/data/databases;

>layout inspector工具查看嵌套层数;

- mat分析方法以及原理

andrdid studio 采用的是`android profiler` 
 - 反复操作,堆的大小一直增大,则内存泄露;
 - 保存为hprof文件,使用tools中的hprof-conv 工具转换;
 - 使用android MAT工具检测泄露;

>使用库leakcanary检测泄露;


### 内存优化(oom,泄露,图片压缩)

>oom (outofmemoryerror),

原因是无法分配内存导致的错误,可由于内存泄漏导致内存溢出;

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

- lrucache主要原理是把最近使用的对象用强引用存储在linkhashMap中,将使用少的对象在缓存值达到预设值前从内存中移出;<br>

- 布局优化
  - 较少过渡重绘;
  - 较少xml的inflate时间,使用new View()方式创建;
  - 公用的布局尽可能的重用;

>bitmap的回收需要自己处理:

- 临时的bitmap;
- activity中的bitmap对象,如果是成员变量,需要在onDestory中释放;
- 使用完毕后,立即手动释放资源,尽量不要依靠GC回收;

recylce主要是释放`native分配`的内存,但一般情况下图像数据是在jvm中分配的,调用recycle并不会释放这部分内存;如果使用createBitmap创建的bitmap且没有别硬件加速过,recycle产生的意义就比较小,可以不主动调用;而象被硬件加速draw的和截屏这种在jvm中分配内存的需要调用方法来释放图像数据; 

### recyclerview的源码及优化:

绘制:

 - measure 中设置 `match_parent` 节省measure的测量工作;
 - layout中,`dispatchLayoutStep` 分三步
 	- dispatchLayoutStep1: 选择动画相关; dispatchLayoutStep2: 真正的布局;fill方法; dispatchLayoutStep3: 触发动画;
 	- `fill`: 有滑动进行一次回收,填充子view,不断添加子view直到 `没有剩余空间`或者`添加的view是focusable且设置stopOnfocusable为true`停止填充; 其中,主要调用`layoutChunk`方法填充,计算剩余空间,有滑动的情况进行回收;
 	- `layoutChunk` 子view处理方法流程: 创建子View -> 子View添加到RecyclerView中->测量子View->对子View进行布局操作;
 		- 创建子View : `layoutState.next()` 调用 `Recycler.getViewForPosition`方法,而Recycler 就是rlist的缓存关键类,从四大缓存中获取ViewHolder; 没有获取vh,那么调用了`onCreateViewHolder`创建view;接下来调用`bindViewHolder`;设置布局参数;
 		- 添加子View: 回调`onViewAttachedToWindow`方法;
 
 ```
	
 	LinearLayoutManager.layoutChunk() -> Recycler.getViewForPosition() -> Adapter.onCreateViewHolder() -> 设置ownerRecyclerView -> Adapter.onBindViewHolder() -> 设置LayoutParams -> RecyclerView.addView() -> Adapter.onViewAttachedToWindow() -> LayoutManager.measureChildWithMargins() -> LayoutManager.layoutDecorated()
 ```

回收

 用户触发滑动,recyclerview进行一次view的回收,往rlist填充子View,又在进行了一次回收;

 滚动的方法调用: 
 
	 `onTouchEvent -> scrollByInternal -> LinearLayoutManager.scrollVerticallyBy -> LinearLayoutManager.scrollBy -> LinearLayoutManager.fill`

优化: 

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
>上滑的时候在从数据库中取出显示,remove下方的item至数据库中,但是一旦删除了某个item后,所有数据库的item都要变;



### 图片处理 (bitmap 压缩策略,裁剪,复用)

### 启动时间优化

### 卡顿检测优化(避免频繁gc,viewholder)

### Http[s]请求慢的解决办法（DNS、携带数据、直接访问 IP）

### 缓存自己如何实现（LRUCache 原理）

### 图形图像相关：OpenGL ES 管线流程、EGL 的认识、Shader 相关

### SurfaceView、TextureView、GLSurfaceView 区别及使用场景

### 音视频(音频解码器,audiotrack,视频解码,opengl绘制,视频编辑转码,视频滤镜)

### 动画、差值器、估值器（Android中的View动画和属性动画 - 简书、Android 动画 介绍与使用）

帧动画,补间动画,属性动画,item布局动画,过渡动画;

### Handler、ThreadLocal、AsyncTask、IntentService 原理及应用

### Gradle（Groovy 语法、Gradle 插件开发基础）

### 热修复、插件化 (class dex,classloader原理,插件化原理,插件化框架学习)

#### 热修复(asm插桩,类加载方式,底层替换)

###  组件化架构思路 (页面路由,cc组件化)

>[CC组件化](https://github.com/luckybilly/CC)

### 插件化

就是apk未安装,通过反射,aapt直接获取apk上的字节码和资源,启动act;

### 系统打包流程 gradle

>release打包,签名,加固,渠道包配置productFlavors;

### Android 有哪些存储数据的方式。

### SharedPrefrence 源码和问题点；

### sqlite 相关

SQLiteOpenHelper继承类 onUpdata();不升级的走onCreate方法,其他的就是sql语句了;

###如何判断一个 APP 在前台还是后台？

###混合开发(rn,fuchsia+flutter+dart,weex,js引擎,渲染引擎)

>webview了解？怎么实现和javascript的通信？相互双方的通信。@JavascriptInterface在？版本有 bug，除了这个还有其他调用android方法的方案吗？

- api>17 情况下,js 调用java: `webview.addJavascriptInterface(Object obj,String name)` 添加类,方法由@JavascriptInterface注解;

js中直接使用window.name.getXX() obj中定义的方法使用;<br>

java调用js: webiview.setWebChromeClient(new WebChromeClient(){});` webview.loadUrl("javascript:toast()");`直接使用loadUrl调用script中的方法;


- api <17时, 使用上述方法会引起漏洞,17以上通过@JavaScriptInterface修改,17以下使用消息框函数解决;

`webview.setWebChromeClient(new WebChromeClient(){ 重写onJsPrompt 方法通信})`; 其中,`onJsAlert`,`onJsComfirm`,`onJsPrompt`前两个带boolean,后一个带string,可以传递json数据;

- 定义json格式的字符串;
- js中封装一个方法,通过prompt方法`window.prompt(text,defaultText)`实现将string传给java层的onJsPrompt方法中,java对string 进行json 解析,通过反射调用逻辑;
- java执行完毕,定义回传json,java通过JsPromptResult传给js`result.confirm("callable")`;

### aop(aspectJ,apt,javassist)

aop 切面编程,oop将问题划分为单个模块;aop就是把涉及到众多模块的某一类问题进行统一管理; 

android aop 就是通过`预编译方式`和`运行期动态代理`实现程序功能的统一维护的一种方式; 

- apt(Annotation processing tool): dagger2,dataBinding,butterknife;

步骤: 1.定义编译期间的注解; 2.继承AbstractProcessor的接口实现编译期间代码逻辑; 3.gradle添加apt的插件以及依赖,使用此注解的地方重新编译即可;

- acpectJ: `advice 通知`,`joint point 连接点`,`pointcut 切点`,`aspect 切面(pointcut+advice)`,`weaving 织入`; 主要原理是动态代理;

- javassist: hotfix,Savior(instantRun); 编译期间修改class文件; 还有就是ASM(组件化CC框架使用的就是这个aop),直接修改编译后的class文件,class->dex文件时处理;

`Transfrom` 也是Task,在项目打包成dex之前会添加到Task执行队列中,刚好可以在Transform api处理 修改class 文件,转化为dex,达到切面编程的目的; 

- 在Transfrom这个api出来之前，想要在项目被打包成dex之前对class进行操作，必须自定义一个Task，然后插入到predex或者dex之前，在自定义的Task中可以使用javassist或者asm对class进行操作。

- 而Transform则更为方便，Transfrom会有他自己的执行时机，不需要我们插入到某个Task前面。Tranfrom一经注册便会自动添加到Task执行序列中，并且正好是项目被打包成dex之前。


步骤: 1.定义一个插件module; 2.自定义Transform(gradle api中的类);3.在tramsform里处理task,input拿到上一个task处理的结果,处理完毕后输出outputs给下一个task处理;4.transform使用javassist或者asm操作字节码,添加新的逻辑或者修改原有逻辑;5.gradle中添加插件的使用,打包时候运行此task对class进行处理;


![](https://upload-images.jianshu.io/upload_images/751860-0641778f0bc265ad.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/540)

- 热修复的原理:[classloader](https://blog.csdn.net/u010386612/article/details/51131642)

![](https://img-blog.csdn.net/20160314140715580)

Android系统是通过`PathClassLoader`加载系统类和已安装的应用的。 
 `DexClassLoader`则可以从一个jar包或者未安装的apk中加载dex ;

dex的分包原理也是classloader: 
dalvik的限制,app功能越来越复杂时,出现 
1.编译失败,因为dvm中存储方法id用的是short,dex中方法不能超过65536个;
2.apk在android2.3之前机器无法安装,dex文件过大(dexopt的内存只分配了5m);

`dexElements`保存了dex的数组;finClass方法中,每个dex就是dexFile的对象,遍历dexElement,通过DexFile加载Class文件,加载成功返回,否则返回null;

热加载: 通常情况下,dexElements只会有一个元素,就是apk安装包的classes.dex,我们可以通过反射将一个外部的dex文件添加到dexElements中,这就是dex的分包原理,也是热修复的原理;
(**简**: DexClassLoader可以加载外部的dex包,可以放在系统dex的前面)

如果两个dex中存在相同的class文件会怎样; 遍历中,如果两个dex存在相同的class情况下,先遍历的dex中找到class会直接返回,不会再接下去的dex中查找;所以热修复利用这一特性,一个app出现bug后,将bug修复后,重新编译打成dex,插入到`dexElements`dex数组的前面,那么出现bug的那个类就会被覆盖,app正常运行; <br>
(**简**: app启动时,如果有多个dex中存在相同的class,先找到的类会覆盖后找到的类;)

将修复后的类打包成dex,通过反射将修复的dex插入到dexElements; 
`jar -cvf patch.jar com` 将当前com目录(类的全限路径)打包成jar;
`dx --dex --output=patch_dex.jar patch.jar`将jar打包成dex;

`BaseDexClassLoader–>pathList–>dexElements`
在应用启动的时候application中处理 1.apk的classes.dex可以从应用本身("dalvik.system.BaseDexClassLoader")的DexClassLoader中获取; 2. path_dex的dex需要new一个DexClassLoader加载后获取;3.分别通过反射取出dex文件,重新合并成一个数组,赋值给应用本身的Classloader的dexElements;<br>
(**简**: 先获取应用自身的classes.dex,在new DexClassLoader获取patch中的dex,反射合并成一个数组,设置进入到应用自身的dexElements中;)

注意: apk安装时会将dex优化成odex拿去执行,会执行**预校验**`CLASS_ISPREVERIFIED`,具体的校验是a和b类都处于同一个dex,并且直接引用了b,a类别加上此flag,不能引用其他dex的类,否则`IllegalAccessError`; 换句话说，只要在static方法，构造方法，private方法，override方法中直接引用了其他dex中的类，那么这个类就不会被打上CLASS_ISPREVERIFIED标记。<br>
(**简**: 让所有的类都不要被打上预校验的标记,防止不能跨dex调用,所以使用aop技术修改class文件,在gradle中通过transform 修改,在所有的类的构造函数中加入一行打印逻辑,为了能用其他dex的类,所以最终的dex是二种dex拼接的数组, 一个是补丁包dex,不过补丁包dex中包含一个空类,修改原app的dex调用此空类为了防止预校验的标记的类dex,一种是原app的dex)


![](https://upload-images.jianshu.io/upload_images/5105267-593595bf220bb46b.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/452)

优点: 避免插桩操作;

>- 新dex与旧dex通过`dex差分算法`生成差异包 patch.dex;
>- 将patch.dex 下发到客户端,客户端将patch.dex与旧的dex合成为新的全量dex;
>- 将合成后的全量dex插入到dexElements前面(与Qzone的实现方式即上面的实现方式一致;)完成修复;

dex结构:  

![](https://upload-images.jianshu.io/upload_images/5105267-2651126f18a9e91f.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/800)


![](https://upload-images.jianshu.io/upload_images/5105267-e77528772f8096a0.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/387)

>- 对于dex文件中的每项Section，遍历其每一项Item，进行新数据与旧数据的对比（新旧数据的对比方法是oldItem.compareTo(newItem)，结果小于0记为DEL，大于0记为ADD）设置item的`标记位`;
>(Tinker中会遍历patchOperationList，将同一个位置既有DEL标识又有ADD标识的情况，替换为REPLACE标识，最后将ADD，DEL，REPLACE标识数据分别记录到各自的List中,最后将操作记录列表写入补丁patch.dex中;)
>- 生成patch.dex后，进行下一步是将patch下发到客户端后合成全量的dex，合成dex这部分内容此处不再展开说，是差量过程的反过程;
>- 合成dex结束加载全量dex的流程: 打完补丁的全量dex的加载是在Application启动后的onBaseContextAttached完成的,无法对application类进行修复,tinker中使用代理的aplication完成对application的实际逻辑处理,流程就是通过反射获取到dexElements,将合成的全量dex插入到dexElements数组前面,完成修复工作;

### NDK 相关(cmake,ndkmake)

### 监控(apm性能检测,webview性能检测,leakcanary内存泄露检测)

### jenkins 持续集成

### handler原理 (ThreadLocal)

简单来说就是每个线程Loop轮询器,loop中又有一个消息队列, loop处于死循环一致轮询消息队列; 而Thread中有ThreadLocal,存储着该Loop对象,只要把消息发送到ThreadLocal中的loop中的消息队列中,就可以运行消息,注意ThreadLocal特性;

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

### asynctask 原理

>android中常用的异步调度操作;

- Timer,TimerTask; java中使用异步的定时;
- CountDownTimer;
- Handler + Thread;
- HandlerThread;

### handlerThread

HandlerThread 继承Thread,内部使用Handler的Thread,使用quit或quitSafety退出消息循环;

### intentService

继承service的处理异步请求的服务类,有工作线程处理耗时任务,任务执行完后会自动停止,不需手动控制或stopself;<br>
 不需要自己new Threrad; 不需要考虑什么时候关闭Service;

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

### measure,layout,draw 及流程

###   加密 ,解密

md5 摘要算法,不可逆 sha1;用于客户端的用户密码加密;

对称加密: AES,DES ,3DES

非对称加密: RSA

Rsa 客户端使用公钥加密,服务端使用私钥解密;注意：使用RSA加密之前必须在AndroidStudio的libs目录下导入bcprov-jdk的jar包

###   编码, 解码

Base64,UrlEncoder

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

旋转时的生命周期: <br>
不设置`android:configchanges`时,切屏会重新调用各个生命周期,切横屏会执行一次,切竖屏会运行两次;<br>
设置Activity的`android:configChange="oriention"`,切屏会重新调用生命周期,横屏和竖屏都会只执行一次;<br>
设置activity的`android:configChange="orientation|keyboardHidden"` 时,切屏不会重新调用生命周期,只会执行onConfigurationChanged()方法;

> act 跳转方式及Intent属性

显示跳转: 本程序的跳转;<br>
隐式跳转: 跳转到另一个程序页面;action 和 categoty和data 匹配;

> 设置一个act为窗口的模式;

android: theme ="@android:style/Theme.Dialog" <br>
android:theme="@android:style/Theme.Translucent"设置透明;

> activity, finish调用后其他声明周期还会走吗;

在oncreate中finish : oncreate->ondestory
onstart方法: oncreate,onstart, onstop,ondestroy;

### 任务栈启动模式详解,Activity的onNewIntent;
   
 程序打开就创建了任务栈,存储当前程序的activity,任务栈顶的act跟用户进行交互;当所有的任务栈中所有的act清除出栈,任务栈会被销毁,程序退出;
 但是任务栈每开启一次都会添加act,造成数据冗余,oom;

standard: 创建一个新的act实例,当前任务栈;

singleTop: 可以有多个实例,act在任务栈顶,启动相同的act实例,不会创建新的实例,调用onNewIntent()方法;

singleTask: 任务栈唯一,只有一个实例,启动时先在系统中查找affinity与它的属性值taskAffinity相同的任务栈是否存在,如果存在,将act之上的其他act destory调并调用act的onNewIntent()方法;不在则在新的任务栈中启动; <br>
>如果想要这种启动模式的act在新的任务栈中启动,设置单独的taskAffinity属性,这种启动模式的act就会跟启动它的act不在同一个task中;

singleInstance: 全局唯一,只有一个实例,这个实例独立运行在一个task任务栈中,不允许有其他的act存在;

>`affinity` 属性设置activity的任务栈所属;

 注意singleTask,singleInstance的onActivtyResult方法失效 startActivityForResult resultCode = 0??!

使用SingleTask设置taskAffinity属性或者使用SingleInstance 启动模式,因为不是处于同一个任务栈,startActivityForResult失效;


### 广播 (本地广播)

### service

 >生命周期: <br>
 startService: 启动service与启动的act无关;

onCreate-> onStartCommand() -> 开始work[stopSelf,stopService] ->onDestroy

bindService: 通过回调获取service的代理对象和service交互,解绑销毁serice;

onCreate -> onBind() ->开始work[unBindService] ->onUnbind() ->onDestory 

同时使用startService和bindService: 

oncreate ->onStartCommand ->绑定服务bindservice 开始work ->onBind ->解绑服务UnBindservice ->onUnbind ->停止服务stopself,stopService ->onDestory 

 在service创建子线程优于act<br>
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

---

在ContextImpl中创建,实现为applicationContentResolver (extend contentResolver) 

![](https://img-blog.csdn.net/20160608135556716)

contentprovider没有发布的化,启动contentprovider所在的那个进程;即从应用启动入口main方法进入;

![](https://img-blog.csdn.net/20160608143836329)

contentprovider实现是跨进程调用,首先当前进程通过ActivityThread跨进程AMS通信获取contentprovider,如果contentprovider还没有被创建,则调用startProcessLocked方法,启动contentprovider所在的进程,获取到contentprovider去curd; 其中,启动contentprovder所在进程从main进入,也是通过AMS的RPC机制创建contentprovider;

### intent

intent 同时匹配action类别,category类别,data类别;

### sp  dp ,屏幕适配相关

dpi = 长平方加宽平方的根号/对角线英寸; 每英寸像素 
density= dpi/160; 屏幕密度
px = dp * density; 独立像素密度

>sp 独立像素缩放,文字大小;

今日头条团队适配方案,动态改变density的值,达到适配的目的;

### Runnable与Callable、Future、FutureTask的区别

Runnable 没有返回值,可将耗时操作写在里面,使用线程池去运行; Callable 有返回值; Future 类似于凭据,对于具体的runnable,callable任务的执行结果进行取消查询是否取消,获取结果,设置结果等操作; futuretask是future,runnable,还可以包装callable,增强型的future;

### android线程池,ThreadPoolExecutor 

- corePoolSize :核心线程数;
- maximumPoolSIze :最大线程数;
- keepAliveTime : 非核心线程的超时时长;
- unit: 时间单位;
- workQueue: 任务队列;
- threadFactory: 线程工厂,创建新线程的功能;

`RejectedExecutionHandler` 线程数量达到线程池最大值,通知调用者;有几种策略;

>android线程池的分类: 

- FixedThread newFixedThreadPool()方法创建;线程数量固定,只有核心线程;
- CacheThreadPool newCachedThreadPool()只有非核心线程;
- ScheduledThreadPool newScheduledThreadPool() 核心数量固定,非核心数量没有限制;
- SingleThreadExecutor 只有一个核心线程;防止并发;

### Java 中的引用方式，及各自的使用场景

强引用,软引用,弱引用;虚引用;

- 强引用: new object;常用;
- 软引用,内存不足时,才会回收;
- 弱引用,一旦发生gc,就会回收;
- 虚引用,提醒,检测对象是否删除,可用于一般二次关闭; 

>软引用,弱应用,一般和引用队列关联,对象被gc时,加入到关联的引用队列中;其中 虚引用必须和引用队列一起使用;
> 引用被回收后会加入到指定的ReferenceQueue;

### requestLayout，invalidate，postInvalidate区别与联系

requestLayout: measure,layout,draw; 触发scheduleTraversal,因为最终调用ViewRootImpl的performTraversal方法;

invalidate: draw; 触发scheduleTraversal,但是没有设置measure,layout的标记位,不会measure,layout;

postInvalidate: 非UI线程,异步执行draw;

### RecyclerView与ListView(缓存原理，区别联系，优缺点),RecyclerView缓存原理，局部刷新原理

 ListView是两级缓存,RecyclerView是四级缓存; listview通过`mActiveView`和`mScrapViews`实现两级缓存; recyclerview通过`mAttachedScrap`,`mCacheViews`,`mViewCacheExtension`,`mRecyclerPool`;  

- mActiveViews和mAttachScrap功能相似, 在于快速重用屏幕上可见的列表项itemView,不需要重新createView和bindView;
- mScrapView和mCacheViews+mRecyclerPool功能相似,在于缓存离开屏幕的view,让即将进入屏幕的itemview重用,bindView;
- recycelrview的优势在于mCacheView的使用,屏幕外的itemview进入屏幕时无需bindview快速重用; mRecyclerViewPool可以供多个RecycelrView共同使用;

![](./knowjpg/640.png)
![](./knowjpg/641.png)
![](https://blog-10039692.file.myqcloud.com/1502175323258_9041_1502175323346.png)
![](https://blog-10039692.file.myqcloud.com/1502175348747_4325_1502175348864.png)

- 缓存不同,RList 缓存RecyclerView.ViewHolder(view,viewholder,flag) ,List缓存的是View;

- 局部刷新,数据源改变时的缓存的处理逻辑,listview是将所有的mActiveViews都移入了二级缓存mScrapViews,Rlist对每个View修改标志位,区分是否重新bindView; Rlist从Recycler中获取合适的view;

> ListView回收机制`RecyclerBin`, 实现两级缓存机制,

- `View[] mActiveViews` 缓存屏幕上的view,在该缓存上的view不需要调用getView; 
- `ArrayList<View>[] mScrapViews` 每个Item Type对应一个列表作为回收站(itemview不能为负),缓存由于滚动而消失的view,此处的view如果被复用,会以参数的形式传给getview重新复用;  布局函数为`layoutChildren()`,fillXXX()对itemview填充,内存调用`makeAndAddView`判断使用哪种缓存view; 

>recyclerView回收机制`Recycler`,实现四级缓存机制,

- `mAttachedScrap` 缓存屏幕上的ViewHolder,该缓存不需要bindView;
- `mCacheViews` 缓存屏幕外的ViewHolder,默认为2个,不会bindview,会被缓存至mRecyclerPool中;
- `mViewCacheExtensions` 用户定制,可bindView;
- `mRecyclerPool`缓存池,可以多个RecycelrView公用,需要bindview重用;默认上限5;  布局函数入口`onLayoutChildren()`,getViewForPosition 判断使用哪种缓存holder; mAttachScrap和mCacheViews就是`ScrapHeap`

>recyclerView 组成 : Adapter ,LayoutManager, ItemAnimator, ItemDecoration;

ItemDecoration : onDraw: 绘制item之前调用绘制分割线;getItemOffset: onMeasure中调用,measureChild,每个item的大小加上装饰的大小; onDrawOver: 绘制item之后调用;

- ItemAnimator : 防闪屏动画 `((SimpleItemAnimator)rv.getItemAnimator()).setSupportsChangeAnimations(false)` 禁用change动画;DefaultItemAnimator 提供多种动画,remove,move,change,add ,顺序执行remove,move+change,add;

- 拖拽,侧滑删除: ItemTouchHelper : onSwiped,onMove,getMovementFlags,onSelectedChanged,clearView,isLongPressDragEnabled;

- 嵌套滑动机制: 子View实现`NestedScrollingChild`,父View实现`NestedScrollingParent`,RecyclerView实现了`NestScrollingCHild`,CooridnatorLayout 实现了`NestScrollingParent`接口;

### Application生命周期

 onCreate: 程序创建的时候运行;
 
 onTerminate: 程序终止的情况运行;
 
 onLowMemory: 低内存的时候运行; 与level一样TRIM_MEMORY_COMPLETE;
 
 onTrimMemory: 内存清理的时候运行;当前内存状态;
 
 onConfigurationChange:  配置改变;

调用OnTrimMemory区别在于 如果应用占有内存较小,可以增加不被杀掉的几率,从而快速的回复(如果不被干掉,启动的时候就是热启动,否则就是冷启动,速度差2~3倍;)

onTrimMemory等级TRIM_MEMORY_UI_HIDDEN ,表示应用即将进入后台; 应用切换至前台 使用app的registerActivityLifecycleCallbacks() 在acticity onresumed方法得到状态 ;

![](https://img-blog.csdn.net/20180716122506349?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3hvdHR5/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)

### 数据库知识

- 左连接和右连接,各连接的区别

左连接where只影响右表,右连接where只影响左表;  左连接后的结果是显示左表的所有数据和右表中满足where条件的数据,没有则null(交叉的部分);

右连接显示的结果是右表中的所有数据和左表中满足where条件的数据,不满足null(交叉的部分); 内连接是交集,其他置null; 联合查询UNION;

左连接只影响左表,右连接只影响右表

- 数据库的ACID

原子性,一致性,隔离性,持久性;

隔离级别(悲观锁和乐观锁): 脏读,不可重复读,虚读; (read_uncommited,read_commit,repeatable_read,serializable)

### 用过数据库么？如何防止数据库读写死锁？ ContentProvider && 单例实现。

多个SqliteOpenHelper,多数据库操作,读写竞争; 

- 1.创建一个SqliteOpenHelper单例;
- 2.使用contentprovider,声明android:multiprocess="false",多进程单实例;
- 3.单进程情况下,使用greendao(objectBox) 因为只有一个sqliteopenhelper,多进程下还是使用contentprovider;

- 自旋锁

CAS(乐观锁) 一个线程获取锁的时候,如果锁已经被其他线程获取,该线程循环等待,不断判断锁是够能成功获取,直到获取锁才会退出循环;
sychronized为互斥锁,会进入睡眠状态;

> 乐观锁和悲观锁

悲观锁 sychronized; 适合写比较多;

乐观锁: 版本号机制或CAS算法(compare and swap);

- 版本号机制(有点像令牌机制)

无锁算法,version++后就不能更新;

- CAS算法 (非阻塞同步,Atomic包下类) 适合写比较少的情况

读写内存值V,进行比较的值A,拟写入的新值B;<br>
 当且仅当V的值等于A时,CAS通过原子方式用新值B更新V值(比较和替换是一个原子操作),自旋操作,不断重试;

cas缺点: 1.ABA问题(a>b>a,无法检测);2.循环时间长开销大;3.只能保证一个共享变量的原子操作(AtomicReference,多个变量放在一起);

## android 最新版本及最新技术相关

![](https://upload-images.jianshu.io/upload_images/4064751-1d2e001a9133ae9a.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/964)

![](https://upload-images.jianshu.io/upload_images/4064751-e63fc95fb46afc38.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1000)

### Android两种虚拟机区别与联系 (dvm,art)

.java -> .class -> .jar ;  基于栈的架构,每次访问数据cpu都要到内存中取到数据;

 .java->.class->.dex; 基于寄存器的架构,cpu直接从寄存器中读取数据,寄存器是cpu的一块存储空间;

`dalvik jit`(just in time)编译器,在运行时,实时将一部分的dalvik字节码翻译成机器码,jit只翻译一部分,内存小;

5.0后 使用`ART(AOT)`代替DALVIK(JIT)编辑器 ,ahead of time编译器,应用的安装期间,将dex字节码翻译成机器码并存储在设备上,不在需要jit编辑,执行速度快,但需要更多的空间;  新的编译器可支持预先编译(AOT),即时编译(JIT)和解释代码;

### **binder ipc 实现原理**: 

- 基于linux的`动态内核可加载模块机制`: `Binder Driver`就是动态添加内核模块运行在内核空间; 

- `内存映射`: mmap() 内存映射方法,物理介质文件系统,用户空间的一块内存区域映射到内核空间;映射建立后,用户对内存区域的修改可以直接反映到内核空间,反之也成立;

- binder中的ipc通信: 
	- binder驱动在内核空间创建一个数据接收缓存区;
	- 在内核空间开辟一块内核缓存区,建立内核缓存区和内核中数据接收缓存区之间的映射关系,以及内核中数据接收缓存区和接收进程用户空间地址的映射关系;
	- 发送方进程通过调用copyformuser() 将数据拷贝到内核中的内核缓存区,由于存在映射,相当于把数据发送到接收进程的用户空间,完成一次进程间的通信;

![](https://pic4.zhimg.com/80/v2-cbd7d2befbed12d4c8896f236df96dbf_hd.jpg)

### **binder 通信模型** 实名binder;

c/s结构,client进程和server进程和servicemanager 都运行在用户空间,binder driver 运行在内核空间; 

servicemanager (DNS)和binder driver(路由)是由系统提供,client 和server 是由应用程序实现;

servicemanager: 将字符形式的Binder名字转化为Client中对该Binder的引用,使得Client能够通过Binder的名字获取对Binder实体的引用;server向servicemanager注册一个binder,相当于一个注册中心;

servicemanager是一个进程,所以server注册binder时也是通过ipc方式,也是Server端,一个A Server想向servermanager注册自己的Binder就必须通过这个0号引用和ServiceManger的Binder通信; 
所有其他的Server或者Client相对于ServiceManager都是一个Client; 
0号引用指的是A进程`BINDERSETCONTEXT_MGR`命令将自己注册成Servicemanager中的binder时,binder driven会自动创建的Binder;(相当于域名服务器地址)

server向servicemanager注册了Binder以后,client通过名字获取Binder的引用,也是利用保留的0号引用向servicemanager请求访问某个Binder;所以Server中的Binder实体有多个引用,一个servicemanager保留的引用和多个Client中保留的引用;


![](https://pic3.zhimg.com/80/v2-729b3444cd784d882215a24067893d0e_hd.jpg)

### **binder 通信过程** (与Dubbo 好像!!)

- 一个进程使用 `BINDERSETCONTEXT_MGR`命令通过binder driver 驱动将自己注册成为servicemanager ; 单独的进程(用户空间);

- server通过 binder driver(内核空间) 向 servicemanager 中注册Binder(`Server中的Binder实体`),binder driver 为binder创建内核中的实体节点和servicemanager对实体的引用;将名字和引用传给servicemanager查找表;

- client通过名字,在binder driver下从servicemanager中获取到对Binder实体的引用,通过这个引用实现与Server的通信;

![](https://pic4.zhimg.com/80/v2-67854cdf14d07a6a4acf9d675354e1ff_hd.jpg)

![](https://pic2.zhimg.com/80/v2-13361906ecda16e36a3b9cbe3d38cbc1_hd.jpg)

>binder:

- binder是一个进程间通信的机制;
- Server: binder是Server中的binder实体对象;
- Client: binder是Binder代理对象,Binder实体对象的远程代理;
- 传输过程: 可以跨进程传输的对象;


### android 的几种进程

前台进程: 即与用户正在交互的act和act用到的service,优先级最高,最后被杀死;

可见进程: 处于暂停状态的act或者绑定的service,即被用户看见,但失去焦点不能交互;

服务进程: 运行着startService方法启动的service,不可见,但是是用户关心的,音乐或者下载文件;

后台进程: 其中运行着执行onStop方法而停止的程序,不是当前用户关心的,后台挂着的qq;

空进程: 不包含任务应用程序和程序组件的进程;

>避免被杀死:<br>
1. 调用`startForeground`,让service所在的线程成为前台进程;

2.Service的onStartCommond返回`START_STICKY`(无Intent值,重启)或`START_REDELIVER_INTENT`(有intent值,重启)(还有一种是`START_NOT_STICKY`(不重启))

3.Service的onDestroy里面重新启动自己

### 使用服务而不是线程?

 进程中运行着线程,android应用程序把所有界面关闭时,进程还没有被销毁,处于空进程状态,很容易被销毁; 服务不容易被销毁,如果非法状态下被销毁了,系统会在内存够用时,重新启动;<br>

 服务进程优先级比空进程优先级高,不容易被销毁,拥有service较高的优先级;

### bindler序列化和反序列化的过程和使用过程;

> Binder的构成有几部分？

 进程空间中分为用户空间和内核空间,用户空间的内容不可享,内核空间的数据可共享,为了保证安全性和独立性,一个进程不能访问另一个进程,bindler就是充当两个进程间(内核空间)的通道.binder跨进程机制模型基于Client-Server模式;<br>

 定义四个角色: Server ,Client ,ServiceManger, Binder驱动;<br>

 server: 提供服务的进程;<br>

 client: 使用服务的进程;<br>

 serviceManger: 管理Service的注册和查询;<br>

 binder驱动: 虚拟设备驱动,连接3者的桥梁; 传递进程间的数据,实现线程控制;<br>

 binder驱动和servicemanager属于android基础架构,client进程属于android应用层(开发者实现),开发者只需自定义client和server进程并显示使用3个步骤(注册服务,获取服务,使用服务),最终借助android的基本架构功能就可完成进程间通信;

### binder的优点

高效(数据拷贝一次),稳定(基于CS结构),安全性高(Uid/Pid)

对比Linux的其他进程通行方式(管道,消息队列,共享内存,信号量,socket),binder机制优点: <br>

高效 : <br>

1. binder数据拷贝只需要一次;

2.通过驱动在内核空间拷贝数据，不需要额外的同步处理;<br>

安全性高: <br>

binder机制为每个进程分配了`UID/PID` 作为鉴别身份的标志,并且在binder通信是会根据UID/PID进行有效性检测;

1. 传统的进程通信方式对于通信双方的身份并没有做出严格的验证,Socket通信 ip地址是客户端手动填入，容易出现伪造

使用简单:<br>
1. 使用CS架构;

2.实现 面向对象 的调用方式，即在使用Binder时就和调用一个本地对象实例一样

Binder请求的线程管理

server进程会创建很多进程来处理binder请求,管理binder模型的线程采用binder驱动的线程池,并由binder驱动自身进行管理,而不是由server进程管理;一个进程的binder线程数默认最大为16,超过的请求会被阻塞等待空闲的binder线程;所以在进程间通信处理并发问题时,如使用contentprovider时,它的curd方法只能同时有16个线程同时工作;

> aidl

 AIDL（Android Interface Definition Language,AIDL）是Android的一种接口描述语言;编译器可以通过aidl文件生成一段代码，通过预先定义的接口达到两个进程内部通信进程的目的(简单来说,就是在app里绑定一个其他app的service,交互);

 创建aidl步骤: <br>

1.创建一个aidl文件

2.在aidl文件中定义一个我们要提供的接口

3.新建一个service，在service里面建一个内部类，继承刚才创建的AIDL的stub类，并实现接口的方法，在onbind方法中返回内部类的实例。


使用aidl步骤:<br>

1.将aidl文件拷贝到src目录下，同时注意添加到的包名要求跟原来工程的包名严格一致

2.通过隐式意图绑定service

3.在onServiceConnected方法中通过Interface.stub.asinterface(service)获取Interface对象

4.通过interface对象调用方法

aidl通过binder实心的,生成相关的binder类,stub就是binder类;

### Android版本特性

>4.4(api 19):  

- `沉浸式体验`;更新nfc,打印框架;转场动画; 
- `支持两种新的蓝牙配置文件`(Bluetouth HID over GATT较短的延迟时间与低功耗(鼠标,手柄,键盘)连接,Bluetouch MAP与附近的设备(汽车,移动设备)交换信息),这两者作为对Bluetooth AVRCP 1.3的扩展; `HOGP`,`MAP`,`AVRCP`; 
- `RenderScript,可以原生代码使用` 
- 红外发射器; wifi TDLS;

>**改变最大** 5.0(api 21): `md设计,过渡动画`,`JobScheduler`

- `material design`,三维视图z视图;
- activity`共享元素`,无缝状态转换;波纹动画;
- `引入ART(aot)作为运行时` 取代dalvik(jit) 编译
- 通知栏改善可悬挂,锁定屏幕显示通知,访客模式,屏幕共享;
- 新增`蓝牙低功耗BLE`执行并发操作api,实现扫描(中心模式)和广播(外设模式)
- 全新camera api,可采集YUV和Bayer Raw原始格式;


>6.0(api 23): `运行时权限`

- `运行时权限控制`;
- 低电耗模式,待机模式; 低电耗: 拔下电源插头,屏幕关闭,休眠状态,定期短时间回复正常工作; 待机: 用户未使用,停用网络访问暂停同步;
- 选中文本时的悬浮框;
- 取消apache http,改用`httpUrlConnection`;
- `硬件标志符访问权` 使用Wlan api 和bluetooth api 的应用,移出对设备本地硬件标志符的编程访问权; 必须拥有Access_fine_location和Access_coarse_location权限;
- 通知栏改用`Notification.Builder`构建通知;
...

>7.0(api 24-25 N): `私有文件访问权限`,手机平板`多窗口支持`

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

>8.0(api 26): `通知渠道 NotificationChannel `,`自适应启动图标`,`刘海屏适配`

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


>9.0(api 28): `显示屏缺口显示`,`通知渠道,通知notificationmanager`,`imageDecoder解码,AnimationImageDrawable 绘制webp`

- wifi 室内定位;
- `显示屏缺口支持`
- 提升短信体验;
- 渠道设置,广播和请勿打扰;
- 多摄像头支持;
-  ImageDecoder 取代BItmapFactory和Options api;
- 动画 AnimatedImageDrawable ;

### 使用过 Gradle 的哪些功能。

- 统一库的版本号,配置签名信息;
- 自定义设置打包apk的输出名称;
- 设置apk的渠道包;

### 硬件开发及jni

- jni/ndk,如何调用c

Java Native Interface,用于调用java调用C/C++代码;

Native Development Kit,工具集合,可以在android更加方便通过JNI调用本地代码C/C++;

>先System.loadLibrary("show-ndk"); 后使用native方法调用;

### 常用用法及设计与工具

- 非UI线程更新UI

非主线程更新UI,会抛出CalledWrongThreadException, checkThread方法检测是否是当前线程,ViewRootImpl指定当前线程; 使用子线程更新UI,只需指定ViewRoot的当前线程为子线程就可;

- 在子线程中使用windowmanager 添加view,可以使用子线程更新此View;
- oncreate中也可以使用子线程更新view,原因是handleResumeActivity中才会使用windowmanager.addview,触发ViewrootImpl的操作;

-----------

# 架构能力

### MVC、MVP、MVVM

### clean architecture ,android architecture components

### app框架 分包,分层



-----------

#android framework

### Linux的fork

通过系统的调用创建一个与原来进程完全相同的进程;n次的fork循环,最终创建了2n-1个子线程;

 fork函数执行成功后,出现两个进程,fork返回的值判断是否是父子进程;fpid(父进程Id)!=0 为父进程(相当于链表);

###  AMS 、PMS , wms

### App 启动流程

### Binder 机制（IPC、AIDL 的使用）

###为什么使用 Parcelable，好处是什么？

### Android 图像显示相关流程，Vsync 信号等

### Activity 启动流程

android基于linux,基于linux的init进程创建出来;

- Init进程->启动 Zygote进程->fork 出SystemServer进程->开启 应用进程;
- SystemServer进程中启动系统的各种服务(AMS,PMS,WMS...);
- 所有进程的父进程为Zygote进程,fork出SystemServer进程;
- systemServer 启动主looper,创建Context上下文,创建SystemServiceManger,依次调用startBootStrapService->startCoreService->startOtherService;

- Zygote 进程启动流程:

	- ZygoteInit main方法;
	- Zygote 和SystemServer进程间的通行使用的是socket;

- SystemServer 进程启动流程;
	-SystemServer main方法中调用run初始化:


- 创建了SystemServiceManger ;
	- createSystemContext 创建上下文Context;
	- SystemServiceManger start Services: `startBootStrapServices()`,`startCoreServices()`,`startOtherServices()`
		- 其中,startBootStrapService,先启动第一个Installer服务, 然后启动Ams(管理android的四大组件), powerManagerService(电源管理服务),lightsService(闪光灯服务),displayManagerService(显示相关服务),Pms(包管理服务),UsermanagerService(用户管理服务),SensorService(传感器服务)...;
		- 接下类调用startCoreServices ,启动核心服务,启动BatteryService(电池服务),UsageStatsService(用户状态),WebViewUpdateService()...;
		- 最后调用startOtherService, 启动一些其他服务,VibratorService,NetworkManagermentService,NetworkStatsService,WindowmanagerService,还有5.0的JobService;

![](https://upload-images.jianshu.io/upload_images/2156477-6f6ad71a21ff1d02.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/950)

### Activity的启动流程 Launcher-> ActivityManager, 及销毁;

- 调用context.startActivity()方法; 启动的app运行在另一个进程中,`ActivityManager`需要和`AMS`进行IPC通行;
- ActivityMangerNative.getDefault()[即ActivityManger.getService(),也即AMSProxy,真正的实现类为AMS].startActivity();
- Ams接受到四大组件的Intent请求后:
	- ResolveInfo查找取出Intent的信息;
	- 判断是否需要新建进程startProcessRecord;
	- startProcessRecord调用Process.start方法,指定ActivityThread作为进程的入口,调用main方法;(具体是: 通过与zygote进程socket通信,fork zygote进程,就是`应用进程`; 接着初始化相应的进程,调用进程的main方法,这个进程中运行的线程即是activitythread主线程)


![](https://upload-images.jianshu.io/upload_images/2156477-37faec1410fbe670?imageMogr2/auto-orient/strip%7CimageView2/2/w/856)
![](https://upload-images.jianshu.io/upload_images/2156477-b29c71ab414e6d02.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/678)

> 总结:

- android基于linux,linux的init进程启动zygote进程,zygote进程fork systemserver进程,systemserver  主要是创建systemservermanager,创建context上下文,进行systemserver进程中的looper循环,而ssm创建 startbootstrapservice,startcoreservice,startotherservice,比如ams 处理四大组件的服务就是在第一个方法中初始化的;  
- 等systemserver启动完成后,启动activity等四大组件时,context.startActivity()打开新的activity,最终是通过ActivityManager.getService()去startActivity(),而与之binder通信的是activitymanangerProxy,代理的是AMS;
- AMS 实际启动组件时,接受Intent的信息,判断是否有相应的process,没有则使用systemserver当前进程通过socket与zygote通信去fork 一个新的进程,即应用进程; 这个进程会相应的初始化,与serversystem 可以ipc通信,调用此进程的main方法,这个main方法即ActivityThread的main方法;

> activity view 的绘制流程(activity->phonewindow->decorview->titleView+contentView)

ActivityThread ,main主要是 attach()建立与systemserver进程的ipc通信,启动当前应用进程的looper循环;

- activitythread会继续和ams通信,发送message至主线程的handler H中,最终通过AMS(ActivityStackSupervisor类)发送`LAUNCH_ACTIVITY`消息调用ActivityThread `handleLaunchActivity()`方法启动activity;
- 调用performlaunchActivity,最终调用Activity的`onCreate`方法;
- `setContentView` 中建立DecorView,添加至PhoneWindow中;加载自定义的布局;
- handleLaunchActivity中调用handleResumeActivity方法,调用windowmanager addView ,wm中建立`ViewRootImpl`,此类负责具体的绘制流程,调用requestLayout->scheduleTraversals->发送TraversalRunnable->最终调用`performTraversals`方法绘制;
- 最终调用activity 的`makeVisble`显示在页面上;

>总结:

athread 建立与systemserver的 AMS的ipc通信,启动looper循环; Ams调用方法发送`Launch_activity`消息至athread的handler消息队列中,启动activity的创建过程handlerlaunchactivity方法,运行oncreate ,onresume等方法; oncreate中调用setContentView方法设置自定义布局且建立decorview添加至phonewindow中;onresume中 调用windowmanager 的addView方法 ,其中生成`ViewRootImpl`,调用`performTraversal`方法负责具体的绘制功能measure,layout,draw;最终调用activity的makeVisible显示activity;

### apk安装流程

### activity与window/view的关系

### contentprovide 全方位解析

### broadcastReceiver 全方位解析 

### window 和windowmanager (pop与dialog区别)

### recyclerview 四级缓存+局部刷新

### servicemanager



-----------

# 三方源码

### 支付的流程,支付功能,支付宝与微信的支付功能接入，常见问题

###  Glide (生命周期控制,二级缓存,bitmappool复用,同一个图片跟size有关么)

### EventBus

### 依赖注入DI ,butterknife,dagger2;

### LeakCanary,blockcanary

### ARouter

### 插件化（不同插件化机制原理与流派，优缺点。局限性）

### 热修复

### Rxjava （RxJava 的线程切换原理）

### Retrofit (Retrofit 在 OkHttp 上做了哪些封装？动态代理和静态代理的区别，是怎么实现的; 动态代理,运行时注解)

### OkHttp (拦截器(责任链模式),超时重传&重定向,http缓存,socket连接池复用) 写过拦截器么

### acache ,room


-----------

# 算法与数据结构

###  二叉排序树(二叉搜索树),查找下一个元素;

 性质: 

- 某节点的左子树非空,则左子树上所有元素的值都小于该元素的值;
- 某节点的右子树非空,则右子树上所有元素的值都大于该元素的值; 


 前序和中序遍历二叉树（我写了递归和非递归）,了解二叉树的遍历

- 前序遍历: 根节点->左节点->右节点;
- 中序遍历: 左节点->根节点->右节点;
- 后序遍历: 左节点->右节点->根节点; 

### 无序数组建立二叉搜索树

 有序数组建立二叉树: 分为三部分:左半部分,中间值,右半部分; (效率高,保证数组的每个存储单元都被利用;)<br>
 数组中值构造节点,左半部分构造节点的左子树,右半部分构造右子树,递归;

###  输出二叉树每层的最大值

###  完全二叉树和满二叉树的区别

完全二叉树: 除了最高层之外,其余层节点个数都达到最大值,并且最高层节点优先集中在最左边;<br>
满二叉树: 除了最高层有叶子节点,其余层无叶子,并且非叶子节点都有两个子节点;

###  单链表：反转、插入、删除

### 双链表：插入、删除

### 手写常见排序、归并排序、堆排序

### 快排；

思想: 通过一次排序将数据分割为独立的两部分,其中一部分的所有数据都比另一部分的所有数据都要小(因为基准数),然后根据此方法对两部分的数据分别进行快排,递归;

 - 选择一个基准数,如第一个数; 
 - 开始从后往前遍历,找到比基准数小的交换位置;然后从前往后遍历,找到比基准数大的交换位置; 相互数据的index指向同一个数据,第一遍比较结束;
 - 使用基准数分解数组,在选择基准数快排,直到不能再分为止;

### 手写二分查找，并分析时间复杂度；

思想: left游标,right游标,middle游标,如果middle游标的值大于target,最终的值在前半部分,right = middle-1,middle = (left+right)/2;重新遍历计算;

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

###  项目开发中遇到的最大的一个难题和挑战，你是如何解决的。（95% 会问到）

### 说说你开发最大的优势点（95% 会问到）

###你为什么会离开上家公司

### 你对未来的职业规划？

### 你的缺点是什么？

### 你能给公司带来什么效益？

-----------

# 计算机基础知识

### 进程和线程

###   操作系统里面的一个「虚拟内存」是指的什么？

 虚拟内存指的是一个对内存和外存进行调度，只是从逻辑上扩充了内存，但实际上不存在的内存存
储器。基于局部性原理，在程序装入的时候，可以将程序的一部分装入内存，而在其余部分留在
外存，就可启动程序执行；在程序执行时，当所访问的信息不在内存的时候，由操作系统所需要的
部分调入内存，然后继续执行程序；操作系统再将内存中暂时不使用的内容换出到外存上，从而腾
出空间存放将要调入内存的信息。

-----------

# 杂项

### gitflow流

>团队代码管理方式:

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

> 步骤: 
 
 `git rebase`: 和`git merge`一样是将一个分支的更改并入到另一个分支;  git checkout feature; git merge master;(git merge master feature) 切换feature分支,合并master的代码; merge是一个安全的操作;现有的分支不会更改;
 
 `git rebase`是替代的选择,git checkout feature; git rebase master; 将整个feature分支移到到master的后面,有效的将master分支上新的提交并入过来,但是,rebase为原分支上每一个提交创建一个新的提交,重写项目历史,不会带来合并提交;项目历史简洁,但是安全性和可追踪性,违反rebase的法则,会丢失且看不到信息;
 
 `rebase的法则为: 不要在公共的分支上使用`,如果在master上rebase到feature上,会将master所有提交都移到feature后面,但只会发生在我的仓库上,其他的开发者还在原来的master上,也就是出现了一个临时的master,rebase引起了新的提交,git会认为你的master分支和其他人的master分支已经分叉了(也就是你所处的master成为一个新的master);而同步两个master的唯一方法就是merge到一起;
 
> 一个一般的gitflow 工作流:
 
 - 首先将远程代码拉到本地,新建一个develop的分支; <br/>
 git clone xxx; git checkout -b develop origin/develop;
 - 新建feature分支 ;  <br/>
 git checkout -b feature;
 - 多人在feature上开发,如果中途需要将develop的变更合入feature,所有人需要将本地的代码变更提交到远程;   <br/>
 git feath origin; git rebase origin/feature() (将当前feature的合并到feature的后面,产生的是一个新的feature); git push origin feature; <br/>
 然后由feature负责人rebase develop分支,删除原来的feature分支,重新创建feature分支; <br/>
 git fetch origin; git rebase origin/feature; git rebase develop; git push origin : feature (git 使用冒号删除远程的feature的分支); git push origin feature;保证了feature的线性变更;
 - feature开发完成后,所有人将本地代码提交到远程;  <br/>
 git fetch origin; git rebase origin/feature; git push origin feature; <br/>
 然后由feature 负责人 rebase develop分支,将feature分支合入develop,删除feature;<br/>
 git fetch origin; git rebase origin/feature; git rebase develop;git checkout develop; git merge feature;git push origin :feature;可以保持develop保持线性变更,各feature的变更完整可追溯;
 - 合入feature后拉出对应的release/feature分支,后续bug修复在release/feature上;<br/>
 git checkout develop; git checkout -b release/feature; 此分支的同步合并与feature分支相同;
 - release/feature 分支bug修复完成后.拉取对应的tag推送远程进行发布;<br/>
 git tag -a v1.0 -m "feature 发布"; git push origin v1.0; 之后将release/feture合入develop分支,然后删除;<br/>
 git rebase develop; git checkout develop; git merge release/feature; git push origin : release/feature;
 - 发布完成后将release 合入master分支为最新稳定版本(merge request);

 ![](https://img-blog.csdn.net/20170215234958484?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvd3dqXzc0OA==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)
 
 
 - **forking工作流:** 
 公开的中央仓库,其他成员`fork` 为私有仓库,然后`pull requst`合并到中央仓库;
 
 ![](https://img-blog.csdn.net/20170215235113109?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvd3dqXzc0OA==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

-----------
