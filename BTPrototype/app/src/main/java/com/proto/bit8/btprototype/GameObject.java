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

import android.graphics.Rect;

/**
 * Class: GameObject
 * Description: Abstract class which will handle object manipulation and collisions
 */
public abstract class GameObject {
    protected int   x;
    protected int   y;
    //Change in X/Y
    protected int   dx;
    protected int   dy;
    protected int   LimitX;

    protected int   width;
    protected int   height;

    /**
     * Method: getRect
     * @return - RECT which represents the coordinates+width/height
     */
    public Rect getRect() {
        return new Rect(x, y, x+width, y+height);
    }

    /**
     * Method: Collision
     * @param obj - Reference to another GameObject
     * @return  - True if objects collide, false otherwise
     */
    public boolean Collision(GameObject obj, int Tolerance)
    {
        Rect objRect = new Rect(obj.getRect());

        objRect.left -= Tolerance;
        objRect.right += Tolerance;
        return getRect().intersect(objRect);
    }

    /**
     * Method: getCollisionSide
     * Description: Method which will be used to check which side of the object has
     *              intersected with the object.
     * @param obj   - Object collided with
     * @return  - left(0), top(1), right(2), bottom(3)
     */
    public int getCollisionSide(GameObject obj) {
        Rect    objRect = new Rect(obj.getRect());
        Rect    thisRect = getRect();

        //Check for left-Right collision
        if((thisRect.top >= objRect.top && thisRect.top <= objRect.bottom) ||
                (thisRect.bottom <= objRect.bottom && thisRect.bottom >= objRect.top)) {

            if(thisRect.right >= objRect.left && thisRect.right <= objRect.right) {
                //Right wall collision
                return 2;
            }
            else if(thisRect.left <= objRect.right && thisRect.left >= objRect.left) {
                //Left wall collision
                return 0;
            }
        }
        //Check for top-bottom collision
        else if((thisRect.left >= objRect.left && thisRect.left <= objRect.right) ||
                (thisRect.right >= objRect.left && thisRect.right <= objRect.right)) {
            if(thisRect.top >= objRect.top && thisRect.top <= objRect.bottom) {
                //Top wall collision
                return 1;
            }
            else if(thisRect.bottom >= objRect.top && thisRect.bottom <= objRect.bottom) {
                //Bottom wall collision
                return 3;
            }
        }
        return -1;
    }

    /*      Getters and Setters     */

    public void setX(int X) {
        x = X;
    }

    public void setY(int Y) {
        y = Y;
    }

    public void setXY(int X, int Y) {
        x = X;
        y = Y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
