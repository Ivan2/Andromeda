package com.games.andromeda.message;

import com.games.andromeda.logic.Fleet;
import com.games.andromeda.logic.GameObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Collection;

public class FleetsCreationMessage extends SetupFleetsMessage implements MessageFlags {

    public FleetsCreationMessage() {}

    public FleetsCreationMessage(GameObject.Side side, Collection<Fleet> fleets) {
        super(side, fleets);
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
        return FLEETS_CREATION_MESSAGE;
    }

}