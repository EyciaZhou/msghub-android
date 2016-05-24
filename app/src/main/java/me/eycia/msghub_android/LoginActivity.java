package me.eycia.msghub_android;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import java.util.regex.Pattern;

import me.eycia.api.API;
import me.eycia.api.UserBaseInfo;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {
    private API.User.LoginTask mLoginTask = null;
    private API.User.SignTask mSignTask = null;

    // UI references.
    private EditText mLoginUname;
    private EditText mLoginPassword;

    private EditText mSignUsername;
    private EditText mSignEmail;
    private EditText mSignPassword;
    private EditText mSignRetryPassword;
    private EditText mSignNickname;

    private View mProgressView;

    private View mLoginFormView;
    private View mSignInFormView;
    private View mLoginOrSignInView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //setupActionBar();
        // Set up the login form.
        mLoginUname = (EditText) findViewById(R.id.login_uname);
        mLoginPassword = (EditText) findViewById(R.id.login_password);

        mSignUsername = (EditText) findViewById(R.id.signin_username);
        mSignEmail = (EditText) findViewById(R.id.signin_email);
        mSignPassword = (EditText) findViewById(R.id.signin_password);
        mSignRetryPassword = (EditText) findViewById(R.id.signin_retry_password);
        mSignNickname = (EditText) findViewById(R.id.signin_nickname);

        mLoginFormView = findViewById(R.id.login_form);
        mSignInFormView = findViewById(R.id.signin_form);
        mLoginOrSignInView = findViewById(R.id.login_or_sign_form);
        mProgressView = findViewById(R.id.login_progress);

        Button mLoginButton = (Button) findViewById(R.id.login_button);
        mLoginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        Button mSignInButton = (Button) findViewById(R.id.signin_button);
        mSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptSignIn();
            }
        });

        findViewById(R.id.to_login_button).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showSignIn(false);
            }
        });

        findViewById(R.id.to_signin_button).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showSignIn(true);
            }
        });
    }

    private void attemptLogin() {
        if (mLoginTask != null) {
            return;
        }

        // Reset errors.
        mLoginUname.setError(null);
        mLoginPassword.setError(null);

        // Store values at the time of the login attempt.
        String uname = mLoginUname.getText().toString();
        String password = mLoginPassword.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(password) || !isPasswordValid(password)) {
            mLoginPassword.setError("密码长度不能小于6");
            focusView = mLoginPassword;
            cancel = true;
        }

        if (TextUtils.isEmpty(uname)) {
            mLoginUname.setError("不能为空");
            focusView = mLoginUname;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);
            mLoginTask = new API.User.LoginTask(uname, password) {
                @Override
                protected void onSuccess(@NonNull UserBaseInfo userBaseInfo) {
                    ((MyApplication) (getApplication())).setUserBaseInfo(userBaseInfo);
                    Intent intent = new Intent();
                    intent.putExtra("userBaseInfo", userBaseInfo);
                    setResult(RESULT_OK, intent);
                    finish();
                }

                @Override
                protected void onError(@NonNull Exception e) {
                    showProgress(false);
                    mLoginPassword.setError(e.getMessage());
                    mLoginPassword.requestFocus();
                }

                @Override
                protected void onFinish() {
                    mLoginTask = null;
                    showProgress(false);
                }
            };
            mLoginTask.execute();
        }
    }

    private void attemptSignIn() {
        if (mSignTask != null) {
            return;
        }
        mSignNickname.setError(null);
        mSignPassword.setError(null);
        mSignRetryPassword.setError(null);
        mSignEmail.setError(null);
        mSignUsername.setError(null);

        String username = mSignUsername.getText().toString();
        String nickname = mSignNickname.getText().toString();
        String email = mSignEmail.getText().toString();
        String password = mSignPassword.getText().toString();
        String pwd2 = mSignRetryPassword.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (!password.equals(pwd2)) {
            mSignRetryPassword.setError("两次输入密码不同");
            focusView = mSignRetryPassword;
            cancel = true;
        }

        if (!isPasswordValid(password)) {
            mSignPassword.setError("密码长度在6~24之间");
            focusView = mSignPassword;
            cancel = true;
        }

        if (!isEmailValid(email)) {
            mSignEmail.setError("邮箱格式错误");
            focusView = mSignEmail;
            cancel = true;
        }

        if (TextUtils.isEmpty(nickname)) {
            mSignNickname.setError("昵称不能为空");
            focusView = mSignNickname;
            cancel = true;
        }

        if (!isUsernameValid(username)) {
            mSignUsername.setError("用户名长度在5-16之间,由数字字母组成,且第一位为字母");
            focusView = mSignUsername;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);
            mSignTask = new API.User.SignTask(email, nickname, password, username) {
                @Override
                protected void onSuccess(@NonNull String s) {
                    Log.d("msghub", "signed id:" + s);
                    showSignIn(false);
                }

                @Override
                protected void onError(@NonNull Exception e) {
                    showProgress(false);
                    mSignPassword.setError(e.getMessage());
                    mSignPassword.requestFocus();
                }

                @Override
                protected void onFinish() {
                    mSignTask = null;
                    showProgress(false);
                }
            };
            mSignTask.execute();
        }
    }

    private boolean isEmailValid(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private Pattern UsernamePatterns = Pattern.compile("^[a-zA-Z][a-zA-Z0-9_]{4,15}$");

    private boolean isUsernameValid(String username) {
        return !TextUtils.isEmpty(username) && UsernamePatterns.matcher(username).matches();
    }

    private boolean isPasswordValid(String password) {
        return !TextUtils.isEmpty(password) && password.length() >= 6 && password.length() <= 24;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginOrSignInView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginOrSignInView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginOrSignInView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginOrSignInView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private void showSignIn(final boolean isSignIn) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(isSignIn ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    isSignIn ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(isSignIn ? View.GONE : View.VISIBLE);
                }
            });

            mSignInFormView.setVisibility(isSignIn ? View.VISIBLE : View.GONE);
            mSignInFormView.animate().setDuration(shortAnimTime).alpha(
                    isSignIn ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mSignInFormView.setVisibility(isSignIn ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            mLoginFormView.setVisibility(isSignIn ? View.GONE : View.VISIBLE);
            mSignInFormView.setVisibility(isSignIn ? View.VISIBLE : View.GONE);
        }
    }
}

