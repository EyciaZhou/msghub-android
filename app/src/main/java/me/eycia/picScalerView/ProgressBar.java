package me.eycia.picScalerView;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.facebook.drawee.drawable.DrawableUtils;

/**
 * A progress bar show in center
 */
public class ProgressBar extends Drawable {
    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int mBackgroundColor = 0x80000000;
    private int mTextColor = Color.BLACK;
    private int mColor = 0x800080FF;
    private int mPadding = 10;
    private int mBarWidth = 20;
    private int mLevel = 0;
    private boolean mHideWhenZero = false;

    public ProgressBar(int BackgroundColor, int Color, int TextColor) {
        super();
        this.mBackgroundColor = BackgroundColor;
        this.mColor = Color;
        this.mTextColor = TextColor;
    }

    /**
     * Sets the progress bar color.
     */
    public void setTextColor(int color) {
        if (mTextColor != color) {
            mTextColor = color;
            invalidateSelf();
        }
    }

    /**
     * Gets the progress bar color.
     */
    public int getTextColor() {
        return mTextColor;
    }


    /**
     * Sets the progress bar color.
     */
    public void setColor(int color) {
        if (mColor != color) {
            mColor = color;
            invalidateSelf();
        }
    }

    /**
     * Gets the progress bar color.
     */
    public int getColor() {
        return mColor;
    }

    /**
     * Sets the progress bar background color.
     */
    public void setBackgroundColor(int backgroundColor) {
        if (mBackgroundColor != backgroundColor) {
            mBackgroundColor = backgroundColor;
            invalidateSelf();
        }
    }

    /**
     * Gets the progress bar background color.
     */
    public int getBackgroundColor() {
        return mBackgroundColor;
    }

    /**
     * Sets the progress bar padding.
     */
    public void setPadding(int padding) {
        if (mPadding != padding) {
            mPadding = padding;
            invalidateSelf();
        }
    }

    /**
     * Gets the progress bar padding.
     */
    @Override
    public boolean getPadding(Rect padding) {
        padding.set(mPadding, mPadding, mPadding, mPadding);
        return mPadding != 0;
    }

    /**
     * Sets the progress bar width.
     */
    public void setBarWidth(int barWidth) {
        if (mBarWidth != barWidth) {
            mBarWidth = barWidth;
            invalidateSelf();
        }
    }

    /**
     * Gets the progress bar width.
     */
    public int getBarWidth() {
        return mBarWidth;
    }

    /**
     * Sets whether the progress bar should be hidden when the progress is 0.
     */
    public void setHideWhenZero(boolean hideWhenZero) {
        mHideWhenZero = hideWhenZero;
    }

    /**
     * Gets whether the progress bar should be hidden when the progress is 0.
     */
    public boolean getHideWhenZero() {
        return mHideWhenZero;
    }

    @Override
    protected boolean onLevelChange(int level) {
        mLevel = level;
        invalidateSelf();
        return true;
    }

    @Override
    public void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        mPaint.setColorFilter(cf);
    }

    @Override
    public int getOpacity() {
        return DrawableUtils.getOpacityFromColor(mPaint.getColor());
    }

    @Override
    public void draw(Canvas canvas) {
        if (mHideWhenZero && mLevel == 0) {
            return;
        }
        drawBar(canvas, 10000, mBackgroundColor);
        drawBar(canvas, mLevel, mColor);
        drawText(canvas, mLevel, mTextColor);
    }

    private void drawText(Canvas canvas, int level, int color) {
        Rect bounds = getBounds();
        String texts = String.format("%5.1f/100.0", (level + 0.0) / 100.0);

        Log.d("msghub", texts);

        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setTextSize(30);

        int xpos_text = bounds.width() / 2;
        int ypos_text = bounds.height() / 2;
        mPaint.setColor(color);
        canvas.drawText(texts, xpos_text, ypos_text, mPaint);
    }

    private void drawBar(Canvas canvas, int level, int color) {
        Rect bounds = getBounds();
        int length = (bounds.width() - 2 * mPadding) * level / 10000;
        int xpos = bounds.left + mPadding;
        int ypos = (bounds.height() - mBarWidth) / 2;
        mPaint.setColor(color);
        canvas.drawRect(xpos, ypos, xpos + length, ypos + mBarWidth, mPaint);
    }
}
