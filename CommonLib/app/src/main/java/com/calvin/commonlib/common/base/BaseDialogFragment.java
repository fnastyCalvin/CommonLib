package com.calvin.commonlib.common.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;

public abstract class BaseDialogFragment extends DialogFragment {
	public String TAG = null;

	public BaseDialogFragment() {
		TAG = ((Object) this).getClass().getSimpleName();
	}

	public Context context;

	protected View rootView;

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		this.context =  getActivity();
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setCancelable(true);
		//全屏 ，连statusBar都消失掉
//		setStyle(DialogFragment.STYLE_NO_FRAME, android.R.style.Theme_Light_NoTitleBar_Fullscreen);
//		全屏 ，statusBar还在
		setStyle(DialogFragment.STYLE_NO_FRAME, android.R.style.Theme_Holo_Light);
		setStyle(STYLE_NO_TITLE, android.R.style.Theme_Translucent_NoTitleBar);
		getIntentInfo();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		if (rootView == null) {
			rootView = inflater.inflate(setContentViewId(),null);
		}
		ButterKnife.bind(this, rootView);
		getDialog().setCanceledOnTouchOutside(true);
		return rootView;
	}

	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initView();
		initData(savedInstanceState);
		initListener();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		if(getDialog() == null){
			//防止DialogFragment在某些特定机型下crash
			//http://stackoverflow.com/questions/12265611/dialogfragment-nullpointerexception-support-library
			setShowsDialog(false);
		}
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void show(FragmentManager manager, String tag) {
		try {
			FragmentTransaction ft = manager.beginTransaction();
			ft.add(this, tag);
			ft.commitAllowingStateLoss();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
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


	@Override
	public void onDestroyView() {
		ButterKnife.unbind(this);
		super.onDestroyView();
	}

}
