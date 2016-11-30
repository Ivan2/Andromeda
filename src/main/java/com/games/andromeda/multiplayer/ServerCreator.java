package com.games.andromeda.multiplayer;

import android.util.Log;

import com.games.andromeda.MainActivity;
import com.games.andromeda.logic.Fleet;
import com.games.andromeda.logic.GameObject;
import com.games.andromeda.message.BaseCreationMessage;
import com.games.andromeda.message.BaseDestructionMessage;
import com.games.andromeda.message.EndPhaseMessage;
import com.games.andromeda.message.FightMessage;
import com.games.andromeda.message.FleetCreationMessage;
import com.games.andromeda.message.FleetDestructionMessage;
import com.games.andromeda.message.MessageFlags;
import com.games.andromeda.message.MoveShipClientMessage;
import com.games.andromeda.message.MoveShipServerMessage;
import com.games.andromeda.message.PocketChangesMessage;
import com.games.andromeda.message.RandomEventMessage;
import com.games.andromeda.message.SetupBasesMessage;
import com.games.andromeda.message.SetupFleetsMessage;
import com.games.andromeda.message.SideMessage;
import com.games.andromeda.message.StartGameMessage;

import org.andengine.extension.multiplayer.protocol.adt.message.client.IClientMessage;
import org.andengine.extension.multiplayer.protocol.adt.message.server.IServerMessage;
import org.andengine.extension.multiplayer.protocol.client.IServerMessageHandler;
import org.andengine.extension.multiplayer.protocol.client.connector.ServerConnector;
import org.andengine.extension.multiplayer.protocol.server.IClientMessageHandler;
import org.andengine.extension.multiplayer.protocol.server.SocketServer;
import org.andengine.extension.multiplayer.protocol.server.connector.ClientConnector;
import org.andengine.extension.multiplayer.protocol.server.connector.SocketConnectionClientConnector;
import org.andengine.extension.multiplayer.protocol.shared.SocketConnection;
import org.andengine.util.debug.Debug;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

