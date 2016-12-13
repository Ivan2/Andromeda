package com.games.andromeda.ui.sprites;

import com.games.andromeda.graph.Node;
import com.games.andromeda.logic.Fleet;
import com.games.andromeda.logic.WorldAccessor;

import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public class ShipSprite extends Sprite {
    private Fleet fleet;
    public ShipSprite(float pX, float pY, ITextureRegion pTextureRegion, VertexBufferObjectManager pVertexBufferObjectManager) {
        super(pX, pY, pTextureRegion, pVertexBufferObjectManager);
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
