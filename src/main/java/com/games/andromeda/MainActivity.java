package com.games.andromeda;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;

import com.games.andromeda.graph.MyGraph;
import com.games.andromeda.graph.Node;
import com.games.andromeda.graph.PathManager;
import com.games.andromeda.layers.AskLayer;
import com.games.andromeda.layers.BackgroundLayer;
import com.games.andromeda.layers.MessageLayer;
import com.games.andromeda.layers.ShipsLayer;
import com.games.andromeda.layers.SystemsLayer;
import com.games.andromeda.level.LevelLoader;
import com.games.andromeda.logic.Base;
import com.games.andromeda.logic.Fleet;
import com.games.andromeda.logic.GameObject;
import com.games.andromeda.logic.Pocket;
import com.games.andromeda.texture.TextureLoader;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.FixedResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.color.Color;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;


import java.net.Socket;
import java.net.UnknownHostException;


import org.andengine.entity.util.FPSLogger;
import org.andengine.extension.multiplayer.protocol.adt.message.IMessage;
import org.andengine.extension.multiplayer.protocol.adt.message.server.IServerMessage;
import org.andengine.extension.multiplayer.protocol.adt.message.server.ServerMessage;
import org.andengine.extension.multiplayer.protocol.adt.message.client.ClientMessage;
import org.andengine.extension.multiplayer.protocol.client.IServerMessageHandler;
import org.andengine.extension.multiplayer.protocol.client.connector.ServerConnector;
import org.andengine.extension.multiplayer.protocol.client.connector.SocketConnectionServerConnector;
import org.andengine.extension.multiplayer.protocol.client.connector.SocketConnectionServerConnector.ISocketConnectionServerConnectorListener;
import org.andengine.extension.multiplayer.protocol.server.SocketServer;
import org.andengine.extension.multiplayer.protocol.server.SocketServer.ISocketServerListener;
import org.andengine.extension.multiplayer.protocol.server.connector.ClientConnector;
import org.andengine.extension.multiplayer.protocol.server.connector.SocketConnectionClientConnector;
import org.andengine.extension.multiplayer.protocol.server.connector.SocketConnectionClientConnector.ISocketConnectionClientConnectorListener;
import org.andengine.extension.multiplayer.protocol.shared.SocketConnection;
import org.andengine.extension.multiplayer.protocol.util.MessagePool;

import org.andengine.util.debug.Debug;
import android.widget.Toast;

public class MainActivity extends SimpleBaseGameActivity{

    private static int CAMERA_WIDTH = 700;
    private static int CAMERA_HEIGHT = 700;

    private Camera camera;
    private TextureLoader textureLoader;

    public static LinkedList<Node> selectedNodes = new LinkedList<>();

    private static final int SERVER_PORT = 4444;
    private static final String LOCALHOST_IP = "127.0.0.1";
    private static final int REQUESTCODE_BLUETOOTH_ENABLE = 0;
    private static final short FLAG_MESSAGE_SERVER_CONNECTION_CLOSE = Short.MIN_VALUE;
    private static final short FLAG_MESSAGE_SERVER_SHOW = 1;

    private static final int DIALOG_CHOOSE_SERVER_OR_CLIENT_ID = 0;
    private static final int DIALOG_ENTER_SERVER_IP_ID = DIALOG_CHOOSE_SERVER_OR_CLIENT_ID + 1;

    private ShipsLayer shipsLayer;
    private SystemsLayer systemsLayer;
    private String mServerIP = LOCALHOST_IP;
    public SocketServer<SocketConnectionClientConnector> mSocketServer;
    private ServerConnector<SocketConnection> mServerConnector;
    public final MessagePool<IMessage> mMessagePool = new MessagePool<IMessage>();



    private void initMessagePool() {
        this.mMessagePool.registerMessage(FLAG_MESSAGE_SERVER_SHOW, AddFaceServerMessage.class);
    }
    @Override
    public void onCreate(final Bundle pSavedInstanceState)
    {
        super.onCreate(pSavedInstanceState);
    }




    private void initServer() {

        this.mSocketServer = new SocketServer<SocketConnectionClientConnector>(SERVER_PORT, new ExampleClientConnectorListener(), new ExampleServerStateListener()) {
            @Override
            protected SocketConnectionClientConnector newClientConnector(final SocketConnection pSocketConnection) throws IOException {
                return new SocketConnectionClientConnector(pSocketConnection);
            }
        };

        this.mSocketServer.start();
    }

