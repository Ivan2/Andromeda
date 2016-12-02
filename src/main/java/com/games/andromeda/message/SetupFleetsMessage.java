package com.games.andromeda.message;

import android.util.Log;

import com.games.andromeda.graph.Node;
import com.games.andromeda.logic.Fleet;
import com.games.andromeda.logic.GameObject;
import com.games.andromeda.logic.WorldAccessor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class SetupFleetsMessage extends SideNodeListMessage {

    private Collection<Fleet> fleets;

    public SetupFleetsMessage() {
    }

    public SetupFleetsMessage(GameObject.Side side, List<Node> nodes, Collection<Fleet> fleets) {
        super(side, nodes);
        this.fleets = fleets;
    }

    @Override
    protected void onReadTransmissionData(DataInputStream pDataInputStream) throws IOException {
        super.onReadTransmissionData(pDataInputStream);
        int count = pDataInputStream.readInt();
        fleets = new LinkedList<>();

        for (int i=0; i<count; ++i) {
            int nodeID = pDataInputStream.readInt();
            int shipCount = pDataInputStream.readInt();
            GameObject.Side side = getSide();
            try {
                fleets.add(new Fleet(side, shipCount, WorldAccessor.getInstance().getBases().get(nodeID)));
            } catch (Exception e) {
                Log.wtf("error", e.toString());
            }
        }
    }

    @Override
    protected void onWriteTransmissionData(DataOutputStream pDataOutputStream) throws IOException {
        super.onWriteTransmissionData(pDataOutputStream);
        pDataOutputStream.writeInt(fleets.size());
        for (Fleet fleet : fleets) {
            pDataOutputStream.writeInt(fleet.getPosition());
            pDataOutputStream.writeInt(fleet.getShipCount());
        }
    }

    @Override
    public short getFlag() {
        return SETUP_FLEET_MESSAGE;
    }

    public Collection<Fleet> getFleets() {
        return fleets;
    }
}