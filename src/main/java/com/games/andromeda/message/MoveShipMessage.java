package com.games.andromeda.message;

import com.games.andromeda.graph.Node;
import com.games.andromeda.logic.GameObject;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

public class MoveShipMessage extends SideNodeListMessage implements MessageFlags{
    private int fleetNumber;

    // for receiving
    public MoveShipMessage(){
    }

    // for sending
    public MoveShipMessage(GameObject.Side side, int fleetNumber, List<Node> path) {
        super(side, path);
        this.fleetNumber = fleetNumber;
    }

    @Override
    protected void onReadTransmissionData(DataInputStream pDataInputStream) throws IOException {
        super.onReadTransmissionData(pDataInputStream);
        this.fleetNumber = pDataInputStream.readInt();
    }

    @Override
    protected void onWriteTransmissionData(DataOutputStream pDataOutputStream) throws IOException {
        super.onWriteTransmissionData(pDataOutputStream);
        pDataOutputStream.writeInt(fleetNumber);
    }

    @Override
    public short getFlag() {
        return MOVE_SHIP_MESSAGE;
    }

    public int getFleetNumber() {
        return fleetNumber;
    }

    public Node getFinish(){
        List<Node> path = getNodeList();
        return path.get(path.size()-1);
    }
}
