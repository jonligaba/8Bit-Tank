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
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Class: MatchHandler
 * Description: This class will initialize and handle actual game play.
 */
public class MatchHandler {
    private BluetoothServer     btServer;
    private BluetoothClient     btClient;
    private Handler             mHandler;
    private boolean             isServer;
    private float               ScaleX;
    private float               ScaleY;
    //Game Resources
    private SoundFXPlayer   soundFXPlayer;
    private Background      background;
    private Explosion[][]   explosions;
    private Platform[]      platforms;
    private Missile[][]     missiles;
    private Tank[]          Tanks;
    //Control Resources
    private Button[]        Controls;
    private ProgressBar     PowerBar;
    //Application Resources
    private Paint       paint;
    private Context     context;
    private int         displayWidth;
    private int         displayHeight;
    //Active game-play Attributes
    private int         MatchType;
    private int         FireCount;
    private boolean     MissileActive;
    //Game Constraints
    private int[]   SpawnX;
    private int[]   SpawnY;
    private int[]   LimitX1;
    private int[]   LimitX2;
    //Features
    private StormSystem     stormSystem;

    /**
     * Method: MatchHandler
     * Description: Method which intializes and assigns values to resources
     *              and attributes.
     * @param conText - Reference to application context
     */
    public MatchHandler(Context conText, float scaleX, float scaleY, Handler msgHandler) {
        ScaleX = scaleX;
        ScaleY = scaleY;
        context = conText;
        mHandler = msgHandler;
        //Initialize paint
        paint = new Paint();
        //Initialize Game Resources
        Tanks = new Tank[2];
        missiles = new Missile[2][3];
        explosions = new Explosion[2][3];
        soundFXPlayer = new SoundFXPlayer(context);
        //Initialize Control Resources
        Controls = new Button[8];
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
        //Toggle Storm System
        if(FireCount > 10) {
            int[]   Contents;

            FireCount = 0;
            stormSystem.update();

            if(stormSystem.getStormType() != 0) {
                Contents = stormSystem.getStormParameters();
                soundFXPlayer.play(SoundFXPlayer.SND_FX_WINDSTM);

                if(isServer && btServer != null) {
                    if(btServer.btHandler != null) {
                        btServer.Send(btServer.btHandler.FormatPacket((byte)0xA5, Contents));
                    }
                }
                else if(!isServer && btClient != null) {
                    if(btClient.btHandler != null) {
                        btClient.Send(btClient.btHandler.FormatPacket((byte)0xA5, Contents));
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
            }
        }
        //Update explosions
        for(int i = 0; i < explosions.length; ++i) {
            for(int j = 0; j < explosions[i].length; ++j) {
                explosions[i][j].update();
            }
        }
        //Check if a tank is dead, display win/lose
        if(MatchType == 2) {
            if(Tanks[0].getHealth() == 0) {
                if(isServer) {
                    mHandler.obtainMessage(2, 0, -1, 0).sendToTarget();
                }
                else {
                    mHandler.obtainMessage(3, 0, -1, 0).sendToTarget();
                }
            }
            else if(Tanks[1].getHealth() == 0) {
                if(isServer) {
                    mHandler.obtainMessage(3, 0, -1, 0).sendToTarget();
                }
                else {
                    mHandler.obtainMessage(2, 0, -1, 0).sendToTarget();
                }
            }
        }
        //Update players
        Tanks[0].update();
        Tanks[1].update();
        //Poll for Fire button event
        HandleFireEvent();
        //Poll for possible collisions
        pollCollisionEvent();
        //Update time and turn
        HandleTimingEvent();
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
            for(int i = 0; i < 8; ++i) {
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
           int[]    Coords = isServer ? btServer.btHandler.ExtractIntArray(inData) :
                                     btClient.btHandler.ExtractIntArray(inData);

            //Assign corresponding tank position
            Tanks[isServer ? 1 : 0].setX(Math.round(ScaleX * Coords[0]));
            Tanks[isServer ? 1 : 0].setY(Math.round(ScaleY * Coords[1]));
        }
        else if(inData[0] == (byte)0xA2) {  //Other tank is aiming
            //Get tank aim angle
            int[]   Angle = isServer ? btServer.btHandler.ExtractIntArray(inData) :
                    btClient.btHandler.ExtractIntArray(inData);
            //Assign tank angle
            Tanks[isServer ? 1 : 0].setAngle(Angle[0]);
        }
        else if(inData[0] == (byte)0xA3) {  //Other tank has fired shot
            //Get angle, magnitude and bullet type
            int[]   ShotParams = isServer ? btServer.btHandler.ExtractIntArray(inData) :
                    btClient.btHandler.ExtractIntArray(inData);
            //Set missile start position
            missiles[isServer ? 1 : 0][ShotParams[2]].setXY(isServer ?
                            Tanks[isServer ? 1 : 0].getRect().left - Math.round(ScaleX * 50) :
                            Tanks[isServer ? 1 : 0].getRect().right + Math.round(ScaleX * 50),
                    Tanks[isServer ? 0 : 1].getRect().top - Math.round(ScaleY * 50));
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
        }
        else if(inData[0] == (byte)0xA5) {  //Storm update
            int[]   stormParams = isServer ? btServer.btHandler.ExtractIntArray(inData) :
                    btClient.btHandler.ExtractIntArray(inData);

            stormSystem.setStormParameters(stormParams);
            soundFXPlayer.play(SoundFXPlayer.SND_FX_WINDSTM);
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
     */
    public void InitResources(int dWidth, int dHeight, Bitmap wallpaper,
                              Rect[] PlatformList, int[] PlatformColorList, boolean[] PlatformVisibleList,
                              Bitmap[] BulletBtnImages, Bitmap[] MissileImages, Bitmap[] ExplosionImages,
                              Bitmap[] ButtonImages, Bitmap Compass) {
        //Initialize features
        stormSystem = new StormSystem(Compass);
        //Initialize background
        displayWidth = dWidth;
        displayHeight = dHeight;
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
                Math.round(ScaleY * 150), true, true);  //Left
        Controls[1] = new Button(ButtonImages[1], 150, 150, Math.round(ScaleX * 210),
                displayHeight-Math.round(ScaleY * 190), Math.round(ScaleX * 150),
                Math.round(ScaleY * 150), true, true); //Right
        Controls[2] = new Button(ButtonImages[2], 150, 150, Math.round(ScaleX * 370),
                displayHeight-Math.round(ScaleY * 190), Math.round(ScaleX * 150),
                Math.round(ScaleY * 150), true, true);    //Up
        Controls[3] = new Button(ButtonImages[3], 150, 150, Math.round(ScaleX * 530),
                displayHeight-Math.round(ScaleY * 190), Math.round(ScaleX * 150),
                Math.round(ScaleY * 150), true, true);  //Down
        Controls[4] = new Button(ButtonImages[4], 150, 150,
                displayWidth-Math.round(ScaleX * 200), displayHeight-Math.round(ScaleY * 190),
                Math.round(ScaleX * 150), Math.round(ScaleY * 150), true, true);//Fire
        Controls[5] = new Button(BulletBtnImages[0], 100, 100,
                displayWidth-Math.round(ScaleX * 540), displayHeight-Math.round(ScaleY * 165),
                Math.round(ScaleX * 100), Math.round(ScaleY * 100), true, true);
        Controls[6] = new Button(BulletBtnImages[1], 100, 100,
                displayWidth-Math.round(ScaleX * 430), displayHeight-Math.round(ScaleY * 165),
                Math.round(ScaleX * 100), Math.round(ScaleY * 100), true, true);
        Controls[7] = new Button(BulletBtnImages[2], 100, 100,
                displayWidth-Math.round(ScaleX * 320), displayHeight-Math.round(ScaleY * 165),
                Math.round(ScaleX * 100), Math.round(ScaleY * 100), true, true);

        for(int i = 0; i < 2; ++i) {
                //Create Standby Missiles
            missiles[i][0] = new Missile(MissileImages[0], 50, 50,
                    Math.round(ScaleX * 50), Math.round(ScaleY * 50), true, i + 1, 10, 8, ScaleX);
            missiles[i][1] = new Missile(MissileImages[1], 50, 50,
                    Math.round(ScaleX * 50), Math.round(ScaleY * 50), true, i + 1, 10, 8, ScaleX);
            missiles[i][2] = new Missile(MissileImages[2], 50, 50,
                    Math.round(ScaleX * 50), Math.round(ScaleY * 50), true, i + 1, 10, 8, ScaleX);
                //Create Standby Explosions
            explosions[i][0] = new Explosion(ExplosionImages[0], 50, 50, Math.round(ScaleX * 50),
                    Math.round(ScaleY * 50), true, 8);
            explosions[i][1] = new Explosion(ExplosionImages[1], 50, 50, Math.round(ScaleX * 50),
                    Math.round(ScaleY * 50), true, 8);
            explosions[i][2] = new Explosion(ExplosionImages[2], 50, 50, Math.round(ScaleX * 50),
                    Math.round(ScaleY * 50), true, 8);
        }
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
        //TODO: Toggle switch to enable control
        Controls[5].setButtonState(1);
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
                resetButtonsAndEvents(1.337f, -1.337f);
                buttons[i].setButtonState(1);
                mainthread.Sleep(100);
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
        int[] Contents;

        switch (getButtonPressed(touchX, touchY, Controls, mainthread)) {
            case 0: //Left Button
                Contents = new int[2];

                soundFXPlayer.play(SoundFXPlayer.SND_FX_TRAVEL);
                Tanks[isServer ? 0 : 1].moveX(-1, LimitX1[isServer ? 0 : 1]);
                //Get Tank X-Coordinate
                Contents[0] = (ScaleX <= 1 ?
                        Math.round(Tanks[isServer ? 0 : 1].getX() / ScaleX) :
                        Math.round(Tanks[isServer ? 0 : 1].getX() * ScaleX));
                //Get Tank Y-Coordinate
                Contents[1] = (ScaleY <= 1 ?
                        Math.round(Tanks[isServer ? 0 : 1].getY() / ScaleY) :
                        Math.round(Tanks[isServer ? 0 : 1].getY() * ScaleY));
                //Send data from corresponding handler
                if(isServer && btServer != null) {
                    if (btServer.btHandler != null) {
                        btServer.Send(btServer.btHandler.FormatPacket((byte) 0xA1, Contents));
                    }
                }
                else if(!isServer && btClient != null) {
                    if (btClient.btHandler != null) {
                        btClient.Send(btClient.btHandler.FormatPacket((byte) 0xA1, Contents));
                    }
                }
                break;

            case 1: //Right Button
                Contents = new int[2];

                soundFXPlayer.play(SoundFXPlayer.SND_FX_TRAVEL);
                Tanks[isServer ? 0 : 1].moveX(1, LimitX2[isServer ? 0 : 1]);
                //Get Tank X-Coordinate
                Contents[0] = (ScaleX <= 1 ?
                        Math.round(Tanks[isServer ? 0 : 1].getX() / ScaleX) :
                        Math.round(Tanks[isServer ? 0 : 1].getX() * ScaleX));
                //Get Tank Y-Coordinate
                Contents[1] = (ScaleY <= 1 ?
                        Math.round(Tanks[isServer ? 0 : 1].getY() / ScaleY) :
                        Math.round(Tanks[isServer ? 0 : 1].getY() * ScaleY));
                //Send data from corresponding handler
                if(isServer && btServer != null) {
                    if (btServer.btHandler != null) {
                        btServer.Send(btServer.btHandler.FormatPacket((byte) 0xA1, Contents));
                    }
                }
                else if(!isServer && btClient != null) {
                    if (btClient.btHandler != null) {
                        btClient.Send(btClient.btHandler.FormatPacket((byte) 0xA1, Contents));
                    }
                }
                break;

            case 2: //Up Button
                Contents = new int[1];
                soundFXPlayer.play(SoundFXPlayer.SND_FX_TANKAIM);
                Tanks[isServer ? 0 : 1].AdjustShot(-1);
                //Get Tank Angle
                Contents[0] = Tanks[isServer ? 0 : 1].getAngle();
                //Send data through corresponding handler
                if(isServer && btServer != null) {
                    if (btServer.btHandler != null) {
                        btServer.Send(btServer.btHandler.FormatPacket((byte) 0xA2, Contents));
                    }
                }
                else if(!isServer && btClient != null) {
                    if (btClient.btHandler != null) {
                        btClient.Send(btClient.btHandler.FormatPacket((byte) 0xA2, Contents));
                    }
                }
                break;

            case 3: //Down Button
                Contents = new int[1];
                soundFXPlayer.play(SoundFXPlayer.SND_FX_TANKAIM);
                Tanks[isServer ? 0 : 1].AdjustShot(1);
                //Get Tank Angle
                Contents[0] = Tanks[isServer ? 0 : 1].getAngle();
                //Send data through corresponding handler
                if(isServer && btServer != null) {
                    if (btServer.btHandler != null) {
                        btServer.Send(btServer.btHandler.FormatPacket((byte) 0xA2, Contents));
                    }
                }
                else if(!isServer && btClient != null) {
                    if (btClient.btHandler != null) {
                        btClient.Send(btClient.btHandler.FormatPacket((byte) 0xA2, Contents));
                    }
                }
                break;

            case 4:
                //Nothing happens here but sound effects
                soundFXPlayer.play(SoundFXPlayer.SND_FX_CHARGE);
                break;

            case 5: //BulletA Button
                //Assign corresponding attributes
                Tanks[isServer ? 0 : 1].setBullet(0);
                Controls[5].setButtonState(1);
                Controls[6].setButtonState(0);
                Controls[7].setButtonState(0);
                break;

            case 6: //BulletB Button
                //Assign corresponding attributes
                Tanks[isServer ? 0 : 1].setBullet(1);
                Controls[5].setButtonState(0);
                Controls[6].setButtonState(1);
                Controls[7].setButtonState(0);
                break;

            case 7: //BulletC Button
                //Assign corresponding attributes
                Tanks[isServer ? 0 : 1].setBullet(2);
                Controls[5].setButtonState(0);
                Controls[6].setButtonState(0);
                Controls[7].setButtonState(1);
                break;
        }
    }

    /**
     * Method: resetButtonsAndEvents
     * Description: Method used to change the state of released buttons and
     *              handle relative sound effects
     * @param touchX    -   touch point X
     * @param touchY    -   touch point Y
     */
    public void resetButtonsAndEvents(float touchX, float touchY) {
        //Special Case
        if(touchX == 1.337f && touchY == 1.337f) {
            for(int i = 0; i < 5; ++i) {
                Controls[i].setButtonState(0);
            }
        }
        //5 Main control buttons
        for(int i = 0; i < 5; ++i) {
            if(Controls[i].isIntersect(touchX, touchY)) {
                if(i <= 1) {    //Travel buttons
                    soundFXPlayer.pause(SoundFXPlayer.SND_FX_TRAVEL);
                } else if(i <= 3) { //Aim buttons
                        soundFXPlayer.pause(SoundFXPlayer.SND_FX_TANKAIM);
                }
                //Set buttons to released
                Controls[i].setButtonState(0);
            }
        }
        //Reset movement and shots
            Tanks[isServer ? 0 : 1].moveX(0, 0);
            Tanks[isServer ? 0 : 1].AdjustShot(0);
    }

    /**
     * Method: HandleFireEvent
     * Description: Method which will handle all events relative to firing a missile.
     */
    private void HandleFireEvent() {
        int[] Contents;

        if(Controls[4].getButtonState() == 1 && MissileActive == false) {
            if (PowerBar.getProgress() < PowerBar.getMax()) {    //Charge up PowerBar
                //Set missile start position
                missiles[isServer ? 0 : 1][Tanks[isServer ? 0 : 1].getBullet()].setXY(isServer ?
                                Tanks[isServer ? 0 : 1].getRect().right + Math.round(ScaleX * 50) :
                                Tanks[isServer ? 0 : 1].getRect().left - Math.round(ScaleX * 50),
                        Tanks[isServer ? 0 : 1].getRect().top - Math.round(ScaleY * 50));
                //Increment PowerBar
                PowerBar.setProgress(PowerBar.getProgress() + 1);
            } else {  //PowerBar hit maximum limit
                Contents = new int[3];
                //Reset button
                Controls[4].setButtonState(0);
                //Fire Missile
                soundFXPlayer.pause(SoundFXPlayer.SND_FX_CHARGE);
                soundFXPlayer.play(SoundFXPlayer.SND_FX_SHOOT);
                missiles[isServer ? 0 : 1][Tanks[isServer ? 0 : 1].getBullet()].setdXdY(Tanks[isServer ? 0 : 1].getAngle(),
                        PowerBar.getProgress() * 1.0 * ScaleX, isServer ? true : false);
                missiles[isServer ? 0 : 1][Tanks[isServer ? 0 : 1].getBullet()].setVisible(true);
                //Reset PowerBar
                MissileActive = true;
                ++FireCount;

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
                }else if (!isServer && btClient != null) {
                    if (btClient.btHandler != null) {
                        btClient.Send(btClient.btHandler.FormatPacket((byte) 0xA3, Contents));
                    }
                }
                PowerBar.setProgress(0);
            }
        }
        else if(PowerBar.getProgress() > PowerBar.getMin() && MissileActive == false) {   //Released Fire Button
            Contents = new int[3];
            //Reset button
            Controls[4].setButtonState(0);
            // Fire Missile
            soundFXPlayer.pause(SoundFXPlayer.SND_FX_CHARGE);
            soundFXPlayer.play(SoundFXPlayer.SND_FX_SHOOT);
            missiles[isServer ? 0 : 1][Tanks[isServer ? 0 : 1].getBullet()].setdXdY(Tanks[isServer ? 0 : 1].getAngle(),
                    PowerBar.getProgress() * 1.0 * ScaleX, isServer ? true : false);
            missiles[isServer ? 0 : 1][Tanks[isServer ? 0 : 1].getBullet()].setVisible(true);
            //Reset PowerBar
            MissileActive = true;
            ++FireCount;

            //Get tank angle
            Contents[0] = Tanks[isServer ? 0 : 1].getAngle();
            //Get tank shot magnitude
            Contents[1] = PowerBar.getProgress();
            //Get tank bullet type
            Contents[2] = Tanks[isServer ? 0 : 1].getBullet();
            //Send data from corresponding handler
            if (isServer && btServer != null) {
                if(btServer.btHandler != null) {
                    btServer.Send(btServer.btHandler.FormatPacket((byte) 0xA3, Contents));
                }
            } else if (!isServer && btClient != null) {
                if(btClient.btHandler != null) {
                    btClient.Send(btClient.btHandler.FormatPacket((byte) 0xA3, Contents));
                }
            }
            PowerBar.setProgress(0);
        }
    }

    /**
     * Method: HandleTimingEvent
     * Description: Method which handles turn switching, and time updating.
     */
    private void HandleTimingEvent() {
        //TODO: Find use for this or remove
    }

    /**
     * Method: pollCollisionEvent
     * Description: Method which continuously verifies missile collision on either
     *              Player 1 or Player 2 and applies corresponding damage
     */
    private void pollCollisionEvent()
    {
        int[]   Contents;

        for(int i = 0; i < missiles.length; ++i) {
            for (int j = 0; j < missiles[i].length; ++j) {
                if (missiles[i][j].isVisible()) {
                    //Check if missile collided with Player 1
                    if (missiles[i][j].Collision(Tanks[0], j == 2 ? 50 : 0)) {
                        Contents = new int[4];
                        //Flash tank to signify hit
                        Tanks[0].Flash();
                        missiles[i][j].setVisible(false);
                        //Apply damage
                        System.out.printf("Tank[%d] HIT! bullet: %d mult: %d", 0, j,
                                Tanks[isServer ? 0 : 1].getDamageMult());
                        Tanks[0].applyDamage(missiles[i][j], j, Tanks[isServer ? 0 : 1].getDamageMult());
                        //Play explosion animation & sound
                        soundFXPlayer.play(SoundFXPlayer.SND_FX_TANKHIT);
                        explosions[i][j].setXY(missiles[i][j].getX(), missiles[i][j].getY());
                        explosions[i][j].setVisible(true);
                        MissileActive = false;

                        //Get Tanks HP
                        Contents[0] = Tanks[0].getHealth();
                        Contents[1] = Tanks[1].getHealth();
                        //Get Tanks SP
                        Contents[2] = Tanks[0].getShield();
                        Contents[3] = Tanks[1].getShield();
                        //Send data from corresponding handler
                        if(isServer && btServer != null) {
                            if(btServer.btHandler != null) {
                                btServer.Send(btServer.btHandler.FormatPacket((byte) 0xA4, Contents));
                            }
                        }
                        else if(!isServer && btClient != null) {
                            if(btClient.btHandler != null) {
                                btClient.Send(btClient.btHandler.FormatPacket((byte) 0xA4, Contents));
                            }
                        }
                    }
                    //Check if missile collided with Player 2
                    else if (missiles[i][j].Collision(Tanks[1], j == 2 ? 25 : 0)) {
                        Contents = new int[4];
                        //Flash tank to signify hit
                        Tanks[1].Flash();
                        missiles[i][j].setVisible(false);
                        //Apply damage
                        System.out.printf("Tank[%d] HIT! bullet: %d mult: %d", 1, j,
                                Tanks[isServer ? 0 : 1].getDamageMult());
                        Tanks[1].applyDamage(missiles[i][j], j, Tanks[isServer ? 0 : 1].getDamageMult());
                        //Play explosion animation
                        soundFXPlayer.play(SoundFXPlayer.SND_FX_TANKHIT);
                        explosions[i][j].setXY(missiles[i][j].getX(), missiles[i][j].getY());
                        explosions[i][j].setVisible(true);
                        MissileActive = false;
                        //Get Tanks HP
                        Contents[0] = Tanks[0].getHealth();
                        Contents[1] = Tanks[1].getHealth();
                        //Get Tanks SP
                        Contents[2] = Tanks[0].getShield();
                        Contents[3] = Tanks[1].getShield();
                        //Send data from corresponding handler
                        if(isServer && btServer != null) {
                            if(btServer.btHandler != null) {
                                btServer.Send(btServer.btHandler.FormatPacket((byte) 0xA4, Contents));
                            }
                        }
                        else if(!isServer && btClient != null) {
                            if(btClient.btHandler != null) {
                                btClient.Send(btClient.btHandler.FormatPacket((byte) 0xA4, Contents));
                            }
                        }
                    }
                    //Check if missile traveled out of bounds
                    else if (missiles[i][j].getX() > displayWidth || missiles[i][j].getX() < 0 ||
                            missiles[i][j].getY() > displayHeight) {
                        //Do nothing, reset attributes
                        missiles[i][j].setVisible(false);
                        MissileActive = false;
                    }
                    else
                    {
                        //Missile may have hit a platform
                        for (int k = 0; k < platforms.length; ++k)
                        {
                            if (missiles[i][j].Collision(platforms[k], 0))
                            {
                                //Missile collided with platform
                                soundFXPlayer.play(SoundFXPlayer.SND_FX_DAMAGE1 + i);
                                explosions[i][j].setXY(missiles[i][j].getX(), missiles[i][j].getY());
                                //Play explosion animation
                                explosions[i][j].setVisible(true);
                                missiles[i][j].setVisible(false);
                                MissileActive = false;
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

        if(server == true) {   //Server
            btServer = btserver;
        }
        else {
            btClient = btclient;
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
}
