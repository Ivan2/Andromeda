package com.games.andromeda;

import android.graphics.PointF;
import android.util.DisplayMetrics;
import android.util.Log;

import com.games.andromeda.graph.Edge;
import com.games.andromeda.graph.MyGraph;
import com.games.andromeda.graph.Node;
import com.games.andromeda.level.LevelLoader;
import com.games.andromeda.sprites.SystemSprite;
import com.games.andromeda.texture.TextureLoader;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.FixedResolutionPolicy;
import org.andengine.entity.primitive.Line;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.color.Color;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

public class MainActivity extends SimpleBaseGameActivity {

    private static int CAMERA_WIDTH = 700;
    private static int CAMERA_HEIGHT = 700;

    private Camera camera;
    private HashMap<Node.SystemType, ITextureRegion> systemTexures;
    private ITextureRegion emptyTextureRegion;
    private Font font;


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
        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
        systemTexures = new HashMap<>();
        systemTexures.put(Node.SystemType.EMPTY, TextureLoader.loadSystemTexture(getTextureManager(),
                Node.SystemType.EMPTY));
        systemTexures.put(Node.SystemType.MINI, TextureLoader.loadSystemTexture(getTextureManager(),
                Node.SystemType.EMPTY));
        systemTexures.put(Node.SystemType.FRIENDLY, TextureLoader.loadSystemTexture(getTextureManager(),
                Node.SystemType.FRIENDLY));
        systemTexures.put(Node.SystemType.ENEMY, TextureLoader.loadSystemTexture(getTextureManager(),
                Node.SystemType.ENEMY));
        emptyTextureRegion = TextureLoader.loadEmptyTexture(getTextureManager());

        BitmapTextureAtlas fontTexture = new BitmapTextureAtlas(getTextureManager(),
                512, 64, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        font = FontFactory.createFromAsset(getFontManager(), fontTexture, getAssets(),
                "fonts/font.ttf", 54, true, android.graphics.Color.WHITE);
        mEngine.getTextureManager().loadTexture(fontTexture);
        mEngine.getFontManager().loadFont(font);
    }

    private PointF getPos(float x, float y) {
        return new PointF(camera.getWidth()*x, camera.getHeight()*y);
    }

    @Override
    protected Scene onCreateScene() {
        final Scene scene = new Scene();
        scene.setBackground(new Background(Color.BLACK));
        //scene.setTouchAreaBindingOnActionDownEnabled(true);//Без этого не будет работать нормально перетаскивание спрайтов
        scene.setOnAreaTouchTraversalFrontToBack();//Сначала получает фокус верхний спрайт

        MyGraph graph = null;
        try {
            graph = LevelLoader.loadMap(this, "testmap");
        } catch (IOException e) {
            Log.wtf("loadMap: opening csv error", e.toString());
        } catch (LevelLoader.MapFormatException e) {
            Log.wtf("loadMap: csv content error", e.toString());
        }

        //Создание слоя с ребрами
        Rectangle lineLayer = new Rectangle(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT,
                this.mEngine.getVertexBufferObjectManager());//Нижний слой со спрайтами
        lineLayer.setColor(Color.TRANSPARENT);
        scene.attachChild(lineLayer);

        //Рисование ребер
        Collection<Edge> edges = graph.getEdges();
        Iterator<Edge> edgeIterator = edges.iterator();
        while (edgeIterator.hasNext()) {
            Edge edge = edgeIterator.next();
            PointF pos1 = getPos(edge.getNode1().getX(), edge.getNode1().getY());
            PointF pos2 = getPos(edge.getNode2().getX(), edge.getNode2().getY());
            Line line = new Line(pos1.x, pos1.y, pos2.x, pos2.y, 1,
                    mEngine.getVertexBufferObjectManager());
            line.setColor(0, 0, 1);
            lineLayer.attachChild(line);
        }

        //Создание слоя с информацией о системе
        final ButtonSprite dialogSprite = new ButtonSprite(0, 0, emptyTextureRegion,
                mEngine.getVertexBufferObjectManager());
        dialogSprite.setSize(camera.getWidth(), camera.getHeight());
        dialogSprite.setVisible(false);

        //Создание слоя с системами
        Rectangle systemsLayer = new Rectangle(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT,
                mEngine.getVertexBufferObjectManager());//Нижний слой со спрайтами
        systemsLayer.setColor(Color.TRANSPARENT);
        scene.attachChild(systemsLayer);

        //Рисование систем
        Collection<Node> nodes = graph.getNodes();
        Iterator<Node> nodeIterator = nodes.iterator();
        while (nodeIterator.hasNext()) {
            Node node = nodeIterator.next();
            float size = 48;
            if (node.getSystemType() == Node.SystemType.MINI)
                size *= 0.4f;
            PointF pos = getPos(node.getX(), node.getY());
            Sprite sprite = new SystemSprite(node, new Runnable() {
                @Override
                public void run() {
                    dialogSprite.setVisible(true);
                }
            }, pos.x - size/2, pos.y - size/2, systemTexures.get(node.getSystemType()),
                    mEngine.getVertexBufferObjectManager());
            sprite.setSize(size, size);
            systemsLayer.attachChild(sprite);
            scene.registerTouchArea(sprite);
        }

        //Добавление слоя с информацией о системе на сцену
        scene.attachChild(dialogSprite);
        scene.registerTouchArea(dialogSprite);

        //Кнопка закрытия слоя с информацией
        ButtonSprite okButtonSprite = new ButtonSprite(100, 100, emptyTextureRegion,
                mEngine.getVertexBufferObjectManager());
        okButtonSprite.setOnClickListener(new ButtonSprite.OnClickListener() {
            @Override
            public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX, float pTouchAreaLocalY) {
                dialogSprite.setVisible(false);
            }
        });
        dialogSprite.attachChild(okButtonSprite);
        scene.registerTouchArea(okButtonSprite);

        Text text = new Text(0, 0, font, "OK", mEngine.getVertexBufferObjectManager());
        text.setColor(1, 1, 1);
        okButtonSprite.attachChild(text);
        text.setSize(100, 50);
        okButtonSprite.setSize(100, 50);


        return scene;
    }

}