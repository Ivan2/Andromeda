package com.games.andromeda.ui.sprites;

import android.view.MotionEvent;

import com.games.andromeda.graph.Node;

import org.andengine.entity.modifier.LoopEntityModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public abstract class SystemSprite extends Sprite {

    public abstract void onMove();
    public abstract void onUp();

    private Node node;

    private LoopEntityModifier modifier;

    public SystemSprite(Node node, float pX, float pY, ITextureRegion pTextureRegion,
                        VertexBufferObjectManager pVertexBufferObjectManager) {
        super(pX, pY, pTextureRegion, pVertexBufferObjectManager);
        this.node = node;

        setScaleCenter(getWidth()/8, getHeight()/8);
        modifier = new LoopEntityModifier(new SequenceEntityModifier(
                new ScaleModifier(1, 1f, 1.2f),
                new ScaleModifier(1, 1.2f, 1f)
        ));
    }

    public void activate(){
        registerEntityModifier(modifier);
    }

    public void deactivate(){
        clearEntityModifiers();
        setScale(1);
    }

    @Override
    public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
        switch (pSceneTouchEvent.getMotionEvent().getAction()) {
            //case MotionEvent.ACTION_DOWN:
                //MainActivity.selectedNodes.clear();
                //MainActivity.selectedNodes.add(node);
            //    break;
            case MotionEvent.ACTION_MOVE:
                onMove();
                /*if (!MainActivity.selectedNodes.isEmpty())
                    if (MainActivity.selectedNodes.getLast().equals(node))
                        break;
                MainActivity.selectedNodes.add(node);*/
                break;
            case MotionEvent.ACTION_UP:
                onUp();
                //Log.wtf("nodes", MainActivity.selectedNodes.toString());
                //TODO
                break;
        }
        return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
    }

}
