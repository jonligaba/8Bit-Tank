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

public class Missile extends GameObject {
    //Visual Attributes
    private Boolean         visible;
    private Animation       animation;
    //Attributes corresponding to movement and damage
    private int         gravity;
    private int         acc_counter;
    private int         acceleration;
    private long        Timer;
    private float       ScaleX;
    private float       ScaleY;
    private boolean     hasBounced;
    /**
     * Method: Constructor
     * Description: Method used to initialize Missile visual attributes
     * @param bmp       - Spritesheet containing Missile frames
     * @param bmpW      - Bitmap Width
     * @param bmpH      - Bitmap Height
     * @param sX        - Scale X
     * @param sY        - Scale Y
     * @param Scale     - Boolean indicating whether or not to scale bitmap
     * @param Gravity   - Amount of change in Y to drag missile down
     * @param frames    - Amount of frames for Missile animation
     * @param scaleX    - scalar X for trajectory
     * @param scaleY    - scalar Y for trajectory
     */
    public Missile(Bitmap bmp, int bmpW, int bmpH, int sX, int sY, boolean Scale,
                   int Gravity, int frames, float scaleX, float scaleY) {
        //Initialize attributes
        Bitmap[]    m_array = new Bitmap[frames];
        Bitmap[]    tmparray = new Bitmap[frames];

        animation = new Animation();
        ScaleX = scaleX;
        ScaleY = scaleY;
        visible = false;
        hasBounced = false;
        width = Scale ? sX : bmpW;
        height = Scale ? sY : bmpH;
        Timer = System.nanoTime();
        gravity = Gravity;
        //Split and assign each frame for animations
        for(int i = 0; i < frames; ++i) {
            tmparray[i] = bmp == null ? null : Bitmap.createBitmap(bmp, i*bmpW, 0, bmpW, bmpH);

            if(Scale) {
                //Scale image
                m_array[i] = tmparray[i] == null ? null :
                        Bitmap.createScaledBitmap(tmparray[i], sX, sY, false);
            } else {
                //normal size
                m_array[i] = tmparray[i];
            }
        }
        //Assign animation frames
        animation.setFrames(m_array);
        animation.setDelay(100);
    }

    /**
     * Method: update
     * Description: method used to update missile position and animation
     */
    public void update() {
        //Draw only if visible
        if(visible) {
            if(((System.nanoTime()-Timer)/1000000) > 25) {
                //Physics handling
                y -= Math.round(ScaleY * dy);
                x += Math.round(ScaleX * dx);

                Timer = System.nanoTime();
                dy -= acceleration;
                acc_counter++;

                animation.update();

                //Reset gravitational counter
                if(acc_counter >= 8 && dy >= (gravity * -1)) {
                    acceleration++;
                    acc_counter = 0;
                }
            }
        }
        else {
            acceleration = 0;
        }
    }

    /**
     * Method: draw
     * Description: Method used to draw missile on canvas
     * @param canvas    - Reference to surface holder's context
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

    /**
     * Method: setdXdY
     * Description: Method used to calculate and set travel change in X/Y
     * @param Angle     -   Angle of shot
     * @param Magnitude -   Magnitude of shot
     * @param PlayerA   -   Indicate which player originated shot
     */
    public void setdXdY(int Angle, double Magnitude, boolean PlayerA) {
        double angle = 1.0 * Angle;

        dx = (int)Math.round(Magnitude * Math.cos(Math.toRadians(90-angle)));
        dy = (int)Math.round(Magnitude * Math.sin(Math.toRadians(90-angle)));
        //Reverse direction if player B
        dx *= PlayerA ? 1 : -1;
    }

    /**
     * Method: appenddXdY
     * Description: Method used to append external force to travel ie: wind
     * @param angle     -   Angle of external force
     * @param Magnitude -   Magnitude of external force
     */
    public void appenddXdY(int angle, double Magnitude) {
        int newdY = (int)Math.round(Magnitude * Math.sin(Math.toRadians(90-angle)));

        dx += (int)Math.round(Magnitude * Math.cos(Math.toRadians(90-angle)));

        //Make sure storm does not send missile flying
        if(dy > 0 && newdY < (dy * -1)) {
            dy -= 1;
        }
        else if(dy < 0 && newdY < 0) {
            dy -= 1;
        }
        else {
            dy += newdY;
        }
    }

    /*          Getters and Setters         */

    public void setVisible(boolean Visible) {
        visible = Visible;
    }

    public boolean isVisible()
    {
        return visible;
    }

    public void setBounced(boolean bounce) {
        hasBounced = bounce;
    }

    public boolean bounced() {
        return hasBounced;
    }

    public int getdX() {
        return dx;
    }

    public int getdY() {
        return dy;
    }

    public void setdX(int DX) {
        dx = DX;
    }

    public void setdY(int DY) {
        dy = DY;
    }
}
