package com.games.andromeda.logic;

public interface FleetProperties {
    float getShieldRestoringChance();
    int getAttack(int shipCount);
    int getSpeed(int shipCount);
    int getMaxShipCount();
    float getEnergyRestore();
    int getShipCost();  // не будем связываться с копейками
    int getEmptyFleetCost();
}
