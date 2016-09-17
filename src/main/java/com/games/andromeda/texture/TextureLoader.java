package com.games.andromeda.texture;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.games.andromeda.graph.Node;

import org.andengine.opengl.texture.TextureManager;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TextureRegionFactory;

public class TextureLoader {

    public static ITextureRegion loadSystemTexture(TextureManager textureManager, Node.SystemType systemType) {
        Bitmap bitmap = android.graphics.Bitmap.createBitmap(128, 128,
                android.graphics.Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setStrokeWidth(16);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        canvas.drawCircle(64, 64, 50, paint);
        switch (systemType) {
            case EMPTY:
                paint.setColor(Color.GRAY);
                break;
            case FRIENDLY:
                paint.setColor(Color.GREEN);
                break;
            case ENEMY:
                paint.setColor(Color.RED);
                break;
            default:
                paint.setColor(Color.GRAY);
        }
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(64, 64, 50, paint);

        BitmapTextureAtlas bitmapTextureAtlas = new BitmapTextureAtlas(textureManager, 128, 128,
                TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        BitmapTextureAtlasSource source = new BitmapTextureAtlasSource(bitmap);
        bitmapTextureAtlas.addTextureAtlasSource(source, 0, 0);
        bitmapTextureAtlas.load();
        return TextureRegionFactory.createFromSource(bitmapTextureAtlas, source, 0, 0);
    }

    public static ITextureRegion loadQwertyTexture(TextureManager textureManager, Context context) {
        BitmapTextureAtlas bitmapTextureAtlas = new BitmapTextureAtlas(textureManager, 128, 128,
                TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        textureManager.loadTexture(bitmapTextureAtlas);
        return BitmapTextureAtlasTextureRegionFactory.createFromAsset
                (bitmapTextureAtlas, context, "qwerty.png", 0, 0);
    }

    public static ITextureRegion loadEmptyTexture(TextureManager textureManager) {
        Bitmap bitmap = android.graphics.Bitmap.createBitmap(128, 128,
                android.graphics.Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.argb(200, 50, 50, 50));

        BitmapTextureAtlas bitmapTextureAtlas = new BitmapTextureAtlas(textureManager, 128, 128,
                TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        BitmapTextureAtlasSource source = new BitmapTextureAtlasSource(bitmap);
        bitmapTextureAtlas.addTextureAtlasSource(source, 0, 0);
        bitmapTextureAtlas.load();
        return TextureRegionFactory.createFromSource(bitmapTextureAtlas, source, 0, 0);
    }

}
