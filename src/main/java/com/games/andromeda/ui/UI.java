package com.games.andromeda.ui;

import android.app.Activity;
import android.util.Log;

import com.games.andromeda.GameActivity;
import com.games.andromeda.graph.Node;
import com.games.andromeda.graph.PathManager;
import com.games.andromeda.logic.WorldAccessor;
import com.games.andromeda.threads.GameTimer;
import com.games.andromeda.threads.Scrolling;
import com.games.andromeda.ui.hud.PanelHUD;
import com.games.andromeda.ui.layers.BackgroundLayer;
import com.games.andromeda.ui.layers.ShipsLayer;
import com.games.andromeda.ui.layers.SystemInfoLayer;
import com.games.andromeda.ui.layers.SystemsLayer;
import com.games.andromeda.ui.texture.TextureLoader;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.color.Color;

import com.games.andromeda.logic.phases.LevelPreparationStrategy;

public class UI {

    private PathManager manager;
    private PanelHUD panel;
    private final BackgroundLayer backgroundLayer;
    private final SystemsLayer systemsLayer;
    private final ShipsLayer shipsLayer;
    private final SystemInfoLayer systemInfoLayer;

    public UI(Activity activity, Scene scene, Camera camera, TextureLoader textureLoader,
              VertexBufferObjectManager vertexBufferObjectManager, final WorldAccessor world,
              ShipsLayer.IOnFleetMove onFleetMove, ShipsLayer.IOnFleetFight onFleetFight) {
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
        //MessageLayer messageLayer = new MessageLayer(scene, camera, textureLoader,
        //        vertexBufferObjectManager);

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
                Log.wtf("Phase", WorldAccessor.getInstance().getPhaseType().toString());
                switch (WorldAccessor.getInstance().getPhaseType()){
                    case LEVEL_PREPARATION_BASES:
                        LevelPreparationStrategy phase = (LevelPreparationStrategy)
                                WorldAccessor.getInstance().getPhase();
                        phase.handlePhaseEvent(node);
                        systemsLayer.repaint(); // todo
                        break;
                    default:
                        systemInfoLayer.show(node);
                }
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
        Thread timeThread = new Thread(new GameTimer(activity, panel));
        timeThread.setDaemon(true);
        timeThread.start();

        Scrolling scrolling = new Scrolling(camera, GameActivity.SCREEN_WIDTH, GameActivity.SCREEN_HEIGHT,
                (int)camera.getWidth()/2, (int)camera.getHeight()/2, shipsLayer);
        scene.setOnSceneTouchListener(scrolling.getListener());

        Thread thread = new Thread(scrolling);
        thread.setDaemon(true);
        thread.start();
    }

    public void repaintHUD() {
        panel.repaint();
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
