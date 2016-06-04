package me.eycia.msghub_android

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.TargetApi
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_login.*
import me.eycia.api.API
import me.eycia.api.UserBaseInfo
import java.util.regex.Pattern

/**
 * A login screen that offers login via email/password.
 */
class LoginActivity : AppCompatActivity() {
    private var mLoginTask: API.User.LoginTask? = null
    private var mSignTask: API.User.SignTask? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        //setupActionBar();

        login_button.setOnClickListener { attemptLogin() }
        signin_button.setOnClickListener { attemptSignIn() }
        to_login_button.setOnClickListener { showSignIn(false) }
        to_signin_button.setOnClickListener { showSignIn(true) }
    }

    private fun attemptLogin() {
        if (mLoginTask != null) {
            return
        }

        // Reset errors.
        login_uname.error = null
        login_password.error = null

        // Store values at the time of the login attempt.
        val uname = login_uname.text.toString()
        val password = login_password.text.toString()

        var cancel = false
        var focusView: View? = null

        if (TextUtils.isEmpty(password) || !isPasswordValid(password)) {
            login_password.error = "密码长度不能小于6"
            focusView = login_password
            cancel = true
        }

        if (TextUtils.isEmpty(uname)) {
            login_uname.error = "不能为空"
            focusView = login_uname
            cancel = true
        }

        if (cancel) {
            focusView!!.requestFocus()
        } else {
            showProgress(true)
            mLoginTask = object : API.User.LoginTask(uname, password) {
                override fun onSuccess(userBaseInfo: UserBaseInfo) {
                    MyApplication.userBaseInfo = userBaseInfo
                    val intent = Intent()
                    intent.putExtra("userBaseInfo", userBaseInfo)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }

                override fun onError(e: Exception) {
                    showProgress(false)
                    login_password.error = e.message
                    login_password.requestFocus()
                }

                override fun onFinish() {
                    mLoginTask = null
                    showProgress(false)
                }
            }
            mLoginTask!!.execute()
        }
    }

    private fun attemptSignIn() {
        if (mSignTask != null) {
            return
        }
        signin_nickname.error = null
        signin_password.error = null
        signin_retry_password.error = null
        signin_email.error = null
        signin_username.error = null

        val username = signin_username.text.toString()
        val nickname = signin_nickname.text.toString()
        val email = signin_email.text.toString()
        val password = signin_password.text.toString()
        val pwd2 = signin_retry_password.text.toString()

        var cancel = false
        var focusView: View? = null

        if (password != pwd2) {
            signin_retry_password.error = "两次输入密码不同"
            focusView = signin_retry_password
            cancel = true
        }

        if (!isPasswordValid(password)) {
            signin_password.error = "密码长度在6~24之间"
            focusView = signin_password
            cancel = true
        }

        if (!isEmailValid(email)) {
            signin_email.error = "邮箱格式错误"
            focusView = signin_email
            cancel = true
        }

        if (TextUtils.isEmpty(nickname)) {
            signin_nickname.error = "昵称不能为空"
            focusView = signin_nickname
            cancel = true
        }

        if (!isUsernameValid(username)) {
            signin_username.error = "用户名长度在5-16之间,由数字字母组成,且第一位为字母"
            focusView = signin_username
            cancel = true
        }

        if (cancel) {
            focusView!!.requestFocus()
        } else {
            showProgress(true)
            mSignTask = object : API.User.SignTask(email, nickname, password, username) {
                override fun onSuccess(s: String) {
                    Log.d("msghub", "signed id:" + s)
                    showSignIn(false)
                }

                override fun onError(e: Exception) {
                    showProgress(false)
                    signin_password.error = e.message
                    signin_password.requestFocus()
                }

                override fun onFinish() {
                    mSignTask = null
                    showProgress(false)
                }
            }
            mSignTask!!.execute()
        }
    }

    private fun isEmailValid(email: String): Boolean {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private val UsernamePatterns = Pattern.compile("^[a-zA-Z][a-zA-Z0-9_]{4,15}$")

    private fun isUsernameValid(username: String): Boolean {
        return !TextUtils.isEmpty(username) && UsernamePatterns.matcher(username).matches()
    }

    private fun isPasswordValid(password: String): Boolean {
        return !TextUtils.isEmpty(password) && password.length >= 6 && password.length <= 24
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private fun showProgress(show: Boolean) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            val shortAnimTime = resources.getInteger(android.R.integer.config_shortAnimTime)

            login_or_sign_form.visibility = if (show) View.GONE else View.VISIBLE
            login_or_sign_form.animate().setDuration(shortAnimTime.toLong()).alpha(
                    (if (show) 0 else 1).toFloat()).setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    login_or_sign_form.visibility = if (show) View.GONE else View.VISIBLE
                }
            })

            login_progress.visibility = if (show) View.VISIBLE else View.GONE
            login_progress.animate().setDuration(shortAnimTime.toLong()).alpha(
                    (if (show) 1 else 0).toFloat()).setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    login_progress.visibility = if (show) View.VISIBLE else View.GONE
                }
            })
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            login_progress.visibility = if (show) View.VISIBLE else View.GONE
            login_or_sign_form.visibility = if (show) View.GONE else View.VISIBLE
        }
    }

    private fun showSignIn(isSignIn: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            val shortAnimTime = resources.getInteger(android.R.integer.config_shortAnimTime)

            login_form.visibility = if (isSignIn) View.GONE else View.VISIBLE
            login_form.animate().setDuration(shortAnimTime.toLong()).alpha(
                    (if (isSignIn) 0 else 1).toFloat()).setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    login_form.visibility = if (isSignIn) View.GONE else View.VISIBLE
                }
            })

            signin_form.visibility = if (isSignIn) View.VISIBLE else View.GONE
            signin_form.animate().setDuration(shortAnimTime.toLong()).alpha(
                    (if (isSignIn) 1 else 0).toFloat()).setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    signin_form.visibility = if (isSignIn) View.VISIBLE else View.GONE
                }
            })
        } else {
            login_form.visibility = if (isSignIn) View.GONE else View.VISIBLE
            signin_form.visibility = if (isSignIn) View.VISIBLE else View.GONE
        }
    }
}

