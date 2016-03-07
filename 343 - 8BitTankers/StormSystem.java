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
import android.graphics.Matrix;
import android.graphics.Paint;

import java.util.Random;

/**
 * Class: StormSystem
 * Description: Class which handles weather events such as Winds and Thunder
 */
public class StormSystem {
    private Paint   paint;
    private Random  rand;
    private Bitmap  Compass;
    //Storm Run Attributes
    private int     stormType;
    private int     ActiveTime;
    private int     CurrentTime;
    //Wind Attributes
    private int     windAngle;
    private int     windMagnitude;

    /**
     * Method: Storm System
     * Description: Constructor which initializes attributes
     */
    public StormSystem(Bitmap compass) {
        //Initialize attributes
        paint = new Paint();
        rand = new Random();
        stormType = 0;
        windAngle = 0;
        windMagnitude = 0;
        Compass = compass;
    }

    /**
     * Method: update
     * Description: Method which randomly generates a storm with
     *              random attributes
     */
    public void update() {
        int Seed = rand.nextInt(100);

        if(stormType == 0) {
            if (Seed <= 25) {
                //Enable wind system
                stormType = 1;
                //Generate random attributes
                windAngle = rand.nextInt(360);
                windMagnitude = rand.nextInt(30);
            } else if (Seed <= 50) {
                //TODO: Implement a new type of storm
                stormType = 2;
            }
            //Generate active amount of turns before disabling
            ActiveTime = (int)(System.nanoTime()/1000000) + rand.nextInt(10000) + 5000;
        } else {
            CurrentTime = (int)(System.nanoTime()/1000000);

            if(CurrentTime >= ActiveTime) {
                //Reset all attributes
                CurrentTime = 0;
                ActiveTime = 0;
                stormType = 0;
            }
        }
    }

    /**
     * Method: draw
     * Description: Method used to draw storm effects on screen.
     * @param canvas    -   Application's canvas
     */
    public void draw(Canvas canvas, int MidX, float ScaleX) {
       if(stormType == 1) {
           Matrix matrix = new Matrix();
           matrix.postRotate(windAngle);
            // create a new bitmap from the original using the matrix to transform the result
           Bitmap   rotCompass = Bitmap.createBitmap(Compass , 0, 0, Compass.getWidth(),
                   Compass.getHeight(), matrix, true);

           canvas.drawBitmap(rotCompass, MidX-rotCompass.getWidth()/2, 50, paint);
           paint.setStrokeWidth(ScaleX * 15.0f);
           canvas.drawText(Integer.toString(windMagnitude), MidX-10, rotCompass.getHeight()+10, paint);
       }
    }

    /*          Getters and Setters         */

    public int[] getStormParameters() {
        int[]   stormParams = {stormType, ActiveTime, windAngle, windMagnitude};

        return stormParams;
    }

    public void setStormParameters(int[] StormParams) {
        stormType = StormParams[0];
        ActiveTime = StormParams[1];
        windAngle = StormParams[2];
        windMagnitude = StormParams[3];
    }

    public int getStormType() {
        return stormType;
    }
}
