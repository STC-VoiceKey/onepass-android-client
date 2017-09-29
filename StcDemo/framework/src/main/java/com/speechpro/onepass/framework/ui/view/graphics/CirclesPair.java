package com.speechpro.onepass.framework.ui.view.graphics;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Pair;

/**
 * @author volobuev
 * @since 11.10.16
 */
public class CirclesPair extends Pair<Circle, Circle> {


    /**
     * Constructor for a CirclesPair.
     *
     * @param first  the first object in the Pair
     * @param second the second object in the pair
     */
    public CirclesPair(Circle first, Circle second) {
        super(first, second);
    }

    public void draw(Canvas canvas, Paint paint){
        first.draw(canvas, paint);
        second.draw(canvas, paint);
    }


}
