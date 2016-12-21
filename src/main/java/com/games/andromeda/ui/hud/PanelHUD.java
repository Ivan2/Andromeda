package com.games.andromeda.ui.hud;

import android.content.res.Resources;

import com.games.andromeda.GameActivity;
import com.games.andromeda.Phases;
import com.games.andromeda.PxDpConverter;
import com.games.andromeda.R;
import com.games.andromeda.graph.Node;
import com.games.andromeda.logic.Base;
import com.games.andromeda.logic.Fleet;
import com.games.andromeda.logic.GameObject;
import com.games.andromeda.logic.WorldAccessor;
import com.games.andromeda.ui.layers.ShipsLayer;
import com.games.andromeda.ui.texture.TextureLoader;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.color.Color;

import java.util.Locale;

public class PanelHUD {

    private HUD hud;
    private Rectangle rectangleVertical;
    private Rectangle rectangleHorizontal;
    private TextureLoader textureLoader;
    private VertexBufferObjectManager vertexBufferObjectManager;

    private float margin = PxDpConverter.dpToPx(5);
    private float size = PxDpConverter.dpToPx(30);
    private float progressHeight = PxDpConverter.dpToPx(4);
    private float gap = PxDpConverter.dpToPx(2);
    private Rectangle[] shipSprites;
    private Sprite[] shipEnergySprites;
    private Text[] shipCountTexts;
    private ButtonSprite endPhaseButton;
    private Text friendlyBaseInfo;
    private Text enemyBaseInfo;
    private Text moneyText;
    private Text phaseText;
    private Text timerText;
    private Sprite[] sideLogo;
    private GameObject.Side side;

    public PanelHUD(Camera camera, TextureLoader textureLoader,
                    VertexBufferObjectManager vertexBufferObjectManager, Resources resources) {
        this.textureLoader = textureLoader;
        this.vertexBufferObjectManager = vertexBufferObjectManager;

        rectangleVertical = new Rectangle((camera.getWidth()- GameActivity.SCREEN_WIDTH)/2,
                (camera.getHeight()-GameActivity.SCREEN_HEIGHT)/2-1,
                resources.getDimension(R.dimen.vertical_panel_width),
                GameActivity.SCREEN_HEIGHT, vertexBufferObjectManager);
        rectangleVertical.setColor(Color.TRANSPARENT);

        rectangleHorizontal = new Rectangle((camera.getWidth()- GameActivity.SCREEN_WIDTH)/2,
                (camera.getHeight()-GameActivity.SCREEN_HEIGHT)/2-1, GameActivity.SCREEN_WIDTH,
                resources.getDimension(R.dimen.horizontal_panel_height),
                vertexBufferObjectManager);
        rectangleHorizontal.setColor(Color.TRANSPARENT);

        hud = new HUD();
        hud.attachChild(rectangleVertical);
        hud.attachChild(rectangleHorizontal);
        camera.setHUD(hud);

        createGUI();
        repaintShipInfo();
    }

