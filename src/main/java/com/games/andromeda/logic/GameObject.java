package com.games.andromeda.logic;

public class GameObject {
    public enum Side {EMPIRE, FEDERATION}
    private Side side;

    public GameObject(Side side) {
        this.side = side;
    }

    public Side getSide() {
        return side;
    }
}
