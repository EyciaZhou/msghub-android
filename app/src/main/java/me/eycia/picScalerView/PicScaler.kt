package me.eycia.picScalerView

import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup

fun ViewGroup.setScaler(childView: View) {
    PicScaler(this, childView)
}

class PicScaler(private var parent: ViewGroup,
                private var toScale: View) : View.OnTouchListener {

    private data class Axis(var x: Float, var y: Float) {
        operator fun minus(other: Axis): Axis {
            return Axis(x - other.x, y - other.y)
        }
    }

    private companion object {
        const val METHOD_NOTHING = 0
        const val METHOD_DRAG = 1
        const val METHOD_SCALE = 2
    }

    private var fingerCount = 0     //finger number on screen
    private var startFingerDist: Float = 0.0f     //two finger's distant, when scale start

    private var startHeightOfToScale: Float = 0.0f    //height when scale start
    private var startWidthOfToScale: Float = 0.0f     //width when scale start

    private var startTopGap: Float = 0.0f
    private var startLeftGap: Float = 0.0f //picView相较于frameLayout的偏移量
    private var startPosOfToScale: Axis = Axis(0.0f, 0.0f)
    private var startPosOfParent: Axis = Axis(0.0f, 0.0f)

    private var method = METHOD_NOTHING

    init {
        if (parent.indexOfChild(toScale) < 0) {
            throw Exception("toScale:View  is not the child of parent:ViewGroup")
        }

        parent.setOnTouchListener(this)

        val toScale_lp = toScale.layoutParams as ViewGroup.MarginLayoutParams
        val ratio = toScale.height.toFloat() / toScale.width

        toScale_lp.width = parent.width
        toScale_lp.height = (parent.width * ratio).toInt()
        toScale_lp.topMargin = (parent.height - toScale_lp.height) / 2
        if (toScale_lp.height > parent.height) {
            toScale_lp.topMargin = 0
        }
        toScale_lp.leftMargin = 0

        toScale.layoutParams = toScale_lp
    }

    private fun getTwoFingerDist(event: MotionEvent): Float {
        val x = event.getX(0) - event.getX(1)
        val y = event.getY(0) - event.getY(1)
        return Math.sqrt((x * x + y * y).toDouble()).toFloat()
    }

    private fun getTwoFingerCenter(event: MotionEvent): Axis {
        return Axis((event.getX(0) + event.getX(1)) / 2, (event.getY(0) + event.getY(1)) / 2)
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        val lp = toScale.layoutParams as ViewGroup.MarginLayoutParams

        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                startTopGap = lp.topMargin.toFloat()
                startLeftGap = lp.leftMargin.toFloat()

                startPosOfParent = Axis(event.x, event.y)

                startHeightOfToScale = toScale.height.toFloat()
                startWidthOfToScale = toScale.width.toFloat()

                method = METHOD_DRAG
                fingerCount = 1
            }
            MotionEvent.ACTION_UP -> {
                Log.d("msghub", "ACTION_UP")
                method = METHOD_NOTHING
                fingerCount = 0
            }
            MotionEvent.ACTION_POINTER_UP -> {
                Log.d("msghub", "ACTION_POINTER_UP")
                fingerCount -= 1
                if (fingerCount < 2) {      //if after finger leave, only one finger remains, not level-down to METHOD_DRAG
                    method = METHOD_NOTHING
                }
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                Log.d("msghub", "ACTION_POINTER_DOWN")
                fingerCount += 1
                if (fingerCount == 2) {     //start METHOD_SCALE, init
                    method = METHOD_SCALE
                    startFingerDist = getTwoFingerDist(event)

                    startTopGap = lp.topMargin.toFloat()
                    startLeftGap = lp.leftMargin.toFloat()

                    startPosOfParent = getTwoFingerCenter(event)

                    startPosOfToScale.x = startPosOfParent.x - startLeftGap
                    startPosOfToScale.y = startPosOfParent.y - startTopGap

                    startHeightOfToScale = toScale.height.toFloat()
                    startWidthOfToScale = toScale.width.toFloat()
                }
            }

            MotionEvent.ACTION_MOVE -> {
                parent.parent.requestDisallowInterceptTouchEvent(true)

                if (method == METHOD_SCALE) {
                    val rate = getTwoFingerDist(event) / startFingerDist * getTwoFingerDist(event) / startFingerDist

                    val delta = getTwoFingerCenter(event) - startPosOfParent

                    lp.height = (startHeightOfToScale * rate).toInt()
                    lp.width = (startWidthOfToScale * rate).toInt()
                    lp.topMargin = (startTopGap - (startPosOfToScale.y * rate - startPosOfToScale.y) + delta.y * 1.25).toInt()
                    lp.leftMargin = (startLeftGap - (startPosOfToScale.x * rate - startPosOfToScale.x) + delta.x * 1.25).toInt()

                } else if (method == METHOD_DRAG) {
                    val delta = Axis(event.getX(0), event.getY(0)) - startPosOfParent

                    lp.height = startHeightOfToScale.toInt()
                    lp.width = startWidthOfToScale.toInt()
                    lp.topMargin = (startTopGap + delta.y * 1.25).toInt()
                    lp.leftMargin = (startLeftGap + delta.x * 1.25).toInt()
                }
            }
            MotionEvent.ACTION_CANCEL -> parent.parent.requestDisallowInterceptTouchEvent(false)
        }

        if (method == METHOD_NOTHING && lp.width < parent.width) {      //when move end, if the picture's width less than parent, set it same as parent's
            val rate = (parent.width + 0.0) / lp.width

            lp.topMargin -= (lp.height * (rate - 1) / 2).toInt()
            lp.leftMargin = 0

            lp.height *= rate.toInt()
            lp.width *= rate.toInt()

            toScale.layoutParams = lp
        }

        /*
        if picture's height > parent's height, top or bottom should not have blank
        if picture's height < parent's height, picture's top or bottom should not overtake parent's border
         */
        if (lp.height > parent.height) {
            if (lp.height + lp.topMargin < parent.height) {
                lp.topMargin = parent.height - lp.height
            } else if (lp.topMargin > 0) {
                lp.topMargin = 0
            }
        } else {
            if (lp.topMargin < 0) {
                lp.topMargin = 0
            } else if (lp.topMargin + lp.height > parent.height) {
                lp.topMargin = parent.height - lp.height
            }
        }

        /*
        same as height
         */
        if (lp.width > parent.width) {
            if (lp.width + lp.leftMargin < parent.width) {
                lp.leftMargin = parent.width - lp.width
            } else if (lp.leftMargin > 0) {
                lp.leftMargin = 0
            }
        } else {
            if (lp.leftMargin < 0) {
                lp.leftMargin = 0
            } else if (lp.leftMargin + lp.width > parent.width) {
                lp.leftMargin = parent.width - lp.width
            }
        }

        toScale.layoutParams = lp

        return true
    }
}
