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

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 *      Class:       MainPanel
 *      Description: This Class provides the Application with control over
 *                   Bluetooth Client and Server connection and Data processing.
 *                   It also provides control over certain events such as
 *                   Touch.
 */
public class MainPanel extends SurfaceView implements SurfaceHolder.Callback {
    private SharedPreferences   sharedPrefs;
    private SoundFXPlayer       soundFX;

    private Context             context;
    private Handler             mHandler;
    private MainThread          mainthread;
    private BluetoothServer     btServer;
    private BluetoothClient     btClient;
    private float               ScaleX;
    private float               ScaleY;

    private float               prevX;
    private float               prevY;
    private boolean             isDown;

    private Profile             profile;
    private MenuHandler         menuHandler;
    private MatchHandler        matchHandler;

    private int                 Display;
    private boolean             isServer;

    private Paint               paint;

    private static final int    SettingCount = 4;
    private static final int    TankCount = 5;

    //Game Resources
    private Bitmap[]    MenuBackgrounds;
    private Bitmap[]    MenuButtonImages;

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

    //Following Attributes intended for Testing Purposes ONLY!!!
    private int          LastAction;
    private float        LastTouchX;
    private float        LastTouchY;
    //END
    /**
     * Method: MainPanel
     * Description: This Constructor initializes and assigns values.
     * @param ctext - Reference to the main activity's context
     * @param mhandler - Handle to message handler
     * @param sp  - Shared Preferences for profile saving
     */
    public MainPanel(Context ctext, Handler mhandler, SharedPreferences sp) {
        super(ctext);
        //Assign values
        Display = 0;
        context = ctext;
        mHandler = mhandler;
        sharedPrefs = sp;
        //Add this as Callback
        getHolder().addCallback(this);
        //Create main thread
        mainthread = new MainThread(getHolder(), this);
        mainthread.start();
        //Set MainPanel as focusable
        setFocusable(true);
        //Initialize and setup Paint
        paint = new Paint();
        //Initialize sound effects
        soundFX = new SoundFXPlayer(context);
        //Initialize arrays
        MenuBackgrounds    = new Bitmap[5];
        MenuButtonImages   = new Bitmap[12];
        PlayerSpawnX       = new int[SettingCount][2];
        PlayerSpawnY       = new int[SettingCount][2];
        PlayerLimitX1      = new int[SettingCount][2];
        PlayerLimitX2      = new int[SettingCount][2];
        PlatColorList       = new int[SettingCount][];
        PlatformVisList     = new boolean[SettingCount][];
        PlatformList        = new Rect[SettingCount][];
        WallPapers          = new Bitmap[SettingCount];
        TankBitmaps         = new Bitmap[TankCount*2];
        TankAttrHP          = new int[TankCount*2];
        TankAttrSP          = new int[TankCount*2];
        TankAttrDMG         = new int[TankCount*2];
        CtrlButtons         = new Bitmap[5];
        BulletButtons       = new Bitmap[4];
        BulletMissiles      = new Bitmap[4];
        BulletExplosions    = new Bitmap[4];
        SpecialItemBmps     = new Bitmap[3];

        //Initialize menu bitmaps
        MenuBackgrounds[0] = BitmapFactory.decodeResource(getResources(), R.drawable.basemenu);
        MenuBackgrounds[1] = BitmapFactory.decodeResource(getResources(), R.drawable.lobbymenu);
        MenuBackgrounds[2] = BitmapFactory.decodeResource(getResources(), R.drawable.loadmenu);
        MenuBackgrounds[3] = BitmapFactory.decodeResource(getResources(), R.drawable.lose);
        MenuBackgrounds[4] = BitmapFactory.decodeResource(getResources(), R.drawable.win);
        MenuButtonImages[0] = BitmapFactory.decodeResource(getResources(), R.drawable.modbtnone);
        MenuButtonImages[1] = BitmapFactory.decodeResource(getResources(), R.drawable.modbtntwo);
        MenuButtonImages[2] = BitmapFactory.decodeResource(getResources(), R.drawable.cpubtn);
        MenuButtonImages[3] = BitmapFactory.decodeResource(getResources(), R.drawable.hostbtn);
        MenuButtonImages[4] = BitmapFactory.decodeResource(getResources(), R.drawable.joinbtn);
        MenuButtonImages[5] = BitmapFactory.decodeResource(getResources(), R.drawable.leftmenubtn);
        MenuButtonImages[6] = BitmapFactory.decodeResource(getResources(), R.drawable.rightmenubtn);
        MenuButtonImages[7] = BitmapFactory.decodeResource(getResources(), R.drawable.basebtn);
        MenuButtonImages[8] = BitmapFactory.decodeResource(getResources(), R.drawable.backbtn);
        MenuButtonImages[9] = BitmapFactory.decodeResource(getResources(), R.drawable.upbtn);
        MenuButtonImages[10] = BitmapFactory.decodeResource(getResources(), R.drawable.downbtn);
        MenuButtonImages[11] = BitmapFactory.decodeResource(getResources(), R.drawable.settingsbtn);

        //Setup Player objects and attributes
        WallPapers[0] = BitmapFactory.decodeResource(getResources(), R.drawable.mapa);
        WallPapers[1] = BitmapFactory.decodeResource(getResources(), R.drawable.mapb);
        WallPapers[2] = BitmapFactory.decodeResource(getResources(), R.drawable.mapc);
        WallPapers[3] = BitmapFactory.decodeResource(getResources(), R.drawable.mapd);
        TankBitmaps[0] = BitmapFactory.decodeResource(getResources(), R.drawable.bluetankright);
        TankBitmaps[1] = BitmapFactory.decodeResource(getResources(), R.drawable.bluetankleft);
        TankBitmaps[2] = BitmapFactory.decodeResource(getResources(), R.drawable.browntankright);
        TankBitmaps[3] = BitmapFactory.decodeResource(getResources(), R.drawable.browntankleft);
        TankBitmaps[4] = BitmapFactory.decodeResource(getResources(), R.drawable.graytankright);
        TankBitmaps[5] = BitmapFactory.decodeResource(getResources(), R.drawable.graytankleft);
        TankBitmaps[6] = BitmapFactory.decodeResource(getResources(), R.drawable.greentankright);
        TankBitmaps[7] = BitmapFactory.decodeResource(getResources(), R.drawable.greentankleft);
        TankBitmaps[8] = BitmapFactory.decodeResource(getResources(), R.drawable.purpletankright);
        TankBitmaps[9] = BitmapFactory.decodeResource(getResources(), R.drawable.purpletankleft);
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
        //Initialie in-game button Bitmaps
        CtrlButtons[0] = BitmapFactory.decodeResource(getResources(), R.drawable.leftbtn);
        CtrlButtons[1] = BitmapFactory.decodeResource(getResources(), R.drawable.rightbtn);
        CtrlButtons[2] = BitmapFactory.decodeResource(getResources(), R.drawable.upbtn);
        CtrlButtons[3] = BitmapFactory.decodeResource(getResources(), R.drawable.downbtn);
        CtrlButtons[4] = BitmapFactory.decodeResource(getResources(), R.drawable.firebtn);

        BulletButtons[0] = BitmapFactory.decodeResource(getResources(), R.drawable.bulletbuttona);
        BulletButtons[1] = BitmapFactory.decodeResource(getResources(), R.drawable.bulletbuttonb);
        BulletButtons[2] = BitmapFactory.decodeResource(getResources(), R.drawable.bulletbuttonc);
        BulletButtons[3] = BitmapFactory.decodeResource(getResources(), R.drawable.bulletbuttond);

        BulletMissiles[0] = BitmapFactory.decodeResource(getResources(), R.drawable.bulleta);
        BulletMissiles[1] = BitmapFactory.decodeResource(getResources(), R.drawable.bulletb);
        BulletMissiles[2] = BitmapFactory.decodeResource(getResources(), R.drawable.bulletc);
        BulletMissiles[3] = BitmapFactory.decodeResource(getResources(), R.drawable.bulletd);

        BulletExplosions[0] = BitmapFactory.decodeResource(getResources(), R.drawable.explosiona);
        BulletExplosions[1] = BitmapFactory.decodeResource(getResources(), R.drawable.explosionb);
        BulletExplosions[2] = BitmapFactory.decodeResource(getResources(), R.drawable.explosionc);
        BulletExplosions[3] = BitmapFactory.decodeResource(getResources(), R.drawable.explosiond);
        //Initialize compass image
        CompassBmp = BitmapFactory.decodeResource(getResources(), R.drawable.compass);

        //Initialize special item bitmaps
        SpecialItemBmps[0] = BitmapFactory.decodeResource(getResources(), R.drawable.potiona);
        SpecialItemBmps[1] = BitmapFactory.decodeResource(getResources(), R.drawable.potionb);
        SpecialItemBmps[2] = BitmapFactory.decodeResource(getResources(), R.drawable.potionc);
    }

