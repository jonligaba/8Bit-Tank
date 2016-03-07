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
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;

public class ProfileActivity extends Activity {
    private int                     profileIndex;
    private final String            PROFILES_PREF = "Bit8Profiles";
    private SharedPreferences       Settings;
    private InputMethodManager      inKeyboard;

    private ArrayAdapter<String>    Profiles;
    private ListView                ProfileView;

    private EditText                editText;
    private android.widget.Button   createBtn;

    /**
     * Method: onCreate
     * Description: THis method will intiailize the activity and load
     *              all profiles.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String[] profileNames = new String[3];

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getWindow().getDecorView().setBackgroundResource(R.drawable.profilemenu);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        //Set to canceled just incase user backs out
        setResult(Activity.RESULT_CANCELED);
        //Initialize Device List View
        Profiles = new ArrayAdapter<String>(this, R.layout.device_name);
        ProfileView = (ListView) findViewById(R.id.profile_view);
        ProfileView.setAdapter(Profiles);
        ProfileView.setOnItemClickListener(ProfileClickListener);
        //Load Shared Preferences
        Settings = getSharedPreferences(PROFILES_PREF, 0);
        Profiles.add(Settings.getString("profile_1", "<new user>"));
        Profiles.add(Settings.getString("profile_2", "<new user>"));
        Profiles.add(Settings.getString("profile_3", "<new user>"));

        editText = (EditText) findViewById(R.id.editText);
        createBtn = (android.widget.Button)findViewById(R.id.button2);

        editText.setOnFocusChangeListener(editFocusListener);
        createBtn.setOnClickListener(btnClickListener);
    }
    /**
     * Method: onDestroy (Override)
     * Description: This method will properly destroy this activity.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private final View.OnClickListener btnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            SharedPreferences.Editor editor = Settings.edit();
            //Save profile information
            editor.putString("profile_" + Integer.toString(profileIndex), editText.getText().toString());
            editor.putInt(editText.getText().toString() + "_ctrl", 0);
            editor.putInt(editText.getText().toString() + "_exp", 0);
            editor.commit();
            //Update list view profile information
            Profiles.remove(Profiles.getItem(profileIndex));
            Profiles.insert(editText.getText().toString(), profileIndex);
            Profiles.notifyDataSetChanged();

            if(inKeyboard.isActive()) {
                editText.setFocusable(false);
                inKeyboard.toggleSoftInput(InputMethodManager.HIDE_NOT_ALWAYS, 0);
            }
        }
    };

    /**
     * Object: editFocusListener
     * Description: This will handle the onFocus event for the editText
     */
    private final View.OnFocusChangeListener editFocusListener = new View.OnFocusChangeListener() {
        /**
         * Method: onFocusChange
         * Description: Override method to handle onFocus Change event
         * @param v - View
         * @param hasFocus  - whether the component has focus or not
         */
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            //Request a soft keyboard.
            if (hasFocus) {
                inKeyboard = (InputMethodManager) getWindow().getContext().
                        getSystemService(Context.INPUT_METHOD_SERVICE);

                inKeyboard.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            }
        }
    };

    /**
     * Object: ProfileClickListener
     * Description: This method is used to handle click events for the
     *              ListView.
     */
    private AdapterView.OnItemClickListener ProfileClickListener = new AdapterView.OnItemClickListener() {
        /**
         * Method: onItemClick Override
         * Description: This method handles the onItemClick Event for the
         *              parent ListView.
         * @param adapterview   -   the adapter view of the ListVIew
         * @param view  -   the View
         * @param arg2  -   Argument 2
         * @param arg3  -   Argument 3
         */
        @Override
        public void onItemClick(AdapterView<?> adapterview, View view, int arg2, long arg3) {
            String ProfileInfo = ((TextView)view).getText().toString();

            if(ProfileInfo.compareToIgnoreCase("<new user>") == 0) {    //Create new profile
                profileIndex = arg2+1;
                editText.setFocusable(true);
                editText.requestFocus();
            }
            else {
                //Return Name and Experience
                Intent intent = new Intent();
                intent.putExtra("profile_name", ProfileInfo);
                intent.putExtra("profile_ctrl", Settings.getInt(ProfileInfo + "_ctrl", 0));
                intent.putExtra("profile_exp", Settings.getInt(ProfileInfo + "_exp", 0));
                //Hide input keyboard
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        }
    };
}

