package com.jdev.wandroid.okhttp

import okhttp3.*
import org.junit.After
import org.junit.Before
import org.junit.Test

import java.io.IOException
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 *  单元测试 - okhttp 的调用方式
 */
class TestOkHttpTest {

    lateinit var url: String
    @Before
    fun setUp() {
        url = "http://test.youshikoudai.com/1bPlus-web/api/generalConfig/public/andriod/version"
    }

    @After
    fun tearDown() {
    }

    @Test
    fun testSyncOk() {
        var countDownLatch = CountDownLatch(1)

        var build = OkHttpClient.Builder()
                .readTimeout(500, TimeUnit.MILLISECONDS)
                .addInterceptor {
                   var request = it.request()
                    var response = it.proceed(request)
                    System.out.println("addInterceptor $response")
                    return@addInterceptor response
                }
                .build()
        var request = Request.Builder()
                .url(url)
                .build()
        var newCall = build.newCall(request)

        newCall.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                System.out.println("onFailure")
                countDownLatch.countDown()
            }

            override fun onResponse(call: Call, response: Response) {
                System.out.println("onResponse ${response.toString()} ${response.body()?.string()} ${Thread.currentThread().name}")
                countDownLatch.countDown()
            }
        })

        countDownLatch.await()
    }

    @Test
    fun testASyncOk(){
        var build = OkHttpClient.Builder()
                .readTimeout(500, TimeUnit.MILLISECONDS)
                .addInterceptor {
                    var request = it.request()
                    var response = it.proceed(request)
                    System.out.println("addInterceptor ${response.toString()}")
                    return@addInterceptor response
                }
                .build()
        var request = Request.Builder()
                .url(url)
                .build()
        var newCall = build.newCall(request)
        var response = newCall.execute()

        System.out.println("response ${response.toString()} \n ${response.body()?.string()} ${Thread.currentThread().name}")
    }
}