public class ServerCreator implements MessageFlags{

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
                connector.registerClientMessage(FLAG_MESSAGE_CLIENT_SHOW, MoveShipClientMessage.class, new IClientMessageHandler<SocketConnection>() {
                    @Override
                    public void onHandleMessage(ClientConnector<SocketConnection> pClientConnector, IClientMessage pClientMessage) throws IOException {
                        try {
                            MoveShipClientMessage moveShipClientMessage = (MoveShipClientMessage) pClientMessage;
                            MoveShipServerMessage moveShipServerMessage = new MoveShipServerMessage
                                    (moveShipClientMessage.getX(), moveShipClientMessage.getY(),moveShipClientMessage.getNum(),moveShipClientMessage.getSide());
                            //server.sendBroadcastServerMessage(moveShipServerMessage);
                            if (moveShipClientMessage.getSide() == GameObject.Side.EMPIRE)
                                federation.sendServerMessage(moveShipServerMessage);
                            else
                                empire.sendServerMessage(moveShipServerMessage);
                        } catch (IOException e) {
                            Debug.e(e);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                connector.registerClientMessage(END_PHASE_MESSAGE, EndPhaseMessage.class, new IClientMessageHandler<SocketConnection>() {
                    @Override
                    public void onHandleMessage(ClientConnector<SocketConnection> clientConnector, IClientMessage iClientMessage) throws IOException {
                        try {
                            EndPhaseMessage message = (EndPhaseMessage) iClientMessage;

                            if (message.getSide() == GameObject.Side.EMPIRE)
                                federation.sendServerMessage(message);
                            else
                                empire.sendServerMessage(message);
                        } catch (IOException e) {
                            Debug.e(e);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
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
                });
                connector.registerClientMessage(BASE_CREATION_MESSAGE, BaseCreationMessage.class, new IClientMessageHandler<SocketConnection>() {
                    @Override
                    public void onHandleMessage(ClientConnector<SocketConnection> clientConnector, IClientMessage iClientMessage) throws IOException {
                        try {
                            BaseCreationMessage message = (BaseCreationMessage)iClientMessage;
                            if (message.getSide() == GameObject.Side.EMPIRE)
                                federation.sendServerMessage(message);
                            else
                                empire.sendServerMessage(message);
                        } catch (IOException e) {
                            Debug.e(e);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                connector.registerClientMessage(BASE_DESTRUCTION_MESSAGE, BaseDestructionMessage.class, new IClientMessageHandler<SocketConnection>() {
                    @Override
                    public void onHandleMessage(ClientConnector<SocketConnection> clientConnector, IClientMessage iClientMessage) throws IOException {
                        try {
                            BaseDestructionMessage message = (BaseDestructionMessage) iClientMessage;
                            if (message.getSide() == GameObject.Side.EMPIRE)
                                federation.sendServerMessage(message);
                            else
                                empire.sendServerMessage(message);
                        } catch (IOException e) {
                            Debug.e(e);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                connector.registerClientMessage(FLEET_CREATION_MESSAGE, FleetCreationMessage.class, new IClientMessageHandler<SocketConnection>() {
                    @Override
                    public void onHandleMessage(ClientConnector<SocketConnection> clientConnector, IClientMessage iClientMessage) throws IOException {
                        try {
                            FleetCreationMessage message = (FleetCreationMessage) iClientMessage;
                            if (message.getSide() == GameObject.Side.EMPIRE)
                                federation.sendServerMessage(message);
                            else
                                empire.sendServerMessage(message);
                        } catch (IOException e) {
                            Debug.e(e);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                connector.registerClientMessage(FLEET_DESTRUCTION_MESSAGE, FleetDestructionMessage.class, new IClientMessageHandler<SocketConnection>() {
                    @Override
                    public void onHandleMessage(ClientConnector<SocketConnection> clientConnector, IClientMessage iClientMessage) throws IOException {
                        try {
                            FleetDestructionMessage message = (FleetDestructionMessage) iClientMessage;
                            if (message.getSide() == GameObject.Side.EMPIRE)
                                federation.sendServerMessage(message);
                            else
                                empire.sendServerMessage(message);
                        } catch (IOException e) {
                            Debug.e(e);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                connector.registerClientMessage(POCKET_CHANGE_MESSAGE, PocketChangesMessage.class, new IClientMessageHandler<SocketConnection>() {
                    @Override
                    public void onHandleMessage(ClientConnector<SocketConnection> clientConnector, IClientMessage iClientMessage) throws IOException {
                        try {
                            PocketChangesMessage message = (PocketChangesMessage) iClientMessage;
                            if (message.getSide() == GameObject.Side.EMPIRE)
                                federation.sendServerMessage(message);
                            else
                                empire.sendServerMessage(message);
                        } catch (IOException e) {
                            Debug.e(e);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                connector.registerClientMessage(RANDOM_EVENT_MESSAGE, RandomEventMessage.class, new IClientMessageHandler<SocketConnection>() {
                    @Override
                    public void onHandleMessage(ClientConnector<SocketConnection> clientConnector, IClientMessage iClientMessage) throws IOException {
                        try {
                            server.sendBroadcastServerMessage((RandomEventMessage)iClientMessage);
                        } catch (IOException e) {
                            Debug.e(e);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                connector.registerClientMessage(SETUP_BASE_MESSAGE, SetupBasesMessage.class, new IClientMessageHandler<SocketConnection>() {
                    @Override
                    public void onHandleMessage(ClientConnector<SocketConnection> clientConnector, IClientMessage iClientMessage) throws IOException {
                        try {
                            SetupBasesMessage message = (SetupBasesMessage) iClientMessage;
                            if (message.getSide() == GameObject.Side.EMPIRE)
                                federation.sendServerMessage(message);
                            else
                                empire.sendServerMessage(message);
                        } catch (IOException e) {
                            Debug.e(e);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                connector.registerClientMessage(SETUP_FLEET_MESSAGE, SetupFleetsMessage.class, new IClientMessageHandler<SocketConnection>() {
                    @Override
                    public void onHandleMessage(ClientConnector<SocketConnection> clientConnector, IClientMessage iClientMessage) throws IOException {
                        try {
                            SetupFleetsMessage message = (SetupFleetsMessage) iClientMessage;
                            if (message.getSide() == GameObject.Side.EMPIRE)
                                federation.sendServerMessage(message);
                            else
                                empire.sendServerMessage(message);
                        } catch (IOException e) {
                            Debug.e(e);
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
        public void onStarted(ClientConnector<SocketConnection> pConnector) {
            activity.toast("SERVER: Client connected: " + pConnector.getConnection().getSocket().getInetAddress().getHostAddress());
            clients.add(pConnector);
            connectedCount++;
            if (connectedCount == 2) //Поменять на 2 для двух устройств
                try {
                    Random random = new Random();
                    int rand = random.nextInt(2);
                    int i = 0;
                    for (ClientConnector client : clients) {
                        client.sendServerMessage(new StartGameMessage());
                        if (i == rand) {
                            client.sendServerMessage(new SideMessage(GameObject.Side.EMPIRE));
                            empire = client;
                        }
                        else {
                            client.sendServerMessage(new SideMessage(GameObject.Side.FEDERATION));
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
