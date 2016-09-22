package com.games.andromeda.layers;

import android.graphics.PointF;

import com.games.andromeda.texture.TextureLoader;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public abstract class Layer {

    abstract void repaint();

    protected Scene scene;
    protected Camera camera;
    protected TextureLoader textureLoader;
    protected VertexBufferObjectManager vertexBufferObjectManager;

    public Layer(Scene scene, Camera camera, TextureLoader textureLoader,
                 VertexBufferObjectManager vertexBufferObjectManager) {
        this.scene = scene;
        this.camera = camera;
        this.textureLoader = textureLoader;
        this.vertexBufferObjectManager = vertexBufferObjectManager;
    }

    protected PointF getPos(float x, float y) {
        return new PointF(camera.getWidth()*x, camera.getHeight()*y);
    }
}
