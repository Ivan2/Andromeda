package com.games.andromeda.layers;

import android.graphics.PointF;

import com.games.andromeda.graph.MyGraph;
import com.games.andromeda.graph.Node;
import com.games.andromeda.sprites.SystemSprite;
import com.games.andromeda.texture.TextureLoader;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.TextureManager;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.color.Color;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

public abstract class DialogLayer extends Layer {

    protected ButtonSprite layer;

    public DialogLayer(Scene scene, Camera camera, TextureLoader textureLoader,
                        VertexBufferObjectManager vertexBufferObjectManager) {
        super(scene, camera, textureLoader, vertexBufferObjectManager);

        layer = new ButtonSprite(0, 0, textureLoader.loadEmptyTexture(),
                vertexBufferObjectManager);
        layer.setSize(camera.getWidth(), camera.getHeight());
        layer.setVisible(false);
        scene.attachChild(layer);
        scene.registerTouchArea(layer);
    }

}
