package me.eycia.views;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by eycia on 16/5/16.
 */
public class PicScaler implements View.OnTouchListener {
    int fgs = 0;
    double startDist = 0;
    double height, width;

    double top, left; //picView相较于frameLayout的偏移量
    double midp_x, midp_y; //触摸点在picView上的x,y(缩放后)
    double start_x, start_y;

    int method = 0;

    View Layout, ToScale;

    public PicScaler(View Layout, View ToScale) {
        Layout.setOnTouchListener(this);
        this.Layout = Layout;
        this.ToScale = ToScale;
    }

    double getDist(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return Math.sqrt(x * x + y * y);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) ToScale.getLayoutParams();
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                Log.d("msghub", "ACTION_DOWN");

                top = lp.topMargin;
                left = lp.leftMargin;

                start_x = event.getX();
                start_y = event.getY();

                height = ToScale.getHeight();
                width = ToScale.getWidth();

                method = 1;
                fgs = 1;
                break;
            case MotionEvent.ACTION_UP:
                Log.d("msghub", "ACTION_UP");
                method = 0;
                fgs = 0;
                break;
            case MotionEvent.ACTION_POINTER_UP:
                Log.d("msghub", "ACTION_POINTER_UP");
                fgs -= 1;
                if (fgs < 2) {
                    method = 0;
                    if (ToScale.getHeight() < Layout.getHeight() || ToScale.getWidth() < Layout.getWidth()) {
                        double rate1 = ((Layout.getHeight() + 0.0) / ToScale.getHeight());
                        double rate2 = ((Layout.getWidth() + 0.0) / ToScale.getWidth());
                        if (rate2 > rate1) {
                            rate1 = rate2;
                        }
                        lp.height *= rate1;
                        lp.width *= rate1;
                        lp.topMargin = 0;
                        lp.leftMargin = 0;
                        ToScale.setLayoutParams(lp);
                    }
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                Log.d("msghub", "ACTION_POINTER_DOWN");
                fgs += 1;
                if (fgs == 2) {
                    method = 2;
                    startDist = getDist(event);

                    top = lp.topMargin;
                    left = lp.leftMargin;

                    start_x = (event.getX(0) + event.getX(1)) / 2;
                    start_y = (event.getY(0) + event.getY(1)) / 2;

                    midp_x = start_x - left;
                    midp_y = start_y - top;

                    height = ToScale.getHeight();
                    width = ToScale.getWidth();
                }
                break;

            case MotionEvent.ACTION_MOVE:
                Layout.getParent().requestDisallowInterceptTouchEvent(true);

                if (method == 2) {
                    double rate = getDist(event) / startDist * getDist(event) / startDist;

                    double delta_x = (event.getX(0) + event.getX(1)) / 2 - start_x;
                    double delta_y = (event.getY(0) + event.getY(1)) / 2 - start_y;

                    lp.height = (int) (height * rate);
                    lp.width = (int) (width * rate);
                    lp.topMargin = (int) (top - (midp_y * rate - midp_y) + delta_y * 1.25);
                    lp.leftMargin = (int) (left - (midp_x * rate - midp_x) + delta_x * 1.25);

                } else if (method == 1) {
                    double delta_x = event.getX(0) - start_x;
                    double delta_y = event.getY(0) - start_y;

                    lp.height = (int) height;
                    lp.width = (int) width;
                    lp.topMargin = (int) (top + delta_y * 1.25);
                    lp.leftMargin = (int) (left + delta_x * 1.25);
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                Layout.getParent().requestDisallowInterceptTouchEvent(false);
                break;
        }

        if (lp.height + lp.topMargin < Layout.getHeight()) {
            lp.topMargin = Layout.getHeight() - lp.height;
        } else if (lp.topMargin > 0) {
            lp.topMargin = 0;
        }

        if (lp.width + lp.leftMargin < Layout.getWidth()) {
            lp.leftMargin = Layout.getWidth() - lp.width;
        } else if (lp.leftMargin > 0) {
            lp.leftMargin = 0;
        }

        ToScale.setLayoutParams(lp);

        return true;
    }
}
