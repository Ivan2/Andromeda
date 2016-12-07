package com.games.andromeda.ui.layers;

import android.graphics.PointF;

import com.games.andromeda.GameActivity;
import com.games.andromeda.graph.MyGraph;
import com.games.andromeda.graph.Node;
import com.games.andromeda.logic.WorldAccessor;
import com.games.andromeda.ui.sprites.SystemSprite;
import com.games.andromeda.ui.texture.TextureLoader;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.color.Color;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class SystemsLayer extends Layer {

    public static abstract class LayerListener {
        public abstract void onMove(Node node);
        public abstract void onUp(Node node);
    }

    private MyGraph graph;
    private Rectangle layer;
    private HashMap<Node.SystemType, ITextureRegion> systemTextures;
    private GameActivity activity;

    private LayerListener layerListener;

    public SystemsLayer(GameActivity activity, Scene scene, Camera camera, TextureLoader textureLoader,
                        VertexBufferObjectManager vertexBufferObjectManager, MyGraph graph) {
        super(scene, camera, textureLoader, vertexBufferObjectManager);
        this.graph = graph;
        this.activity = activity;

        systemTextures = new HashMap<>();
        systemTextures.put(Node.SystemType.EMPTY,
                textureLoader.loadSystemTexture(Node.SystemType.EMPTY));
        systemTextures.put(Node.SystemType.MINI,
                textureLoader.loadSystemTexture(Node.SystemType.MINI));
        systemTextures.put(Node.SystemType.FRIENDLY,
                textureLoader.loadSystemTexture(Node.SystemType.FRIENDLY));
        systemTextures.put(Node.SystemType.ENEMY,
                textureLoader.loadSystemTexture(Node.SystemType.ENEMY));
        systemTextures.put(Node.SystemType.HYPER,
                textureLoader.loadHyperTexture());

        layer = new Rectangle(0, 0, camera.getWidth(), camera.getHeight(),
                vertexBufferObjectManager);
        layer.setColor(Color.TRANSPARENT);
        scene.attachChild(layer);

        sprites = new HashMap<>();
        Collection<Node> nodes = graph.getNodes();
        for (final Node node : nodes) {
            float size = 50;
            PointF pos = getPos(node.getX(), node.getY());
            SystemSprite sprite = new SystemSprite(node, pos.x - size / 2, pos.y - size / 2,
                    systemTextures.get(node.getSystemType()), vertexBufferObjectManager) {

                @Override
                public void onMove() {
                    if (layerListener != null)
                        layerListener.onMove(node);
                }

                @Override
                public void onUp() {
                    if (layerListener != null)
                        layerListener.onUp(node);
                }
            };

            if (node.getSystemType() == Node.SystemType.HYPER)
                sprite.setSize(size * 2, size * 2);
            else
                sprite.setSize(size, size);
            layer.attachChild(sprite);
            scene.registerTouchArea(sprite);
            sprites.put(sprite, node.getId());
        }
    }

    private Map<SystemSprite, Integer> sprites;

    @Override
    public void repaint() {
        activity.runOnUpdateThread(new Runnable() {
            @Override
            public void run() {
                Map<Integer, Node> nodes = WorldAccessor.getInstance().getNodes();
                for(Map.Entry<SystemSprite, Integer> entry: sprites.entrySet()){
                    switch (nodes.get(entry.getValue()).getSystemType()){
                        case EMPTY:
                            entry.getKey().setColor(new Color(0.8f, 0.8f, 0.8f));
                            break;
                        case FRIENDLY:
                            entry.getKey().setColor(Color.GREEN);
                            break;
                        case ENEMY:
                            entry.getKey().setColor(Color.RED);
                            break;
                        default:
                            entry.getKey().setColor(new Color(0.8f, 0.8f, 0.8f));
                    }
                }
            }

        });
    }

    public void setLayerListener(LayerListener layerListener) {
        this.layerListener = layerListener;
    }

}
