package com.games.andromeda.ui.sprites;

import android.opengl.GLES20;

import com.games.andromeda.graph.Node;
import com.games.andromeda.logic.Fleet;
import com.games.andromeda.logic.WorldAccessor;

import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.LoopEntityModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public class ShipSprite extends Sprite {
    private Fleet fleet;
    private LoopEntityModifier activeModifier;

    public ShipSprite(float pX, float pY, ITextureRegion pTextureRegion, VertexBufferObjectManager pVertexBufferObjectManager) {
        super(pX, pY, pTextureRegion, pVertexBufferObjectManager);
        setBlendFunction(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        activeModifier = new LoopEntityModifier(new SequenceEntityModifier(
                new AlphaModifier(1, 1, 0),
                new AlphaModifier(1, 0, 1)
        ));
    }

    public void activate(){
        deactivate();
        registerEntityModifier(activeModifier);
    }

    public void deactivate(){
        unregisterEntityModifier(activeModifier);
        setAlpha(1);
    }

    public Fleet getFleet() {
        return fleet;
    }

    public void rotate() {
        if (fleet.getPrevPosition() != null){
            WorldAccessor world = WorldAccessor.getInstance();
            Node prev = world.getNodes().get(fleet.getPrevPosition());
            Node current = world.getNodes().get(fleet.getPosition());
            rotate(prev.getX(), prev.getY(), current.getX(), current.getY());
        }
    }

    public void rotate(float oldX, float oldY, float newX, float newY){
        setRotation((float) Math.toDegrees(Math.atan2(newX - oldX, oldY - newY)));
    }


    public void setFleet(Fleet fleet) {
        this.fleet = fleet;
    }
}
