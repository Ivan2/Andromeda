package com.games.andromeda.message;

import com.games.andromeda.logic.GameObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PocketChangesMessage extends SideMessage implements MessageFlags{
    private int delta;

    public PocketChangesMessage() {
    }

    public PocketChangesMessage(GameObject.Side side, int delta) {
        super(side);
        this.delta = delta;
    }

    @Override
    protected void onReadTransmissionData(DataInputStream pDataInputStream) throws IOException {
        super.onReadTransmissionData(pDataInputStream);
        delta = pDataInputStream.readInt();
    }

    @Override
    protected void onWriteTransmissionData(DataOutputStream pDataOutputStream) throws IOException {
        super.onWriteTransmissionData(pDataOutputStream);
        pDataOutputStream.writeInt(delta);
    }

    @Override
    public short getFlag() {
        return POCKET_CHANGE_MESSAGE;
    }

    public int getDelta() {
        return delta;
    }
}
