package me.eycia.msghub_android;

import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.image.ImageInfo;

import me.eycia.api.Msg;
import me.eycia.api.PicRef;
import me.eycia.views.PicScaler;

public class pictures extends AppCompatActivity {
    private Msg m;

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pictures);

        if (savedInstanceState != null) {
            m = savedInstanceState.getParcelable("m");
        } else {
            Intent intent = getIntent();
            m = intent.getParcelableExtra("m");
        }

        if (m != null) {
            mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), m.PicRefs);
        }

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putParcelable("m", m);
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

            final FrameLayout fl = (FrameLayout) rootView.findViewById(R.id.contain_view);
            fl.setOnClickListener(null);

            final SimpleDraweeView picView = (SimpleDraweeView) rootView.findViewById(R.id.view);
            picView.setController(Fresco.newDraweeControllerBuilder().setControllerListener(new BaseControllerListener<ImageInfo>() {
                @Override
                public void onFinalImageSet(
                        String id,
                        @Nullable ImageInfo imageInfo,
                        @Nullable Animatable anim) {
                    if (imageInfo == null) {
                        return;
                    }

                    Log.d("msghub", "onFinalImageSet");
                    Log.d("msghub", imageInfo.getWidth() + "");
                    Log.d("msghub", imageInfo.getHeight() + "");

                    ViewGroup.LayoutParams lp = picView.getLayoutParams();
                    lp.width = imageInfo.getWidth();
                    lp.height = imageInfo.getHeight();
                    picView.setLayoutParams(lp);

                    new PicScaler(fl, picView);
                }

                @Override
                public void onIntermediateImageSet(String id, @Nullable ImageInfo imageInfo) {

                }

                @Override
                public void onFailure(String id, Throwable throwable) {

                }
            }).setUri(pic.Url).build());

            TextView textView = (TextView) rootView.findViewById(R.id.textView);
            textView.setText(pic.Description);

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