    private void createGUI() {
        float top = PxDpConverter.dpToPx(10);
        float left = PxDpConverter.dpToPx(4);
        Font font = textureLoader.loadSubtitleDialogTexture();
        sideLogo = new Sprite[2];

        Sprite moneySprite = new Sprite(left, top,
                textureLoader.loadMoneyTexture(), vertexBufferObjectManager);
        moneySprite.setSize(size, size);
        rectangleVertical.attachChild(moneySprite);

        moneyText = new Text(0, 0, textureLoader.loadPanelTexture(),
                "10000", vertexBufferObjectManager);
        moneyText.setPosition(moneySprite.getWidth(),
                moneySprite.getHeight());
        rectangleVertical.attachChild(moneyText);

        top += moneyText.getY() + moneyText.getHeight() + top;

        String[] colors = ShipsLayer.SHIP_COLORS;
        shipSprites = new Rectangle[6];
        shipCountTexts = new Text[6];
        shipEnergySprites = new Sprite[6];
        for (int i=0; i<6; i++) {
            shipSprites[i] = createFleetSprite(i, colors[i],
                    textureLoader, vertexBufferObjectManager);
            top += PxDpConverter.dpToPx(10);
            if (i % 3 == 0)
                top += PxDpConverter.dpToPx(10);
            shipSprites[i].setPosition(left, top);
            top += shipSprites[i].getHeight();
            rectangleVertical.attachChild(shipSprites[i]);
        }

        sideLogo[0] = createLogoSprite("empire", left);
        sideLogo[1] = createLogoSprite("federation", left);

        Sprite friendlyBase = createBaseSprite(Node.SystemType.FRIENDLY, 0);
        friendlyBaseInfo = new Text(friendlyBase.getX() + friendlyBase.getWidth()*1.2f,
                (rectangleHorizontal.getHeight()-font.getLineHeight())/2,
                font, "--", vertexBufferObjectManager);
        Sprite enemyBase = createBaseSprite(Node.SystemType.ENEMY, friendlyBaseInfo.getX()
                + friendlyBaseInfo.getAutoWrapWidth());
        enemyBaseInfo = new Text(enemyBase.getX() + enemyBase.getWidth()*1.2f,
                (rectangleHorizontal.getHeight()-font.getLineHeight())/2,
                font, "--", vertexBufferObjectManager);

        endPhaseButton = new ButtonSprite(0, 0,
                textureLoader.loadEmptyTexture(android.graphics.Color.argb(15, 255, 255, 255)),
                vertexBufferObjectManager);
        endPhaseButton.setOnClickListener(new ButtonSprite.OnClickListener() {
            @Override
            public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX, float pTouchAreaLocalY) {
                Phases.getInstance().clientEndPhase();
            }
        });
        hud.registerTouchArea(endPhaseButton);


        Text endPhaseText = new Text(0, 0, textureLoader.loadDialogTexture(),
                "Завершить фазу", vertexBufferObjectManager);
        endPhaseText.setColor(1, 1, 1);
        endPhaseText.setHorizontalAlign(HorizontalAlign.CENTER);
        endPhaseButton.attachChild(endPhaseText);
        endPhaseButton.setSize(endPhaseText.getWidth()+PxDpConverter.dpToPx(20),
                rectangleHorizontal.getHeight()-margin*2);
        endPhaseText.setPosition((endPhaseButton.getWidth() - endPhaseText.getWidth())/2,
                (endPhaseButton.getHeight() - endPhaseText.getHeight())/2);

        endPhaseButton.setPosition(GameActivity.SCREEN_WIDTH-endPhaseButton.getWidth()-margin*2, margin);

        timerText = new Text(0,
                (rectangleHorizontal.getHeight()-font.getLineHeight())/2,
                font, "00:00", vertexBufferObjectManager);
        timerText.setX(endPhaseButton.getX()-timerText.getWidth()-margin*4);

        phaseText = new Text(0,
                (rectangleHorizontal.getHeight()-font.getLineHeight())/2,
                font, "абвгдеёжзийклмнопрстуфхцчшщъыьэюяabcdefghijklmnopqrstuvwxyz1234567890",
                vertexBufferObjectManager);
        phaseText.setX(timerText.getX()-phaseText.getWidth()-margin*4);


