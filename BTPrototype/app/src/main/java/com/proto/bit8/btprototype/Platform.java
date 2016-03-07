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
 * Class: Platform
 * Description: Class which servers as a floor for tanks and walls for missiles
 */
public class Platform extends GameObject {
    //Visual attributes of Platform
    private Rect    rect;
    private Paint   paint;
    private int     platcolor;
    private boolean isVisible;

    /**
     * Method: Platform
     * Description: Constructor which initializes attributes
     * @param Left      - left of platform
     * @param Top       - top of platform
     * @param Right     - right of platform
     * @param Bottom    - bottom of platform
     * @param color     - color of platform
     * @param vis       - visibility of platform
     */
    public Platform(int Left, int Top, int Right, int Bottom, int color, boolean vis) {
        //Initialize attributes
        rect = new Rect();
        paint = new Paint();

        x = Left;
        y = Top;
        width = Right-Left;
        height = Bottom-Top;
        platcolor = color;
        rect.set(Left, Top, Right, Bottom);
        isVisible = vis;
    }

    /**
     * Method: Platform
     * Description: Overloaded constructor which takes in RECT instead of dimensions
     * @param platform_rect - RECT of platform to be created
     * @param color         - Color of platform
     * @param vis           - visibility of platform
     */
    public Platform(Rect platform_rect, int color, boolean vis) {
        //Initialize attributes
        rect = new Rect();
        paint = new Paint();

        rect.set(platform_rect);
        x = rect.left;
        y = rect.top;
        width = rect.right-x;
        height = rect.bottom-y;
        platcolor = color;
        isVisible = vis;
    }

    /*
    public void update()
    {

    }
    */

    /**
     * Method: draw
     * Description: Method which draws the platform onto the application's canvas
     * @param canvas - Reference to application's canvas
     */
    public void draw(Canvas canvas)
    {
        if(isVisible) {
            //Draw Platform if visible
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(platcolor);
            canvas.drawRect(rect, paint);
        }
    }
}
