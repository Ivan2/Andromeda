package com.games.andromeda.logic;

import java.util.Random;

/**
 * Сильный, но медленный
 */
public class BasicEmpireFleetProperties implements FleetProperties {
    private static Random random = new Random();
    @Override
    public float getShieldRestoringChance() {
        return 0.5f;
    }

    @Override
    public int getAttack(int shipCount) {
        return 2*(shipCount/7) + 4;
    }

    @Override
    public int getSpeed(int shipCount) {
        return (getMaxShipCount() + 1 - shipCount)/5 + 3;
    }

    @Override
    public int getMaxShipCount() {
        return 19;
    }

    @Override
    public float getEnergyRestore() {
        return (float)(40 + random.nextInt(40))/100;
    }

    @Override
    public int getShipCost() {
        return 30;
    }

    @Override
    public int getEmptyFleetCost() {
        return 20;
    }
}
