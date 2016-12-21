package com.games.andromeda.ui.layers;

import android.graphics.PointF;
import android.util.Log;

import com.games.andromeda.Phases;
import com.games.andromeda.PxDpConverter;
import com.games.andromeda.graph.Node;
import com.games.andromeda.graph.PathInfo;
import com.games.andromeda.logic.Fleet;
import com.games.andromeda.logic.FleetObserver;
import com.games.andromeda.logic.GameObject;
import com.games.andromeda.logic.WorldAccessor;
import com.games.andromeda.logic.phases.FleetMovingStrategy;
import com.games.andromeda.ui.UI;
import com.games.andromeda.ui.sprites.ShipSprite;
import com.games.andromeda.ui.texture.TextureLoader;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.PathModifier;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.color.Color;
import org.andengine.util.modifier.ease.EaseCircularInOut;


public class ShipsLayer extends Layer implements FleetObserver {

    public interface IOnFleetMove {
        void onFleetMove(Fleet fleet, int num);
    }
    public interface IOnFleetFight
    {
        void onFleetFight(Fleet attackingFleet, Fleet anotherFleet, int number, int secondNumber);
    }

    private static float SHIP_SIZE = PxDpConverter.dpToPx(35); // размер иконки корабля
    private static float SHIP_MARGIN = PxDpConverter.dpToPx(60);  // вынос иконки корабля относительно системы в px
//    private static float SHIP_SCALE = 0.3f; // масштаб иконки корабля
    // разные флоты смещены внутри системы в разные стороны, чтобы не перекрывать друг друга
    private static double[] SHIP_ANGLES = {
        -2*Math.PI/3, Math.PI, 2*Math.PI/3, -Math.PI/3, 0, Math.PI/3
    };
    // первые 3 - цвета империи, последние 3 - федерации
    public static String[] SHIP_COLORS = {
        //"red", "green", "blue", "gray", "pink", "brown"
        "yellow1", "yellow2", "yellow3", "blue1", "blue2", "blue3"
    };

    private PointF[] deltas;  // запоминаем смещения
    private ITextureRegion[] textures; // текстуры тоже не изменятся
    private ShipSprite[] sprites; // спрайты просто прикрепляем/открепляем от слоя, их всегда 6
//    private Fleet[] fleets;  // флоты - та часть, которая вносит изменения


    public static ShipSprite activeSprite;

    //private PathBuilder pathManager;

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
    public void onFleetChanged(Fleet fleet) {
        addFleet(fleet);
    }

    public static abstract class LayerListener {
        public abstract void onClick(Fleet fleet);
        public abstract void onCancel();
    }


    public ShipsLayer(Scene scene, Camera camera, TextureLoader textureLoader,
                      VertexBufferObjectManager vertexBufferObjectManager,// final PathBuilder manager,
                      final IOnFleetMove onFleetMove, final IOnFleetFight onFleetFight) {
        super(scene, camera, textureLoader, vertexBufferObjectManager);


        //pathManager = manager;

        layer = new Rectangle(0, 0, camera.getWidth(), camera.getHeight(), vertexBufferObjectManager);
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
                        if (pSceneTouchEvent.isActionUp()) {
                            if (this.getFleet().getSide() == Phases.getInstance().side) {
                                if (activeSprite == null) {
                                    UI.toast("Выберите систему для перемещения флота");
                                    setActive(this);
                                    if (layerListener != null){
                                        layerListener.onClick(this.getFleet());
                                    }
                                } else {
                                    releaseSprite();
                                    if (layerListener != null) {
                                        layerListener.onCancel();
                                    }
                                }
                            } else {
                                UI.toast("Вы не можете перемещать не свои флоты");
                            }
                        }
                    return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
                }
            };
