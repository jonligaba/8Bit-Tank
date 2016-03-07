/**
 *      Project Description:
 *          Bit8 Tanks Bluetooth Prototype
 *
 *      Authors:
 *          Seursing, Jonathan
 *          Son, Il Won
 *          Gaba, Jonli Angelo
 *          Valdez, Oscar
 *          Justiniani, Ian
 *
 *      References/Documents:
 *      http://developer.android.com/guide/topics/connectivity/bluetooth.html
 */

package com.proto.bit8.btprototype;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * Class: ProgressBar
 * Description: A representation of a progress with progress handling
 */
public class ProgressBar {
    //Drawing attributes of class
    private Rect    ProgressRect;
    private Rect    BarRect;
    private Paint   paint;
    private int     BarColor;

    private int     OutlineColor;
    private int     Progress;
    private int     Min;
    private int     Max;

    /**
     * Method: ProgressBar
     * Description: Constructor which initializes progressbar attributes
     * @param left          -   left of ProgressBar
     * @param top           -   top of ProgressBar
     * @param right         -   right of ProgressBar
     * @param bottom        -   bottom of ProgressBar
     * @param outlinecolor  -   Outline Color of Progress Bar
     * @param barcolor      -   Color of Bar within Progress Bar
     * @param min           -   Minimum value of Progress Bar
     * @param max           -   Maximum value of Progress Bar
     */
    public ProgressBar(int left, int top, int right, int bottom,
                       int outlinecolor, int barcolor, int min, int max) {
        //Initialize attributes
        paint = new Paint();
        BarRect = new Rect();
        ProgressRect = new Rect();
        //Set RECT dimensions
        BarRect.set(left, top, right, bottom);
        //Set cooresponding colors
        BarColor = barcolor;
        OutlineColor = outlinecolor;
        //Set progress attributes
        Progress = min;
        Min = min;
        Max = max;
        //Set width of paint stroke
    }

    /**
     * Method: update
     * Description: class which is used to update the current progress of the ProgressBar
     */
    public void update() {
        ProgressRect.set(BarRect.left+2, BarRect.top+2,
                BarRect.left+2+((BarRect.right-BarRect.left-2)*Progress/Max), BarRect.bottom-2);
    }

    /**
     * Method: draw
     * Description: Class which is used to draw the ProgressBar onto the canvas
     * @param canvas - Reference to application Canvas
     */
    public void draw(Canvas canvas)
    {
        if(paint.getStrokeWidth() != 2.0f) {
            paint.setStrokeWidth(2.0f);
        }
        //Set color of Progress outline and draw
        paint.setColor(OutlineColor);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(BarRect, paint);
        //Set color of Progress Bar and draw
        paint.setColor(BarColor);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(ProgressRect, paint);
    }

    /*      Getters and Setters     */

    public void setProgress(int prog) {
        Progress = (prog >= Min && prog <= Max) ? prog :
                prog > Max ? Max : 0;
    }

    public int getProgress() {
        return Progress;
    }

    public void setMax(int max) {
        Max = max;
    }

    public int getMax() {
        return Max;
    }

    public void setMin(int min) {
        Min = min;
    }

    public int getMin() {
        return Min;
    }
}
