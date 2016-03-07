package com.proto.bit8.btprototype;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;

/**
 * Class: MatchHandler
 * Description: This class will initialize and handle actual game play.
 */
public class MatchHandler {
    private BluetoothServer btServer;
    private BluetoothClient btClient;
    private Handler         mHandler;
    private boolean         isServer;
    private boolean         isBTGame;
    private Profile         profile;
    private float           ScaleX;
    private float           ScaleY;
    //Game Resources
    private SoundFXPlayer   soundFXPlayer;
    private SpecialItem     specialItem;
    private Background      background;
    private Explosion[][]   explosions;
    private Platform[]      platforms;
    private Missile[][]     missiles;
    private Tank[]          Tanks;
    //Control Resources
    private Button[]        Controls;
    private ProgressBar     PowerBar;
    //Application Resources
    private Paint           paint;
    private Context         context;
    private int             displayWidth;
    private int             displayHeight;
    //Active game-play Attributes
    private int             MatchType;
    private int             FireCount;
    private boolean         MissileActive;
    //Game Constraints
    private int[]           SpawnX;
    private int[]           SpawnY;
    private int[]           LimitX1;
    private int[]           LimitX2;
    //Features
    private StormSystem     stormSystem;

    /**
     * Method: MatchHandler
     * Description: Method which intializes and assigns values to resources
     *              and attributes.
     * @param conText - Reference to application context
     * @param scaleX - width scalar
     * @param scaleY - height scalar
     * @param msgHandler - message handler for match completion
     * @param soundfx - sound effects
     */
    public MatchHandler(Context conText, float scaleX, float scaleY, Handler msgHandler,
                        SoundFXPlayer soundfx) {
        ScaleX = scaleX;
        ScaleY = scaleY;
        context = conText;
        mHandler = msgHandler;
        //Initialize paint
        paint = new Paint();
        //Initialize Game Resources
        Tanks = new Tank[2];
        soundFXPlayer = soundfx;
        missiles = new Missile[2][4];
        explosions = new Explosion[2][4];
        //Initialize Control Resources
        Controls = new Button[9];
        //Initialize game constraints
        SpawnX = new int[2];
        SpawnY = new int[2];
        LimitX1 = new int[2];
        LimitX2 = new int[2];
        //Assign initial values
        FireCount = 0;
        MissileActive = false;
    }

    /**
     * Method: update
     * Description: Method which is used to update all component classes.
     */
    public void update()
    {
        int[]   Contents;
        //If Bluetooth match, send position update
        if(isBTGame && Tanks[isServer ? 0 : 1].hasChanged()) {
            Contents = new int[3];
            //Get Tank X-Coordinate
            Contents[0] = Math.round(Tanks[isServer ? 0 : 1].getX() / ScaleX);
            //Get Tank Y-Coordinate
            Contents[1] = Math.round(Tanks[isServer ? 0 : 1].getY() / ScaleY);
            //Get Tank Angle
            Contents[2] = Tanks[isServer ? 0 : 1].getAngle();
            //Send data from corresponding handler
            if (isServer && btServer != null) {
                if (btServer.btHandler != null) {
                    btServer.Send(btServer.btHandler.FormatPacket((byte) 0xA1, Contents));
                }
            } else if (!isServer && btClient != null) {
                if (btClient.btHandler != null) {
                    btClient.Send(btClient.btHandler.FormatPacket((byte) 0xA1, Contents));
                }
            }
        }
        //Toggle Special Item
        if((FireCount % 8) == 7 && specialItem.isVisible() == false) {
            specialItem.update();
            //Send item toggle if game is Bluetooth
            if(specialItem.isVisible() && isBTGame) { //Special Item toggled, send packet
                Contents = new int[3];
                Contents[0] = specialItem.getType();
                Contents[1] = specialItem.getRectID();
                Contents[2] = Math.round(specialItem.getX() / ScaleX);

                if(isServer && btServer != null) {
                    if (btServer.btHandler != null) {
                        btServer.Send(btServer.btHandler.FormatPacket((byte) 0xA7, Contents));
                    }
                }
                else if(!isServer && btClient != null) {
                    if (btClient.btHandler != null) {
                        btClient.Send(btClient.btHandler.FormatPacket((byte) 0xA7, Contents));
                    }
                }
            }
        }
        else if(specialItem.isVisible()) {
            specialItem.update(); //Update special item
        }
        //Toggle Storm System
        if((FireCount % 11) == 10 && stormSystem.getStormType() == 0) {
            stormSystem.update();
            //Check to see if storm is fresh
            if(stormSystem.getStormType() == 1) {
                Contents = stormSystem.getStormParameters();

                soundFXPlayer.play(soundFXPlayer.SND_FX_WINDSTM);
                //Send packet since storm is fresh
                if(isBTGame) {
                    if (isServer && btServer != null) {
                        if (btServer.btHandler != null) {
                            btServer.Send(btServer.btHandler.FormatPacket((byte) 0xA5, Contents));
                        }
                    } else if (!isServer && btClient != null) {
                        if (btClient.btHandler != null) {
                            btClient.Send(btClient.btHandler.FormatPacket((byte) 0xA5, Contents));
                        }
                    }
                }
            }
        }
        else if(stormSystem.getStormType() != 0){
            Contents = new int[1];
            Contents[0] = 0;

            stormSystem.update();   //Update storm

            if(stormSystem.getStormType() == 0) {
                if(isBTGame) {
                    if (isServer && btServer != null) {
                        if (btServer.btHandler != null) {
                            //Send storm disable packet
                            btServer.Send(btServer.btHandler.FormatPacket((byte) 0xA8, Contents));
                        }
                    } else if (!isServer && btClient != null) {
                        if (btClient.btHandler != null) {
                            //Send storm disable packet
                            btClient.Send(btClient.btHandler.FormatPacket((byte) 0xA8, Contents));
                        }
                    }
                }
            }
        }
        //Update power bar progress
        PowerBar.update();
        //Update Missiles
        for(int i = 0; i < missiles.length; ++i) {
            for(int j = 0; j < missiles[i].length; ++j) {
                missiles[i][j].update();

                //Check if wind is active, append dXdY if so!
                if(missiles[i][j].isVisible() && stormSystem.getStormType() == 1) {
                    int[]   stormParams = stormSystem.getStormParameters();

                    missiles[i][j].appenddXdY(stormParams[2], stormParams[3]);
                }
            }
        }
        //Update explosions
        for(int i = 0; i < explosions.length; ++i) {
            for(int j = 0; j < explosions[i].length; ++j) {
                explosions[i][j].update();
            }
        }
        //Check if a tank is dead, display win/lose
        if(Tanks[0].getHealth() == 0) {
            //Disable bluetooth connection
            if(isServer && btServer != null) {
                btServer.Stop(3);
            }
            else if(!isServer && btClient != null) {
                btClient.Stop(3);
            }
            //Display win/lose screen
            if(isServer) {
                mHandler.obtainMessage(2, 0, -1, 0).sendToTarget();
            }
            else {
                mHandler.obtainMessage(3, 0, -1, 0).sendToTarget();
            }
        }
        else if(Tanks[1].getHealth() == 0) {
            //Disable bluetooth connection
            if(isServer && btServer != null) {
                btServer.Stop(3);
            }
            else if(!isServer && btClient != null) {
                btClient.Stop(3);
            }
            //Display win/lose screen
            if(isServer) {
                mHandler.obtainMessage(3, 0, -1, 0).sendToTarget();
            }
            else {
                mHandler.obtainMessage(2, 0, -1, 0).sendToTarget();
            }
        }
        //Update players
        Tanks[0].update();
        Tanks[1].update();
        //Poll for Fire button event
        HandleFireEvent();
        //Poll for possible collisions
        pollCollisionEvent();
    }

