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
            setRotation((float) Math.toDegrees(
                Math.atan2(current.getX() - prev.getX(), prev.getY() - current.getY())));
        }
    }



    public void setFleet(Fleet fleet) {
        this.fleet = fleet;
    }
}
