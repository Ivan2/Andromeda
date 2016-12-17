package com.games.andromeda.graph;

public class Node {
    //типы системы
    public static enum SystemType {EMPTY, FRIENDLY, ENEMY, MINI, HYPER}

    private int id;
    //Относительные координаты 0..1
    private float x;
    private float y;

    private SystemType systemType;

    public Node(int id, float x, float y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }

    public Node(int id, float x, float y, SystemType systemType) {
        this(id, x, y);
        this.systemType = systemType;
    }

    public int getId() {
        return id;
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

    public void setSystemType(SystemType systemType) {
        this.systemType = systemType;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Node))
            return false;
        Node node = (Node)obj;
        return node.id == id;
    }

    @Override
    public String toString() {
        return String.format("%d(%.2f; %.2f)", id, x, y);
    }
}
