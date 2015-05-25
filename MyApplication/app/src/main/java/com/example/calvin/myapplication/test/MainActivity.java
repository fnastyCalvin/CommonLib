package com.example.calvin.myapplication.test;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.calvin.myapplication.R;
import com.example.calvin.myapplication.common.log.Log;
import com.example.calvin.myapplication.http.OKHttpRequest;
import com.example.calvin.myapplication.http.base.ResultData;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends ActionBarActivity {

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private ListView lvLeftMenu;
    private String[] lvs = {"List Item 01", "List Item 02", "List Item 03", "List Item 04"};
    private ArrayAdapter arrayAdapter;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //get();
        findViews(); //获取控件
        toolbar.setTitle("Toolbar");//设置Toolbar标题
        toolbar.setTitleTextColor(Color.parseColor("#ffffff")); //设置标题颜色
        setSupportActionBar(toolbar);
        //需要api level 14  使用home-icon 可点击
        getSupportActionBar().setHomeButtonEnabled(true);
        // enable ActionBar app icon to behave as action to toggle nav drawer  需要api level 11
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //创建返回键，并实现打开关/闭监听
        mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
//                mAnimationDrawable.stop();
            }
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
//                mAnimationDrawable.start();
            }
        };
        mDrawerToggle.syncState();
        drawerLayout.setDrawerListener(mDrawerToggle);
        //设置菜单列表
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, lvs);
        lvLeftMenu.setAdapter(arrayAdapter);

    }

    private void get() {
        Map<String,String> map = new HashMap<String,String>();
        map.put("userId", "1");
        map.put("name", "test");
            File f = new File("/storage/extSdCard/1.jpg");
        File f2 = new File("/storage/extSdCard/2.jpg");
        List<File> l = new ArrayList<File>();
        l.add(f);
        l.add(f2);
        Log.d("test","是否存在"+f.exists()+f2.exists());
        Type type = new TypeToken<ResultData>(){}.getType();
        OKHttpRequest.getInstance().postFileAsync(this, "http://192.168.0.230:8080/UploadTest/processUpload", type, l,  false, "", "this",
                new OKHttpRequest.HttpRequestCallback<ResultData>() {
                    @Override
                    public void onStart(Context context) {

                    }

                    @Override
                    public void onResult(ResultData result, Response response) {
                        //Log.d("TAG", result.toString());
                    }

                    @Override
                    public void onFailure(Request request, String msg) {

                    }
                });
    }


    private void findViews() {
        imageView = (ImageView) findViewById(R.id.iv_main);
        toolbar = (Toolbar) findViewById(R.id.toolBar);
        drawerLayout = (DrawerLayout) findViewById(R.id.dl_left);
        lvLeftMenu = (ListView) findViewById(R.id.lv_left_menu);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //END即gravity.right 从右向左显示   START即left  从左向右弹出显示
                /*if(drawerLayout.isDrawerVisible(Gravity.LEFT)){
                    drawerLayout.closeDrawer(Gravity.LEFT);
                }else{
                    drawerLayout.openDrawer(Gravity.LEFT);
                }*/
                //get();
                Intent i= new Intent(MainActivity.this,AActivity.class);
                startActivity(i);
            }
        });
    }


}
