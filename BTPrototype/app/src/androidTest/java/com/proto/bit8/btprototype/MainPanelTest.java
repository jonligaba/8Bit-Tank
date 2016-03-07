package com.proto.bit8.btprototype;

import android.test.InstrumentationTestCase;
import android.view.MotionEvent;
import junit.framework.Assert;

public class MainPanelTest extends InstrumentationTestCase{
    private MainPanel       mPanel;

    @Override
    /**
     * Initialize MainPanel to handle Touch Events
     */
    protected void setUp() throws Exception {
        super.setUp();
        mPanel = new MainPanel(getInstrumentation().getContext(), null, null);
        mPanel.surfaceCreated(mPanel.getHolder());
    }

    /**
     * Test Touch_Release at a certain point
     * @throws Exception
     */
    public void testOnTouchEvent_ValidUP() throws Exception {
        //Send Touch Message to Panel
        mPanel.dispatchTouchEvent(MotionEvent.obtain(
                System.currentTimeMillis(),
                System.currentTimeMillis(),
                MotionEvent.ACTION_UP,
                150.0f,
                150.0f,
                0));
        //Verify predicted results
        Assert.assertEquals(MotionEvent.ACTION_UP, mPanel.getLastAction());
        Assert.assertEquals(150.0f, mPanel.getLastTouchX(), 0.0f);
        Assert.assertEquals(150.0f, mPanel.getLastTouchY(), 0.0f);
    }

    /**
     * Test Invalid Touch_Release at invalid point
     * @throws Exception
     */
    public void testOnTouchEvent_InvalidUP() throws Exception {
        //Send Touch Message to Panel
        mPanel.onTouchEvent(MotionEvent.obtain(
                        System.currentTimeMillis(),
                        System.currentTimeMillis(),
                        MotionEvent.ACTION_UP,
                        -150.0f,
                        150.0f,
                        0));
        //Verify Action type is invalid
        Assert.assertEquals(-1, mPanel.getLastAction());
    }

    /**
     * Verify Touch_Down event
     * @throws Exception
     */
    public void testOnTouchEvent_ValidDOWN() throws Exception {
        //Send Touch Message to Panel
        mPanel.dispatchTouchEvent(MotionEvent.obtain(
                System.currentTimeMillis(),
                System.currentTimeMillis(),
                MotionEvent.ACTION_DOWN,
                150.0f,
                150.0f,
                0));
        //Verify predicted results
        Assert.assertEquals(MotionEvent.ACTION_DOWN, mPanel.getLastAction());
        Assert.assertEquals(150.0f, mPanel.getLastTouchX(), 0.0f);
        Assert.assertEquals(150.0f, mPanel.getLastTouchY(), 0.0f);
    }

    /**
     * Verify invalid Touch_Down event at invalid coordinates
     * @throws Exception
     */
    public void testOnTouchEvent_InvalidDOWN() throws Exception {
        //Send Touch Message to Panel
        mPanel.dispatchTouchEvent(MotionEvent.obtain(
                System.currentTimeMillis(),
                System.currentTimeMillis(),
                MotionEvent.ACTION_DOWN,
                -150.0f,
                150.0f,
                0));
        //Verify Action is invalid
        Assert.assertEquals(-1, mPanel.getLastAction());
    }

    /**
     * Verify Valid Swipe Event
     * @throws Exception
     */
    public void testOnTouchEvent_ValidSwipe() throws Exception {
        //Send Touch Message to Panel
        mPanel.dispatchTouchEvent(MotionEvent.obtain(
                System.currentTimeMillis(),
                System.currentTimeMillis(),
                MotionEvent.ACTION_DOWN,
                100.0f,
                100.0f,
                0));
        //Verify predicted results
        Assert.assertEquals(mPanel.getLastAction(), MotionEvent.ACTION_DOWN);
        Assert.assertEquals(100.0f, mPanel.getLastTouchX(), 0.0f);
        Assert.assertEquals(100.0f, mPanel.getLastTouchY(), 0.0f);

        //Send Touch Message to Panel
        mPanel.dispatchTouchEvent(MotionEvent.obtain(
                System.currentTimeMillis(),
                System.currentTimeMillis(),
                MotionEvent.ACTION_MOVE,
                200.0f,
                100.0f,
                0));
        //Verify predicted results
        Assert.assertEquals(mPanel.getLastAction(), MotionEvent.ACTION_MOVE);
        Assert.assertEquals(200.0f, mPanel.getLastTouchX(), 0.0f);
        Assert.assertEquals(100.0f, mPanel.getLastTouchY(), 0.0f);

        //Send Touch Message to Panel
        mPanel.dispatchTouchEvent(MotionEvent.obtain(
                System.currentTimeMillis(),
                System.currentTimeMillis(),
                MotionEvent.ACTION_UP,
                300.0f,
                100.0f,
                0));
        //Verify predicted results
        Assert.assertEquals(MotionEvent.ACTION_UP, mPanel.getLastAction());
        Assert.assertEquals(300.0f, mPanel.getLastTouchX(), 0.0f);
        Assert.assertEquals(100.0f, mPanel.getLastTouchY(), 0.0f);
    }

    /**
     * Verify Invalid Swipe event
     * @throws Exception
     */
    public void testOnTouchEvent_InvalidSwipe() throws Exception {
        //Send Touch Message to Panel
        mPanel.dispatchTouchEvent(MotionEvent.obtain(
                System.currentTimeMillis(),
                System.currentTimeMillis(),
                MotionEvent.ACTION_DOWN,
                100.0f,
                100.0f,
                0));
        //verify Event is valid first
        Assert.assertEquals(MotionEvent.ACTION_DOWN, mPanel.getLastAction());
        Assert.assertEquals(100.0f, mPanel.getLastTouchX(), 0.0f);
        Assert.assertEquals(100.0f, mPanel.getLastTouchY(), 0.0f);
        //Send Touch Message to Panel
        mPanel.dispatchTouchEvent(MotionEvent.obtain(
                System.currentTimeMillis(),
                System.currentTimeMillis(),
                MotionEvent.ACTION_MOVE,
                150.0f,
                100.0f,
                0));
        //verify Event is valid first
        Assert.assertEquals(mPanel.getLastAction(), MotionEvent.ACTION_MOVE);
        Assert.assertEquals(150.0f, mPanel.getLastTouchX(), 0.0f);
        Assert.assertEquals(100.0f, mPanel.getLastTouchY(), 0.0f);
        //Send Touch Message to Panel
        mPanel.dispatchTouchEvent(MotionEvent.obtain(
                System.currentTimeMillis(),
                System.currentTimeMillis(),
                MotionEvent.ACTION_UP,
                -1,
                150.0f,
                0));
        //Verify Action is now Invalid
        Assert.assertEquals(-1, mPanel.getLastAction());
    }
}