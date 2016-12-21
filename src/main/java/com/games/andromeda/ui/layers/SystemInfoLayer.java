package com.games.andromeda.ui.layers;

import android.app.Activity;
import android.app.Dialog;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RadioButton;

import com.games.andromeda.Phases;
import com.games.andromeda.PxDpConverter;
import com.games.andromeda.R;
import com.games.andromeda.graph.Node;
import com.games.andromeda.logic.Fleet;
import com.games.andromeda.logic.GameObject;
import com.games.andromeda.logic.Pocket;
import com.games.andromeda.logic.Purchase;
import com.games.andromeda.logic.WorldAccessor;
import com.games.andromeda.logic.phases.MoneySpendingStrategy;
import com.games.andromeda.ui.UI;
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

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Locale;

public abstract class SystemInfoLayer extends DialogLayer {

    protected abstract void onOk();
    protected abstract void onCancel();
    protected MediaPlayer mediaPlayer;

    private Activity activity;
    private Node node;

    public SystemInfoLayer(Activity activity, Scene scene, Camera camera, TextureLoader textureLoader,
                           VertexBufferObjectManager vertexBufferObjectManager) {
        super(activity.getResources(), scene, camera, textureLoader, vertexBufferObjectManager);
        this.activity = activity;
    }

    public void hide() {
        setVisibility(false);
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

        String systemType = "Нейтральная";
        if (node.getSystemType() == Node.SystemType.FRIENDLY)
            systemType = "Дружественная";
        if (node.getSystemType() == Node.SystemType.ENEMY)
            systemType = "Враждебная";

        Text titleText = new Text(0, 0, textureLoader.loadTitleDialogTexture(),
                systemType + " система", vertexBufferObjectManager);
        titleText.setPosition((WIDTH-titleText.getWidth())/2, PxDpConverter.dpToPx(20));
        titleText.setColor(1, 1, 1);
        contentLayer.attachChild(titleText);

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
                titleText.getY()+titleText.getHeight()+PxDpConverter.dpToPx(10),
                WIDTH,
                HEIGHT-(titleText.getY()+titleText.getHeight()+PxDpConverter.dpToPx(10))-
                (HEIGHT-okButton.getY()),
                vertexBufferObjectManager);
        parent.setColor(Color.TRANSPARENT);

