package com.games.andromeda;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.games.andromeda.message.MoveShipClientMessage;
import com.games.andromeda.message.MoveShipServerMessage;

import org.andengine.extension.multiplayer.protocol.adt.message.client.IClientMessage;
import org.andengine.extension.multiplayer.protocol.client.connector.ServerConnector;
import org.andengine.extension.multiplayer.protocol.client.connector.SocketConnectionServerConnector;
import org.andengine.extension.multiplayer.protocol.server.IClientMessageHandler;
import org.andengine.extension.multiplayer.protocol.server.SocketServer;
import org.andengine.extension.multiplayer.protocol.server.connector.ClientConnector;
import org.andengine.extension.multiplayer.protocol.server.connector.SocketConnectionClientConnector;
import org.andengine.extension.multiplayer.protocol.shared.SocketConnection;
import org.andengine.util.debug.Debug;

import java.io.IOException;

public class ServerCreator {
    private SocketServer<SocketConnectionClientConnector> server;
    private MainActivity activity;

    public ServerCreator(MainActivity activity) {
        this.activity = activity;
    }

    public SocketServer<SocketConnectionClientConnector> getServer(int port){
        server = new SocketServer<SocketConnectionClientConnector>(port, new ServerCreator.ClientConnectorListener(), new ServerCreator.ServerStateListener()) {
            @Override
            protected SocketConnectionClientConnector newClientConnector(final SocketConnection pSocketConnection) throws IOException {
                SocketConnectionClientConnector connector = new SocketConnectionClientConnector(pSocketConnection);
                connector.registerClientMessage((short)1, MoveShipClientMessage.class, new IClientMessageHandler<SocketConnection>() {
                    @Override
                    public void onHandleMessage(ClientConnector<SocketConnection> pClientConnector, IClientMessage pClientMessage) throws IOException {
                        try  {
                            try {
                                MoveShipClientMessage moveShipClientMessage = (MoveShipClientMessage) pClientMessage;
                                final MoveShipServerMessage moveShipServerMessage = new MoveShipServerMessage(moveShipClientMessage.getX()
                                        ,moveShipClientMessage.getY());
                                server.sendBroadcastServerMessage(moveShipServerMessage);
                            } catch (final IOException e) {

                                Debug.e(e);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                return connector;
            }
        };

        return server;
    }

    private class ServerStateListener implements SocketServer.ISocketServerListener<SocketConnectionClientConnector> {
        @Override
        public void onStarted(final SocketServer<SocketConnectionClientConnector> pSocketServer) {
            activity.toast("SERVER: Started.");
        }

        @Override
        public void onTerminated(final SocketServer<SocketConnectionClientConnector> pSocketServer) {
            //MainActivity.this.toast("SERVER: Terminated.");
        }

        @Override
        public void onException(final SocketServer<SocketConnectionClientConnector> pSocketServer, final Throwable pThrowable) {
            Debug.e(pThrowable);
        }
    }

    private class ClientConnectorListener implements SocketConnectionClientConnector.ISocketConnectionClientConnectorListener {
        @Override
        public void onStarted(final ClientConnector<SocketConnection> pConnector) {
            activity.toast("SERVER: Client connected: " + pConnector.getConnection().getSocket().getInetAddress().getHostAddress());
        }

        @Override
        public void onTerminated(final ClientConnector<SocketConnection> pConnector) {
            // MainActivity.this.toast("SERVER: Client disconnected: " + pConnector.getConnection().getSocket().getInetAddress().getHostAddress());
        }

        public void onMoveShipClientMessage(final ClientConnector<SocketConnection> pConnector)
        {
            Log.wtf("Word 1","Word 2");
        }
    }
}
