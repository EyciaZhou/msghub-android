package me.eycia.msghub_android

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import com.facebook.drawee.backends.pipeline.Fresco
import kotlinx.android.synthetic.main.left_menu.*
import me.eycia.api.API
import me.eycia.api.UserBaseInfo

class LeftMenuLayoutController(private val mActivity: Activity) {
    //var view: LinearLayout
    private val mFrameLogin: FrameLogin
    private val mFrameLogined: FrameLogined

    private var Logined: Boolean = false

    private inner class FrameLogin {
        var view: LinearLayout
        private val mLoginOrSign: Button

        internal var onLoginOrSignClick: View.OnClickListener = View.OnClickListener {
            val it = Intent(mActivity.applicationContext, LoginActivity::class.java)
            mActivity.startActivityForResult(it, TO_LOGIN)
        }

        init {
            view = mActivity.menu_up_login_frame
            mLoginOrSign = mActivity.findViewById(R.id.button_login_or_sign) as Button
            mLoginOrSign.setOnClickListener(onLoginOrSignClick)
        }
    }

    private inner class FrameLogined {
        var view: LinearLayout

        fun startUpload(uriPicture: Uri) {
            object : API.User.ChangeAvatarTask(uriPicture) {
                @Throws(Exception::class)
                override fun onSuccess(result: String) {
                    Fresco.getImagePipeline().evictFromCache(Uri.parse(result))
                    mActivity.mHeadView.setImageURI(Uri.parse(result))
                }
            }.execute()
        }

        fun startPickImage() {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            mActivity.startActivityForResult(intent, TO_SELECT_AVATAR)
        }

        init {
            view = mActivity.menu_up_logined_frame
            mActivity.mHeadView.setOnLongClickListener {
                startPickImage()
                true
            }
        }

        fun showUserInfo(userBaseInfo: UserBaseInfo) {
            mActivity.mUsername.text = userBaseInfo.Nickname
            mActivity.mHeadView.setImageURI(Uri.parse(userBaseInfo.HeadUrl))
        }
    }

    fun setUserInfo(userBaseInfo: UserBaseInfo?) {
        if (userBaseInfo == null) {
            return
        }
        ToggleLogined(true)
        mFrameLogined.showUserInfo(userBaseInfo)
    }

    private fun ToggleLogined(isLogined: Boolean) {
        if (isLogined == Logined) {
            return
        }
        Logined = isLogined
        if (isLogined) {
            mFrameLogin.view.visibility = View.GONE
            mFrameLogined.view.visibility = View.VISIBLE
        } else {
            mFrameLogin.view.visibility = View.VISIBLE
            mFrameLogined.view.visibility = View.GONE
        }
    }

    fun SetBackground(url: String) {
        mActivity.mBackgound.setImageURI(Uri.parse(url))
    }

    init {
        //view = mActivity.findViewById(R.id.left_menu) as LinearLayout
        mFrameLogin = FrameLogin()
        mFrameLogined = FrameLogined()

        val userBaseInfo = MyApplication.userBaseInfo
        if (userBaseInfo != null) {
            setUserInfo(userBaseInfo)
        }
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            TO_LOGIN -> if (resultCode == Activity.RESULT_OK) {
                val userBaseInfo = data!!.getParcelableExtra<UserBaseInfo>("userBaseInfo")
                if (userBaseInfo != null) {
                    setUserInfo(userBaseInfo)
                }
            }
            TO_SELECT_AVATAR -> if (resultCode == Activity.RESULT_OK) {
                if (data == null) {
                    MyApplication.toast("选择图片失败")
                    return
                }
                mFrameLogined.startUpload(data.data)
            }
        }
    }

    companion object {
        val TO_LOGIN = 1
        val TO_SELECT_AVATAR = 2
    }
}