    /**
     * Method: setProfile
     * Description: Method used to set the profile name and experience
     * @param name  -   name of profile
     * @param Experience    -   experience corresonding to profile
     * @param ctrl  - 0 = button, 1 = swipe
     */
    public void setProfile(String name, int Experience, int ctrl) {
        profile = new Profile(name);
        profile.addExp(Experience);
        profile.setControlType(ctrl);
        menuHandler.setProfile(profile);
        matchHandler.setProfile(profile);
    }

    /**
     * Method: setBluetoothHandler
     * Description: Method used to distinguish client as server or client
     * @param btserver - Reference to the bluetooth server object
     * @param btclient - Reference to the bluetooth client object
     * @param server   - Boolean indicating Bluetooth Connection type Server/Client
     */
    public void setBluetoothHandler(BluetoothServer btserver, BluetoothClient btclient, boolean server) {
        isServer = server;

        if(isServer) {
            btServer = btserver;    //Server
        }
        else {
            btClient = btclient;    //Client
        }
        //Pass bluetooth objects to menu and match handlers
        menuHandler.setBluetoothHandler(btServer, btClient, isServer);
        matchHandler.setBluetoothHandler(btServer, btClient, isServer);
    }

    /**
     * Method: ProcessIncomingData
     * Description: Incoming data should be coordinates and is used to update pointX/Y
     * @param inData - Incoming data passed by Bluetooth->Read
     */
    public void ProcessIncomingData(byte[] inData) {
        if(inData[0] >= (byte)0xA1 && inData[0] <= (byte)0xAF) {
            //Data header pertaining to in-game actions
            matchHandler.ProcessGameData(inData);
        }
        else if(inData[0] >= (byte)0xB1 && inData[0] <= (byte)0xBF) {
            //Data header pertaining to in-menu actions
            menuHandler.ProcessGameData(inData);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {}

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {}

    /**
     * Method: surfaceCreated
     * Description: Override method which activates when the SurfaceHolder is created.
     * @param holder    - Application's Surface Holder
     */
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if(menuHandler == null && matchHandler == null) {   //Only initialize attributes if menuHandler has not been set
            //Scales based off 1920/1080 dimension phone
            Display = 0;
            ScaleX = (getWidth() * 1.0f) / 1920.0f;
            ScaleY = (getHeight() * 1.0f) / 1080.0f;

            //Initialize action game play handler
            matchHandler = new MatchHandler(context, ScaleX, ScaleY, MenuMsgHandler, soundFX);
            //Initialize menus
            menuHandler = new MenuHandler(getWidth(), getHeight(), ScaleX, ScaleY, MenuBackgrounds,
                    MenuButtonImages, mHandler, MenuMsgHandler, soundFX);
            //Assign menu resources
            menuHandler.setParamResources(TankBitmaps, 36, 36, 3, WallPapers,
                    TankAttrHP, TankAttrSP, TankAttrDMG);

            //Initialize Level(s)' platform(s) and constraints
            PlatformList[0] = new Rect[1];
            PlatformList[1] = new Rect[2];
            PlatformList[2] = new Rect[2];
            PlatformList[3] = new Rect[2];
            PlatformList[0][0] = new Rect(0, getHeight() - Math.round(ScaleY * 300), getWidth(), getHeight());
            PlatformList[1][0] = new Rect(0, getHeight() - Math.round(ScaleY * 350), getWidth(), getHeight());
            PlatformList[1][1] = new Rect(getWidth() / 2 - Math.round(ScaleX * 180),
                    getHeight()/2 - Math.round(ScaleY * 100), getWidth() / 2 + Math.round(ScaleX * 180),
                    getHeight());
            PlatformList[2][0] = new Rect(0, Math.round(ScaleY * 330), 550, getHeight());
            PlatformList[2][1] = new Rect(getWidth() - Math.round(ScaleX * 550),
                    getHeight()/2 + Math.round(ScaleY * 140), getWidth(), getHeight());
            PlatformList[3][0] = new Rect(0, getHeight()/2 + Math.round(ScaleY * 140), 550,
                    getHeight());
            PlatformList[3][1] = new Rect(getWidth() - Math.round(ScaleX * 550), Math.round(ScaleY * 350),
                    getWidth(), getHeight());

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

            PlayerSpawnX[0][1] = getWidth() - Math.round(ScaleX * 100);
            PlayerSpawnX[1][1] = getWidth() - Math.round(ScaleX * 100);
            PlayerSpawnX[2][1] = getWidth() - Math.round(ScaleX * 100);
            PlayerSpawnX[3][1] = getWidth() - Math.round(ScaleX * 100);

            PlayerSpawnY[0][0] = PlatformList[0][0].top - Math.round(ScaleY * 100);
            PlayerSpawnY[1][0] = PlatformList[1][0].top - Math.round(ScaleY * 50);
            PlayerSpawnY[2][0] = PlatformList[2][0].top - Math.round(ScaleY * 100);
            PlayerSpawnY[3][0] = PlatformList[3][0].top - Math.round(ScaleY * 100);

            PlayerSpawnY[0][1] = PlatformList[0][0].top - Math.round(ScaleY * 100);
            PlayerSpawnY[1][1] = PlatformList[1][0].top - Math.round(ScaleY * 50);
            PlayerSpawnY[2][1] = PlatformList[2][1].top - Math.round(ScaleY * 100);
            PlayerSpawnY[3][1] = PlatformList[3][1].top - Math.round(ScaleY * 100);

            PlayerLimitX1[0][0] = 0;
            PlayerLimitX1[1][0] = 0;
            PlayerLimitX1[2][0] = 0;
            PlayerLimitX1[3][0] = 0;

            PlayerLimitX1[0][1] = 0;
            PlayerLimitX1[1][1] = PlatformList[1][1].right;
            PlayerLimitX1[2][1] = PlatformList[2][1].left;
            PlayerLimitX1[3][1] = PlatformList[3][1].left;

            PlayerLimitX2[0][0] = getWidth()-Math.round(ScaleX * 100);
            PlayerLimitX2[1][0] = PlatformList[1][1].left;
            PlayerLimitX2[2][0] = PlatformList[2][0].right-Math.round(ScaleX * 100);
            PlayerLimitX2[3][0] = PlatformList[3][0].right-Math.round(ScaleX * 100);

            PlayerLimitX2[0][1] = getWidth()-Math.round(ScaleX * 100);
            PlayerLimitX2[1][1] = getWidth()-Math.round(ScaleX * 100);
            PlayerLimitX2[2][1] = getWidth()-Math.round(ScaleX * 100);
            PlayerLimitX2[3][1] = getWidth()-Math.round(ScaleX * 100);

            if(mHandler != null) {
                mHandler.obtainMessage(100).sendToTarget();
            }
        }
    }

    /**
     * Method: onTouchEvent (Override)
     * Description: This method handles Touch Events by passing it onto a onTouchEvent
     *              wrapper.
     * @param event - The event that is passed to this method.
     * @return - True if noted, else allows super to distinguish.
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float   touchX = event.getX();
        float   touchY = event.getY();
        int     EventType = event.getAction();
        int     xLim = getWidth();
        int     yLim = getHeight();

        xLim = xLim == 0 ? 1920 : xLim;
        yLim = yLim == 0 ? 1080 : yLim;
        //Verify coordinates are within screen dimensions
        if(touchX >= 0 && touchX <= xLim && touchY >= 0 && touchY <= yLim) {
                switch(EventType) {
                    //Touch Down
                    case MotionEvent.ACTION_DOWN:
                        switch(Display) {
                            case 0:
                                //Handle Press from Menu
                                menuHandler.handleButtonEvent(touchX, touchY, mainthread);
                                break;

                            case 1:
                                //Handle Press from Match
                                matchHandler.handleButtonEvent(touchX, touchY, mainthread);
                                break;
                        }
                        //Testing Purposes
                        if (isDown == false) {
                            prevX = touchX;
                            prevY = touchY;
                            isDown = true;
                        }
                        break;

                    case MotionEvent.ACTION_MOVE:
                        switch(Display) {
                            case 0:
                                //Handle Swipe from Menu
                                menuHandler.handleSwipe(touchX - prevX,
                                        touchY - prevY,
                                        prevX,
                                        prevY);
                                break;

                            case 1:
                                //Handle Swipe from Match
                                matchHandler.handleSwipe(touchX - prevX,
                                        touchY - prevY,
                                        prevX,
                                        prevY);
                                break;
                        }
                        break;

                    case MotionEvent.ACTION_UP:
                        switch(Display) {
                            case 0:
                                //Handle Release from Menu
                                menuHandler.resetButtonsAndEvents(touchX, touchY);
                                break;

                            case 1:
                                //Handle Release from Match
                                matchHandler.resetButtonsAndEvents(touchX, touchY, prevX, prevY);
                                break;
                        }
                        isDown = false;
                        break;
                }
                return true;
        }
        return super.onTouchEvent(event);
    }

    /**
     * Method: update
     * Description: Constantly updates the state and error messages.
     */
    public void update() {
        if(Display == 0) {
            //Update menu
            menuHandler.update();
        }
        else {
            //Update in-game
            matchHandler.update();
        }
    }

    /**
     * Method: draw (Override)
     * Description: Draws out the state, error and paired devices' last touch location.
     * @param canvas - Reference to the main activity's canvas.
     */
    @Override
    public void draw(Canvas canvas) {
        //Verify that we can draw and canvas is not null
        if(canvas != null) {
            if(paint.getTextSize() != 25.0f) {
                paint.setTextSize(25.0f);
            }
            if(paint.getStrokeWidth() != 2.0f) {
                paint.setStrokeWidth(2.0f);
            }

            if(Display == 0) {
                //Draw menu
                menuHandler.draw(canvas);
            }
            else {
                //Draw in-game match
                matchHandler.draw(canvas);
            }

            if(menuHandler.getGameParams()[0] == 2) {
                paint.setColor(Color.GREEN);
                paint.setTextSize(Math.round(ScaleX * 25.0f));
            }
        }
    }

    /**
     * Object: MenuMsgHandler
     * Description: Message Handler which receives input from menu handler and then
     *              initializes match.
     */
    private final Handler MenuMsgHandler = new Handler() {
        /**
         * Method: handleMessage
         * Description: Method used to recieve incoming messages and interrupt them.
         * @param msg   - incoming message
         */
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Bitmap[]    chosenTanks = new Bitmap[2];
                    int[]       chosenHP = new int[2];
                    int[]       chosenSP = new int[2];
                    int[]       chosenDMG = new int[2];

                    //Retrieve game parameters from menu
                    //[GameType][PrimaryPlayer][LevelID][PrimaryTankID][SecondaryTankID]
                    int     p1Array[] = {0, 2, 4, 6, 8};
                    int     p2Array[] = {1, 3, 5, 7, 9};
                    int[]   params = menuHandler.getGameParams();
                    Rect[]  ItemRects = new Rect[params[2] > 1 ? 2 : 1];

                    ItemRects[0] = new Rect(PlatformList[params[2]][0]);

                    if(params[2] > 1) {
                        ItemRects[1] = new Rect(PlatformList[params[2]][1]);
                    }

                    //Initialize game play resources
                    matchHandler.InitResources(getWidth(), getHeight(), WallPapers[params[2]],
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
                    //Send other player custom tank stats if permitted
                    if(params[0] == 2) { //Bluetooth Match
                        if(params[3] == 4 && isServer) { //Primary player, so send to client
                            int[]   Contents = {params[5], params[6], params[7]};

                            if(btServer != null) {
                                if (btServer.btHandler != null) {
                                    btServer.Send(btServer.btHandler.FormatPacket((byte) 0xA6, Contents));
                                }
                            }
                        }
                        else if(params[4] == 4 && !isServer) {//Secondary layer, so send to server
                            int[]   Contents = {params[8], params[9], params[10]};

                            if(!isServer && btClient != null) {
                                if (btClient.btHandler != null) {
                                    btClient.Send(btClient.btHandler.FormatPacket((byte) 0xA6, Contents));
                                }
                            }
                        }
                    }
                    //Set game players
                    matchHandler.SetGamePlayers(chosenTanks, 36, 36, 3,
                            chosenHP, chosenSP, chosenDMG);
                    //Reset match variables
                    matchHandler.BeginMatch();
                    //Switch display from menu to game
                    Display = 1;
                    break;

                case 2:
                    soundFX.pause(-1);
                    matchHandler.resetButtonsAndEvents(0, 0, 0, 0);
                    Display = 0;
                    menuHandler.setMenu(3);
                    profile.addExp(10);
                    profile.saveProfile(sharedPrefs);
                    break;

                case 3:
                    soundFX.pause(-1);
                    matchHandler.resetButtonsAndEvents(0, 0, 0, 0);
                    Display = 0;
                    menuHandler.setMenu(4);
                    profile.addExp(25);
                    profile.saveProfile(sharedPrefs);
                    break;
            }
        }
    };

