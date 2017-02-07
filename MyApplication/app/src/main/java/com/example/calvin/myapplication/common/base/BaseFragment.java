package com.example.calvin.myapplication.common.base;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;


/**
 * Fragment基类，
 */
public abstract class BaseFragment extends Fragment implements OnTouchListener {
	public static String TAG = null;

	public BaseActivity mActivity;

    LinearLayout rootView;
    View contentView;

    private View titleView;

    private TextView textView;

    private ImageView backView;
    private ImageView img_middle;
    private ImageView img_right;
    private View iconView;
    private RelativeLayout titleLayout;


//    private ScreenObserver mScreenObserver;

    public BaseFragment() {
		TAG = ((Object) this).getClass().getSimpleName();
	}

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mActivity = (BaseActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
           /* mScreenObserver = new ScreenObserver(getActivity());
            mScreenObserver.requestScreenStateUpdate(new ScreenObserver.ScreenStateListener() {
                @Override
                public void onScreenStateChange(boolean isScreenOn) {
                    if (!isScreenOn&& LockUtil.getPwdStatus(getActivity())&&LockUtil.getPwd(getActivity()).length>0) {
                        doSomethingOnScreenOff();
                    }
                }
            });*/
            /*titleView = View.inflate(mActivity, R.layout.layout_base_activity_head, null);

            titleLayout = (RelativeLayout) titleView.findViewById(R.id.topTitle);

            textView = (TextView) titleView.findViewById(R.id.top_title);

            backView = (ImageView) titleView.findViewById(R.id.img_back);
            
            img_middle = (ImageView) titleView.findViewById(R.id.img_middle);
            
            img_right = (ImageView) titleView.findViewById(R.id.img_right);*/

            //设置标题
            initHeadView();

            //设置fragment的内容
            contentView = setContentView(mActivity);

            rootView = new LinearLayout(mActivity);

            rootView.setOrientation(LinearLayout.VERTICAL);

            rootView.addView(titleView);

            if(contentView == null)
            {
                throw new RuntimeException("please implement setContentView method!");
            }

            rootView.addView(contentView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }
        ViewGroup parent = (ViewGroup) rootView.getParent();
        if (parent != null)
        {
            parent.removeView(rootView);
        }

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        initData(savedInstanceState);
        initListener();
    }


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
     * 初始化标题栏toolbar
     */
    protected void initHeadView() {
    }


    /**
     * 设置主体页面
     * @param context
     * @return
     */
    protected abstract View setContentView(Context context);


    public <K extends View> K getViewById(int id) {
        return (K) contentView.findViewById(id);
    }


    /**
     * 点击返回键的监听事件
     * @param l
     */
    public void setBackViewOnClickListener(final OnClickListener l)
    {
        backView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                l.onClick(v);
            }
        });
    }

    public void setBackViewVisiable(boolean visiable)
    {
        if(backView != null)
        {
            if(visiable)
                backView.setVisibility(View.VISIBLE);
            else
                backView.setVisibility(View.GONE);
        }
    }



    public void setMiddleButtonOnClickListener(int localtion, int icon,final OnClickListener l)
    {
    	img_middle.setBackgroundResource(icon);
        img_middle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                l.onClick(v);
            }
        });
    }

    public void setMiddleButtonVisiable(boolean visiable)
    {
        if(img_middle != null)
        {
            if(visiable)
                img_middle.setVisibility(View.VISIBLE);
            else
                img_middle.setVisibility(View.GONE);
        }
    }



    public void setRightButtonOnClickListener(int localtion, int icon,final OnClickListener l)
    {
    	img_right.setBackgroundResource(icon);
        img_right.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                l.onClick(v);
            }
        });
    }

    public void setRightButtonVisiable(boolean visiable)
    {
        if(img_right != null)
        {
            if(visiable)
                img_right.setVisibility(View.VISIBLE);
            else
                img_right.setVisibility(View.GONE);
        }
    }




    public void setHeadViewVisiable(boolean visiable)
    {
        if(titleLayout != null)
        {
            if(visiable)
                titleLayout.setVisibility(View.VISIBLE);
            else
                titleLayout.setVisibility(View.GONE);
        }
    }



    public void setIconTitle(int icon, OnClickListener l) {
        iconView = new ImageView(mActivity);
        ((ImageView) iconView).setImageResource(icon);
        iconView.setOnClickListener(l);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        titleLayout.addView(iconView, params);

    }

    public void setTitleText(int text) {
        if (textView != null) {
            textView.setText(text);
        }
    }

    public void setTitleText(String text) {
        if (textView != null) {
            textView.setText(text);
        }
    }



    protected void setTitle() {
    }

   /* private void doSomethingOnScreenOff() {
        if(!LoginLockActivity.IS_SHOW){
            Intent intent = new Intent();
            intent.setClass(getActivity().getApplicationContext(), LoginLockActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }
*/
    protected void back() {
		getFragmentManager().popBackStack();
	}


	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return false;
	}


    protected abstract void onCancelHttpRequest();


	public void onResume() {
		super.onResume();
//		MobclickAgent.onPageEnd();
        //MobclickAgent.onResume(getActivity());
	}

	public void onPause() {
		super.onPause();
        //MobclickAgent.onPause(getActivity());
	}

    @Override
    public void onDestroyView() {
       /* if(mScreenObserver!=null){
            mScreenObserver.stopScreenStateUpdate();
        }*/
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        onCancelHttpRequest();
        super.onDestroy();
    }
}
