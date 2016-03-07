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

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import java.util.UUID;

public class BTProtoMain extends Activity {
    private BluetoothServer     btServer;
    private BluetoothClient     btClient;
    private MainPanel           mPanel;
    private boolean             Server;

    private final String        PROFILES_PREF = "Bit8Profiles";
    public final int            PROFILE_PROMPT = 1337;

    /**
     * Method: onCreate (Override
     * Description: Method which controls actions that take place on creation of this activity.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Disable Title Screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Set Application to fullscreen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        //Disable Sleep
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //Open profile manager
        //Create main panel
        mPanel = new MainPanel(this, btMsgHandler, getSharedPreferences(PROFILES_PREF, 0));
        setContentView(mPanel);
    }

    /**
     * Method: onSaveInstanceState
     * Description: Method used to save the state of the application for when
     *              the user chooses to resume the app.
     * @param outState  -   bundle which holds all settings to resume game.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if(mPanel != null) {
            if(mPanel.getDisplayID() == 1) {
                outState.putIntArray("p0_attr", mPanel.getPlayerAttributes(0));
                outState.putIntArray("p1_attr", mPanel.getPlayerAttributes(1));
            }
            else {
                outState.putInt("menu_id", mPanel.getMenuID());
            }
            outState.putIntArray("game_params", mPanel.getGameParameters());
            outState.putInt("display_id", mPanel.getDisplayID());
        }
        super.onSaveInstanceState(outState);
    }

    /**
     * Method: onRestoreInstanceState
     * Description: Method used to load saved state information to resume
     *              the application from when the user left it.
     * @param savedInstanceState
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if(mPanel != null && savedInstanceState != null) {
            int displayID = savedInstanceState.getInt("display_id");

            if(displayID == 1) {    //Resume match
                int[]   p0Attributes = savedInstanceState.getIntArray("p0_attr");
                int[]   p1Attributes = savedInstanceState.getIntArray("p1_attr");
                int[]   hp = {p0Attributes[0], p1Attributes[0]};
                int[]   sp = {p0Attributes[1], p1Attributes[1]};
                int[]   x = {p0Attributes[2], p1Attributes[2]};
                int[]   y = {p0Attributes[3], p1Attributes[3]};

                mPanel.setDisplayID(1);
                mPanel.resumeGame(savedInstanceState.getIntArray("game_params"), hp, sp, x, y);
            }
            else {
                mPanel.setDisplayID(0);
                mPanel.setMenuID(savedInstanceState.getInt("menu_id"));
                mPanel.setGameParameters(savedInstanceState.getIntArray("game_params"));
            }
            super.onRestoreInstanceState(savedInstanceState);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() { //Disable back button
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    /**
     * Handler: btMsgHandler
     * Description: Handler which receives and processes incoming messages pertaining
     *              to bluetooth status updates and incoming data
     */
    public final Handler btMsgHandler = new Handler() {

        /**
         * Method: handleMessage
         * Description: This method receives messages with specific content and processes
         *              them accordingly
         * @param msg - Passed in msg to be processed
         */
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case 1: //Enable Bluetooth / host
                    //Verify that a bluetooth adapter exists and is not enabled
                    if(BluetoothAdapter.getDefaultAdapter() != null) {
                        if(BluetoothAdapter.getDefaultAdapter().isEnabled() == false) {
                            //Prompt user to enable Bluetooth
                            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);

                            startActivityForResult(enableIntent, BluetoothHandler.BT_REQUEST_ENABLE);
                        }
                    }

                    Server = true;
                    //Initialize Bluetooth Server service
                    btServer = new BluetoothServer(BTProtoMain.this, btMsgHandler, "BIT8PROTOTYPE", new UUID(0x78E0, 0x89FF));

                    //If the device is not discoverable, make it discoverable
                    if(btServer.getAdapter().getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
                        //Prompt user to make Bluetooth Discoverable
                        Intent discoverIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                        //Discoverable for 300 seconds
                        discoverIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
                        startActivityForResult(discoverIntent, BluetoothHandler.BT_PROMPT_DISCOVE);
                        btServer.Start(); //TODO: Temporary fix for message not reaching activity handler
                    }
                    else {
                        //Device is already discoverable, start listening
                        btServer.Start();
                    }
                    mPanel.setBluetoothHandler(btServer, null, true);
                    break;

