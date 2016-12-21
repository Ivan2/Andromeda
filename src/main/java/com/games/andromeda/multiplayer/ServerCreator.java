package com.games.andromeda.multiplayer;

import android.util.Log;

import com.games.andromeda.MainActivity;
import com.games.andromeda.graph.LevelGraph;
import com.games.andromeda.level.LevelLoader;
import com.games.andromeda.logic.GameObject;
import com.games.andromeda.message.BaseDestructionMessage;
import com.games.andromeda.message.BasesCreationMessage;
import com.games.andromeda.message.EndGameLooseMessage;
import com.games.andromeda.message.EndGameMessage;
import com.games.andromeda.message.EndPhaseMessage;
import com.games.andromeda.message.FleetStateMessage;
import com.games.andromeda.message.FleetsCreationMessage;
import com.games.andromeda.message.MessageFlags;
import com.games.andromeda.message.MoveFleetMessage;
import com.games.andromeda.message.PocketChangesMessage;
import com.games.andromeda.message.RandomEventMessage;
import com.games.andromeda.message.SetupBasesMessage;
import com.games.andromeda.message.SetupFleetsMessage;
import com.games.andromeda.message.SideMessage;
import com.games.andromeda.message.StartGameMessage;
import com.games.andromeda.message.TimeAlmostOverMessage;
import com.games.andromeda.message.TimeOverMessage;
import com.games.andromeda.threads.ServerTimeThread;

import org.andengine.extension.multiplayer.protocol.adt.message.client.IClientMessage;
import org.andengine.extension.multiplayer.protocol.server.IClientMessageHandler;
import org.andengine.extension.multiplayer.protocol.server.SocketServer;
import org.andengine.extension.multiplayer.protocol.server.connector.ClientConnector;
import org.andengine.extension.multiplayer.protocol.server.connector.SocketConnectionClientConnector;
import org.andengine.extension.multiplayer.protocol.shared.SocketConnection;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

public class ServerCreator implements MessageFlags {

    private SocketServer<SocketConnectionClientConnector> server;
    private MainActivity activity;
    private int connectedCount = 0;
    private ClientConnector empire;
    private ClientConnector federation;
    Set<ClientConnector> clients = new HashSet<>();

    private ServerTimeThread timeThread;

    public ServerCreator(MainActivity activity) {
        this.activity = activity;
        timeThread = new ServerTimeThread() {
            @Override
            public void onAlmostTime(GameObject.Side side) {
                sendMessage(new TimeAlmostOverMessage(side));
            }

            @Override
            public void onTime(GameObject.Side side) {

                sendMessage(new TimeOverMessage(side));
            }
        };
    }

    public SocketServer<SocketConnectionClientConnector> getServer(int port){
        server = new SocketServer<SocketConnectionClientConnector>(port,
                new ClientConnectorListener(), new ServerStateListener()) {
            @Override
            protected SocketConnectionClientConnector newClientConnector(final SocketConnection pSocketConnection) throws IOException {
                SocketConnectionClientConnector connector = new SocketConnectionClientConnector(pSocketConnection);

                registerMessages(connector,
                        // к сожалению, getFlag не является статическим методом
                        new HashMap<Short, Class<? extends IClientMessage>>(){{
                            put(SETUP_BASE_MESSAGE,         SetupBasesMessage.class);
                            put(SETUP_FLEET_MESSAGE,        SetupFleetsMessage.class);
                            put(RANDOM_EVENT_MESSAGE,       RandomEventMessage.class);
                            put(POCKET_CHANGE_MESSAGE,      PocketChangesMessage.class);
                            put(BASES_CREATION_MESSAGE,     BasesCreationMessage.class);
                            put(FLEETS_CREATION_MESSAGE,    FleetsCreationMessage.class);
                            put(FLEETS_CREATION_MESSAGE,    FleetsCreationMessage.class);
                            put(MOVE_FLEET_MESSAGE,         MoveFleetMessage.class);
                            put(FLEET_STATE_MESSAGE,        FleetStateMessage.class);
                            put(END_PHASE_MESSAGE,          EndPhaseMessage.class);
                            put(END_GAME_MESSAGE,           EndGameMessage.class);
                            put(BASE_DESTRUCTION_MESSAGE,   BaseDestructionMessage.class);
                            put(END_GAME_LOOSE_MESSAGE,     EndGameLooseMessage.class);
                        }},
                        new HashMap<Short, Integer>(){{
                            put(SETUP_BASE_MESSAGE, 40);
                            put(SETUP_FLEET_MESSAGE, 40);
                            put(POCKET_CHANGE_MESSAGE, 40); //TODO POCKET_CHANGE_MESSAGE отправляется в двух фазах (для одной из них timeout не нужен). Разбить сообщение на два
                            put(MOVE_FLEET_MESSAGE, 40);
                        }}
                );
                return connector;
            }
        };

        return server;
    }

