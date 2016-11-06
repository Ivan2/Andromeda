package com.games.andromeda.message;


import com.games.andromeda.graph.Node;
import com.games.andromeda.logic.GameObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

class SideNodeMessage extends SideMessage {
    private Node node;

    public SideNodeMessage() {
    }

    public SideNodeMessage(GameObject.Side side, Node node) {
        super(side);
        this.node = node;
    }

    @Override
    protected void onReadTransmissionData(DataInputStream pDataInputStream) throws IOException {
        super.onReadTransmissionData(pDataInputStream);
        float x = pDataInputStream.readFloat();
        float y = pDataInputStream.readFloat();
        node = new Node(x, y);
    }

    @Override
    protected void onWriteTransmissionData(DataOutputStream pDataOutputStream) throws IOException {
        super.onWriteTransmissionData(pDataOutputStream);
        pDataOutputStream.writeFloat(node.getX());
        pDataOutputStream.writeFloat(node.getY());
    }

    @Override
    public short getFlag() {
        return 1;
    }

    public Node getNode() {
        return node;
    }
}
