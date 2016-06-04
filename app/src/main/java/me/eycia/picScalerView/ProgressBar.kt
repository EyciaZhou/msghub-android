package me.eycia.picScalerView

import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.Log
import com.facebook.drawee.drawable.DrawableUtils

/**
 * A progress bar show in center
 */
class ProgressBar(BackgroundColor: Int, color: Int, TextColor: Int) : Drawable() {
    private val mPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    var backgroundColor = 0x80000000.toInt()
        set(backgroundColor) {
            if (field != backgroundColor) {
                field = backgroundColor
                invalidateSelf()
            }
        }

    var textColor = Color.BLACK
        set(color) {
            if (textColor != color) {
                field = color
                invalidateSelf()
            }
        }

    var color = 0x800080FF.toInt()
        set(color) {
            if (this.color != color) {
                this.color = color
                invalidateSelf()
            }
        }

    private var mPadding = 10
    fun setPadding(padding: Int) {
        if (mPadding != padding) {
            mPadding = padding
            invalidateSelf()
        }
    }

    var barWidth = 20
        set(barWidth) {
            if (this.barWidth != barWidth) {
                this.barWidth = barWidth
                invalidateSelf()
            }
        }
    private var mLevel = 0

    var hideWhenZero = false

    init {
        this.backgroundColor = BackgroundColor
        this.color = color
        this.textColor = TextColor
    }

    /**
     * Gets the progress bar padding.
     */
    override fun getPadding(padding: Rect): Boolean {
        padding.set(mPadding, mPadding, mPadding, mPadding)
        return mPadding != 0
    }

    override fun onLevelChange(level: Int): Boolean {
        mLevel = level
        invalidateSelf()
        return true
    }

    override fun setAlpha(alpha: Int) {
        mPaint.alpha = alpha
    }

    override fun setColorFilter(cf: ColorFilter?) {
        mPaint.colorFilter = cf
    }

    override fun getOpacity(): Int {
        return DrawableUtils.getOpacityFromColor(mPaint.color)
    }

    override fun draw(canvas: Canvas) {
        if (hideWhenZero && mLevel == 0) {
            return
        }
        drawBar(canvas, 10000, backgroundColor)
        drawBar(canvas, mLevel, color)
        drawText(canvas, mLevel, textColor)
    }

    private fun drawText(canvas: Canvas, level: Int, color: Int) {
        val bounds = bounds
        val texts = String.format("%5.1f/100.0", (level + 0.0) / 100.0)

        Log.d("msghub", texts)

        mPaint.textAlign = Paint.Align.CENTER
        mPaint.textSize = 30f

        val xpos_text = bounds.width() / 2
        val ypos_text = bounds.height() / 2
        mPaint.color = color
        canvas.drawText(texts, xpos_text.toFloat(), ypos_text.toFloat(), mPaint)
    }

    private fun drawBar(canvas: Canvas, level: Int, color: Int) {
        val bounds = bounds
        val length = (bounds.width() - 2 * mPadding) * level / 10000
        val xpos = bounds.left + mPadding
        val ypos = (bounds.height() - barWidth) / 2
        mPaint.color = color
        canvas.drawRect(xpos.toFloat(), ypos.toFloat(), (xpos + length).toFloat(), (ypos + barWidth).toFloat(), mPaint)
    }
}
