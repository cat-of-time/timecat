package com.time.cat.test;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.time.cat.R;
import com.time.cat.ThemeSystem.manager.ThemeManager;
import com.time.cat.component.base.BaseActivity;
import com.time.cat.mvp.presenter.ActivityPresenter;
import com.time.cat.util.ToastUtil;

/**
 * @author dlink
 * @date 2018/2/2
 * @discription TestActivity
 */
public class TestActivity extends BaseActivity implements ActivityPresenter, View.OnClickListener {
    private static final String TAG = "MainActivity";
    //-//<Activity>---------------------------------------------------------------------------------
    private static final int REQUEST_LOGIN = 0;

    //<生命周期>-------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setStatusBarFullTransparent();

        //<功能归类分区方法，必须调用>-----------------------------------------------------------------
        initView();
        initData();
        initEvent();
        //</功能归类分区方法，必须调用>----------------------------------------------------------------
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
    //</生命周期>------------------------------------------------------------------------------------


    //<UI显示区>---操作UI，但不存在数据获取或处理代码，也不存在事件监听代码-----------------------------------

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    //</UI显示区>---操作UI，但不存在数据获取或处理代码，也不存在事件监听代码)>--------------------------------

    @Override
    public void initView() {//必须调用
        switch (ThemeManager.getTheme(this)) {
            case ThemeManager.CARD_WHITE:
            case ThemeManager.CARD_THUNDER:
            case ThemeManager.CARD_MAGENTA:
                setStatusBarFontIconDark(true);
                break;
            default:
                setStatusBarFontIconDark(false);
        }
    }
    //</Data数据区>---存在数据获取或处理代码，但不存在事件监听代码-------------------------------------------

    //<Data数据区>---存在数据获取或处理代码，但不存在事件监听代码--------------------------------------------
    @Override
    public void initData() {//必须调用

    }

    //<Event事件区>---只要存在事件监听代码就是-----------------------------------------------------------
    @Override
    public void initEvent() {//必须调用
        subscribeToEvents();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_LOGIN) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // 设置用户登录后的界面
                ToastUtil.show("登录成功！");
            }
        }
    }
    //-//</Activity>--------------------------------------------------------------------------------


    //-//<View.OnClickListener>---------------------------------------------------------------------
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
        }
    }
    //-//</View.OnClickListener>--------------------------------------------------------------------


    //</Event事件区>---只要存在事件监听代码就是----------------------------------------------------------


    //<回调接口>-------------------------------------------------------------------------------------
    //</回调接口>------------------------------------------------------------------------------------


    //<内部类>---尽量少用-----------------------------------------------------------------------------
    //</内部类>---尽量少用----------------------------------------------------------------------------

}
