package com.example.calvin.myapplication.test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.example.calvin.myapplication.R;
import com.example.calvin.myapplication.common.util.SwipeBackUtil;

/**
 * Created by jiangtao on 2015/5/8.
 */
public class AActivity extends Activity{

    SwipeBackUtil helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_a);
        helper = new SwipeBackUtil(this);
        ImageView img = (ImageView) findViewById(R.id.img);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AActivity.this, SimpleListActivity.class);
                startActivity(i);
            }
        });

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return helper.onTouchEvent(ev) || super.dispatchTouchEvent(ev);
    }
}
