package com.proto.bit8.btprototype;

import android.test.InstrumentationTestCase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;


public class MatchHandlerTest extends InstrumentationTestCase {
    private MatchHandler    matchHandler;
    private Profile     profile;
    private int[][]     PlayerSpawnX;
    private int[][]     PlayerSpawnY;
    private int[][]     PlayerLimitX1;
    private int[][]     PlayerLimitX2;
    private int[][]     PlatColorList;
    private boolean[][] PlatformVisList;
    private Rect[][]    PlatformList;
    private int[]       TankAttrHP;
    private int[]       TankAttrSP;
    private int[]       TankAttrDMG;
    private Bitmap      CompassBmp;
    private Bitmap[]    WallPapers;
    private Bitmap[]    TankBitmaps;
    private Bitmap[]    CtrlButtons;
    private Bitmap[]    BulletButtons;
    private Bitmap[]    BulletMissiles;
    private Bitmap[]    BulletExplosions;
    private Bitmap[]    SpecialItemBmps;
    private static final int    SettingCount = 4;
    private static final int    TankCount = 5;

    @Override
    protected void setUp() throws Exception {
        PlayerSpawnX = new int[SettingCount][2];
        PlayerSpawnY = new int[SettingCount][2];
        PlayerLimitX1 = new int[SettingCount][2];
        PlayerLimitX2 = new int[SettingCount][2];
        PlatColorList = new int[SettingCount][];
        PlatformVisList = new boolean[SettingCount][];
        PlatformList = new Rect[SettingCount][];
        WallPapers = new Bitmap[SettingCount];
        TankBitmaps = new Bitmap[TankCount * 2];
        TankAttrHP = new int[TankCount * 2];
        TankAttrSP = new int[TankCount * 2];
        TankAttrDMG = new int[TankCount * 2];
        CtrlButtons = new Bitmap[5];
        BulletButtons = new Bitmap[4];
        BulletMissiles = new Bitmap[4];
        BulletExplosions = new Bitmap[4];
        SpecialItemBmps = new Bitmap[3];

        //Setup Player objects and attributes
        WallPapers[0] = null;
        WallPapers[1] = null;
        WallPapers[2] = null;
        WallPapers[3] = null;
        TankBitmaps[0] = null;
        TankBitmaps[1] = null;
        TankBitmaps[2] = null;
        TankBitmaps[3] = null;
        TankBitmaps[4] = null;
        TankBitmaps[5] = null;
        TankBitmaps[6] = null;
        TankBitmaps[7] = null;
        TankBitmaps[8] = null;
        TankBitmaps[9] = null;
        //300 Total points for attributes
        TankAttrHP[0] = 100;
        TankAttrHP[1] = 125;
        TankAttrHP[2] = 75;
        TankAttrHP[3] = 50;
        TankAttrHP[4] = 25;
        //
        TankAttrSP[0] = 100;
        TankAttrSP[1] = 125;
        TankAttrSP[2] = 75;
        TankAttrSP[3] = 50;
        TankAttrSP[4] = 25;
        //One point of DMG equivalent to 25 Points of other attributes
        TankAttrDMG[0] = 4;
        TankAttrDMG[1] = 2;
        TankAttrDMG[2] = 6;
        TankAttrDMG[3] = 8;
        TankAttrDMG[4] = 1;
        //Initialie in-game button Bitmaps
        for (int i = 0; i < 4; ++i) {
            CtrlButtons[i] = null;
            BulletButtons[i] = null;
            BulletMissiles[i] = null;
            BulletExplosions[i] = null;
            CompassBmp = null;
        }
        CtrlButtons[4] = null;
        //Initialize special item bitmaps
        SpecialItemBmps[0] = null;
        SpecialItemBmps[1] = null;
        SpecialItemBmps[2] = null;

        matchHandler = new MatchHandler(null, 1.0f, 1.0f, null, null);

        profile = new Profile("Test");
        profile.addExp(10);
        profile.setControlType(1);
        matchHandler.setProfile(profile);

        PlatformList[0] = new Rect[1];
        PlatformList[1] = new Rect[2];
        PlatformList[2] = new Rect[2];
        PlatformList[3] = new Rect[2];
        PlatformList[0][0] = new Rect(0, Math.round(300), 1920, 1080);
        PlatformList[1][0] = new Rect(0, Math.round(350), 1920, 1080);
        PlatformList[1][1] = new Rect(1920 / 2 - Math.round(180),
                1080/2 - Math.round(100), 1920 / 2 + Math.round(180),
                1080);
        PlatformList[2][0] = new Rect(0, Math.round(330), 550, 1080);
        PlatformList[2][1] = new Rect(Math.round(550),
                1080/2 + Math.round(140), 1920, 1080);
        PlatformList[3][0] = new Rect(0, 1080/2 + Math.round(140), 550,
                1080);
        PlatformList[3][1] = new Rect(Math.round(550), Math.round(350),
                1920, 1080);

        PlatformVisList[0] = new boolean[1];
        PlatformVisList[1] = new boolean[2];
        PlatformVisList[2] = new boolean[2];
        PlatformVisList[3] = new boolean[2];
        PlatformVisList[0][0] = false;
        PlatformVisList[1][0] = false;
        PlatformVisList[1][1] = false;
        PlatformVisList[2][0] = false;
        PlatformVisList[2][1] = false;
        PlatformVisList[3][0] = false;
        PlatformVisList[3][1] = false;

        PlatColorList[0] = new int[1];
        PlatColorList[1] = new int[2];
        PlatColorList[2] = new int[2];
        PlatColorList[3] = new int[2];

        PlatColorList[0][0] = Color.RED;
        PlatColorList[1][0] = Color.RED;
        PlatColorList[1][1] = Color.RED;
        PlatColorList[2][0] = Color.RED;
        PlatColorList[2][1] = Color.RED;
        PlatColorList[3][0] = Color.RED;
        PlatColorList[3][1] = Color.RED;

        PlayerSpawnX[0][0] = 0;
        PlayerSpawnX[1][0] = 0;
        PlayerSpawnX[2][0] = 0;
        PlayerSpawnX[3][0] = 0;

        PlayerSpawnX[0][1] = 1920 - Math.round(100);
        PlayerSpawnX[1][1] = 1920 - Math.round(100);
        PlayerSpawnX[2][1] = 1920 - Math.round(100);
        PlayerSpawnX[3][1] = 1920 - Math.round(100);

        PlayerSpawnY[0][0] = PlatformList[0][0].top - Math.round(100);
        PlayerSpawnY[1][0] = PlatformList[1][0].top - Math.round(50);
        PlayerSpawnY[2][0] = PlatformList[2][0].top - Math.round(100);
        PlayerSpawnY[3][0] = PlatformList[3][0].top - Math.round(100);

        PlayerSpawnY[0][1] = PlatformList[0][0].top - Math.round(100);
        PlayerSpawnY[1][1] = PlatformList[1][0].top - Math.round(50);
        PlayerSpawnY[2][1] = PlatformList[2][1].top - Math.round(100);
        PlayerSpawnY[3][1] = PlatformList[3][1].top - Math.round(100);

        PlayerLimitX1[0][0] = 0;
        PlayerLimitX1[1][0] = 0;
        PlayerLimitX1[2][0] = 0;
        PlayerLimitX1[3][0] = 0;

        PlayerLimitX1[0][1] = 0;
        PlayerLimitX1[1][1] = PlatformList[1][1].right;
        PlayerLimitX1[2][1] = PlatformList[2][1].left;
        PlayerLimitX1[3][1] = PlatformList[3][1].left;

        PlayerLimitX2[0][0] = 1920-Math.round(100);
        PlayerLimitX2[1][0] = PlatformList[1][1].left;
        PlayerLimitX2[2][0] = PlatformList[2][0].right-Math.round(100);
        PlayerLimitX2[3][0] = PlatformList[3][0].right-Math.round(100);

        PlayerLimitX2[0][1] = Math.round(100);
        PlayerLimitX2[1][1] = Math.round(100);
        PlayerLimitX2[2][1] = Math.round(100);
        PlayerLimitX2[3][1] = Math.round(100);

        Bitmap[]    chosenTanks = new Bitmap[2];
        int[]       chosenHP = new int[2];
        int[]       chosenSP = new int[2];
        int[]       chosenDMG = new int[2];

        //Retrieve game parameters from menu
        //[GameType][PrimaryPlayer][LevelID][PrimaryTankID][SecondaryTankID]
        int     p1Array[] = {0, 2, 4, 6, 8};
        int     p2Array[] = {1, 3, 5, 7, 9};
        int[]   params = {0, 0, 1, 0, 1};
        Rect[]  ItemRects = new Rect[params[2] > 1 ? 2 : 1];

        ItemRects[0] = new Rect(PlatformList[params[2]][0]);

        if(params[2] > 1) {
            ItemRects[1] = new Rect(PlatformList[params[2]][1]);
        }

        //Initialize game play resources
        matchHandler.InitResources(1920, 1080, WallPapers[params[2]],
                PlatformList[params[2]], PlatColorList[params[2]], PlatformVisList[params[2]],
                BulletButtons, BulletMissiles, BulletExplosions, CtrlButtons, CompassBmp,
                SpecialItemBmps, ItemRects);
        //Assign current game constraints
        matchHandler.SetGameConstraints(params[0] , PlayerSpawnX[params[2]],
                PlayerSpawnY[params[2]], PlayerLimitX1[params[2]], PlayerLimitX2[params[2]]);

        //Assign values to in-game attributes
        chosenTanks[0] = TankBitmaps[p1Array[params[3]]];
        chosenTanks[1] = TankBitmaps[p2Array[params[4]]];
        chosenHP[0] = params[3] == 4 ? params[5] : TankAttrHP[params[3]];
        chosenSP[0] = params[3] == 4 ? params[6] : TankAttrSP[params[3]];
        chosenDMG[0] = params[3] == 4 ? params[7] : TankAttrDMG[params[3]];
        chosenHP[1] = params[4] == 4 ? params[8] : TankAttrHP[params[4]];
        chosenSP[1] = params[4] == 4 ? params[9] : TankAttrSP[params[4]];
        chosenDMG[1] = params[4] == 4 ? params[10] : TankAttrDMG[params[4]];
        //Set game players
        matchHandler.SetGamePlayers(chosenTanks, 36, 36, 3,
                chosenHP, chosenSP, chosenDMG);
        //Reset match variables
        matchHandler.BeginMatch();
    }