    private void initClient() {
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try  {
                    try {
                        MainActivity.this.mServerConnector = new SocketConnectionServerConnector(new SocketConnection(new Socket(MainActivity.this.mServerIP, SERVER_PORT)), new ExampleServerConnectorListener());

                        MainActivity.this.mServerConnector.registerServerMessage(FLAG_MESSAGE_SERVER_CONNECTION_CLOSE, ConnectionCloseServerMessage.class, new IServerMessageHandler<SocketConnection>() {
                            @Override
                            public void onHandleMessage(final ServerConnector<SocketConnection> pServerConnector, final IServerMessage pServerMessage) throws IOException {
                                MainActivity.this.finish();
                            }
                        });

                        MainActivity.this.mServerConnector.registerServerMessage(FLAG_MESSAGE_SERVER_SHOW, AddFaceServerMessage.class, new IServerMessageHandler<SocketConnection>() {
                            @Override
                            public void onHandleMessage(final ServerConnector<SocketConnection> pServerConnector, final IServerMessage pServerMessage) throws IOException {
                                final AddFaceServerMessage addFaceServerMessage = (AddFaceServerMessage)pServerMessage;
                                MainActivity.this.addFace(addFaceServerMessage.x,addFaceServerMessage.y);
                            }
                        });



                        MainActivity.this.mServerConnector.getConnection().start();
                    } catch (final Throwable t) {
                        Debug.e(t);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();

    }

    public void addFace(float x, float y) {
        Node node = new Node(x,y, Node.SystemType.FRIENDLY);
        try {
            shipsLayer.addFleet(new Fleet(GameObject.Side.EMPIRE,5,new Base(GameObject.Side.EMPIRE, node)),1);
        } catch (Exception e){
            Log.wtf("my stupid exception: ", e.toString());
        }
        MainActivity.this.shipsLayer.repaint();
    }

    public static class AddFaceServerMessage extends ServerMessage {
        private float x;
        private float y;

        public AddFaceServerMessage() {

        }

        public AddFaceServerMessage(final float x, final float y) {
            this.x = x;
            this.y = y;
        }

        public void set(final float x, final float y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public short getFlag() {
            return FLAG_MESSAGE_SERVER_SHOW;
        }

        @Override
        protected void onReadTransmissionData(final DataInputStream pDataInputStream) throws IOException {
            x = pDataInputStream.readFloat();
            y = pDataInputStream.readFloat();
        }

        @Override
        protected void onWriteTransmissionData(final DataOutputStream pDataOutputStream) throws IOException {
            pDataOutputStream.writeFloat(x);
            pDataOutputStream.writeFloat(y);
        }
    }
    private void toast(final String pMessage) {
        //this.log(pMessage);
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, pMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
    private class ExampleServerConnectorListener implements ISocketConnectionServerConnectorListener {
        @Override
        public void onStarted(final ServerConnector<SocketConnection> pConnector) {
            MainActivity.this.toast("CLIENT: Connected to server.");
        }

        @Override
        public void onTerminated(final ServerConnector<SocketConnection> pConnector) {
           // MainActivity.this.toast("CLIENT: Disconnected from Server...");
            MainActivity.this.finish();
        }
    }

    private class ExampleServerStateListener implements ISocketServerListener<SocketConnectionClientConnector> {
        @Override
        public void onStarted(final SocketServer<SocketConnectionClientConnector> pSocketServer) {
            MainActivity.this.toast("SERVER: Started.");
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

    private class ExampleClientConnectorListener implements ISocketConnectionClientConnectorListener {
        @Override
        public void onStarted(final ClientConnector<SocketConnection> pConnector) {
            MainActivity.this.toast("SERVER: Client connected: " + pConnector.getConnection().getSocket().getInetAddress().getHostAddress());
        }

        @Override
        public void onTerminated(final ClientConnector<SocketConnection> pConnector) {
           // MainActivity.this.toast("SERVER: Client disconnected: " + pConnector.getConnection().getSocket().getInetAddress().getHostAddress());
        }
    }

    @Override
    public EngineOptions onCreateEngineOptions() {

        if (Main2Activity.getIsServer())
        {
            initServer();
            try{
                Thread.sleep(500);
            }
            catch(InterruptedException e){}
            initClient();
        }
        else {
            mServerIP = Main2Activity.getServerIP();
            initClient();

        }
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        CAMERA_WIDTH = metrics.widthPixels;
        CAMERA_HEIGHT = metrics.heightPixels;

        camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
        return new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED,
                new FixedResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), camera);
    }

    @Override
    protected void onCreateResources() {
        textureLoader = new TextureLoader(this, mEngine);
    }

    @Override
    protected Scene onCreateScene() {
        this.mEngine.registerUpdateHandler(new FPSLogger());
        PxDpConverter.createInstance(this);
        final Scene scene = new Scene();
        scene.setBackground(new Background(Color.BLACK));
        //scene.setTouchAreaBindingOnActionDownEnabled(true);//Без этого не будет работать нормально перетаскивание спрайтов
        scene.setOnAreaTouchTraversalFrontToBack();//Сначала получает фокус верхний спрайт

        MyGraph graph = null;
        try {
            graph = LevelLoader.loadMap(this, "map");
        } catch (IOException e) {
            Log.wtf("loadMap: opening csv error", e.toString());
        } catch (LevelLoader.MapFormatException e) {
            Log.wtf("loadMap: csv content error", e.toString());
        }

        //Слой с фоном и линиями
        BackgroundLayer backgroundLayer = new BackgroundLayer(scene, camera, textureLoader,
                mEngine.getVertexBufferObjectManager(), graph);
        backgroundLayer.repaint();

        //слой с системами
        systemsLayer = new SystemsLayer(scene, camera, textureLoader,
                mEngine.getVertexBufferObjectManager(), graph);
        systemsLayer.repaint();





        ///////////////////////////////////////////////shitcode on
        /// демка корабля

        Iterator<Node> iter = graph.getNodes().iterator();
        iter.next();
        iter.next();
        iter.next();
        Node node = iter.next();
        final PathManager manager = new PathManager();
        shipsLayer = new ShipsLayer(scene, camera, textureLoader, mEngine.getVertexBufferObjectManager(), manager);
        Pocket pocket = new Pocket(GameObject.Side.EMPIRE);
        pocket.increase(100500);
        try {
            Base base = new Base(GameObject.Side.EMPIRE, node);
            Fleet fleet = Fleet.buy(5, base, pocket);
            shipsLayer.addFleet(fleet, 1);
        } catch (Exception e){
            Log.wtf("my stupid exception: ", e.toString());
        }
        shipsLayer.repaint();

        ///////////////////////////////////////////////shitcode off

        //слой с сообщением
        MessageLayer messageLayer = new MessageLayer(scene, camera, textureLoader,
                mEngine.getVertexBufferObjectManager());

        //слой с вопросом
        final AskLayer askLayer = new AskLayer(scene, camera, textureLoader,
                mEngine.getVertexBufferObjectManager()) {
            @Override
            protected void onOk() {

            }

            @Override
            protected void onCancel() {

            }
        };

        systemsLayer.setLayerListener(new SystemsLayer.LayerListener() {
            @Override
            public void onClick(Node node) {
                askLayer.show("fsdfasdfasfsdaf");
            }

            @Override
            public void onMove(Node node) {
                manager.addNode(node);
                try  {
                    float x = node.getX(), y = node.getY();

                    if(MainActivity.this.mSocketServer != null) {
                        try {
                            final AddFaceServerMessage addFaceServerMessage = new AddFaceServerMessage(x,y);
                            MainActivity.this.mSocketServer.sendBroadcastServerMessage(addFaceServerMessage);
                            MainActivity.this.mMessagePool.recycleMessage(addFaceServerMessage);
                        } catch (final IOException e) {

                            Debug.e(e);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onUp(Node node) {

            }
        });


        return scene;
    }

    @Override
    protected void onDestroy() {
        if(this.mSocketServer != null) {
            try {
                this.mSocketServer.sendBroadcastServerMessage(new ConnectionCloseServerMessage());
            } catch (final IOException e) {
                Debug.e(e);
            }
            this.mSocketServer.terminate();
        }

        if(this.mServerConnector != null) {
            this.mServerConnector.terminate();
        }

        super.onDestroy();
    }

}