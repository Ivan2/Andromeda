package com.games.andromeda.message.unused;


import com.games.andromeda.graph.Node;
import com.games.andromeda.logic.GameObject;
import com.games.andromeda.message.SideMessage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class SideNodeMessage extends SideMessage {
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
        int id = pDataInputStream.readInt();
        float x = pDataInputStream.readFloat();
        float y = pDataInputStream.readFloat();
        int type = pDataInputStream.readInt();
        node = new Node(id, x, y, Node.SystemType.values()[type]);
    }

    @Override
    protected void onWriteTransmissionData(DataOutputStream pDataOutputStream) throws IOException {
        super.onWriteTransmissionData(pDataOutputStream);
        pDataOutputStream.writeInt(node.getId());
        pDataOutputStream.writeFloat(node.getX());
        pDataOutputStream.writeFloat(node.getY());
        pDataOutputStream.writeInt(node.getSystemType().ordinal());
    }

    @Override
    public short getFlag() {
        return SIDE_NODE_MESSAGE;
    }

    public Node getNode() {
        return node;
    }
}
