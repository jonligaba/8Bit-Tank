package com.proto.bit8.btprototype;

import android.graphics.Bitmap;
import android.test.InstrumentationTestCase;

public class MenuHandlerTest extends InstrumentationTestCase {
    private MenuHandler menuHandler;
    private Bitmap[]    WallPapers;
    private Bitmap[]    TankBitmaps;
    private int[]       TankAttrHP;
    private int[]       TankAttrSP;
    private int[]       TankAttrDMG;
    private Bitmap[]    MenuBackgrounds;
    private Bitmap[]    MenuButtonImages;
    private static final int    SettingCount = 4;
    private static final int    TankCount = 5;

    @Override
    protected void setUp() throws Exception {
        MenuBackgrounds    = new Bitmap[5];
        MenuButtonImages   = new Bitmap[12];

        WallPapers          = new Bitmap[SettingCount];
        TankBitmaps         = new Bitmap[TankCount*2];
        TankAttrHP          = new int[TankCount*2];
        TankAttrSP          = new int[TankCount*2];
        TankAttrDMG         = new int[TankCount*2];
        //Initialize menu bitmaps
        for(int i = 0; i < 12; ++i) {
            MenuButtonImages[i] = null;

            if(i < 5) {
                MenuBackgrounds[i] = null;
            }
            if(i < 4) {
                WallPapers[i] = null;
            }
            if( i < 10) {
                TankBitmaps[i] = null;
            }
        }
        //300 Total points for attributes
        TankAttrHP[0]   = 100;
        TankAttrHP[1]   = 125;
        TankAttrHP[2]   = 75;
        TankAttrHP[3]   = 50;
        TankAttrHP[4]   = 25;
        //
        TankAttrSP[0]   = 100;
        TankAttrSP[1]   = 125;
        TankAttrSP[2]   = 75;
        TankAttrSP[3]   = 50;
        TankAttrSP[4]   = 25;
        //One point of DMG equivalent to 25 Points of other attributes
        TankAttrDMG[0]  = 4;
        TankAttrDMG[1]  = 2;
        TankAttrDMG[2]  = 6;
        TankAttrDMG[3]  = 8;
        TankAttrDMG[4]  = 1;

        menuHandler = new MenuHandler(1920, 1080, 1.0f, 1.0f, MenuBackgrounds,
                MenuButtonImages, null, null, null);
        //Assign menu resources
        menuHandler.setParamResources(TankBitmaps, 36, 36, 3, WallPapers,
                TankAttrHP, TankAttrSP, TankAttrDMG);
    }

    public void testHandleButtonEvent_CreateCPUGame() throws Exception {
        int[]   Params;
        //Select CPU Button
        Button  btn = menuHandler.getButton(0, 2);
        menuHandler.handleButtonEvent(btn.getX(), btn.getY(), null);
        //Verify Display is at match creation
        assertEquals(1, menuHandler.getMenu());
        //Begin match and verify game contents
        btn = menuHandler.getButton(1, 6);
        menuHandler.handleButtonEvent(btn.getX(), btn.getY(), null);
        Params = menuHandler.getGameParams();
        //Default Game Type
        assertEquals(1, Params[0]);
        //Default as Server
        assertEquals(1, Params[1]);
        //Default Map
        assertEquals(0, Params[2]);
        //Default Tank 1
        assertEquals(0, Params[3]);
        //Default Tank 2
        assertEquals(0, Params[4]);
        //Verify Menu has incremented
        assertEquals(2, menuHandler.getMenu());
    }

    public void testHandleButtonEvent_CreateCPUGameAlt() throws Exception {
        int[]   Params;
        //Select CPU Button
        Button  btn = menuHandler.getButton(0, 2);
        menuHandler.handleButtonEvent(btn.getX(), btn.getY(), null);
        //Verify Display is at match creation
        assertEquals(1, menuHandler.getMenu());
        //Change Map
        btn = menuHandler.getButton(1, 1);
        menuHandler.handleButtonEvent(btn.getX(), btn.getY(), null);
        //Attempt to switch Tank, but shouldnt change due to level limit
        btn = menuHandler.getButton(1, 2);
        menuHandler.handleButtonEvent(btn.getX(), btn.getY(), null);
        //Begin match and verify game contents
        btn = menuHandler.getButton(1, 6);
        menuHandler.handleButtonEvent(btn.getX(), btn.getY(), null);
        Params = menuHandler.getGameParams();
        //Default Game Type
        assertEquals(1, Params[0]);
        //Default as Server
        assertEquals(1, Params[1]);
        //Map changed
        assertEquals(1, Params[2]);
        //Default Tank 1
        assertEquals(0, Params[3]);
        //Default Tank 2
        assertEquals(0, Params[4]);
        //Verify Menu has incremented
        assertEquals(2, menuHandler.getMenu());
    }

    public void testHandleButtonEvent_ExitEndScreen() throws Exception {
        //Lose Screen
        menuHandler.setMenu(3);
        menuHandler.handleButtonEvent(0, 0, null);
        //Should be at main screen
        assertEquals(0, menuHandler.getMenu());
        //Win Screen
        menuHandler.setMenu(4);
        menuHandler.handleButtonEvent(0, 0, null);
        //Should be at main screen
        assertEquals(0, menuHandler.getMenu());
    }
}