    public void testHandleFireEvent_FireBtnDown() throws Exception {
        //Test within valid Ranges
        matchHandler.getButton(4).setButtonState(1);//Simulate Button Press
        matchHandler.getPowerBar().setProgress(0);
        matchHandler.HandleFireEvent();
        assertEquals(2, matchHandler.getPowerBar().getProgress());
        //Test below Minimum Capacity
        matchHandler.getButton(4).setButtonState(1);//Simulate Button Press
        matchHandler.getPowerBar().setProgress(-4);
        matchHandler.HandleFireEvent();
        assertEquals(2, matchHandler.getPowerBar().getProgress());
        //Test above Maximum Capacity
        matchHandler.getButton(4).setButtonState(1);//Simulate Button Press
        matchHandler.getPowerBar().setProgress(104);
        matchHandler.HandleFireEvent();
        assertEquals(0, matchHandler.getPowerBar().getProgress());
        assertEquals(0, matchHandler.getButton(4).getButtonState());
    }

    public void testHandleFireEvent_FireBtnRelease() throws Exception {
        int[]   AngleSet = {0, 25, 50, 100}; //0, 25 and 50 are valid
        int[]   BulletSet = {0, 1, 2, 4}; //0, 1 and 2 are valid
        int[]   ProgressSet = {0, 20, 40, 110}; //0, 20 and 40 are valid
        //Valid bullets range from 0 - 3
        for (int i = 0; i < 4; ++i) { //four missiles per set
            //Simulate Missile setup and fire
            matchHandler.setMissileStatus(false);
            matchHandler.getPowerBar().setProgress(ProgressSet[i]);
            matchHandler.getTank(0).setBullet(BulletSet[i]);
            matchHandler.getTank(0).setAngle(AngleSet[i]);
            matchHandler.getTank(1).setBullet(BulletSet[i]);
            matchHandler.getTank(1).setAngle(AngleSet[i]);
            //Missile should not be fired
            assertEquals(false,
                    matchHandler.getMissile(0, matchHandler.getTank(0).getBullet()).isVisible());
            //Verify Progress bar is properly sey
            assertEquals(i < 3 ? ProgressSet[i] : 100,
                    matchHandler.getPowerBar().getProgress());
            matchHandler.HandleFireEvent();
            //Verify that Bullet is within valid ranges
            assertEquals(i < 3 ? BulletSet[i] : 0, matchHandler.getTank(0).getBullet());
            //Verify that angle is within valid ranges
            assertEquals(i < 3 ? AngleSet[i] : 0, matchHandler.getTank(0).getAngle());
            //Missile should now be visible with assigned dX / dY
            assertEquals(i != 0,
                    matchHandler.getMissile(0, matchHandler.getTank(0).getBullet()).isVisible());
            if(i == 0) {
                //should have NULL delta XY
                assertEquals(0, matchHandler.getMissile(0, matchHandler.getTank(0).getBullet()).getdX());
                assertEquals(0, matchHandler.getMissile(0, matchHandler.getTank(0).getBullet()).getdY());
            }
        }
    }

