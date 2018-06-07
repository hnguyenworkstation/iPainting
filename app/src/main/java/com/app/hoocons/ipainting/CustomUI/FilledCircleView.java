package com.app.hoocons.ipainting.CustomUI;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.app.hoocons.ipainting.Helpers.Constants;
import com.app.hoocons.ipainting.R;

/**
 * Created by hungnguyen on 6/7/18.
 * At: 11:28 AM
 */

/*
* FilledCircleView will be used to represent a color which it is filled of
* -> mostly used for color picker's viewholder
* */
public class FilledCircleView extends View {
    private int color;
    private int padding;
    private Paint paint;

    public FilledCircleView(Context context) {
        super(context, null);
    }

    public FilledCircleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        /* initial values */
        TypedArray attributes = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.FilledCircle,
                0, 0);

        try {
            color = attributes.getInteger(R.styleable.FilledCircle_filled_color, Constants.DEFAULT_PAINT_COLOR);
            padding = attributes.getInteger(R.styleable.FilledCircle_internal_padding, Constants.DEFAULT_CIRCLE_PADDING);
        } finally {
            attributes.recycle();
        }

        /* init painter */
        paint = new Paint();
        paint.setAntiAlias(true);
    }

    public int getColor() {
        return color;
    }


    /**
     * Sets color.
     * Update color to the circle and redraw
     *
     * @param color new color
     */
    public void setColor(int color) {
        this.color = color;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Prepare paint
        paint.setColor(color);

        // Calculate properties of the circle
        float width = getWidth() - (2 * padding);
        float height = getHeight() - (2 * padding);

        float rad = Math.min(width, height) / 2;

        float centerX = padding + (width / 2);
        float centerY = padding + (height / 2);

        // Draw circle
        canvas.drawCircle(centerX, centerY, rad, paint);
    }
}
