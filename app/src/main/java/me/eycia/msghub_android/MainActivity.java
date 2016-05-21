package me.eycia.msghub_android;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;

import com.facebook.drawee.backends.pipeline.Fresco;

import me.eycia.Notifier;
import me.eycia.view.SlidingTabLayout;

public class MainActivity extends AppCompatActivity {
    private MainActivityChannelsAdapter mMainActivityChannelsAdapter;

    private ViewPager mViewPager;
    private SlidingTabLayout mSlidingTabLayout;
    private AppBarLayout mAppBarLayout;
    private ImageView mTitleImage;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);

        setContentView(R.layout.activity_msgs_display);

        mTitleImage = (ImageView) findViewById(R.id.title_imgview);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        mAppBarLayout = (AppBarLayout) findViewById(R.id.appbar2);
        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            private boolean mIsTheTitleVisible;

            public void startAlphaAnimation (View v, long duration, int visibility) {
                AlphaAnimation alphaAnimation = (visibility == View.VISIBLE)
                        ? new AlphaAnimation(0f, 1f)
                        : new AlphaAnimation(1f, 0f);

                

                alphaAnimation.setDuration(duration);
                alphaAnimation.setFillAfter(true);
                v.startAnimation(alphaAnimation);
            }

            private void handleToolbarTitleVisibility(float percentage) {
                if (percentage <= 0.3) {

                    if(!mIsTheTitleVisible) {
                        startAlphaAnimation(mTitleImage, 200, View.VISIBLE);
                        mIsTheTitleVisible = true;
                    }

                } else {

                    if (mIsTheTitleVisible) {
                        startAlphaAnimation(mTitleImage, 200, View.INVISIBLE);
                        mIsTheTitleVisible = false;
                    }
                }
            }

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                int maxScroll = appBarLayout.getTotalScrollRange();
                float percentage = (float) Math.abs(verticalOffset) / (float) maxScroll;
                handleToolbarTitleVisibility(percentage);
            }
        });

        mMainActivityChannelsAdapter = new MainActivityChannelsAdapter(getSupportFragmentManager(), savedInstanceState);
        mMainActivityChannelsAdapter.ChansNotifier.addOnDataChangeListener(new Notifier.OnDataChangeListener() {
            @Override
            public void OnDataChange() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mSlidingTabLayout.setViewPager(mViewPager);
                    }
                });
            }
        });

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mMainActivityChannelsAdapter);

        mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.tabs);
        mSlidingTabLayout.setViewPager(mViewPager);
        mSlidingTabLayout.setSelectedIndicatorColors(getResources().getColor(R.color.colorPrimaryDark));

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(getResources().getColor(R.color.colorPrimary));
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        mMainActivityChannelsAdapter.onSaveInstanceState(savedInstanceState);
    }
}


