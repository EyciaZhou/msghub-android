package me.eycia.picScalerView

import android.annotation.TargetApi
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Animatable
import android.os.Build
import android.util.AttributeSet
import android.widget.FrameLayout
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.controller.BaseControllerListener
import com.facebook.drawee.drawable.ScalingUtils
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.image.ImageInfo

class PicScalerView : FrameLayout {
    private var picView: SimpleDraweeView
    private val builder = GenericDraweeHierarchyBuilder(resources)

    var ImgUri: String = ""
        set(value) {
            field = value
            setImgController(value)
        }

    private fun setImgController(Uri: String) {
        picView.controller = Fresco.newDraweeControllerBuilder().setControllerListener(object : BaseControllerListener<ImageInfo>() {
            override fun onFinalImageSet(id: String?, imageInfo: ImageInfo?, anim: Animatable?) {
                if (imageInfo == null) {
                    return
                }
                this@PicScalerView.setScaler(picView)
            }

            override fun onIntermediateImageSet(id: String?, imageInfo: ImageInfo?) {

            }

            override fun onFailure(id: String?, throwable: Throwable?) {

            }
        }).setAutoPlayAnimations(true).setTapToRetryEnabled(true).setUri(Uri).build()
    }

    private fun initPicView() {
        val hierarchy = builder.setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER).setProgressBarImage(ProgressBar(Color.WHITE, Color.GREEN, Color.BLACK)).build()
        picView.hierarchy = hierarchy
    }

    constructor(context: Context) : super(context) {
        picView = SimpleDraweeView(context)
        this.addView(picView)
        initPicView()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        picView = SimpleDraweeView(context, attrs)
        this.addView(picView)
        initPicView()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        picView = SimpleDraweeView(context, attrs, defStyleAttr)
        this.addView(picView)
        initPicView()
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        picView = SimpleDraweeView(context, attrs, defStyleAttr, defStyleRes)
        this.addView(picView)
        initPicView()
    }
}