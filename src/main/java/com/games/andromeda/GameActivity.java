package com.games.andromeda;

import android.util.DisplayMetrics;

import com.games.andromeda.dialogs.QuitDialog;
import com.games.andromeda.logic.Fleet;
import com.games.andromeda.multiplayer.Client;
import com.games.andromeda.threads.GameTimer;
import com.games.andromeda.ui.UI;
import com.games.andromeda.ui.layers.ShipsLayer;
import com.games.andromeda.ui.texture.TextureLoader;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.FixedResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.ui.activity.SimpleBaseGameActivity;

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
        PxDpConverter.createInstance(this);
        Scene scene = new Scene();

        /*try {
            WorldAccessor.init(LevelLoader.loadMap(this, "map"));
        } catch (IOException e) {
            Log.wtf("loadMap: opening csv error", e.toString());
        } catch (LevelLoader.MapFormatException e) {
            Log.wtf("loadMap: csv content error", e.toString());
        }*/

        ShipsLayer.IOnFleetMove onFleetMove = new ShipsLayer.IOnFleetMove() {
            @Override
            public void onFleetMove(Fleet fleet,int num) {
                //client.sendMoveShipMessage(fleet,num);
            }
        };
        ShipsLayer.IOnFleetFight onFleetFight = new ShipsLayer.IOnFleetFight() {
            @Override
            public void onFleetFight(Fleet attackingFleet,  Fleet anotherFleet, int number, int secondNumber) {
                //client.sendChangeFleetMessage(attackingFleet.getSide(),attackingFleet,anotherFleet,number,secondNumber);

            }
        };

        UI.createInstance(this, scene, camera, textureLoader, mEngine.getVertexBufferObjectManager(),
                onFleetMove,onFleetFight);

        GameTimer gameTimer = new GameTimer() {
            @Override
            public void onTime(final int time) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        UI.getInstance().getPanel().repaintTime(time);
                        if (time == 0) {
                            Phases.getInstance().serverEndPhase();
                        }
                    }
                });
            }
        };
        gameTimer.stop();
        UI.getInstance().getPanel().repaintMoney(0);
        UI.getInstance().getPanel().repaintSide(Phases.getInstance().side);

        Phases.getInstance().setActivity(this);
        Phases.getInstance().startGame(gameTimer);

        return scene;
    }

    @Override
    public void onDestroy(){
        if (Phases.getInstance().getMediaPlayer()!=null) {
            Phases.getInstance().getMediaPlayer().release();
        }
        if (UI.getInstance().getMediaPlayer()!=null) {
            UI.getInstance().getMediaPlayer().release();
        }
        setResult(1);
        super.onDestroy();
    }

    @Override
    public void onBackPressed()
    {
        QuitDialog dialog = new QuitDialog();
        dialog.show(getFragmentManager(),"");
    }

}