    /*      Getters and Setters     */
    public void setStateMsg(String msg) {
        //Update this in the future
    }

    public void setErrorMsg(String msg) {
        Display = 0;
        setMenuID(0); //Set Main Menu
        //Because of error, stop connection
        if(btServer != null) {
            btServer.Stop(3);
            btServer = null;
        } else if(btClient != null) {
            btClient.Stop(3);
            btClient = null;
        }
    }

    public int getDisplayID() { return Display; }

    public void setDisplayID(int id) { Display = id; }

    public int getMenuID() {
        if(menuHandler != null) {
            return menuHandler.getMenu();
        }
        return -1;
    }

    public void setMenuID(int id) {
        if (menuHandler != null) {
            menuHandler.setMenu(id);
        }
    }

    public int[] getGameParameters() {
        int[] Params = {-1, -1, -1, -1, -1};

        if(menuHandler != null && Display == 1) {
            Params = menuHandler.getGameParams();
        }
        return Params;
    }

    public void setGameParameters(int[] params) {
        if(menuHandler != null && Display == 0) {
            menuHandler.setGameParams(params);
        }
    }

    public int[] getPlayerAttributes(int index) {
        int[] attr = {-1, -1, -1, -1};

        if(Display == 1 && matchHandler != null) {
            attr[0] = matchHandler.getHP()[index];
            attr[1] = matchHandler.getSP()[index];
            attr[2] = matchHandler.getPX()[index];
            attr[3] = matchHandler.getPY()[index];
        }
        return attr;
    }

