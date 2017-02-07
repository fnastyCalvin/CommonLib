package com.calvin.commonlib.common.base;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ContentFrameLayout;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import com.calvin.commonlib.BuildConfig;
import com.calvin.commonlib.R;
import com.calvin.commonlib.common.util.SystemBarTintManager;
import com.calvin.commonlib.common.view.ContentLoadingProgressBar;
import com.umeng.analytics.MobclickAgent;

import java.lang.ref.WeakReference;

import butterknife.ButterKnife;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

public abstract class BaseActivity extends AppCompatActivity {
    protected final String TAG;
    private boolean isFinished;

    public BaseActivity() {
        TAG = getClass().getSimpleName();
    }
    protected SystemBarTintManager tintManager;
    protected ProgressDialog mProgressDialog;
    protected Context context;
    protected View rootView;
    protected WeakHandler weakHandler;
    protected CompositeSubscription compositeSubscription;

    protected void onCreate(Bundle savedInstanceState, @LayoutRes int res) {
        super.onCreate(savedInstanceState);
        setWindowFeature();
        context = this;
        weakHandler = new WeakHandler(this);
        getIntentInfo();
        setContentView(res);
        ContentLoadingProgressBar loadingProgressBar = (ContentLoadingProgressBar) View.inflate(this,R.layout.loading_progressbar,null);
        //set ProgressBar color
        loadingProgressBar.getIndeterminateDrawable().setColorFilter(0xFFFF00FF, PorterDuff.Mode.MULTIPLY);
        final ContentFrameLayout contentView = (ContentFrameLayout) this.findViewById(android.R.id.content);
        if (contentView != null) {
            //show content loading ProgressBar
            contentView.addView(loadingProgressBar,new ContentFrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER));
            loadingProgressBar.show();
            final ViewGroup viewGroup = (ViewGroup) contentView.getChildAt(0);
            ViewCompat.setFitsSystemWindows(viewGroup,true);
            viewGroup.setClipToPadding(false);
            rootView = getWindow().getDecorView();
            ButterKnife.bind(this);
            initToolbar();
            initView();
            initData(savedInstanceState);
            initListener();
            loadingProgressBar.hide();
        }
    }

    /**
     * 设置页面的属性，如：全屏、输入法、无标题等
     */
    protected void setWindowFeature() {
        tintManager = new SystemBarTintManager(this);
        SystemBarTintManager.SystemBarConfig config = tintManager.getConfig();
//		getWindow().getDecorView().setPadding(0, tintManager.getConfig().getNavigationBarHeight(), config.getPixelInsetRight(), config.getPixelInsetBottom());
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setNavigationBarTintEnabled(true);
        tintManager.setNavigationBarTintResource(R.color.transparent);
        tintManager.setStatusBarTintResource(R.color.toolbar_bg);
    }

    /**
     * 获取Intent中的数据
     */
    protected abstract void getIntentInfo();


    /**
     * 初始化标题栏toolbar
     */
    protected void initToolbar() {}

    /**
     * 初始化View,使用
     * {@code getViewById}
     */
    protected abstract void initView();

    /**
     * 初始化或者恢复data，包含恢复保存状态、网络请求、本地数据等等
     *
     */
    protected abstract void initData(Bundle savedInstanceState);

    /**
     * 初始化Listener
     */
    protected abstract void initListener();


    /**
     * findViewById的复写，不需要转型了
     * @param id
     * @return
     */
    public <K extends View> K getViewById(@IdRes int id) {
        return (K) getWindow().findViewById(id);
    }

    public <K extends View> K getViewById(@NonNull View view, @IdRes int id) {
        return (K) view.findViewById(id);
    }

    public void showProgressDialog(String message) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setCancelable(false);
        }
        mProgressDialog.setMessage(message);
        if (!isFinishing()) {
            mProgressDialog.show();
        }
    }

    public void showProgressDialog() {
        showProgressDialog("正在加载...");
    }

    public void dismissProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }


    @Override
    protected void onDestroy() {
        if(compositeSubscription != null) {
            compositeSubscription.isUnsubscribed();
        }
        isFinished = true;
        context = null;
        if(weakHandler != null){
            weakHandler.removeCallbacksAndMessages(null);
        }
        //启动页背景要注释掉，释放内存
		getWindow().setBackgroundDrawable(new ColorDrawable(0));
        ButterKnife.unbind(this);
        super.onDestroy();
    }

    public boolean isFinished() {
        return isFinished;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!BuildConfig.DEBUG) {
            MobclickAgent.onResume(this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(!BuildConfig.DEBUG) {
            MobclickAgent.onPause(this);
        }
    }

    protected CompositeSubscription getCompositeSubscription() {
        if (this.compositeSubscription == null) {
            this.compositeSubscription = new CompositeSubscription();
        }

        return this.compositeSubscription;
    }


    public void addSubscription(Subscription s) {
        getCompositeSubscription().add(s);
    }

    protected static class WeakHandler extends Handler {

        WeakReference<BaseActivity> mReference = null;

        WeakHandler(BaseActivity activity) {
            this.mReference = new WeakReference<BaseActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            BaseActivity activity = mReference.get();
            if (activity == null || activity.isFinishing() || activity.isFinished()) {
                return;
            }

            activity.handleMessage(msg);
        }
    }

    /**
     * WeakHandler handleMessage
     * @param msg
     */
    protected void handleMessage(Message msg){}
}
