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
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.UUID;

/**
 * Class: BluetoothHandler
 * Description: Abstract class which handles union data between Server/Client and Data processing.
 */
public abstract class BluetoothHandler {

    //btHandler     Reference to DataHandler class which input/output data through bluetooth
    //btAdapter     Reference to Bluetooth Local Adapter
    //mHandler      Reference to message handler
    //context       Reference to activity's context
    //SvcName       Name of the bluetooth service
    //btUUID        Bluetooth Service UUID
    //State         Indicator of current state of Bluetooth Adapter
    protected DataHandler       btHandler;
    protected BluetoothAdapter  btAdapter;
    protected Handler           mHandler;
    protected Context           context;
    protected String            SvcName;
    protected UUID              btUUID;
    protected int               State;
    //All possible states of the Bluetooth Adapter
    public static final int     BT_STATE_IDLE       = 0;
    public static final int     BT_STATE_LISTENING  = 1;
    public static final int     BT_STATE_CONNECTING = 2;
    public static final int     BT_STATE_CONNECTED  = 3;
    public static final int     BT_STATE_UPDATE     = 4;
    //All possible errors of the Bluetooth Adapter
    public static final int     BT_NOADAPTER_ERROR  = 5;
    public static final int     BT_SOCKCREAT_ERROR  = 6;
    public static final int     BT_SOCKETCON_ERROR  = 7;
    public static final int     BT_SOCKCLOSE_ERROR  = 8;
    public static final int     BT_SSTREAMIO_ERROR  = 9;
    public static final int     BT_STREAMINT_ERROR  = 10;
    public static final int     BT_STREAMOUT_ERROR  = 11;
    public static final int     BT_ERROR_UPDATE     = 12;
    //Activity related prompts
    public static final int     BT_PROMPT_DISCOVE   = 13;
    public static final int     BT_PROMPT_CONNECT   = 14;
    public static final int     BT_DEVICE_CONNECT   = 15;
    public static final int     BT_REQUEST_ENABLE   = 16;
    public static final int     BT_MESSAGE_READ     = 17;
    //String array containing status/error messages
    private static final String[]   Messages = {
            "IDLE",
            "LISTENING",
            "CONNECTING",
            "CONNECTED",
            "SCANNING",
            "NO ADAPTER FOUND",
            "SOCKET CREATION ERROR",
            "SOCKET CONNECTION ERROR",
            "SOCKET CLOSE ERROR",
            "SOCKET I/O STREAM ERROR",
            "INPUT STREAM ERROR",
            "OUTPUT STREAM ERROR" };

    public BluetoothAdapter getAdapter() {
        return btAdapter;
    }

    public void setState(int state) {
        State = state;

        if(State <= 4) {
            mHandler.obtainMessage(BT_STATE_UPDATE, State, -1).sendToTarget();
        }
        else {
            mHandler.obtainMessage(BT_ERROR_UPDATE, State, -1).sendToTarget();
        }
    }

    public int getState() {
        return State;
    }

    public String getStateMessage(int index) {
        return Messages[index];
    }

    public void Send(byte[] out) {
        if(btHandler != null) {
            btHandler.Send(out);
        }
    }

    /**
     * Class: DataHandler
     * Description: This class reads and writes through the Bluetooth Adapter
     */
    public class DataHandler extends Thread {
        //btSocket      Socket which is used for communication between client/server
        //inStream      Input Stream which is used to read data
        //outStream     Output Stream which is used to write data
        private BluetoothSocket   btSocket;
        private InputStream       inStream;
        private OutputStream      outStream;

        /**
         * Method: DataHandler
         * Description: Constructor which initializes the input and output streams
         * @param btsocket
         */
        public DataHandler(BluetoothSocket btsocket) {
            btSocket = btsocket;

            try {
                inStream = btSocket.getInputStream();
                outStream = btSocket.getOutputStream();
            }
            catch(IOException io) {
                //Failed opening socket IO stream
                io.printStackTrace();
                setState(BT_SSTREAMIO_ERROR);
            }
        }

