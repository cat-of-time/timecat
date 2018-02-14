package com.time.cat.component.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.time.cat.R;
import com.time.cat.component.base.BaseActivity;
import com.time.cat.mvp.presenter.ActivityPresenter;

/**
 * @author dlink
 * @date 2018/2/14
 * @discription
 */
public class DialogActivity extends BaseActivity implements ActivityPresenter, View.OnClickListener {
    @SuppressWarnings("unused")
    private static final String TAG = "DialogActivity ";


    //<启动方法>-------------------------------------------------------------------------------------
    //<UI显示区>---操作UI，但不存在数据获取或处理代码，也不存在事件监听代码--------------------------------
    private Button dialog_add_task_footer_bt_submit;
    private EditText dialog_add_task_et_content;
    //</启动方法>------------------------------------------------------------------------------------

    /**
     * 启动这个Activity的Intent
     *
     * @param context 　上下文
     *
     * @return 返回intent实例
     */
    public static Intent createIntent(Context context) {
        return new Intent(context, DialogActivity.class);
    }

    @Override
    public Activity getActivity() {
        return this;
    }
    //</生命周期>------------------------------------------------------------------------------------

    //<生命周期>-------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);
        getWindow().setBackgroundDrawableResource(R.color.background_material_light_1);

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

    @Override
    public void initView() {//必须调用
        //点击空白处让Activity消失，可在Style中设置
//        setFinishOnTouchOutside(true);

        setWindow();

        dialog_add_task_et_content = findViewById(R.id.dialog_add_task_et_content);
        new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
//                InputMethodManager inputMethodManager=(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                inputMethodManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                InputMethodManager inputManager = (InputMethodManager) dialog_add_task_et_content.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (inputManager != null) {
                    inputManager.showSoftInput(dialog_add_task_et_content, 0);
                }
                return false;
            }
        }).sendEmptyMessageDelayed(0, 300);
        dialog_add_task_footer_bt_submit = findViewById(R.id.dialog_add_task_footer_bt_submit);
    }

    private void setWindow() {
        //窗口对齐屏幕宽度
        Window win = this.getWindow();
        win.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.BOTTOM;//设置对话框置顶显示
        win.setAttributes(lp);
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
        dialog_add_task_footer_bt_submit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_add_task_footer_bt_submit:
                Toast.makeText(this, dialog_add_task_et_content.getText().toString(), Toast.LENGTH_SHORT).show();
                finish();
                break;
        }
    }

    //-//<Listener>------------------------------------------------------------------------------
    //-//</Listener>-----------------------------------------------------------------------------

    //</Event事件区>---只要存在事件监听代码就是---------------------------------------------------------


    //<内部类>---尽量少用----------------------------------------------------------------------------

    //</内部类>---尽量少用---------------------------------------------------------------------------

}
