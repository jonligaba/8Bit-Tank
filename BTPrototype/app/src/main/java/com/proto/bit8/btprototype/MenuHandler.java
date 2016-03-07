package com.proto.bit8.btprototype;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Class: MenuHandler
 * Description: Class which handles menu manipulation and match parameter
 *              assignments.
 */
public class MenuHandler {
    private BluetoothServer btServer;
    private BluetoothClient btClient;
    private SoundFXPlayer   soundFX;
    private Profile         profile;

    private float           ScaleX;
    private float           ScaleY;

    private int             MenuID;
    private int             dispWidth;
    private int             dispHeight;
    private int[]           GameParams;
    private Handler[]       MsgHandlers;
    private boolean[]       Unlockables;
    private Button[][]      MenuButtons;
    private Background[]    backgrounds;

    private int[][]         tankStats;
    private Tank[]          LselTanks;
    private Tank[]          RselTanks;
    private Bitmap[]        selBackgrounds;
    private ProgressBar[][] healthBars;
    private ProgressBar[][] shieldBars;
    private ProgressBar[][] damageBars;

    private Paint           paint;

    /**
     * Method: MenuHandler
     * Description: Constructor which assigns message handlers and bitmaps for menu objects.
     * @param dWidth    -   display width
     * @param dHeight   -   display height
     * @param scaleX    -   scale X
     * @param scaleY    -   scaleY
     * @param MenuBmp   -   Menu background bitmaps
     * @param MenuBtnBmps   -   Menu    button Bitmaps
     * @param msgHandler    -   Bluetooth
     * @param cmpHandler    -   completion handler for menu transition
     * @param soundfx   -   sound effects
     */
    public MenuHandler(int dWidth, int dHeight, float scaleX, float scaleY, Bitmap[] MenuBmp,
                       Bitmap[] MenuBtnBmps, Handler msgHandler, Handler cmpHandler,
                       SoundFXPlayer soundfx) {
        //Initialize attributes
        MenuID = 0;
        ScaleX = scaleX;
        ScaleY = scaleY;
        soundFX = soundfx;
        dispWidth = dWidth;
        dispHeight = dHeight;
        paint = new Paint();
        MsgHandlers = new Handler[2];
        MsgHandlers[0] = msgHandler; //Bluetooth Message Handler
        MsgHandlers[1] = cmpHandler; //Completion Match Making Handler
        //Parameters for match making (default)
        GameParams = new int[5];
        GameParams[0] = 0;  //GameType
        GameParams[1] = 1;  //Primary Player
        GameParams[2] = 0;  //Level_id
        GameParams[3] = 0;  //Primary Tank ID
        GameParams[4] = 0;  //Secondary Tank ID
        //Initialize tank stats
        tankStats = new int[2][3];
        tankStats[0][0] = 25;
        tankStats[1][0] = 25;
        tankStats[0][1] = 25;
        tankStats[1][1] = 25;
        tankStats[0][2] = 1;
        tankStats[1][2] = 1;
        //Initialize unlockables array
        Unlockables = new boolean[7];
        Unlockables[0] = false;
        Unlockables[1] = false;
        Unlockables[2] = false;
        Unlockables[3] = false;
        Unlockables[4] = false;
        Unlockables[5] = false;
        Unlockables[6] = false;
        //Menu Backgrounds
        backgrounds = new Background[5];
        backgrounds[0] = new Background(MenuBmp[0], dispWidth, dispHeight, true);
        backgrounds[1] = new Background(MenuBmp[1], dispWidth, dispHeight, true);
        backgrounds[2] = new Background(MenuBmp[2], dispWidth, dispHeight, true);
        backgrounds[3] = new Background(MenuBmp[3], dispWidth, dispHeight, true);
        backgrounds[4] = new Background(MenuBmp[4], dispWidth, dispHeight, true);
        //Button array init
        MenuButtons = new Button[2][];  //Two Menus with Buttons
        //1-Player, 2-Player, CPU, Player, BT host, BT join
        MenuButtons[0] = new Button[6];
        MenuButtons[0][0] = new Button(MenuBtnBmps[0], 200, 100, Math.round(ScaleX * 50),
                dispHeight - Math.round(ScaleY * 245), Math.round(ScaleX * 400),
                Math.round(ScaleY * 200), true, true);
        MenuButtons[0][1] = new Button(MenuBtnBmps[1], 200, 100, Math.round(ScaleX * 50),
                dispHeight - Math.round(ScaleY * 245), Math.round(ScaleX * 400),
                Math.round(ScaleY * 200), true, false);
        MenuButtons[0][2] = new Button(MenuBtnBmps[2], 200, 100, Math.round(ScaleX * 500),
                dispHeight-Math.round(ScaleY * 145), Math.round(ScaleX * 200),
                Math.round(ScaleY * 100), true, true);
        MenuButtons[0][3] = new Button(MenuBtnBmps[3], 200, 100, Math.round(ScaleX * 500),
                dispHeight-Math.round(ScaleY * 145), Math.round(ScaleX * 200),
                Math.round(ScaleY * 100), true, false);
        MenuButtons[0][4] = new Button(MenuBtnBmps[4], 200, 100, Math.round(ScaleX * 715),
                dispHeight-Math.round(ScaleY * 145), Math.round(ScaleX * 200),
                Math.round(ScaleY * 100), true, false);
        MenuButtons[0][5] = new Button(MenuBtnBmps[11], 100, 100, dispWidth-Math.round(ScaleX * 100),
                dispHeight-Math.round(ScaleY * 100), Math.round(ScaleX * 100),
                Math.round(ScaleY * 100), true, true);
        //Stage_L, Stage_R, Tank1_L, Tank1_R, Tank2_L, Tank2_R, Start, Back
        MenuButtons[1] = new Button[20];
        MenuButtons[1][0] = new Button(MenuBtnBmps[5], 100, 100, dispWidth/2 - Math.round(ScaleX * 475),
                Math.round(ScaleY * 200), Math.round(ScaleX * 100), Math.round(ScaleY * 100),
                true, true);
        MenuButtons[1][1] = new Button(MenuBtnBmps[6], 100, 100, dispWidth/2 + Math.round(ScaleX * 375),
                Math.round(ScaleY * 200), Math.round(ScaleX * 100), Math.round(ScaleY * 100),
                true, true);
        MenuButtons[1][2] = new Button(MenuBtnBmps[5], 100, 100, Math.round(ScaleX * 50),
                dispHeight - Math.round(ScaleY * 105), Math.round(ScaleX * 100),
                Math.round(ScaleY * 100), true, false);
        MenuButtons[1][3] = new Button(MenuBtnBmps[6], 100, 100, Math.round(ScaleX * 550),
                dispHeight - Math.round(ScaleY * 105), Math.round(ScaleX * 100),
                Math.round(ScaleY * 100), true, false);
        MenuButtons[1][4] = new Button(MenuBtnBmps[5], 100, 100, dispWidth - Math.round(ScaleX * 650),
                dispHeight - Math.round(ScaleY * 105), Math.round(ScaleX * 100),
                Math.round(ScaleY * 100), true, false);
        MenuButtons[1][5] = new Button(MenuBtnBmps[6], 100, 100, dispWidth - Math.round(ScaleX * 175),
                dispHeight - Math.round(ScaleY * 105), Math.round(ScaleX * 100),
                Math.round(ScaleY * 100), true, false);
        MenuButtons[1][6] = new Button(MenuBtnBmps[7], 100, 100, dispWidth/2 - Math.round(ScaleX * 50),
                dispHeight - Math.round(ScaleY * 150), Math.round(ScaleX * 100),
                Math.round(ScaleY * 100), true, true);
        MenuButtons[1][7] = new Button(MenuBtnBmps[8], 200, 100, dispWidth - Math.round(ScaleX * 200),
                0, Math.round(ScaleX * 200), Math.round(ScaleY * 100), true, true);

        //Primary Player Customize Tank Buttons
        MenuButtons[1][8] = new Button(MenuBtnBmps[9], 150, 150, Math.round(ScaleX * 480),
                dispHeight/2 + Math.round(ScaleY * 100), Math.round(ScaleX * 75),
                Math.round(ScaleY * 40), true, false);
        MenuButtons[1][9] = new Button(MenuBtnBmps[10], 150, 150, Math.round(ScaleX * 580),
                dispHeight/2 + Math.round(ScaleY * 100), Math.round(ScaleX * 75),
                Math.round(ScaleY * 40), true, false);
        MenuButtons[1][10] = new Button(MenuBtnBmps[9], 150, 150, Math.round(ScaleX * 480),
                dispHeight/2 + Math.round(ScaleY * 155), Math.round(ScaleX * 75),
                Math.round(ScaleY * 40), true, false);
        MenuButtons[1][11] = new Button(MenuBtnBmps[10], 150, 150, Math.round(ScaleX * 580),
                dispHeight/2 + Math.round(ScaleY * 155), Math.round(ScaleX * 75),
                Math.round(ScaleY * 40), true, false);
        MenuButtons[1][12] = new Button(MenuBtnBmps[9], 150, 150, Math.round(ScaleX * 480),
                dispHeight/2 + Math.round(ScaleY * 210), Math.round(ScaleX * 75),
                Math.round(ScaleY * 40), true, false);
        MenuButtons[1][13] = new Button(MenuBtnBmps[10], 150, 150, Math.round(ScaleX * 580),
                dispHeight/2 + Math.round(ScaleY * 210), Math.round(ScaleX * 75),
                Math.round(ScaleY * 40), true, false);

        //Secondary Player Customize Tank Buttons
        MenuButtons[1][14] = new Button(MenuBtnBmps[9], 150, 150, dispWidth - Math.round(ScaleX * 250),
                dispHeight/2 + Math.round(ScaleY * 100), Math.round(ScaleX * 75),
                Math.round(ScaleY * 40), true, false);
        MenuButtons[1][15] = new Button(MenuBtnBmps[10], 150, 150, dispWidth - Math.round(ScaleX * 160),
                dispHeight/2 + Math.round(ScaleY * 100), Math.round(ScaleX * 75),
                Math.round(ScaleY * 40), true, false);
        MenuButtons[1][16] = new Button(MenuBtnBmps[9], 150, 150, dispWidth - Math.round(ScaleX * 250),
                dispHeight/2 + Math.round(ScaleY * 155), Math.round(ScaleX * 75),
                Math.round(ScaleY * 40), true, false);
        MenuButtons[1][17] = new Button(MenuBtnBmps[10], 150, 150, dispWidth - Math.round(ScaleX * 160),
                dispHeight/2 + Math.round(ScaleY * 155), Math.round(ScaleX * 75),
                Math.round(ScaleY * 40), true, false);
        MenuButtons[1][18] = new Button(MenuBtnBmps[9], 150, 150, dispWidth - Math.round(ScaleX * 250),
                dispHeight/2 + Math.round(ScaleY * 210), Math.round(ScaleX * 75),
                Math.round(ScaleY * 40), true, false);
        MenuButtons[1][19] = new Button(MenuBtnBmps[10], 150, 150, dispWidth - Math.round(ScaleX * 160),
                dispHeight/2 + Math.round(ScaleY * 210), Math.round(ScaleX * 75),
                Math.round(ScaleY * 40), true, false);
    }