        /**
         * Method: run
         * Description: This method constantly attempts to read from the stream, and if a message
         *              is found, passes it to the message handler
         */
        public void run() {
            byte[]  inData = new byte[1024];

            while(true) {
                try {
                    //Read data from inStream
                    int BytesRead = inStream.read(inData);

                    if(BytesRead > 0) {
                        //Message is valid, relay to message Handler
                        mHandler.obtainMessage(BT_MESSAGE_READ, BytesRead, -1, inData).sendToTarget();
                    }
                }
                catch(IOException io) {
                    //Error reading from input stream
                    io.printStackTrace();
                    setState(BT_STREAMINT_ERROR);
                }
            }
        }

        /**
         * Method: FormatPacket
         * Description: Method used to take in inputs and format a packet based off
         *              provided data.
         * @param Header    -   Packet Identifier
         * @param contents  -   Contents of packet
         * @return  -   Formatted array of bytes to send.
         */
        public byte[] FormatPacket(byte Header, int[] contents) {
           byte[] outData = new byte[(contents.length * 4) + 1];

            outData[0] = Header;

            for(int i = 0; i < contents.length; ++i) {
                System.arraycopy(ByteBuffer.allocate(4).putInt(contents[i]).array(), 0, outData,
                        (i*4) + 1, 4);
            }
            return outData;
        }

        /**
         * Method: FormatPacket
         * Description: Method used to take in inputs and format a packet based off
         *              provided data.
         * @param Header    -   Packet Identifier
         * @param contents  -   Contents of packet
         * @return  -   Formatted array of bytes to send.
         */
        public byte[] FormatPacket(byte Header, float[] contents) {
            byte[] outData = new byte[(contents.length * 4) + 1];

            outData[0] = Header;

            for(int i = 0; i < contents.length; ++i) {
                System.arraycopy(ByteBuffer.allocate(4).putFloat(contents[i]).array(), 0, outData,
                        (i*4) + 1, 4);
            }
            return outData;
        }

        /**
         * Method: ExtractIntArray
         * Description: Method used to convert an array of bytes into
         *              an array of integers.
         * @param data  - byte array to be converted
         * @return  - an array of integers representing the data
         */
        public int[] ExtractIntArray(byte[] data) {
            int[]   Contents = new int[(data.length-1)/4];

            for(int i = 0; i < Contents.length; ++i) {
                Contents[i] = ByteBuffer.wrap(Arrays.copyOfRange(data,
                        (i*4) + 1, ((i+1)*4) + 1)).getInt();
            }
            return Contents;
        }

        /**
         * Method: ExtractFloatArray
         * Description: Method used to convert an array of bytes into
         *              an array of floats.
         * @param data  - byte array to be converted
         * @return  - an array of floats representing the data
         */
        public float[] ExtractFloatArray(byte[] data) {
            float[]   Contents = new float[(data.length-1)/4];

            for(int i = 0; i < Contents.length; ++i) {
                Contents[i] = ByteBuffer.wrap(Arrays.copyOfRange(data,
                        (i*4) + 1, ((i+1)*4) + 1)).getFloat();
            }
            return Contents;
        }

        /**
         * Method: Send
         * Description: This method places output data onto the Output Stream
         * @param outData - Data to be written to stream
         */
        public void Send(byte[] outData) {
            if(btSocket.isConnected()) {
                try {
                    //Write to output stream
                    outStream.write(outData);
                }
                catch (IOException io) {
                    //Error writing to stream
                    io.printStackTrace();
                    setState(BT_STREAMOUT_ERROR);
                }
            }
        }

        /**
         * Method: cancel
         * Description: This method closes the in/out stream
         */
        public void cancel() {
            if(inStream != null && outStream != null) {
                try {
                    //Close I/O streams
                    inStream.close();
                    outStream.close();
                } catch (IOException io) {
                    //Error closing streams
                    io.printStackTrace();
                }
            }
        }
    }
}

