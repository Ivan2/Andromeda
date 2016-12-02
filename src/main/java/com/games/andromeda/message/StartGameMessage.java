package com.games.andromeda.message;

import com.games.andromeda.graph.Edge;
import com.games.andromeda.graph.Node;
import com.games.andromeda.logic.GameObject;
import com.games.andromeda.logic.WorldAccessor;

import org.andengine.util.color.Color;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class StartGameMessage extends SideNodeListMessage {

    private List<Edge> edgeList;

    public StartGameMessage() {
    }

    public StartGameMessage(GameObject.Side side, List<Node> nodes, List<Edge> edges) {
        super(side, nodes);
        this.edgeList = edges;
    }

    @Override
    protected void onReadTransmissionData(DataInputStream pDataInputStream) throws IOException {
        super.onReadTransmissionData(pDataInputStream);
        edgeList = new ArrayList<>();
        int size = pDataInputStream.readInt();
        for (int i=0; i<size; ++i) {
            int node1ID = pDataInputStream.readInt();
            int node2ID = pDataInputStream.readInt();
            int weight = pDataInputStream.readInt();
            float alpha = pDataInputStream.readFloat();
            float red = pDataInputStream.readFloat();
            float green = pDataInputStream.readFloat();
            float blue = pDataInputStream.readFloat();
            edgeList.add(new Edge(
                    WorldAccessor.getInstance().getNodes().get(node1ID),
                    WorldAccessor.getInstance().getNodes().get(node2ID),
                    weight,
                    new Color(red, green, blue, alpha)
            ));
        }

    }

    @Override
    protected void onWriteTransmissionData(DataOutputStream pDataOutputStream) throws IOException {
        super.onWriteTransmissionData(pDataOutputStream);
        pDataOutputStream.writeInt(edgeList.size());
        for (Edge edge: edgeList) {
            pDataOutputStream.writeInt(edge.getNode1().getId());
            pDataOutputStream.writeInt(edge.getNode2().getId());
            pDataOutputStream.writeInt(edge.getWeight());
            pDataOutputStream.writeFloat(edge.getColor().getAlpha());
            pDataOutputStream.writeFloat(edge.getColor().getRed());
            pDataOutputStream.writeFloat(edge.getColor().getGreen());
            pDataOutputStream.writeFloat(edge.getColor().getBlue());
        }
    }

    @Override
    public short getFlag() {
        return START_GAME_MESSAGE;
    }

    public List<Edge> getEdgeList() {
        return edgeList;
    }
}