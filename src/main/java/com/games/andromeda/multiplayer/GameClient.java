package com.games.andromeda.multiplayer;


import android.content.Intent;

import com.games.andromeda.GameActivity;
import com.games.andromeda.MainActivity;
import com.games.andromeda.level.LevelLoader;
import com.games.andromeda.logic.WorldAccessor;
import com.games.andromeda.message.BaseDestructionMessage;
import com.games.andromeda.message.BasesCreationMessage;
import com.games.andromeda.message.ConnectionCloseServerMessage;
import com.games.andromeda.message.EndGameLooseMessage;
import com.games.andromeda.message.EndPhaseMessage;
import com.games.andromeda.message.FleetStateMessage;
import com.games.andromeda.message.FleetsCreationMessage;
import com.games.andromeda.message.MessageFlags;
import com.games.andromeda.message.MoveFleetMessage;
import com.games.andromeda.message.PocketChangesMessage;
import com.games.andromeda.message.RandomEventMessage;
import com.games.andromeda.message.SetupBasesMessage;
import com.games.andromeda.message.SetupFleetsMessage;
import com.games.andromeda.message.StartGameMessage;
import com.games.andromeda.message.TimeAlmostOverMessage;
import com.games.andromeda.message.TimeOverMessage;
import com.games.andromeda.message.EndGameMessage;
import com.games.andromeda.ui.UI;

import org.andengine.extension.multiplayer.protocol.adt.message.client.IClientMessage;
import org.andengine.extension.multiplayer.protocol.adt.message.server.IServerMessage;
import org.andengine.extension.multiplayer.protocol.client.IServerMessageHandler;
import org.andengine.extension.multiplayer.protocol.client.connector.ServerConnector;
import org.andengine.extension.multiplayer.protocol.client.connector.SocketConnectionServerConnector;
import org.andengine.extension.multiplayer.protocol.shared.SocketConnection;
import org.andengine.util.debug.Debug;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

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
                    if (UI.getInstance().activity!=null)
                        UI.getInstance().finishGame();
                }
            });

            connector.registerServerMessage(START_GAME_MESSAGE, StartGameMessage.class, new IServerMessageHandler<SocketConnection>() {
                @Override
                public void onHandleMessage(ServerConnector<SocketConnection> serverConnector, IServerMessage iServerMessage) throws IOException {
                    if (messageReceiver == null)
                        Client.getInstance();
                    messageReceiver.onMessageReceive(START_GAME_MESSAGE, iServerMessage);
                    WorldAccessor.init(LevelLoader.loadMap((StartGameMessage)iServerMessage));

                    Intent start = new Intent(activity, GameActivity.class);
                    //activity.startActivity(start);
                    activity.startActivityForResult(start,1);
                }
            });

            for(Map.Entry<Short, Class<?extends IServerMessage>> entry:
                    new HashMap<Short, Class<?extends IServerMessage>>(){{
                put(SETUP_BASE_MESSAGE, SetupBasesMessage.class);
                put(SETUP_FLEET_MESSAGE, SetupFleetsMessage.class);
                put(RANDOM_EVENT_MESSAGE, RandomEventMessage.class);
                put(POCKET_CHANGE_MESSAGE, PocketChangesMessage.class);
                put(BASES_CREATION_MESSAGE, BasesCreationMessage.class);
                put(FLEETS_CREATION_MESSAGE, FleetsCreationMessage.class);
                put(MOVE_FLEET_MESSAGE, MoveFleetMessage.class);
                put(FLEET_STATE_MESSAGE, FleetStateMessage.class);
                put(END_PHASE_MESSAGE, EndPhaseMessage.class);
                put(END_GAME_MESSAGE, EndGameMessage.class);
                put(BASE_DESTRUCTION_MESSAGE, BaseDestructionMessage.class);
                put(END_GAME_LOOSE_MESSAGE,EndGameLooseMessage.class);
            }}.entrySet()){
                registerEchoMessage(entry.getKey(), entry.getValue());
            }

            connector.registerServerMessage(TIME_OVER_MESSAGE, TimeOverMessage.class, new IServerMessageHandler<SocketConnection>() {
                @Override
                public void onHandleMessage(ServerConnector<SocketConnection> serverConnector, IServerMessage iServerMessage) throws IOException {
                    //TODO
                    //UI.toast("Время закончилось");
                }
            });
            connector.registerServerMessage(TIME_ALMOST_OVER_MESSAGE, TimeAlmostOverMessage.class, new IServerMessageHandler<SocketConnection>() {
                @Override
                public void onHandleMessage(ServerConnector<SocketConnection> serverConnector, IServerMessage iServerMessage) throws IOException {
                    //TODO
                    //UI.toast("У вас осталось 10 секунд");
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

    private void registerEchoMessage(final short flag, final Class<? extends IServerMessage> messageClass){
        connector.registerServerMessage(flag, messageClass, new IServerMessageHandler<SocketConnection>() {
            @Override
            public void onHandleMessage(ServerConnector<SocketConnection> serverConnector, IServerMessage iServerMessage) throws IOException {
                if (messageReceiver != null)
                    messageReceiver.onMessageReceive(flag, iServerMessage);
            }
        });
    }

    private class ServerConnectorListener implements SocketConnectionServerConnector.ISocketConnectionServerConnectorListener {
        @Override
        public void onStarted(final ServerConnector<SocketConnection> pConnector) {
//
        }

        @Override
        public void onTerminated(final ServerConnector<SocketConnection> pConnector) {
            if (UI.getInstance().activity!=null)
                UI.getInstance().finishGame();
        }



    }
    public void dispose()
    {
        connector.terminate();
        instance = null;
    }

}
