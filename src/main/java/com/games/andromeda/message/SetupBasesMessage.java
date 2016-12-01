package com.games.andromeda.message;

import android.util.Log;

import com.games.andromeda.graph.Node;
import com.games.andromeda.logic.Base;
import com.games.andromeda.logic.GameObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class SetupBasesMessage extends SideNodeListMessage{

    private Collection<Base> bases;

    public SetupBasesMessage() {
    }

    public SetupBasesMessage(GameObject.Side side, List<Node> nodes, Collection<Base> bases) {
        super(side, nodes);
        this.bases = bases;
    }

    @Override
    protected void onReadTransmissionData(DataInputStream pDataInputStream) throws IOException {
        super.onReadTransmissionData(pDataInputStream);
        int count = pDataInputStream.readInt();
        bases = new LinkedList<>();
        for (int i=0; i<count; ++i){
            float x = pDataInputStream.readFloat();
            float y = pDataInputStream.readFloat();
            GameObject.Side side = getSide();
            try {
                bases.add(new Base(side, new Node(x, y)));
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
            pDataOutputStream.writeFloat(base.getNode().getX());
            pDataOutputStream.writeFloat(base.getNode().getY());
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