        rectangleHorizontal.attachChild(friendlyBase);
        rectangleHorizontal.attachChild(friendlyBaseInfo);
        rectangleHorizontal.attachChild(enemyBaseInfo);
        rectangleHorizontal.attachChild(enemyBase);
        rectangleHorizontal.attachChild(endPhaseButton);
        rectangleHorizontal.attachChild(timerText);
        rectangleHorizontal.attachChild(phaseText);
    }

    public void repaintSide(GameObject.Side side) {
        this.side = side;
        rectangleVertical.attachChild(sideLogo[(side == GameObject.Side.EMPIRE) ? 0 : 1]);
    }

    public void repaintPhaseName(String phaseName) {
        phaseText.setText(phaseName);
        phaseText.setX(timerText.getX()-phaseText.getWidth()-margin*4);
    }

    public void repaintBaseInfo(){
        WorldAccessor world = WorldAccessor.getInstance();
        int friendly = 0, enemy = 0;
        for (Base base: world.getBases().values()){
            if (base.getSide() == side){
                friendly++;
            } else {
                enemy++;
            }
        }
        enemyBaseInfo.setText(String.format(Locale.getDefault(), "%02d", enemy));
        friendlyBaseInfo.setText(String.format(Locale.getDefault(), "%02d", friendly));
    }

    public void repaintTime(int time) {
        timerText.setText("00:"+(time<10?"0"+time:time));
    }

    public void repaintMoney(int money) {
        moneyText.setText(money+"");
    }

    public void repaintShipInfo() {
        WorldAccessor world = WorldAccessor.getInstance();
        Fleet[] fleets = world.getAllFleets();
        for (int i=0; i<fleets.length; i++)
            if (fleets[i] != null) {
                shipCountTexts[i].setText(fleets[i].getShipCount()+"");
                shipEnergySprites[i].setWidth(size*fleets[i].getEnergy());
            } else {
                shipCountTexts[i].setText("0");
                shipEnergySprites[i].setWidth(0);
            }
    }

    private Rectangle createFleetSprite(int ind, String color, TextureLoader textureLoader,
                                        VertexBufferObjectManager vertexBufferObjectManager) {

        Font font = textureLoader.loadPanelTexture();

        shipCountTexts[ind] = new Text(0, 0, font, "00", vertexBufferObjectManager);

        Rectangle rectangle = new Rectangle(0, 0, size+gap+size/2, size+progressHeight+gap,
                vertexBufferObjectManager);
        rectangle.setColor(Color.TRANSPARENT);

        Sprite shipSprite = new Sprite(0, progressHeight+gap,
                textureLoader.loadColoredShipTexture(color), vertexBufferObjectManager);
        shipSprite.setSize(size, size);
        rectangle.attachChild(shipSprite);

        Sprite shipMaxEnergySprite = new Sprite(0, 0,
                textureLoader.loadEmptyTexture(new Color(0.2f, 0.2f, 0.2f).getARGBPackedInt()),
                vertexBufferObjectManager);
        shipMaxEnergySprite.setSize(size, progressHeight);
        rectangle.attachChild(shipMaxEnergySprite);

        shipEnergySprites[ind] = new Sprite(0, 0,
                textureLoader.loadEmptyTexture(android.graphics.Color.BLUE),
                vertexBufferObjectManager);
        shipEnergySprites[ind].setSize(0, progressHeight);
        rectangle.attachChild(shipEnergySprites[ind]);

        shipCountTexts[ind].setPosition(shipSprite.getWidth()+gap,
                rectangle.getHeight()-shipCountTexts[ind].getHeight());
        rectangle.attachChild(shipCountTexts[ind]);

        return rectangle;
    }

    public void setButtonEnabled(boolean enabled) {
        endPhaseButton.setEnabled(enabled);
    }

    private Sprite createLogoSprite(String logo, float margin){
        Sprite result = new Sprite(0, 0, textureLoader.loadLogoTexture(logo), vertexBufferObjectManager);
        result.setSize(size*2, size*2);
        result.setPosition(margin, rectangleVertical.getHeight() - result.getHeight() - margin);
        return result;
    }

    private Sprite createBaseSprite(Node.SystemType color, float margin){
        Sprite result = new Sprite(0, 0, textureLoader.loadSystemTexture(color), vertexBufferObjectManager);
        result.setSize(size, size);
        result.setPosition(rectangleVertical.getWidth() + PxDpConverter.dpToPx(20) + margin,
                (rectangleHorizontal.getHeight()-size)/2);
        result.setColor((color == Node.SystemType.FRIENDLY) ? Color.GREEN: Color.RED);
        return result;
    }
}
