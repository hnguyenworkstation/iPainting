package com.app.hoocons.ipainting.CustomUI;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.app.hoocons.ipainting.Entities.BrushStroke;
import com.app.hoocons.ipainting.Helpers.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hungnguyen on 6/6/18.
 * At: 7:16 PM
 */


/*
* PaintView is a custom view at a higher level which allow user to draw lines/shapes on it
* -- PaintView will be able to listen to the touching events that happen to the view's surface
* and then collect those drawing path and display it later on
* Todo: update PaintView to listen to the properties changing from the DrawingFragment
* */
public class PaintView extends View {
    private final String TAG = this.getClass().getSimpleName();

    private Paint mPaint;
    private Path mDrawingPath;

    private Canvas mCanvas;

    private int currentPaintColor;
    private int backgroundColor;
    private int brushWidth;

    private List<BrushStroke> paintStrokes;

    private Bitmap mBitmap;

    /* This paint will be used to draw the bit map to the canvas layer */
    private final Paint drawingPaint = new Paint(Paint.DITHER_FLAG);

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

    public void init(DisplayMetrics metrics) {
        int height = metrics.heightPixels;
        int width = metrics.widthPixels;

        paintStrokes = new ArrayList<>();
        mDrawingPath = new Path();

        mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);

        currentPaintColor = Constants.DEFAULT_PAINT_COLOR;
        backgroundColor = Constants.DEFAULT_BACKGROUND_COLOR;
        brushWidth = Constants.DEFAULT_BRUSH_STROKE_SIZE;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // Drawing the screen background
        canvas.drawColor(backgroundColor);

        // Drawing the current drawing stroke
        canvas.drawPath(mDrawingPath, mPaint);

        // Drawing the strokes that previously drew to the screen
        for (BrushStroke stroke : paintStrokes) {
            mPaint.setColor(stroke.getColor());
            mPaint.setStrokeWidth(stroke.getWidth());
            canvas.drawPath(stroke.getPath(), mPaint);
        }
    }

    @Override
    @SuppressLint("ClickableViewAccessibility")
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                mDrawingPath.moveTo(event.getX(), event.getY());
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                mDrawingPath.lineTo(event.getX(), event.getY());
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                // Add current stroke to the list and reinitialize the current drawing path
                paintStrokes.add(new BrushStroke(currentPaintColor, brushWidth, mDrawingPath));
                invalidate();
                break;
        }

        return true;
    }
}
