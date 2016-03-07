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

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

/**
 * Class: Button
 * Description: Class which handles creating and drawing buttons and
 *              button events.
 */
public class Button
{
    private Bitmap[]    button;
    private boolean     isVisible;
    private int         state;
    private int         width;
    private int         height;
    private int         x;
    private int         y;

    /**
     * Method: Button
     * Description: Constructor which will create a button down/up
     *              separation of the provided bitmap. Scaling will
     *              occur if necessary
     * @param bmp   -   Reference to Bitmap of button up and down
     * @param w     -   Width of button
     * @param h     -   Height of button
     * @param X     -   Point X of button
     * @param Y     -   Point Y of button
     * @param sX    -   Scale X of button
     * @param sY    -   Scale Y of button
     * @param scale -   indicator of whether or not to scale bitmap
     * @param visible   -   indicator of whether or not to draw button
     */
    public Button(Bitmap bmp, int w, int h, int X, int Y, int sX, int sY, boolean scale, boolean visible)
    {
        button = new Bitmap[2];
        //Initialize attributes
        x = X;
        y = Y;
        //state == 0 if up, 1 if down
        state = 0;
        isVisible = visible;
        width =  scale ? sX : w;    //Assign corresponding width
        height = scale ? sY : h;    //Assign corresponding height

        if(scale) {
            //Create scaled bitmaps of button up/down
            button[0] = bmp == null ? null : Bitmap.createScaledBitmap(
                    Bitmap.createBitmap(bmp, 0, 0, w, h), sX, sY, false);
            button[1] = bmp == null ? null : Bitmap.createScaledBitmap(
                    Bitmap.createBitmap(bmp, w, 0, w, h), sX, sY, false);
        }
        else {
            //Assign normal sized bitmaps of button up/down
            button[0] = bmp == null ? null : Bitmap.createBitmap(bmp, 0, 0, w, h);
            button[1] = bmp == null ? null : Bitmap.createBitmap(bmp, w, 0, w, h);
        }
    }

    /**
     * Method: isIntersect
     * Description: Method to determine of touch point intersects with button
     * @param tX    - Touch Point X
     * @param tY    - Touch Point Y
     * @return      - True if intersects, false otherwise
     */
    public boolean isIntersect(float tX, float tY) {
        //If X falls between left and right of button
        if((tX >= x) && (tX <= (x + width))) {
            //If Y falls between top and bottom of button
            if((tY >= y) && (tY <= (y + height))) {
                return true;
            }
        }
        return false;
    }

        /*  Reserved for possible use
    public void update()
    {

    }
    */

    /**
     * Method: draw
     * Description: draws the button on the application's canvas
     * @param canvas - Application's canvas
     */
    public void draw(Canvas canvas) {
        if(isVisible) {
            canvas.drawBitmap(button[state], x, y, null);
        }
    }

    /*      Getters and Setters     */

    public void setButtonState(int down) {
        state = down;
    }

    public int getButtonState() {
        return state;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    public boolean Visible() {
        return isVisible;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
