package com.games.andromeda.logic;

import java.util.Random;

/**
 * Быстрый, но уязвимый
 */

public class BasicFederationFleetProperties implements FleetProperties {
    private static Random random = new Random();
    @Override
    public float getShieldRestoringChance() {
        return 0.3f;
    }

    @Override
    public int getAttack(int shipCount) {
        return 4 + (shipCount + 2)/7;
    }

    @Override
    public int getSpeed(int shipCount) {
        return (getMaxShipCount() + 1 - shipCount)/5 + 5;
    }

    @Override
    public int getMaxShipCount() {
        return 14;
    }

    @Override
    public float getEnergyRestore() {
        return (float)(55 + random.nextInt(30))/100;
    }

    @Override
    public int getShipCost() {
        return 20;
    }

    @Override
    public int getEmptyFleetCost() {
        return 40;
    }
}
