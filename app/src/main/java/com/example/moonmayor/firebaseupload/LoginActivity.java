package com.example.moonmayor.firebaseupload;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

/**
 * Created by moonmayor on 10/17/17.
 */

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.usernameInput) EditText mUsernameInput;
    @BindView(R.id.loginButton) Button mLoginButton;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        mUsernameInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                // detect if the user presses [enter]
                if (keyEvent.getKeyCode() == android.view.KeyEvent.KEYCODE_ENTER) {
                    saveUsername();
                    return true;
                }
                return false;
            }
        });
    }

    @OnClick(R.id.loginButton)
    public void saveUsername() {
        String username = mUsernameInput.getText().toString();
        MySharedPreferences.storeUsername(this, username);

        launchMainActivity();
    }

    private void launchMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
