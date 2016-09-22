package com.games.andromeda;

import android.util.DisplayMetrics;
import android.util.Log;

import com.games.andromeda.graph.MyGraph;
import com.games.andromeda.graph.Node;
import com.games.andromeda.layers.AskLayer;
import com.games.andromeda.layers.BackgroundLayer;
import com.games.andromeda.layers.MessageLayer;
import com.games.andromeda.layers.SystemsLayer;
import com.games.andromeda.level.LevelLoader;
import com.games.andromeda.texture.TextureLoader;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.FixedResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.color.Color;

import java.io.IOException;
import java.util.LinkedList;

public class MainActivity extends SimpleBaseGameActivity {

    private static int CAMERA_WIDTH = 700;
    private static int CAMERA_HEIGHT = 700;

    private Camera camera;
    private TextureLoader textureLoader;

    public static LinkedList<Node> selectedNodes = new LinkedList<>();

    @Override
    public EngineOptions onCreateEngineOptions() {
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
        SystemsLayer systemsLayer = new SystemsLayer(scene, camera, textureLoader,
                mEngine.getVertexBufferObjectManager(), graph);
        systemsLayer.repaint();

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

            }

            @Override
            public void onUp(Node node) {

            }
        });


        return scene;
    }

}