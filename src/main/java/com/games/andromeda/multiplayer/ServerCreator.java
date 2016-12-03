package com.games.andromeda.multiplayer;

import android.util.Log;

import com.games.andromeda.MainActivity;
import com.games.andromeda.graph.MyGraph;
import com.games.andromeda.level.LevelLoader;
import com.games.andromeda.logic.GameObject;
import com.games.andromeda.message.BasesCreationMessage;
import com.games.andromeda.message.EndFightMessage;
import com.games.andromeda.message.FleetsCreationMessage;
import com.games.andromeda.message.MessageFlags;
import com.games.andromeda.message.MoveFleetMessage;
import com.games.andromeda.message.PocketChangesMessage;
import com.games.andromeda.message.RandomEventMessage;
import com.games.andromeda.message.SetupBasesMessage;
import com.games.andromeda.message.SetupFleetsMessage;
import com.games.andromeda.message.SideMessage;
import com.games.andromeda.message.StartGameMessage;

import org.andengine.extension.multiplayer.protocol.adt.message.client.IClientMessage;
import org.andengine.extension.multiplayer.protocol.server.IClientMessageHandler;
import org.andengine.extension.multiplayer.protocol.server.SocketServer;
import org.andengine.extension.multiplayer.protocol.server.connector.ClientConnector;
import org.andengine.extension.multiplayer.protocol.server.connector.SocketConnectionClientConnector;
import org.andengine.extension.multiplayer.protocol.shared.SocketConnection;
import org.andengine.util.debug.Debug;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;
import java.util.Set;

public class ServerCreator implements MessageFlags {

    private SocketServer<SocketConnectionClientConnector> server;
    private MainActivity activity;
    private int connectedCount = 0;
    private ClientConnector empire;
    private ClientConnector federation;
    Set<ClientConnector> clients = new HashSet<>();

    public ServerCreator(MainActivity activity) {
        this.activity = activity;
    }

