package com.games.andromeda.multiplayer;


import android.content.Intent;
import android.widget.Toast;

import com.games.andromeda.GameActivity;
import com.games.andromeda.MainActivity;
import com.games.andromeda.level.LevelLoader;
import com.games.andromeda.logic.WorldAccessor;
import com.games.andromeda.message.ConnectionCloseServerMessage;
import com.games.andromeda.message.MessageFlags;
import com.games.andromeda.message.SetupBasesMessage;
import com.games.andromeda.message.SetupFleetsMessage;
import com.games.andromeda.message.StartGameMessage;

import org.andengine.extension.multiplayer.protocol.adt.message.client.IClientMessage;
import org.andengine.extension.multiplayer.protocol.adt.message.server.IServerMessage;
import org.andengine.extension.multiplayer.protocol.client.IServerMessageHandler;
import org.andengine.extension.multiplayer.protocol.client.connector.ServerConnector;
import org.andengine.extension.multiplayer.protocol.client.connector.SocketConnectionServerConnector;
import org.andengine.extension.multiplayer.protocol.shared.SocketConnection;
import org.andengine.util.debug.Debug;

import java.io.IOException;
import java.net.Socket;

public class GameClient implements Runnable,MessageFlags {

    private static GameClient instance;

    public static GameClient createInstance(MainActivity activity, String ip, int port) {
        instance = new GameClient(activity, ip, port);
        return instance;
    }

    public static GameClient getInstance() {
        return instance;
    }

    public interface MessageReceiver {
        void onMessageReceive(short flag, IServerMessage message);
    }

    //private static final short FLAG_MESSAGE_SERVER_CONNECTION_CLOSE = Short.MIN_VALUE;
    //private static final short FLAG_MESSAGE_SERVER_SHOW = 1;
    private ServerConnector<SocketConnection> connector;
    private int serverPort;
    private String serverIP;
    private MainActivity activity;
    private MessageReceiver messageReceiver;

    private GameClient(MainActivity activity, String ip, int port) {
        this.serverPort = port;
        this.serverIP = ip;
        this.activity = activity;
    }

    public ServerConnector<SocketConnection> getConnector() {
        return connector;
    }

