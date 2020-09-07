package com.jdev.wandroid.test

import android.util.Log
import okhttp3.*
import org.junit.After
import org.junit.Before
import org.junit.Test

import org.junit.Assert.*
import java.io.IOException
import java.util.*
import java.util.concurrent.CountDownLatch

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
    fun testOk() {
        var countDownLatch = CountDownLatch(1)

        var build = OkHttpClient.Builder()
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
                System.out.println("onResponse ${response.toString()} ${response.body()?.string()}")
                countDownLatch.countDown()
            }
        })

        countDownLatch.await()
    }
}