    /**
     * Method: setParamResources
     * Description: Method which initializes all bitmaps and tank attributes for menu display.
     * @param Tanks         -   Tank Bitmaps
     * @param bmpWidth      -   Tank Bitmap Width
     * @param bmpHeight     -   Tank Bitmap Height
     * @param bmpFrames     -   Tank Frames for animation
     * @param Backgrounds   -   Level(s) bitmap(s)
     * @param tankHP        -   Tank Health Attributes
     * @param tankSP        -   Tank Shield Attributes
     * @param tankDMG       -   Tank Damage Attributes
     */
    public void setParamResources(Bitmap[] Tanks, int bmpWidth, int bmpHeight, int bmpFrames,
                                  Bitmap[] Backgrounds, int[] tankHP, int[] tankSP, int[] tankDMG) {
        //Assign corresponding attributes
        selBackgrounds = new Bitmap[Backgrounds.length];
        LselTanks = new Tank[Tanks.length/2];
        RselTanks = new Tank[Tanks.length/2];
        healthBars = new ProgressBar[2][tankHP.length];
        shieldBars = new ProgressBar[2][tankSP.length];
        damageBars = new ProgressBar[2][tankDMG.length];

        //Assign and scale background (level)
        for(int i = 0; i < Backgrounds.length; ++i) {
            selBackgrounds[i] = Backgrounds[i] == null ? null : Bitmap.createScaledBitmap(Backgrounds[i],
                    Math.round(ScaleX * 550), Math.round(ScaleY * 300), false);
        }

        //Assign and scale primary and secondary tank
        for(int i = 0, j = 0; i < Tanks.length; i+= 2, ++j) {
            LselTanks[j] = new Tank(Tanks[i], bmpWidth, bmpHeight, Math.round(ScaleX * 150),
                    Math.round(ScaleY * 150), true, bmpFrames, true, tankHP[j], tankSP[j],
                    tankDMG[j]);
            LselTanks[j].setX(Math.round(ScaleX * 100));
            LselTanks[j].setY(dispHeight / 2 + Math.round(ScaleY * 100));
            RselTanks[j] = new Tank(Tanks[i+1], bmpWidth, bmpHeight, Math.round(ScaleX * 150),
                    Math.round(ScaleY * 150), true, bmpFrames, false, tankHP[j], tankSP[j],
                    tankDMG[j]);
            RselTanks[j].setX(dispWidth - Math.round(ScaleX * 600));
            RselTanks[j].setY(dispHeight / 2 + Math.round(ScaleY * 100));
        }

        //Create each Health, Shield and Damage bar for each tank
        for(int i = 0; i < tankHP.length; ++i) {
            healthBars[0][i] = new ProgressBar(Math.round(ScaleX * 300), dispHeight/2 +
                    Math.round(ScaleY * 100), Math.round(ScaleX * 450), dispHeight/2 +
                    Math.round(ScaleY * 140), Color.RED, Color.RED, 0, 250);
            healthBars[1][i] = new ProgressBar(dispWidth - Math.round(ScaleX * 425), dispHeight/2 +
                    Math.round(ScaleY * 100), dispWidth - Math.round(ScaleX * 275), dispHeight/2 +
                    Math.round(ScaleY * 140), Color.RED, Color.RED, 0, 250);
            shieldBars[0][i] = new ProgressBar(Math.round(ScaleX * 300), dispHeight/2 +
                    Math.round(ScaleY * 155), Math.round(ScaleX * 450), dispHeight/2 +
                    Math.round(ScaleY * 195), Color.BLUE, Color.BLUE, 0, 250);
            shieldBars[1][i] = new ProgressBar(dispWidth - Math.round(ScaleX * 425), dispHeight/2 +
                    Math.round(ScaleY * 155), dispWidth - Math.round(ScaleX * 275), dispHeight/2 +
                    Math.round(ScaleY * 195), Color.BLUE, Color.BLUE, 0, 250);
            damageBars[0][i] = new ProgressBar(Math.round(ScaleX * 300), dispHeight/2 +
                    Math.round(ScaleY * 210), Math.round(ScaleX * 450), dispHeight/2 +
                    Math.round(ScaleY * 250), Color.CYAN, Color.CYAN, 0, 10);
            damageBars[1][i] = new ProgressBar(dispWidth - Math.round(ScaleX * 425), dispHeight/2 +
                    Math.round(ScaleY * 210), dispWidth - Math.round(ScaleX * 275), dispHeight/2 +
                    Math.round(ScaleY * 250), Color.CYAN, Color.CYAN, 0, 10);
            //Set corresponding tank attribute to each progressbar
            healthBars[0][i].setProgress(tankHP[i]);
            healthBars[1][i].setProgress(tankHP[i]);
            shieldBars[0][i].setProgress(tankSP[i]);
            shieldBars[1][i].setProgress(tankSP[i]);
            damageBars[0][i].setProgress(tankDMG[i]);
            damageBars[1][i].setProgress(tankDMG[i]);
        }
    }

