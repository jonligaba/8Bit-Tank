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
import android.view.SurfaceHolder;

/**
 * Class: MainThread
 * Description: This class handles the applications update/draw in specific intervals
 */

public class MainThread extends Thread {
    //surfaceholder     Reference to the main activity's holder
    //mainpanel         Reference to the main panel
    //canvas            Reference to the main activity's canvas
    private SurfaceHolder   surfaceholder;
    private MainPanel       mainpanel;
    private Canvas          canvas;
    private boolean         isIdle;

    /**
     * Method: MainThread
     * Description: Constructor which assigns values
     */
    public MainThread(SurfaceHolder holder, MainPanel panel) {
        super();
        surfaceholder = holder;
        mainpanel = panel;
        isIdle = false;
    }

    /**
     * Method: run (Override)
     * Description: This method calculates the elapsed time, and allows updating and drawing
     *              to occur under a certain amount of Frames per Second
     */
    @Override
    public void run() {
        long    BaseTime = 0;
        long    WaitTime = 0;
        long    Target = 100/3;
        int     FrameCount = 0;

        while(isIdle == false) {
            //Only update if not idle
            BaseTime = System.nanoTime();
            canvas = null;

            try {
                canvas = this.surfaceholder.lockCanvas();
                synchronized (surfaceholder) {
                    //Update canvas while frozen
                    mainpanel.update();
                    mainpanel.draw(canvas);
                }
            }
            catch (Exception exception) {

            }
            finally {
                if(canvas != null) {
                    try {
                        //Unlock canvas and display updates
                        surfaceholder.unlockCanvasAndPost(canvas);
                    }
                    catch(Exception exception) {
                        exception.printStackTrace();
                    }
                }
            }
            //Calculate amount of time to wait
            WaitTime = Target - ((System.nanoTime() - BaseTime) / 1000000);

            try {
                //Sleep for amount of time
                this.sleep(WaitTime);
            }
            catch (Exception exception) {}

            FrameCount++;

            if (FrameCount >= 30) {
                //Reached Frames per Second rate
                FrameCount = 0;
            }
        }
    }

    public void Sleep(long ms) {
        try {
            //Sleep for amount of time
            this.sleep(ms);
        }
        catch (Exception exception) {}
    }
}
