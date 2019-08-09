package com.jdev.module_welcome.ui.helper;

import android.annotation.SuppressLint;
import android.os.Build;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ScrollView;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧）Github地址：https://github.com/jeasonlzy0216
 * 版    本：1.0
 * 创建日期：2016/5/8
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class HeaderScrollHelper {

    private int sysVersion;         //当前sdk版本，用于判断api版本
    private ScrollableContainer mCurrentScrollableContainer;

    public HeaderScrollHelper() {
        sysVersion = Build.VERSION.SDK_INT;
    }

    /**
     * 包含有 ScrollView ListView RecyclerView 的组件
     */
    public interface ScrollableContainer {

        /**
         * @return ScrollView ListView RecyclerView 或者其他的布局的实例
         */
        View getScrollableView();
    }

    public void setCurrentScrollableContainer(ScrollableContainer scrollableContainer) {
        this.mCurrentScrollableContainer = scrollableContainer;
    }

    private View getScrollableView() {
        if (mCurrentScrollableContainer == null) return null;
        return mCurrentScrollableContainer.getScrollableView();
    }

    /**
     * 判断是否滑动到顶部方法,ScrollAbleLayout根据此方法来做一些逻辑判断
     * 目前只实现了AdapterView,ScrollView,RecyclerView
     * 需要支持其他view可以自行补充实现
     */
    public boolean isTop() {
        View scrollableView = getScrollableView();
        if (scrollableView == null) {
            Log.e("isTop", "You should call ScrollableHelper.setCurrentScrollableContainer() to set ScrollableContainer.");
            return false;
        }
        if (scrollableView instanceof AdapterView) {
            return isAdapterViewTop((AdapterView) scrollableView);
        }
        if (scrollableView instanceof ScrollView) {
            return isScrollViewTop((ScrollView) scrollableView);
        }
        if (scrollableView instanceof RecyclerView) {
            return isRecyclerViewTop((RecyclerView) scrollableView);
        }
        if (scrollableView instanceof WebView) {
            return isWebViewTop((WebView) scrollableView);
        }
        Log.e("isTop", "scrollableView must be a instance of AdapterView|ScrollView|RecyclerView");
        return false;
    }

    public static boolean isRecyclerViewTop(RecyclerView recyclerView) {
        if (recyclerView != null) {
            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            if (layoutManager instanceof LinearLayoutManager) {
                int firstVisibleItemPosition = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
                View childAt = recyclerView.getChildAt(0);
                if (childAt == null || (firstVisibleItemPosition == 0 && childAt.getTop() == 0)) {
                    return true;
                }
            } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                int[] mFirstVisibleItems = null;
                int[] firstVisibleItemPositions = ((StaggeredGridLayoutManager) layoutManager).findFirstVisibleItemPositions(mFirstVisibleItems);
                View childAt = recyclerView.getChildAt(0);
                if (childAt == null || firstVisibleItemPositions[0] == 0 && childAt.getTop() == 0) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isRecyclerViewBottom(RecyclerView recyclerView) {
        if (recyclerView != null) {
            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            if (layoutManager instanceof LinearLayoutManager) {
                int firstLastItemPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                if (firstLastItemPosition == totalItemCount - 1 && visibleItemCount > 0) {
                    return true;
                }
            } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                int[] mFirstVisibleItems = null;
                int[] firstLastItemPositions = ((StaggeredGridLayoutManager) layoutManager).findLastVisibleItemPositions(mFirstVisibleItems);
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                if (firstLastItemPositions[0] == totalItemCount - 1 && visibleItemCount > 0) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isAdapterViewTop(AdapterView adapterView) {
        if (adapterView != null) {
            int firstVisiblePosition = adapterView.getFirstVisiblePosition();
            View childAt = adapterView.getChildAt(0);
            if (childAt == null || (firstVisiblePosition == 0 && childAt.getTop() == 0)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isAdapterViewBottom(AdapterView adapterView) {
        if (adapterView != null) {
            int lastVisiblePosition = adapterView.getLastVisiblePosition();
            int count = adapterView.getCount();
            int visibleCount = adapterView.getChildCount();
            if ((lastVisiblePosition == count - 1 && visibleCount > 0)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isScrollViewTop(ScrollView scrollView) {
        if (scrollView != null) {
            int scrollViewY = scrollView.getScrollY();
            return scrollViewY <= 0;
        }
        return false;
    }

    public static boolean isScrollViewBottom(ScrollView scrollView) {
        if (scrollView != null) {
            View childAt = scrollView.getChildAt(0);
            int scrollViewY = scrollView.getScrollY();
            int childViewH = childAt.getMeasuredHeight();
            int parentViewH = scrollView.getMeasuredHeight();
            if (scrollViewY == childViewH - parentViewH) {
                return true;
            }
        }
        return false;
    }

    public static boolean isWebViewTop(WebView scrollView) {
        if (scrollView != null) {
            int scrollViewY = scrollView.getScrollY();
            return scrollViewY <= 0;
        }
        return false;
    }

    public static boolean isWebViewBottom(WebView webView) {
        if (webView != null) {
            if (webView.getContentHeight() * webView.getScale() - (webView.getHeight() + webView.getScrollY()) == 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * 将特定的view按照初始条件滚动
     *
     * @param velocityY 初始滚动速度
     * @param distance  需要滚动的距离
     * @param duration  允许滚动的时间
     */
    @SuppressLint("NewApi")
    public void smoothScrollBy(int velocityY, int distance, int duration) {
        View scrollableView = getScrollableView();
        if (scrollableView instanceof AbsListView) {
            AbsListView absListView = (AbsListView) scrollableView;
            if (sysVersion >= 21) {
                absListView.fling(velocityY);
            } else {
                absListView.smoothScrollBy(distance, duration);
            }
        } else if (scrollableView instanceof ScrollView) {
            ((ScrollView) scrollableView).fling(velocityY);
        } else if (scrollableView instanceof RecyclerView) {
            ((RecyclerView) scrollableView).fling(0, velocityY);
        } else if (scrollableView instanceof WebView) {
            ((WebView) scrollableView).flingScroll(0, velocityY);
        }
    }

    /**
     * 将特定的view滑动到顶部;
     */
    public static void scrollToTop(View scrollableView) {
        if (scrollableView instanceof AbsListView) {
            AbsListView absListView = (AbsListView) scrollableView;
            absListView.smoothScrollToPosition(0);
        } else if (scrollableView instanceof ScrollView) {
            ((ScrollView) scrollableView).smoothScrollTo(0, 0);
        } else if (scrollableView instanceof RecyclerView) {
            ((RecyclerView) scrollableView).smoothScrollToPosition(0);
        } else if (scrollableView instanceof WebView) {
            ((WebView) scrollableView).scrollTo(0, 0);
        }
    }

    public static void scrollToTopNoAnimator(View scrollableView) {
        if (scrollableView instanceof AbsListView) {
            AbsListView absListView = (AbsListView) scrollableView;
            absListView.scrollTo(0, 0);
        } else if (scrollableView instanceof ScrollView) {
            ((ScrollView) scrollableView).scrollTo(0, 0);
        } else if (scrollableView instanceof RecyclerView) {
            ((RecyclerView) scrollableView).scrollToPosition(0);
        } else if (scrollableView instanceof WebView) {
            ((WebView) scrollableView).scrollTo(0, 0);
        }
    }
}
