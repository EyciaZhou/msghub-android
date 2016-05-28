package me.eycia.msghub_android;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;

import me.eycia.api.API;
import me.eycia.api.UserBaseInfo;

/**
 * Created by eycia on 16/5/22.
 */
public class LeftMenuLayoutController {
    public static final int TO_LOGIN = 1;
    public static final int TO_SELECT_AVATAR = 2;

    public LinearLayout view;
    private Activity mActivity;
    private SimpleDraweeView mBackgound;
    private FrameLogin mFrameLogin;
    private FrameLogined mFrameLogined;

    private boolean Logined;

    private class FrameLogin {
        public LinearLayout view;
        //private Activity mActivity;
        private Button mLoginOrSign;

        View.OnClickListener onLoginOrSignClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(mActivity.getApplicationContext(), LoginActivity.class);
                mActivity.startActivityForResult(it, TO_LOGIN);
            }
        };

        public FrameLogin() {
            view = (LinearLayout) mActivity.findViewById(R.id.menu_up_login_frame);
            mLoginOrSign = (Button) mActivity.findViewById(R.id.button_login_or_sign);
            mLoginOrSign.setOnClickListener(onLoginOrSignClick);
        }
    }

    private class FrameLogined {
        public LinearLayout view;
        private SimpleDraweeView mHeadView;
        private TextView mUsername;

        public void startUpload(final Uri uriPicture) {
            new API.User.ChangeAvatarTask(uriPicture) {
                @Override
                protected void onSuccess(@NonNull String url) throws Exception {
                    Fresco.getImagePipeline().evictFromCache(Uri.parse(url));
                    mHeadView.setImageURI(Uri.parse(url));
                }
            }.execute();
        }

        public void startPickImage() {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            mActivity.startActivityForResult(intent, TO_SELECT_AVATAR);
        }

        public FrameLogined() {
            view = (LinearLayout) mActivity.findViewById(R.id.menu_up_logined_frame);
            mHeadView = (SimpleDraweeView) mActivity.findViewById(R.id.menu_user_head);
            mUsername = (TextView) mActivity.findViewById(R.id.menu_username);

            mHeadView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    startPickImage();
                    return true;
                }
            });
        }

        public void showUserInfo(UserBaseInfo userBaseInfo) {
            mUsername.setText(userBaseInfo.Nickname);
            mHeadView.setImageURI(Uri.parse(userBaseInfo.HeadUrl));
        }
    }

    public void setUserInfo(UserBaseInfo userBaseInfo) {
        if (userBaseInfo == null) {
            return;
        }
        ToggleLogined(true);
        mFrameLogined.showUserInfo(userBaseInfo);
    }

    private void ToggleLogined(boolean isLogined) {
        if (isLogined == Logined) {
            return;
        }
        Logined = isLogined;
        if (isLogined) {
            mFrameLogin.view.setVisibility(View.GONE);
            mFrameLogined.view.setVisibility(View.VISIBLE);
        } else {
            mFrameLogin.view.setVisibility(View.VISIBLE);
            mFrameLogined.view.setVisibility(View.GONE);
        }
    }

    public void SetBackground(String url) {
        if (mBackgound != null) {
            mBackgound.setImageURI(Uri.parse(url));
        }
    }

    public LeftMenuLayoutController(Activity activity) {
        this.mActivity = activity;
        view = (LinearLayout) activity.findViewById(R.id.left_menu);
        mBackgound = (SimpleDraweeView) activity.findViewById(R.id.menu_background);
        mFrameLogin = new FrameLogin();
        mFrameLogined = new FrameLogined();

        UserBaseInfo userBaseInfo = ((MyApplication) activity.getApplication()).getUserBaseInfo();
        if (userBaseInfo != null) {
            setUserInfo(userBaseInfo);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TO_LOGIN:
                if (resultCode == Activity.RESULT_OK) {
                    UserBaseInfo userBaseInfo = data.getParcelableExtra("userBaseInfo");
                    if (userBaseInfo != null) {
                        setUserInfo(userBaseInfo);
                    }
                }
                break;
            case TO_SELECT_AVATAR:
                if (resultCode == Activity.RESULT_OK) {
                    if (data == null) {
                        MyApplication.showToast("选择图片失败");
                        return;
                    }
                    mFrameLogined.startUpload(data.getData());
                }
                break;
        }
    }
}

