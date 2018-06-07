package com.app.hoocons.ipainting.Entities;

import android.graphics.Path;

/**
 * Created by hungnguyen on 6/6/18.
 * At: 8:15 PM
 * Package: ${PACKAGE_NAME}
 */

/*
* Brush stroke represents each drawing stroke that the user has made
* Each brush will have its own simple and advance characteristics
* such as type, path, color, and stroke width
*
* @type: Will be used to tell is this stroke is a shape or a free line
* @color: what should be the stroke's color
* @path: its path to draw to canvas
* @width: size of the stroke
* */
public class BrushStroke {

    public enum Type {
        FREE, LINE, RECTANGLE, SQUARE,
    }

    private int color;
    private int width;
    private Path path;
    private Type type;

    public BrushStroke(int color, int width, Path path) {
        this.color = color;
        this.width = width;
        this.path = path;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
}
