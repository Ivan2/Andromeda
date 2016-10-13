package com.games.andromeda;

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
import org.andengine.entity.Entity;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.input.touch.TouchEvent;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.color.Color;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;

public class MainActivity extends SimpleBaseGameActivity {

    private static int CAMERA_WIDTH = 700;
    private static int CAMERA_HEIGHT = 700;

    private static int SCREEN_WIDTH;
    private static int SCREEN_HEIGHT;

    private Camera camera;
    private TextureLoader textureLoader;

    public static LinkedList<Node> selectedNodes = new LinkedList<>();

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
        SystemsLayer systemsLayer = new SystemsLayer(scene, camera, textureLoader,
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
        final ShipsLayer shipsLayer = new ShipsLayer(scene, camera, textureLoader, mEngine.getVertexBufferObjectManager(), manager);
        Pocket pocket = new Pocket(GameObject.Side.EMPIRE);
        pocket.increase(100500);
        try {
            Base base = new Base(GameObject.Side.EMPIRE, node);
            Fleet fleet = Fleet.buy(5, base, pocket);
            fleet.setEnergy(100);
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
            }

            @Override
            public void onUp(Node node) {

            }
        });


        scrollEntity = new Entity(CAMERA_WIDTH/2, CAMERA_HEIGHT/2);
        camera.setChaseEntity(scrollEntity);


        scene.setOnSceneTouchListener(new IOnSceneTouchListener() {
            @Override
            public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
                if (pSceneTouchEvent.getMotionEvent().getRawX() < PxDpConverter.dpToPx(100) ||
                        pSceneTouchEvent.getMotionEvent().getRawX() > SCREEN_WIDTH-PxDpConverter.dpToPx(100))
                    mTouchX = (pSceneTouchEvent.getMotionEvent().getRawX()-SCREEN_WIDTH/2)/(SCREEN_WIDTH/2);
                else
                    mTouchX = 0;

                if (pSceneTouchEvent.getMotionEvent().getRawY() < PxDpConverter.dpToPx(100) ||
                        pSceneTouchEvent.getMotionEvent().getRawY() > SCREEN_HEIGHT-PxDpConverter.dpToPx(100))
                    mTouchY = (pSceneTouchEvent.getMotionEvent().getRawY()-SCREEN_HEIGHT/2)/(SCREEN_HEIGHT/2);
                else
                    mTouchY = 0;

                if (pSceneTouchEvent.isActionMove() && pSceneTouchEvent.getMotionEvent().getHistorySize() > 0) {
                    float dx = pSceneTouchEvent.getMotionEvent().getX()-
                            pSceneTouchEvent.getMotionEvent().getHistoricalX(0);
                    float dy = pSceneTouchEvent.getMotionEvent().getY()-
                            pSceneTouchEvent.getMotionEvent().getHistoricalY(0);

                    float x = scrollEntity.getX();
                    float y = scrollEntity.getY();
                    if (shipsLayer.isShipMoves())
                        scrollEntity.setPosition(x+dx, y+dy);
                    else
                        scrollEntity.setPosition(x-dx, y-dy);

                    if (scrollEntity.getX()+SCREEN_WIDTH/2 > camera.getWidth())
                        scrollEntity.setX(camera.getWidth()-SCREEN_WIDTH/2);
                    if (scrollEntity.getY()+SCREEN_HEIGHT/2 > camera.getHeight())
                        scrollEntity.setY(camera.getHeight()-SCREEN_HEIGHT/2);
                    if (scrollEntity.getX()-SCREEN_WIDTH/2 < 0)
                        scrollEntity.setX(SCREEN_WIDTH/2);
                    if (scrollEntity.getY()-SCREEN_HEIGHT/2 < 0)
                        scrollEntity.setY(SCREEN_HEIGHT/2);
                }

                return true;
            }
        });

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                    }

                    if (shipsLayer.isShipMoves()) {
                        float x = scrollEntity.getX();
                        float y = scrollEntity.getY();

                        float dx = shipsLayer.getPos().x - scrollEntity.getX();
                        float dy = shipsLayer.getPos().y - scrollEntity.getY();

                        scrollEntity.setPosition(x + mTouchX * PxDpConverter.dpToPx(10),
                                y + mTouchY * PxDpConverter.dpToPx(10));

                        if (scrollEntity.getX()+SCREEN_WIDTH/2 > camera.getWidth())
                            scrollEntity.setX(camera.getWidth()-SCREEN_WIDTH/2);
                        if (scrollEntity.getY()+SCREEN_HEIGHT/2 > camera.getHeight())
                            scrollEntity.setY(camera.getHeight()-SCREEN_HEIGHT/2);
                        if (scrollEntity.getX()-SCREEN_WIDTH/2 < 0)
                            scrollEntity.setX(SCREEN_WIDTH/2);
                        if (scrollEntity.getY()-SCREEN_HEIGHT/2 < 0)
                            scrollEntity.setY(SCREEN_HEIGHT/2);

                        shipsLayer.move(scrollEntity.getX()+dx, scrollEntity.getY()+dy);
                    }
                }
            }
        });
        thread.setDaemon(true);
        thread.start();


        return scene;
    }

    Entity scrollEntity;
    float mTouchY;
    float mTouchX;

}