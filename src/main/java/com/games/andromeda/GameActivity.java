package com.games.andromeda;

import android.util.DisplayMetrics;
import android.util.Log;

import com.games.andromeda.graph.Node;
import com.games.andromeda.level.LevelLoader;
import com.games.andromeda.logic.Base;
import com.games.andromeda.logic.Fleet;
import com.games.andromeda.logic.GameObject;
import com.games.andromeda.logic.Pocket;
import com.games.andromeda.logic.WorldAccessor;
import com.games.andromeda.multiplayer.Client;
import com.games.andromeda.ui.UI;
import com.games.andromeda.ui.layers.ShipsLayer;
import com.games.andromeda.ui.texture.TextureLoader;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.FixedResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.ui.activity.SimpleBaseGameActivity;

import java.io.IOException;
import java.util.Iterator;

public class GameActivity extends SimpleBaseGameActivity{

    private static int CAMERA_WIDTH = 700;
    private static int CAMERA_HEIGHT = 700;

    public static int SCREEN_WIDTH;
    public static int SCREEN_HEIGHT;

    private Camera camera;
    private TextureLoader textureLoader;

    private Client client;

    @Override
    public EngineOptions onCreateEngineOptions() {

        PxDpConverter.createInstance(this);
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

    @Override
    protected Scene onCreateScene() {
        //this.mEngine.registerUpdateHandler(new FPSLogger());
        PxDpConverter.createInstance(this);
        Scene scene = new Scene();

        try {
            WorldAccessor.init(LevelLoader.loadMap(this, "map"));
        } catch (IOException e) {
            Log.wtf("loadMap: opening csv error", e.toString());
        } catch (LevelLoader.MapFormatException e) {
            Log.wtf("loadMap: csv content error", e.toString());
        }

        WorldAccessor world = WorldAccessor.getInstance();

        ShipsLayer.IOnFleetMove onFleetMove = new ShipsLayer.IOnFleetMove() {
            @Override
            public void onFleetMove(Fleet fleet,int num) {
                client.sendMoveShipMessage(fleet,num);
                //////////
                client.sendEndPhaseMessage();
            }
        };
        ShipsLayer.IOnFleetFight onFleetFight = new ShipsLayer.IOnFleetFight() {
            @Override
            public void onFleetFight(Fleet attackingFleet,  Fleet anotherFleet, int number, int secondNumber) {
                client.sendFightMessage(attackingFleet,anotherFleet,number,secondNumber);
            }
        };


        UI ui = new UI(this, scene, camera, textureLoader, mEngine.getVertexBufferObjectManager(),
                world, onFleetMove,onFleetFight);
        client = new Client(ui);


        //generate bases and fleets
        Pocket pocket = world.getPocket(GameObject.Side.EMPIRE);
        pocket.increase(100500);

        Iterator<Node> iter = world.getMap().getNodes().iterator();
        iter.next();
        iter.next();
        iter.next();
        Node node = iter.next();
        try {
            Base base = new Base(GameObject.Side.EMPIRE, node);
            world.setBase(base);
            world.setFleet(Fleet.buy(5, base, pocket), 1);
        } catch (Exception e) {
            Log.wtf("my stupid exception: ", e.toString());
        }

        iter.next();
        iter.next();
        node = iter.next();

        Pocket pocket2 = world.getPocket(GameObject.Side.FEDERATION);
        pocket2.increase(150000);
        try {
            Base base = new Base(GameObject.Side.FEDERATION, node);
            world.setBase(base);
            world.setFleet(Fleet.buy(8, base, pocket2), 2);
        } catch (Exception e) {
            Log.wtf("my stupid exception: ", e.toString());
        }

        ui.repaintHUD();

        return scene;
    }

}