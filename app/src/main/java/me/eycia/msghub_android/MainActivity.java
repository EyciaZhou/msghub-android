package me.eycia.msghub_android;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.facebook.drawee.backends.pipeline.Fresco;

import me.eycia.view.SlidingTabLayout;

public class MainActivity extends AppCompatActivity {
    private MainActivityChannelsAdapter mMainActivityChannelsAdapter;

    private ViewPager mViewPager;
    private SlidingTabLayout mSlidingTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);

        setContentView(R.layout.activity_msgs_display);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.

        mMainActivityChannelsAdapter = new MainActivityChannelsAdapter(getSupportFragmentManager(), this, savedInstanceState);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mMainActivityChannelsAdapter);

        mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.tabs);
        mSlidingTabLayout.setViewPager(mViewPager);
        mSlidingTabLayout.setSelectedIndicatorColors(0xFF00BFA5);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        mMainActivityChannelsAdapter.onSaveInstanceState(savedInstanceState);
    }

    public void UpdateUI() {
        mMainActivityChannelsAdapter.notifyDataSetChanged();
        mSlidingTabLayout.setViewPager(mViewPager);
    }
}


