package com.games.andromeda.ui.layers;

import android.content.res.Resources;
import android.graphics.Color;

import com.games.andromeda.GameActivity;
import com.games.andromeda.PxDpConverter;
import com.games.andromeda.R;
import com.games.andromeda.ui.texture.TextureLoader;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public abstract class DialogLayer extends Layer {

    private ButtonSprite background;
    protected Rectangle contentLayer;
    protected float marginLeft;
    protected float marginTop;

    public DialogLayer(Resources resources, Scene scene, Camera camera, TextureLoader textureLoader,
                       VertexBufferObjectManager vertexBufferObjectManager) {
        super(scene, camera, textureLoader, vertexBufferObjectManager);

        marginLeft = PxDpConverter.dpToPx(resources.getDimension(R.dimen.vertical_panel_width));
        marginTop = PxDpConverter.dpToPx(resources.getDimension(R.dimen.horizontal_panel_height));

        background = new ButtonSprite(0, 0,
                textureLoader.loadEmptyTexture(Color.argb(220, 0, 0, 0)),
                vertexBufferObjectManager);
        background.setSize(GameActivity.SCREEN_WIDTH, GameActivity.SCREEN_HEIGHT);
        background.setVisible(false);
        scene.attachChild(background);
        scene.registerTouchArea(background);

        contentLayer = new Rectangle(marginLeft, marginTop,
                background.getWidth()-marginLeft, background.getHeight()-marginTop,
                vertexBufferObjectManager);
        contentLayer.setColor(org.andengine.util.color.Color.TRANSPARENT);
        background.attachChild(contentLayer);
    }

    protected void moveToCenter() {
        background.setPosition(camera.getCenterX() - GameActivity.SCREEN_WIDTH/2,
                camera.getCenterY() - GameActivity.SCREEN_HEIGHT/2);
    }

    protected void setVisibility(boolean visibility) {
        if (visibility) {
            background.setVisible(true);
            moveToCenter();
        } else
            background.setVisible(false);
    }

}