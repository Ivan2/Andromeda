package com.games.andromeda.ui.layers;

import android.content.res.Resources;

import com.games.andromeda.ui.texture.TextureLoader;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.text.Text;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public class MessageLayer extends DialogLayer {

    private String msg = "";

    public MessageLayer(Resources resources, Scene scene, Camera camera, TextureLoader textureLoader,
                        VertexBufferObjectManager vertexBufferObjectManager) {
        super(resources, scene, camera, textureLoader, vertexBufferObjectManager);
    }

    public void show(String msg) {
        this.msg = msg;
        repaint();
        setVisibility(true);
    }

    @Override
    public void repaint() {
        Text text = new Text(0, 0, textureLoader.loadDialogTexture(), msg, vertexBufferObjectManager);
        text.setColor(1, 1, 1);
        contentLayer.attachChild(text);
        text.setX(camera.getCenterX()-text.getWidth()/2);
        text.setY(camera.getCenterY()-text.getHeight()/2);
    }

}