                case 2: //Enable Bluetooth / join
                    //Verify that a bluetooth adapter exists and is not enabled
                    if(BluetoothAdapter.getDefaultAdapter() != null) {
                        if(BluetoothAdapter.getDefaultAdapter().isEnabled() == false) {
                            //Prompt user to enable Bluetooth
                            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);

                            startActivityForResult(enableIntent, BluetoothHandler.BT_REQUEST_ENABLE);
                        }
                    }

                    //Join
                    Server = false;
                    //Initialize Bluetooth Client service
                    btClient = new BluetoothClient(BTProtoMain.this, btMsgHandler, new UUID(0x78E0, 0x89FF));

                    //Open Device list to connect to device
                    Intent  getDevicesIntent = new Intent(BTProtoMain.this, BTDevicesActivity.class);

                    startActivityForResult(getDevicesIntent, BluetoothHandler.BT_DEVICE_CONNECT);
                    mPanel.setBluetoothHandler(null, btClient, false);
                    break;

                //Device has status update
                case BluetoothHandler.BT_STATE_UPDATE:
                    if(Server) {
                        //Update status
                        mPanel.setStateMsg(btServer.getStateMessage(btServer.getState()));
                        //Device is now connected
                        if(msg.obj == BluetoothHandler.BT_STATE_CONNECTED) {
                            //Stop server from accepting new connections
                            btServer.Stop(0);
                        }
                    }
                    else {
                        //Update status
                        mPanel.setStateMsg(btClient.getStateMessage(btClient.getState()));

                        if(msg.obj == BluetoothHandler.BT_STATE_CONNECTED) {
                            //Stop client from attempting to connect
                            btClient.Stop(0);
                        }
                    }
                    break;
                //All possible errors
                case BluetoothHandler.BT_ERROR_UPDATE:
                    if(Server && btServer != null) {
                        //Stop all Server threads entirely
                        btServer.Stop(2);
                        //Set current error status
                        mPanel.setErrorMsg(btServer.getStateMessage(btServer.getState()));
                    }
                    else if(!Server && btClient != null) {
                        //Stop all Client threads entirely
                        btClient.Stop(2);
                        //Set current error status
                        mPanel.setErrorMsg(btClient.getStateMessage(btClient.getState()));
                    }
                    break;
                //Data has been read through bluetooth
                case BluetoothHandler.BT_MESSAGE_READ:
                    //Process the incoming data
                    mPanel.ProcessIncomingData((byte[]) msg.obj);
                    break;

                case 100: //Main panel initialized, get profile
                    Intent  getProfileIntent = new Intent(BTProtoMain.this, ProfileActivity.class);

                    startActivityForResult(getProfileIntent, PROFILE_PROMPT);
                    break;
            }
        }
    };

    /**
     * Method: onActivityResult
     * Description: Handler which obtains, filters and processes results from activities
     * @param ReqCode - Request Code of activity
     * @param ResCode - Result code of activity
     * @param intent - Intent returned from activity
     */
    public void onActivityResult(int ReqCode, int ResCode, Intent intent) {
        switch(ReqCode) {
            //Result of prompting user to enable their bluetooth device
            case BluetoothHandler.BT_REQUEST_ENABLE:

                break;

            //Result from prompting user to become discoverable
            case BluetoothHandler.BT_PROMPT_DISCOVE:
                if(ResCode == Activity.RESULT_OK) {
                    //Device is discoverable, start listening
                    btServer.Start();
                }
                break;
            //Result from prompting user to select a device to connect to
            case BluetoothHandler.BT_DEVICE_CONNECT:
                if(ResCode == Activity.RESULT_OK) {
                    //Get the device info here and connect
                    btClient.Start(btClient.getAdapter().getRemoteDevice(
                            intent.getExtras().getString("device_address")));
                }
                break;
            //Result from prompting user to pick a profile
            case PROFILE_PROMPT:
                if(ResCode == Activity.RESULT_OK) {
                    mPanel.setProfile(intent.getExtras().getString("profile_name"),
                            intent.getExtras().getInt("profile_exp"),
                            intent.getExtras().getInt("profile_ctrl"));
                }
                break;
        }
    }
}