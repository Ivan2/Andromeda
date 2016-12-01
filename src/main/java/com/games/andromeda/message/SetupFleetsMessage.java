package com.games.andromeda.message;

import android.util.Log;

import com.games.andromeda.graph.Node;
import com.games.andromeda.logic.Base;
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

        Collection<Base> bases = WorldAccessor.getInstance().getBases();

        for (int i=0; i<count; ++i) {
            float x = pDataInputStream.readFloat();
            float y = pDataInputStream.readFloat();
            int shipCount = pDataInputStream.readInt();
            GameObject.Side side = getSide();
            //костыли (нужен id вершины вместо базы)
            Base base = null;
            for (Base b : bases)
                if (b.getNode().equals(new Node(x, y)))
                    base = b;
            try {
                fleets.add(new Fleet(side, shipCount, base));
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
            pDataOutputStream.writeFloat(fleet.getPosition().getX());
            pDataOutputStream.writeFloat(fleet.getPosition().getY());
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