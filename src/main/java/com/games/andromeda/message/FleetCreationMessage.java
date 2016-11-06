package com.games.andromeda.message;

import com.games.andromeda.logic.Fleet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;


public class FleetCreationMessage extends FleetDestructionMessage {
    private int shipCount;

    public FleetCreationMessage(){}
    public FleetCreationMessage(Fleet fleet, int number){
        super(fleet, number);
        shipCount = fleet.getShipCount();
    }

    @Override
    protected void onReadTransmissionData(DataInputStream pDataInputStream) throws IOException {
        super.onReadTransmissionData(pDataInputStream);
        shipCount = pDataInputStream.readInt();
    }

    @Override
    protected void onWriteTransmissionData(DataOutputStream pDataOutputStream) throws IOException {
        super.onWriteTransmissionData(pDataOutputStream);
        pDataOutputStream.writeInt(shipCount);
    }

    @Override
    public short getFlag() {
        return 1;
    }

    public int getShipCount() {
        return shipCount;
    }
}