package com.games.andromeda.ui.layers;

import android.content.res.Resources;

import com.games.andromeda.PxDpConverter;
import com.games.andromeda.graph.Node;
import com.games.andromeda.logic.Fleet;
import com.games.andromeda.logic.WorldAccessor;
import com.games.andromeda.ui.texture.TextureLoader;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.color.Color;

import java.util.ArrayList;

public abstract class SystemInfoLayer extends DialogLayer {

    protected abstract void onOk();
    protected abstract void onCancel();

    private Node node;

    public SystemInfoLayer(Resources resources, Scene scene, Camera camera, TextureLoader textureLoader,
                           VertexBufferObjectManager vertexBufferObjectManager) {
        super(resources, scene, camera, textureLoader, vertexBufferObjectManager);
    }

    public void show(Node node) {
        this.node = node;
        repaint();
        setVisibility(true);
    }

    @Override
    public void repaint() {
        contentLayer.detachChildren();

        float WIDTH = contentLayer.getWidth();
        float HEIGHT = contentLayer.getHeight();

        Text titleText = new Text(0, 0, textureLoader.loadTitleDialogTexture(),
                "Система " + node.getSystemType(), vertexBufferObjectManager);
        titleText.setPosition((WIDTH-titleText.getWidth())/2, PxDpConverter.dpToPx(20));
        titleText.setColor(1, 1, 1);
        contentLayer.attachChild(titleText);

        String systemType = "нейтральная";
        if (node.getSystemType() == Node.SystemType.FRIENDLY)
            systemType = "дружественная";
        if (node.getSystemType() == Node.SystemType.ENEMY)
            systemType = "враждебная";
        Text subtitleText = new Text(0, 0, textureLoader.loadSubtitleDialogTexture(),
                "Тип: " + systemType, vertexBufferObjectManager);
        subtitleText.setPosition((WIDTH-subtitleText.getWidth())/2,
                titleText.getY()+titleText.getHeight()+PxDpConverter.dpToPx(5));
        subtitleText.setColor(1, 1, 1);
        contentLayer.attachChild(subtitleText);


        ButtonSprite okButton = new ButtonSprite(0, 0,
                textureLoader.loadEmptyTexture(android.graphics.Color.TRANSPARENT),
                vertexBufferObjectManager);
        okButton.setOnClickListener(new ButtonSprite.OnClickListener() {
            @Override
            public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX, float pTouchAreaLocalY) {
                setVisibility(false);
                onOk();
            }
        });
        contentLayer.attachChild(okButton);
        scene.registerTouchArea(okButton);

        Text okText = new Text(0, 0, textureLoader.loadDialogTexture(), "OK", vertexBufferObjectManager);
        okText.setColor(1, 1, 1);
        okText.setHorizontalAlign(HorizontalAlign.CENTER);
        okButton.attachChild(okText);
        okButton.setSize(PxDpConverter.dpToPx(100), okText.getHeight()+PxDpConverter.dpToPx(10));
        okText.setPosition((okButton.getWidth() - okText.getWidth())/2,
                (okButton.getHeight() - okText.getHeight())/2);

        okButton.setPosition((WIDTH-okButton.getWidth())/2,
                HEIGHT-okButton.getHeight()-PxDpConverter.dpToPx(20));


        Rectangle parent = new Rectangle(0,
                subtitleText.getY()+subtitleText.getHeight()+PxDpConverter.dpToPx(10),
                WIDTH,
                HEIGHT-(subtitleText.getY()+subtitleText.getHeight()+PxDpConverter.dpToPx(10))-
                (HEIGHT-okButton.getY()),
                vertexBufferObjectManager);
        parent.setColor(Color.TRANSPARENT);

