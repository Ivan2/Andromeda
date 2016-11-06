package com.games.andromeda;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import com.games.andromeda.graph.MyGraph;
import com.games.andromeda.graph.Node;
import com.games.andromeda.graph.PathManager;
import com.games.andromeda.hud.PanelHUD;
import com.games.andromeda.layers.BackgroundLayer;
import com.games.andromeda.layers.MessageLayer;
import com.games.andromeda.layers.ShipsLayer;
import com.games.andromeda.layers.SystemInfoLayer;
import com.games.andromeda.layers.SystemsLayer;
import com.games.andromeda.level.LevelLoader;
import com.games.andromeda.logic.Base;
import com.games.andromeda.logic.Fleet;
import com.games.andromeda.logic.GameObject;
import com.games.andromeda.logic.Pocket;
import com.games.andromeda.logic.WorldAccessor;
import com.games.andromeda.message.ConnectionCloseServerMessage;
import com.games.andromeda.message.MoveShipServerMessage;
import com.games.andromeda.texture.TextureLoader;
import com.games.andromeda.threads.GameClient;
import com.games.andromeda.threads.GameTimer;
import com.games.andromeda.threads.Scrolling;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.FixedResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.util.FPSLogger;
import org.andengine.extension.multiplayer.protocol.adt.message.IMessage;
import org.andengine.extension.multiplayer.protocol.server.SocketServer;
import org.andengine.extension.multiplayer.protocol.server.connector.ClientConnector;
import org.andengine.extension.multiplayer.protocol.server.connector.SocketConnectionClientConnector;
import org.andengine.extension.multiplayer.protocol.shared.SocketConnection;
import org.andengine.extension.multiplayer.protocol.util.MessagePool;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.color.Color;
import org.andengine.util.debug.Debug;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;

public class MainActivity extends SimpleBaseGameActivity{

    private static int CAMERA_WIDTH = 700;
    private static int CAMERA_HEIGHT = 700;

    public static int SCREEN_WIDTH;
    public static int SCREEN_HEIGHT;

    private Camera camera;
    private TextureLoader textureLoader;

    public static LinkedList<Node> selectedNodes = new LinkedList<>();

    private static final int SERVER_PORT = 4444;
    private static final String LOCALHOST_IP = "127.0.0.1";
    private static final short FLAG_MESSAGE_SERVER_SHOW = 1;

    private static final int DIALOG_CHOOSE_SERVER_OR_CLIENT_ID = 0;
    private static final int DIALOG_ENTER_SERVER_IP_ID = DIALOG_CHOOSE_SERVER_OR_CLIENT_ID + 1;

    private ShipsLayer shipsLayer;
    private SystemsLayer systemsLayer;
    private String mServerIP = LOCALHOST_IP;
    private SocketServer<SocketConnectionClientConnector> mSocketServer;
    private GameClient client;
    private final MessagePool<IMessage> mMessagePool = new MessagePool<IMessage>();
    private ClientConnector<SocketConnection> clientConnector;


    private void initMessagePool() {
        this.mMessagePool.registerMessage(FLAG_MESSAGE_SERVER_SHOW, MoveShipServerMessage.class);
    }
    @Override
    public void onCreate(final Bundle pSavedInstanceState)
    {
        super.onCreate(pSavedInstanceState);
    }


    private void initServer() {
        ServerCreator creator = new ServerCreator(this);
        this.mSocketServer = creator.getServer(SERVER_PORT);
        this.mSocketServer.start();
    }

    private void initClient() {
        this.client = new GameClient(this, mServerIP, SERVER_PORT);
        Thread thread = new Thread(client);
        thread.start();

    }

    public void MoveShip(float x, float y) {
        Node node = new Node(x, y);
        try {
            WorldAccessor.getInstance().moveFleet(GameObject.Side.EMPIRE, 1, node);
        } catch (Exception e){
            Log.wtf("my stupid exception: ", e.toString());
        }
    }

