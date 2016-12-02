package com.games.andromeda.ui;

import android.app.Activity;
import android.widget.Toast;

import com.games.andromeda.GameActivity;
import com.games.andromeda.Phases;
import com.games.andromeda.graph.Node;
import com.games.andromeda.graph.PathManager;
import com.games.andromeda.logic.Base;
import com.games.andromeda.logic.WorldAccessor;
import com.games.andromeda.logic.phases.LevelPreparationStrategy;
import com.games.andromeda.threads.Scrolling;
import com.games.andromeda.ui.hud.PanelHUD;
import com.games.andromeda.ui.layers.BackgroundLayer;
import com.games.andromeda.ui.layers.MessageLayer;
import com.games.andromeda.ui.layers.ShipsLayer;
import com.games.andromeda.ui.layers.SystemInfoLayer;
import com.games.andromeda.ui.layers.SystemsLayer;
import com.games.andromeda.ui.texture.TextureLoader;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.color.Color;

public class UI {

    public static void toast(final String msg) {
        if (getInstance() != null && getInstance().activity != null)
            getInstance().activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getInstance().activity, msg, Toast.LENGTH_LONG).show();
                }
            });
    }

    private static UI instance;

    public static void createInstance(Activity activity, Scene scene, Camera camera, TextureLoader textureLoader,
                                      VertexBufferObjectManager vertexBufferObjectManager, WorldAccessor world,
                                      ShipsLayer.IOnFleetMove onFleetMove,ShipsLayer.IOnFleetFight onFleetFight) {
        instance = new UI(activity, scene, camera, textureLoader, vertexBufferObjectManager,
                world, onFleetMove, onFleetFight);
    }

    public static UI getInstance() {
        return instance;
    }

    private PathManager manager;
    private PanelHUD panel;
    private final BackgroundLayer backgroundLayer;
    private final SystemsLayer systemsLayer;
    private final ShipsLayer shipsLayer;
    private final SystemInfoLayer systemInfoLayer;
    private final MessageLayer messageLayer;

    public Activity activity;

    private UI(Activity activity, Scene scene, Camera camera, TextureLoader textureLoader,
              VertexBufferObjectManager vertexBufferObjectManager, WorldAccessor world,
              ShipsLayer.IOnFleetMove onFleetMove,ShipsLayer.IOnFleetFight onFleetFight) {
        this.activity = activity;
        manager = new PathManager();

        scene.setBackground(new Background(Color.BLACK));
        //scene.setTouchAreaBindingOnActionDownEnabled(true);//Без этого не будет работать нормально перетаскивание спрайтов
        scene.setOnAreaTouchTraversalFrontToBack();//Сначала получает фокус верхний спрайт

        //Слой с фоном и линиями
        backgroundLayer = new BackgroundLayer(scene, camera, textureLoader,
                vertexBufferObjectManager, world.getMap());
        backgroundLayer.repaint();

        //слой с системами
        systemsLayer = new SystemsLayer(scene, camera, textureLoader,
                vertexBufferObjectManager, world.getMap());
        systemsLayer.repaint();

        //слой с кораблями
        shipsLayer = new ShipsLayer(scene, camera, textureLoader,
                vertexBufferObjectManager, manager, onFleetMove,onFleetFight);
        world.addFleetObserver(shipsLayer);
        shipsLayer.repaint();

        //слой с сообщением
        messageLayer = new MessageLayer(activity.getResources(), scene, camera, textureLoader,
                vertexBufferObjectManager);

        //слой с вопросом
        systemInfoLayer =
                new SystemInfoLayer(activity.getResources(), scene,
                camera, textureLoader, vertexBufferObjectManager) {
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
                if (Phases.getInstance().getPhase() instanceof LevelPreparationStrategy) {
                    if (node.getSystemType() != Node.SystemType.EMPTY)
                        return;
                    try {
                        Base base = new Base(Phases.getInstance().side, node); //TODO check base count
                        WorldAccessor.getInstance().setBase(base);
                        getSystemsLayer().repaint(); //TODO перерисовка только одной ноды
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else
                    systemInfoLayer.show(node);
            }

            @Override
            public void onMove(Node node) {
                manager.addNode(node);
            }

            @Override
            public void onUp(Node node) {

            }
        });

        panel = new PanelHUD(camera, textureLoader, vertexBufferObjectManager,
                activity.getResources());

        Scrolling scrolling = new Scrolling(camera, GameActivity.SCREEN_WIDTH, GameActivity.SCREEN_HEIGHT,
                (int)camera.getWidth()/2, (int)camera.getHeight()/2, shipsLayer);
        scene.setOnSceneTouchListener(scrolling.getListener());

        Thread thread = new Thread(scrolling);
        thread.setDaemon(true);
        thread.start();
    }

    public void setEnabled(boolean enabled) {
        if (enabled) {
            messageLayer.show("Ход противника");
        } else {
            messageLayer.hide();
        }
    }

    public void repaintPhaseName(String phaseName) {
        panel.repaintPhaseName(phaseName);
    }

    public void repaintShipInfo() {
        panel.repaintShipInfo();
    }

    public void repaintTimer(int time) {
        panel.repaintTime(time);
    }

    public BackgroundLayer getBackgroundLayer() {
        return backgroundLayer;
    }

    public SystemsLayer getSystemsLayer() {
        return systemsLayer;
    }

    public ShipsLayer getShipsLayer() {
        return shipsLayer;
    }

    public SystemInfoLayer getSystemInfoLayer() {
        return systemInfoLayer;
    }
}
