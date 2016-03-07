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

import android.content.SharedPreferences;

public class Profile {
    private boolean[]   Unlockables;

    private String      profileName;
    private int         ControlType;
    private int         Experience;
    private int         Level;

    private final int[] LevelReq = {0, 10, 35, 85, 185, 400, 800, 1500, 2500, 5000};
    //Tank2, Bullet2, Tank3, Bullet3, Tank4, Bullet4, Tank5
    private final int[] UnlockReq = {2, 3, 4, 5, 6, 7, 8};
    /**
     * Method: Profile
     * Description: Constructor used to initialize new profile
     * @param name  -   Name of profile
     */
    Profile(String name) {
        //Two unlockables for now, special tank and special missile
        Unlockables = new boolean[7];
        Unlockables[0] = false;
        Unlockables[1] = false;
        Unlockables[2] = false;
        Unlockables[3] = false;
        Unlockables[4] = false;
        Unlockables[5] = false;
        Unlockables[6] = false;
        //Profile Attributes
        profileName = name;
        ControlType = 0;
        Experience = 0;
        Level = 1;
    }

    /**
     * Method: addExp
     * Description: Method used to increment experience by a set amount
     *              and update level accordingly.
     * @param exp   Amount of Experience to increment by
     */
    void addExp(int exp) {
        //Increment Experience
        Experience += exp;
        //Set Level accordingly to list of level requirements
        for(int i = 0; i < LevelReq.length; ++i) {
            if(Experience >= LevelReq[i]) {
                Level = i+1;
            }
        }
        //Set unlock attributes according to requirements
        for(int i = 0; i < Unlockables.length; ++i) {
            Unlockables[i] = (Level >= UnlockReq[i] ? true : false);
        }
    }

    /**
     * Method: saveProfile
     * Description: This method will save the profile information
     *              to the provided shared preference.
     * @param sp    -   Shared Preference
     */
    void saveProfile(SharedPreferences sp) {
        if (sp != null) {
            SharedPreferences.Editor editor = sp.edit();

            editor.putInt(profileName + "_exp", Experience);
            editor.putInt(profileName + "_ctrl", ControlType);
            editor.commit();
        }
    }

    /*      Getters and Setters     */
    void setControlType(int type) {
        ControlType = type;
    }

    int getControlType() {
        return ControlType;
    }

    String getName() {
        return profileName;
    }

    boolean[] getUnlockables() {
        //Set unlock attributes according to requirements
        for(int i = 0; i < Unlockables.length; ++i) {
            Unlockables[i] = (Level >= UnlockReq[i] ? true : false);
        }
        return Unlockables;
    }

    void setExperience(int exp) {
        Experience = exp;
        addExp(0);
    }

    int getExperience() {
        return Experience;
    }

    int getLevel() {
        return Level;
    }
}
