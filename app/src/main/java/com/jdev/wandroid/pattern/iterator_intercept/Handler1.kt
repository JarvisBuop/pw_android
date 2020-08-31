package com.jdev.wandroid.pattern.iterator_intercept

import com.jdev.wandroid.pattern.iterator_intercept.AbstractHandler
import com.jdev.wandroid.pattern.iterator_intercept.AbstractRequest

/**
 * Created by JarvisDong on 2018/12/16.
 * @Description:
 * @see:
 *
 * 具体处理者,节点类;
 */
class Handler1 : AbstractHandler(){

    override fun getHandlerLevel(): Int {
        return 1
    }

    override fun handle(request: AbstractRequest) {
        System.out.println("Handler1 request Request level: "
                + request.getRequestLevel())
    }

}