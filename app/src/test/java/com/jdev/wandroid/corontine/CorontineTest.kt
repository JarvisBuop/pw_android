package com.jdev.wandroid.corontine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.util.*
class CorontineTest {

    @Test
    fun testCorontine1(){
        println("start------ ${Thread.currentThread().name}")
        GlobalScope.launch {
            println("work------ ${Thread.currentThread().name}")
        }
        println("end------ ${Thread.currentThread().name}")
        Thread.sleep(2000)
    }

    @Test
    fun main() = runBlocking<Unit> {
        println("start------ ${Thread.currentThread().name}")
        launch(Dispatchers.Unconfined) { // 非受限的——将和主线程一起工作
            println("Unconfined      : I'm working in thread ${Thread.currentThread().name}")
            delay(500)
            println("Unconfined      : After delay in thread ${Thread.currentThread().name}")
        }
        launch { // 父协程的上下文，主 runBlocking 协程
            println("main runBlocking: I'm working in thread ${Thread.currentThread().name}")
            delay(1000)
            println("main runBlocking: After delay in thread ${Thread.currentThread().name}")
        }
    }

    private lateinit var scope : CoroutineScope
    @Before
    fun create(){
        scope =  CoroutineScope(Dispatchers.Default)
    }

    @Test
    fun testScope(){
        println(" I'm working in thread ${Thread.currentThread().name}")
        scope.launch {
            println(" I'm working in thread2 ${Thread.currentThread().name}")
        }
        Thread.sleep(1000)
    }
}