package com.games.andromeda.layers;

import android.content.Context;
import android.graphics.PointF;

import com.games.andromeda.graph.Edge;
import com.games.andromeda.graph.MyGraph;
import com.games.andromeda.graph.Node;
import com.games.andromeda.texture.TextureLoader;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.primitive.Line;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.TextureManager;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import java.util.Iterator;

public class BackgroundLayer extends Layer {

    private MyGraph graph;
    private Sprite layer;

    public BackgroundLayer(Scene scene, Camera camera, TextureLoader textureLoader,
                           VertexBufferObjectManager vertexBufferObjectManager, MyGraph graph) {
        super(scene, camera, textureLoader, vertexBufferObjectManager);
        this.graph = graph;

        layer = new Sprite(0, 0, camera.getWidth(), camera.getHeight(),
                textureLoader.loadBackgroundTexture(),
                vertexBufferObjectManager);
        scene.attachChild(layer);
    }

    @Override
    public void repaint() {
        //TODO удалить все спрайты со слоя
        for (Edge edge : graph.getEdges()) {
            PointF pos1 = getPos(edge.getNode1().getX(), edge.getNode1().getY());
            PointF pos2 = getPos(edge.getNode2().getX(), edge.getNode2().getY());
            if (!(edge.getNode1().getSystemType() == Node.SystemType.HYPER &&
                    edge.getNode2().getSystemType() == Node.SystemType.HYPER)) {
                Line line = new Line(pos1.x, pos1.y, pos2.x, pos2.y, 1,
                        vertexBufferObjectManager);
                line.setColor(0, 0, 1);
                layer.attachChild(line);
            }
        }
    }

}
