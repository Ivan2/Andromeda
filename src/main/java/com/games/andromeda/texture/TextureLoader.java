package com.games.andromeda.texture;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.games.andromeda.PxDpConverter;
import com.games.andromeda.R;
import com.games.andromeda.graph.Node;

import org.andengine.engine.Engine;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.TextureManager;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TextureRegionFactory;
import org.andengine.ui.activity.BaseGameActivity;

public class TextureLoader {

    private BaseGameActivity activity;
    private TextureManager textureManager;
    private Engine engine;

    public TextureLoader(BaseGameActivity activity, Engine engine) {
        this.activity = activity;
        this.engine = engine;
        textureManager = engine.getTextureManager();
        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
    }

    public ITextureRegion loadSystemTexture(Node.SystemType systemType) {
        int size = 256;
        float radius = 50;
        Bitmap bitmap = android.graphics.Bitmap.createBitmap(size, size,
                android.graphics.Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setStrokeWidth(16);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        if (systemType == Node.SystemType.MINI)
            radius *= 0.4;
        canvas.drawCircle(bitmap.getWidth()/2, bitmap.getHeight()/2, radius, paint);
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
        canvas.drawCircle(bitmap.getWidth()/2, bitmap.getHeight()/2, radius, paint);

        BitmapTextureAtlas bitmapTextureAtlas = new BitmapTextureAtlas(textureManager,
                bitmap.getWidth(), bitmap.getHeight(), TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        BitmapTextureAtlasSource source = new BitmapTextureAtlasSource(bitmap);
        bitmapTextureAtlas.addTextureAtlasSource(source, 0, 0);
        bitmapTextureAtlas.load();
        return TextureRegionFactory.createFromSource(bitmapTextureAtlas, source, 0, 0);
    }

    public ITextureRegion loadBackgroundTexture() {
         return loadFileTexture("background.png", 1024, 1024);
    }

    public ITextureRegion loadColoredShipTextire(String color){
        return loadFileTexture("ship/" + color + ".png", 128, 128);
    }

    public ITextureRegion loadHyperTexture() {
        return loadFileTexture("hyper.png", 128, 128);
    }

    private ITextureRegion loadFileTexture(String file, int sizeX, int sizeY){
        BitmapTextureAtlas bitmapTextureAtlas = new BitmapTextureAtlas(textureManager, sizeX, sizeY,
                TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        textureManager.loadTexture(bitmapTextureAtlas);
        return BitmapTextureAtlasTextureRegionFactory.createFromAsset
                (bitmapTextureAtlas, activity, file, 0, 0);
    }

    public ITextureRegion loadEmptyTexture() {
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

    public Font loadTextTexture() {
        BitmapTextureAtlas fontTexture = new BitmapTextureAtlas(textureManager,
                128, 64, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        Font font = FontFactory.createFromAsset(activity.getFontManager(), fontTexture, activity.getAssets(),
                "fonts/font.ttf", PxDpConverter.dpToPx(activity.getResources().getDimension(R.dimen.text_size)),
                true, android.graphics.Color.WHITE);
        textureManager.loadTexture(fontTexture);
        engine.getFontManager().loadFont(font);
        return font;
    }

}