//            sprites[i].setSize(SHIP_SIZE, SHIP_SIZE);
//            sprites[i].setScale(SHIP_SCALE);
            sprites[i].resize(SHIP_SIZE);
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
            PointF point = calcPosition(node, idx);
            sprites[idx].setX(point.x);
            sprites[idx].setY(point.y);
            sprites[idx].rotate();
        }
    }

    private PointF calcPosition(Node node, int idx){
        PointF point = getPos(node.getX(), node.getY());
        float x = point.x + deltas[idx].x - sprites[idx].getWidth() / 2;
        float y = point.y + deltas[idx].y - sprites[idx].getHeight() / 2;
        point.set(x, y);
        return point;
    }

    /**
     * Добавляем флот на слой
     * @param fleet - "логический флот"
     */
    public void addFleet(Fleet fleet){
        // сначала хранятся имперские флоты, потом флоты федерации
        int idx = getIdx(fleet);
        // todo бросить исключение при перезаписи или выходе за рамки 1..3
        sprites[idx].setFleet(fleet);
        moveToPosition(idx);
        Log.wtf("idx = ","" + idx);
        layer.attachChild(sprites[idx]);
    }

    private int getIdx(GameObject.Side side, int id){
        return (side == GameObject.Side.EMPIRE ? -1 : 2) + id;
    }

    private int getIdx(Fleet fleet){
        return getIdx(fleet.getSide(), fleet.getId());
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

    public void moveFleetRemotely(PathInfo path, GameObject.Side side, int fleetId){
        activeSprite = sprites[getIdx(side, fleetId)];
        try {
            moveFleet(path);
        } catch (Exception e){
            Log.wtf("moveFleetRemotely", "Прислана дичь. " + e);
        }
        activeSprite = null;
    }

    public void moveFleet(final PathInfo path) throws Fleet.NotEnoughEnergyException,
            Fleet.InvalidPositionException, Fleet.InvalidPathException {
        final ShipSprite sprite = activeSprite;
        if (sprite != null) {
            sprite.getFleet().makeMove(path);
            sprite.clearEntityModifiers();
            sprite.registerEntityModifier(new PathModifier(0.5f * path.getNodeIds().size(),
                    convertPath(path), new PathModifier.IPathModifierListener() {
                @Override
                public void onPathStarted(PathModifier pPathModifier, IEntity pEntity) {
                }

                @Override
                public void onPathWaypointStarted(PathModifier pPathModifier, IEntity pEntity, int pWaypointIndex) {
                    if (pWaypointIndex < path.getNodeIds().size() - 1) {
                        Node old = getPathNode(pWaypointIndex);
                        Node current = getPathNode(pWaypointIndex + 1);
                        sprite.rotate(old.getX(), old.getY(), current.getX(), current.getY());
                    }
                }

                private Node getPathNode(int idx) {
                    return WorldAccessor.getInstance().getNodes().get(path.getNodeIds().get(idx));
                }

                @Override
                public void onPathWaypointFinished(PathModifier pPathModifier, IEntity pEntity, int pWaypointIndex) {

                }

                @Override
                public void onPathFinished(PathModifier pPathModifier, IEntity pEntity) {

                }
            }, EaseCircularInOut.getInstance()));
        }
    }

    public PathModifier.Path convertPath(PathInfo path){
        PathModifier.Path result = new PathModifier.Path(path.getNodeIds().size());
        for(int id: path.getNodeIds()){
            Node node = WorldAccessor.getInstance().getNodes().get(id);
            PointF point = calcPosition(node, getIdx(activeSprite.getFleet()));
            result.to(point.x, point.y);
        }
        return result;
    }

    @Override
    public void repaint() {
        removeDeadFleets();
        for (int i=0; i<6; ++i){
            moveToPosition(i);
        }
        //TODO удалить все спрайты со слоя
    }

    public void setActive(ShipSprite sprite){
        activeSprite = sprite;
        sprite.activate();
    }

    public void releaseSprite(){
        if (activeSprite != null){
            activeSprite.deactivate();
            activeSprite = null;
        }
    }

    public void setLayerListener(LayerListener layerListener) {
        this.layerListener = layerListener;
    }
//
//    public boolean isShipMoves() {
//        return (activeSprite != null);
//    }
//
//    public PointF getPos() {
//        return new PointF(activeSprite.getX(), activeSprite.getY());
//    }
//
//    public void move(float x, float y) {
//        activeSprite.setX(x);
//        activeSprite.setY(y);
//    }

}


