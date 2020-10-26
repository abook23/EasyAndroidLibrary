package com.android.easy.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.android.easy.R;
import com.android.easy.URL;
import com.android.easy.app.HttpCall;
import com.android.easy.app.base.BaseAppCompatActivity;
import com.android.easy.data.ResponseBean;
import com.android.easy.data.Result;
import com.android.easy.data.model.UserInfo;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends BaseAppCompatActivity {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setStatusBarTranslucentStatus();

        setAppBarTitle("标题");

        final EditText usernameEditText = findViewById(R.id.username);
        final EditText passwordEditText = findViewById(R.id.password);
        final Button loginButton = findViewById(R.id.login);
        final ProgressBar loadingProgressBar = findViewById(R.id.loading);


        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!usernameEditText.getText().toString().isEmpty() || !passwordEditText.getText().toString().isEmpty()){
                    loginButton.setEnabled(true);
                }else {
                    loginButton.setEnabled(false);
                }
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    toLogin(usernameEditText.getText().toString(), passwordEditText.getText().toString(),loadingProgressBar);
                }
                return false;
            }
        });
        loginButton.setEnabled(true);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingProgressBar.setVisibility(View.VISIBLE);
                toLogin(usernameEditText.getText().toString(), passwordEditText.getText().toString(),loadingProgressBar);
            }
        });
    }

    private void toLogin(String userName, String password,ProgressBar progressBar) {
        Map<String, Object> params = new HashMap<>();
        params.put("userName", userName);
        params.put("password", password);
        post(URL.login, params, new HttpCall<ResponseBean<UserInfo>>() {
            @Override
            public void onSuccess(@NonNull ResponseBean<UserInfo> stringResult) {
                progressBar.setVisibility(View.GONE);
                finish();
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}
