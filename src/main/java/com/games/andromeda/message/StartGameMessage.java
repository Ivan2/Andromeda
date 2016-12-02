package com.games.andromeda.message;

import com.games.andromeda.graph.Edge;
import com.games.andromeda.graph.Node;
import com.games.andromeda.logic.GameObject;

import org.andengine.util.color.Color;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StartGameMessage extends SideNodeListMessage {

    public static class TempEdge {
        public int node1ID;
        public int node2ID;
        public int weight;
        public Color color;

        public TempEdge(int node1ID, int node2ID, int weight, Color color) {
            this.node1ID = node1ID;
            this.node2ID = node2ID;
            this.weight = weight;
            this.color = color;
        }
    }

    private List<TempEdge> edgeList;

    public StartGameMessage() {
    }

    public StartGameMessage(GameObject.Side side, List<Node> nodes, List<Edge> edges) {
        super(side, nodes);
        edgeList = new ArrayList<>(edges.size());
        for (Edge edge : edges)
            edgeList.add(new TempEdge(edge.getNode1().getId(), edge.getNode2().getId(),
                    edge.getWeight(), edge.getColor()));
    }

    @Override
    protected void onReadTransmissionData(DataInputStream pDataInputStream) throws IOException {
        super.onReadTransmissionData(pDataInputStream);
        int size = pDataInputStream.readInt();
        edgeList = new ArrayList<>(size);
        for (int i=0; i<size; ++i) {
            int node1ID = pDataInputStream.readInt();
            int node2ID = pDataInputStream.readInt();
            int weight = pDataInputStream.readInt();
            float alpha = pDataInputStream.readFloat();
            float red = pDataInputStream.readFloat();
            float green = pDataInputStream.readFloat();
            float blue = pDataInputStream.readFloat();
            edgeList.add(new TempEdge(node1ID, node2ID, weight, new Color(red, green, blue, alpha)));
        }

    }

    @Override
    protected void onWriteTransmissionData(DataOutputStream pDataOutputStream) throws IOException {
        super.onWriteTransmissionData(pDataOutputStream);
        pDataOutputStream.writeInt(edgeList.size());
        for (TempEdge edge: edgeList) {
            pDataOutputStream.writeInt(edge.node1ID);
            pDataOutputStream.writeInt(edge.node2ID);
            pDataOutputStream.writeInt(edge.weight);
            pDataOutputStream.writeFloat(edge.color.getAlpha());
            pDataOutputStream.writeFloat(edge.color.getRed());
            pDataOutputStream.writeFloat(edge.color.getGreen());
            pDataOutputStream.writeFloat(edge.color.getBlue());
        }
    }

    @Override
    public short getFlag() {
        return START_GAME_MESSAGE;
    }

    public List<TempEdge> getEdgeList() {
        return edgeList;
    }
}