package com.games.andromeda.message;

import com.games.andromeda.logic.GameObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class TimeAlmostOverMessage extends SideMessage {

    public TimeAlmostOverMessage() {
    }

    public TimeAlmostOverMessage(GameObject.Side side) {
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
        return TIME_ALMOST_OVER_MESSAGE;
    }

}