    /**
     * Method: update
     * Description: Method used to update animations and displays (tanks, progressbars etc.)
     */
    public void update() {
        if(MenuID == 1) {
            //Update profile
            if(profile != null) {
                Unlockables = profile.getUnlockables();
            }
            //Update tank animations
            LselTanks[GameParams[3]].update();
            RselTanks[GameParams[4]].update();
            //Update tank attribute progress bars
            healthBars[0][GameParams[3]].update();
            shieldBars[0][GameParams[3]].update();
            damageBars[0][GameParams[3]].update();
            healthBars[1][GameParams[4]].update();
            shieldBars[1][GameParams[4]].update();
            damageBars[1][GameParams[4]].update();
        }
    }

    /**
     * Method: draw
     * Description: Method used to draw out the menus, tanks and attribute progress bars.
     * @param canvas    -   Application's canvas to draw to
     */
    public void draw(Canvas canvas) {
        backgrounds[MenuID].draw(canvas);
        //Draw all visible menu buttons
        for(int i = 0; i < MenuButtons[MenuID].length; ++i) {
            MenuButtons[MenuID][i].draw(canvas);
        }
        //Draw profile information
        if(MenuID == 0) {
            //Draw Profile Information
            paint.setColor(Color.CYAN);
            paint.setTextSize(25.0f * ScaleX);
            canvas.drawText("NAME: " + profile.getName(), 0, 30, paint);
            canvas.drawText("LEVEL: " + Integer.toString(profile.getLevel()), 0, 60, paint);
            canvas.drawText("EXP: " + Integer.toString(profile.getExperience()), 0, 90, paint);
            canvas.drawText("CTRL" + (profile.getControlType() == 0 ? "BTN" : "SWIPE"), 0, 120, paint);
        }
        //Tank Select menu
        if(MenuID == 1) {
            //Draw level
            canvas.drawBitmap(selBackgrounds[GameParams[2]], dispWidth / 2 -
                    Math.round(ScaleX * 275), Math.round(ScaleY * 75), paint);
            //Draw each tank
            LselTanks[GameParams[3]].draw(canvas);
            RselTanks[GameParams[4]].draw(canvas);
            //Draw corresponding tank attributes
            healthBars[0][GameParams[3]].draw(canvas);
            shieldBars[0][GameParams[3]].draw(canvas);
            damageBars[0][GameParams[3]].draw(canvas);
            healthBars[1][GameParams[4]].draw(canvas);
            shieldBars[1][GameParams[4]].draw(canvas);
            damageBars[1][GameParams[4]].draw(canvas);
        }
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
        if(Math.abs(dx) > 50) {
            if(MenuID == 0) {
                if(MenuButtons[MenuID][0].Visible() && MenuButtons[MenuID][0].isIntersect(X, Y) ||
                        MenuButtons[MenuID][1].Visible() && MenuButtons[MenuID][1].isIntersect(X, Y)) {
                    MenuButtons[MenuID][0].setVisible(MenuButtons[MenuID][0].Visible() ? false : true);
                    MenuButtons[MenuID][1].setVisible(MenuButtons[MenuID][0].Visible() ? false : true);
                    MenuButtons[MenuID][2].setVisible(MenuButtons[MenuID][0].Visible() ? true : false);
                    MenuButtons[MenuID][3].setVisible(MenuButtons[MenuID][0].Visible() ? false : true);
                    MenuButtons[MenuID][4].setVisible(MenuButtons[MenuID][0].Visible() ? false : true);
                }
            }
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
        //5 Main control buttons
        if(MenuID < 2) {
            for (int i = 0; i < MenuButtons[MenuID].length; ++i) {
                MenuButtons[MenuID][i].setButtonState(0);

                if (MenuButtons[MenuID][i].isIntersect(touchX, touchY)) {
                    //Set buttons to released
                    MenuButtons[MenuID][i].setButtonState(0);
                }
            }
        }
    }

    /**
     * Method: getButtonPressed
     * Description: Method used to return the index of a control that has been activated
     * @param touchX    -   touch X coordinate
     * @param touchY    -   touch Y coordinate
     * @return  -   index of touched button
     */
    private int getButtonPressed(float touchX, float touchY, Button[] buttons, int[] SkipIndex, MainThread mainthread) {
        //Loop through all controls
        for(int i = 0; i < buttons.length; ++i) {
            //Skip ignored buttons!
            if(SkipIndex != null) {
                for(int j = 0; j < SkipIndex.length; ++j) {
                    if (i == SkipIndex[j]) {
                        ++i;
                    }
                }
            }

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
        int[]   Contents;
        int[]   Skip = {0, 1};

        if(MenuID == 3 || MenuID == 4) {
            MenuID = 0;
        }
        else if(MenuID < 2) {
            switch (getButtonPressed(touchX, touchY, MenuButtons[MenuID],
                    MenuID == 0 ? Skip : null, mainthread)) {
                case 0:
                    if (MenuID == 0) {   //1-Player Button

                    } else if (MenuID == 1) {  //Previous-Level button
                        if (GameParams[2] > 0) {
                            --GameParams[2];
                        }
                        else {
                            GameParams[2] = (selBackgrounds.length -1);
                        }

                        if (GameParams[0] == 2) {    //Bluetooth Match
                            Contents = new int[1];
                            Contents[0] = GameParams[2];
                            //Notify device of level change
                            if (GameParams[1] == 1 && btServer != null) {
                                if(btServer.btHandler != null) {
                                    btServer.Send(btServer.btHandler.FormatPacket((byte) 0xB1, Contents));
                                }
                            } else if (GameParams[1] == 0 && btClient != null) {
                                if (btClient.btHandler != null) {
                                    btClient.Send(btClient.btHandler.FormatPacket((byte) 0xB1, Contents));
                                }
                            }
                        }
                    }
                    break;

                case 1:
                    if (MenuID == 0) {   //2-Player Button

                    } else if (MenuID == 1) {  //Next-Level button
                        if (GameParams[2] < (selBackgrounds.length - 1)) {
                            ++GameParams[2];
                        }
                        else {
                            GameParams[2] = 0;
                        }

                        if (GameParams[0] == 2) {    //Bluetooth Match
                            Contents = new int[1];
                            Contents[0] = GameParams[2];
                            //Notify device of level change
                            if (GameParams[1] == 1 && btServer != null) {
                                if (btServer.btHandler != null) {
                                    btServer.Send(btServer.btHandler.FormatPacket((byte) 0xB1, Contents));
                                }
                            }
                            else if (GameParams[1] == 0 && btClient != null) {
                                if (btClient.btHandler != null) {
                                    btClient.Send(btClient.btHandler.FormatPacket((byte) 0xB1, Contents));
                                }
                            }
                        }
                    }
                    break;

                case 2:
                    if (MenuID == 0) {   //CPU Button
                        ++MenuID;
                        GameParams[0] = 1;  //Game Type "CPU"
                        GameParams[1] = 1;  //Primary Player
                        MenuButtons[MenuID][2].setVisible(true);
                        MenuButtons[MenuID][3].setVisible(true);
                        MenuButtons[MenuID][4].setVisible(true);
                        MenuButtons[MenuID][5].setVisible(true);
                        MenuButtons[MenuID][6].setVisible(true);
                        MenuButtons[MenuID][7].setVisible(true);
                    } else if (MenuID == 1) {  //Previous-Tank(1)
                        if (GameParams[3] > 0) {
                            --GameParams[3];
                        }
                        else {
                            GameParams[3] = (LselTanks.length - (Unlockables[6] ? 1 :
                                    Unlockables[4] ? 2 :
                                    Unlockables[2] ? 3 :
                                    Unlockables[0] ? 4 : 5));

                            for(int i = 8; i < 14; ++i) {
                                MenuButtons[MenuID][i].setVisible(
                                        (GameParams[3] == LselTanks.length-1) ? true : false);
                            }
                        }

                        if (GameParams[0] == 2) {    //Bluetooth Match
                            Contents = new int[1];
                            Contents[0] = GameParams[3];
                            //Notify device of level change
                            if (GameParams[1] == 1 && btServer != null) {
                                if (btServer.btHandler != null) {
                                    btServer.Send(btServer.btHandler.FormatPacket((byte) 0xB2, Contents));
                                }
                            }
                            else if (GameParams[1] == 0 && btClient != null) {
                                if (btClient.btHandler != null) {
                                    btClient.Send(btClient.btHandler.FormatPacket((byte) 0xB2, Contents));
                                }
                            }
                        }
                    }
                    break;

                case 3:
                    if (MenuID == 0) {   //BT-Host
                        ++MenuID;
                        GameParams[0] = 2;  //Game Type "BlueTooth"
                        GameParams[1] = 1;  //Primary Player/server
                        MenuButtons[MenuID][2].setVisible(true);
                        MenuButtons[MenuID][3].setVisible(true);
                        MenuButtons[MenuID][4].setVisible(false);
                        MenuButtons[MenuID][5].setVisible(false);
                        MenuButtons[MenuID][6].setVisible(false);
                        MenuButtons[MenuID][7].setVisible(true);
                        MsgHandlers[0].obtainMessage(1, 0, -1, 0).sendToTarget();   //Enable Bluetooth/host
                    } else if (MenuID == 1) {  //Next-Tank(1)
                        if (GameParams[3] < (LselTanks.length - (Unlockables[6] ? 1 :
                                        Unlockables[4] ? 2 :
                                        Unlockables[2] ? 3 :
                                        Unlockables[0] ? 4 : 5))) {
                            ++GameParams[3];

                            for(int i = 8; i < 14; ++i) {
                                MenuButtons[MenuID][i].setVisible(
                                        (GameParams[3] == LselTanks.length-1) ? true : false);
                            }
                        }
                        else {
                            GameParams[3] = 0;
                        }

                        if (GameParams[0] == 2) {    //Bluetooth Match
                            Contents = new int[1];
                            Contents[0] = GameParams[3];
                            //Notify device of level change
                            if (GameParams[1] == 1 && btServer != null) {
                                if (btServer.btHandler != null) {
                                    btServer.Send(btServer.btHandler.FormatPacket((byte) 0xB2, Contents));
                                }
                            }
                            else if (GameParams[1] == 0 && btClient != null) {
                                if (btClient.btHandler != null) {
                                    btClient.Send(btClient.btHandler.FormatPacket((byte) 0xB2, Contents));
                                }
                            }
                        }
                    }
                    break;

                case 4:
                    if (MenuID == 0) {   //BT-Join
                        ++MenuID;
                        GameParams[0] = 2;  //Game Type "BlueTooth"
                        GameParams[1] = 0;  //Secondary Player/Client
                        MenuButtons[MenuID][2].setVisible(false);
                        MenuButtons[MenuID][3].setVisible(false);
                        MenuButtons[MenuID][4].setVisible(true);
                        MenuButtons[MenuID][5].setVisible(true);
                        MenuButtons[MenuID][6].setVisible(true);
                        MenuButtons[MenuID][7].setVisible(true);
                        MsgHandlers[0].obtainMessage(2, 0, -1, 0).sendToTarget();   //Enable Bluetooth/host
                    } else if (MenuID == 1) {  //Previous-Tank(2)
                        if (GameParams[4] > 0) {
                            --GameParams[4];
                        }
                        else {
                            GameParams[4] = (RselTanks.length - (Unlockables[6] ? 1 :
                                            Unlockables[4] ? 2 :
                                            Unlockables[2] ? 3 :
                                            Unlockables[0] ? 4 : 5));

                            for(int i = 14; i < 20; ++i) {
                                MenuButtons[MenuID][i].setVisible(
                                        (GameParams[4] == RselTanks.length-1) ? true : false);
                            }
                        }

                        if (GameParams[0] == 2) {    //Bluetooth Match
                            Contents = new int[1];
                            Contents[0] = GameParams[4];
                            //Notify device of level change
                            if (GameParams[1] == 1 && btServer != null) {
                                if (btServer.btHandler != null) {
                                    btServer.Send(btServer.btHandler.FormatPacket((byte) 0xB2, Contents));
                                }
                            }
                            else if (GameParams[1] == 0 && btClient != null) {
                                if (btClient.btHandler != null) {
                                    btClient.Send(btClient.btHandler.FormatPacket((byte) 0xB2, Contents));
                                }
                            }
                        }
                    }
                    break;

                case 5:
                    if (MenuID == 0) { //Settings Button
                        profile.setControlType(profile.getControlType() == 0 ? 1 : 0);

                    } else if (MenuID == 1) {  //Next-Tank(2)
                        if (GameParams[4] < (RselTanks.length - (Unlockables[6] ? 1 :
                                        Unlockables[4] ? 2 :
                                        Unlockables[2] ? 3 :
                                        Unlockables[0] ? 4 : 5))) {
                            ++GameParams[4];

                            for(int i = 14; i < 20; ++i) {
                                MenuButtons[MenuID][i].setVisible(
                                        (GameParams[4] == RselTanks.length-1) ? true : false);
                            }
                        }
                        else {
                            GameParams[4] = 0;
                        }

                        if (GameParams[0] == 2) {    //Bluetooth Match, send data
                            Contents = new int[1];
                            Contents[0] = GameParams[4];
                            //Notify device of level change
                            if (GameParams[1] == 1 && btServer != null) {
                                if (btServer.btHandler != null) {
                                    btServer.Send(btServer.btHandler.FormatPacket((byte) 0xB2, Contents));
                                }
                            }
                            else if (GameParams[1] == 0 && btClient != null) {
                                if (btClient.btHandler != null) {
                                    btClient.Send(btClient.btHandler.FormatPacket((byte) 0xB2, Contents));
                                }
                            }
                        }
                    }
                    break;

                case 6:
                    if (MenuID == 0) {  //Reserved for Future Use

                    } else if (MenuID == 1) {   //Ready Button
                        if (GameParams[0] <= 1) {    //If GameType is not bluetooth

                            if(MsgHandlers[1] != null) {
                                MsgHandlers[1].obtainMessage(1, 0, -1, 0).sendToTarget();  //Begin Match Making
                            }
                            ++MenuID;
                        } else if (GameParams[0] == 2) {    //If GameType is bluetooth
                            byte[] readybyte = {(byte) 0xB3};

                            if (GameParams[1] == 1 && btServer != null) {
                                //Primary Player and Secondary Player is ready
                                ++MenuID;
                                btServer.Send(readybyte);
                                MsgHandlers[1].obtainMessage(1, 0, -1, 0).sendToTarget();  //Begin Match Making
                            } else if (GameParams[1] == 0 && btClient != null) {
                                //Secondary Player
                                ++MenuID;
                                btClient.Send(readybyte);
                            }
                        }
                    }
                    break;

                case 7:
                    if(MenuID == 0) {   //TODO: Find something for this button

                    }
                    else if(MenuID == 1) {  //Back Button
                        MenuButtons[MenuID][7].setVisible(false);
                        --MenuID;
                    }
                    break;

                case 8: //Increment Custom Tank 1 HP
                    if(MenuID == 1) {
                        if((tankStats[0][0] + tankStats[0][1] + (tankStats[0][2]*25)) < 300) {
                            tankStats[0][0] += 25;
                            healthBars[0][4].setProgress(tankStats[0][0]);
                        }
                    }
                    break;

                case 9: //Decrement Custom Tank 1 HP
                    if(MenuID == 1) {
                        if((tankStats[0][0] + tankStats[0][1] + (tankStats[0][2]*25)) > 75) {
                            tankStats[0][0] -= 25;
                            healthBars[0][4].setProgress(tankStats[0][0]);
                        }
                    }
                    break;

                case 10: //Increment Custom Tank 1 SP
                    if(MenuID == 1) {
                        if((tankStats[0][0] + tankStats[0][1] + (tankStats[0][2]*25)) < 300) {
                            tankStats[0][1] += 25;
                            shieldBars[0][4].setProgress(tankStats[0][1]);
                        }
                    }
                    break;

                case 11: //Decrement Custom Tank 1 SP
                    if(MenuID == 1) {
                        if((tankStats[0][0] + tankStats[0][1] + (tankStats[0][2]*25)) > 75) {
                            tankStats[0][1] -= 25;
                            shieldBars[0][4].setProgress(tankStats[0][1]);
                        }
                    }
                    break;

                case 12: //Increment Custom Tank 1 DMG
                    if(MenuID == 1) {
                        if((tankStats[0][0] + tankStats[0][1] + (tankStats[0][2]*25)) < 300) {
                            ++tankStats[0][2];
                            damageBars[0][4].setProgress(tankStats[0][2]);
                        }
                    }
                    break;

                case 13: //Decrement Custom Tank 1 DMG
                    if(MenuID == 1) {
                        if((tankStats[0][0] + tankStats[0][1] + (tankStats[0][2]*25)) > 75) {
                            --tankStats[0][2];
                            damageBars[0][4].setProgress(tankStats[0][2]);
                        }
                    }
                    break;

                case 14: //Increment Custom Tank 2 HP
                    if(MenuID == 1) {
                        if((tankStats[1][0] + tankStats[1][1] + (tankStats[1][2]*25)) < 300) {
                            tankStats[1][0] += 25;
                            healthBars[1][4].setProgress(tankStats[1][0]);
                        }
                    }
                    break;

                case 15: //Decrement Custom Tank 2 HP
                    if(MenuID == 1) {
                        if((tankStats[1][0] + tankStats[1][1] + (tankStats[1][2]*25)) > 75) {
                            tankStats[1][0] -= 25;
                            healthBars[1][4].setProgress(tankStats[1][0]);
                        }
                    }
                    break;

                case 16: //Increment Custom Tank 2 SP
                    if(MenuID == 1) {
                        if((tankStats[1][0] + tankStats[1][1] + (tankStats[1][2]*25)) < 300) {
                            tankStats[1][1] += 25;
                            shieldBars[1][4].setProgress(tankStats[1][1]);
                        }
                    }
                    break;

                case 17: //Decrement Custom Tank 2 SP
                    if(MenuID == 1) {
                        if((tankStats[1][0] + tankStats[1][1] + (tankStats[1][2]*25)) > 75) {
                            tankStats[1][1] -= 25;
                            shieldBars[1][4].setProgress(tankStats[1][1]);
                        }
                    }
                    break;

                case 18: //Increment Custom Tank 2 DMG
                    if(MenuID == 1) {
                        if((tankStats[1][0] + tankStats[1][1] + (tankStats[1][2]*25)) < 300) {
                            ++tankStats[1][2];
                            damageBars[1][4].setProgress(tankStats[1][2]);
                        }
                    }
                    break;

                case 19: //Decrement Custom Tank 2 DMG
                    if(MenuID == 1) {
                        if((tankStats[1][0] + tankStats[1][1] + (tankStats[1][2]*25)) > 75) {
                            --tankStats[1][2];
                            damageBars[1][4].setProgress(tankStats[1][2]);
                        }
                    }
                    break;
            }
        }
    }

    /**
     * Method: ProcessGameData
     * Description: Method used to process incoming data pertaining to
     *              Match making
     * @param inData    -   incoming data
     */
    public void ProcessGameData(byte[] inData) {
        if(inData[0] == (byte)0xB1) {   //Level Change
            int[]   Level = GameParams[1] == 1 ? btServer.btHandler.ExtractIntArray(inData) :
                    btClient.btHandler.ExtractIntArray(inData);
            //Assign level id
            GameParams[2] = Level[0];
        }
        else if(inData[0] == (byte)0xB2) {  //Tank Change
            int[]   tank = GameParams[1] == 1 ? btServer.btHandler.ExtractIntArray(inData) :
                    btClient.btHandler.ExtractIntArray(inData);
            //Assign corresponding tank id
            GameParams[GameParams[1] == 0 ? 3 : 4] = tank[0];
        }
        else if(inData[0] == (byte)0xB3) {  //Secondary Player is ready

            if(GameParams[1] == 1) {
                //Received from client, toggle ready button
                MenuButtons[1][6].setVisible(true);
            }
            else if(GameParams[1] == 0) {
                //Received from Server, begin match
                MsgHandlers[1].obtainMessage(1, 0, -1, 0).sendToTarget();  //Begin Match Making
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
        GameParams[1] = server ? 1 : 0;

        if(GameParams[1] == 1) {   //Server
            btServer = btserver;
        }
        else {
            btClient = btclient;
        }
    }

    /*      Getters and Setters     */
    public int[] getGameParams() {
        //Primary and Secondary Tank order + custom stats
        int parameters[] = {GameParams[0], GameParams[1], GameParams[2], GameParams[3], GameParams[4],
                tankStats[0][0], tankStats[0][1], tankStats[0][2],
                tankStats[1][0], tankStats[1][1], tankStats[1][2],
                profile == null ? 0 : profile.getControlType()};

        return parameters;
    }

    public void setGameParams(int[] params) {
        for(int i = 0; i < params.length; ++i) {
            GameParams[i] = params[i];
        }
    }

    public void setMenu(int ID) {
        MenuID = ID;
    }

    public int getMenu() {
        return MenuID;
    }

    public void setProfile(Profile p) {
        profile = p;
    }

    //Following Method(s) for TESTING PURPOSES ONLY
    public Button getButton(int MenuID, int Index) {
        return MenuButtons[MenuID][Index];
    }
}
