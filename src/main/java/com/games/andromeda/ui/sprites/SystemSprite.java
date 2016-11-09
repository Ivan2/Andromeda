package com.games.andromeda.ui.sprites;

import android.view.MotionEvent;

import com.games.andromeda.graph.Node;

import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public abstract class SystemSprite extends ButtonSprite {

    public abstract void onClick();
    public abstract void onMove();
    public abstract void onUp();

    private Node node;

    public SystemSprite(Node node, float pX, float pY, ITextureRegion pTextureRegion,
                        VertexBufferObjectManager pVertexBufferObjectManager) {
        super(pX, pY, pTextureRegion, pVertexBufferObjectManager);
        this.node = node;
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX, float pTouchAreaLocalY) {
                SystemSprite.this.onClick();
                //runnable.run();
            }
        });
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
