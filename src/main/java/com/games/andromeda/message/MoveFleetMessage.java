package com.games.andromeda.message;

import com.games.andromeda.graph.PathInfo;
import com.games.andromeda.logic.GameObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MoveFleetMessage extends SideMessage implements MessageFlags {

    public static class Move {
        public int fleetID;
        public float energy;
        public PathInfo pathInfo;

        public Move(int fleetID, float energy, PathInfo pathInfo) {
            this.fleetID = fleetID;
            this.energy = energy;
            this.pathInfo = pathInfo;
        }
    }

    private Move move;

    public MoveFleetMessage() {}

    public MoveFleetMessage(GameObject.Side side, Move move) {
        super(side);
        this.move = move;
    }

    @Override
    protected void onReadTransmissionData(DataInputStream pDataInputStream) throws IOException {
        super.onReadTransmissionData(pDataInputStream);
        int fleetID = pDataInputStream.readInt();
        float energy = pDataInputStream.readFloat();
        int pathLength = pDataInputStream.readInt();
        int size = pDataInputStream.readInt();
        List<Integer> nodes = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            nodes.add(pDataInputStream.readInt());
        }
        move = new Move(fleetID, energy, new PathInfo(nodes, pathLength));
    }

    @Override
    protected void onWriteTransmissionData(DataOutputStream pDataOutputStream) throws IOException {
        super.onWriteTransmissionData(pDataOutputStream);
        pDataOutputStream.writeInt(move.fleetID);
        pDataOutputStream.writeFloat(move.energy);
        pDataOutputStream.writeInt(move.pathInfo.getLength());
        pDataOutputStream.writeInt(move.pathInfo.getNodeIds().size());
        for (int node : move.pathInfo.getNodeIds()) {
            pDataOutputStream.writeInt(node);
        }
    }

    @Override
    public short getFlag() {
        return MOVE_FLEET_MESSAGE;
    }

    public Move getMove() {
        return move;
    }
}
