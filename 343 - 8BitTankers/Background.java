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
 * Class: Background
 * Description: Class which handles drawing and updating of the current Background
 */
public class Background
{
    //Reference to wallpaper bitmap
    private Bitmap  wallpaper;

    /**
     * Method: Background
     * Description: Constructor which initializes values and
     *              scales wallpaper to fit the device screen
     * @param bg    - Bitmap referencing background image
     * @param sX    - Scale X
     * @param sY    - Scale Y
     * @param Scale - Indicating whether or not scaling should occur
     */
    public Background(Bitmap bg, int sX, int sY, boolean Scale) {
        if(Scale) {
            //scale wallpaper
            wallpaper = Bitmap.createScaledBitmap(bg, sX, sY, false);
        } else {
            //no scaling
            wallpaper = bg;
        }
    }

    /*  Reserved for possible future use
    public void update() {

    }
    */

    /**
     * Method: draw
     * Description: Draws background bitmap onto canvas
     * @param canvas - Reference to the application canvas
     */
    public void draw(Canvas canvas) {
        canvas.drawBitmap(wallpaper, 0, 0, null);
    }
}
