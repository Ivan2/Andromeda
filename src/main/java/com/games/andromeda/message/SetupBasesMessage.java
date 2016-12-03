package com.games.andromeda.message;

import android.util.Log;

import com.games.andromeda.logic.Base;
import com.games.andromeda.logic.GameObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;

public class SetupBasesMessage extends SideMessage {

    private Collection<Base> bases;

    public SetupBasesMessage() {
    }

    public SetupBasesMessage(GameObject.Side side, Collection<Base> bases) {
        super(side);
        this.bases = bases;
    }

    @Override
    protected void onReadTransmissionData(DataInputStream pDataInputStream) throws IOException {
        super.onReadTransmissionData(pDataInputStream);
        int count = pDataInputStream.readInt();
        bases = new LinkedList<>();
        for (int i=0; i<count; ++i){
            int nodeID = pDataInputStream.readInt();
            GameObject.Side side = getSide();
            try {
                bases.add(new Base(side, nodeID));
            } catch (Base.IncorrectNodeException e) {
                Log.wtf("error", e.toString());
            }
        }
    }

    @Override
    protected void onWriteTransmissionData(DataOutputStream pDataOutputStream) throws IOException {
        super.onWriteTransmissionData(pDataOutputStream);
        pDataOutputStream.writeInt(bases.size());
        for (Base base: bases) {
            pDataOutputStream.writeInt(base.getNodeID());
        }
    }

    @Override
    public short getFlag() {
        return SETUP_BASE_MESSAGE;
    }

    public Collection<Base> getBases() {
        return bases;
    }
}
