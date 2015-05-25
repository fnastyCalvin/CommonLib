package com.example.calvin.myapplication.test;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;

import com.example.calvin.myapplication.R;
import com.example.calvin.myapplication.common.util.SwipeBackUtil;

/**
 * Created by jiangtao on 2015/5/8.
 */
public class BActivity extends Activity{

    SwipeBackUtil helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b);
        helper = new SwipeBackUtil(this);
    }

   /* @Override
    public boolean onTouchEvent(MotionEvent event) {
        helper.onTouchEvent(event);
        return super.onTouchEvent(event);
    }*/

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return helper.onTouchEvent(ev) || super.dispatchTouchEvent(ev);
    }
}
