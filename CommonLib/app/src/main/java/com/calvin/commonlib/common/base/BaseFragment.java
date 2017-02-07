package com.calvin.commonlib.common.base;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import butterknife.ButterKnife;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

public abstract class BaseFragment extends Fragment {
    public String TAG = null;

    protected boolean isFirstIn = true;
    /**
     * View是否已经初始化完成
     */
    private boolean isPrepared = false;

    public BaseFragment() {
        TAG = ((Object) this).getClass().getSimpleName();
    }

    protected ProgressDialog mProgressDialog;
    public BaseActivity context;

    protected View rootView;

    protected CompositeSubscription compositeSubscription;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = (BaseActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(setContentViewId(),null);
        }
        ViewGroup parent = (ViewGroup) rootView.getParent();
        if (parent != null)
        {
            parent.removeView(rootView);
        }
        isFirstIn = true;
        ButterKnife.bind(this,rootView);
        return rootView;
    }

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getIntentInfo();
        initToolbar();
        initView();
        initData(savedInstanceState);
        initListener();
        initPrepare();
    }

    /**
     * 如果是与ViewPager一起使用，调用的是setUserVisibleHint
     * @param isVisibleToUser
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser ) {
            if (isFirstIn){
                isFirstIn = false;
                initPrepare();
            }
            else {
                onUserVisible();
            }
        }
    }

    private synchronized void initPrepare() {
        if (isPrepared) {
            onFirstUserVisible();
        } else {
            isPrepared = true;
        }
    }

    /**
     * 如果是通过FragmentTransaction的show和hide的方法来控制显示，调用的是onHiddenChanged.
     * 若是初始就show的Fragment 为了触发该事件 需要先hide再show
     * @param hidden
     */
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            onUserVisible();
        }
    }

    private boolean isFirstResume = true;

    @Override
    public void onResume() {
        super.onResume();
        if (isFirstResume) {
            isFirstResume = false;
            return;
        }

        if (getUserVisibleHint()) {
            Log.i(TAG, "onResume: onUserVisible");
            onUserVisible();
        }
    }

    /**
     * 当第一次fragment可见时调用（进行初始化工作）,可在该方法中实现懒加载数据
     */
    protected void onFirstUserVisible(){

    }


    /**
     * 当fragment可见时调用（来回切换或者onResume时）
     */
    protected void onUserVisible(){

    }

    /**
     * 设置主体页面
     */
    protected abstract @LayoutRes
    int setContentViewId();

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
    public <K extends View> K getViewById(int id) {
        return getViewById(rootView,id);
    }

    public <K extends View> K getViewById(View rootView, int id) {
        return (K) rootView.findViewById(id);
    }

    public void showProgressDialog(String message) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(context, android.R.style.Theme_Holo_Dialog);
        }
        mProgressDialog.setMessage(message);
        if (!context.isFinishing()) {
            mProgressDialog.show();
        }
    }

    public void showProgressDialog() {
        showProgressDialog("正在加载...");
    }

    public void dismissProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.cancel();
            mProgressDialog.dismiss();
        }
    }

    public void showShortToast(String message) {
        if (!context.isFinishing()) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    }

    public void showLongToast(String message) {
        if (!context.isFinishing()) {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        }
    }

    public AlertDialog.Builder getAleartDialog() {
        return new AlertDialog.Builder(context, android.R.style.Theme_Holo_Dialog);
    }

    private CompositeSubscription getCompositeSubscription() {
        if (this.compositeSubscription == null) {
            this.compositeSubscription = new CompositeSubscription();
        }

        return this.compositeSubscription;
    }


    public void addSubscription(Subscription s) {
        getCompositeSubscription().add(s);
    }

    @Override
    public void onDestroy() {
        isFinished = true;
//		OKHttpRequest.getInstance().cancelPendingRequests(TAG);
        ButterKnife.unbind(this);
//		Log.i("test", TAG);
        super.onDestroy();
    }
    private boolean isFinished;
    public boolean isFinished() {
        return isFinished;
    }
}
