#kotlin extra 

## 协程

kotlin 在标准库中提供最基本底层api以便各种其他库能够利用协程的语言;

本质上,协程是一个轻量级的线程;

####`runBlocking`和`coroutineScope`区别: 

- 同: 都会等待其协程体以及所有子协程结束;(可理解为都是阻塞所在(当前)线程直到函数体内运行完毕,但是函数体内{lambda表达式}的回调还是处于所在(当前)线程;)
- 异: 
	- runBlocking方法会`阻塞`当前线程来等待(感觉更像sleep;)
	- coroutineScope只是`挂起`,会释放底层线程用于其他用途;(类似于wait,让别的线程可以使用资源;只能使用在协程中或者suspend方法)
	-  runBlocking 是常规函数,coroutineScope是挂起函数;

coroutineScope 作用域构建，创建一个协程作用域并且在所有已启动子协程执行完毕之前不会结束；

####`GlobalScope` 全局协程像守护线程;

#### 协程的取消
- 协程的取消是`协作`的,一段协程代码必须协作才能被取消; 
- 所有 `kotlinx.coroutines` 中的挂起函数都是 可被取消的 。检查协程的取消,取消时抛出`CancellationException`; (需要使用try catch finally 处理可被取消的挂起函数，在finally中释放资源)

- 如果协程正在执行计算任务,并且没有检查取消的话,那么它是不能被取消的;(使用CoroutineScope 的扩展属性 `isActive`)
- 运行不被取消的代码块, 如果需要挂起一个被取消的协程(finally块中),使用`withContext(NonCancellable){...}`withContext函数和NonCancellable上下文;

#### 超时
-  `withTimeout` 超时抛出`TimeoutCancellationException extend CancellationException` 取消是正常原因，正常没有打印CancellationException被认为协程执行结束的正常原因; 
- 或者`withTimeoutOrNull`有返回值，返回一个null代替抛出异常进行超市操作;
- withTimeout 为异步执行函数，本身是安全的， 可保存一个变量对资源的引用，而不是在withTimeout块中返回，以保证并发正确；

#### 挂起函数的组合

- `async`类似于`launch`,启动一个单独的协程,这是一个轻量级的线程并与其他所有的协程一起并发的工作; 
	- 不同之处就是launch返回一个job并且不附带任何结果值; async返回一个`Deferred`一个轻量级的非阻塞future,代表一个将会在稍后提供结果的promise;
		- 可以使用`.await`在一个延期的值上得到它的最终结果; Deferred也是一个job,可以取消;
	- async 可设置惰性启动方式,只有结果通过await获取的时候协程或者job的start函数调用的时候才会启动 `val one = async(start=CoroutineStart.LAZY){doSomeThing()}  one.start()`不调用start,直接调用await会导致顺序行为,直到await启动该协程执行并等待至它结束 注意它运行所在的线程处于 定义它所处的线程;
	- async的结构化并发 如果一个suspend函数内部发生错误,并且抛出一个异常,所有作用域中的启动的协程都会被取消;

### 协程上下文 调度器

- 协程总是运行在一些以`CoroutineContext`类型为代表的上下文中，定义在标准库中；
- 协程上下文包含 `协程调度器 CoroutineDispatcher ` 确定相关的协程在那个线程或哪些线程上执行；所有的协程构建器 接受一个可选的CoroutineContext，用来显示的为一个新协程或其他上下文元素指定一个调度器；
- 受限 && 非受限调度器 
	- Dispatchers.Unconfined 调用它的线程启动一个协程，仅仅只运行到第一个挂起点，挂起后，它恢复线程中的协程，完全由被调用的挂起函数决定；
	- Dispatchers.Unconfined 非受限调度器 适合执行不消耗cpu时间的任务，以及不更新局限于特定线程的任何共享数据（ui）的协程；
	- 非受限的调度器是一种高级机制，可以在某些极端情况下提供帮助而不需要调度协程以便稍后执行或产生不希望的副作用， 因为某些操作必须立即在协程中执行。 非受限调度器不应该在通常的代码中使用。

