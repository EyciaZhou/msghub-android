package me.eycia.msghub_android;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;

import me.eycia.api.API;
import me.eycia.api.ChanInfo;
import me.eycia.view.SlidingTabLayout;

public class msgsDisplay extends AppCompatActivity {
    private allChans mAllChans;

    private ViewPager mViewPager;
    private SlidingTabLayout mSlidingTabLayout;

    private ChanInfo[] cs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Fresco.initialize(this);

        setContentView(R.layout.activity_msgs_display);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.

        mAllChans = new allChans(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mAllChans);

        mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.tabs);
        mSlidingTabLayout.setViewPager(mViewPager);
        mSlidingTabLayout.setSelectedIndicatorColors(0xFF00BFA5);

        if (savedInstanceState == null) {

            API.ChansInfoCallback(new API.Callback() {
                @Override
                public void Succ(final Object o) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (o != null) {
                                cs = (ChanInfo[]) (o);
                                mAllChans.Update(cs);
                                mSlidingTabLayout.setViewPager(mViewPager);
                            }
                        }
                    });
                }

                @Override
                public void Err(final Exception e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        } else {
            cs = (ChanInfo[])savedInstanceState.getParcelableArray("chanInfos");
            mAllChans.Update(cs);
            mSlidingTabLayout.setViewPager(mViewPager);
        }

      FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
      fab.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                      .setAction("Action", null).show();
          }
      });
      
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_msgs_display, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putParcelableArray("chanInfos", cs);
    }
}


