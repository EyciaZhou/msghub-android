package me.eycia.msghub_android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import me.eycia.api.MsgBase;
import me.eycia.api.PicRef;
import me.eycia.picScalerView.PicScalerView;

public class pictures extends AppCompatActivity {
    private MsgBase mMsg;

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pictures);

        if (savedInstanceState != null) {
            mMsg = savedInstanceState.getParcelable("m");
        } else {
            Intent intent = getIntent();
            mMsg = intent.getParcelableExtra("m");
        }

        if (mMsg != null) {
            mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), mMsg.getPicRefs());
        }

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        if (savedInstanceState == null) {
            int firstShowPic = getIntent().getIntExtra("clicked_pic", 0);
            if (firstShowPic >= mMsg.getPicRefs().length) {
                firstShowPic = 0;
            }

            mViewPager.setCurrentItem(firstShowPic);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putParcelable("m", mMsg);
    }

    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(PicRef info) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putParcelable("info", info);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_pictures, container, false);

            PicRef pic = getArguments().getParcelable("info");

            PicScalerView picScalerView = (PicScalerView) rootView.findViewById(R.id.pic_scaler_view);
            picScalerView.SetImgUri(pic.getUrl());

            TextView textView = (TextView) rootView.findViewById(R.id.textView);
            textView.setText(pic.getDescription());

            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        private PicRef[] pics = new PicRef[0];

        public SectionsPagerAdapter(FragmentManager fm, PicRef[] pics) {
            super(fm);
            this.pics = pics;
            if (this.pics == null) {
                this.pics = new PicRef[0];
            }
            notifyDataSetChanged();
        }

        @Override
        public Fragment getItem(int position) {
            return PlaceholderFragment.newInstance(pics[position]);
        }

        @Override
        public int getCount() {
            return pics.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "";
        }
    }
}
