package com.games.andromeda.threads;

import com.games.andromeda.PxDpConverter;
import com.games.andromeda.ui.layers.ShipsLayer;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.Entity;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.input.touch.TouchEvent;

public class Scrolling implements Runnable {

    private Camera camera;
    private int screenWidth;
    private int screenHeight;
    private Entity scrollEntity;
    private ShipsLayer layer;
    private float mTouchX;
    private float mTouchY;

    public Scrolling(Camera camera, int screenWidth, int screenHeight,
                     int entityX, int entityY, ShipsLayer shipsLayer) {
        this.camera = camera;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        layer = shipsLayer;
        scrollEntity = new Entity(entityX, entityY);
        camera.setChaseEntity(scrollEntity);
    }

    public IOnSceneTouchListener getListener(){
        return new ScrollListener();
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
            }

            if (layer.isShipMoves()) {
                float x = scrollEntity.getX();
                float y = scrollEntity.getY();

                float dx = layer.getPos().x - scrollEntity.getX();
                float dy = layer.getPos().y - scrollEntity.getY();

                scrollEntity.setPosition(x + mTouchX * PxDpConverter.dpToPx(10),
                        y + mTouchY * PxDpConverter.dpToPx(10));

                if (scrollEntity.getX()+screenWidth/2 > camera.getWidth())
                    scrollEntity.setX(camera.getWidth()-screenWidth/2);
                if (scrollEntity.getY()+screenHeight/2 > camera.getHeight())
                    scrollEntity.setY(camera.getHeight()-screenHeight/2);
                if (scrollEntity.getX()-screenWidth/2 < 0)
                    scrollEntity.setX(screenWidth/2);
                if (scrollEntity.getY()-screenHeight/2 < 0)
                    scrollEntity.setY(screenHeight/2);

                layer.move(scrollEntity.getX()+dx, scrollEntity.getY()+dy);
            }
        }
    }

    private class ScrollListener implements IOnSceneTouchListener{
        @Override
        public boolean onSceneTouchEvent (Scene pScene, TouchEvent pSceneTouchEvent){
            if (pSceneTouchEvent.getMotionEvent().getRawX() < PxDpConverter.dpToPx(100) ||
                    pSceneTouchEvent.getMotionEvent().getRawX() > screenWidth - PxDpConverter.dpToPx(100))
                mTouchX = (pSceneTouchEvent.getMotionEvent().getRawX() - screenWidth / 2) / (screenWidth / 2);
            else
                mTouchX = 0;

            if (pSceneTouchEvent.getMotionEvent().getRawY() < PxDpConverter.dpToPx(100) ||
                    pSceneTouchEvent.getMotionEvent().getRawY() > screenHeight - PxDpConverter.dpToPx(100))
                mTouchY = (pSceneTouchEvent.getMotionEvent().getRawY() - screenHeight / 2) / (screenHeight / 2);
            else
                mTouchY = 0;

            if (pSceneTouchEvent.isActionMove() && pSceneTouchEvent.getMotionEvent().getHistorySize() > 0) {
                float dx = pSceneTouchEvent.getMotionEvent().getX() -
                        pSceneTouchEvent.getMotionEvent().getHistoricalX(0);
                float dy = pSceneTouchEvent.getMotionEvent().getY() -
                        pSceneTouchEvent.getMotionEvent().getHistoricalY(0);

                float x = scrollEntity.getX();
                float y = scrollEntity.getY();
                if (layer.isShipMoves())
                    scrollEntity.setPosition(x + dx, y + dy);
                else
                    scrollEntity.setPosition(x - dx, y - dy);

                if (scrollEntity.getX() + screenWidth / 2 > camera.getWidth())
                    scrollEntity.setX(camera.getWidth() - screenWidth / 2);
                if (scrollEntity.getY() + screenHeight / 2 > camera.getHeight())
                    scrollEntity.setY(camera.getHeight() - screenHeight / 2);
                if (scrollEntity.getX() - screenWidth / 2 < 0)
                    scrollEntity.setX(screenWidth / 2);
                if (scrollEntity.getY() - screenHeight / 2 < 0)
                    scrollEntity.setY(screenHeight / 2);
            }

            return true;
        }
    }
}
