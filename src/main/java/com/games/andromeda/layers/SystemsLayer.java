package com.games.andromeda.layers;

import android.graphics.PointF;

import com.games.andromeda.graph.MyGraph;
import com.games.andromeda.graph.Node;
import com.games.andromeda.sprites.SystemSprite;
import com.games.andromeda.texture.TextureLoader;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.TextureManager;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.color.Color;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

public class SystemsLayer extends Layer {

    public static abstract class LayerListener {
        public abstract void onClick(Node node);
        public abstract void onMove(Node node);
        public abstract void onUp(Node node);
    }

    private MyGraph graph;
    private Rectangle layer;
    private HashMap<Node.SystemType, ITextureRegion> systemTextures;

    private LayerListener layerListener;

    public SystemsLayer(Scene scene, Camera camera, TextureLoader textureLoader,
                        VertexBufferObjectManager vertexBufferObjectManager, MyGraph graph) {
        super(scene, camera, textureLoader, vertexBufferObjectManager);
        this.graph = graph;

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
    }

    @Override
    public void repaint() {
        //TODO удалить все спрайты со слоя

        Collection<Node> nodes = graph.getNodes();
        Iterator<Node> nodeIterator = nodes.iterator();
        while (nodeIterator.hasNext()) {
            final Node node = nodeIterator.next();
            float size = 100;
            PointF pos = getPos(node.getX(), node.getY());
            Sprite sprite = new SystemSprite(node, pos.x - size / 2, pos.y - size / 2,
                    systemTextures.get(node.getSystemType()), vertexBufferObjectManager) {

                @Override
                public void onClick() {
                    if (layerListener != null)
                        layerListener.onClick(node);
                }

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
            sprite.setSize(size, size);
            layer.attachChild(sprite);
            scene.registerTouchArea(sprite);
        }
    }

    public void setLayerListener(LayerListener layerListener) {
        this.layerListener = layerListener;
    }

}