    @Override
    public void run() {
        try {
            connector = new SocketConnectionServerConnector(new SocketConnection
                    (new Socket(serverIP, serverPort)), new ServerConnectorListener());

            connector.registerServerMessage(FLAG_MESSAGE_SERVER_CONNECTION_CLOSE,
                    ConnectionCloseServerMessage.class, new IServerMessageHandler<SocketConnection>() {
                @Override
                public void onHandleMessage(ServerConnector<SocketConnection> pServerConnector,
                                            IServerMessage pServerMessage) throws IOException {
                    activity.finish();
                }
            });

            /*connector.registerServerMessage(FLAG_MESSAGE_SERVER_SHOW, MoveShipServerMessage.class,
                    new IServerMessageHandler<SocketConnection>() {
                @Override
                public void onHandleMessage(ServerConnector<SocketConnection> pServerConnector,
                                            IServerMessage pServerMessage) throws IOException {
                    if (messageReceiver != null)
                        messageReceiver.onMessageReceive(FLAG_MESSAGE_SERVER_SHOW, pServerMessage);
                    //MoveShipServerMessage moveShipServerMessage = (MoveShipServerMessage) pServerMessage;
                    //activity.MoveShip(moveShipServerMessage.getX(), moveShipServerMessage.getY());
                }
            });

            connector.registerServerMessage(MessageFlags.START_GAME_MESSAGE, StartGameMessage.class,
                    new IServerMessageHandler<SocketConnection>() {
                        @Override
                        public void onHandleMessage(ServerConnector<SocketConnection> pServerConnector,
                                                    IServerMessage pServerMessage) throws IOException {
                        }
                    });
            connector.registerServerMessage(END_PHASE_MESSAGE, EndPhaseMessage.class, new IServerMessageHandler<SocketConnection>() {
                @Override
                public void onHandleMessage(ServerConnector<SocketConnection> serverConnector, IServerMessage iServerMessage) throws IOException {
                    if (messageReceiver != null)
                        messageReceiver.onMessageReceive(END_PHASE_MESSAGE, iServerMessage);
                }
            });
            connector.registerServerMessage(FIGHT_MESSAGE, FightMessage.class, new IServerMessageHandler<SocketConnection>() {
                @Override
                public void onHandleMessage(ServerConnector<SocketConnection> serverConnector, IServerMessage iServerMessage) throws IOException {
                    if (messageReceiver != null)
                        messageReceiver.onMessageReceive(FIGHT_MESSAGE, iServerMessage);
                }
            });*/
            connector.registerServerMessage(START_GAME_MESSAGE, StartGameMessage.class, new IServerMessageHandler<SocketConnection>() {
                @Override
                public void onHandleMessage(ServerConnector<SocketConnection> serverConnector, IServerMessage iServerMessage) throws IOException {
                    if (messageReceiver == null)
                        Client.getInstance();
                    messageReceiver.onMessageReceive(START_GAME_MESSAGE, iServerMessage);
                    WorldAccessor.init(LevelLoader.loadMap((StartGameMessage)iServerMessage));

                    Intent start = new Intent(activity, GameActivity.class);
                    activity.startActivity(start);
                }
            });
            /*connector.registerServerMessage(BASE_CREATION_MESSAGE, BaseCreationMessage.class, new IServerMessageHandler<SocketConnection>() {
                @Override
                public void onHandleMessage(ServerConnector<SocketConnection> serverConnector, IServerMessage iServerMessage) throws IOException {
                    if (messageReceiver != null)
                        messageReceiver.onMessageReceive(BASE_CREATION_MESSAGE, iServerMessage);
                }
            });
            connector.registerServerMessage(BASE_DESTRUCTION_MESSAGE, BaseDestructionMessage.class, new IServerMessageHandler<SocketConnection>() {
                @Override
                public void onHandleMessage(ServerConnector<SocketConnection> serverConnector, IServerMessage iServerMessage) throws IOException {
                    if (messageReceiver != null)
                        messageReceiver.onMessageReceive(BASE_DESTRUCTION_MESSAGE, iServerMessage);
                }
            });
            connector.registerServerMessage(FLEET_CREATION_MESSAGE, FleetCreationMessage.class, new IServerMessageHandler<SocketConnection>() {
                @Override
                public void onHandleMessage(ServerConnector<SocketConnection> serverConnector, IServerMessage iServerMessage) throws IOException {
                    if (messageReceiver != null)
                        messageReceiver.onMessageReceive(FLEET_CREATION_MESSAGE, iServerMessage);
                }
            });
            connector.registerServerMessage(FLEET_DESTRUCTION_MESSAGE, FleetDestructionMessage.class, new IServerMessageHandler<SocketConnection>() {
                @Override
                public void onHandleMessage(ServerConnector<SocketConnection> serverConnector, IServerMessage iServerMessage) throws IOException {
                    if (messageReceiver != null)
                        messageReceiver.onMessageReceive(FLEET_DESTRUCTION_MESSAGE, iServerMessage);
                }
            });
            connector.registerServerMessage(POCKET_CHANGE_MESSAGE, PocketChangesMessage.class, new IServerMessageHandler<SocketConnection>() {
                @Override
                public void onHandleMessage(ServerConnector<SocketConnection> serverConnector, IServerMessage iServerMessage) throws IOException {
                    if (messageReceiver != null)
                        messageReceiver.onMessageReceive(POCKET_CHANGE_MESSAGE, iServerMessage);
                }
            });
            connector.registerServerMessage(RANDOM_EVENT_MESSAGE, RandomEventMessage.class, new IServerMessageHandler<SocketConnection>() {
                @Override
                public void onHandleMessage(ServerConnector<SocketConnection> serverConnector, IServerMessage iServerMessage) throws IOException {
                    if (messageReceiver != null)
                        messageReceiver.onMessageReceive(RANDOM_EVENT_MESSAGE, iServerMessage);
                }
            });*/
            connector.registerServerMessage(SETUP_BASE_MESSAGE, SetupBasesMessage.class, new IServerMessageHandler<SocketConnection>() {
                @Override
                public void onHandleMessage(ServerConnector<SocketConnection> serverConnector, IServerMessage iServerMessage) throws IOException {
                    if (messageReceiver != null)
                        messageReceiver.onMessageReceive(SETUP_BASE_MESSAGE, iServerMessage);
                }
            });
            connector.registerServerMessage(SETUP_FLEET_MESSAGE, SetupFleetsMessage.class, new IServerMessageHandler<SocketConnection>() {
                @Override
                public void onHandleMessage(ServerConnector<SocketConnection> serverConnector, IServerMessage iServerMessage) throws IOException {
                    if (messageReceiver != null)
                        messageReceiver.onMessageReceive(SETUP_FLEET_MESSAGE, iServerMessage);
                }
            });
            connector.getConnection().start();
        } catch (Throwable t) {
            Debug.e(t);
        }
    }

    public void setMessageReceiver(MessageReceiver messageReceiver) {
        this.messageReceiver = messageReceiver;
    }

    public void sendMessage(IClientMessage message) throws IOException {
        connector.sendClientMessage(message);
    }

    private class ServerConnectorListener implements SocketConnectionServerConnector.ISocketConnectionServerConnectorListener {
        @Override
        public void onStarted(final ServerConnector<SocketConnection> pConnector) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(activity, "CLIENT: Connected to server.", Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void onTerminated(final ServerConnector<SocketConnection> pConnector) {
            // MainActivity.this.toast("CLIENT: Disconnected from Server...");
            activity.finish();
        }

    }

}
