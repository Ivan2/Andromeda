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
import org.andengine.extension.multiplayer.protocol.util.WifiUtils;

import org.andengine.util.debug.Debug;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends SimpleBaseGameActivity{

    private static int CAMERA_WIDTH = 700;
    private static int CAMERA_HEIGHT = 700;

    private Camera camera;
    private TextureLoader textureLoader;

    public static LinkedList<Node> selectedNodes = new LinkedList<>();

    //private BluetoothAdapter mBluetoothAdapter;
    private static final int SERVER_PORT = 4444;
    private static final String LOCALHOST_IP = "127.0.0.1";
    private static final String EXAMPLE_UUID = "6D2DF50E-06EF-C21C-7DB0-345099A5F64E";
    //private static final int DIALOG_CHOOSE_SERVER_OR_CLIENT_ID = 0;
    private static final int REQUESTCODE_BLUETOOTH_ENABLE = 0;
    //private static final int DIALOG_SHOW_SERVER_IP_ID = DIALOG_CHOOSE_SERVER_OR_CLIENT_ID + 1;
    private static final int REQUESTCODE_BLUETOOTH_CONNECT = REQUESTCODE_BLUETOOTH_ENABLE + 1;
    private static final short FLAG_MESSAGE_SERVER_CONNECTION_CLOSE = Short.MIN_VALUE;
    private static final short FLAG_MESSAGE_SERVER_SHOW = 1;

    private static final int DIALOG_CHOOSE_SERVER_OR_CLIENT_ID = 0;
    private static final int DIALOG_ENTER_SERVER_IP_ID = DIALOG_CHOOSE_SERVER_OR_CLIENT_ID + 1;
    private static final int DIALOG_SHOW_SERVER_IP_ID = DIALOG_ENTER_SERVER_IP_ID + 1;

    private ShipsLayer shipsLayer;
    private SystemsLayer systemsLayer;
    private String mServerMACAddress;
    private String mServerIP = LOCALHOST_IP;
    public SocketServer<SocketConnectionClientConnector> mSocketServer;
    //private BluetoothSocketServer<BluetoothSocketConnectionClientConnector> mBluetoothSocketServer;
    //private ServerConnector<BluetoothSocketConnection> mServerConnector;
    private ServerConnector<SocketConnection> mServerConnector;
    public final MessagePool<IMessage> mMessagePool = new MessagePool<IMessage>();



    private void initMessagePool() {
        this.mMessagePool.registerMessage(FLAG_MESSAGE_SERVER_SHOW, AddFaceServerMessage.class);
        //his.mMessagePool.registerMessage(FLAG_MESSAGE_SERVER_MOVE_FACE, MoveFaceServerMessage.class);
    }
    @Override
    public void onCreate(final Bundle pSavedInstanceState)
    {
        super.onCreate(pSavedInstanceState);
       // StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        //StrictMode.setThreadPolicy(policy);
    }

    @Override
    protected Dialog onCreateDialog(final int pID) {
        switch(pID) {
            case DIALOG_SHOW_SERVER_IP_ID:
                try {
                    return new AlertDialog.Builder(this)
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .setTitle("Your Server-IP ...")
                            .setCancelable(false)
                            .setMessage("The IP of your Server is:\n" + WifiUtils.getWifiIPv4Address(this))
                            .setPositiveButton(android.R.string.ok, null)
                            .create();
                } catch (final UnknownHostException e) {
                    return new AlertDialog.Builder(this)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle("Your Server-IP ...")
                            .setCancelable(false)
                            .setMessage("Error retrieving IP of your Server: " + e)
                            .setPositiveButton(android.R.string.ok, new OnClickListener() {
                                @Override
                                public void onClick(final DialogInterface pDialog, final int pWhich) {
                                    MainActivity.this.finish();
                                }
                            })
                            .create();
                }
            case DIALOG_ENTER_SERVER_IP_ID:
                final EditText ipEditText = new EditText(this);
                return new AlertDialog.Builder(this)
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setTitle("Enter Server-IP ...")
                        .setCancelable(false)
                        .setView(ipEditText)
                        .setPositiveButton("Connect", new OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface pDialog, final int pWhich) {
                                MainActivity.this.mServerIP = ipEditText.getText().toString();
                                MainActivity.this.initClient();
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, new OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface pDialog, final int pWhich) {
                                MainActivity.this.finish();
                            }
                        })
                        .create();
            case DIALOG_CHOOSE_SERVER_OR_CLIENT_ID:
                return new AlertDialog.Builder(this)
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setTitle("Be Server or Client ...")
                        .setCancelable(false)
                        .setPositiveButton("Client", new OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface pDialog, final int pWhich) {
                                MainActivity.this.showDialog(DIALOG_ENTER_SERVER_IP_ID);
                            }
                        })
                        .setNeutralButton("Server", new OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface pDialog, final int pWhich) {
                                MainActivity.this.toast("You can add and move sprites, which are only shown on the clients.");
                                MainActivity.this.initServer();
                                try {
                                    Thread.sleep(500);
                                } catch (final Throwable t) {
                                    Debug.e(t);
                                }

                                MainActivity.this.initClient();
                                MainActivity.this.showDialog(DIALOG_SHOW_SERVER_IP_ID);
                            }
                        })
                        .create();
            default:
                return super.onCreateDialog(pID);
        }
    }
    /*@Override
    protected void onActivityResult(final int pRequestCode, final int pResultCode, final Intent pData) {
        switch(pRequestCode) {
            case REQUESTCODE_BLUETOOTH_ENABLE:
                this.showDialog(DIALOG_CHOOSE_SERVER_OR_CLIENT_ID);
                break;
            case REQUESTCODE_BLUETOOTH_CONNECT:
                this.mServerMACAddress = pData.getExtras().getString(BluetoothListDevicesActivity.EXTRA_DEVICE_ADDRESS);
                this.initClient();
                break;
            default:
                super.onActivityResult(pRequestCode, pResultCode, pData);
        }
    }*/

    /*private void initServer() {
        this.mServerMACAddress = BluetoothAdapter.getDefaultAdapter().getAddress();
        try {
            this.mBluetoothSocketServer = new BluetoothSocketServer<BluetoothSocketConnectionClientConnector>(EXAMPLE_UUID, new ClientConnectorListener(), new ServerStateListener()) {
                @Override
                protected BluetoothSocketConnectionClientConnector newClientConnector(final BluetoothSocketConnection pBluetoothSocketConnection) throws IOException {
                    try {
                        return new BluetoothSocketConnectionClientConnector(pBluetoothSocketConnection);
                    } catch (final BluetoothException e) {
                        Debug.e(e);
                        return null;
                    }
                }
            };
        } catch (final BluetoothException e) {
            Debug.e(e);
        }

        this.mBluetoothSocketServer.start();
    }

    private void initClient() {
        try {
            this.mServerConnector = new BluetoothSocketConnectionServerConnector(new BluetoothSocketConnection(this.mBluetoothAdapter, this.mServerMACAddress, EXAMPLE_UUID), new ServerConnectorListener());

            this.mServerConnector.registerServerMessage(FLAG_MESSAGE_SERVER_CONNECTION_CLOSE, ConnectionCloseServerMessage.class, new IServerMessageHandler<BluetoothSocketConnection>() {
                @Override
                public void onHandleMessage(final ServerConnector<BluetoothSocketConnection> pServerConnector, final IServerMessage pServerMessage) throws IOException {
                    MainActivity.this.finish();
                }
            });

            this.mServerConnector.registerServerMessage(FLAG_MESSAGE_SERVER_SHOW, AddFaceServerMessage.class, new IServerMessageHandler<BluetoothSocketConnection>() {
                @Override
                public void onHandleMessage(final ServerConnector<BluetoothSocketConnection> pServerConnector, final IServerMessage pServerMessage) throws IOException {
                    final AddFaceServerMessage addFaceServerMessage = (AddFaceServerMessage)pServerMessage;
                    MainActivity.this.addFace(addFaceServerMessage.S);
                }
            });

            this.mServerConnector.registerServerMessage(FLAG_MESSAGE_SERVER_MOVE_FACE, MoveFaceServerMessage.class, new IServerMessageHandler<BluetoothSocketConnection>() {
                @Override
                public void onHandleMessage(final ServerConnector<BluetoothSocketConnection> pServerConnector, final IServerMessage pServerMessage) throws IOException {
                    final MoveFaceServerMessage moveFaceServerMessage = (MoveFaceServerMessage)pServerMessage;
                    MainActivity.this.moveFace(moveFaceServerMessage.mID, moveFaceServerMessage.mX, moveFaceServerMessage.mY);
                }
            });

            this.mServerConnector.getConnection().start();
        } catch (final Throwable t) {
            Debug.e(t);
        }
    }*/

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
        final Scene scene = this.mEngine.getScene();

        /*final Sprite face = new Sprite(0, 0, this.mFaceTextureRegion, this.getVertexBufferObjectManager());
        face.setPosition(pX - face.getWidth() * 0.5f, pY - face.getHeight() * 0.5f);
        face.setUserData(pID);
        this.mFaces.put(pID, face);
        scene.registerTouchArea(face);
        scene.attachChild(face);*/
        final PathManager manager = new PathManager();
       // MainActivity.this.toast(x + " "  + y);
        Node node = new Node(x,y, Node.SystemType.FRIENDLY);
       // ShipsLayer shipsLayer = new ShipsLayer(scene, camera, textureLoader, mEngine.getVertexBufferObjectManager(), manager);
       // Pocket pocket = new Pocket(GameObject.Side.EMPIRE);
        //pocket.increase(100500);

        try {

            //Fleet fleet = Fleet.buy(5, base, pocket);
            //shipsLayer.addFleet(fleet, 1);

            shipsLayer.addFleet(new Fleet(GameObject.Side.EMPIRE,5,new Base(GameObject.Side.EMPIRE, node)),1);
        } catch (Exception e){
            Log.wtf("my stupid exception: ", e.toString());
        }
        MainActivity.this.shipsLayer = shipsLayer;
        MainActivity.this.shipsLayer.repaint();
        //scene.registerTouchArea();
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
            MainActivity.this.toast("CLIENT: Disconnected from Server...");
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
            MainActivity.this.toast("SERVER: Terminated.");
        }

        @Override
        public void onException(final SocketServer<SocketConnectionClientConnector> pSocketServer, final Throwable pThrowable) {
            Debug.e(pThrowable);
            MainActivity.this.toast("SERVER: Exception: " + pThrowable);
        }
    }

    private class ExampleClientConnectorListener implements ISocketConnectionClientConnectorListener {
        @Override
        public void onStarted(final ClientConnector<SocketConnection> pConnector) {
            MainActivity.this.toast("SERVER: Client connected: " + pConnector.getConnection().getSocket().getInetAddress().getHostAddress());
        }

        @Override
        public void onTerminated(final ClientConnector<SocketConnection> pConnector) {
            MainActivity.this.toast("SERVER: Client disconnected: " + pConnector.getConnection().getSocket().getInetAddress().getHostAddress());
        }
    }
    /*private class ServerConnectorListener implements IBluetoothSocketConnectionServerConnectorListener {
        @Override
        public void onStarted(final ServerConnector<BluetoothSocketConnection> pConnector) {
            MainActivity.this.toast("CLIENT: Connected to server.");
        }

        @Override
        public void onTerminated(final ServerConnector<BluetoothSocketConnection> pConnector) {
            MainActivity.this.toast("CLIENT: Disconnected from Server...");
            MainActivity.this.finish();
        }
    }

    private class ServerStateListener implements IBluetoothSocketServerListener<BluetoothSocketConnectionClientConnector> {
        @Override
        public void onStarted(final BluetoothSocketServer<BluetoothSocketConnectionClientConnector> pBluetoothSocketServer) {
            MainActivity.this.toast("SERVER: Started.");
        }

        @Override
        public void onTerminated(final BluetoothSocketServer<BluetoothSocketConnectionClientConnector> pBluetoothSocketServer) {
            MainActivity.this.toast("SERVER: Terminated.");
        }

        @Override
        public void onException(final BluetoothSocketServer<BluetoothSocketConnectionClientConnector> pBluetoothSocketServer, final Throwable pThrowable) {
            Debug.e(pThrowable);
            MainActivity.this.toast("SERVER: Exception: " + pThrowable);
        }
    }

    private class ClientConnectorListener implements IBluetoothSocketConnectionClientConnectorListener {
        @Override
        public void onStarted(final ClientConnector<BluetoothSocketConnection> pConnector) {
            MainActivity.this.toast("SERVER: Client connected: " + pConnector.getConnection().getBluetoothSocket().getRemoteDevice().getAddress());
        }

        @Override
        public void onTerminated(final ClientConnector<BluetoothSocketConnection> pConnector) {
            MainActivity.this.toast("SERVER: Client disconnected: " + pConnector.getConnection().getBluetoothSocket().getRemoteDevice().getAddress());
        }
    }*/

    @Override
    public EngineOptions onCreateEngineOptions() {
        this.showDialog(DIALOG_CHOOSE_SERVER_OR_CLIENT_ID);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        CAMERA_WIDTH = metrics.widthPixels;//Получаем ширину экрана устройства
        CAMERA_HEIGHT = metrics.heightPixels;//Получаем высоту экрана устройства

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
                //Thread thread = new Thread(new Runnable() {

                    //@Override
                    //public void run() {
                        try  {
                            float x = node.getX(), y = node.getY();

                            if(MainActivity.this.mSocketServer != null) {
                                //MainActivity.this.toast("Smth unexpected happened ");
                                try {
                                    final AddFaceServerMessage addFaceServerMessage = new AddFaceServerMessage(x,y);
                                    //MainActivity.this.toast("Smth unexpected happened ");




                                    //addFaceServerMessage.set(15);

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
               // });

                //thread.start();



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