        createSystem(parent, node.getSystemType());
        contentLayer.attachChild(parent);
    }

    private void createSystem(Rectangle parent, Node.SystemType systemType) {
        float margin = PxDpConverter.dpToPx(20);

        float systemSpriteBottomMargin = 0;
        if (systemType == Node.SystemType.EMPTY) {
            ///Создание кнопки постройки базы
            ButtonSprite buildButton = new ButtonSprite(0, 0, textureLoader.loadBuildTexture(),
                    vertexBufferObjectManager);
            buildButton.setOnClickListener(new ButtonSprite.OnClickListener() {
                @Override
                public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX, float pTouchAreaLocalY) {
                    if (Phases.getInstance().getPhase() instanceof MoneySpendingStrategy) {
                        Purchase purchase = new Purchase(Purchase.Kind.BUILD_BASE, node);
                        try {
                            ((MoneySpendingStrategy) Phases.getInstance().getPhase()).handlePhaseEvent(purchase);
                            setVisibility(false);
                            String options = readFile();
                            if (options == null) {
                                mediaPlayer = MediaPlayer.create(Phases.getInstance().getActivity(), R.raw.get_money);
                                mediaPlayer.start();
                            }
                            else
                                if (options.equals("11")||(options.equals("01")))
                                {
                                    mediaPlayer = MediaPlayer.create(Phases.getInstance().getActivity(), R.raw.get_money);
                                    mediaPlayer.start();
                                }
                        } catch (Exception e) {
                            UI.toast(e.getMessage());
                        }
                    }
                }
            });
            buildButton.setSize(PxDpConverter.dpToPx(50), PxDpConverter.dpToPx(50));
            buildButton.setPosition(margin, parent.getHeight() - buildButton.getHeight() - margin);
            parent.attachChild(buildButton);
            scene.registerTouchArea(buildButton);
            systemSpriteBottomMargin = parent.getHeight()-buildButton.getY();
        }

        ITextureRegion textureRegion = textureLoader.loadEmptySystemTexture();
        if (systemType == Node.SystemType.FRIENDLY)
            textureRegion = textureLoader.loadFriendlySystemTexture();
        if (systemType == Node.SystemType.ENEMY)
            textureRegion = textureLoader.loadEnemySystemTexture();
        ///Создание картинки с системой
        Sprite systemSprite = new Sprite(0, 0, textureRegion, vertexBufferObjectManager);
        float size = parent.getWidth()/2 - margin*2;
        if (parent.getHeight() - margin - systemSpriteBottomMargin < size)
            size = parent.getHeight() - margin - systemSpriteBottomMargin;
        systemSprite.setSize(size, size);
        systemSprite.setPosition((parent.getWidth()/2-size)/2,
                (parent.getHeight()-systemSpriteBottomMargin-size)/2);

        parent.attachChild(systemSprite);


        float shipRowHeight = PxDpConverter.dpToPx(70);

        ArrayList<Fleet> fleets = new ArrayList<>(3);
        for (Fleet fleet : WorldAccessor.getInstance().getAllFleets())
            if (fleet != null && fleet.getPosition() == node.getId())
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

        if (fleets.size() < 3 && systemType == Node.SystemType.FRIENDLY) {
            ButtonSprite addShipRow = new ButtonSprite(
                    parent.getWidth()/2+margin,
                    margin*(fleets.size()+1)+shipRowHeight*fleets.size(),
                    textureLoader.loadEmptyTexture(new Color(1, 1, 1, 0.05f).getARGBPackedInt()),
                    vertexBufferObjectManager);
            addShipRow.setSize(parent.getWidth()/2-margin*2, shipRowHeight);
            addShipRow.setOnClickListener(new ButtonSprite.OnClickListener() {
                @Override
                public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX, float pTouchAreaLocalY) {
                    //TODO check (после первого нажатия иногда что-то ломается)
                    if (Phases.getInstance().getPhase() instanceof MoneySpendingStrategy) {
                        try {
                            ((MoneySpendingStrategy) Phases.getInstance().getPhase()).
                                    handlePhaseEvent(new Purchase(Purchase.Kind.BUY_FLEET, node));
                            setVisibility(false);
                            String options = readFile();
                            if (options == null) {
                                mediaPlayer = MediaPlayer.create(Phases.getInstance().getActivity(), R.raw.get_money);
                                mediaPlayer.start();
                            }
                            else
                            if (options.equals("11")||(options.equals("01")))
                            {
                                mediaPlayer = MediaPlayer.create(Phases.getInstance().getActivity(), R.raw.get_money);
                                mediaPlayer.start();
                            }
                        } catch (Exception e) {
                            UI.toast(e.getMessage());
                        }
                    }
                }
            });
            parent.attachChild(addShipRow);
            scene.registerTouchArea(addShipRow);

            Font font = textureLoader.loadDialogTexture();
            Text text = new Text(margin, (addShipRow.getHeight()-font.getLineHeight())/2,
                    font, "Создать флот", vertexBufferObjectManager);
            addShipRow.attachChild(text);
        }
    }

    private void createShipRow(Rectangle shipRow, final Fleet fleet) {
        float shipSize = PxDpConverter.dpToPx(70);

        String color = (fleet.getSide() == GameObject.Side.EMPIRE) ? "red1" : "green1";
        Sprite shipSprite = new Sprite(0, (shipRow.getHeight()-shipSize)/2,
                textureLoader.loadColoredShipTexture(color),
                vertexBufferObjectManager);
        shipSprite.setSize(shipSize, shipSize);
        shipRow.attachChild(shipSprite);

        ITextureRegion shipCountTexture = textureLoader.loadColoredShipTexture(color);
        Sprite shipCountSprite = new Sprite(shipSize+PxDpConverter.dpToPx(20),
                shipSprite.getY(), shipCountTexture, vertexBufferObjectManager);
        shipCountSprite.setSize(PxDpConverter.dpToPx(30), PxDpConverter.dpToPx(30));

        Font font = textureLoader.loadPanelTexture();
        final Text shipCountText = new Text(
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
                String.format(Locale.ENGLISH, "%.2f%%", fleet.getEnergy()*100),
                vertexBufferObjectManager
        );

        shipRow.attachChild(shipCountSprite);
        shipRow.attachChild(shipCountText);
        shipRow.attachChild(energySprite);
        shipRow.attachChild(energyText);


        if (fleet.getSide() == Phases.getInstance().side) {
            ///Создание кнопки патча
            ButtonSprite patchButton = new ButtonSprite(0, 0, textureLoader.loadPatchTexture(),
                    vertexBufferObjectManager);
            patchButton.setOnClickListener(new ButtonSprite.OnClickListener() {
                @Override
                public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX, float pTouchAreaLocalY) {
                    //TODO
                    if (fleet.getSide() != Phases.getInstance().side)
                        return;

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            final Dialog dialog = new Dialog(activity);
                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            dialog.setContentView(R.layout.buy_dialog);

                            final RadioButton radioButton1 = (RadioButton) dialog.findViewById(R.id.count1_radio_button);
                            radioButton1.setText("1 корабль (" + fleet.getOneShipCost()+")");
                            final RadioButton radioButton2 = (RadioButton) dialog.findViewById(R.id.count2_radio_button);
                            radioButton2.setText("3 корабля (" + fleet.getOneShipCost()*3 + ")");
                            final RadioButton radioButton3 = (RadioButton) dialog.findViewById(R.id.count3_radio_button);
                            radioButton3.setText("5 кораблей (" + fleet.getOneShipCost()*5 + ")");
                            Button okButton = (Button)dialog.findViewById(R.id.ok_button);
                            Button cancelButton = (Button)dialog.findViewById(R.id.cancel_button);

                            okButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    try {
                                        Pocket pocket = WorldAccessor.getInstance().getPocket(fleet.getSide());
                                        int shipCount = 0;
                                        if (radioButton1.isChecked())
                                            shipCount = 1;
                                        if (radioButton2.isChecked())
                                            shipCount = 3;
                                        if (radioButton3.isChecked())
                                            shipCount = 5;
                                        fleet.buyShips(shipCount, pocket);
                                        ((MoneySpendingStrategy) Phases.getInstance().getPhase())
                                                .handlePhaseEvent(new Purchase(fleet, node));
                                        shipCountText.setText(fleet.getShipCount()+"");
                                        UI.getInstance().getPanel().repaintShipInfo();
                                    } catch (Exception e) {
                                        UI.toast(e.getMessage());
                                    }
                                    dialog.dismiss();
                                }
                            });

                            cancelButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialog.dismiss();
                                }
                            });

                            dialog.show();
                        }
                    });

                }
            });
            patchButton.setSize(PxDpConverter.dpToPx(50), PxDpConverter.dpToPx(50));
            patchButton.setPosition(shipRow.getWidth() - patchButton.getWidth(),
                    (shipRow.getHeight() - patchButton.getHeight()) / 2);
            shipRow.attachChild(patchButton);
            scene.registerTouchArea(patchButton);
        }
    }

    private String readFile() {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    activity.openFileInput("options")));
            String str = "";
            str = br.readLine();
            return str;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
