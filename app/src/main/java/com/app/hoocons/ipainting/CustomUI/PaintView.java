package com.app.hoocons.ipainting.CustomUI;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.app.hoocons.ipainting.Entities.BrushStroke;
import com.app.hoocons.ipainting.Helpers.Constants;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;

/**
 * Created by hungnguyen on 6/6/18.
 * At: 7:16 PM
 */


/*
* PaintView is a custom view at a higher level which allow user to draw lines/shapes on it
* -- PaintView will be able to listen to the touching events that happen to the view's surface
* and then collect those drawing path and display it later on
* */
public class PaintView extends View {
    private final String TAG = this.getClass().getSimpleName();

    private Paint mPaint;

    private int currentPaintColor;
    private int backgroundColor;
    private int brushWidth;

    // Keep track of the path that user is currently drawing (latest touching action down)
    private Path mDrawingPath;

    // Stack - Collections of strokes that have been drew to the view
    private Deque<BrushStroke> paintStrokes;

    // Stack - Collections of strokes that have been removed by users
    private Deque<BrushStroke> removedStrokes;

    // Keeping track of which position was last touched on the screen's surface
    private float lastX, lastY;

    // Current action determine what the painter should be doing when the touch event happens
    private Constants.Action mCurrentAction;

    public PaintView(Context context) {
        super(context, null);
    }

    public PaintView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        initPaint();
    }

    private void initPaint() {
        mPaint = new Paint();

        mPaint.setColor(Constants.DEFAULT_PAINT_COLOR);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAlpha(0xff);

        mPaint.setAntiAlias(true);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setXfermode(null);
        mPaint.setStrokeWidth(Constants.DEFAULT_BRUSH_STROKE_SIZE);
    }

    /*
    * Initialize needed variables to be able to draw stuffs
    * */
    public void init(int color, int size) {
        paintStrokes = new ArrayDeque<>();
        removedStrokes = new ArrayDeque<>();
        mDrawingPath = new Path();

        currentPaintColor = color;
        backgroundColor = Constants.DEFAULT_BACKGROUND_COLOR;
        brushWidth = size;

        mPaint.setColor(currentPaintColor);
        mPaint.setStrokeWidth(brushWidth);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // Drawing the screen background
        canvas.drawColor(backgroundColor);

        // Drawing the strokes that previously drew to the screen
        Iterator iterator = paintStrokes.descendingIterator();

        while (iterator.hasNext()) {
            BrushStroke stroke = (BrushStroke) iterator.next();
            mPaint.setColor(stroke.getColor());
            mPaint.setStrokeWidth(stroke.getWidth());
            canvas.drawPath(stroke.getPath(), mPaint);

            // After drawing the previous stroke -- set back the properties of painter to current
            if (mCurrentAction == Constants.Action.PAINTING) {
                mPaint.setColor(currentPaintColor);
            } else {
                mPaint.setColor(backgroundColor);
            }

            mPaint.setStrokeWidth(brushWidth);
        }

        // Drawing the current drawing stroke
        canvas.drawPath(mDrawingPath, mPaint);
    }

    /*
    * Clear the view
    * */
    public void clear() {
        paintStrokes.clear();
        removedStrokes.clear();

        invalidate();
    }


    /**
     * Update brush color.
     *
     * @param color new color for the brush
     */
    public void changeColor(int color) {
        mPaint.setColor(color);
        currentPaintColor = color;
    }


    /**
     * Update width of the brush
     *
     * @param brushWidth the brush width
     */
    public void changeBrushSize(int brushWidth) {
        mPaint.setStrokeWidth(brushWidth);
        this.brushWidth = brushWidth;
    }

    /**
     * Change action.
     *
     * Update the action for paint
     * -- If the action is erasing, we need to make the paint has the same color as the background
     *
     * @param action the action
     */
    public void changeAction(Constants.Action action) {
        mCurrentAction = action;

        if (action == Constants.Action.PAINTING) {
            mPaint.setColor(currentPaintColor);
        } else if (action == Constants.Action.ERASING) {
            mPaint.setColor(backgroundColor);
        }
    }


    /**
     * Go back one step.
     * Allow user to remove the latest path displayed on the screen
     * --> removed first item in the paint strokes stack (latest painted stroke)
     * --> then temporary store that stroke into the removedStroke stack
     */
    public void goBackOneStep() {
        if (paintStrokes.size() > 0) {
            removedStrokes.push(paintStrokes.removeFirst());
            invalidate();
        }
    }


    /*
    * Go forward one step
    * Reverse one step forward if user mistakenly went back one step
    * --> pop the item in removed strokes stack and push back to the paint strokes
    * */
    public void goForwardOneStep() {
        if (removedStrokes.size() > 0) {
            paintStrokes.push(removedStrokes.removeFirst());
            invalidate();
        }
    }

    /*
    * onTouchEvent:
    *
    * The view will be capture all touching event that happen to the view's surface
    * -> Action Down: user wants to make a new stroke, so display the current drawing stroke to the
    * screen so that can see what he/she is drawing
    *
    * -> Action Move: user wants to make shape for the stroke looking good, also need to update the
    * current drawing stroke to the screen. Add a quadratic bezier from the last point, approaching
    * control point (x1,y1), and ending at (x2,y2). Using quadTo for path tempting to make the curved
    * line so it looks smoother when making turn in example.
    *
    * -> Action Up: Finally, the user has finished the current drawing line, so we need to collect
    * the current path and store it into the collection of strokes by pushing to the stack of strokes,
    * then display the whole updated strokes collection again.
    * */
    @Override
    @SuppressLint("ClickableViewAccessibility")
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                /* When user started drawing a new stroke -- clear the temporary removed strokes */
                removedStrokes.clear();

                mDrawingPath.moveTo(event.getX(), event.getY());
                lastX = event.getX();
                lastY = event.getY();
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                /*
                * the ending point of quad (x2, y2) is the average point between the current touching [x,y] coordinate and last touching coordinate
                * because it make the line looks smoother when we make a turn on the line
                * Else, if we use the current touching [x,y] as the ending point of quad,
                * the line will have a sharp point of any peak when we make a line curving
                * */
                mDrawingPath.quadTo(lastX, lastY, (lastX + event.getX()) / 2, (lastY + event.getY()) / 2);
                lastX = event.getX();
                lastY = event.getY();
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                mDrawingPath.lineTo(event.getX(), event.getY());

                // Save the current stroke to the list and reset the current drawing path
                Path savedPath = new Path();
                savedPath.addPath(mDrawingPath);

                if (mCurrentAction == Constants.Action.PAINTING) {
                    paintStrokes.push(new BrushStroke(currentPaintColor, brushWidth, savedPath));
                } else {
                    paintStrokes.push(new BrushStroke(backgroundColor, brushWidth, savedPath));
                }

                mDrawingPath.reset();
                invalidate();
                break;
            default:
                break;
        }

        return true;
    }
}
