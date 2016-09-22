package com.games.andromeda.layers;

import com.games.andromeda.graph.Node;
import com.games.andromeda.texture.TextureLoader;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.color.Color;

public class ShipsLayer extends Layer {

    public static abstract class LayerListener {
        public abstract void onClick(Node node);
        public abstract void onMove(Node node);
        public abstract void onUp(Node node);
    }

    private Rectangle layer;

    private LayerListener layerListener;

    public ShipsLayer(Scene scene, Camera camera, TextureLoader textureLoader,
                      VertexBufferObjectManager vertexBufferObjectManager) {
        super(scene, camera, textureLoader, vertexBufferObjectManager);

        layer = new Rectangle(0, 0, camera.getWidth(), camera.getHeight(),
                vertexBufferObjectManager);
        layer.setColor(Color.TRANSPARENT);
        scene.attachChild(layer);
    }

    @Override
    public void repaint() {
        //TODO удалить все спрайты со слоя
    }

    public void setLayerListener(LayerListener layerListener) {
        this.layerListener = layerListener;
    }

}
