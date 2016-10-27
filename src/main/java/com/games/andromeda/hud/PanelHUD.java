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
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.color.Color;

public class PanelHUD {

    private Text timerText;

    public PanelHUD(Camera camera, TextureLoader textureLoader,
                          VertexBufferObjectManager vertexBufferObjectManager) {

        Rectangle rectangle = new Rectangle((camera.getWidth()-MainActivity.SCREEN_WIDTH)/2,
                (camera.getHeight()-MainActivity.SCREEN_HEIGHT)/2-1, MainActivity.SCREEN_WIDTH,
                PxDpConverter.dpToPx(40), vertexBufferObjectManager);
        rectangle.setColor(new Color(0.5f, 0.5f, 0.5f, 0.9f));

        Rectangle ship1Sprite = createFleetSprite("red", 0.5f, 0.3f, textureLoader,
                vertexBufferObjectManager);
        ship1Sprite.setPosition(PxDpConverter.dpToPx(20),
                (rectangle.getHeight()-ship1Sprite.getHeight())/2);
        rectangle.attachChild(ship1Sprite);

        Rectangle ship2Sprite = createFleetSprite("gray", 0, 0, textureLoader,
                vertexBufferObjectManager);
        ship2Sprite.setPosition(ship1Sprite.getX()+ship1Sprite.getWidth()+PxDpConverter.dpToPx(30),
                (rectangle.getHeight()-ship2Sprite.getHeight())/2);
        rectangle.attachChild(ship2Sprite);

        Rectangle ship3Sprite = createFleetSprite("blue", 0, 0, textureLoader,
                vertexBufferObjectManager);
        ship3Sprite.setPosition(ship2Sprite.getX()+ship2Sprite.getWidth()+PxDpConverter.dpToPx(30),
                (rectangle.getHeight()-ship3Sprite.getHeight())/2);
        rectangle.attachChild(ship3Sprite);

        Rectangle ship4Sprite = createFleetSprite("brown", 0, 0, textureLoader,
                vertexBufferObjectManager);
        ship4Sprite.setPosition(ship3Sprite.getX()+ship3Sprite.getWidth()+PxDpConverter.dpToPx(50),
                (rectangle.getHeight()-ship4Sprite.getHeight())/2);
        rectangle.attachChild(ship4Sprite);

        Rectangle ship5Sprite = createFleetSprite("pink", 0, 0, textureLoader,
                vertexBufferObjectManager);
        ship5Sprite.setPosition(ship4Sprite.getX()+ship4Sprite.getWidth()+PxDpConverter.dpToPx(30),
                (rectangle.getHeight()-ship5Sprite.getHeight())/2);
        rectangle.attachChild(ship5Sprite);

        Rectangle ship6Sprite = createFleetSprite("green", 0, 0, textureLoader,
                vertexBufferObjectManager);
        ship6Sprite.setPosition(ship5Sprite.getX()+ship5Sprite.getWidth()+PxDpConverter.dpToPx(30),
                (rectangle.getHeight()-ship6Sprite.getHeight())/2);
        rectangle.attachChild(ship6Sprite);

        Font font = textureLoader.loadPanelTexture();
        /*ITextureRegion moneyTexture = textureLoader.loadMoneyTexture();
        Sprite moneySprite = new Sprite(20, (rectangle.getHeight()-moneyTexture.getHeight())/2,
                moneyTexture, vertexBufferObjectManager);

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

        rectangle.attachChild(moneySprite);
        rectangle.attachChild(moneyText);
        rectangle.attachChild(energySprite);
        rectangle.attachChild(energyText);
        */

        Text phaseText = new Text(0,
                (rectangle.getHeight()-font.getLineHeight())/2,
                font, "Фаза 1. ............", vertexBufferObjectManager);
        phaseText.setX(MainActivity.SCREEN_WIDTH-phaseText.getWidth()-10);

        timerText = new Text(0,
                (rectangle.getHeight()-font.getLineHeight())/2,
                font, "00:45", vertexBufferObjectManager);
        timerText.setX(phaseText.getX()-timerText.getWidth()-20);

        rectangle.attachChild(phaseText);
        rectangle.attachChild(timerText);

        HUD hud = new HUD();
        hud.attachChild(rectangle);
        camera.setHUD(hud);
    }

    public void repaint(int time) {
        timerText.setText("00:"+(time<10?"0"+time:time));
    }

    private Rectangle createFleetSprite(String color, float v1, float v2, TextureLoader textureLoader,
                                      VertexBufferObjectManager vertexBufferObjectManager) {
        float size = PxDpConverter.dpToPx(30);
        float progressHeight = PxDpConverter.dpToPx(8);
        float gap = PxDpConverter.dpToPx(5);

        Rectangle rectangle = new Rectangle(0, 0, size*2+gap, size,
                vertexBufferObjectManager);
        rectangle.setColor(Color.TRANSPARENT);

        Sprite shipSprite = new Sprite(0, 0,
                textureLoader.loadColoredShipTextire(color), vertexBufferObjectManager);
        shipSprite.setSize(size, size);
        rectangle.attachChild(shipSprite);

        Sprite shipMaxCountSprite = new Sprite(size+gap, 0,
                textureLoader.loadEmptyTexture(android.graphics.Color.GRAY),
                vertexBufferObjectManager);
        shipMaxCountSprite.setSize(size, progressHeight);
        rectangle.attachChild(shipMaxCountSprite);

        Sprite shipCountSprite = new Sprite(size+gap, 0,
                textureLoader.loadEmptyTexture(android.graphics.Color.GREEN),
                vertexBufferObjectManager);
        shipCountSprite.setSize(size*v1, progressHeight);
        rectangle.attachChild(shipCountSprite);

        Sprite shipMaxEnergySprite = new Sprite(size+gap, size-progressHeight,
                textureLoader.loadEmptyTexture(android.graphics.Color.GRAY),
                vertexBufferObjectManager);
        shipMaxEnergySprite.setSize(size, progressHeight);
        rectangle.attachChild(shipMaxEnergySprite);

        Sprite shipEnergySprite = new Sprite(size+gap, size-progressHeight,
                textureLoader.loadEmptyTexture(android.graphics.Color.BLUE),
                vertexBufferObjectManager);
        shipEnergySprite.setSize(size*v2, progressHeight);
        rectangle.attachChild(shipEnergySprite);

        return rectangle;
    }

}
