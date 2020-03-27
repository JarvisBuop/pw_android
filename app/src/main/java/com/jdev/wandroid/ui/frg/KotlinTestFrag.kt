package com.jdev.wandroid.ui.frg

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import com.blankj.utilcode.util.LogUtils
import com.jdev.kit.baseui.BaseViewStubFragment
import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis

/**
 * info: create by jd in 2020/3/26
 * @see:
 * @description:
 *
 */
class KotlinTestFrag : BaseViewStubFragment() {

    override fun getViewStubDefault(): View? {
        var linearLayout = LinearLayout(mContext)
        linearLayout.orientation = LinearLayout.VERTICAL

        Button(mContext).apply {
            text = "kotlin coroutines"
            setOnClickListener {
                //                this@KotlinTestFrag::clickCoroutines
                clickCoroutines()
            }
            linearLayout.addView(this)
        }

        Button(mContext).apply {
            text = "kotlin join 先运行"
            setOnClickListener {
                test1()
            }
            linearLayout.addView(this)
        }
        Button(mContext).apply {
            text = "kotlin 挂起和阻塞"
            setOnClickListener {
                test3()
                LogUtils.e(TAG, "end click in main")
            }
            linearLayout.addView(this)
        }
        Button(mContext).apply {
            text = "kotlin suspend"
            setOnClickListener {
                test_suspend()
            }
            linearLayout.addView(this)
        }
        Button(mContext).apply {
            text = "kotlin canceljoin isactive"
            setOnClickListener {
                test_canceljoin()
            }
            linearLayout.addView(this)
        }

        Button(mContext).apply {
            text = "kotlin finally"
            setOnClickListener {
                test_finally()
            }
            linearLayout.addView(this)
        }

        Button(mContext).apply {
            text = "kotlin concurrence"
            setOnClickListener {
                test_concurrence()
            }
            linearLayout.addView(this)
        }


        return linearLayout
    }

    override fun customOperate(savedInstanceState: Bundle?) {

    }

    //协程阻塞,kotlin coroutines
    fun clickCoroutines() {
        //非阻塞
        GlobalScope.launch {
            delay(1000)
            LogUtils.e(TAG, "2222222 in child")
        }
        LogUtils.e(TAG, "1111111111111 in main")
        //阻塞
//        Thread.sleep(2000)
//        coroutineScope { //仅仅使用在协程里;
        runBlocking {
            delay(10000)
        }
    }

    //等待一个作业 join;kotlin join 先运行
    fun test1() = runBlocking<Unit> {
        GlobalScope.launch {
            delay(1000)
            LogUtils.e(TAG, "222222222222 in child")
        }
        //in main;
        LogUtils.e(TAG, "111111111111 in main")
        delay(2000) // 延迟 2 秒来保证 JVM 存活

        val job = GlobalScope.launch {
            delay(1000)
            LogUtils.e(TAG, "4444444 in child")
        }
        LogUtils.e(TAG, "3333333 in main")
        job.join()
        LogUtils.e(TAG, "55555555 in main")
    }

    //kotlin 挂起和阻塞
    fun test3() = runBlocking {
        //启动一个新协程,不阻塞;
        launch {
            delay(200L)
            //in child //todo 这里也是main!!!
            LogUtils.e(TAG, "Task from runBlocking 2")
        }

        //挂起 wait;
        coroutineScope {
            // 创建一个协程作用域
            launch {
                delay(500L)
                //in coroutineScope2 //todo 这里还是main!!!
                LogUtils.e(TAG, "Task from nested launch 3")
            }

            delay(100L)
            //in coroutinescope todo 这里是main!!!
            LogUtils.e(TAG, "Task from coroutine scope 1") // 这一行会在内嵌 launch 之前输出
        }

        //in main
        LogUtils.e(TAG, "Coroutine scope is over 4") // 这一行在内嵌 launch 执行完毕后才输出
    }

    //挂起函数kotlin suspend
    fun test_suspend() {
        runBlocking {
            launch {
                innerSuspend()
            }
            LogUtils.e(TAG, "real start in main 1")
        }
        LogUtils.e(TAG, "start or end in main 3")
    }

    suspend fun innerSuspend() {
        delay(1000)
        LogUtils.e(TAG, "runblocking in main 2")
    }

    fun test_canceljoin() = runBlocking {
        val startTime = System.currentTimeMillis()
        val job = launch(Dispatchers.Default) {
            //计算任务需要协作,才能取消??
            var nextPrintTime = startTime
            var i = 0
            while (/*i < 5 && */isActive) { // 一个执行计算的循环，只是为了占用 CPU
                // 每秒打印消息两次
                if (System.currentTimeMillis() >= nextPrintTime) {
                    LogUtils.e(TAG, "job: I'm sleeping ${i++} ...")
                    nextPrintTime += 500L
                }
            }
        }
        delay(1300L) // 延迟一段时间
        LogUtils.e(TAG, "main: I'm tired of waiting! in main")
//        job.cancel() // 取消该作业
//        job.join() // 等待作业执行结束
        job.cancelAndJoin()
        LogUtils.e(TAG, "main: Now I can quit.")
    }

    //kotlin finally
    fun test_finally() = runBlocking {
        val job = launch {
            try {
                repeat(1000) { i ->
                    LogUtils.e(TAG, "job: I'm sleeping $i ...")
                    delay(500L)
                }
            } finally {
                withContext(NonCancellable) {
                    LogUtils.e(TAG, "job: I'm running finally")
                    delay(1000L)
                    LogUtils.e(TAG, "job: And I've just delayed for 1 sec because I'm non-cancellable")
                }
                LogUtils.e(TAG, "after finally nonCancellable")
            }
        }
        delay(1300L) // 延迟一段时间
        LogUtils.e(TAG, "main: I'm tired of waiting!")
        job.cancelAndJoin() // 取消该作业并等待它结束
        LogUtils.e(TAG, "main: Now I can quit.")
    }

    //使用future并发
    fun test_concurrence() = runBlocking {
        val time = measureTimeMillis {
            //            val one = async { doSomethingUsefulOne() }
//            val two = async { doSomethingUsefulTwo() }

            var one: Deferred<*>? = null
            var two: Deferred<*>? = null
            //设置协程惰性启动; await获取的时候才会启动;
            GlobalScope.launch {
                one = async(start = CoroutineStart.LAZY) { doSomethingUsefulOne() }
                two = async(start = CoroutineStart.LAZY) { doSomethingUsefulTwo() }
                //如果不加start , 会导致顺序行为;
                one?.start()
                two?.start()
                LogUtils.e(TAG, "The answer is ${one?.await() ?: "" + two?.await()}")
            }
        }
        LogUtils.e(TAG, "Completed in $time ms")


    }

    suspend fun doSomethingUsefulOne(): Int {
        delay(1000L) // 假设我们在这里做了些有用的事
        LogUtils.e(TAG, "one compute in define thread")
        return 13
    }

    suspend fun doSomethingUsefulTwo(): Int {
        delay(1000L) // 假设我们在这里也做了些有用的事
        LogUtils.e(TAG, "two compute in host")
        return 29
    }

    //async 结构化并发
    suspend fun concurrentSum(): Int = coroutineScope {
        val one = async { doSomethingUsefulOne() }
        val two = async { doSomethingUsefulTwo() }
        one.await() + two.await()
    }

}