    public void testPollCollisionEvent_Tank0() throws Exception {
        //Four different Bullets
        for(int i = 0; i < 4; ++i) {
            //Setup Scenario
            matchHandler.getTank(0).setHealth(100);
            matchHandler.getMissile(0, i).setVisible(true);
            //if i is two, bullet has blast radius of 50
            matchHandler.getMissile(0, i).setXY(matchHandler.getTank(0).getX() + i == 2 ? 50 : 0,
                    matchHandler.getTank(0).getY());
            //Explosion should be hidden
            assertNotSame(true, matchHandler.getExplosion(0, i).isVisible());
            //Process collision
            matchHandler.pollCollisionEvent();
            //Collision should make missile hidden
            assertEquals(false, matchHandler.getMissile(0, i).isVisible());
            //Tank should now have less health
            assertNotSame(100, matchHandler.getTank(0).getHealth());
            //Corresponding explosion should be visible
            assertEquals(true, matchHandler.getExplosion(0, i).isVisible());
        }
    }

    public void testPollCollisionEvent_Tank0Miss() throws Exception {
        //Four different Bullets
        for(int i = 0; i < 4; ++i) {
            //Setup Scenario
            matchHandler.getTank(0).setHealth(100);
            matchHandler.getMissile(0, i).setVisible(true);
            //if i is two, bullet has blast radius of 50
            matchHandler.getMissile(0, i).setXY(0, 0);
            //Explosion should be hidden
            assertNotSame(true, matchHandler.getExplosion(0, i).isVisible());
            //Process collision
            matchHandler.pollCollisionEvent();
            //No Collision, bullet should be visible
            assertEquals(true, matchHandler.getMissile(0, i).isVisible());
            //Tank should have same health
            assertEquals(100, matchHandler.getTank(0).getHealth());
            //Corresponding explosion should be hidden
            assertNotSame(true, matchHandler.getExplosion(0, i).isVisible());
        }
    }

