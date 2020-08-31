package com.jdev.wandroid.pattern.iterator_intercept

/**
 * Created by JarvisDong on 2018/12/16.
 * @Description:
 * @see:
 *
 * 设计模式8--责任链模式
 *
 * 抽象处理者
 *
 * >>由强引用连接成一条责任链,由一个判断条件判断是否是哪个节点去处理;
 */
abstract class AbstractHandler {
    //下一节点处理者
    var nextHander: AbstractHandler? = null

    //处理请求
    fun handleRequest(request: AbstractRequest) {
        //通过处理级别判断是否当前节点处理,如果不是交由下一个节点处理
        if (getHandlerLevel() == request.getRequestLevel()) {
            handle(request)
        } else {
            nextHander?.handleRequest(request) ?:
                    System.out.println(" all of handler can not handle ")
        }
    }

    //处理者对象的处理级别
    protected abstract fun getHandlerLevel(): Int

    //每个处理者对象的具体处理方式
    protected abstract fun handle(request: AbstractRequest)
}