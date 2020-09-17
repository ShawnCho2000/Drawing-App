package cse340.undo.app;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

/**
 * This is a subclass of AbstractColorPickerView, that is, this View implements a ColorPicker.
 *
 * There are several class fields, enums, callback classes, and helper functions which have
 * been implemented for you.
 *
 * PLEASE READ AbstractColorPickerView.java to learn about these.
 */
public class ColorPickerView extends AbstractColorPickerView {
/* ********************************************************************************************** *
 * All of your applications state (the model) and methods that directly manipulate it are here    *
 * This does not include mState which is the literal state of your PPS, which is inherited
 * ********************************************************************************************** */

    /**
     * The current color selected in the ColorPicker. Not necessarily the last
     * color that was sent to the listeners.
     */
    @ColorInt
    protected int mCurrentColor;
    public int currentColor;

    @Override
    public void setColor(@ColorInt int newColor) {
        mCurrentColor = newColor;
        // TODO maybe there's more to do here
        // hint: there is.
        invalidate();
    }

    private void updateModel(float x, float y) {
        // TODO implement this
        // hint: we give you a very helpful function to call


        float xCord = x;
        float yCord = y;

        xCord -= mCenterX;
        yCord -= mCenterY;
        yCord = -1 * yCord;
        double angle2 = Math.atan2(yCord, xCord);
        mCurrentColor = getColorFromAngle(-angle2);

    }

/* ********************************************************************************************** *
 *                               <End of model declarations />
 * ********************************************************************************************** */

/* ********************************************************************************************** *
 * You may create any constants you wish here.                                                     *
 * You may also create any fields you want, that are not necessary for the state but allow       *
 * for better optimized or cleaner code                                                           *
 * ********************************************************************************************** */

    Paint paint = new Paint();
    Paint thumbWhite = new Paint();
    int i = 0;
    float touchX;
    float touchY;
    boolean start = true;

/* ********************************************************************************************** *
 *                               <End of other fields and constants declarations />
 * ********************************************************************************************** */

    public ColorPickerView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // TODO: Initialize variables as necessary (such as state)
        mState = State.START;
        mCurrentColor = DEFAULT_COLOR;
        thumbWhite.setColor(Color.WHITE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // TODO: draw the thumb and center circle

        float xCord = touchX;
        float yCord = touchY;

        xCord -= mCenterX;
        yCord -= mCenterY;
        yCord = -1 * yCord;
        double angle2 = Math.atan2(yCord, xCord);
        currentColor = getColorFromAngle(-angle2);
        paint.setColor(getColorFromAngle(-angle2));


        float smallR  = mRadius * RADIUS_TO_THUMB_RATIO;
        angle2 += Math.PI / 2;
        if (i == 0) {
            canvas.drawCircle(mCenterX,mCenterY - (mRadius - smallR), ((mRadius * 85) / 1000), thumbWhite);
//            paint.setColor(DEFAULT_COLOR);
//            canvas.drawCircle(mCenterX ,mCenterY,(mRadius - 2 * smallR), paint);
            start = false;
        }
        else {
            canvas.drawCircle((mCenterX + (mRadius - smallR) * (float) Math.sin(angle2)), mCenterY + (mRadius - smallR) * (float) Math.cos(angle2),
                    ((mRadius * 85) / 1000), thumbWhite);
//            canvas.drawCircle(mCenterX, mCenterY, (mRadius - 2 * smallR), paint);
        }
        Log.d("tag9", "tesasldfjsdlfjdsfjdslkt");
        Log.d("tag2", Math.cos(angle2) + " asdfsdf");
        i++;
        if (mState == State.INSIDE) {
            thumbWhite.setAlpha((int)(0.5f * 255));
        }

        else{
            thumbWhite.setAlpha((int)(1f * 255));
        }

    }

    @Override
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        // TODO: calculate mRadius, mCenterX, and mCenterY based View dimensions
        mRadius = Math.abs((left - right)/2);
        mCenterX = Math.abs(left - right)/2;
        mCenterY = Math.abs(top - bottom)/2;
    }

    /**
     * Calculate the essential geometry given an event.
     *
     * @param event Motion event to compute geometry for, most likely a touch.
     * @return EssentialGeometry value.
     */
    @Override
    protected EssentialGeometry essentialGeometry(MotionEvent event) {
        // TODO: compute the geometry for the given event
        touchX = event.getX();
        touchY = event.getY();

//        if ((touchX < (mCenterX + mRadius) || touchX > (mCenterX - mRadius)) && (touchY < (mCenterY + mRadius) || (touchY > (mCenterY - mRadius)))){
        float xCoord = touchX - mCenterX;
        float yCoord = touchY - mCenterY;

        float radius = (float)Math.sqrt((double)(xCoord * xCoord + yCoord * yCoord));

        if ((radius < mRadius)){
            return EssentialGeometry.WHEEL;
        }

        else {
            return EssentialGeometry.OFFWHEEL;
        }
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        EssentialGeometry geometry = essentialGeometry(event);
        Log.d("tag3", paint.getColor() + "    o123");

        switch(mState) {
            case START:
                if (event.getAction() == MotionEvent.ACTION_DOWN && geometry == EssentialGeometry.WHEEL) {
                    mState = State.INSIDE;
                    updateModel(event.getX(), event.getY());
                    invalidate();
                    return true;
                }
                break;
            case INSIDE:
                if (event.getAction() == MotionEvent.ACTION_MOVE && geometry == EssentialGeometry.WHEEL) {
                    updateModel(event.getX(), event.getY());
                    invalidate();
                    Log.d("tag3", paint.getColor() + "   123" + currentColor);
                    return true;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    mState = State.START;
                    invokeColorChangeListeners(mCurrentColor);
                    updateModel(event.getX(),event.getY());
                    invalidate();
                    return true;
                }
                return false;
             default:
                return false;
        }

        return false;
    }

    /**
     * Converts from a color to angle on the wheel.
     *
     * @param color RGB color as integer.
     * @return Position of this color on the wheel in radians.
     * @see AbstractColorPickerView#getTouchAngle(float, float)
     */
    public static float getAngleFromColor(int color) {
        // TODO: Convert hue (degrees) to angle on wheel (radians).

        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        float hue = hsv[0];
        float angle = ((float) Math.toRadians(hue - 90));

        return angle;
    }

}