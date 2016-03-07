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

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;
import java.io.IOException;
import java.util.UUID;

/**
 * Class: BluetoothClient
 * Description: This class handles connecting to a server and starting the data processing
 *              thread
 */
public class BluetoothClient extends BluetoothHandler
{
    //Connector     Reference to ConnectionHandler which attempts to connect to a server
    private ConnectionHandler           Connector;

    /**
     * Method: BluetoothClient
     * Description: Constructor which assigns values
     * @param ctext - Activity's context
     * @param handler - Reference of Message Handler
     * @param myUUID - UUID
     */
    public BluetoothClient(Context ctext, Handler handler, UUID myUUID) {
        context = ctext;
        btUUID = myUUID;
        mHandler = handler;
        setState(BT_STATE_IDLE);
        btAdapter = BluetoothAdapter.getDefaultAdapter();

        if(btAdapter == null) {
            //Adapter does not exist!
            setState(BT_NOADAPTER_ERROR);
        }
    }

    /**
     * Method: Start
     * Description: Initialize thread to begin connecting to a server
     * @param btServer - Reference to Server to connect to
     */
    public void Start(BluetoothDevice btServer) {
        Connector = new ConnectionHandler(btServer, btUUID);
        Connector.start();
    }

    /**
     * Method: Stop
     * Description: Stop certain or all threads pertaining to client
     * @param type - Indicator of which services to stop
     */
    public void Stop(int type) {
        setState(BT_STATE_IDLE);

        if(type == 0) {
            Connector.cancel();
        }
        else if(type == 1) {
            if(btHandler != null) {
                btHandler.cancel();
            }
        }
        else if(type == 2) {
            Connector.cancel();

            if(btHandler != null) {
                btHandler.cancel();
            }
        }
    }

    /**
     * Class: ConnectionHandler
     * Description: This class handles connecting to a server
     */
    private class ConnectionHandler extends Thread {
        //btDevice      Reference to the Bluetooth Device
        //btSocket      Socket which connects client to server
        private BluetoothDevice btDevice;
        private BluetoothSocket btSocket;

        /**
         * Method: ConnectionHandler
         * Description: Default Constructor which assigns values and creates a socket to link
         *              to the server
         * @param btdevice - Reference to the Bluetooth Device
         * @param svcUUID - UUID of service
         */
        public ConnectionHandler(BluetoothDevice btdevice, UUID svcUUID) {
            btDevice = btdevice;

            setState(BT_STATE_CONNECTING);

            try {
                //Create socket to connect to erver
                btSocket = btDevice.createRfcommSocketToServiceRecord(svcUUID);
            }
            catch(IOException io) {
                //Error creating Socket
                io.printStackTrace();
                setState(BT_SOCKCREAT_ERROR);
            }
        }

        public void run() {
            //Disable bluetooth Discovery
            btAdapter.cancelDiscovery();

            try {
                //Connect to server
                btSocket.connect();
            }
            catch(IOException io) {
                //Error attempting to connect to server

                try {
                    btSocket.close();
                }
                catch(IOException IOClose) {
                    //Error closing socket
                    IOClose.printStackTrace();
                }
                io.printStackTrace();
            }
            //Client is now connected to server
            if (btSocket.isConnected()) {
                setState(BT_STATE_CONNECTED);
                //Initialize data processing thread
                btHandler = new DataHandler(btSocket);
                btHandler.start();
            }
        }

        /**
         * Method: cancel
         * Description: Stops attempting to connect to a server
         */
        public void cancel() {
            if(btSocket != null) {
                try {
                    btSocket.close();
                } catch (IOException io) {
                    //Error closing socket
                    io.printStackTrace();
                    setState(BT_SOCKCLOSE_ERROR);
                }
            }
        }
    }
}