    public SocketServer<SocketConnectionClientConnector> getServer(int port){
        server = new SocketServer<SocketConnectionClientConnector>(port,
                new ClientConnectorListener(), new ServerStateListener()) {
            @Override
            protected SocketConnectionClientConnector newClientConnector(final SocketConnection pSocketConnection) throws IOException {
                SocketConnectionClientConnector connector = new SocketConnectionClientConnector(pSocketConnection);

                /*
                connector.registerClientMessage(FIGHT_MESSAGE, FightMessage.class, new IClientMessageHandler<SocketConnection>() {
                    @Override
                    public void onHandleMessage(ClientConnector<SocketConnection> clientConnector, IClientMessage iClientMessage) throws IOException {
                        try {
                            FightMessage fightMessage = (FightMessage) iClientMessage;
                            //Fleet   first = fightMessage.getFleet1(),
                            //        second = fightMessage.getFleet2();
                            //first.attack(second);
                           // FightMessage serverFightMessage = new FightMessage(fightMessage.getSide(),
                           //         first,second,fightMessage.getNumber1(),fightMessage.getNumber2());
                            if (fightMessage.getSide() == GameObject.Side.EMPIRE) {
                                federation.sendServerMessage(fightMessage);
                            }
                            else {
                                empire.sendServerMessage(fightMessage);
                            }
                        } catch (IOException e) {
                            Debug.e(e);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });*/
                connector.registerClientMessage(SETUP_BASE_MESSAGE, SetupBasesMessage.class, new IClientMessageHandler<SocketConnection>() {
                    @Override
                    public void onHandleMessage(ClientConnector<SocketConnection> clientConnector, IClientMessage iClientMessage) throws IOException {
                        sendMessageToEnemy(iClientMessage);
                    }
                });
                connector.registerClientMessage(SETUP_FLEET_MESSAGE, SetupFleetsMessage.class, new IClientMessageHandler<SocketConnection>() {
                    @Override
                    public void onHandleMessage(ClientConnector<SocketConnection> clientConnector, IClientMessage iClientMessage) throws IOException {
                        sendMessageToEnemy(iClientMessage);
                    }
                });
                connector.registerClientMessage(RANDOM_EVENT_MESSAGE, RandomEventMessage.class, new IClientMessageHandler<SocketConnection>() {
                    @Override
                    public void onHandleMessage(ClientConnector<SocketConnection> clientConnector, IClientMessage iClientMessage) throws IOException {
                        sendMessageToEnemy(iClientMessage);
                    }
                });
                connector.registerClientMessage(POCKET_CHANGE_MESSAGE, PocketChangesMessage.class, new IClientMessageHandler<SocketConnection>() {
                    @Override
                    public void onHandleMessage(ClientConnector<SocketConnection> clientConnector, IClientMessage iClientMessage) throws IOException {
                        sendMessageToEnemy(iClientMessage);
                    }
                });
                connector.registerClientMessage(BASES_CREATION_MESSAGE, BasesCreationMessage.class, new IClientMessageHandler<SocketConnection>() {
                    @Override
                    public void onHandleMessage(ClientConnector<SocketConnection> clientConnector, IClientMessage iClientMessage) throws IOException {
                        sendMessageToEnemy(iClientMessage);
                    }
                });
                connector.registerClientMessage(FLEETS_CREATION_MESSAGE, FleetsCreationMessage.class, new IClientMessageHandler<SocketConnection>() {
                    @Override
                    public void onHandleMessage(ClientConnector<SocketConnection> clientConnector, IClientMessage iClientMessage) throws IOException {
                        sendMessageToEnemy(iClientMessage);
                    }
                });
                connector.registerClientMessage(MOVE_FLEET_MESSAGE, MoveFleetMessage.class, new IClientMessageHandler<SocketConnection>() {
                    @Override
                    public void onHandleMessage(ClientConnector<SocketConnection> clientConnector, IClientMessage iClientMessage) throws IOException {
                        sendMessageToEnemy(iClientMessage);
                    }
                });
                connector.registerClientMessage(END_FIGHT_MESSAGE, EndFightMessage.class, new IClientMessageHandler<SocketConnection>() {
                    @Override
                    public void onHandleMessage(ClientConnector<SocketConnection> clientConnector, IClientMessage iClientMessage) throws IOException {
                        sendMessageToEnemy(iClientMessage);
                    }
                });
                return connector;
            }
        };

        return server;
    }

    private void sendMessageToEnemy(IClientMessage message) {
        try {
            SideMessage sideMessage = (SideMessage) message;
            if (sideMessage.getSide() == GameObject.Side.EMPIRE)
                federation.sendServerMessage(sideMessage);
            else
                empire.sendServerMessage(sideMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        public void onStarted(ClientConnector<SocketConnection> pConnector) {
            activity.toast("SERVER: Client connected: " + pConnector.getConnection().getSocket().getInetAddress().getHostAddress());
            clients.add(pConnector);
            connectedCount++;
            if (connectedCount == 2) //Поменять на 2 для двух устройств
                try {
                    MyGraph graph = null;
                    try {
                        graph = LevelLoader.loadMap(activity, "map");
                    } catch (IOException e) {
                        Log.wtf("loadMap: opening csv error", e.toString());
                    } catch (LevelLoader.MapFormatException e) {
                        Log.wtf("loadMap: csv content error", e.toString());
                    }

                    Random random = new Random();
                    int rand = random.nextInt(2);
                    int i = 0;
                    for (ClientConnector client : clients) {
                        if (i == rand) {
                            client.sendServerMessage(new StartGameMessage(GameObject.Side.EMPIRE,
                                    new LinkedList<>(graph.getNodes()),
                                    new LinkedList<>(graph.getEdges())));
                            empire = client;
                        } else {
                            client.sendServerMessage(new StartGameMessage(GameObject.Side.FEDERATION,
                                    new LinkedList<>(graph.getNodes()),
                                    new LinkedList<>(graph.getEdges())));
                            federation = client;
                        }
                        i++;
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
        }

        @Override
        public void onTerminated(final ClientConnector<SocketConnection> pConnector) {
            // MainActivity.this.toast("SERVER: Client disconnected: " + pConnector.getConnection().getSocket().getInetAddress().getHostAddress());
        }

    }
}
