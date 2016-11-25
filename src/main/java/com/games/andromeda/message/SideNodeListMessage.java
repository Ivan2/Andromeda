package com.games.andromeda.message;

import com.games.andromeda.graph.Node;
import com.games.andromeda.logic.GameObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class SideNodeListMessage extends SideMessage {
    private List<Node> nodeList;

    public SideNodeListMessage() {
    }

    public SideNodeListMessage(GameObject.Side side, List<Node> nodes) {
        super(side);
        this.nodeList = nodes;
    }

    @Override
    protected void onReadTransmissionData(DataInputStream pDataInputStream) throws IOException {
        super.onReadTransmissionData(pDataInputStream);
        nodeList = new ArrayList<>();
        int size = pDataInputStream.readInt();
        for (int i=0; i<size; ++i){
            float x = pDataInputStream.readFloat();
            float y = pDataInputStream.readFloat();
            nodeList.add(new Node(x, y));
        }

    }

    @Override
    protected void onWriteTransmissionData(DataOutputStream pDataOutputStream) throws IOException {
        super.onWriteTransmissionData(pDataOutputStream);
        pDataOutputStream.writeInt(nodeList.size());
        for (Node node: nodeList){
            pDataOutputStream.writeFloat(node.getX());
            pDataOutputStream.writeFloat(node.getY());
        }
    }

    @Override
    public short getFlag() {
        return SIDE_NODE_LIST_MESSAGE;
    }

    public List<Node> getNodeList() {
        return nodeList;
    }
}