    @Override
    public EngineOptions onCreateEngineOptions() {
        PxDpConverter.createInstance(this);

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
        SCREEN_WIDTH = metrics.widthPixels; //Получаем ширину экрана устройства
        SCREEN_HEIGHT = metrics.heightPixels; //Получаем высоту экрана устройства

        CAMERA_WIDTH = (int)PxDpConverter.dpToPx(1800);
        CAMERA_HEIGHT = (int)PxDpConverter.dpToPx(1800);

        camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
        return new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED,
                new FixedResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), camera);
    }

    @Override
    protected void onCreateResources() {
        textureLoader = new TextureLoader(this, mEngine);
    }
    Fleet fleet;
    @Override
    protected Scene onCreateScene() {
        this.mEngine.registerUpdateHandler(new FPSLogger());
        PxDpConverter.createInstance(this);
        final Scene scene = new Scene();
        scene.setBackground(new Background(Color.BLACK));
        //scene.setTouchAreaBindingOnActionDownEnabled(true);//Без этого не будет работать нормально перетаскивание спрайтов
        scene.setOnAreaTouchTraversalFrontToBack();//Сначала получает фокус верхний спрайт

        try {
            WorldAccessor.init(LevelLoader.loadMap(this, "map"));
        } catch (IOException e) {
            Log.wtf("loadMap: opening csv error", e.toString());
        } catch (LevelLoader.MapFormatException e) {
            Log.wtf("loadMap: csv content error", e.toString());
        }

        WorldAccessor world = WorldAccessor.getInstance();


        //Слой с фоном и линиями
        BackgroundLayer backgroundLayer = new BackgroundLayer(scene, camera, textureLoader,
                mEngine.getVertexBufferObjectManager(), world.getMap());
        backgroundLayer.repaint();

        //слой с системами
        SystemsLayer systemsLayer = new SystemsLayer(scene, camera, textureLoader,
                mEngine.getVertexBufferObjectManager(), world.getMap());
        systemsLayer.repaint();





        ///////////////////////////////////////////////shitcode on
        /// демка корабля

        Iterator<Node> iter = world.getMap().getNodes().iterator();
        iter.next();
        iter.next();
        iter.next();
        Node node = iter.next();
        final PathManager manager = new PathManager();
        shipsLayer = new ShipsLayer(scene, camera, textureLoader,
                mEngine.getVertexBufferObjectManager(), manager, client);
        world.addFleetObserver(shipsLayer);
        Pocket pocket = world.getPocket(GameObject.Side.EMPIRE);
        pocket.increase(100500);
        try {
            Base base = new Base(GameObject.Side.EMPIRE, node);
            world.setBase(base);
            world.setFleet(Fleet.buy(5, base, pocket), 1);
        } catch (Exception e){
            Log.wtf("my stupid exception: ", e.toString());
        }
        shipsLayer.repaint();

        ///////////////////////////////////////////////shitcode off

        //слой с сообщением
        MessageLayer messageLayer = new MessageLayer(scene, camera, textureLoader,
                mEngine.getVertexBufferObjectManager());

        //слой с вопросом
        final SystemInfoLayer systemInfoLayer = new SystemInfoLayer(scene, camera, textureLoader,
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
                systemInfoLayer.show(node, fleet);
            }

            @Override
            public void onMove(Node node) {
                manager.addNode(node);
            }

            @Override
            public void onUp(Node node) {

            }
        });

        final PanelHUD panel = new PanelHUD(camera, textureLoader, mEngine.getVertexBufferObjectManager());
        Thread timeThread = new Thread(new GameTimer(this, panel));
        timeThread.setDaemon(true);
        timeThread.start();

        Scrolling scrolling = new Scrolling(camera, SCREEN_WIDTH, SCREEN_HEIGHT,
                CAMERA_WIDTH/2, CAMERA_HEIGHT/2);
        scene.setOnSceneTouchListener(scrolling.getListener());

        Thread thread = new Thread(scrolling);
        thread.setDaemon(true);
        thread.start();

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

        if(this.client.getConnector() != null) {
            this.client.getConnector().terminate();
        }

        super.onDestroy();
    }


    public void toast(final String pMessage) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, pMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public GameClient getClient(){
        return client;
    }

}