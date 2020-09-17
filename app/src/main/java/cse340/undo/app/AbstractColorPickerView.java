package cse340.undo.app;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.List;

import cse340.undo.R;

/* ********************************************************************************************** *
 * ********************************************************************************************** *
 *                      DO NOT EDIT THIS FILE, PLEASE, DO NOT EDIT THIS FILE                      *
 * ********************************************************************************************** *
 * ********************************************************************************************** */

/**
 * This is an abstract class which serves to provide an interface for a general ColorPickerView
 * which is a view that allows users to choose colors and provides a method to register
 * event listeners.
 */
public abstract class AbstractColorPickerView extends AppCompatImageView { //} implements View.OnKeyListener {
    /** The default color that the ColorPickerView should display when launched. */
    @ColorInt
    public static final int DEFAULT_COLOR = Color.RED;

    /** Helper fields for keeping track of view geometry. */
    protected float mCenterX, mCenterY, mRadius;

    /** Ratio between radius of the thumb handle and mRadius, the radius of the wheel. */
    protected static final float RADIUS_TO_THUMB_RATIO = 0.085f;

    /** Used the state to keep track of the PPS state for ColorPickerView. */
    public enum State { START, INSIDE } // staff note: should be protected
    protected State mState;

    /** @see AbstractColorPickerView#essentialGeometry(MotionEvent)  */
    public enum EssentialGeometry { WHEEL, OFFWHEEL } // staff note: should be protected


    /** A List of registered ColorChangeListeners */
    @NonNull
    private List<ColorChangeListener> mColorChangeListeners;

    /**
     * Class which defines a listener to be called when a new color is selected.
     */
    public interface ColorChangeListener {
        void onColorSelected(@ColorInt int color);
    }

    /**
     * Registers a new listener
     *
     * @param colorChangeListener New listener (should not be null).
     * @throws IllegalArgumentException if colorChangeListener is null
     */
    public final void addColorChangeListener(@NonNull ColorChangeListener colorChangeListener) {
        if (colorChangeListener == null) {
            throw new IllegalArgumentException("colorChangeListener should never be null");
        }
        mColorChangeListeners.add(colorChangeListener);
    }

    /**
     * Removes a ColorChangeListener, if it exists
     *
     * @param colorChangeListener Listener that should be removed (should not be null).
     * @return True if the listener did exist, and was thus removed. False otherwise.
     */
    public final boolean removeColorChangeListener(ColorChangeListener colorChangeListener) {
        return mColorChangeListeners.remove(colorChangeListener);
    }

    /**
     * Calculate the essential geometry given an event.
     *
     * @param event Motion event to compute geometry for, most likely a touch.
     * @return EssentialGeometry value.
     */
    protected abstract EssentialGeometry essentialGeometry(MotionEvent event);

    /***
     * Calculate the angle of the selection on color wheel given a touch.
     *
     * @param touchX Horizontal position of the touch event.
     * @param touchY Vertical position of the touch event.
     * @return Angle of the touch, in radians.
     */
    protected float getTouchAngle(float touchX, float touchY) {
        // NOTE: This function REQUIRES that you properly use mCenterX, mCenterY, etc.

        // Assumes (for cardinal directions on the color wheel):
        // [ E => 0, South => Pi/2, W => -Pi, N => -Pi/2 ]

        // However, you can override this function in ColorPickerView
        // with your own angle mappings if you desire.
        return (float) Math.atan2(touchY - mCenterY, touchX - mCenterX);
    }

    /**
     * Converts an angle on the wheel to a color.
     *
     * @param angle Position on the wheel in radians.
     * @return Color corresponding to that position as RGB.
     * @see AbstractColorPickerView#getTouchAngle(float, float)
     */
    @ColorInt
    public static int getColorFromAngle(double angle) {
        float hue = ((float) Math.toDegrees(angle) + 360 + 90) % 360;
        return Color.HSVToColor(new float[]{ hue, 1f, 1f });
    }

    /**
     * Method that will notify all the registered listeners that the color has changed
     * @param color The new color
     */
    protected void invokeColorChangeListeners(int color) {
        mColorChangeListeners.forEach(l -> l.onColorSelected(color));
    }

    /**
     * Your model should be private to the application, but the application needs a way to set
     * the color of the ColorPickerView, so we provide a setter of the color for the app
     */
    public abstract void setColor(@ColorInt int newColor);

    /** From here on out, this is boilerplate. */
    public AbstractColorPickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setImageResource(R.drawable.color_wheel);
        mColorChangeListeners = new ArrayList<>();
    }

}