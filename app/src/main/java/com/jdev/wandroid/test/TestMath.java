package com.jdev.wandroid.test;

import android.util.Log;

/**
 * info: create by jd in 2019/11/15
 *
 * @see:
 * @description:
 */
public class TestMath {

    public static void testInteger() {
        Integer a = 1;
        Integer b = 2;
        Integer c = 3;
        Integer d = 3;
        Integer e = 321;
        Integer f = 321;
        Long g = 3L;
        Log.e("TESTMATH", String.valueOf(c == d));//true
        Log.e("TESTMATH", String.valueOf(e == f));//false
        Log.e("TESTMATH", String.valueOf(c == (a + b)));//true
        Log.e("TESTMATH", String.valueOf(c.equals(a + b)));//true
        Log.e("TESTMATH", String.valueOf(g == (a + b)));//true
        Log.e("TESTMATH", String.valueOf(g.equals(a + b)));//false
    }
}
