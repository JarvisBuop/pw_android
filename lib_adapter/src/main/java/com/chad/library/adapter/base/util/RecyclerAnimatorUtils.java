package com.chad.library.adapter.base.util;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

/**
 * info: create by jd in 2019/5/5
 *
 * @see:
 * @description:
 */
public class RecyclerAnimatorUtils {

    public static void setDefaultAnimator(RecyclerView recyclerView, boolean isSupport) {
        try {
            ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(isSupport);
        } catch (Exception e) {

        }
    }
}
