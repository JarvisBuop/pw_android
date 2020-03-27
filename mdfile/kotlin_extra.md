##kotlin extra 

### 协程

kotlin 在标准库中提供最底层api以便各种其他库能够利用协程的语言;

本质上,协程是一个轻量级的线程;

####`runBlocking`和`coroutineScope`区别: 

- 同: 都会等待其协程体以及所有子协程结束;(可理解为都是阻塞所在(当前)线程直到函数体内运行完毕,但是函数体内{lambda表达式}的回调还是处于所在(当前)线程;)
- 异: 
	- runBlocking方法会`阻塞`当前线程来等待(感觉更像sleep;)
	- coroutineScope只是`挂起`,会释放底层线程用于其他用途;(类似于wait,让别的线程可以使用资源;只能使用在协程中或者suspend方法)
	-  runBlocking 是常规函数,coroutineScope是挂起函数;


####`GlobalScope` 更像守护线程;

#### 协程的取消
- 协程的取消是`协作`的,一段协程代码必须协作才能被取消; 
- 所有 `kotlinx.coroutines` 中的挂起函数都是 可被取消的 。检查协程的取消,取消时抛出`CancellationException`; (需要使用try catch finally 处理可被取消的挂起函数)

- 如果协程正在执行计算任务,并且没有检查取消的话,那么它是不能被取消的;(使用CoroutineScope 的扩展属性 `isActive`)
- 运行不被取消的代码块, 如果需要挂起一个被取消的协程(finally块中),使用`withContext(NonCancellable){...}`withContext函数和NonCancellable上下文;

####超时
-  `withTimeout` 超时抛出`TimeoutCancellationException extend CancellationException` 正常没有打印CancellationException被认为协程执行结束的正常原因; 
- 或者`withTimeoutOrNull`返回一个null代替抛出异常;

#### 挂起函数的组合

- `async`类似于`launch`,启动一个单独的协程,这是一个轻量级的线程并与其他所有的协程一起并发的工作; 
	- 不同之处就是launch返回一个job并且不附带任何结果值; async返回一个`Deferred`一个轻量级的非阻塞future,代表一个将会在稍后提供结果的promise;
		- 可以使用`.await`在一个延期的值上得到它的最终结果; Deferred也是一个job;
		- 可设置惰性启动方式`val one = async(start=CoroutineStart.LAZY){doSomeThing()}  one.start()`不调用start,直接调用await会导致顺序行为,直到await启动该协程执行并等待至它结束 注意它运行所在的线程处于 定义它所处的线程;
	- async的结构化并发 如果一个suspend函数内部发生错误,并且抛出一个异常,所有作用域中的启动的协程都会被取消;
