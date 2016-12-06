package com.games.andromeda.ui.layers;

import android.graphics.PointF;
import android.util.Log;

import com.games.andromeda.Phases;
import com.games.andromeda.graph.Node;
import com.games.andromeda.logic.Fleet;
import com.games.andromeda.logic.FleetObserver;
import com.games.andromeda.logic.GameObject;
import com.games.andromeda.logic.WorldAccessor;
import com.games.andromeda.logic.phases.FleetMovingStrategy;
import com.games.andromeda.ui.UI;
import com.games.andromeda.ui.sprites.ShipSprite;
import com.games.andromeda.ui.texture.TextureLoader;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.color.Color;


public class ShipsLayer extends Layer implements FleetObserver {

    public interface IOnFleetMove {
        void onFleetMove(Fleet fleet, int num);
    }
    public interface IOnFleetFight
    {
        void onFleetFight(Fleet attackingFleet, Fleet anotherFleet, int number, int secondNumber);
    }
    private static float SHIP_SCALE = 0.3f; // масштаб иконки корабля
    private static int SHIP_MARGIN = 60;  // вынос иконки корабля относительно системы в px
    // разные флоты смещены внутри системы в разные стороны, чтобы не перекрывать друг друга
    private static double[] SHIP_ANGLES = {
        -2*Math.PI/3, Math.PI, 2*Math.PI/3, -Math.PI/3, 0, Math.PI/3
    };
    // первые 3 - цвета империи, последние 3 - федерации
    public static String[] SHIP_COLORS = {
        "red", "green", "blue", "gray", "pink", "brown"
    };

    private PointF[] deltas;  // запоминаем смещения
    private ITextureRegion[] textures; // текстуры тоже не изменятся
    private ShipSprite[] sprites; // спрайты просто прикрепляем/открепляем от слоя, их всегда 6
//    private Fleet[] fleets;  // флоты - та часть, которая вносит изменения


    public static ShipSprite activeSprite;

    //private PathManager pathManager;

    private Rectangle layer;

    private LayerListener layerListener;

    /**
     * Получение координат смещения иконки
     * @param angle угол смещения
     * @return разица в координатах относительно центра Node
     */
    private static PointF calc_delta(double angle){
        return new PointF(
                (float)(SHIP_MARGIN*Math.cos(angle)),
                (float)(SHIP_MARGIN*Math.sin(angle)));
    }

    @Override
    public void onFleetChanged(Fleet fleet, int idx) {
        addFleet(fleet, idx);
    }

    public static abstract class LayerListener {
        public abstract void onClick(Node node);
        public abstract void onMove(Node node);
        public abstract void onUp(Node node);
    }


    public ShipsLayer(Scene scene, Camera camera, TextureLoader textureLoader,
                      VertexBufferObjectManager vertexBufferObjectManager,// final PathManager manager,
                      final IOnFleetMove onFleetMove, final IOnFleetFight onFleetFight) {
        super(scene, camera, textureLoader, vertexBufferObjectManager);

        //pathManager = manager;

        // todo refacor this using magic layerListener ???
        layer = new Rectangle(0, 0, camera.getWidth(), camera.getHeight(),
                vertexBufferObjectManager){
            @Override
            public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
                /*if (activeSprite != null) {
                    //if (pSceneTouchEvent.isActionMove()) {
                        activeSprite.setX(pSceneTouchEvent.getX() - activeSprite.getWidth() / 2);
                        activeSprite.setY(pSceneTouchEvent.getY() - activeSprite.getHeight() / 2);
                    } else if (pSceneTouchEvent.isActionUp()) {
                        // todo сделать ход, если возможно
                        try {
                            activeSprite.getFleet().makeMove(manager.getPath());
                            WorldAccessor world = WorldAccessor.getInstance();
                            int fleetNum = -1;
                            for (int i = 0; i < 6; i++) {
                                if (world.getAllFleets()[i] == activeSprite.getFleet()) {
                                    if (i > 2) i -=3;
                                    fleetNum = i+1;
                                    onFleetMove.onFleetMove(activeSprite.getFleet(), fleetNum);
                                    break;
                                }
                            }
                            for (int i = 0; i < 6; i++) {
                                if (world.getAllFleets()[i] != null && world.getAllFleets()[i] != activeSprite.getFleet() &&
                                        world.getAllFleets()[i].getPosition() == activeSprite.getFleet().getPosition() &&
                                        activeSprite.getFleet().getSide() != world.getAllFleets()[i].getSide()
                                        ) {
                                    if (i > 2) i -=3;
                                    onFleetFight.onFleetFight(activeSprite.getFleet(),world.getAllFleets()[i],fleetNum,i+1);
                                    break;
                                }
                            }
                        } catch (Exception e) {
                            Log.wtf("PATH", e.toString());
                            e.printStackTrace();
                        }
                        activeSprite = null;
                        manager.reset();
                        ShipsLayer.this.repaint();
                    }
                }*/
                return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
            }
        };
        layer.setColor(Color.TRANSPARENT);
        scene.attachChild(layer);
        scene.registerTouchArea(layer);

