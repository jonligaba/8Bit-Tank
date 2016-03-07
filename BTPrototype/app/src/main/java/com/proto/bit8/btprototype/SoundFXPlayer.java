package com.proto.bit8.btprototype;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.provider.MediaStore;
import android.util.Log;

import java.io.IOException;

/**
 * Class: SoundFXPlayer
 * Description: Class which handles sound effects
 */
public class SoundFXPlayer {
    //Array containing all game sound effects
    private int[]           resids;
    private Context         context;
    private SoundPool       sndManager;
    //constants used to identify each sound effect
    public static final int    SND_FX_TRAVEL   = 0;
    public static final int    SND_FX_CHARGE   = 1;
    public static final int    SND_FX_SHOOT    = 2;
    public static final int    SND_FX_DAMAGE1  = 3;
    public static final int    SND_FX_DAMAGE2  = 4;
    public static final int    SND_FX_DAMAGE3  = 5;
    public static final int    SND_FX_TANKHIT  = 6;
    public static final int    SND_FX_TANKAIM  = 7;
    public static final int    SND_FX_BTNPRES  = 8;
    public static final int    SND_FX_WINDSTM  = 9;
    //public static final int    SND_FX_SONG     = 8;

    /**
     * Method: SoundFXPlayer=
     * Description: Constructor used to initialize each sound effect
     * @param context - Reference to SurfaceView's context
     */
    public SoundFXPlayer(Context context) {
        //Initialize each sound effect
        resids = new int[10];
        this.context = context;
        //0=UI, 1=TANK, 2=MISSILES, 3=STORM
        sndManager = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
        resids[0] = sndManager.load(context, R.raw.travel, 1);
        resids[1] = sndManager.load(context, R.raw.charge, 1);
        resids[2] = sndManager.load(context, R.raw.fire, 1);
        resids[3] = sndManager.load(context, R.raw.damagea, 1);
        resids[4] = sndManager.load(context, R.raw.damageb, 1);
        resids[5] = sndManager.load(context, R.raw.damagec, 1);
        resids[6] = sndManager.load(context, R.raw.tankhit, 1);
        resids[7] = sndManager.load(context, R.raw.tankaim, 1);
        resids[8] = sndManager.load(context, R.raw.buttonpress, 1);
        resids[9] = sndManager.load(context, R.raw.wind, 1);
    }

    /**
     * Method: play
     * Description: Method which resets and starts a sound effect
     * @param sound_id  -   sound effect id
     */
    public void play(int sound_id) {
        try {
            sndManager.play(resids[sound_id], 1.0f, 1.0f, 0, 0, 1.5f);
        }
        catch(Exception e) {
            //Caught illegalStateException, Ignore due to no damage
        }
    }

    /**
     * Method: pause
     * Description: Method which pauses the sound effect
     */
    public void pause(int sound_id) {
        if(sound_id != -1) {
            sndManager.stop(resids[sound_id]);
        }
        else {
            for (int i = 0; i < resids.length; ++i) {
                sndManager.stop(resids[i]);
            }
        }
    }

}
