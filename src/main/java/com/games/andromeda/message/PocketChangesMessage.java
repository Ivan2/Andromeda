package com.games.andromeda.message;

import com.games.andromeda.logic.GameObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PocketChangesMessage extends SideMessage implements MessageFlags {

    private int total;

    public PocketChangesMessage() {
    }

    public PocketChangesMessage(GameObject.Side side, int total) {
        super(side);
        this.total = total;
    }

    @Override
    protected void onReadTransmissionData(DataInputStream pDataInputStream) throws IOException {
        super.onReadTransmissionData(pDataInputStream);
        total = pDataInputStream.readInt();
    }

    @Override
    protected void onWriteTransmissionData(DataOutputStream pDataOutputStream) throws IOException {
        super.onWriteTransmissionData(pDataOutputStream);
        pDataOutputStream.writeInt(total);
    }

    @Override
    public short getFlag() {
        return POCKET_CHANGE_MESSAGE;
    }

    public int getTotal() {
        return total;
    }
}
