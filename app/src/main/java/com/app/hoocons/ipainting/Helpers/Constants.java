package com.app.hoocons.ipainting.Helpers;

import android.graphics.Color;

/**
 * Created by hungnguyen on 6/6/18.
 */

public class Constants {
    // Default properties of the paint view
    public static final int DEFAULT_PAINT_COLOR = Color.BLACK;
    public static final int DEFAULT_BACKGROUND_COLOR = Color.WHITE;
    public static final int DEFAULT_BRUSH_STROKE_SIZE = 20;
    public static final int DEFAULT_CIRCLE_PADDING = 8;


    // List of possible actions for the drawing activity
    public enum Action {
        ERASING, PAINTING
    }
}
