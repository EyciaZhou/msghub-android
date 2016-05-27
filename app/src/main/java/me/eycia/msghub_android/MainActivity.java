package me.eycia.msghub_android;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.view.SlidingTabLayout;

import me.eycia.Notifier;

public class MainActivity extends AppCompatActivity {
    private MainActivityChannelsAdapter mMainActivityChannelsAdapter;

    private ViewPager mViewPager;
    private SlidingTabLayout mSlidingTabLayout;
    private AppBarLayout mAppBarLayout;
    private ImageView mTitleImage;
    private DrawerLayout mDrawerLayout;
    private LeftMenuLayoutController mMenu;
    private Toolbar mToolbar;
    private TextView mTitleText;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);

        setContentView(R.layout.activity_msgs_display);

        mTitleText = (TextView) findViewById(R.id.title_text);
        mTitleText.setAlpha(0);
        mTitleImage = (ImageView) findViewById(R.id.title_imgview);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);

        mMenu = new LeftMenuLayoutController(this);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ActionBarDrawerToggle actionBarDrawerToggle=new ActionBarDrawerToggle(this,mDrawerLayout,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        actionBarDrawerToggle.syncState();
        mDrawerLayout.setDrawerListener(actionBarDrawerToggle);

        mAppBarLayout = (AppBarLayout) findViewById(R.id.appbar2);
        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                int maxScroll = appBarLayout.getTotalScrollRange();
                float percentage = (float) Math.abs(verticalOffset) / (float) maxScroll;


                ViewGroup.LayoutParams lp = mTitleImage.getLayoutParams();
                lp.height = (int) (0.6*(1-percentage/2.5) * appBarLayout.getHeight());
                mTitleImage.setLayoutParams(lp);

                mTitleText.setAlpha(percentage);
                mSlidingTabLayout.setAlpha(1-percentage);
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
                        mTitleText.setText(mMainActivityChannelsAdapter.getPageTitle(mViewPager.getCurrentItem()));
                    }
                });
            }
        });

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mTitleText.setText(mMainActivityChannelsAdapter.getPageTitle(position));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
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
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            if(  mDrawerLayout.isDrawerOpen(GravityCompat.START)
                    ){
                mDrawerLayout.closeDrawers();
            }else{
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        mMainActivityChannelsAdapter.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mMenu.onActivityResult(requestCode, resultCode, data);
    }
}


