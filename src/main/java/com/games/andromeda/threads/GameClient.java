package com.games.andromeda.threads;


import android.util.Log;

import com.games.andromeda.MainActivity;
import com.games.andromeda.message.ConnectionCloseServerMessage;
import com.games.andromeda.message.MoveShipServerMessage;

import org.andengine.extension.multiplayer.protocol.adt.message.server.IServerMessage;
import org.andengine.extension.multiplayer.protocol.client.IServerMessageHandler;
import org.andengine.extension.multiplayer.protocol.client.connector.ServerConnector;
import org.andengine.extension.multiplayer.protocol.client.connector.SocketConnectionServerConnector;
import org.andengine.extension.multiplayer.protocol.server.connector.ClientConnector;
import org.andengine.extension.multiplayer.protocol.shared.SocketConnection;
import org.andengine.util.debug.Debug;

import java.io.IOException;
import java.net.Socket;

public class GameClient implements Runnable {
    private static final short FLAG_MESSAGE_SERVER_CONNECTION_CLOSE = Short.MIN_VALUE;
    private static final short FLAG_MESSAGE_SERVER_SHOW = 1;
    private ServerConnector<SocketConnection> connector;
    private int serverPort;
    private String serverIP;
    private MainActivity activity;

    public GameClient(MainActivity activity, String ip, int port) {
        this.serverPort = port;
        this.serverIP = ip;
        this.activity = activity;
    }

    public ServerConnector<SocketConnection> getConnector() {
        return connector;
    }

    @Override
    public void run() {
        try  {
            try {
                connector = new SocketConnectionServerConnector(new SocketConnection(new Socket(serverIP, serverPort)), new ServerConnectorListener());

                connector.registerServerMessage(FLAG_MESSAGE_SERVER_CONNECTION_CLOSE, ConnectionCloseServerMessage.class, new IServerMessageHandler<SocketConnection>() {
                    @Override
                    public void onHandleMessage(final ServerConnector<SocketConnection> pServerConnector, final IServerMessage pServerMessage) throws IOException {
                        activity.finish();
                    }
                });

                connector.registerServerMessage(FLAG_MESSAGE_SERVER_SHOW, MoveShipServerMessage.class, new IServerMessageHandler<SocketConnection>() {
                    @Override
                    public void onHandleMessage(final ServerConnector<SocketConnection> pServerConnector, final IServerMessage pServerMessage) throws IOException {
                        final MoveShipServerMessage moveShipServerMessage = (MoveShipServerMessage) pServerMessage;
                        activity.MoveShip(moveShipServerMessage.getX(),moveShipServerMessage.getY());
                    }
                });
                connector.getConnection().start();
            } catch (final Throwable t) {
                Debug.e(t);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class ServerConnectorListener implements SocketConnectionServerConnector.ISocketConnectionServerConnectorListener {
        @Override
        public void onStarted(final ServerConnector<SocketConnection> pConnector) {
            activity.toast("CLIENT: Connected to server.");
        }

        @Override
        public void onTerminated(final ServerConnector<SocketConnection> pConnector) {
            // MainActivity.this.toast("CLIENT: Disconnected from Server...");
            activity.finish();
        }

        public void onMoveShipClientMessage(final ClientConnector<SocketConnection> pConnector)
        {
            Log.wtf("Word 1","Word 2");
        }
    }
}
