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
import com.time.cat.mvp.model.Account;
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
 * @discription 注册
 */
public class SignupActivity extends BaseActivity implements ActivityPresenter, View.OnClickListener {
    private static final String TAG = "SignupActivity";
    //<UI显示区>---操作UI，但不存在数据获取或处理代码，也不存在事件监听代码-----------------------------------
    EditText emailText;
    //</生命周期>------------------------------------------------------------------------------------
    EditText passwordText;
    EditText reEnterPasswordText;
    Button signupButton;
    TextView loginLink;

    //<生命周期>------------------------------------------------------------------------------------
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);


        //<功能归类分区方法，必须调用>-----------------------------------------------------------------
        initView();
        initData();
        initEvent();
        //</功能归类分区方法，必须调用>----------------------------------------------------------------
    }

    @Override
    public void initView() {
        emailText = findViewById(R.id.input_email);
        passwordText = findViewById(R.id.input_password);
        reEnterPasswordText = findViewById(R.id.input_reEnterPassword);
        signupButton = findViewById(R.id.btn_signup);
        loginLink = findViewById(R.id.link_login);
    }
    //</UI显示区>---操作UI，但不存在数据获取或处理代码，也不存在事件监听代码-----------------------------------


    //<Data数据区>---存在数据获取或处理代码，但不存在事件监听代码--------------------------------------------
    @Override
    public void initData() {

    }
    //</Data数据区>---存在数据获取或处理代码，但不存在事件监听代码--------------------------------------------


    //<Event事件区>---只要存在事件监听代码就是-----------------------------------------------------------
    @Override
    public void initEvent() {
        signupButton.setOnClickListener(this);
        loginLink.setOnClickListener(this);
    }

    //-//<View.OnClickListener>---------------------------------------------------------------------
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_signup:
                signup();
                break;
            case R.id.link_login:
                // Finish the registration screen and return to the Login activity
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                finish();
                break;
        }
    }

    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        signupButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();
        String reEnterPassword = reEnterPasswordText.getText().toString();

        User u = new User();
        u.setAccount(new Account());
        u.setEmail(email);
        u.setUsername(email);
        u.setIs_staff(false);
        u.setPassword(password);
        Log.e(TAG, u.toString());

        final boolean[] isSuccess = {false};
        // TODO: Implement your own signup logic here.
        RetrofitHelper.getLoginService().createUser(u) //获取Observable对象
                .compose(SignupActivity.this.bindToLifecycle()).subscribeOn(Schedulers.newThread())//请求在新的线程中执行
                .observeOn(Schedulers.io())         //请求完成后在io线程中执行
                .doOnNext(new Action1<User>() {
                    @Override
                    public void call(User user) {
//                        saveUser(user);//保存用户信息到本地
                        DB.users().saveAndFireEvent(ModelUtil.toDBUser(user));
                        Log.e(TAG, "保存用户信息到本地" + user.toString());
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
                        onSignupFailed();
                        progressDialog.dismiss();

                    }

                    @Override
                    public void onNext(User user) {
                        //请求成功
                        intent = new Intent(SignupActivity.this, MainActivity.class);
                        intent.putExtra(LoginActivity.INTENT_USER_EMAIL, user.getEmail());
                        setResult(RESULT_OK, intent);
                        onSignupSuccess();
                        progressDialog.dismiss();
                        Log.e(TAG, "请求成功" + user.toString());
                    }
                });

//        new android.os.Handler().postDelayed(
//                new Runnable() {
//                    public void run() {
//                        // On complete call either onSignupSuccess or onSignupFailed
//                        // depending on success
//                        if (isSuccess[0]) {
//                            onSignupSuccess();
//                        } else {
//                            onSignupFailed();
//                        }
//                        // onSignupFailed();
//                        progressDialog.dismiss();
//                    }
//                }, 3000);
    }

    public void onSignupSuccess() {
        signupButton.setEnabled(false);
        ToastUtil.show("创建用户成功！");
        finish();
    }

    public void onSignupFailed() {
        ToastUtil.show("创建用户失败！");
        signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

//        String name = nameText.getText().toString();
//        String address = addressText.getText().toString();
        String email = emailText.getText().toString();
//        String mobile = mobileText.getText().toString();
        String password = passwordText.getText().toString();
        String reEnterPassword = reEnterPasswordText.getText().toString();

//        if (name.isEmpty() || name.length() < 3) {
//            nameText.setError("at least 3 characters");
//            valid = false;
//        } else {
//            nameText.setError(null);
//        }
//
//        if (address.isEmpty()) {
//            addressText.setError("Enter Valid Address");
//            valid = false;
//        } else {
//            addressText.setError(null);
//        }


        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailText.setError("enter a valid email address");
            valid = false;
        } else {
            emailText.setError(null);
        }

//        if (mobile.isEmpty() || mobile.length()!=10) {
//            mobileText.setError("Enter Valid Mobile Number");
//            valid = false;
//        } else {
//            mobileText.setError(null);
//        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            passwordText.setError(null);
        }

        if (reEnterPassword.isEmpty() || reEnterPassword.length() < 4 || reEnterPassword.length() > 10 || !(reEnterPassword.equals(password))) {
            reEnterPasswordText.setError("Password Do not match");
            valid = false;
        } else {
            reEnterPasswordText.setError(null);
        }

        return valid;
    }
    //-//</View.OnClickListener>---------------------------------------------------------------------

    //</Event事件区>---只要存在事件监听代码就是-----------------------------------------------------------

}