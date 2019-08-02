package com.jdev.wandroid.utils;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.jdev.wandroid.MyApplication;

import java.math.BigDecimal;

/**
 * Created by cjh on 17-3-14
 */

public class ViewUtils {

    public interface OnCallback<T> {
        void callback(T ts);
    }

    public static void resetVisiableState(boolean isVisiable, View... params) {
        if (isVisiable) {
            setVisiable(params);
        } else {
            setGone(params);
        }
    }

    public static void setVisiable(View view) {
        if (view != null) {
            view.setVisibility(View.VISIBLE);
        }
    }

    public static void setVisiable(View... views) {
        for (View view : views) {
            setVisiable(view);
        }
    }

    public static void setInVisible(View view) {
        if (view != null) {
            view.setVisibility(View.INVISIBLE);
        }
    }

    public static void setInVisible(View... params) {
        for (View view : params) {
            setInVisible(view);
        }
    }

    public static void setGone(View view) {
        if (view != null) {
            view.setVisibility(View.GONE);
        }
    }

    public static void setGone(View... params) {
        for (View view : params) {
            setGone(view);
        }
    }

    public static String getText(TextView textView) {
        if (textView != null) {
            String text = textView.getText().toString().trim();
            return text;
        }
        return "";
    }

    public static void setMsgIntoView(String msg, TextView textView) {
        if (msg != null && textView != null) {
            textView.setText(msg);
        }
    }

    public static void setMsgIntoView(TextView[] textViews, String[] strings) {
        if (textViews.length == strings.length) {
            for (int i = 0; i < textViews.length; i++) {
                setMsgIntoView(strings[i], textViews[i]);
            }
        } else {
            LogUtils.e("长度不同");
        }
    }

    public static void setMsgIntoView(BigDecimal[] prices, TextView[] textViews) {
        if (textViews.length == prices.length) {
            for (int i = 0; i < textViews.length; i++) {
                setMsgIntoView(prices[i], textViews[i]);
            }
        } else {
            LogUtils.e("长度不同");
        }
    }

    public static void setMsgIntoView(BigDecimal price, TextView textView) {
        if (price != null) {
            if (!TextUtils.isEmpty(price + "") && textView != null) {
                textView.setText(price + "");
            }
        }
    }

    public static void setMsgIntoViewWithNotFloat(BigDecimal[] prices, TextView[] textViews) {
        if (textViews.length == prices.length) {
            for (int i = 0; i < textViews.length; i++) {
                setMsgIntoViewWithNotFloat(prices[i], textViews[i]);
            }
        } else {
            LogUtils.e("长度不同");
        }
    }

    public static void setMsgIntoViewWithNotFloat(BigDecimal price, TextView textView) {
        if (price != null) {
            if (!TextUtils.isEmpty(price + "") && textView != null) {
                String s = price + "";
                if (s.contains(".00")) {
                    s = s.replace(".00", "");
                }
                textView.setText(s);
            }
        }
    }

    public static void setHtmlMsgIntoView(String html, TextView textView) {
        if (!TextUtils.isEmpty(html) && textView != null) {
            textView.setText(Html.fromHtml(html));
        }
    }

    public static void setHintHtmlMsgIntoView(String html, EditText textView) {
        if (!TextUtils.isEmpty(html) && textView != null) {
            textView.setHint(Html.fromHtml(html));
        }
    }

    public static void clearAnimation(View view) {
        if (view != null) {
            view.clearAnimation();
        }
    }

    public static void startAnimation(View view, Animation animation) {
        if (view != null && animation != null) {
            view.startAnimation(animation);
        }
    }

    public static void setImageBitmap(ImageView imageView, Bitmap bitmap) {
        if (imageView != null && bitmap != null) {
            imageView.setImageBitmap(bitmap);
        }
    }

    public static void setImageRes(ImageView imageView, int resId) {
        if (imageView != null && resId != -1) {
            imageView.setImageResource(resId);
        }
    }

    public static void setViewBg(View view, int color) {
        if (view != null) {
            view.setBackgroundColor(color);
        }
    }

    public static boolean isAllInvisiable(View... params) {
        for (View view : params) {
            if (view != null && view.getVisibility() == View.GONE) {
                return true;
            }
        }
        return false;
    }

    public static boolean isAllVisiable(View... params) {
        for (View view : params) {
            if (view == null || view.getVisibility() == View.GONE) {
                return false;
            }
        }

        return true;
    }

    public static void recycleImageView(View... params) {
        for (View view : params) {
            if (view != null && view instanceof ImageView) {
                recyclerImageView((ImageView) view);
            }
        }
    }

    private static void recyclerImageView(ImageView view) {
        Drawable drawable = view.getDrawable();
//        LogUtils.e(drawable.toString());
        if (drawable != null) {
            Bitmap bmp = null;
            if (drawable instanceof BitmapDrawable) {
                bmp = ((BitmapDrawable) drawable).getBitmap();
            } else {
//                Glide v3 版本中的 GlideDrawable 类已经被移除，支持标准的Android Drawable 。 GlideBitmapDrawable 也已经被删除，由 BitmapDrawable 代替之。
//                bmp = ((GlideBitmapDrawable) drawable).getBitmap();
            }
            if (bmp != null && !bmp.isRecycled()) {
                view.setImageBitmap(null);
                LogUtils.e(bmp.toString());
                bmp.recycle();
                bmp = null;
            }
        }
    }

    public static void setBackgound(View view, Drawable drawable) {
        if (view != null) {
            view.setBackground(drawable);
        }
    }

    public static void setTextColor(TextView view, int color){
        if(view != null){
            try {
                view.setTextColor(color);
            }catch (Exception e){}
        }
    }

    public static void setTextColorRes(TextView view, @ColorRes int color){
        if(view != null){
            try {
                view.setTextColor(MyApplication.Companion.getApp().getResources().getColor(color));
            }catch (Exception e){}
        }
    }


    public static void setImageColorFilter(ImageView imageView, int color){
        if(imageView != null){
            try{
                imageView.setColorFilter(color);
            }catch (Exception e){}
        }
    }

    public static void setBackground(View[] views, Drawable[] drawables) {
        if (views.length == drawables.length) {
            for (int i = 0; i < views.length; i++) {
                setBackgound(views[i], drawables[i]);
            }
        } else {
            LogUtils.e("长度不同");
        }
    }

    public static boolean isEmpty(TextView textView) {
        return TextUtils.isEmpty(getText(textView));
    }

    public static void clearAnimations(View... params) {
        for (View view : params) {
            clearAnimation(view);
        }
    }

}
