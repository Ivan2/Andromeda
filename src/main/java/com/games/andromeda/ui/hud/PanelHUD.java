package com.games.andromeda.ui.hud;

import android.content.res.Resources;

import com.games.andromeda.GameActivity;
import com.games.andromeda.PxDpConverter;
import com.games.andromeda.R;
import com.games.andromeda.logic.Fleet;
import com.games.andromeda.logic.GameObject;
import com.games.andromeda.logic.WorldAccessor;
import com.games.andromeda.ui.texture.TextureLoader;

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
    private Rectangle rectangleVertical;
    private Rectangle rectangleHorizontal;
    private TextureLoader textureLoader;
    private VertexBufferObjectManager vertexBufferObjectManager;

    public PanelHUD(Camera camera, TextureLoader textureLoader,
                    VertexBufferObjectManager vertexBufferObjectManager, Resources resources) {
        this.textureLoader = textureLoader;
        this.vertexBufferObjectManager = vertexBufferObjectManager;

        rectangleVertical = new Rectangle((camera.getWidth()- GameActivity.SCREEN_WIDTH)/2,
                (camera.getHeight()-GameActivity.SCREEN_HEIGHT)/2-1,
                PxDpConverter.dpToPx(resources.getDimension(R.dimen.vertical_panel_width)),
                GameActivity.SCREEN_HEIGHT, vertexBufferObjectManager);
        rectangleVertical.setColor(Color.TRANSPARENT);

        rectangleHorizontal = new Rectangle((camera.getWidth()- GameActivity.SCREEN_WIDTH)/2,
                (camera.getHeight()-GameActivity.SCREEN_HEIGHT)/2-1, GameActivity.SCREEN_WIDTH,
                PxDpConverter.dpToPx(resources.getDimension(R.dimen.horizontal_panel_height)),
                vertexBufferObjectManager);
        rectangleHorizontal.setColor(Color.TRANSPARENT);

        repaint();

        HUD hud = new HUD();
        hud.attachChild(rectangleVertical);
        hud.attachChild(rectangleHorizontal);
        camera.setHUD(hud);
    }

    public void repaintTime(int time) {
        timerText.setText("00:"+(time<10?"0"+time:time));
    }

    public void repaint() {
        rectangleVertical.detachChildren();
        rectangleHorizontal.detachChildren();

        WorldAccessor world = WorldAccessor.getInstance();
        Fleet fleet1 = world.getFleet(GameObject.Side.FEDERATION, 1);
        Fleet fleet2 = world.getFleet(GameObject.Side.FEDERATION, 2);
        Fleet fleet3 = world.getFleet(GameObject.Side.FEDERATION, 3);
        Fleet fleet4 = world.getFleet(GameObject.Side.EMPIRE, 1);
        Fleet fleet5 = world.getFleet(GameObject.Side.EMPIRE, 2);
        Fleet fleet6 = world.getFleet(GameObject.Side.EMPIRE, 3);

        Rectangle ship1Sprite = createFleetSprite("red", fleet1==null?0:fleet1.getEnergy(),
                fleet1==null?0:fleet1.getShipCount(),
                textureLoader, vertexBufferObjectManager);
        ship1Sprite.setPosition(PxDpConverter.dpToPx(4),
                PxDpConverter.dpToPx(20));
        rectangleVertical.attachChild(ship1Sprite);

        Rectangle ship2Sprite = createFleetSprite("gray", fleet2==null?0:fleet2.getEnergy(),
                fleet2==null?0:fleet2.getShipCount(),
                textureLoader, vertexBufferObjectManager);
        ship2Sprite.setPosition(ship1Sprite.getX(),
                ship1Sprite.getY()+ship1Sprite.getHeight()+PxDpConverter.dpToPx(10));
        rectangleVertical.attachChild(ship2Sprite);

        Rectangle ship3Sprite = createFleetSprite("blue", fleet3==null?0:fleet3.getEnergy(),
                fleet3==null?0:fleet3.getShipCount(),
                textureLoader, vertexBufferObjectManager);
        ship3Sprite.setPosition(ship1Sprite.getX(),
                ship2Sprite.getY()+ship2Sprite.getHeight()+PxDpConverter.dpToPx(10));
        rectangleVertical.attachChild(ship3Sprite);

        Rectangle ship4Sprite = createFleetSprite("brown", fleet4==null?0:fleet4.getEnergy(),
                fleet4==null?0:fleet4.getShipCount(),
                textureLoader, vertexBufferObjectManager);
        ship4Sprite.setPosition(ship1Sprite.getX(),
                ship3Sprite.getY()+ship3Sprite.getHeight()+PxDpConverter.dpToPx(30));
        rectangleVertical.attachChild(ship4Sprite);

        Rectangle ship5Sprite = createFleetSprite("pink", fleet5==null?0:fleet5.getEnergy(),
                fleet5==null?0:fleet5.getShipCount(),
                textureLoader, vertexBufferObjectManager);
        ship5Sprite.setPosition(ship1Sprite.getX(),
                ship4Sprite.getY()+ship4Sprite.getHeight()+PxDpConverter.dpToPx(10));
        rectangleVertical.attachChild(ship5Sprite);

        Rectangle ship6Sprite = createFleetSprite("green", fleet6==null?0:fleet6.getEnergy(),
                fleet6==null?0:fleet6.getShipCount(),
                textureLoader, vertexBufferObjectManager);
        ship6Sprite.setPosition(ship1Sprite.getX(),
                ship5Sprite.getY()+ship5Sprite.getHeight()+PxDpConverter.dpToPx(10));
        rectangleVertical.attachChild(ship6Sprite);

        Font font = textureLoader.loadSubtitleDialogTexture();

        Text phaseText = new Text(0,
                (rectangleHorizontal.getHeight()-font.getLineHeight())/2,
                font, "Фаза 1. ............", vertexBufferObjectManager);
        phaseText.setX(GameActivity.SCREEN_WIDTH-phaseText.getWidth()-10);

        timerText = new Text(0,
                (rectangleHorizontal.getHeight()-font.getLineHeight())/2,
                font, "00:45", vertexBufferObjectManager);
        timerText.setX(phaseText.getX()-timerText.getWidth()-20);

        rectangleHorizontal.attachChild(phaseText);
        rectangleHorizontal.attachChild(timerText);
    }

    private Rectangle createFleetSprite(String color, float energy, int count,
                                        TextureLoader textureLoader,
                                        VertexBufferObjectManager vertexBufferObjectManager) {
        float size = PxDpConverter.dpToPx(30);
        float progressHeight = PxDpConverter.dpToPx(4);
        float gap = PxDpConverter.dpToPx(2);

        Font font = textureLoader.loadPanelTexture();
        Text text = new Text(0, 0, font, count+"", vertexBufferObjectManager);

        Rectangle rectangle = new Rectangle(0, 0, size+gap+text.getWidth(), size+progressHeight+gap,
                vertexBufferObjectManager);
        rectangle.setColor(Color.TRANSPARENT);

        Sprite shipSprite = new Sprite(0, progressHeight+gap,
                textureLoader.loadColoredShipTexture(color), vertexBufferObjectManager);
        shipSprite.setSize(size, size);
        rectangle.attachChild(shipSprite);

        Sprite shipMaxEnergySprite = new Sprite(0, 0,
                textureLoader.loadEmptyTexture(android.graphics.Color.BLACK),
                vertexBufferObjectManager);
        shipMaxEnergySprite.setSize(size, progressHeight);
        rectangle.attachChild(shipMaxEnergySprite);

        Sprite shipEnergySprite = new Sprite(0, 0,
                textureLoader.loadEmptyTexture(android.graphics.Color.BLUE),
                vertexBufferObjectManager);
        shipEnergySprite.setSize(size*energy, progressHeight);
        rectangle.attachChild(shipEnergySprite);

        text.setPosition(rectangle.getWidth()-text.getWidth(), rectangle.getHeight()-text.getHeight());
        rectangle.attachChild(text);

        return rectangle;
    }

}
