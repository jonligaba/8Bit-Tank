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
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.RectF;

/**
 * Class: Tank
 * Description: Class which will represent a Tank and be controlled by the player
 */
public class Tank extends GameObject {
    //Visual attributes
    private Animation       animation;
    private Paint           shader;
    private Paint           paint;
    private RectF           oval;
    private float           StrokeScale;
    //Other attributes
    private int             Angle;
    private int             dAngle;
    private boolean         Destroyed;
    private boolean         isPlayerA;
    private int             flashCounter;

    private int             BulletType;
    private int             MaxHealth;
    private int             MaxShield;
    private int             Health;
    private int             Shield;
    private int             Damage;

    /**
     * Method: Tank
     * Desecription: Constructor which initializes and assigns class attributes
     * @param bmp   -   tank bitmap
     * @param bmpW  -   bitmap width
     * @param bmpH  -   bitmap height
     * @param sX    -   scale X
     * @param sY    -   scale Y
     * @param Scale -   Boolean of Scaling
     * @param Frames-   Frames of bitmaps
     * @param PlayerA   -   if Left player
     * @param hp    -   health points
     * @param sh    -   shield points
     * @param dmg   -   dmg multiplier
     */
    public Tank(Bitmap bmp, int bmpW, int bmpH, int sX, int sY, boolean Scale, int Frames,
                boolean PlayerA, int hp, int sh, int dmg) {

        ColorFilter filter = new LightingColorFilter(0xFF222222, 0x00000000);
        Bitmap[]    bmp_array = new Bitmap[Frames];
        Bitmap[]    tmpBmp = new Bitmap[Frames];

        //Initialize objects
        oval = new RectF();
        paint = new Paint();
        shader = new Paint();
        animation = new Animation();
        //Initialize bitmap dimension
        shader.setColorFilter(filter);
        width = Scale ? sX : bmpW;
        height = Scale ? sY : bmpH;
        StrokeScale = Scale ? sX / bmpW : 1;
        //Constant initial values
        dx = 0;
        dy = 0;
        Angle = 45;
        BulletType = 1;
        flashCounter = -1;
        Destroyed = false;
        isPlayerA = PlayerA;
        //Set tank Attributes
        MaxHealth = hp;
        MaxShield = sh;
        Damage = dmg;
        Health = MaxHealth;
        Shield = MaxShield;
        //Loop through each frame and set each frame
        for(int i = 0; i < Frames; ++i) {
            //Split frames
            tmpBmp[i] = bmp == null ? null : Bitmap.createBitmap(bmp, i*bmpW, 0, bmpW, bmpH);

            if(Scale) {
                //Scaled Bitmaps
                bmp_array[i] = tmpBmp[i] == null ? null :
                        Bitmap.createScaledBitmap(tmpBmp[i], sX, sY, false);
            } else  {
                //Non-scaled
                bmp_array[i] = tmpBmp[i];
            }
        }
        //Assign animation values
        animation.setFrames(bmp_array);
        animation.setDelay(50);
    }

    /**
     * Method: update
     * Description: Method which updates the tanks animation and
     *              handles movement.
     */
    public void update() {
        animation.update();

        if(dx != 0) {
            //Update tank position
            if((dx > 0 && x < LimitX) || (dx < 0 && x > LimitX)) {
                x += dx;
            }
        }

        if(dAngle != 0) {
            //Update tank aim angle
            if((dAngle > 0 && Angle < 90) || (dAngle < 0 && Angle > 0)) {
                Angle += dAngle;
            }
        }
    }

    /**
     * Method: draw
     * Description: Method which handles the visual representation of the tank.
     * @param canvas    - Application canvas
     */
    public void draw(Canvas canvas) {
        if(Destroyed == false) {
            if (flashCounter != -1) {
                //Tank has been hit, flash the tank
                if ((flashCounter % 2) == 1) {
                    canvas.drawBitmap(animation.getImage(), x, y, shader);
                } else {
                    canvas.drawBitmap(animation.getImage(), x, y, null);
                }
                //Reset flash counter
                if (flashCounter >= 8) {
                    flashCounter = -1;
                } else {
                    ++flashCounter;
                }
            } else {
                //Draw the tank without shading
                canvas.drawBitmap(animation.getImage(), x, y, null);
            }
            //Setup paint stroke for Aim Arc
            paint.setColor(Color.RED);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(2.0f * StrokeScale);
            //Draw aiming ARC
            oval.set(getRect().left - 25, getRect().top - 25,
                    getRect().right + 25, getRect().bottom + 25);
            canvas.drawArc(oval, isPlayerA ? 270 : 180, 90, false, paint);
            //Draw Pointer
            paint.setColor(Color.BLUE);
            paint.setStrokeWidth(15.0f * StrokeScale);
            canvas.drawArc(oval, isPlayerA ? 270 + Angle : 270 - Angle, 2, false, paint);
            //Write Angle
            paint.setTextSize(15.0f);
            paint.setStrokeWidth(1.0f * StrokeScale);
            canvas.drawText(Integer.toString(90 - Angle),
                    isPlayerA ? getRect().right + 5 : getRect().left - 25,
                    getRect().bottom - (10 * StrokeScale), paint);
            //Draw Health Bar
            paint.setStrokeWidth(1.0f * StrokeScale);
            paint.setColor(Color.RED);
            canvas.drawRect(x, getRect().bottom + 15, getRect().right,
                    getRect().bottom + 15 + (7 * StrokeScale), paint);
            //Fill Health Bar
            paint.setStrokeWidth(2.5f * StrokeScale);
            canvas.drawLine(x + 5, getRect().bottom + 10 + (5 * StrokeScale),
                    x + 5 + ((Health * (width - 10)) / MaxHealth),
                    getRect().bottom + 10 + (5 * StrokeScale), paint);
            //Draw Shield Bar
            paint.setStrokeWidth(1.0f * StrokeScale);
            paint.setColor(Color.BLUE);
            canvas.drawRect(x, getRect().bottom + 25 + (7 * StrokeScale), getRect().right,
                    getRect().bottom + 25 + (14 * StrokeScale), paint);
            //Fill Shield Bar
            paint.setStrokeWidth(2.5f * StrokeScale);
            canvas.drawLine(x + 5, getRect().bottom + 30 + (7 * StrokeScale),
                    x + 5 + ((Shield * (width - 10)) / MaxShield),
                    getRect().bottom + 30 + (7 * StrokeScale), paint);
        }
    }

