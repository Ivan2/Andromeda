package com.games.andromeda.layers;

import android.graphics.PointF;

import com.games.andromeda.graph.Node;
import com.games.andromeda.logic.Fleet;
import com.games.andromeda.logic.GameObject;
import com.games.andromeda.texture.TextureLoader;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.color.Color;



public class ShipsLayer extends Layer {

    private static float SHIP_SCALE = 0.3f; // масштаб иконки корабля
    private static int SHIP_MARGIN = 20;  // вынос иконки корабля относительно системы в px
    // разные флоты смещены внутри системы в разные стороны, чтобы не перекрывать друг друга
    private static double[] SHIP_ANGLES = {
        -2*Math.PI/3, Math.PI, 2*Math.PI/3, -Math.PI/3, 0, Math.PI/3
    };
    // первые 3 - цвета империи, последние 3 - федерации
    private static String[] SHIP_COLORS = {
        "red", "green", "blue", "gray", "pink", "brown"
    };

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

    private PointF[] deltas;  // запоминаем смещения
    private ITextureRegion[] textures; // текстуры тоже не изменятся
    private Sprite[] sprites; // спрайты просто прикрепляем/открепляем от слоя, их всегда 6
    private Fleet[] fleets;  // флоты - та часть, которая вносит изменения

    private Sprite activeSprite;

    public static abstract class LayerListener {
        public abstract void onClick(Node node);
        public abstract void onMove(Node node);
        public abstract void onUp(Node node);
    }

    private Rectangle layer;

    private LayerListener layerListener;

    public ShipsLayer(Scene scene, Camera camera, TextureLoader textureLoader,
                      VertexBufferObjectManager vertexBufferObjectManager) {
        super(scene, camera, textureLoader, vertexBufferObjectManager);

        // todo refacor this using magic layerListener ???
        layer = new Rectangle(0, 0, camera.getWidth(), camera.getHeight(),
                vertexBufferObjectManager){
            @Override
            public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
                if (pSceneTouchEvent.isActionMove()){
                    if (activeSprite != null){
                        activeSprite.setX(pSceneTouchEvent.getX() - activeSprite.getWidth() / 2);
                        activeSprite.setY(pSceneTouchEvent.getY() - activeSprite.getHeight() / 2);
                    }
                } else if (pSceneTouchEvent.isActionUp()){
                    // todo сделать ход, если возможно
                    activeSprite = null;
                    ShipsLayer.this.repaint();
                }
                return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
            }
        };
        layer.setColor(Color.TRANSPARENT);
        scene.attachChild(layer);
        scene.registerTouchArea(layer);

        deltas = new PointF[6];
        textures = new ITextureRegion[6];
        fleets = new Fleet[6];
        sprites = new Sprite[6];
        for(int i=0; i<6; ++i){
            deltas[i] = calc_delta(SHIP_ANGLES[i]);
            textures[i] = textureLoader.loadColoredShipTextire(SHIP_COLORS[i]);
            sprites[i] = new Sprite(0, 0, textures[i],vertexBufferObjectManager){
                @Override
                public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
                    if (pSceneTouchEvent.isActionDown()){
                        activeSprite = this;
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
        if ((fleets[idx] != null) && (activeSprite != sprites[idx]) ){
            Node node = fleets[idx].getPosition();
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
        fleets[idx] = fleet;
        moveToPosition(idx);
        layer.attachChild(sprites[idx]);
    }

    public void removeDeadFleets(){
        for(int i=0; i<6; ++i){
            if (fleets[i] != null){
                if (fleets[i].getShipCount() == 0){
                    fleets[i] = null;
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

}