    public void testPollCollisionEvent_Tank1() throws Exception {
        //Four different Bullets
        for(int i = 0; i < 4; ++i) {
            //Setup Scenario
            matchHandler.getTank(1).setHealth(100);
            matchHandler.getMissile(0, i).setVisible(true);
            matchHandler.getTank(1).setXY(100, 800);
            //if i is two, bullet has blast radius of 50
            matchHandler.getMissile(0, i).setXY(100, 800);
            //Explosion should be hidden
            assertNotSame(true, matchHandler.getExplosion(0, i).isVisible());
            //Process collision
            matchHandler.pollCollisionEvent();
            //Collision should make missile hidden
            assertEquals(false, matchHandler.getMissile(0, i).isVisible());
            //Tank should now have less health
            assertNotSame(100, matchHandler.getTank(1).getHealth());
            //Corresponding explosion should be visible
            assertEquals(true, matchHandler.getExplosion(0, i).isVisible());
        }
    }

    public void testPollCollisionEvent_Tank1Miss() throws Exception {
        //Four different Bullets
        for(int i = 0; i < 4; ++i) {
            //Setup Scenario
            matchHandler.getTank(1).setHealth(100);
            matchHandler.getMissile(0, i).setVisible(true);
            //if i is two, bullet has blast radius of 50
            matchHandler.getMissile(0, i).setXY(0, 0);
            //Explosion should be hidden
            assertNotSame(true, matchHandler.getExplosion(0, i).isVisible());
            //Process collision
            matchHandler.pollCollisionEvent();
            //No Collision, bullet should be visible
            assertEquals(true, matchHandler.getMissile(0, i).isVisible());
            //Tank should have same health
            assertEquals(100, matchHandler.getTank(1).getHealth());
            //Corresponding explosion should be hidden
            assertNotSame(true, matchHandler.getExplosion(0, i).isVisible());
        }
    }

    public void testPollCollisionEvent_SpecialItem() throws Exception {
        //Four different Bullets
        for(int i = 0; i < 4; ++i) {
            //Setup Scenario
            matchHandler.getSpecialItem().setXY(0, 0);
            matchHandler.getSpecialItem().setVisible(true);
            matchHandler.getMissile(0, i).setVisible(true);
            matchHandler.getMissile(0, i).setXY(0, 0);
            //Explosion should be hidden
            assertNotSame(true, matchHandler.getExplosion(0, i).isVisible());
            //Process collision
            matchHandler.pollCollisionEvent();
            //Collision, bullet should be hidden
            assertNotSame(true, matchHandler.getMissile(0, i).isVisible());
            //Item should be hidden
            assertNotSame(true, matchHandler.getSpecialItem().isVisible());
            //Corresponding explosion should be visible
            assertEquals(true, matchHandler.getExplosion(0, i).isVisible());
        }
    }
}