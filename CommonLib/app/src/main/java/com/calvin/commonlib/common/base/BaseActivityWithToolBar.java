package com.calvin.commonlib.common.base;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.ContentFrameLayout;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.calvin.commonlib.R;
import com.calvin.commonlib.common.rx.OnSingleClickListener;
import com.calvin.commonlib.common.view.ContentLoadingProgressBar;

import butterknife.ButterKnife;

/**
 * 有标题栏的BaseActivity
 */
public abstract class BaseActivityWithToolBar extends BaseActivity {
	public Toolbar toolbar;

	protected void onCreate(Bundle savedInstanceState, @LayoutRes int res) {
		super.onCreate(savedInstanceState);
		setWindowFeature();
		context = this;
		weakHandler = new WeakHandler(this);
		getIntentInfo();
		View contentView = View.inflate(this, res, null);
		toolbar = new Toolbar(context);
		toolbar.setBackgroundResource(R.color.toolbar_bg);
//		toolbar.setNavigationIcon(R.drawable.selector_button_back_white);
		toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
		LinearLayout linearLayout = new LinearLayout(context);
		linearLayout.setOrientation(LinearLayout.VERTICAL);
		linearLayout.addView(toolbar);
		linearLayout.addView(contentView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		setContentView(linearLayout);
		ContentLoadingProgressBar loadingProgressBar = (ContentLoadingProgressBar) View.inflate(this,R.layout.loading_progressbar,null);
		ContentFrameLayout contentFrameLayout = (ContentFrameLayout) this.findViewById(android.R.id.content);
		if (contentFrameLayout != null) {
			//show content loading ProgressBar
			contentFrameLayout.addView(loadingProgressBar,new ContentFrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER));
			loadingProgressBar.show();
			final ViewGroup viewGroup = (ViewGroup) contentFrameLayout.getChildAt(0);
			ViewCompat.setFitsSystemWindows(viewGroup, true);
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

	@Override
	protected void initToolbar() {
		super.initToolbar();
		toolbar.setNavigationOnClickListener(new OnSingleClickListener() {
			@Override
			public void onClicked(View v) {
				finish();
			}
		});
	}
}
