package me.eycia.msghub_android

import android.annotation.TargetApi
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v4.view.GravityCompat
import android.support.v4.view.ViewPager
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.WindowManager
import com.facebook.drawee.backends.pipeline.Fresco
import kotlinx.android.synthetic.main.activity_msgs_display.*

class MainActivity : AppCompatActivity() {
    private var mMainActivityChannelsAdapter: MainActivityChannelsAdapter? = null
    private var mMenu: LeftMenuLayoutController? = null

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Fresco.initialize(this)

        setContentView(R.layout.activity_msgs_display)

        mTitleText.alpha = 0f
        mToolbar.title = ""
        setSupportActionBar(mToolbar)

        mMenu = LeftMenuLayoutController(this)

        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        val actionBarDrawerToggle = ActionBarDrawerToggle(this, mDrawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        actionBarDrawerToggle.syncState()

        mDrawerLayout.setDrawerListener(actionBarDrawerToggle)

        mAppBarLayout.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            val maxScroll = appBarLayout.totalScrollRange
            val percentage = Math.abs(verticalOffset).toFloat() / maxScroll.toFloat()

            val lp = mTitleImage.layoutParams
            lp.height = (0.35 * (1 - percentage / 2.3) * appBarLayout.height.toDouble()).toInt()
            mTitleImage.layoutParams = lp

            mTitleText.alpha = percentage
            mSlidingTabLayout.alpha = 1 - percentage
        }

        mMainActivityChannelsAdapter = MainActivityChannelsAdapter(supportFragmentManager, savedInstanceState)
        mMainActivityChannelsAdapter!!.ChansNotifier.addOnDataChangeListener {
            runOnUiThread {
                mSlidingTabLayout.setViewPager(mViewPager)
                mTitleText.text = mMainActivityChannelsAdapter!!.getPageTitle(mViewPager.currentItem)
            }
        }

        // Set up the ViewPager with the sections adapter.
        mViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                mTitleText.text = mMainActivityChannelsAdapter!!.getPageTitle(position)
            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })
        mViewPager.adapter = mMainActivityChannelsAdapter

        mSlidingTabLayout.setViewPager(mViewPager)
        mSlidingTabLayout.setSelectedIndicatorColors(resources.getColor(R.color.colorPrimaryDark))


        val window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = resources.getColor(R.color.colorPrimary)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                mDrawerLayout.closeDrawers()
            } else {
                mDrawerLayout.openDrawer(GravityCompat.START)
            }
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    public override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        mMainActivityChannelsAdapter!!.onSaveInstanceState(savedInstanceState)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        mMenu!!.onActivityResult(requestCode, resultCode, data)
    }
}


