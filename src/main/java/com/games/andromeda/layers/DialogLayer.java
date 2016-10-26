package com.games.andromeda.layers;

import android.graphics.Color;

import com.games.andromeda.MainActivity;
import com.games.andromeda.texture.TextureLoader;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public abstract class DialogLayer extends Layer {

    protected ButtonSprite layer;

    public DialogLayer(Scene scene, Camera camera, TextureLoader textureLoader,
                        VertexBufferObjectManager vertexBufferObjectManager) {
        super(scene, camera, textureLoader, vertexBufferObjectManager);

        layer = new ButtonSprite(0, 0,
                textureLoader.loadEmptyTexture(Color.argb(220, 0, 0, 0)),
                vertexBufferObjectManager);
        layer.setSize(MainActivity.SCREEN_WIDTH, MainActivity.SCREEN_HEIGHT);
        layer.setVisible(false);
        scene.attachChild(layer);
        scene.registerTouchArea(layer);
    }

    protected void moveToCenter() {
        layer.setPosition(camera.getCenterX() - MainActivity.SCREEN_WIDTH/2,
                camera.getCenterY() - MainActivity.SCREEN_HEIGHT/2);
    }

}
