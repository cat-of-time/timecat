package com.time.cat.component.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.MenuItem;
import android.widget.TextView;
import android.support.design.widget.BottomNavigationView;

import com.time.cat.R;
import com.time.cat.component.base.BaseActivity;
import com.time.cat.mvp.presenter.ActivityPresenter;

/**
 * @author dlink
 * @date 2018/1/19
 */
public class MainActivity extends BaseActivity implements ActivityPresenter, BottomNavigationView.OnNavigationItemSelectedListener{
    @SuppressWarnings("unused")
    private static final String TAG = "MainActivity";


    //<生命周期>-------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //<功能归类分区方法，必须调用>-----------------------------------------------------------------
        initView();
        initData();
        initEvent();
        //</功能归类分区方法，必须调用>----------------------------------------------------------------
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    //</生命周期>------------------------------------------------------------------------------------


    //<UI显示区>---操作UI，但不存在数据获取或处理代码，也不存在事件监听代码--------------------------------
    TextView message;
    BottomNavigationView navigation;

    @Override
    public void initView() {//必须调用
        message = (TextView) findViewById(R.id.message);
        navigation = (BottomNavigationView) findViewById(R.id.navigation);
    }
    //</UI显示区>---操作UI，但不存在数据获取或处理代码，也不存在事件监听代码)>-----------------------------


    //<Data数据区>---存在数据获取或处理代码，但不存在事件监听代码-----------------------------------------
    @Override
    public void initData() {//必须调用

    }
    //</Data数据区>---存在数据获取或处理代码，但不存在事件监听代码----------------------------------------


    //<Event事件区>---只要存在事件监听代码就是----------------------------------------------------------
    @Override
    public void initEvent() {//必须调用
        navigation.setOnNavigationItemSelectedListener(this);
    }

    //-//<Listener>------------------------------------------------------------------------------
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_home:
                message.setText(R.string.title_home);
                return true;
            case R.id.navigation_dashboard:
                message.setText(R.string.title_dashboard);
                return true;
            case R.id.navigation_notifications:
                message.setText(R.string.title_notifications);
                return true;
        }
        return false;
    }
    //-//</Listener>-----------------------------------------------------------------------------

    //</Event事件区>---只要存在事件监听代码就是---------------------------------------------------------


    //<内部类>---尽量少用----------------------------------------------------------------------------

    //</内部类>---尽量少用---------------------------------------------------------------------------

}
