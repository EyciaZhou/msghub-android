package me.eycia;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

/**
 * Created by eycia on 16/5/30.
 */
public class LScrollView extends ScrollView {
    public LScrollView(Context context) {
        super(context);
    }

    public LScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public LScrollView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return false;
    }
}
