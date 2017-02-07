package com.calvin.commonlib.common.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

/**
 * SwipeBack功能
 * @author jiangtao
 */
public class SwipeBackUtil {
    private static final String TAG = SwipeBackUtil.class.getName();
    private boolean mIsTrackingSwipeBackGesture;
    private float mLastX;
    private float mTouchSlop;
    private float mScaledEdgeSlop;
    private float mScreenWidth;
    private View mDecorView;
    private Activity mActivity;

    public SwipeBackUtil(Activity activity) {
        mActivity = activity;
        mTouchSlop = ViewConfiguration.get(mActivity).getScaledTouchSlop();
        mScaledEdgeSlop = ViewConfiguration.get(mActivity).getScaledEdgeSlop();
        mScreenWidth = mActivity.getWindowManager().getDefaultDisplay().getWidth();
        mDecorView = mActivity.getWindow().getDecorView();
        mIsTrackingSwipeBackGesture = false;
        mLastX = 0.0f;
    }

    /**
     * Call this method in Activity's onTouchEvent method.
     *
     * @param ev
     * @return true if consumed the event, false if not.
     */
    public final boolean onTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN && ev.getX() <= mScaledEdgeSlop) {
            mIsTrackingSwipeBackGesture = true;
            return true;
        }

        if (mIsTrackingSwipeBackGesture) {
            if (ev.getAction() == MotionEvent.ACTION_MOVE && Math.abs(ev.getX() - mLastX) > mTouchSlop) {
                mLastX = ev.getX();
                mDecorView.setX(ev.getX());
            } else if (ev.getAction() == MotionEvent.ACTION_UP) {
                mIsTrackingSwipeBackGesture = false;
                if (ev.getX() < 200.0f) {
                    ObjectAnimator anim = ObjectAnimator.ofFloat(mDecorView, "translationX", mDecorView.getX(), 0);
                    anim.start();
                } else {
                    ObjectAnimator anim = ObjectAnimator.ofFloat(mDecorView, "translationX", mDecorView.getX(), mScreenWidth);
                    anim.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            mActivity.finish();
                        }
                    });
                    anim.start();
                }
            }
            return true;
        }
        return false;
    }
}
