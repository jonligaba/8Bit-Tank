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
import android.graphics.Paint;
import android.graphics.Rect;

import java.util.Random;

/**
 * Class: SpecialItem
 * Description: This class will represent a special item which will
 *              randomly appear upon players.
 */
public class SpecialItem extends GameObject {
    private int         Type;   //Type of ITEM 0=HEALTH, 1=SHIELD, 2=DMG
    private int         RectID;
    private Bitmap[]    ItemBmp;
    private boolean     Visible;
    private Rect[]      Boundaries;

    private Paint       paint;
    private Random      random;
    /**
     * Method: SpecialItem
     * Description: Constructor which will assign initial values.
     * @param SpecItemBmp
     */
    public SpecialItem(Bitmap[] SpecItemBmp, int W, int H, Rect[] Bounds) {
        paint = new Paint();
        random = new Random();
        ItemBmp = SpecItemBmp;
        Boundaries = Bounds;
        Visible = false;
        height = H;
        width = W;
    }

    /**
     * Method: update
     * Description: This method will control toggling of the item and
     *              position updating.
     */
    public void update() {
        if(Visible == false) {
            int Seed = random.nextInt(100);

            if (Seed < 25) { //25% chance of enabling
                Type = random.nextInt(3); //Randomly set Item Type
                //Setup Initial Position
                y = 0;
                RectID = random.nextInt(2); //Get bounded RECT to fall on
                x = Boundaries[RectID].left + //Fall between a RECT
                        random.nextInt(Boundaries[RectID].right-Boundaries[RectID].left);
                Visible = true; //Begin drawing
            }
        }
        else {
            if(this.getRect().bottom < (Boundaries[RectID].top-5)) {
                //Already visible, just lower the position
                y += 2;
            }
        }
    }

    /**
     * Method: draw
     * Description: Method which will handle the drawing of this object
     *              in the game.
     * @param canvas - Applications' canvas
     */
    public void draw(Canvas canvas) {
        if(Visible) {
            canvas.drawBitmap(ItemBmp[Type], x, y, paint);
        }
    }

    /**
     * Method: Toggle
     * Description: method used to toggle special item event usually when
     *              playing bluetooth and other player toggles this event.
     * @param type  - type of special item
     * @param rectid - Bounded Rect ID
     * @param X - X Coordinate
     */
    public void Toggle(int type, int rectid, int X) {
        x = X;
        y = 0;
        Type = type;
        RectID = rectid;
        Visible = true;
    }

    /**
     * Method: isVisible
     * Description: Method used to return boolean indicating visible attribute.
     * @return - Objects visibility
     */
    public boolean isVisible() {
        return Visible;
    }

    /*      Getters and Setters         */
    public int getType() {
        return Type;
    }

    public void setVisible(boolean vis) {
        Visible = vis;
    }

    public int getRectID() {
        return RectID;
    }
}
