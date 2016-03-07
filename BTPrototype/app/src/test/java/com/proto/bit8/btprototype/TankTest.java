package com.proto.bit8.btprototype;

import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TankTest {
    private Tank[]      testTanks;
    private Missile[][] testMissiles;

    @Before
    public void InitializeTest() {
        //There are only every 2 tanks in one match
        testTanks = new Tank[2];
        //There are only ever 6 missiles in one match, 3 per tank
        testMissiles = new Missile[2][4];
        //Initialize tanks 1 and 2
        testTanks[0] = new Tank(null, 100, 100, 100, 100, false, 0, true, 100, 100, 100);
        testTanks[1] = new Tank(null, 100, 100, 100, 100, false, 0, false, 100, 100, 100);
        testTanks[0].setXY(0, 0);
        testTanks[1].setXY(500, 0);
        //Initialize all missiles
        for(int i = 0; i < 2; ++i) {
            for(int j = 0; j < 4; ++j) {
                //Create Standby Missiles
                testMissiles[i][j] = new Missile(null, 50, 50, 50, 50, true, 10+(2*j), 8, 50, 50);
            }
        }
    }

    @Test
    //public void applyDamage(GameObject obj, int BulletType, int DmgMult)
    public void testCriticalBulletCollisions() throws Exception {
        //One missile outside tank on LEFT Side
        testMissiles[0][0].setXY(testTanks[1].getX() - 250, 0);
        //One missile directly centered on tank
        testMissiles[0][1].setXY(testTanks[1].getX(), 0);
        //One missile outside tank on RIGHT Size
        testMissiles[0][2].setXY(testTanks[1].getX()+250, 0);
        //One missile which gets closer and closer
        testMissiles[1][0].setXY(testTanks[1].getX() - 50, 0);
        //One missile directly centered on tank
        testMissiles[1][1].setXY(testTanks[1].getX() + 25, 0);
        //One missile outside tank on RIGHT Size
        testMissiles[1][2].setXY(testTanks[1].getX() - 10, 0);
        //Apply missiles one by one
        assertEquals(100, testTanks[1].getHealth());
        testTanks[1].applyDamage(testMissiles[0][0], 1, 1);
        assertEquals(100, testTanks[1].getHealth());
        testTanks[1].applyDamage(testMissiles[0][1], 1, 1);
        assertEquals(75, testTanks[1].getHealth());
        testTanks[1].applyDamage(testMissiles[0][2], 1, 1);
        assertEquals(75, testTanks[1].getHealth());
        testTanks[1].applyDamage(testMissiles[1][0], 1, 1);
        assertEquals(73, testTanks[1].getHealth());
        testTanks[1].applyDamage(testMissiles[1][1], 1, 1);
        assertEquals(69, testTanks[1].getHealth());
        testTanks[1].applyDamage(testMissiles[1][2], 1, 1);
        assertEquals(61, testTanks[1].getHealth());
    }

    @Test
    public void testBulletTypes() {
        //One missile outside tank on LEFT Side
        testMissiles[0][0].setXY(testTanks[1].getX() - 250, 0);
        //One missile directly centered on tank
        testMissiles[0][1].setXY(testTanks[1].getX(), 0);
        //One missile outside tank on RIGHT Size
        testMissiles[0][2].setXY(testTanks[1].getX() + 250, 0);
        //One missile which gets closer and closer
        testMissiles[1][0].setXY(testTanks[1].getX() - 50, 0);
        //One missile directly centered on tank
        testMissiles[1][1].setXY(testTanks[1].getX() + 25, 0);
        //One missile outside tank on RIGHT Size
        testMissiles[1][2].setXY(testTanks[1].getX() - 10, 0);

        for(int i = -10; i < 10; ++i) { //only valid from 0-2
            int     health = testTanks[1].getHealth();

            testTanks[1].applyDamage(testMissiles[0][0], i, 1);

            if(i != 1) {
                if (i >= 0 && i <= 2) {
                    assertNotEquals(health, testTanks[1].getHealth());
                } else {
                    assertEquals(health, testTanks[1].getHealth());
                }
            }
        }
    }

    @Test
    public void testDamageMultipliers() {
        //One missile outside tank on LEFT Side
        testMissiles[0][0].setXY(testTanks[1].getX() - 250, 0);
        //One missile directly centered on tank
        testMissiles[0][1].setXY(testTanks[1].getX(), 0);
        //One missile outside tank on RIGHT Size
        testMissiles[0][2].setXY(testTanks[1].getX() + 250, 0);
        //One missile which gets closer and closer
        testMissiles[1][0].setXY(testTanks[1].getX() - 50, 0);
        //One missile directly centered on tank
        testMissiles[1][1].setXY(testTanks[1].getX() + 25, 0);
        //One missile outside tank on RIGHT Size
        testMissiles[1][2].setXY(testTanks[1].getX() - 10, 0);

        for(int i = -10; i < 10; ++i) { //only valid after 0
            int     health = testTanks[1].getHealth();

            testTanks[1].applyDamage(testMissiles[0][0], 0, i);

            if(i > 0) {
                assertNotEquals(health, testTanks[1].getHealth());
            } else {
                assertEquals(health, testTanks[1].getHealth());
            }
        }
    }


    @After
    public void FinishTest() {
    }
}