        deltas = new PointF[6];
        textures = new ITextureRegion[6];
//        fleets = new Fleet[6];
        sprites = new ShipSprite[6];
        for(int i=0; i<6; ++i){
            deltas[i] = calc_delta(SHIP_ANGLES[i]);
            textures[i] = textureLoader.loadColoredShipTexture(SHIP_COLORS[i]);
            sprites[i] = new ShipSprite(0, 0, textures[i],vertexBufferObjectManager){
                @Override
                public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
                    if (Phases.getInstance().getPhase() instanceof FleetMovingStrategy)
                        if (pSceneTouchEvent.isActionUp()){
                            if (activeSprite == null) {
                                UI.toast("Выберите систему для перемещения флота");
                                activeSprite = this;
                            } else
                                activeSprite = null;
    //                        activeSprite.getFleet().setEnergy(100500);
                            //manager.start(this.getFleet());
                        }
                    return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
                }
            };
            sprites[i].setScale(SHIP_SCALE);
            scene.registerTouchArea(sprites[i]);
        }
    }

    /**
     * Приводим координаты спрайта в соответствие с "адресом" флота
     * @param idx - индекс флота (0..5)
     */
    private void moveToPosition(int idx){
        if ((sprites[idx].getFleet() != null) && (activeSprite != sprites[idx]) ){
            Node node = WorldAccessor.getInstance().getNodes().get(sprites[idx].getFleet().getPosition());
            PointF point = getPos(node.getX(), node.getY());
            float x = point.x + deltas[idx].x - sprites[idx].getWidth() / 2;
            float y = point.y + deltas[idx].y - sprites[idx].getHeight() / 2;
            sprites[idx].setX(x);
            sprites[idx].setY(y);
        }
    }

    /**
     * Добавляем флот на слой
     * @param fleet - "логический флот"
     * @param number - номер флота (от 1 до 3)
     */
    public void addFleet(Fleet fleet, int number){
        // сначала хранятся имперские флоты, потом флоты федерации
        int idx = (fleet.getSide() == GameObject.Side.EMPIRE ? -1 : 2) + number;
        // todo бросить исключение при перезаписи или выходе за рамки 1..3
        sprites[idx].setFleet(fleet);
        moveToPosition(idx);
        Log.wtf("idx = ","" + idx);
        layer.attachChild(sprites[idx]);
    }

    public void removeDeadFleets(){
        for(int i=0; i<6; ++i){
            if (sprites[i].getFleet() != null){
                if (sprites[i].getFleet().getShipCount() == 0){
                    sprites[i].setFleet(null);
                    layer.detachChild(sprites[i]);
                }
            } else {
                if (sprites[i].getFleet() != null) {
                    sprites[i].setFleet(null);
                    layer.detachChild(sprites[i]);
                }
            }
        }
    }

    @Override
    public void repaint() {
        removeDeadFleets();
        for (int i=0; i<6; ++i){
            moveToPosition(i);
        }
        //TODO удалить все спрайты со слоя
    }

    public void setLayerListener(LayerListener layerListener) {
        this.layerListener = layerListener;
    }

    public boolean isShipMoves() {
        return (activeSprite != null);
    }

    public PointF getPos() {
        return new PointF(activeSprite.getX(), activeSprite.getY());
    }

    public void move(float x, float y) {
        activeSprite.setX(x);
        activeSprite.setY(y);
    }

}


