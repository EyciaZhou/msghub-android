package me.eycia.msghub_android;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import me.eycia.api.UserBaseInfo;

/**
 * Created by eycia on 16/5/22.
 */
public class LeftMenuLayoutController {
    public static final int TO_LOGIN = 1;

    public LinearLayout view;
    private Activity mActivity;
    private SimpleDraweeView mBackgound;
    private FrameLogin mFrameLogin;
    private FrameLogined mFrameLogined;

    private boolean Logined;

    private class FrameLogin {
        public LinearLayout view;
        private Activity mActivity;
        private Button mLoginOrSign;

        View.OnClickListener onLoginOrSignClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(mActivity.getApplicationContext(), LoginActivity.class);
                mActivity.startActivityForResult(it, TO_LOGIN);
            }
        };

        public FrameLogin(Activity activity) {
            mActivity = activity;
            view = (LinearLayout) activity.findViewById(R.id.menu_up_login_frame);
            mLoginOrSign = (Button) activity.findViewById(R.id.button_login_or_sign);
            mLoginOrSign.setOnClickListener(onLoginOrSignClick);
        }
    }

    private class FrameLogined {
        public LinearLayout view;
        private Activity mActivity;
        private SimpleDraweeView mHeadView;
        private TextView mUsername;

        public void showUserInfo(String Username, String HeadUrl) {
            mHeadView.setImageURI(Uri.parse(HeadUrl));
            mUsername.setText(Username);
        }

        public FrameLogined(Activity activity) {
            mActivity = activity;
            view = (LinearLayout) activity.findViewById(R.id.menu_up_logined_frame);
            mHeadView = (SimpleDraweeView) activity.findViewById(R.id.menu_user_head);
            mUsername = (TextView) activity.findViewById(R.id.menu_username);
        }

        public void showUserInfo(UserBaseInfo userBaseInfo) {
            mUsername.setText(userBaseInfo.Nickname);
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
        mFrameLogin = new FrameLogin(activity);
        mFrameLogined = new FrameLogined(activity);

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
        }
    }
}

