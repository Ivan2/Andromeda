package com.games.andromeda.message;

import com.games.andromeda.graph.Node;
import com.games.andromeda.logic.Fleet;
import com.games.andromeda.logic.GameObject;
import com.games.andromeda.logic.Pair;
import com.games.andromeda.logic.WorldAccessor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

public class MoveFleetMessage extends SideMessage implements MessageFlags {

    private Collection<Pair<Fleet, Node>> moves;

    public MoveFleetMessage() {}

    public MoveFleetMessage(GameObject.Side side, Collection<Pair<Fleet, Node>> moves) {
        super(side);
        this.moves = moves;
    }

    @Override
    protected void onReadTransmissionData(DataInputStream pDataInputStream) throws IOException {
        super.onReadTransmissionData(pDataInputStream);
        int size = pDataInputStream.readInt();
        moves = new ArrayList<>(size);
        for (int i=0; i<size; i++) {
            int id = pDataInputStream.readInt();
            float energy = pDataInputStream.readFloat();
            int node = pDataInputStream.readInt();

            Fleet fleet = WorldAccessor.getInstance().getFleet(getSide(), id);
            if (fleet != null) {
                fleet.setEnergy(energy);
                fleet.setPosition(node);
            }
        }
    }

    @Override
    protected void onWriteTransmissionData(DataOutputStream pDataOutputStream) throws IOException {
        super.onWriteTransmissionData(pDataOutputStream);
        pDataOutputStream.writeInt(moves.size());
        for (Pair<Fleet, Node> entry : moves) {
            pDataOutputStream.writeInt(entry.getKey().getId());
            pDataOutputStream.writeFloat(entry.getKey().getEnergy());
            pDataOutputStream.writeInt(entry.getValue().getId());
        }
    }

    @Override
    public short getFlag() {
        return MOVE_FLEET_MESSAGE;
    }

}
