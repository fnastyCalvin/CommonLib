package com.example.calvin.myapplication.common.base;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Activity的基类，项目中除SplashActivity之外的所有的Activity都应该继承这个类，方便对各个Activity进行管理。
 */
public abstract class BaseActivity extends FragmentActivity implements View.OnClickListener{

    public static String TAG = null;

	private View titleView;

	private RelativeLayout titleLayout;


	private TextView textView,tv_right;

    private ImageView backView,img_right,img_middle;

    public Context context;


//    private ScreenObserver mScreenObserver;

    public BaseActivity() {
        TAG = ((Object) this).getClass().getSimpleName();
    }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN|WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
//                |WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setWindowFeature();
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        context = this;
        getIntentInfo();
	}

    /**
     *
     * @param savedInstanceState
     * @param res
     */
    protected void onCreate(Bundle savedInstanceState, int res) {
		super.onCreate(savedInstanceState);
        /*mScreenObserver = new ScreenObserver(this);
        mScreenObserver.requestScreenStateUpdate(new ScreenObserver.ScreenStateListener() {
            @Override
            public void onScreenStateChange(boolean isScreenOn) {
                if (!isScreenOn&& LockUtil.getPwdStatus(context)&&LockUtil.getPwd(context).length>0) {
                    doSomethingOnScreenOff();
                }
            }
        });*/

		/*titleView = View.inflate(this, R.layout.layout_base_activity_head, null);

		titleLayout = (RelativeLayout) titleView.findViewById(R.id.topTitle);

		textView = (TextView) titleView.findViewById(R.id.top_title);

        backView = (ImageView) titleView.findViewById(R.id.img_back);

        img_right = (ImageView) titleView.findViewById(R.id.img_right);

        img_middle = (ImageView) titleView.findViewById(R.id.img_middle);

        tv_right=(TextView)titleView.findViewById(R.id.tv_right);*/

		initHeadView();

		View contentView = View.inflate(this, res, null);

		LinearLayout layout = new LinearLayout(this);

		layout.setOrientation(LinearLayout.VERTICAL);

		layout.addView(titleView);
		layout.addView(contentView, new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));

		setContentView(layout);

        getWindow().setBackgroundDrawable(null);

        initView();
        initData(savedInstanceState);
        initListener();
	}

    /**
     * 获取Intent中的数据
     */
    protected abstract void getIntentInfo();

    /**
     * 设置页面的属性，如：全屏、输入法、无标题等
     */
    protected void setWindowFeature() {
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
     * findViewById的复写，不需要转型了
     * @param id
     * @return
     */
    public <K extends View> K getViewById(int id) {
        return (K) getWindow().findViewById(id);
    }


    /**
     * 返回键
     */
    public void setBackView(boolean visiable,final OnClickListener l)
    {
        if(backView != null)
        {
            if(visiable)
                backView.setVisibility(View.VISIBLE);
            else
                backView.setVisibility(View.GONE);
            if(l != null) {
                backView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        l.onClick(v);
                    }
                });
            }
        }

    }

    /**
     * 右边文字点击
     * @param visiable
     * @param str
     * @param l
     */
    public void setRightTextView(int visiable, String str,final OnClickListener l){
        if(tv_right != null)
        {
            tv_right.setVisibility(visiable);

            if(str.length()>0){
                tv_right.setText(str);
            }
            if(l != null) {
                tv_right.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        l.onClick(v);
                    }
                });
            }
        }
    }



    /**
     * 设置右边按钮
     * @param visiable
     * @param icon
     * @param l
     */
    public void setRightButton(boolean visiable, int icon,final OnClickListener l)
    {
        if(img_right != null)
        {
            if(visiable)
                img_right.setVisibility(View.VISIBLE);
            else
                img_right.setVisibility(View.GONE);

            if(icon != 0)
                img_right.setBackgroundResource(icon);
            if(l != null) {
                img_right.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        l.onClick(v);
                    }
                });
            }
        }

    }


    /**
     * 设置中间按钮
     * @param visiable
     * @param icon
     * @param l
     */
    public void setMiddleButtonOnClickListener(boolean visiable, int icon,final OnClickListener l)
    {
        if(img_middle != null)
        {
            if(visiable)
                img_middle.setVisibility(View.VISIBLE);
            else
                img_middle.setVisibility(View.GONE);

            if(icon != 0)
                img_middle.setBackgroundResource(icon);
            if(l != null) {
                img_middle.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        l.onClick(v);
                    }
                });
            }
        }

    }

    /**
     * 设置标题栏是否显示
     * @param visiable
     */
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


    /**
     * 设置标题
     * @param text 资源ID
     */
	public void setTitleText(int text) {
		if (textView != null) {
			textView.setText(text);
		}
	}

    /**
     * 设置标题
     * @param text 字符串
     */
	public void setTitleText(String text) {
		if (textView != null) {
			textView.setText(text);
		}
	}

    protected void back() {
        back(true);
    }

    /**
     * 返回键（动画关闭）
     * @param anim
     */
    protected void back(boolean anim) {
        finish();
        if (android.os.Build.VERSION.SDK_INT > 10) {
            if (anim) {
//                overridePendingTransition(R.anim.push_left_in,R.anim.push_right_out);
            }
            else {

            }
        }
    }

	public void popBackAllStack() {
		getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
	}

	@Override
	protected void onDestroy() {
        /*if(mScreenObserver!=null){
            mScreenObserver.stopScreenStateUpdate();
        }*/
		super.onDestroy();

//		ActivityCollector.remove(this);
	}

    @Override
    protected void onStop() {
        onCancelHttpRequest();
        super.onStop();
    }

    /**
     * 取消网络请求
     */
    protected abstract void onCancelHttpRequest();

    /*private void doSomethingOnScreenOff() {
        if(!LoginLockActivity.IS_SHOW){
            Intent intent = new Intent();
            intent.setClass(getApplicationContext(), LoginLockActivity.class);
            intent.putExtra("current","resume");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }*/

	@Override
	protected void onPause() {
		super.onPause();
	//	MobclickAgent.onPageEnd(this.getLocalClassName());
//		MobclickAgent.onPause(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
	//	MobclickAgent.onPageStart(this.getLocalClassName());
//		MobclickAgent.onResume(this);
	}


}
