package com.games.andromeda.message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.andengine.extension.multiplayer.protocol.adt.message.server.ServerMessage;


public class ConnectionCloseServerMessage extends ServerMessage implements MessageFlags {

	public ConnectionCloseServerMessage() {

	}

	@Override
	public short getFlag() {
		return FLAG_MESSAGE_SERVER_CONNECTION_CLOSE;
	}

	@Override
	protected void onReadTransmissionData(final DataInputStream pDataInputStream) throws IOException {
	}

	@Override
	protected void onWriteTransmissionData(final DataOutputStream pDataOutputStream) throws IOException {
	}

}
