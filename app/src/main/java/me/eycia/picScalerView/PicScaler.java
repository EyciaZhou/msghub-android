package me.eycia.picScalerView;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.imagepipeline.image.ImageInfo;

public class PicScaler implements View.OnTouchListener {
    int fgs = 0;
    double startDist = 0;
    double height, width;

    double top, left; //picView相较于frameLayout的偏移量
    double midp_x, midp_y; //触摸点在picView上的x,y(缩放后)
    double start_x, start_y;

    int method = 0;

    ImageInfo imageInfo;

    View Layout, ToScale;

    public PicScaler(View Layout, View ToScale, ImageInfo imageInfo) {
        Layout.setOnTouchListener(this);
        this.Layout = Layout;
        this.ToScale = ToScale;
        this.imageInfo = imageInfo;

        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) ToScale.getLayoutParams();
        //lp.width = imageInfo.getWidth();
        //lp.height = imageInfo.getHeight();

        //if (lp.width > Layout.getWidth()) {
        lp.width = Layout.getWidth();
        lp.height = Layout.getWidth() * imageInfo.getHeight() / imageInfo.getWidth();
        //}

        lp.topMargin = (Layout.getHeight() - lp.height) / 2;
        lp.leftMargin = (Layout.getWidth() - lp.width) / 2;

        if (lp.height > Layout.getHeight()) {
            lp.topMargin = 0;
        }

        ToScale.setLayoutParams(lp);
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

        if (method == 0 && lp.width < Layout.getWidth()) {
            double rate = ((Layout.getWidth() + 0.0) / lp.width);

            lp.topMargin -= lp.height * (rate - 1) / 2;
            lp.leftMargin = 0;

            lp.height *= rate;
            lp.width *= rate;

            ToScale.setLayoutParams(lp);
        }

        if (lp.height > Layout.getHeight()) {
            if (lp.height + lp.topMargin < Layout.getHeight()) {
                lp.topMargin = Layout.getHeight() - lp.height;
            } else if (lp.topMargin > 0) {
                lp.topMargin = 0;
            }
        } else {
            if (lp.topMargin < 0) {
                lp.topMargin = 0;
            } else if (lp.topMargin + lp.height > Layout.getHeight()) {
                lp.topMargin = Layout.getHeight() - lp.height;
            }
        }

        if (lp.width > Layout.getWidth()) {
            if (lp.width + lp.leftMargin < Layout.getWidth()) {
                lp.leftMargin = Layout.getWidth() - lp.width;
            } else if (lp.leftMargin > 0) {
                lp.leftMargin = 0;
            }
        } else {
            if (lp.leftMargin < 0) {
                lp.leftMargin = 0;
            } else if (lp.leftMargin + lp.width > Layout.getWidth()) {
                lp.leftMargin = Layout.getWidth() - lp.width;
            }
        }

        ToScale.setLayoutParams(lp);

        return true;
    }
}
