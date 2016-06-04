package me.eycia.msghub_android

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.activity_pictures.*
import kotlinx.android.synthetic.main.fragment_pictures.view.*
import me.eycia.api.MsgBase
import me.eycia.api.PicRef

class pictures : AppCompatActivity() {
    private var mMsg: MsgBase? = null
    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pictures)

        val mMsg: MsgBase
        if (savedInstanceState != null) {
            mMsg = savedInstanceState.getParcelable<MsgBase>("m")
        } else {
            val intent = intent
            mMsg = intent.getParcelableExtra<MsgBase>("m")
        }

        if (mMsg != null) {
            mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager, mMsg.PicRefs)
        } else {
            finish()
        }

        mViewPager.adapter = mSectionsPagerAdapter

        if (savedInstanceState == null) {
            var firstShowPic = intent.getIntExtra("clicked_pic", 0)
            if (firstShowPic >= mMsg.PicRefs.size) {
                firstShowPic = 0
            }
            mViewPager.currentItem = firstShowPic
        }

        this.mMsg = mMsg
    }

    public override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        savedInstanceState.putParcelable("m", mMsg)
    }

    class PlaceholderFragment(val picInfo: PicRef) : Fragment() {

        constructor() : this(PicRef("", "", 0, 0, "")) {
        }

        override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                                  savedInstanceState: Bundle?): View? {
            val rootView = inflater!!.inflate(R.layout.fragment_pictures, container, false)
            rootView.pic_scaler_view.ImgUri = picInfo.Url
            rootView.textView.text = picInfo.Description
            return rootView
        }

        companion object {
            fun newInstance(info: PicRef): PlaceholderFragment = PlaceholderFragment(info)
        }
    }

    private inner class SectionsPagerAdapter(fm: FragmentManager, val pics: Array<PicRef>) : FragmentPagerAdapter(fm) {

        init {
            notifyDataSetChanged()
        }

        override fun getItem(position: Int): Fragment {
            return PlaceholderFragment.newInstance(pics[position])
        }

        override fun getCount(): Int {
            return pics.size
        }

        override fun getPageTitle(position: Int): CharSequence {
            return ""
        }
    }
}
