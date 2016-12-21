package com.games.andromeda.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.util.Log;
import android.widget.Toast;

import com.games.andromeda.GameActivity;
import com.games.andromeda.Phases;
import com.games.andromeda.R;
import com.games.andromeda.graph.BFSSolver;
import com.games.andromeda.graph.Node;
import com.games.andromeda.graph.PathBuilder;
import com.games.andromeda.graph.PathInfo;
import com.games.andromeda.logic.Fleet;
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

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

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

    private PathBuilder pathBuilder;
    private PanelHUD panel;
    private final BackgroundLayer backgroundLayer;
    private final SystemsLayer systemsLayer;
    private final ShipsLayer shipsLayer;
    private final SystemInfoLayer systemInfoLayer;
    private final MessageLayer messageLayer;
    private MediaPlayer mediaPlayer;

    public Activity activity;

    private UI(final GameActivity activity, final Scene scene, Camera camera, TextureLoader textureLoader,
               VertexBufferObjectManager vertexBufferObjectManager,
               ShipsLayer.IOnFleetMove onFleetMove, ShipsLayer.IOnFleetFight onFleetFight) {
        this.activity = activity;
        pathBuilder = new PathBuilder();

        scene.setBackground(new Background(Color.BLACK));
        //scene.setTouchAreaBindingOnActionDownEnabled(true);//Без этого не будет работать нормально перетаскивание спрайтов
        scene.setOnAreaTouchTraversalFrontToBack();//Сначала получает фокус верхний спрайт

        //Слой с фоном и линиями
        backgroundLayer = new BackgroundLayer(scene, camera, textureLoader,
                vertexBufferObjectManager, WorldAccessor.getInstance().getLevel());
        backgroundLayer.repaint();

        //слой с системами
        systemsLayer = new SystemsLayer(activity, scene, camera, textureLoader,
                vertexBufferObjectManager, WorldAccessor.getInstance().getLevel());
        WorldAccessor.getInstance().addBaseObserver(systemsLayer);
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
                new SystemInfoLayer(activity, scene,
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
                //pathBuilder.setTarget(node.getId());
            }

            @Override
            public void onUp(Node node) {
                //TODO handlePhaseEvent
                getSystemsLayer().unhighlightAll();
                if (!enabled)
                    return;
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
                    if (activeSprite != null && activeSprite.getFleet().getPosition() != node.getId()) {
                        final Fleet fleet = activeSprite.getFleet();

                        pathBuilder.start(fleet);
                        pathBuilder.setTarget(node.getId());
                        try {
                            final float energy = fleet.getEnergy();
                            final PathInfo path = pathBuilder.getPath();
                            float requiredEnergy =(float) (path.getPathWeight())/fleet.getProperties().getSpeed(fleet.getShipCount()) - 0.0000001f;
                            String str = "У флота осталось " + energy*100 + "% энергии. Вы хотите истратить " + requiredEnergy*100 + "% для перемещения?";
                            final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                            builder.setTitle("Предупреждение")
                                    .setMessage(str)
                                    .setCancelable(false)
                                    .setPositiveButton("Да",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    try {
                                                        getShipsLayer().moveFleet(path);
                                                        ((FleetMovingStrategy) Phases.getInstance().getPhase()).handlePhaseEvent(
                                                                new MoveFleetMessage.Move(fleet.getId(), energy, path));
                                                        getShipsLayer().releaseSprite();
                                                        pathBuilder.reset();
                                                        getShipsLayer().repaint();
                                                        panel.repaintShipInfo();
                                                    } catch (Fleet.NotEnoughEnergyException e) {
                                                        Log.wtf("errr",e.getMessage());
                                                    } catch (Fleet.InvalidPositionException e) {
                                                        Log.wtf("errr",e.getMessage());
                                                    } catch (Fleet.InvalidPathException e) {
                                                        Log.wtf("errr",e.getMessage());
                                                    }
                                                }
                                            })
                                    .setNegativeButton("Нет",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    dialog.cancel();
                                                }
                                            });
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    final AlertDialog alert = builder.create();
                                    alert.show();
                                }
                            });
                        }  catch (Exception e){
                            Log.wtf("moving: ", e.getMessage());
                        }
                    }
                } else {
                    String options = readFile();
                    if (options!=null && (options.equals("11")||options.equals("01"))) {
                        Phases.getInstance().getMediaPlayer().pause();
                        mediaPlayer = MediaPlayer.create(Phases.getInstance().getActivity(), R.raw.base);
                        mediaPlayer.setLooping(true);
                        mediaPlayer.start();
                    }
                    systemInfoLayer.show(node);
                }
            }
        });

        shipsLayer.setLayerListener(new ShipsLayer.LayerListener() {
            @Override
            public void onClick(Fleet fleet) {
                systemsLayer.unhighlightAll();
                if (!enabled)
                    return;
                BFSSolver solver = new BFSSolver(fleet);
                for (int node : solver.availableNodes()) {
                    systemsLayer.highlightSystem(node);
                }
            }
            @Override
            public void onCancel(){
                systemsLayer.unhighlightAll();
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

    private boolean enabled = false;

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        /*if (enabled) {
            messageLayer.hide();
        } else {
            messageLayer.show("Ход противника");
        }*/
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

    public void finishGame()
    {

        activity.finish();
    }

    public void hideAllDialogs() {
        if (systemInfoLayer != null)
            systemInfoLayer.hide();

        systemsLayer.unhighlightAll();
        shipsLayer.releaseSprite();
    }

    public MediaPlayer getMediaPlayer()
    {
        return mediaPlayer;
    }

    public void dispose()
    {
        instance = null;
    }

    private String readFile() {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    activity.openFileInput("options")));
            String str = "";
            str = br.readLine();
            return str;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "11";
    }
}
