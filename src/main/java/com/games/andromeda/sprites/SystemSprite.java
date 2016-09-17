package com.games.andromeda.sprites;

import android.util.Log;
import android.view.MotionEvent;

import com.games.andromeda.MainActivity;
import com.games.andromeda.graph.Node;

import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public class SystemSprite extends ButtonSprite {

    private Node node;

    public SystemSprite(Node node, final Runnable runnable, float pX, float pY, ITextureRegion pTextureRegion, VertexBufferObjectManager pVertexBufferObjectManager) {
        super(pX, pY, pTextureRegion, pVertexBufferObjectManager);
        this.node = node;
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX, float pTouchAreaLocalY) {
                runnable.run();
            }
        });
    }

    @Override
    public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
        switch (pSceneTouchEvent.getMotionEvent().getAction()) {
            case MotionEvent.ACTION_DOWN:
                MainActivity.selectedNodes.clear();
                MainActivity.selectedNodes.add(node);
                break;
            case MotionEvent.ACTION_MOVE:
                if (!MainActivity.selectedNodes.isEmpty())
                    if (MainActivity.selectedNodes.getLast().equals(node))
                        break;
                MainActivity.selectedNodes.add(node);
                break;
            case MotionEvent.ACTION_UP:
                Log.wtf("nodes", MainActivity.selectedNodes.toString());
                //TODO
                break;
        }
        return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
    }

}
