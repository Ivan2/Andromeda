package com.games.andromeda.layers;

import com.games.andromeda.texture.TextureLoader;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.text.Text;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public class MessageLayer extends DialogLayer {

    private String msg = "";

    public MessageLayer(Scene scene, Camera camera, TextureLoader textureLoader,
                        VertexBufferObjectManager vertexBufferObjectManager) {
        super(scene, camera, textureLoader, vertexBufferObjectManager);

        layer.setOnClickListener(new ButtonSprite.OnClickListener() {
            @Override
            public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX, float pTouchAreaLocalY) {
                layer.setVisible(false);
            }
        });
    }

    public void show(String msg) {
        this.msg = msg;
        repaint();
        layer.setVisible(true);
    }

    @Override
    public void repaint() {
        Text text = new Text(0, 0, textureLoader.loadDialogTexture(), msg, vertexBufferObjectManager);
        text.setColor(1, 1, 1);
        layer.attachChild(text);
        text.setX(camera.getCenterX()-text.getWidth()/2);
        text.setY(camera.getCenterY()-text.getHeight()/2);
    }

}
