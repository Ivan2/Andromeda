package com.games.andromeda.graph;

import com.games.andromeda.logic.Base;
import com.games.andromeda.logic.Fleet;

import java.util.ArrayList;
import java.util.List;

public class Node {

    //типы системы
    public static enum SystemType {EMPTY, FRIENDLY, ENEMY}

    //Относительные координаты 0..1
    private float x;
    private float y;

    private SystemType systemType;


    private Base base = null;
    private List<Fleet> fleets;

    public Node(float x, float y, SystemType systemType) {
        this.x = x;
        this.y = y;
        this.systemType = systemType;

        fleets = new ArrayList<>();
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public SystemType getSystemType() {
        return systemType;
    }

    @Override
    public int hashCode() {
        return (int)(x*1000000+y*1000000);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Node))
            return false;
        Node node = (Node)obj;
        return node.x == x && node.y == y;
    }

    @Override
    public String toString() {
        return String.format("%.2f %.2f", x, y);
    }
}
