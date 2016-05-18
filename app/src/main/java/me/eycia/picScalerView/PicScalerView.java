package me.eycia.picScalerView;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Animatable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.image.ImageInfo;

public class PicScalerView extends FrameLayout {
    private SimpleDraweeView picView;
    private GenericDraweeHierarchyBuilder builder = new GenericDraweeHierarchyBuilder(getResources());
    private boolean LayoutLoaded = false;
    private String SettedImgUriBeforeLayoutLoaded;

    private void setImgController(String Uri) {
        picView.setController(Fresco.newDraweeControllerBuilder().setControllerListener(new BaseControllerListener<ImageInfo>() {
            @Override
            public void onFinalImageSet(String id, @Nullable ImageInfo imageInfo, @Nullable Animatable anim) {
                if (imageInfo == null) {
                    return;
                }
                new PicScaler(PicScalerView.this, picView, imageInfo);
            }

            @Override
            public void onIntermediateImageSet(String id, @Nullable ImageInfo imageInfo) {

            }

            @Override
            public void onFailure(String id, Throwable throwable) {

            }
        }).setAutoPlayAnimations(true)
                .setTapToRetryEnabled(true)
                .setUri(Uri)
                .build());
    }

    private void initPicView(Context context) {
        GenericDraweeHierarchy hierarchy = builder.setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER)
                .setProgressBarImage(new ProgressBar(Color.WHITE, Color.GREEN, Color.BLACK))
                .build();
        picView.setHierarchy(hierarchy);

        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onGlobalLayout() {
                getViewTreeObserver().removeOnGlobalLayoutListener(this);
                if (SettedImgUriBeforeLayoutLoaded != null && !SettedImgUriBeforeLayoutLoaded.equals("")) {
                    setImgController(SettedImgUriBeforeLayoutLoaded);
                }
                LayoutLoaded = true;
            }
        });
    }

    public void SetImgUri(String Uri) {
        if (LayoutLoaded) {
            setImgController(Uri);
        } else {
            SettedImgUriBeforeLayoutLoaded = Uri;
        }
    }

    public PicScalerView(Context context) {
        super(context);

        picView = new SimpleDraweeView(context);
        this.addView(picView);
        initPicView(context);
    }

    public PicScalerView(Context context, AttributeSet attrs) {
        super(context, attrs);

        picView = new SimpleDraweeView(context, attrs);
        this.addView(picView);
        initPicView(context);
    }

    public PicScalerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        picView = new SimpleDraweeView(context, attrs, defStyleAttr);
        this.addView(picView);
        initPicView(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PicScalerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        picView = new SimpleDraweeView(context, attrs, defStyleAttr, defStyleRes);
        this.addView(picView);
        initPicView(context);
    }
}
