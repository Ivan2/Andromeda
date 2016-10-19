package com.games.andromeda.layers;

import android.util.Log;

import com.games.andromeda.texture.TextureLoader;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.IEntity;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.text.Text;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.HorizontalAlign;

public abstract class AskLayer extends DialogLayer {

    protected abstract void onOk();
    protected abstract void onCancel();

    private String msg = "";

    public AskLayer(Scene scene, Camera camera, TextureLoader textureLoader,
                    VertexBufferObjectManager vertexBufferObjectManager) {
        super(scene, camera, textureLoader, vertexBufferObjectManager);
    }

    public void show(String msg) {
        this.msg = msg;
        repaint();
        layer.setVisible(true);
    }

    @Override
    public void repaint() {
        layer.detachChildren();
        Text text = new Text(0, 0, textureLoader.loadTextTexture(), msg, vertexBufferObjectManager);
        text.setColor(1, 1, 1);
        layer.attachChild(text);

        ButtonSprite okButtonSprite = new ButtonSprite(0, 0, textureLoader.loadEmptyTexture(),
                vertexBufferObjectManager);
        okButtonSprite.setOnClickListener(new ButtonSprite.OnClickListener() {
            @Override
            public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX, float pTouchAreaLocalY) {
                layer.setVisible(false);
                onOk();
            }
        });
        layer.attachChild(okButtonSprite);
        scene.registerTouchArea(okButtonSprite);

        Text okText = new Text(0, 0, textureLoader.loadTextTexture(), "Да", vertexBufferObjectManager);
        okText.setColor(1, 1, 1);
        okText.setHorizontalAlign(HorizontalAlign.CENTER);
        okButtonSprite.attachChild(okText);
        okButtonSprite.setSize(100, okText.getHeight());
        okText.setX((okButtonSprite.getWidth()-okText.getWidth())/2);


        ButtonSprite cancelButtonSprite = new ButtonSprite(0, 0, textureLoader.loadEmptyTexture(),
                vertexBufferObjectManager);
        cancelButtonSprite.setOnClickListener(new ButtonSprite.OnClickListener() {
            @Override
            public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX, float pTouchAreaLocalY) {
                layer.setVisible(false);
                onCancel();
            }
        });
        layer.attachChild(cancelButtonSprite);
        scene.registerTouchArea(cancelButtonSprite);

        Text cancelText = new Text(0, 0, textureLoader.loadTextTexture(), "Нет", vertexBufferObjectManager);
        cancelText.setColor(1, 1, 1);
        cancelText.setHorizontalAlign(HorizontalAlign.CENTER);
        cancelButtonSprite.attachChild(cancelText);
        cancelButtonSprite.setSize(100, cancelText.getHeight());
        cancelText.setX((cancelButtonSprite.getWidth()-cancelText.getWidth())/2);

        text.setX(camera.getCenterX() - text.getWidth() / 2);
        text.setY(camera.getCenterY() - text.getHeight());

        okButtonSprite.setX(camera.getCenterX() - okButtonSprite.getWidth()-10);
        okButtonSprite.setY(camera.getCenterY() + okButtonSprite.getHeight()/2);

        cancelButtonSprite.setX(camera.getCenterX()+10);
        cancelButtonSprite.setY(camera.getCenterY() + cancelButtonSprite.getHeight()/2);
    }

}
