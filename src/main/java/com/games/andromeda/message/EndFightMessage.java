package com.games.andromeda.message;

import com.games.andromeda.logic.GameObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class EndFightMessage extends SideMessage implements MessageFlags {

    public EndFightMessage() {}

    public EndFightMessage(GameObject.Side side) {
        super(side);
    }

    @Override
    protected void onReadTransmissionData(DataInputStream pDataInputStream) throws IOException {
        super.onReadTransmissionData(pDataInputStream);
    }

    @Override
    protected void onWriteTransmissionData(DataOutputStream pDataOutputStream) throws IOException {
        super.onWriteTransmissionData(pDataOutputStream);
    }

    @Override
    public short getFlag() {
        return END_FIGHT_MESSAGE;
    }

}