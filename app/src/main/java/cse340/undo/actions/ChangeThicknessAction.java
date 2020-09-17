package cse340.undo.actions;

import android.graphics.Paint;
import android.support.annotation.NonNull;

import cse340.undo.app.DrawingView;

/**
 * Reversible action which changes the thickness of the DrawingView's paint.
 */
public class ChangeThicknessAction extends AbstractReversibleAction {
    /** The thickness that this action changes the current paint to. */
    private final int thickness;

    /** The thickness that this action changes the current paint from. */
    private float prev;

    /**
     * Creates an action that changes the paint thickness.
     *
     * @param thickness New thickness for DrawingView paint.
     * @throws IllegalArgumentException if thickness not positive.
     */
    public ChangeThicknessAction(int thickness) { this.thickness = thickness; }

    /** @inheritDoc */
    @Override
    public void doAction(DrawingView view) {
        super.doAction(view);
        Paint cur = view.getCurrentPaint();
        prev = cur.getStrokeWidth();
        cur.setStrokeWidth(thickness);

        // TODO: update the thickness in the view
        // TODO: store any information you'll need to undo this later
        // TODO: don't store any information you won't need

    }

    /** @inheritDoc */
    @Override
    public void undoAction(DrawingView view) {
        super.undoAction(view);
        view.getCurrentPaint().setStrokeWidth(prev);
        // TODO: update the thickness in the view
    }

    /** @inheritDoc */
    @NonNull
    @Override
    public String toString() {
        return "Change thickness to " + thickness;
    }
}