### 父子协程的关系

- 除了GlobalScope启动协程外，一个父协程（coroutineScope中启动）被取消，所有它的子协程也会被递归的取消；
- 一个父协程总是等待所有的子协程执行结束；

### 协程作用域

- CoroutineScope 管理协程的生命周期，使它与activity的生命周期想关联； 可以通过`CoroutineScope`创建通用作用域或`MainScope`工厂函数以Dispatchers.Main为调度器；

### 线程局部函数

- 协程中的ThreadLocal， `threadLocal.asContextElement` 创建了一个ThreadLocal给协程使用; 且 工作在线程池中的不同线程中，仍然具有线程局部变量的值；
- ThreadLocal 限制，当一个线程局部变量变化时，这个新值不会传播给协程调用者，且下次挂起时更新的值将丢失； 使用`withContext`在协程中更新线程局部变量；

### 异步流

- 序列： Sequence，消耗cpu资源计算；
- 挂起函数： suspend
- 异步计算流：Flow 是 冷的 ，flow构建器中的代码直到流被收集的时候才运行；
	- 名为flow的Flow类型构建器函数
	- flow{  }构建块中的代码可挂起； 
	- emit发射值，collect收集值；
	- 流取消与协程一样协作取消；
	- 流构建器
		- flowOf 构建器定义了一个发射固定值的流；
		- 使用`.asFlow`扩展函数，可以将各种集合与序列转换为流；
	- 过渡流操作符，map，filter等，流与序列的主要区别在于这些操作符中的代码可以调用挂起函数；
	- 转换操作符，transform，可发射任意值任意次；
	- 限长操作符，take，将限长的情况下将执行取消协程;
	- 末端流操作符，collect 是最基础的末端操作符，用于启动流收集的挂起函数
		- 转换为各种集合，toList，toSet；
		- 获取第一个 first 值 与确保发射单个single值的操作符；
		- 使用reduce，fold将流规约到单个值；
	- 流上下文，流的`收集`总是在调用协程的上下文中发生，该属性称为`上下文保存`；
	- 缓冲操作符，buffer，并发运行flow流中发射元素的代码，改串行为并行；注意，当必须更改CoroutineDispatcher时，flowOn操作符使用了相同的缓冲机制，区别是buffer不改变执行上下文；
	- 合并操作符，conflate，当发送和收集速度不一致时，将合并发射项，只发射最新值，旧值丢失；
	- 收集最新值操作符，xxx -> xxxLatest，当发射器和收集器都很慢时，合并时加快速度的一种方式； 
		- 通过删除发射值来实现；
		- 取消缓慢的收集器，并在每次发射新值的时候重新启动它；
		- xxxLatest 可在新值产生的时候取消执行块中的代码，最后只发送最后发送的值；
	- 组合流操作符系
		- zip，组合两个流中的相关值；
		- combine，也是组合两个流的操作符，在两个发送流时间差不一致时，zip会等待，然后一起发送配对的组合流； combine会在每个流发送新值时都会直接配对，也就是一个流的上次发送数据可能会和另一个流的最新发送数据进行组合被接受；
	- 展平流操作符系 ，将包含流 展平为单个流以进行下一步处理；
		- flatMapConcat  连接模式由flatMapConcat & flattenConcat实现；等待内部流完成之前开始收集下一个值；即可将每一个发送的数据再一次包装flow{ }，通过此操作符展平，发送成多个数据， 输入1->输出n，且会等待一个处理完了在去处理下一个数据；
		- flatMapMerge 由flatMapMerge&flattenMerge实现；并发收集所有传入的流，并将它们的值合并到一个单独的流，以便尽快的发射值；区别就是此操作符不会等待处理完后在处理下一发送数据，因为并发收集 会直接取下一条数据发送；
		- flatMapLatest 发出新流后立即取消先前流的收集；

