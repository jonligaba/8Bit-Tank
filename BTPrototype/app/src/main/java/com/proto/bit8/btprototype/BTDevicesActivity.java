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
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Class: BTDevicesActivity
 * Description: This class is used to list all found devices and connect to one
 */
public class BTDevicesActivity extends Activity
{
    //Devices       ArrayAdapter containing found devices to list in ListView
    //btAdapter     Reference to Bluetooth Adapter
    //DeviceView    ListView to be used to display all found devices
    private ArrayAdapter<String>    Devices;
    private BluetoothAdapter        btAdapter;
    private ListView                DeviceView;
    private android.widget.Button   stopBtn;

    /**
     * Method: onCreate (Override)
     * Description: This method initializes the Device ListView and enables discovery of bluetooth
     *              adapters
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        getWindow().getDecorView().setBackgroundResource(R.drawable.profilemenu);
        setContentView(R.layout.activity_btdevices);
        //Set to canceled just incase user backs out
        setResult(Activity.RESULT_CANCELED);
        //Initialize Device List View
        Devices = new ArrayAdapter<String>(this, R.layout.device_name);
        DeviceView = (ListView)findViewById(R.id.new_devices);
        DeviceView.setAdapter(Devices);
        DeviceView.setOnItemClickListener(DeviceViewClickListener);
        //Initialize stop scan button
        stopBtn = (android.widget.Button)findViewById(R.id.button);
        stopBtn.setOnClickListener(ButtonClickListener);
        //Initialize Bluetooth Adapter
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        //Begin discovering devices
        btAdapter.startDiscovery();
        //Set up Receiver intent filters
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(btReceiver, filter);
    }

    /**
     * Method: onDestroy (Override)
     * Description: This method disables discovering devices if closing
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(btAdapter != null) {
            btAdapter.cancelDiscovery();
        }
        //Unregister the receiver
        unregisterReceiver(btReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_btdevices, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Method: DeviceViewClickListener
     * Description: Handler which processes all clicking events
     */
    private AdapterView.OnItemClickListener DeviceViewClickListener
            = new AdapterView.OnItemClickListener()
    {
        /**
         * Method: onItemClick
         * Dscription: Processes all events pertaining to item clicks
         * @param adapterview
         * @param view
         * @param arg2
         * @param arg3
         */
        public void onItemClick(AdapterView<?> adapterview, View view, int arg2, long arg3)
        {
            //Cancel discovery
            btAdapter.cancelDiscovery();
            //Get Device MAC Address
            String DeviceInformation = ((TextView)view).getText().toString();
            String DeviceMAC = DeviceInformation.substring(DeviceInformation.length() - 17);
            Intent intent = new Intent();
            //Return MAC address to activity result callback
            intent.putExtra("device_address", DeviceMAC);
            setResult(Activity.RESULT_OK, intent);
            //Close this activity
            finish();
        }
    };

    /**
     * Object: ButtonClickListener
     * Description: This object will handle button click events for this activity.
     */
    private View.OnClickListener ButtonClickListener = new View.OnClickListener() {
        /**
         * Method: onClick Override
         * Description: This method will handle the onclick event
         * @param v
         */
        @Override
        public void onClick(View v) {
            if(btAdapter != null) {
                btAdapter.cancelDiscovery();
            }
        }
    };

    /**
     * Method: BroadcastReceiver
     * Description: Receiver which is used to handle vents such as device finds
     */
    private final BroadcastReceiver btReceiver = new BroadcastReceiver()
    {
        /**
         * Method: onReceive
         * Description: Handles events pertaining to finding a new device
         * @param context - Reference to activity's context
         * @param intent - Reference to intent
         */
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();

            //If device is found
            if (BluetoothDevice.ACTION_FOUND.equals(action))
            {
                //Add device to Device ListView
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Devices.add(device.getName() + "\n" + device.getAddress());
            }
            else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action))
            {
                //Discovering has completed
            }
        }
    };
}
