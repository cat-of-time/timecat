package com.time.cat.component.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.time.cat.R;
import com.time.cat.component.base.BaseActivity;
import com.time.cat.mvp.presenter.ActivityPresenter;
import com.time.cat.util.ViewUtil;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author dlink
 * @date 2018/2/14
 * @discription
 */
public class DialogActivity extends BaseActivity implements
                                                 ActivityPresenter,
                                                 View.OnClickListener,
                                                 SoftKeyBoardListener.OnSoftKeyBoardChangeListener {
    @SuppressWarnings("unused")
    private static final String TAG = "DialogActivity ";


    //<启动方法>-------------------------------------------------------------------------------------
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
    //</启动方法>------------------------------------------------------------------------------------


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
    //</生命周期>------------------------------------------------------------------------------------


    //<UI显示区>---操作UI，但不存在数据获取或处理代码，也不存在事件监听代码--------------------------------
    private Button dialog_add_task_footer_bt_submit;
    private EditText dialog_add_task_et_content;

    @Override
    public void initView() {//必须调用
        //点击空白处让Activity消失，可在Style中设置
//        setFinishOnTouchOutside(true);

        setWindow();

        dialog_add_task_et_content = findViewById(R.id.dialog_add_task_et_content);

        //获取焦点 光标出现
        dialog_add_task_et_content.setFocusable(true);
        dialog_add_task_et_content.setFocusableInTouchMode(true);
        dialog_add_task_et_content.requestFocus();

        // 这里给出个延迟弹出键盘，如果直接弹出键盘会和界面view渲染一起，体验不太好
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                ViewUtil.showInputMethod(dialog_add_task_et_content);
            }
        }, 256);


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
        SoftKeyBoardListener.setListener(DialogActivity.this, this);
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



    //-//<SoftKeyBoardListener.OnSoftKeyBoardChangeListener>------------------------------------------------------------------------------
    @Override
    public void keyBoardShow(int height) {
        Toast.makeText(DialogActivity.this, "键盘显示 高度" + height, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void keyBoardHide(int height) {
        Toast.makeText(DialogActivity.this, "键盘隐藏 高度" + height, Toast.LENGTH_SHORT).show();
    }
    //-//</SoftKeyBoardListener.OnSoftKeyBoardChangeListener>-----------------------------------------------------------------------------

    //</Event事件区>---只要存在事件监听代码就是---------------------------------------------------------


    //<内部类>---尽量少用----------------------------------------------------------------------------

    //</内部类>---尽量少用---------------------------------------------------------------------------

}
