package com.games.andromeda.message;

import com.games.andromeda.logic.GameObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

public class MoveFleetMessage extends SideMessage implements MessageFlags {

    public static class Move {
        public int fleetID;
        public float energy;
        public int nodeID;

        public Move(int fleetID, float energy, int nodeID) {
            this.fleetID = fleetID;
            this.energy = energy;
            this.nodeID = nodeID;
        }
    }

    private Collection<Move> moves;

    public MoveFleetMessage() {}

    public MoveFleetMessage(GameObject.Side side, Collection<Move> moves) {
        super(side);
        this.moves = moves;
    }

    @Override
    protected void onReadTransmissionData(DataInputStream pDataInputStream) throws IOException {
        super.onReadTransmissionData(pDataInputStream);
        int size = pDataInputStream.readInt();
        moves = new ArrayList<>(size);
        for (int i=0; i<size; i++) {
            int fleetID = pDataInputStream.readInt();
            float energy = pDataInputStream.readFloat();
            int nodeID = pDataInputStream.readInt();
            moves.add(new Move(fleetID, energy, nodeID));
        }
    }

    @Override
    protected void onWriteTransmissionData(DataOutputStream pDataOutputStream) throws IOException {
        super.onWriteTransmissionData(pDataOutputStream);
        pDataOutputStream.writeInt(moves.size());
        for (Move move : moves) {
            pDataOutputStream.writeInt(move.fleetID);
            pDataOutputStream.writeFloat(move.energy);
            pDataOutputStream.writeInt(move.nodeID);
        }
    }

    @Override
    public short getFlag() {
        return MOVE_FLEET_MESSAGE;
    }

    public Collection<Move> getMoves() {
        return moves;
    }
}
