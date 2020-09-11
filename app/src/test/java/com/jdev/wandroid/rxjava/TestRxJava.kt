package com.jdev.wandroid.rxjava

import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.internal.schedulers.ExecutorScheduler.ExecutorWorker
import io.reactivex.schedulers.Schedulers
import org.junit.BeforeClass
import org.junit.Test
import java.util.concurrent.Callable
import java.util.concurrent.Executor
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit


/**
 * info: create by jd in 2020/9/10
 * @see:
 * @description: 单元测试- 测试rxjava的使用
 *
 */
class TestRxJava {

    companion object {
        @BeforeClass
        @JvmStatic
        fun setUpRxSchedulers() {
            val immediate: Scheduler = object : Scheduler() {
                override fun scheduleDirect(run: Runnable, delay: Long, unit: TimeUnit): Disposable {
                    // this prevents StackOverflowErrors when scheduling with a delay
                    return super.scheduleDirect(run!!, 0, unit)
                }

                override fun createWorker(): Worker {
                    return ExecutorWorker(Executor { obj: Runnable ->
                        System.out.println("currentThread is ${Thread.currentThread().name}, waiting switch to targetThread")
                        obj.run()
                    })
                }
            }

//            RxJavaPlugins.setInitIoSchedulerHandler { scheduler: Callable<Scheduler?>? -> immediate }
//            RxJavaPlugins.setInitComputationSchedulerHandler { scheduler: Callable<Scheduler?>? -> immediate }
//            RxJavaPlugins.setInitNewThreadSchedulerHandler { scheduler: Callable<Scheduler?>? -> immediate }
//            RxJavaPlugins.setInitSingleSchedulerHandler { scheduler: Callable<Scheduler?>? -> immediate }
            RxAndroidPlugins.setInitMainThreadSchedulerHandler { scheduler: Callable<Scheduler?>? -> immediate }
        }


    }


    @Test
    fun testFunc1() {
        /**
         * subscribeOn 表示 流开始在哪个线程上面, 只要运行一次就确定在哪个线程上面,再次调用无效;
         *
         * >> 运行结果 io和compute 等线程,顺序在前决定了流开始在哪个线程上, mainThread优先级低于其他线程;
         *
         * observeOn 表示下个输出流所处的线程为指定的线程;
         *
         */
        var semaphore = Semaphore(0)
        Observable.fromCallable(object : Callable<String> {
            override fun call(): String {
                System.out.println("fromCallable  ${Thread.currentThread().name}")
                return "test"
            }
        })
                .flatMap {
                    System.out.println("map $it ${Thread.currentThread().name}")
                    return@flatMap Observable.just(it)
                }
                .observeOn(Schedulers.newThread())
                .doOnNext {
                    System.out.println("doOnNext1 $it ${Thread.currentThread().name}")
                }
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext {
                    System.out.println("doOnNext2 $it ${Thread.currentThread().name}")
                }
//                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.io())
                .doOnNext {
                    System.out.println("doOnNext3 $it ${Thread.currentThread().name}")
                }
                .observeOn(Schedulers.io())
                .doOnNext {
                    System.out.println("doOnNext4 $it ${Thread.currentThread().name}")
                }
                .observeOn(Schedulers.computation())
                .doOnNext {
                    System.out.println("doOnNext5 $it ${Thread.currentThread().name}")
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    System.out.println("subscribe  $it ${Thread.currentThread().name}")
                    semaphore.release()
                }

        semaphore.acquire()
        System.out.println("exit ${Thread.currentThread().name}")
    }
}