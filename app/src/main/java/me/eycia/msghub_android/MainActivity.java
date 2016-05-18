package me.eycia.msghub_android;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Window;
import android.view.WindowManager;

import com.facebook.drawee.backends.pipeline.Fresco;

import me.eycia.Notifier;
import me.eycia.view.SlidingTabLayout;

public class MainActivity extends AppCompatActivity {
    private MainActivityChannelsAdapter mMainActivityChannelsAdapter;

    private ViewPager mViewPager;
    private SlidingTabLayout mSlidingTabLayout;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);

        setContentView(R.layout.activity_msgs_display);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.

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


