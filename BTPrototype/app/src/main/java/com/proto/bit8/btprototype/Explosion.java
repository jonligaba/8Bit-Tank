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

/**
 * Class: Explosion
 * Description: Class which handles the animation of explosion after a
 *              missile collides with an object.
 */
public class Explosion extends GameObject
{
    //Visual attributes
    private Boolean     visible;
    private Animation   animation;

    /**
     * Method: Explosion
     * Description: Constructor which initializes animation attributes
     * @param bmp   -   Reference to spritesheet bitmap
     * @param bmpW -   Bitmap Width
     * @param bmpH -   Bitmap Height
     * @param sX   -   Bitmap Scale X
     * @param sY   -   Bitmap Scale Y
     * @param Scale -   Indicator of whether or not to scale bitmap
     * @param frames-   Frame count for animation
     */
    public Explosion(Bitmap bmp, int bmpW, int bmpH, int sX, int sY, boolean Scale, int frames) {
        //Initialize attributes
        Bitmap[]    m_array = new Bitmap[frames];
        Bitmap[]    tmparray = new Bitmap[frames];

        animation = new Animation();

        x = 0;
        y = 0;
        visible = false;
        width = Scale ? sX : bmpW;
        height = Scale ? sY : bmpH;

        for(int i = 0; i < frames; ++i) {
            //Split spritesheet into separate frames
            tmparray[i] = bmp == null ? null : Bitmap.createBitmap(bmp, i*bmpW, 0, bmpW, bmpH);

            if(Scale) {
                //Scale bitmap
                m_array[i] = tmparray[i] == null ? null :
                        Bitmap.createScaledBitmap(tmparray[i], sX, sY, false);
            } else {
                //normal size
                m_array[i] = tmparray[i];
            }
        }
        animation.setDelay(100);
        animation.setFrames(m_array);
    }

    /**
     * Method: update
     * Description: Method which updates the explosion's animation
     */
    public void update() {
        if(visible) {
            animation.update();

            if(animation.hasCompleted()) {
                visible = false;
            }
        }
    }

    /**
     * Method: draw
     * Description: Method which draws the animation onto application canvas
     * @param canvas    -   Application's canvas
     */
    public void draw(Canvas canvas) {
        try {
            if(visible) {
                canvas.drawBitmap(animation.getImage(), x, y, null);
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    /*          Getters and Setters         */

    public void setVisible(boolean Visible) {
        visible = Visible;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setXY(int X, int Y) {
        x = X;
        y = Y;
    }
}