    /**
     * Method: draw
     * Description: Method which will call each components' draw method
     * @param canvas    - Application's canvas
     */
    public void draw(Canvas canvas)
    {
        if(canvas != null)
        {
            //draw background
            background.draw(canvas);
            //draw platform(s)
            for(int i = 0; i < platforms.length; ++i) {
                platforms[i].draw(canvas);
            }
            //draw players
            Tanks[0].draw(canvas);
            Tanks[1].draw(canvas);
            //draw buttons
            for(int i = 0; i < Controls.length; ++i) {
                Controls[i].draw(canvas);
            }
            //Draw missiles
            for(int i = 0; i < missiles.length; ++i) {
                for(int j = 0; j < missiles[i].length; ++j) {
                    missiles[i][j].draw(canvas);
                }
            }
            //Draw explosions
            for(int i = 0; i < explosions.length; ++i) {
                for(int j = 0; j < explosions[i].length; ++j) {
                    explosions[i][j].draw(canvas);
                }
            }
            //Draw storm
            stormSystem.draw(canvas, displayWidth/2, ScaleX);
            //Draw Special Item
            specialItem.draw(canvas);
            //Draw Power Bar and Corresponding Power
            PowerBar.draw(canvas);
            //Set up paint for Timer Output
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.DKGRAY);
            paint.setTextSize(Math.round(ScaleX * 75.0f));
        }
    }

    /**
     * Method: ProcessGameData
     * Description: Method used to take incoming data and provide a response
     * @param inData    - incoming data
     */
    public void ProcessGameData(byte[] inData) {
        if(inData[0] == (byte)0xA1) {   //Other tank has moved
            //Get X+Y Coordinates
            int[]    Params = isServer ? btServer.btHandler.ExtractIntArray(inData) :
                    btClient.btHandler.ExtractIntArray(inData);

            //Assign corresponding tank position
            Tanks[isServer ? 1 : 0].setX(Math.round(ScaleX * Params[0]));
            Tanks[isServer ? 1 : 0].setY(Math.round(ScaleY * Params[1]));
            Tanks[isServer ? 1 : 0].setAngle(Params[2]);
        }
        else if(inData[0] == (byte)0xA2) {  //Previously Tank Angle

        }
        else if(inData[0] == (byte)0xA3) {  //Other tank has fired shot
            //Get angle, magnitude and bullet type
            int[]   ShotParams = isServer ? btServer.btHandler.ExtractIntArray(inData) :
                    btClient.btHandler.ExtractIntArray(inData);
            //Set missile start position
            missiles[isServer ? 1 : 0][ShotParams[2]].setXY(isServer ?
                            Tanks[isServer ? 1 : 0].getRect().left - Math.round(ScaleX * 50) :
                            Tanks[isServer ? 1 : 0].getRect().right + Math.round(ScaleX * 50),
                    Tanks[isServer ? 1 : 0].getRect().top - Math.round(ScaleY * 50));
            //Fire missile
            missiles[isServer ? 1 : 0][ShotParams[2]].setdXdY(ShotParams[0], ShotParams[1],
                    isServer ? false : true);
            missiles[isServer ? 1 : 0][ShotParams[2]].setVisible(true);
            ++FireCount;
        }
        else if(inData[0] == (byte)0xA4) {  //I Received Damage on other player's screen
            int[]   pStats = isServer ? btServer.btHandler.ExtractIntArray(inData) :
                    btClient.btHandler.ExtractIntArray(inData);

            setHP(pStats[0], 0);
            setHP(pStats[1], 1);
            setSP(pStats[2], 0);
            setSP(pStats[3], 1);
            setDMG(pStats[4], 0);
            setDMG(pStats[5], 1);
        }
        else if(inData[0] == (byte)0xA5) {  //Storm update
            int[]   stormParams = isServer ? btServer.btHandler.ExtractIntArray(inData) :
                    btClient.btHandler.ExtractIntArray(inData);

            stormSystem.setStormParameters(stormParams);
            soundFXPlayer.play(soundFXPlayer.SND_FX_WINDSTM);
        }
        else if(inData[0] == (byte)0xA6) { //Custom Tank Stats
            int[]   statParams = isServer ? btServer.btHandler.ExtractIntArray(inData) :
                    btClient.btHandler.ExtractIntArray(inData);

            Tanks[isServer ? 1 : 0].setStats(statParams);
        }
        else if(inData[0] == (byte)0xA7) { //Toggled Special Item
            int[]   itemParams = isServer ? btServer.btHandler.ExtractIntArray(inData) :
                    btClient.btHandler.ExtractIntArray(inData);

            specialItem.Toggle(itemParams[0], itemParams[1], Math.round(ScaleX*itemParams[2]));
        }
        else if(inData[0] == (byte)0xA8) { //Disable StormSystem!
            int[]   stormParams = {0, 0, 0, 0};

            stormSystem.setStormParameters(stormParams);
        }
        else if(inData[0] == (byte)0xA9) { //Special Item hit, remove
            specialItem.setVisible(false);
        }
    }

    /**
     * Method: InitializeResources
     * Description: Method which initializes game resources for future use.
     * @param dWidth                - display width
     * @param dHeight               - display height
     * @param wallpaper             - match wall paper
     * @param PlatformList          - List of platforms
     * @param PlatformColorList     - List of colors for each platform
     * @param PlatformVisibleList   - List of visibility for each platform
     * @param BulletBtnImages       - Images corresponding to each bullet button
     * @param MissileImages         - Images corresponging to each missile
     * @param ExplosionImages       - Images corresponding to each explosion
     * @param ButtonImages          - Images of each Control Button (Left-Right-Up-Down-Fire)
     * @param Compass               - Image of the compass to draw for the StormSystem
     * @param ItemBmps              - Image of the special items
     */
    public void InitResources(int dWidth, int dHeight, Bitmap wallpaper,
                              Rect[] PlatformList, int[] PlatformColorList, boolean[] PlatformVisibleList,
                              Bitmap[] BulletBtnImages, Bitmap[] MissileImages, Bitmap[] ExplosionImages,
                              Bitmap[] ButtonImages, Bitmap Compass, Bitmap[] ItemBmps, Rect[] ItemRects) {
        //Initialize features
        stormSystem = new StormSystem(Compass);
        //Initialize background
        displayWidth = dWidth == 0 ? 1920 : dWidth;
        displayHeight = dHeight == 0 ? 1080 : dHeight;
        background = new Background(wallpaper, displayWidth, displayHeight, true);
        //Initialize platforms
        platforms = new Platform[PlatformList.length];

        for(int i = 0; i < PlatformList.length; ++i) {
            platforms[i] = new Platform(PlatformList[i], PlatformColorList[i], PlatformVisibleList[i]);
        }
        //Initialize Buttons and Power Bar
        PowerBar = new ProgressBar(Math.round(ScaleX * 140), displayHeight-Math.round(ScaleX * 290),
                displayWidth - Math.round(ScaleX * 140), displayHeight-Math.round(ScaleY * 230),
                Color.DKGRAY, Color.BLUE, 0, 100);
        //Add Control Buttons
        Controls[0] = new Button(ButtonImages[0], 150, 150, Math.round(ScaleX * 50),
                displayHeight-Math.round(ScaleY * 190), Math.round(ScaleX * 150),
                Math.round(ScaleY * 150), true, profile.getControlType() == 1 ? false : true);  //Left
        Controls[1] = new Button(ButtonImages[1], 150, 150, Math.round(ScaleX * 210),
                displayHeight-Math.round(ScaleY * 190), Math.round(ScaleX * 150),
                Math.round(ScaleY * 150), true, profile.getControlType() == 1 ? false : true); //Right
        Controls[2] = new Button(ButtonImages[2], 150, 150, Math.round(ScaleX * 370),
                displayHeight-Math.round(ScaleY * 190), Math.round(ScaleX * 150),
                Math.round(ScaleY * 150), true, profile.getControlType() == 1 ? false : true);    //Up
        Controls[3] = new Button(ButtonImages[3], 150, 150, Math.round(ScaleX * 530),
                displayHeight-Math.round(ScaleY * 190), Math.round(ScaleX * 150),
                Math.round(ScaleY * 150), true, profile.getControlType() == 1 ? false : true);  //Down
        Controls[4] = new Button(ButtonImages[4], 150, 150,
                displayWidth-Math.round(ScaleX * 200), displayHeight-Math.round(ScaleY * 190),
                Math.round(ScaleX * 150), Math.round(ScaleY * 150), true, true);//Fire)

        Controls[5] = new Button(BulletBtnImages[0], 100, 100,
                displayWidth-Math.round(ScaleX * 540), displayHeight-Math.round(ScaleY * 165),
                Math.round(ScaleX * 100), Math.round(ScaleY * 100), true, true);
        Controls[6] = new Button(BulletBtnImages[1], 100, 100,
                displayWidth-Math.round(ScaleX * 430), displayHeight-Math.round(ScaleY * 165),
                Math.round(ScaleX * 100), Math.round(ScaleY * 100), true,
                profile.getUnlockables()[1]);
        Controls[7] = new Button(BulletBtnImages[2], 100, 100,
                displayWidth-Math.round(ScaleX * 320), displayHeight-Math.round(ScaleY * 165),
                Math.round(ScaleX * 100), Math.round(ScaleY * 100), true,
                profile.getUnlockables()[3]);
        Controls[8] = new Button(BulletBtnImages[3], 100, 100,
                displayWidth-Math.round(ScaleX * 650), displayHeight-Math.round(ScaleY * 165),
                Math.round(ScaleX * 100), Math.round(ScaleY * 100), true,
                profile.getUnlockables()[5]);

        for(int i = 0; i < 2; ++i) {
            for(int j = 0; j < 4; ++j) {
                //Create Standby Missiles
                missiles[i][j] = new Missile(MissileImages[j], 50, 50, Math.round(ScaleX * 50),
                        Math.round(ScaleY * 50), true, 10+(2*j), 8, ScaleX, ScaleY);
                //Create Standby Explosions
                explosions[i][j] = new Explosion(ExplosionImages[j], 50, 50, Math.round(ScaleX * 50),
                        Math.round(ScaleY * 50), true, 8);
            }
        }

        specialItem = new SpecialItem(ItemBmps, 100, 100, ItemRects);
    }

    /**
     * Method: SetGameConstraints
     * Description: Method used to assign constraint values prior to match
     * @param mType     - Type of match (0=save device, 1=cpu, 2=bluetooth)
     * @param spawnX        - Spawn X constraint
     * @param spawnY        - Spawn Y constraint
     * @param limitX1       - Limit X (left)
     * @param limitX2       - Limit Y (right)
     */
    public void SetGameConstraints(int mType, int[] spawnX, int[] spawnY, int[] limitX1, int[] limitX2) {
        //Assign match attributes
        MatchType = mType;
        //Assign player constraints
        SpawnX[0] = spawnX[0];
        SpawnX[1] = spawnX[1];
        SpawnY[0] = spawnY[0];
        SpawnY[1] = spawnY[1];
        LimitX1[0] = limitX1[0];
        LimitX1[1] = limitX1[1];
        LimitX2[0] = limitX2[0];
        LimitX2[1] = limitX2[1];
    }

    /**
     * Method: SetGamePlayers
     * Description: Method which assigns each player a specific tank with attributes.
     * @param TankBitmaps   -   Bitmaps of tanks to be used
     * @param TankHP        -   Each Tank's health stat
     * @param TankSH        -   Each Tank's shield stat
     * @param TankDMG       -   Each Tank's damage stat
     * @param bmpWidth      -   Width of the bitmap
     * @param bmpHeight     -   Height of the bitmap
     * @param bmpFrames     -   Frames of the bitmap for animation
     */
    public void SetGamePlayers(Bitmap[] TankBitmaps, int bmpWidth, int bmpHeight,
                               int bmpFrames, int[] TankHP, int[] TankSH, int TankDMG[]) {
        //Initialize and Set Tank spawn points
        for(int i = 0; i < 2; ++i) {
            Tanks[i] = new Tank(TankBitmaps[i], bmpWidth, bmpHeight, Math.round(ScaleX * 100),
                    Math.round(ScaleY * 100), true, bmpFrames, i == 0 ? true : false,
                    TankHP[i], TankSH[i], TankDMG[i]);
            Tanks[i].setX(SpawnX[i]);
            Tanks[i].setY(SpawnY[i]);
        }
    }

    /**
     * Method: BeginMatch
     * Description: Method used to reset timer and turn
     */
    public void BeginMatch() {
        if(MatchType != 2) {
            isBTGame = false;
            isServer = true;
        }
        Controls[5].setButtonState(1);
        Tanks[0].setBullet(0);
        Tanks[1].setBullet(0);
    }

    /**
     * Method: handleSwipe
     * Description: Method used to detect and process swipe controls
     * @param dx - change in X
     * @param dy - change in Y
     * @param X - X coordinate
     * @param Y - Y coordinate
     */
    public void handleSwipe(float dx, float dy, float X, float Y) {
        //Only handle SWIPES if SwipeControl is set
        if(dx < -50 && (Math.abs(dx) > Math.abs(dy)) && profile.getControlType() == 1) {
            soundFXPlayer.play(soundFXPlayer.SND_FX_TRAVEL);
            Tanks[isServer ? 0 : 1].moveX(-2, LimitX1[isServer ? 0 : 1]);
        }
        else if(dx > 50 && (Math.abs(dx) > Math.abs(dy)) && profile.getControlType() == 1) {
            soundFXPlayer.play(soundFXPlayer.SND_FX_TRAVEL);
            Tanks[isServer ? 0 : 1].moveX(2, LimitX2[isServer ? 0 : 1]);
        }
        else if(dy > 50 && (Math.abs(dx) < Math.abs(dy)) && profile.getControlType() == 1) {
            Tanks[isServer ? 0 : 1].AdjustShot(1);
            soundFXPlayer.play(soundFXPlayer.SND_FX_TANKAIM);
        }
        else if(dy < -50 && (Math.abs(dx) < Math.abs(dy)) && profile.getControlType() == 1) {
            Tanks[isServer ? 0 : 1].AdjustShot(-1);
            soundFXPlayer.play(soundFXPlayer.SND_FX_TANKAIM);
        }
    }

    /**
     * Method: getButtonPressed
     * Description: Method used to return the index of a control that has been activated
     * @param touchX    -   touch X coordinate
     * @param touchY    -   touch Y coordinate
     * @return  -   index of touched button
     */
    private int getButtonPressed(float touchX, float touchY, Button[] buttons, MainThread mainthread) {
        //Loop through all controls
        for(int i = 0; i < buttons.length; ++i) {
            //If Touch Point intersects with Control and Control is active
            if(buttons[i].isIntersect(touchX, touchY) && buttons[i].Visible()) {
                //Switch control to "pressed" && return index
                buttons[i].setButtonState(1);
                return i;
            }
        }
        //No controls have been pressed
        return -1;
    }

    /**
     * Method: handleButtonEvent
     * Description: Method which provides feedback to corresponding button events
     * @param touchX    -   touch X coordinate
     * @param touchY    -   touch Y coordinate
     */
    public void handleButtonEvent(float touchX, float touchY, MainThread mainthread) {
        //Continue only if missile is not traveling
        int[]   Contents;

        switch(getButtonPressed(touchX, touchY, Controls, mainthread)) {
            case 0: //Left Button
                Contents = new int[2];
                soundFXPlayer.play(soundFXPlayer.SND_FX_TRAVEL);
                Tanks[isServer ? 0 : 1].moveX(-1, LimitX1[isServer ? 0 : 1]);
                //Disable other buttons
                Controls[1].setButtonState(0);
                Controls[2].setButtonState(0);
                Controls[3].setButtonState(0);
                break;

            case 1: //Right Button
                Contents = new int[2];

                soundFXPlayer.play(soundFXPlayer.SND_FX_TRAVEL);
                Tanks[isServer ? 0 : 1].moveX(1, LimitX2[isServer ? 0 : 1]);
                //Disable other buttons
                Controls[0].setButtonState(0);
                Controls[2].setButtonState(0);
                Controls[3].setButtonState(0);
                break;

            case 2: //Up Button
                Tanks[isServer ? 0 : 1].AdjustShot(-1);
                soundFXPlayer.play(soundFXPlayer.SND_FX_TANKAIM);
                //Disable other buttons
                Controls[0].setButtonState(0);
                Controls[1].setButtonState(0);
                Controls[3].setButtonState(0);
                break;

            case 3: //Down Button
                Tanks[isServer ? 0 : 1].AdjustShot(1);
                soundFXPlayer.play(soundFXPlayer.SND_FX_TANKAIM);
                //Disable other buttons
                Controls[0].setButtonState(0);
                Controls[1].setButtonState(0);
                Controls[2].setButtonState(0);
                break;

            case 4:
                System.out.printf("\n[Successfully Chosen Button]");
                //Disable other buttons
                Controls[0].setButtonState(0);
                Controls[1].setButtonState(0);
                Controls[2].setButtonState(0);
                Controls[3].setButtonState(0);
                soundFXPlayer.play(soundFXPlayer.SND_FX_CHARGE);
                break;

            case 5: //BulletA Button
                //Disable other buttons
                Controls[0].setButtonState(0);
                Controls[1].setButtonState(0);
                Controls[2].setButtonState(0);
                Controls[3].setButtonState(0);
                //Assign corresponding attributes
                Tanks[isServer ? 0 : 1].setBullet(0);
                Controls[5].setButtonState(1);
                Controls[6].setButtonState(0);
                Controls[7].setButtonState(0);
                Controls[8].setButtonState(0);
                break;

            case 6: //BulletB Button
                //Disable other buttons
                Controls[0].setButtonState(0);
                Controls[1].setButtonState(0);
                Controls[2].setButtonState(0);
                Controls[3].setButtonState(0);
                //Assign corresponding attributes
                Tanks[isServer ? 0 : 1].setBullet(1);
                Controls[5].setButtonState(0);
                Controls[6].setButtonState(1);
                Controls[7].setButtonState(0);
                Controls[8].setButtonState(0);
                break;

            case 7: //BulletC Button
                //Disable other buttons
                Controls[0].setButtonState(0);
                Controls[1].setButtonState(0);
                Controls[2].setButtonState(0);
                Controls[3].setButtonState(0);
                //Assign corresponding attributes
                Tanks[isServer ? 0 : 1].setBullet(2);
                Controls[5].setButtonState(0);
                Controls[6].setButtonState(0);
                Controls[7].setButtonState(1);
                Controls[8].setButtonState(0);
                break;

            case 8: //BulletD Button
                //Disable other buttons
                Controls[0].setButtonState(0);
                Controls[1].setButtonState(0);
                Controls[2].setButtonState(0);
                Controls[3].setButtonState(0);
                //Assign corresponding attributes
                Tanks[isServer ? 0 : 1].setBullet(3);
                Controls[5].setButtonState(0);
                Controls[6].setButtonState(0);
                Controls[7].setButtonState(0);
                Controls[8].setButtonState(1);
                break;
        }
    }

    /**
     * Method: resetButtonsAndEvents
     * Description: Method used to change the state of released buttons and
     *              handle relative sound effects
     * @param touchX    -   touch point X
     * @param touchY    -   touch point Y
     * @param dX    - change in X
     * @param dY    - change in Y
     */
    public void resetButtonsAndEvents(float touchX, float touchY, float dX, float dY) {
        int[]   Contents = new int[2];
        //5 Main control buttons
        if(profile.getControlType() == 1) { //Swipe Control
            if(dX != 0 | dY != 0) {
                Tanks[isServer ? 0 : 1].moveX(0, 0);
                Tanks[isServer ? 0 : 1].AdjustShot(0);
                soundFXPlayer.pause(soundFXPlayer.SND_FX_CHARGE); //Pause sound
                soundFXPlayer.pause(soundFXPlayer.SND_FX_TANKAIM);
                soundFXPlayer.pause(soundFXPlayer.SND_FX_TRAVEL);
            }
            if (Controls[4].isIntersect(touchX, touchY)) {
                if (Controls[4].Visible()) {
                    Controls[4].setButtonState(0);
                }
            }
        }
        else {
            soundFXPlayer.pause(soundFXPlayer.SND_FX_CHARGE); //Pause sound
            soundFXPlayer.pause(soundFXPlayer.SND_FX_TANKAIM);
            soundFXPlayer.pause(soundFXPlayer.SND_FX_TRAVEL);

            for (int i = 0; i < 5; ++i) {
                if (Controls[i].isIntersect(touchX, touchY)) {
                    if (Controls[i].Visible()) {
                        Controls[i].setButtonState(0);

                        switch (i) {
                            case 0:
                            case 1:
                                Tanks[isServer ? 0 : 1].moveX(0, 0);
                                break;

                            case 2:
                            case 3:
                                Tanks[isServer ? 0 : 1].AdjustShot(0);
                                break;

                            case 4:
                                soundFXPlayer.play(soundFXPlayer.SND_FX_SHOOT);
                                break;
                        }
                    }
                }
            }
        }
    }

    /**
     * Method: HandleFireEvent
     * Description: Method which will handle all events relative to firing a missile.
     */
    public void HandleFireEvent() {
        int[] Contents;

        if(Controls[4].getButtonState() == 1 && MissileActive == false) {
            if (PowerBar.getProgress() < PowerBar.getMax()) {    //Charge up PowerBar
                //Set missile start position
                missiles[isServer ? 0 : 1][Tanks[isServer ? 0 : 1].getBullet()].setXY(isServer ?
                                Tanks[isServer ? 0 : 1].getRect().right + Math.round(ScaleX * 50) :
                                Tanks[isServer ? 0 : 1].getRect().left - Math.round(ScaleX * 50),
                        Tanks[isServer ? 0 : 1].getRect().top - Math.round(ScaleY * 50));
                //Increment PowerBar
                PowerBar.setProgress(PowerBar.getProgress() + 2);
            } else {  //PowerBar hit maximum limit
                //Reset button
                Controls[4].setButtonState(0);
                //Fire Missile
                missiles[isServer ? 0 : 1][Tanks[isServer ? 0 : 1].getBullet()].setdXdY(Tanks[isServer ? 0 : 1].getAngle(),
                        PowerBar.getProgress() * 1.0, isServer ? true : false);
                missiles[isServer ? 0 : 1][Tanks[isServer ? 0 : 1].getBullet()].setVisible(true);
                //Reset PowerBar
                MissileActive = true;
                ++FireCount;

                if(isBTGame) {
                    Contents = new int[3];
                    //Get tank angle
                    Contents[0] = Tanks[isServer ? 0 : 1].getAngle();
                    //Get tank shot magnitude
                    Contents[1] = PowerBar.getProgress();
                    //Get tank bullet type
                    Contents[2] = Tanks[isServer ? 0 : 1].getBullet();
                    //Send data from corresponding handler
                    if (isServer && btServer != null) {
                        if (btServer.btHandler != null) {
                            btServer.Send(btServer.btHandler.FormatPacket((byte) 0xA3, Contents));
                        }
                    }
                    else if (!isServer && btClient != null) {
                        if (btClient.btHandler != null) {
                            btClient.Send(btClient.btHandler.FormatPacket((byte) 0xA3, Contents));
                        }
                    }
                }
                PowerBar.setProgress(0);
            }
        }
        else if(PowerBar.getProgress() > PowerBar.getMin() && MissileActive == false) {   //Released Fire Button
            //Reset button
            Controls[4].setButtonState(0);
            // Fire Missile
            missiles[isServer ? 0 : 1][Tanks[isServer ? 0 : 1].getBullet()].setdXdY(Tanks[isServer ? 0 : 1].getAngle(),
                    PowerBar.getProgress(), isServer ? true : false);
            missiles[isServer ? 0 : 1][Tanks[isServer ? 0 : 1].getBullet()].setVisible(true);
            //Reset PowerBar
            MissileActive = true;
            ++FireCount;

            if(isBTGame) {
                Contents = new int[3];
                //Get tank angle
                Contents[0] = Tanks[isServer ? 0 : 1].getAngle();
                //Get tank shot magnitude
                Contents[1] = PowerBar.getProgress();
                //Get tank bullet type
                Contents[2] = Tanks[isServer ? 0 : 1].getBullet();
                //Send data from corresponding handler
                if (isServer && btServer != null) {
                    if (btServer.btHandler != null) {
                        btServer.Send(btServer.btHandler.FormatPacket((byte) 0xA3, Contents));
                    }
                } else if (!isServer && btClient != null) {
                    if (btClient.btHandler != null) {
                        btClient.Send(btClient.btHandler.FormatPacket((byte) 0xA3, Contents));
                    }
                }
            }
            PowerBar.setProgress(0);
        }
    }

    /**
     * Method: pollCollisionEvent
     * Description: Method which continuously verifies missile collision on either
     *              Player 1 or Player 2 and applies corresponding damage
     */
    public void pollCollisionEvent()
    {
        int[]   Contents;

        for(int i = 0; i < missiles.length; ++i) {
            for (int j = 0; j < missiles[i].length; ++j) {
                if (missiles[i][j].isVisible()) {
                    //Check if missile collided with Player 1
                    if (missiles[i][j].Collision(Tanks[0], j == 2 ? 50 : 0)) {
                        if(soundFXPlayer != null) {
                            soundFXPlayer.play(soundFXPlayer.SND_FX_TANKHIT);
                        }
                        //Flash tank to signify hit
                        Tanks[0].Flash();
                        missiles[i][j].setVisible(false);
                        //Apply damage
                        Tanks[0].applyDamage(missiles[i][j], j == 3 ? 0 : j,
                                Tanks[isServer ? 0 : 1].getDamageMult() *
                                        (missiles[i][j].bounced() ? 2 : 1));
                        missiles[i][j].setBounced(false);
                        //Play explosion animation & sound
                        explosions[i][j].setXY(missiles[i][j].getX(), missiles[i][j].getY());
                        explosions[i][j].setVisible(true);
                        MissileActive = false;

                        if(isBTGame) {
                            Contents = new int[6];
                            //Get Tanks HP
                            Contents[0] = Tanks[0].getHealth();
                            Contents[1] = Tanks[1].getHealth();
                            //Get Tanks SP
                            Contents[2] = Tanks[0].getShield();
                            Contents[3] = Tanks[1].getShield();
                            //Get Tanks DMG
                            Contents[4] = Tanks[0].getDamageMult();
                            Contents[5] = Tanks[0].getDamageMult();
                            //Send data from corresponding handler
                            if (isServer && btServer != null) {
                                if (btServer.btHandler != null) {
                                    btServer.Send(btServer.btHandler.FormatPacket((byte) 0xA4, Contents));
                                }
                            } else if (!isServer && btClient != null) {
                                if (btClient.btHandler != null) {
                                    btClient.Send(btClient.btHandler.FormatPacket((byte) 0xA4, Contents));
                                }
                            }
                        }
                    }
                    //Check if missile collided with Player 2
                    else if (missiles[i][j].Collision(Tanks[1], j == 2 ? 25 : 0)) {
                        if(soundFXPlayer != null) {
                            soundFXPlayer.play(soundFXPlayer.SND_FX_TANKHIT);
                        }
                        //Flash tank to signify hit
                        Tanks[1].Flash();
                        missiles[i][j].setVisible(false);
                        //Apply damage
                        Tanks[1].applyDamage(missiles[i][j], j == 3 ? 0 : j,
                                Tanks[isServer ? 0 : 1].getDamageMult() *
                                        (missiles[i][j].bounced() ? 2 : 1));
                        missiles[i][j].setBounced(false);
                        //Play explosion animation
                        explosions[i][j].setXY(missiles[i][j].getX(), missiles[i][j].getY());
                        explosions[i][j].setVisible(true);
                        MissileActive = false;

                        if(isBTGame) {
                            Contents = new int[6];
                            //Get Tanks HP
                            Contents[0] = Tanks[0].getHealth();
                            Contents[1] = Tanks[1].getHealth();
                            //Get Tanks SP
                            Contents[2] = Tanks[0].getShield();
                            Contents[3] = Tanks[1].getShield();
                            //Get Tanks DMG
                            Contents[4] = Tanks[0].getDamageMult();
                            Contents[5] = Tanks[0].getDamageMult();
                            //Send data from corresponding handler
                            if (isServer && btServer != null) {
                                if (btServer.btHandler != null) {
                                    btServer.Send(btServer.btHandler.FormatPacket((byte) 0xA4, Contents));
                                }
                            } else if (!isServer && btClient != null) {
                                if (btClient.btHandler != null) {
                                    btClient.Send(btClient.btHandler.FormatPacket((byte) 0xA4, Contents));
                                }
                            }
                        }
                    }
                    else if (missiles[i][j].Collision(specialItem, j == 2 ? 25 : 0)) {
                        if(soundFXPlayer != null) {
                            soundFXPlayer.play(soundFXPlayer.SND_FX_DAMAGE1 + j);
                        }

                        if(specialItem.isVisible()) {
                            if(i == 0 && isServer) {
                                switch(specialItem.getType()) {
                                    case 0:
                                        Tanks[0].setHealth(1337);
                                        break;

                                    case 1:
                                        Tanks[0].setShield(1337);
                                        break;

                                    case 2:
                                        Tanks[0].setDamageMult(Tanks[0].getDamageMult()*2);
                                        break;
                                }
                            }
                            else if(i == 1 && !isServer) {
                                switch(specialItem.getType()) {
                                    case 0:
                                        Tanks[1].setHealth(1337);
                                        break;

                                    case 1:
                                        Tanks[1].setShield(1337);
                                        break;

                                    case 2:
                                        Tanks[1].setDamageMult(Tanks[1].getDamageMult() * 2);
                                        break;
                                }
                            }
                            //Process missile/explosion animation
                            specialItem.setVisible(false);
                            missiles[i][j].setVisible(false);
                            missiles[i][j].setBounced(false);
                            explosions[i][j].setXY(missiles[i][j].getX(), missiles[i][j].getY());
                            explosions[i][j].setVisible(true);
                            MissileActive = false;

                            if(isBTGame) {
                                //Setup packet to send to other client
                                Contents = new int[6];
                                //Get Tanks HP
                                Contents[0] = Tanks[0].getHealth();
                                Contents[1] = Tanks[1].getHealth();
                                //Get Tanks SP
                                Contents[2] = Tanks[0].getShield();
                                Contents[3] = Tanks[1].getShield();
                                //Get Tanks DMG
                                Contents[4] = Tanks[0].getDamageMult();
                                Contents[5] = Tanks[0].getDamageMult();
                                //Send data from corresponding handler
                                if (isServer && btServer != null) {
                                    if (btServer.btHandler != null) {
                                        btServer.Send(btServer.btHandler.FormatPacket((byte)0xA4, Contents));
                                        btServer.Send(btServer.btHandler.FormatPacket((byte)0xA9, Contents));
                                    }
                                } else if (!isServer && btClient != null) {
                                    if (btClient.btHandler != null) {
                                        btClient.Send(btClient.btHandler.FormatPacket((byte) 0xA4, Contents));
                                        btClient.Send(btClient.btHandler.FormatPacket((byte) 0xA9, Contents));
                                    }
                                }
                            }
                        }
                    }
                    //Check if missile traveled out of bounds
                    else if (missiles[i][j].getX() > displayWidth || missiles[i][j].getX() < 0 ||
                            missiles[i][j].getY() > displayHeight) {
                        //Do nothing, reset attributes
                        missiles[i][j].setVisible(false);
                        missiles[i][j].setBounced(false);
                        MissileActive = false;
                    }
                    else
                    {
                        //Missile may have hit a platform
                        for (int k = 0; k < platforms.length; ++k) {
                            if (missiles[i][j].Collision(platforms[k], 0)) {
                                if(soundFXPlayer != null) {
                                    soundFXPlayer.play(soundFXPlayer.SND_FX_DAMAGE1+j);
                                }
                                //Check for special Bouncing Bullet
                                if(j == 3 && missiles[i][j].bounced() == false) {
                                    int[]   dXY = {missiles[i][j].getdX(), missiles[i][j].getdY()};

                                    missiles[i][j].setdY(dXY[1] * -1);
                                    missiles[i][j].setBounced(true);
                                    missiles[i][j].update();
                                }
                                else {
                                    //Missile collided with platform
                                    explosions[i][j].setXY(missiles[i][j].getX(), missiles[i][j].getY());
                                    //Play explosion animation
                                    explosions[i][j].setVisible(true);
                                    missiles[i][j].setVisible(false);
                                    missiles[i][j].setBounced(false);
                                    MissileActive = false;
                                }
                            }
                        }
                    }
                }
            }
        }
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

        if(btserver == null && btclient == null) {
            isBTGame = false;
        }
        else {
            isBTGame = true;

            if (server == true) {   //Server
                btServer = btserver;
            } else {
                btClient = btclient;
            }
        }
    }

    /*      Getters and Setters     */
    public int[] getHP() {
        int[] hp = {Tanks[0].getHealth(), Tanks[1].getHealth()};

        return hp;
    }

    public void setHP(int hp, int player) {
        if(Tanks != null) {
            Tanks[player].setHealth(hp);
        }
    }

    public int[] getSP() {
        int[] sp = {Tanks[0].getShield(), Tanks[1].getShield()};

        return sp;
    }

    public void setSP(int sp, int player) {
        if(Tanks != null) {
            Tanks[player].setShield(sp);
        }
    }

    public void setDMG(int dmg, int player) {
        if(Tanks != null) {
            Tanks[player].setDamageMult(dmg);
        }
    }

    public int[] getPX() {
        int [] px = {Tanks[0].getX(), Tanks[1].getX()};

        return px;
    }

    public void setPX(int[] XCoords) {
        if(Tanks != null) {
            Tanks[0].setX(XCoords[0]);
            Tanks[1].setX(XCoords[1]);
        }
    }

    public int[] getPY() {
        int[] py = {Tanks[0].getY(), Tanks[1].getY()};

        return py;
    }

    public void setPY(int[] YCoords) {
        if(Tanks != null) {
            Tanks[0].setY(YCoords[0]);
            Tanks[1].setY(YCoords[1]);
        }
    }

    public void setProfile(Profile p) {
        profile = p;
    }

    //The Following Method(s) are Intended for TESTING PURPOSES ONLY!!!
    public Button getButton(int btnIndex) {
        return Controls[btnIndex];
    }

    public ProgressBar getPowerBar() {
        return PowerBar;
    }

    public Missile getMissile(int TankIndex, int MissileIndex) {
        return missiles[TankIndex][MissileIndex];
    }

    public Tank getTank(int TankIndex) {
        return Tanks[TankIndex];
    }

    public Explosion getExplosion(int TankIndex, int ExplosionIndex) {
        return explosions[TankIndex][ExplosionIndex];
    }

    public SpecialItem getSpecialItem() {
        return specialItem;
    }

    public void setMissileStatus(boolean status) {
        MissileActive = status;
    }
}
