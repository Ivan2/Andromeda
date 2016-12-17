package com.games.andromeda.message;

import com.games.andromeda.logic.Base;
import com.games.andromeda.logic.GameObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Collection;

public class BasesCreationMessage extends SetupBasesMessage implements MessageFlags {

    public BasesCreationMessage() {}

    public BasesCreationMessage(GameObject.Side side, Collection<Base> bases) {
        super(side, bases);
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
        return BASES_CREATION_MESSAGE;
    }

}