        switch (node.getSystemType()) {
            case FRIENDLY:
                createFriendlySystem(parent);
                break;
            case ENEMY:
                createEnemySystem(parent);
                break;
            case EMPTY:
            case MINI:
            case HYPER:
                createEmptySystem(parent);
                break;
        }
        contentLayer.attachChild(parent);
    }

    private void createEmptySystem(Rectangle parent) {
        float margin = PxDpConverter.dpToPx(20);

        ///Создание кнопки постройки базы
        ButtonSprite buildButton = new ButtonSprite(0, 0, textureLoader.loadBuildTexture(),
                vertexBufferObjectManager);
        buildButton.setOnClickListener(new ButtonSprite.OnClickListener() {
            @Override
            public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX, float pTouchAreaLocalY) {
                //TODO построить базу
            }
        });
        buildButton.setSize(PxDpConverter.dpToPx(50), PxDpConverter.dpToPx(50));
        buildButton.setPosition(margin, parent.getHeight()-buildButton.getHeight()-margin);
        parent.attachChild(buildButton);
        scene.registerTouchArea(buildButton);


        ///Создание картинки с системой
        Sprite systemSprite = new Sprite(0, 0, textureLoader.loadEmptySystemTexture(),
                vertexBufferObjectManager);
        float size = parent.getWidth()/2 - margin*2;
        if (parent.getHeight() - margin - (parent.getHeight()-buildButton.getY()) < size)
            size = parent.getHeight() - margin - (parent.getHeight()-buildButton.getY());
        systemSprite.setSize(size, size);
        systemSprite.setPosition((parent.getWidth()/2-size)/2,
                (parent.getHeight()-(parent.getHeight()-buildButton.getY())-size)/2);

        parent.attachChild(systemSprite);


        float shipRowHeight = PxDpConverter.dpToPx(90);

        ArrayList<Fleet> fleets = new ArrayList<>(3);
        for (Fleet fleet : WorldAccessor.getInstance().getAllFleets())
            if (fleet != null && fleet.getPosition().equals(node))
                fleets.add(fleet);

        for (int i=0; i<fleets.size(); i++) {
            Fleet fleet = fleets.get(i);

            Rectangle shipRow = new Rectangle(
                    parent.getWidth()/2+margin,
                    margin*(i+1)+shipRowHeight*i,
                    parent.getWidth()/2-margin*2,
                    shipRowHeight,
                    vertexBufferObjectManager
            );
            shipRow.setColor(Color.TRANSPARENT);
            parent.attachChild(shipRow);

            createShipRow(shipRow, fleet);
        }
    }

    private void createShipRow(Rectangle shipRow, Fleet fleet) {
        float shipSize = PxDpConverter.dpToPx(70);

        Sprite shipSprite = new Sprite(0, (shipRow.getHeight()-shipSize)/2,
                textureLoader.loadColoredShipTexture("red"),
                vertexBufferObjectManager);
        shipSprite.setSize(shipSize, shipSize);
        shipRow.attachChild(shipSprite);

        ITextureRegion shipCountTexture = textureLoader.loadColoredShipTexture("red");
        Sprite shipCountSprite = new Sprite(shipSize+PxDpConverter.dpToPx(20),
                shipSprite.getY(), shipCountTexture, vertexBufferObjectManager);
        shipCountSprite.setSize(PxDpConverter.dpToPx(30), PxDpConverter.dpToPx(30));

        Font font = textureLoader.loadPanelTexture();
        Text shipCountText = new Text(
                shipCountSprite.getX()+shipCountSprite.getWidth()+PxDpConverter.dpToPx(10),
                shipCountSprite.getY(),
                font,
                fleet.getShipCount()+"",
                vertexBufferObjectManager
        );

        ITextureRegion energyTexture = textureLoader.loadEnergyTexture();
        Sprite energySprite = new Sprite(shipSize+PxDpConverter.dpToPx(20),
                shipCountSprite.getY()+shipCountSprite.getHeight()+PxDpConverter.dpToPx(10),
                energyTexture, vertexBufferObjectManager);
        energySprite.setSize(PxDpConverter.dpToPx(30), PxDpConverter.dpToPx(30));

        Text energyText = new Text(
                energySprite.getX()+energySprite.getWidth()+PxDpConverter.dpToPx(10),
                energySprite.getY(),
                font,
                fleet.getEnergy()+"",
                vertexBufferObjectManager
        );

        shipRow.attachChild(shipCountSprite);
        shipRow.attachChild(shipCountText);
        shipRow.attachChild(energySprite);
        shipRow.attachChild(energyText);

/*
        ///Создание кнопки патча
        ButtonSprite patchButton = new ButtonSprite(0, 0, textureLoader.loadPatchTexture(),
                vertexBufferObjectManager);
        patchButton.setOnClickListener(new ButtonSprite.OnClickListener() {
            @Override
            public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX, float pTouchAreaLocalY) {
                //TODO
            }
        });
        patchButton.setSize(PxDpConverter.dpToPx(50), PxDpConverter.dpToPx(50));
        patchButton.setPosition(shipRow1.getWidth()-patchButton.getWidth(),
                (shipRow1.getHeight()-patchButton.getHeight())/2);
        shipRow1.attachChild(patchButton);
        scene.registerTouchArea(patchButton);*/
    }

    private void createFriendlySystem(Rectangle parent) {
        float margin = PxDpConverter.dpToPx(20);

        ///Создание кнопки постройки базы
        ButtonSprite patchBaseButton = new ButtonSprite(0, 0, textureLoader.loadPatchTexture(),
                vertexBufferObjectManager);
        patchBaseButton.setOnClickListener(new ButtonSprite.OnClickListener() {
            @Override
            public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX, float pTouchAreaLocalY) {
                //TODO построить базу
            }
        });
        patchBaseButton.setSize(PxDpConverter.dpToPx(50), PxDpConverter.dpToPx(50));
        patchBaseButton.setPosition(margin, parent.getHeight()-patchBaseButton.getHeight()-margin);
        parent.attachChild(patchBaseButton);
        scene.registerTouchArea(patchBaseButton);


        ///Создание картинки с системой
        Sprite systemSprite = new Sprite(0, 0, textureLoader.loadFriendlySystemTexture(),
                vertexBufferObjectManager);
        float size = parent.getWidth()/2 - margin*2;
        if (parent.getHeight() - margin - (parent.getHeight()-patchBaseButton.getY()) < size)
            size = parent.getHeight() - margin - (parent.getHeight()-patchBaseButton.getY());
        systemSprite.setSize(size, size);
        systemSprite.setPosition((parent.getWidth()/2-size)/2,
                (parent.getHeight()-(parent.getHeight()-patchBaseButton.getY())-size)/2);

        parent.attachChild(systemSprite);


        float shipRowHeight = PxDpConverter.dpToPx(150);

        ArrayList<Fleet> fleets = new ArrayList<>(3);
        for (Fleet fleet : WorldAccessor.getInstance().getAllFleets())
            if (fleet.getPosition().equals(node))
                fleets.add(fleet);

        for (int i=0; i<fleets.size(); i++) {
            Fleet fleet = fleets.get(i);

            Rectangle shipRow = new Rectangle(
                    parent.getWidth()/2+margin,
                    margin*(i+1)+shipRowHeight*i,
                    parent.getWidth()/2-margin*2,
                    shipRowHeight,
                    vertexBufferObjectManager
            );
            shipRow.setColor(Color.TRANSPARENT);
            parent.attachChild(shipRow);

            createShipRow(shipRow, fleet);
        }
    }

    private void createEnemySystem(Rectangle parent) {
        float margin = PxDpConverter.dpToPx(20);

        ///Создание картинки с системой
        Sprite systemSprite = new Sprite(0, 0, textureLoader.loadEnemySystemTexture(),
                vertexBufferObjectManager);
        float size = parent.getHeight() - margin*2;
        systemSprite.setSize(size, size);
        systemSprite.setPosition((parent.getWidth()-size)/2,
                (parent.getHeight()-size)/2);

        parent.attachChild(systemSprite);
    }

}
