package com.jdev.wandroid.rxjava

import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.internal.schedulers.ExecutorScheduler.ExecutorWorker
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.BeforeClass
import org.junit.Test
import java.util.concurrent.Callable
import java.util.concurrent.Executor
import java.util.concurrent.TimeUnit


/**
 * info: create by jd in 2020/9/10
 * @see:
 * @description:
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
                    return ExecutorWorker(Executor { obj: Runnable -> obj.run() })
                }
            }

            RxJavaPlugins.setInitIoSchedulerHandler { scheduler: Callable<Scheduler?>? -> immediate }
            RxJavaPlugins.setInitComputationSchedulerHandler { scheduler: Callable<Scheduler?>? -> immediate }
            RxJavaPlugins.setInitNewThreadSchedulerHandler { scheduler: Callable<Scheduler?>? -> immediate }
            RxJavaPlugins.setInitSingleSchedulerHandler { scheduler: Callable<Scheduler?>? -> immediate }
            RxAndroidPlugins.setInitMainThreadSchedulerHandler { scheduler: Callable<Scheduler?>? -> immediate }
        }


    }


    @Test
    fun testFunc1() {
        Observable.fromCallable(object : Callable<String> {
            override fun call(): String {
                System.out.println("fromCallable  ${Thread.currentThread().name}")
                return "test"
            }
        }).map {
            System.out.println("map $it ${Thread.currentThread().name}")
        }.observeOn(Schedulers.io())
                .doOnNext {
                    System.out.println("doOnNext1 $it ${Thread.currentThread().name}")
                }
                .subscribeOn(Schedulers.io())
                .doOnNext {
                    System.out.println("doOnNext2 $it ${Thread.currentThread().name}")
                }.subscribeOn(Schedulers.computation())
                .doOnNext {
                    System.out.println("doOnNext3 $it ${Thread.currentThread().name}")
                }.observeOn(Schedulers.io())
                .doOnNext {
                    System.out.println("doOnNext4 $it ${Thread.currentThread().name}")
                }.observeOn(Schedulers.computation())
                .doOnNext {
                    System.out.println("doOnNext5 $it ${Thread.currentThread().name}")
                }
                .subscribe {
                    System.out.println("subscribe  $it ${Thread.currentThread().name}")
                }
    }
}