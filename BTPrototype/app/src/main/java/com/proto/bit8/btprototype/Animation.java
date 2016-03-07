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

/**
 * Class: Animation
 * Description: This class handles updating of Bitmaps using Sprite Sheets
 */
public class Animation
{
    //Reference to different bitmaps containing each frame
    private Bitmap[]    frames;
    private int         currFrame;
    private long        startTime;
    private long        delay;
    private boolean     completed;

    /**
     * Method: setFrames
     * @param Frames - An array of Bitmaps which make up the animation
     */
    public void setFrames(Bitmap[] Frames)
    {
        //Assign bitmaps
        frames = Frames;
        //Reset timing
        currFrame = 0;
        startTime = System.nanoTime();
    }

    /**
     * Method: getImage
     * @return  -   Returns current frame image
     */
    public Bitmap getImage() {
        return frames[currFrame];
    }

    /**
     * Method: setDelay
     * @param Delay - Delay between switching bitmaps
     */
    public void setDelay(long Delay) {
        delay = Delay;
    }

    /**
     * Method: setFrame
     * @param cbFrame - Sets the current animation's frame index
     */
    public void setFrame(int cbFrame) {
        currFrame = cbFrame;
    }

    /**
     * Method: update
     * Description: Updates the animation if elapsed time permits
     */
    public void update() {
        //Get current time in Miliseconds
        long    elapsed_time = (System.nanoTime()-this.startTime)/1000000;

        if(elapsed_time >= delay) {
            //Elapsed time passed delay, update animation frame
            currFrame++;

            //Check if last frame has been reached
            if(currFrame == frames.length) {
                //Reset animation
                currFrame = 0;
                completed = true;
            }
            //Update start time
            startTime = System.nanoTime();
        }
    }

    /**
     * Method: hasCompleted
     * @return - Returns boolean indicating if animation finished running once
     */
    public boolean hasCompleted() {
        if(completed) {
            completed = false;
            return true;
        }
        return false;
    }
}