    public void resumeGame(int[] params, int[] hp, int[] sp, int[] X, int[] Y) {
        Bitmap[]    chosenTanks = new Bitmap[2];
        int[]       chosenHP = new int[2];
        int[]       chosenSP = new int[2];
        int[]       chosenDMG = new int[2];

        //Retrieve game parameters from menu
        //[GameType][PrimaryPlayer][LevelID][PrimaryTankID][SecondaryTankID]
        int     p1Array[] = {0, 2, 4, 6};
        int     p2Array[] = {1, 3, 5, 7};
        Rect[]  ItemRects = new Rect[params[2] > 1 ? 2 : 1];

        ItemRects[0] = new Rect(PlatformList[params[2]][0]);

        if(params[2] > 1) {
            ItemRects[1] = new Rect(PlatformList[params[2]][1]);
        }
        //Initialize game play resource
        matchHandler.InitResources(getWidth(), getHeight(), WallPapers[params[2]],
                PlatformList[params[2]], PlatColorList[params[2]], PlatformVisList[params[2]],
                BulletButtons, BulletMissiles, BulletExplosions, CtrlButtons, CompassBmp,
                SpecialItemBmps, ItemRects);
        //Assign current game constraints
        matchHandler.SetGameConstraints(params[0], PlayerSpawnX[params[2]],
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
        //Switch display from menu to game
        Display = 1;
    }

    /*
        The following methods are intended for TESTING PURPOSES ONLY!!!
     */
    public int getLastAction() {
        return LastAction;
    }

    public float getLastTouchX() {
        return LastTouchX;
    }

    public float getLastTouchY() {
        return LastTouchY;
    }
}
