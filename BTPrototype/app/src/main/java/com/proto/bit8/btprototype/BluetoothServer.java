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
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import java.io.IOException;
import android.os.Handler;
import java.util.UUID;


/**
 * Class: BluetoothServer
 * Description: This class handles listening and accepting new connections
 */
public class BluetoothServer extends BluetoothHandler {
    //Listener          Reference to ConnectionHandler class which listens/accepts connections
    //btServerSocket    Socket which is used to listen for connections
    private ConnectionHandler       Listener;
    private BluetoothServerSocket   btServerSocket;

    /**
     * Method: BluetoothServer
     * Description: Constructor which initializes variables
     * @param ctext - Reference to activity's context
     * @param handler - Reference to message handler
     * @param svcName - Description of service
     * @param svcUUID - UUID
     */
    public BluetoothServer(Context ctext, Handler handler, String svcName, UUID svcUUID) {
        context = ctext;
        btUUID = svcUUID;
        SvcName = svcName;
        mHandler = handler;
        btAdapter = BluetoothAdapter.getDefaultAdapter();

        if(btAdapter == null) {
            //Adapter does not exist!
            setState(BT_NOADAPTER_ERROR);
        }
    }

    /**
     * Method: Start
     * Description: This method starts the thread to open a new socket, listen/accept connections
     */
    public void Start() {
        Listener = new ConnectionHandler(SvcName, btUUID);
        setState(BT_STATE_LISTENING);
        Listener.start();
    }

    /**
     * Method: Stop
     * Description: This method cancels service(s) such as listening/data processing or both
     * @param type - Indicator of which services to stop
     */
    public void Stop(int type) {
        setState(BT_STATE_IDLE);

        if(type == 0) {
            Listener.cancel();
        }
        else if(type == 1) {
            if(btHandler != null) {
                btHandler.cancel();
            }
        }
        else if(type == 2) {
            Listener.cancel();

            if(btHandler != null) {
                btHandler.cancel();
            }
        }
    }

    /**
     * Class: ConnectionHandler
     * Description: This class sets up a server socket, listeners/accepts new connections,
     *              then initializes the data processing thread
     */
    private class ConnectionHandler extends Thread {

        /**
         * Method: ConnectionHandler
         * Description: This constructor initializes variables and sets up server socket
         *              to listen for incoming connections.
         * @param SvcName - Description of Service
         * @param SvcUUID - UUID
         */
        public ConnectionHandler(String SvcName, UUID SvcUUID) {
            BluetoothServerSocket tmpSocket = null;

            try {
                //Setup Server Socket
                btServerSocket = btAdapter.listenUsingRfcommWithServiceRecord(SvcName, SvcUUID);
                System.out.println("Listener Created");
            }
            catch(IOException io) {
                //Error setting up Server Socket
                io.printStackTrace();
                setState(BT_SOCKCREAT_ERROR);
            }
        }

        /**
         * Method: run
         * Description: This method constants accepts incoming connections and then starting
         *              the data processing thread for each
         */
        public void run() {
            //btSocket      Socket which leads to paired device
            BluetoothSocket btSocket = null;

            while(State != BT_STATE_CONNECTED) {
                try {
                    //Accept the incoming connection
                    if(btServerSocket != null) {
                        btSocket = btServerSocket.accept();
                    }
                }
                catch(IOException io) {
                    //Failed accepting incoming connection
                    io.printStackTrace();
                }

                try {
                    if (btSocket != null) {
                        //Stop listening for connections
                        btServerSocket.close();
                        setState(BT_STATE_CONNECTED);
                        System.out.println("Connected");
                        //Initialize data processing thread
                        btHandler = new DataHandler(btSocket);
                        btHandler.start();
                        break;
                    }
                }
                catch (IOException io) {
                    //Error closing server socket
                    io.printStackTrace();
                    setState(BT_SOCKCLOSE_ERROR);
                }
            }
        }

        /**
         * Method: cancel
         * Description: Cancels the listener thread
         */
        public void cancel() {
            if(btServerSocket != null) {
                try {
                    btServerSocket.close();
                } catch (IOException io) {
                    //Failed closing socket
                    io.printStackTrace();
                    setState(BT_SOCKCLOSE_ERROR);
                }
            }
        }
    }
}