### withContext 发出错误

- withContext用于在kotlin中改变代码的上下文
- 一般长时间运行的消耗cpu的代码需要 在Dispatchers.Default上下文中执行，更新ui的代码需要在DisPatchers.Main中执行；
- flow{ }构建器中的代码必须遵循上下文保存属性，并且不允许从其他上下文中发射；
- 例外的是`flowOn`函数，该函数用于更改流`发射`的上下文；flowOn 会改变流的默认顺序性，收集在一个协程中，发射的运行在另一个协程中；flowOn其实是创建了另一个协程；

```
fun simple(): Flow<Int> = flow {
    for (i in 1..3) {
        Thread.sleep(100) // 假装我们以消耗 CPU 的方式进行计算
        log("Emitting $i")
        emit(i) // 发射下一个值
    }
}.flowOn(Dispatchers.Default) // 在流构建器中改变消耗 CPU 代码上下文的正确方式

fun main() = runBlocking<Unit> {
    simple().collect { value ->
        log("Collected $value") 
    } 
} 

```
### 流异常

- 收集器try catch 
- 流必须对异常透明，flow{ } 构建器内部trycatch块发射值违反透明性；
	- throw重新跑出
	- 透明捕捉 ，catch 过渡操作符，catch上游的异常， 代码块emit将异常转换为值发射出去；  
	- 声明式捕捉，透明捕捉在末端收集中的异常会出现逃逸，当处理所有的异常时，将collect操作符代码移动到onEach中，将其放到catch操作符之前，使用无参的collect触发；
- 命令式 `finally`块  收集完成时触发一个动作；
- 声明式处理， 使用`onCompletion`过渡操作符，在流完全收集时调用，优点在于提供可空参数Throwable用于异常发生下的收集；与catch操作符的区别是不处理异常,需要位于catch操作符的上流；

### 启动流

- onEach 获取容器中的事件流，使用末端操作符collect来启动和收集流,且collect后面的代码会等到流被收集完成后才能执行；
- `launchIn` 末端操作符可以替换collect 可在`单独的协程`中启动流的收集；
	- 需要指定使用哪个协程启动流的收集 CoroutneScope；
	- launchIn 返回Job ，可取消可join；
- 取消检测，`ensureActive` ，具体使用可以`.onEach{ currentCoroutineContext().ensureActive() }`== `cacenllable() `操作符；

### 通道 channel

协程生成一系列元素的模式很常见,这是`生产者-消费者模式`的一部分;

- 类似于BlockingQueue，不同是代替了阻塞的put和阻塞的take，提供了挂起的send和receive； 
- 通道提供了一种在流中传输值的方法；
- 与队列不同一个通道可以通过被关闭表明没有更多的元素进入通道,close关闭指令,可以使用for循环从通道中接受元素(直到通道被关闭);
- 可以将生产者抽象成一个函数,使通道成为它的参数;`produce` 协程构建器,作为生产者端输出; 使用`consumeEach`在消费端替代for循环;
- 管道:一个协程在流中开始生产可能无穷多个元素的模式, 使用`cancelChildren`可以取消所有的子协程;  (Channel里应该包含一个容器)
	- send-receive 
	- send , 使用for-in循环 扇出也可;
- 带缓冲的通道,默认是没有缓冲区的,无缓冲的通道在发送者和接受者相遇时传输元素(对接),先被调用的先被挂起;  Channel工厂函数和produce构造器使用capacity参数可指定缓冲区大小; 缓冲区被占满后将会引起阻塞;
- 通道是公平的,每个调用的协程的调用FIFO;
- 计时器通道,经过特定的延迟都会从该通道进行消费产生Unit; ticker工厂函数创建channel;

```
fun CoroutineScope.produceSquares(): ReceiveChannel<Int> = produce {
    for (x in 1..5) send(x * x)
}

fun main() = runBlocking {
    val squares = produceSquares()
    squares.consumeEach { println(it) }
    println("Done!")
}

```

















