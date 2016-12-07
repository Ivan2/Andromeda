package com.games.andromeda.ui;

import android.app.Activity;
import android.widget.Toast;

import com.games.andromeda.GameActivity;
import com.games.andromeda.Phases;
import com.games.andromeda.graph.Node;
import com.games.andromeda.logic.WorldAccessor;
import com.games.andromeda.logic.phases.FleetMovingStrategy;
import com.games.andromeda.logic.phases.FleetPreparationStrategy;
import com.games.andromeda.logic.phases.LevelPreparationStrategy;
import com.games.andromeda.message.MoveFleetMessage;
import com.games.andromeda.threads.Scrolling;
import com.games.andromeda.ui.hud.PanelHUD;
import com.games.andromeda.ui.layers.BackgroundLayer;
import com.games.andromeda.ui.layers.MessageLayer;
import com.games.andromeda.ui.layers.ShipsLayer;
import com.games.andromeda.ui.layers.SystemInfoLayer;
import com.games.andromeda.ui.layers.SystemsLayer;
import com.games.andromeda.ui.sprites.ShipSprite;
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

    public static void createInstance(GameActivity activity, Scene scene, Camera camera, TextureLoader textureLoader,
                                      VertexBufferObjectManager vertexBufferObjectManager,
                                      ShipsLayer.IOnFleetMove onFleetMove,ShipsLayer.IOnFleetFight onFleetFight) {
        instance = new UI(activity, scene, camera, textureLoader, vertexBufferObjectManager,
                onFleetMove, onFleetFight);
    }

    public static UI getInstance() {
        return instance;
    }

    //private PathManager manager;
    private PanelHUD panel;
    private final BackgroundLayer backgroundLayer;
    private final SystemsLayer systemsLayer;
    private final ShipsLayer shipsLayer;
    private final SystemInfoLayer systemInfoLayer;
    private final MessageLayer messageLayer;

    public Activity activity;

    private UI(GameActivity activity, Scene scene, Camera camera, TextureLoader textureLoader,
              VertexBufferObjectManager vertexBufferObjectManager,
              ShipsLayer.IOnFleetMove onFleetMove,ShipsLayer.IOnFleetFight onFleetFight) {
        this.activity = activity;
        //manager = new PathManager();

        scene.setBackground(new Background(Color.BLACK));
        //scene.setTouchAreaBindingOnActionDownEnabled(true);//Без этого не будет работать нормально перетаскивание спрайтов
        scene.setOnAreaTouchTraversalFrontToBack();//Сначала получает фокус верхний спрайт

        //Слой с фоном и линиями
        backgroundLayer = new BackgroundLayer(scene, camera, textureLoader,
                vertexBufferObjectManager, WorldAccessor.getInstance().getMap());
        backgroundLayer.repaint();

        //слой с системами
        systemsLayer = new SystemsLayer(activity, scene, camera, textureLoader,
                vertexBufferObjectManager, WorldAccessor.getInstance().getMap());
        systemsLayer.repaint();

        //слой с кораблями
        shipsLayer = new ShipsLayer(scene, camera, textureLoader,
                vertexBufferObjectManager, onFleetMove,onFleetFight);
        WorldAccessor.getInstance().addFleetObserver(shipsLayer);
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
            public void onMove(Node node) {
                //manager.addNode(node.getId());
            }

            @Override
            public void onUp(Node node) {
                //TODO handlePhaseEvent
                if (Phases.getInstance().getPhase() instanceof LevelPreparationStrategy) {
                    try {
                        boolean res = ((LevelPreparationStrategy)Phases.getInstance().
                                getPhase()).handlePhaseEvent(node);
                        if (res)
                            getSystemsLayer().repaint();
                    } catch (Exception e) {
                        UI.toast(e.getMessage());
                    }
                } else if (Phases.getInstance().getPhase() instanceof FleetPreparationStrategy) {
                    try {
                        ((FleetPreparationStrategy)Phases.getInstance().getPhase()).createFleet(node);
                    } catch (Exception e) {
                        UI.toast(e.getMessage());
                    }
                } else if (Phases.getInstance().getPhase() instanceof FleetMovingStrategy) {
                    ShipSprite activeSprite = ShipsLayer.activeSprite;
                    if (activeSprite != null && activeSprite.getFleet().getId() != node.getId()) {
                        //TODO show ask dialog (in beta?)
                        //TODO check energy and calc path
                        activeSprite.getFleet().setPosition(node.getId());
                        ((FleetMovingStrategy)Phases.getInstance().getPhase()).handlePhaseEvent(
                                new MoveFleetMessage.Move(activeSprite.getFleet().getId(),
                                        activeSprite.getFleet().getEnergy(), node.getId()));
                        /*try {
                            activeSprite.getFleet().makeMove(manager.getPath());
                            WorldAccessor world = WorldAccessor.getInstance();
                            int fleetNum = -1;
                            for (int i = 0; i < 6; i++) {
                                if (world.getAllFleets()[i] == activeSprite.getFleet()) {
                                    if (i > 2) i -=3;
                                    fleetNum = i+1;
                                    onFleetMove.onFleetMove(activeSprite.getFleet(), fleetNum);
                                    break;
                                }
                            }
                            for (int i = 0; i < 6; i++) {
                                if (world.getAllFleets()[i] != null && world.getAllFleets()[i] != activeSprite.getFleet() &&
                                        world.getAllFleets()[i].getPosition() == activeSprite.getFleet().getPosition() &&
                                        activeSprite.getFleet().getSide() != world.getAllFleets()[i].getSide()
                                        ) {
                                    if (i > 2) i -=3;
                                    onFleetFight.onFleetFight(activeSprite.getFleet(),world.getAllFleets()[i],fleetNum,i+1);
                                    break;
                                }
                            }
                        } catch (Exception e) {
                            Log.wtf("PATH", e.toString());
                            e.printStackTrace();
                        }*/
                        ShipsLayer.activeSprite = null;
                        //manager.reset();
                        getShipsLayer().repaint();
                    }
                } else
                    systemInfoLayer.show(node);
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
            messageLayer.hide();
        } else {
            messageLayer.show("Ход противника");
        }
        getPanel().setButtonEnabled(enabled);
    }

    public PanelHUD getPanel() {
        return panel;
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
