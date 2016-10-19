package com.games.andromeda.hud;

import com.games.andromeda.MainActivity;
import com.games.andromeda.PxDpConverter;
import com.games.andromeda.texture.TextureLoader;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.color.Color;

public class PanelHUD {

    private Text timerText;

    public PanelHUD(Camera camera, TextureLoader textureLoader,
                          VertexBufferObjectManager vertexBufferObjectManager) {

        Rectangle rectangle = new Rectangle((camera.getWidth()-MainActivity.SCREEN_WIDTH)/2,
                (camera.getHeight()-MainActivity.SCREEN_HEIGHT)/2-1, MainActivity.SCREEN_WIDTH,
                PxDpConverter.dpToPx(30), vertexBufferObjectManager);
        rectangle.setColor(new Color(0, 0, 0, 0.85f));

        ITextureRegion moneyTexture = textureLoader.loadMoneyTexture();
        Sprite moneySprite = new Sprite(20, (rectangle.getHeight()-moneyTexture.getHeight())/2,
                moneyTexture, vertexBufferObjectManager);

        Font font = textureLoader.loadPanelTexture();
        Text moneyText = new Text(moneySprite.getX()+moneySprite.getWidth()+10,
                (rectangle.getHeight()-font.getLineHeight())/2,
                font, "100", vertexBufferObjectManager);

        ITextureRegion energyTexture = textureLoader.loadEnergyTexture();
        Sprite energySprite = new Sprite(moneyText.getX()+moneyText.getWidth()+30,
                (rectangle.getHeight()-energyTexture.getHeight())/2,
                energyTexture, vertexBufferObjectManager);

        Text energyText = new Text(energySprite.getX()+energySprite.getWidth()+10,
                (rectangle.getHeight()-font.getLineHeight())/2,
                font, "100", vertexBufferObjectManager);

        Text phaseText = new Text(energyText.getX()+energyText.getWidth()+40,
                (rectangle.getHeight()-font.getLineHeight())/2,
                font, "Фаза 1. ............", vertexBufferObjectManager);
        phaseText.setX(MainActivity.SCREEN_WIDTH-phaseText.getWidth()-10);

        timerText = new Text(0,
                (rectangle.getHeight()-font.getLineHeight())/2,
                font, "00:45", vertexBufferObjectManager);
        timerText.setX(phaseText.getX()-timerText.getWidth()-20);

        rectangle.attachChild(moneySprite);
        rectangle.attachChild(moneyText);
        rectangle.attachChild(energySprite);
        rectangle.attachChild(energyText);
        rectangle.attachChild(phaseText);
        rectangle.attachChild(timerText);

        HUD hud = new HUD();
        hud.attachChild(rectangle);
        camera.setHUD(hud);
    }

    public void repaint(int time) {
        timerText.setText("00:"+(time<10?"0"+time:time));
    }

}
