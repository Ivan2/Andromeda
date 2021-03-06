package com.games.andromeda.logic;

import com.games.andromeda.graph.Node;

/**
 * класс-обёртка для покупки
 */
public class Purchase {

    public enum Kind {
        BUILD_BASE,
        BUY_FLEET,
        UPGRADE_FLEET
    }

    private Kind kind;
    private Node node;

    public Base base;
    public Fleet fleet;

    public Purchase(Kind kind, Node node) {
        this.kind = kind;
        this.node = node;
    }

    public Purchase(Fleet fleet, Node node){
        this.fleet = fleet;
        this.node = node;
        this.kind = Kind.UPGRADE_FLEET;
    }

    public Kind getKind() {
        return kind;
    }

    public Node getNode() {
        return node;
    }

}
