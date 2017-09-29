package com.speechpro.onepass.framework.ui.view.graphics;

import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.Objects;

/**
 * @author volobuev
 * @since 11.10.16
 */
public class Circle {

    //Circle center
    private final float x;
    private final float y;
    //Radius
    private final float r;

    public Circle(float x, float y, float r) {
        this.x = x;
        this.y = y;
        this.r = r;
    }

    public void draw(Canvas canvas, Paint paint) {
        canvas.drawCircle(x, y, r, paint);
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getR() {
        return r;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (!(o instanceof Circle)) { return false; }
        Circle circle = (Circle) o;
        return Float.compare(circle.x, x) == 0 &&
               Float.compare(circle.y, y) == 0 &&
               Float.compare(circle.r, r) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, r);
    }
}
