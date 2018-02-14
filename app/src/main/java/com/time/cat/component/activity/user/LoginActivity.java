package com.time.cat.component.activity.user;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.time.cat.NetworkSystem.RetrofitHelper;
import com.time.cat.R;
import com.time.cat.component.activity.main.MainActivity;
import com.time.cat.component.base.BaseActivity;
import com.time.cat.database.DB;
import com.time.cat.mvp.model.APImodel.User;
import com.time.cat.mvp.presenter.ActivityPresenter;
import com.time.cat.util.ModelUtil;
import com.time.cat.util.ToastUtil;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * @author dlink
 * @date 2018/2/2
 * @discription 登录
 */
public class LoginActivity extends BaseActivity implements ActivityPresenter, View.OnClickListener {
    public static final String INTENT_USER_EMAIL = "intent_user_email";
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;
    //<UI显示区>---操作UI，但不存在数据获取或处理代码，也不存在事件监听代码-----------------------------------
    EditText emailText;
    EditText passwordText;
    Button loginButton;
    //</生命周期>------------------------------------------------------------------------------------
    TextView signupLink;

    //<生命周期>------------------------------------------------------------------------------------
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //<功能归类分区方法，必须调用>-----------------------------------------------------------------
        initView();
        initData();
        initEvent();
        //</功能归类分区方法，必须调用>----------------------------------------------------------------
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {
                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                if (data != null) {
                    intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra(INTENT_USER_EMAIL, data.getStringExtra(INTENT_USER_EMAIL));
                    setResult(RESULT_OK, intent);
                    onLoginSuccess();
                } else {
                    Log.e(TAG, "onActivityResult --> data == null");
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }

    @Override
    public void initView() {
        emailText = findViewById(R.id.input_email);
        passwordText = findViewById(R.id.input_password);
        loginButton = findViewById(R.id.btn_login);
        signupLink = findViewById(R.id.link_signup);
    }
    //</UI显示区>---操作UI，但不存在数据获取或处理代码，也不存在事件监听代码-----------------------------------


    //<Data数据区>---存在数据获取或处理代码，但不存在事件监听代码--------------------------------------------
    @Override
    public void initData() {
        intent = new Intent(LoginActivity.this, MainActivity.class);
    }
    //</Data数据区>---存在数据获取或处理代码，但不存在事件监听代码-------------------------------------------


    //<Event事件区>---只要存在事件监听代码就是-----------------------------------------------------------
    @Override
    public void initEvent() {
        loginButton.setOnClickListener(this);
        signupLink.setOnClickListener(this);
    }

    //-//<View.OnClickListener>---------------------------------------------------------------------
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                login();
                break;
            case R.id.link_signup:
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
        }
    }

    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();

        // TODO: Implement your own authentication logic here.
        final boolean[] isSuccess = {false};
        RetrofitHelper.getLoginService().login(email, password, false) //获取Observable对象
                .compose(LoginActivity.this.bindToLifecycle()).subscribeOn(Schedulers.newThread())//请求在新的线程中执行
                .observeOn(Schedulers.io())         //请求完成后在io线程中执行
                .doOnNext(new Action1<User>() {
                    @Override
                    public void call(User user) {
                        //保存用户信息到本地
                        DB.users().saveAndFireEvent(ModelUtil.toDBUser(user));
                        Log.i(TAG, user.toString());
                    }
                }).observeOn(AndroidSchedulers.mainThread())//最后在主线程中执行
                .subscribe(new Subscriber<User>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        //请求失败
                        Log.e(TAG, e.toString());
                        onLoginFailed();
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onNext(User user) {
                        //请求成功
                        isSuccess[0] = true;
                        intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra(INTENT_USER_EMAIL, user.getEmail());
                        setResult(RESULT_OK, intent);
                        onLoginSuccess();
                        progressDialog.dismiss();
                        Log.i(TAG, "请求成功" + user.toString());
                    }
                });
//        new android.os.Handler().postDelayed(
//                new Runnable() {
//                    public void run() {
//                        // On complete call either onLoginSuccess or onLoginFailed
//                        if (isSuccess[0]) {
//                            onLoginSuccess();
//                        } else {
//                            onLoginFailed();
//                        }
//                        // onLoginFailed();
//                        progressDialog.dismiss();
//                    }
//                }, 3000);
    }

    public void onLoginSuccess() {
        loginButton.setEnabled(false);
//        Log.e(TAG, intent.toString());
//        Log.e(TAG, intent.getStringExtra(INTENT_USER_EMAIL));

        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        finish();
    }

    public void onLoginFailed() {
        ToastUtil.show("登录失败！");
        loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailText.setError("enter a valid email address");
            valid = false;
        } else {
            emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            passwordText.setError(null);
        }

        return valid;
    }
    //-//</View.OnClickListener>---------------------------------------------------------------------


    //<Event事件区>---只要存在事件监听代码就是-----------------------------------------------------------

}
