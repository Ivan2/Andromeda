package com.games.andromeda.message;

import com.games.andromeda.logic.GameObject;
import com.games.andromeda.logic.SpaceShip;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FleetStateMessage extends SideMessage implements MessageFlags {

    public static class FleetState {
        public int fleetID;
        public float energy;
        public GameObject.Side side;
        public List<SpaceShip> ships;

        public FleetState(int fleetID, float energy, GameObject.Side side, List<SpaceShip> ships) {
            this.fleetID = fleetID;
            this.energy = energy;
            this.side = side;
            this.ships = ships;
        }
    }

    private FleetState fleetState;

    public FleetStateMessage() {}

    public FleetStateMessage(GameObject.Side side, FleetState fleetState) {
        super(side);
        this.fleetState = fleetState;
    }

    @Override
    protected void onReadTransmissionData(DataInputStream pDataInputStream) throws IOException {
        super.onReadTransmissionData(pDataInputStream);
        int fleetID = pDataInputStream.readInt();
        float energy = pDataInputStream.readFloat();
        GameObject.Side side = GameObject.Side.values()[pDataInputStream.readInt()];
        int count = pDataInputStream.readInt();
        List<SpaceShip> ships = new ArrayList<>(count);
        for (int i=0; i<count; i++)
            ships.add(new SpaceShip(pDataInputStream.readBoolean()));
        fleetState = new FleetState(fleetID, energy, side, ships);
    }

    @Override
    protected void onWriteTransmissionData(DataOutputStream pDataOutputStream) throws IOException {
        super.onWriteTransmissionData(pDataOutputStream);

        pDataOutputStream.writeInt(fleetState.fleetID);
        pDataOutputStream.writeFloat(fleetState.energy);
        pDataOutputStream.writeInt(fleetState.side.ordinal());
        pDataOutputStream.writeInt(fleetState.ships.size());
        for (SpaceShip ship : fleetState.ships) {
            pDataOutputStream.writeBoolean(ship.getShield());
        }
    }

    @Override
    public short getFlag() {
        return FLEET_STATE_MESSAGE;
    }

    public FleetState getFleetState() {
        return fleetState;
    }
}