    private void registerMessages(SocketConnectionClientConnector connector,
                                  Map<Short, Class<? extends IClientMessage>> classes,
                                  final Map<Short, Integer> timeouts){
        for(final Map.Entry<Short, Class<? extends IClientMessage>> entry: classes.entrySet()){
            connector.registerClientMessage(entry.getKey(), entry.getValue(), new IClientMessageHandler<SocketConnection>() {
                @Override
                public void onHandleMessage(ClientConnector<SocketConnection> clientConnector, IClientMessage iClientMessage) throws IOException {
                    if (timeouts.containsKey(entry.getKey())){
                        timeThread.restart(timeouts.get(entry.getKey()),
                                ((SideMessage)iClientMessage).getSide());
                    }
                    sendMessageToEnemy(iClientMessage);
                }
            });
        }
    }

    private void sendMessage(IClientMessage message) {
        try {
            SideMessage sideMessage = (SideMessage) message;
            if (sideMessage.getSide() == GameObject.Side.FEDERATION)
                federation.sendServerMessage(sideMessage);
            else
                empire.sendServerMessage(sideMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            Log.wtf("toast", "SERVER: Started.");
        }

        @Override
        public void onTerminated(final SocketServer<SocketConnectionClientConnector> pSocketServer) {
            //MainActivity.this.toast("SERVER: Terminated.");
        }

        @Override
        public void onException(final SocketServer<SocketConnectionClientConnector> pSocketServer, final Throwable pThrowable) {
        }
    }

    private class ClientConnectorListener implements SocketConnectionClientConnector.ISocketConnectionClientConnectorListener {
        @Override
        public void onStarted(ClientConnector<SocketConnection> pConnector) {
            Log.wtf("toast", "SERVER: Client connected: " + pConnector.getConnection().getSocket().getInetAddress().getHostAddress());
            clients.add(pConnector);
            connectedCount++;
            if (connectedCount == 1)
            {
                Thread th  = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            String adr = null;
                            MulticastSocket ds = new MulticastSocket(9999);
                            Enumeration e = NetworkInterface.getNetworkInterfaces();
                            boolean f = true;
                            while (e.hasMoreElements() && f) {
                                NetworkInterface n = (NetworkInterface) e.nextElement();
                                Enumeration ee = n.getInetAddresses();
                                while (ee.hasMoreElements()) {
                                    InetAddress i = (InetAddress) ee.nextElement();
                                        String addr = i.getHostAddress().toString();
                                        if (addr.length() < 8) continue;
                                        if (addr.substring(0, 7).equals("192.168")) {
                                            adr = addr;
                                            f = false;
                                            break;
                                        }
                                }
                            }

                            StringBuilder sb = new StringBuilder();
                            Scanner scaner = new Scanner(adr);
                            scaner.useDelimiter("\\.");
                            sb.append(scaner.next() + ".");
                            sb.append(scaner.next() + ".");
                            sb.append(scaner.next() + ".");
                            sb.append(255);
                            byte[] buffer = new byte[1024];
                            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(sb.toString()), 9999);
                            while ( connectedCount == 1) {
                                try {
                                    Thread.sleep(1000);
                                    for (int i=0; i<10; i++)
                                        ds.send(packet);
                                }
                                catch (Exception ex)
                                {
                                    Log.wtf("Error ",""  +ex.toString());
                                }
                            }
                            ds.close();
                        } catch (Exception e)
                        {
                            Log.wtf("Error : ",""  +e.toString());
                        }
                    }
                });
                th.start();
            }

            if (connectedCount == 2) //Поменять на 2 для двух устройств
                try {
                    LevelGraph graph = null;
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
        }

    }
}
