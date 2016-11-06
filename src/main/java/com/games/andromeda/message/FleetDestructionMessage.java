package com.games.andromeda.message;

import com.games.andromeda.logic.Fleet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class FleetDestructionMessage extends SideNodeMessage {
    protected int number;

    public FleetDestructionMessage(){}
    public FleetDestructionMessage(Fleet fleet, int number){
        super(fleet.getSide(), fleet.getPosition());
        this.number = number;
    }

    @Override
    protected void onReadTransmissionData(DataInputStream pDataInputStream) throws IOException {
        super.onReadTransmissionData(pDataInputStream);
        number = pDataInputStream.readInt();

    }

    @Override
    protected void onWriteTransmissionData(DataOutputStream pDataOutputStream) throws IOException {
        super.onWriteTransmissionData(pDataOutputStream);
        pDataOutputStream.writeInt(number);
    }

    @Override
    public short getFlag() {
        return 1;
    }

    public int getNumber() {
        return number;
    }
}