    /**
     * Method: moveX
     * Description: Method which moves the tank's position
     * @param deltaX    - amount of change in X
     * @param limitX    - Limit of change in X
     */
    public void moveX(int deltaX, int limitX) {
        dx = deltaX;
        LimitX = limitX;
    }

    /**
     * Method: AdjustShot
     * Desciption: Method used to adjust the aim angle
     * @param angle     - Change in angle
     */
    public void AdjustShot(int angle) {
        dAngle = angle;
    }

    /**
     * Method: applyDamage
     * Description: Method used to apply damage to tank corresponding to bullet type
     * @param obj           - Missile which intersects with tank
     * @param BulletType    - Type of missile
     * @param DmgMult       - Damage multiplier
     */
    public void applyDamage(GameObject obj, int BulletType, int DmgMult) {
        int LeftOverDmg = 0;
        int CalculatedDmg = 0;

        if(DmgMult > 0) {
            if (BulletType == 0) {
                // Basic, if you hit, same damage
                if (Shield >= DmgMult * 2) {
                    Shield -= DmgMult * 2;

                    if (Health > DmgMult) {
                        Health -= DmgMult;
                    } else {
                        Destroyed = true;
                        Health = 0;
                    }
                } else {
                    //Not enough shield, remove leftovers from Health
                    LeftOverDmg = (2 * DmgMult) - Shield;
                    Shield = 0;

                    if (Health > LeftOverDmg) {
                        Health -= LeftOverDmg;
                    } else {
                        Destroyed = true;
                        Health = 0;
                    }
                }
            } else if (BulletType == 1) {
                //Closer to center of tank, more damage
                double dist = Math.abs(getX() - obj.getX());

                if (dist <= (width / 2)) {
                    dist = (dist >= 0 && dist <= 1.0) ? 1.0 : Math.round(dist / 4);
                    CalculatedDmg = (int) Math.round((width / 4) / dist);
                    //Decrement portion from shield, then health
                    if (Shield >= (CalculatedDmg * DmgMult)) {
                        Shield -= (CalculatedDmg * DmgMult * 2);

                        if (Health > (CalculatedDmg * DmgMult)) {
                            Health -= (CalculatedDmg * DmgMult);
                        } else {
                            Destroyed = true;
                            Health = 0;
                        }
                    } else {
                        //Not enough shield, decrement leftover damage from health
                        LeftOverDmg = (CalculatedDmg * (DmgMult / 2)) - Shield;
                        Shield = 0;

                        if (Health > LeftOverDmg) {
                            Health -= LeftOverDmg;
                        } else {
                            Health = 0;
                            Destroyed = true;
                        }
                    }
                }
            } else if (BulletType == 2) {
                //Splash bullet, low damage
                if (Shield >= DmgMult) {
                    Shield -= DmgMult;

                    if (Health > DmgMult) {
                        Health -= DmgMult;
                    } else {
                        Destroyed = true;
                        Health = 0;
                    }
                } else {
                    //Not enough shield, remove leftovers from Health
                    LeftOverDmg = DmgMult - Shield;
                    Shield = 0;

                    if (Health > LeftOverDmg) {
                        Health -= LeftOverDmg;
                    } else {
                        Destroyed = true;
                        Health = 0;
                    }
                }
            }
        }
    }

    /*          Getters and Setters         */
    public void setBullet(int bullet) {
        BulletType = bullet >= 0 && bullet <= 3 ? bullet : 0;
    }

    public int getBullet() {
        return BulletType;
    }

    public void setAngle(int angle) {
        Angle = angle >= 0 && angle <= 90 ? angle : 0;
    }

    public int getAngle() {
        return Angle;
    }

    public int getHealth() {
        return Health;
    }

    public int getShield() { return Shield; }

    public void setHealth(int hp) {
        if (hp == 1337) {
            Health = MaxHealth;
        } else {
            Health = hp;
        }
    }

    public void setShield(int sh) {
        if (sh == 1337) {
            Shield = MaxShield;
        } else {
            Shield = sh;
        }
    }

    public int getDamageMult() {
        return Damage;
    }

    public void setDamageMult(int dmg) {
        Damage = dmg;
    }

    public void setStats(int[] stats) {
        Health = stats[0];
        MaxHealth = stats[0];
        Shield = stats[1];
        MaxShield = stats[1];
        Damage = stats[2];
    }

    public void Flash() {
        flashCounter = 0;
    }

    public boolean hasChanged() {
        return dx == 0 ? false : true;
    }
}
