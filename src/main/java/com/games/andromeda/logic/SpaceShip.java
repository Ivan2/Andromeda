package com.games.andromeda.logic;

import java.util.Random;

public class SpaceShip {
    private boolean shield;
    private static Random random = new Random();

    public SpaceShip(){
        this.shield = true;
    }

    /**
     * Выстрел по кораблю
     * @return true, если выстрел смертельный
     */
    public boolean hit(){
        if (shield) {
            shield = false;
            return false;
        }
        return true;
    }

    public boolean tryToRestoreShield(float chance){
        if (random.nextFloat() <= chance){
            restoreShield();
        }
        return this.shield;
    }

    public void restoreShield(){
        this.shield = true;
    }

}
