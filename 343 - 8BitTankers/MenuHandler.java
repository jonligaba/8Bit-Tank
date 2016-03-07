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
    private float           ScaleX;
    private float           ScaleY;

    private int             MenuID;
    private int             dispWidth;
    private int             dispHeight;
    private int[]           GameParams;
    private boolean         playerReady;
    private Handler[]       MsgHandlers;
    private Button[][]      MenuButtons;
    private Background[]    backgrounds;

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
     * @param cmpHandler
     */
    public MenuHandler(int dWidth, int dHeight, float scaleX, float scaleY, Bitmap[] MenuBmp,
                       Bitmap[] MenuBtnBmps, Handler msgHandler, Handler cmpHandler) {
        //Initialize attributes
        MenuID = 0;
        ScaleX = scaleX;
        ScaleY = scaleY;
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
        MenuButtons[0] = new Button[5];
        MenuButtons[0][0] = new Button(MenuBtnBmps[0], 200, 100, Math.round(ScaleX * 50),
                dispHeight - Math.round(ScaleY * 250), Math.round(ScaleX * 400),
                Math.round(ScaleY * 195), true, true);
        MenuButtons[0][1] = new Button(MenuBtnBmps[1], 200, 100, Math.round(ScaleX * 50),
                dispHeight - Math.round(ScaleY * 250), Math.round(ScaleX * 400),
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
        //Stage_L, Stage_R, Tank1_L, Tank1_R, Tank2_L, Tank2_R, Start, Back
        MenuButtons[1] = new Button[8];
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
            selBackgrounds[i] = Bitmap.createScaledBitmap(Backgrounds[i],
                    Math.round(ScaleX * 550), Math.round(ScaleY * 300), false);
        }

        //Assign and scale primary and secondary tank
        for(int i = 0, j = 0; i < Tanks.length; i+= 2, ++j) {
            LselTanks[j] = new Tank(Tanks[i], bmpWidth, bmpHeight, Math.round(ScaleX * 150),
                    Math.round(ScaleY * 150), true, bmpFrames, true, tankHP[j], tankSP[j], tankDMG[j]);
            LselTanks[j].setX(Math.round(ScaleX * 100));
            LselTanks[j].setY(dispHeight / 2 + Math.round(ScaleY * 100));
            RselTanks[j] = new Tank(Tanks[i+1], bmpWidth, bmpHeight, Math.round(ScaleX * 150),
                    Math.round(ScaleY * 150), true, bmpFrames, false, tankHP[j], tankSP[j], tankDMG[j]);
            RselTanks[j].setX(dispWidth - Math.round(ScaleX * 550));
            RselTanks[j].setY(dispHeight / 2 + Math.round(ScaleY * 100));
        }

        int maxHP = 0, maxSP = 0, maxDMG = 0;
        //Get Highest HP, SP and DMG for progressbar
        for(int i = 0; i < tankHP.length; ++i) {
            if(maxHP < tankHP[i]) {
                maxHP = tankHP[i];
            }
            if(maxSP < tankSP[i]) {
                maxSP = tankSP[i];
            }
            if(maxDMG < tankDMG[i]) {
                maxDMG = tankDMG[i];
            }
        }

        //Create each Health, Shield and Damage bar for each tank
        for(int i = 0; i < tankHP.length; ++i) {
            healthBars[0][i] = new ProgressBar(Math.round(ScaleX * 350), dispHeight/2 +
                    Math.round(ScaleY * 100), Math.round(ScaleX * 550), dispHeight/2 +
                    Math.round(ScaleY * 120), Color.RED, Color.RED, 0, maxHP);
            healthBars[1][i] = new ProgressBar(dispWidth - Math.round(ScaleX * 300), dispHeight/2 +
                    Math.round(ScaleY * 100), dispWidth - Math.round(ScaleX * 100), dispHeight/2 +
                    Math.round(ScaleY * 120), Color.RED, Color.RED, 0, maxHP);
            shieldBars[0][i] = new ProgressBar(Math.round(ScaleX * 350), dispHeight/2 +
                    Math.round(ScaleY * 135), Math.round(ScaleX * 550), dispHeight/2 +
                    Math.round(ScaleY * 155), Color.BLUE, Color.BLUE, 0, maxSP);
            shieldBars[1][i] = new ProgressBar(dispWidth - Math.round(ScaleX * 300), dispHeight/2 +
                    Math.round(ScaleY * 135), dispWidth - Math.round(ScaleX * 100), dispHeight/2 +
                    Math.round(ScaleY * 155), Color.BLUE, Color.BLUE, 0, maxSP);
            damageBars[0][i] = new ProgressBar(Math.round(ScaleX * 350), dispHeight/2 +
                    Math.round(ScaleY * 170), Math.round(ScaleX * 550), dispHeight/2 +
                    Math.round(ScaleY * 190), Color.CYAN, Color.CYAN, 0, maxDMG);
            damageBars[1][i] = new ProgressBar(dispWidth - Math.round(ScaleX * 300), dispHeight/2 +
                    Math.round(ScaleY * 170), dispWidth - Math.round(ScaleX * 100), dispHeight/2 +
                    Math.round(ScaleY * 190), Color.CYAN, Color.CYAN, 0, maxDMG);
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
    private int getButtonPressed(float touchX, float touchY, Button[] buttons, MainThread mainthread) {
        //Loop through all controls
        for(int i = 0; i < buttons.length; ++i) {
            //If Touch Point intersects with Control and Control is active
            if(buttons[i].isIntersect(touchX, touchY) && buttons[i].Visible()) {
                //Switch control to "pressed" && return index
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
        int[]   Contents;

        if(MenuID == 3 || MenuID == 4) {
            MenuID = 0;
        }
        else if(MenuID < 2) {
            switch (getButtonPressed(touchX, touchY, MenuButtons[MenuID], mainthread)) {
                case 0:
                    if (MenuID == 0) {   //1-Player Button
                        MenuButtons[MenuID][0].setVisible(false);
                        MenuButtons[MenuID][1].setVisible(true);
                        MenuButtons[MenuID][2].setVisible(false);
                        MenuButtons[MenuID][3].setVisible(true);
                        MenuButtons[MenuID][4].setVisible(true);
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
                        MenuButtons[MenuID][0].setVisible(true);
                        MenuButtons[MenuID][1].setVisible(false);
                        MenuButtons[MenuID][2].setVisible(true);
                        MenuButtons[MenuID][3].setVisible(false);
                        MenuButtons[MenuID][4].setVisible(false);
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
                            GameParams[3] = (LselTanks.length - 1);
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
                        if (GameParams[3] < (LselTanks.length - 1)) {
                            ++GameParams[3];
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
                            GameParams[4] = (RselTanks.length - 1);
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
                    if (MenuID == 0) {
                        //TODO: Reserved for future use
                    } else if (MenuID == 1) {  //Next-Tank(2)
                        if (GameParams[4] < (RselTanks.length - 1)) {
                            ++GameParams[4];
                        }
                        else {
                            GameParams[4] = 0;
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

                case 6:
                    if (MenuID == 0) {
                        //TODO: Implement settings here
                    } else if (MenuID == 1) {
                        if (GameParams[0] <= 1) {    //If GameType is not bluetooth
                            MsgHandlers[1].obtainMessage(1, 0, -1, 0).sendToTarget();  //Begin Match Making
                            ++MenuID;
                        } else if (GameParams[0] == 2) {    //If GameType is bluetooth
                            byte[] readybyte = {(byte) 0xB3};

                            if (GameParams[1] == 1 && btServer != null && playerReady == true) {
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
                //Received from Server, toggle ready button
                playerReady = !playerReady;
                MenuButtons[1][6].setVisible(playerReady);
            }
            else if(GameParams[1] == 0) {
                //Received from Server, begin match
                MsgHandlers[1].obtainMessage(1, 0, -1, 0).sendToTarget();  //Begin Match Making
            }
        }
    }

    /*      Getters and Setters     */
    public int[] getGameParams() {
        //Primary and Secondary Tank order
        int parameters[] = {GameParams[0], GameParams[1], GameParams[2], GameParams[3], GameParams[4]};

        return parameters;
    }

    public void setGameParams(int[] params) {
        for(int i = 0; i < params.length; ++i) {
            GameParams[i] = params[i];
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

    public void setMenu(int ID) {
        MenuID = ID;
    }

    public int getMenu() {
        return MenuID;
    }

    public void setTank(int Player, int id) {
        GameParams[Player+3] = id;
    }

    public void setLevel(int id) {
        GameParams[2] = id;
    }

    public void setReady(boolean ready) {
        playerReady = ready;
    }
}
