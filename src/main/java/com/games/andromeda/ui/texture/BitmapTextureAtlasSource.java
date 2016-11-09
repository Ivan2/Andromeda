package com.games.andromeda.ui.texture;

import android.graphics.Bitmap;

import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.source.BaseTextureAtlasSource;

public class BitmapTextureAtlasSource extends BaseTextureAtlasSource
        implements IBitmapTextureAtlasSource {

    private final int[] mColors;

    public BitmapTextureAtlasSource(Bitmap pBitmap) {
        super(0,0, pBitmap.getWidth(), pBitmap.getHeight());

        mColors = new int[mTextureWidth * mTextureHeight];

        for(int y = 0; y < mTextureHeight; ++y)
            for( int x = 0; x < mTextureWidth; ++x)
                mColors[x + y * mTextureWidth] = pBitmap.getPixel(x, y);
    }

    @Override
    public Bitmap onLoadBitmap(Bitmap.Config pBitmapConfig) {
        return Bitmap.createBitmap(mColors, mTextureWidth, mTextureHeight, Bitmap.Config.ARGB_8888);
    }

    @Override
    public IBitmapTextureAtlasSource deepCopy() {
        return new BitmapTextureAtlasSource(Bitmap.createBitmap(mColors, mTextureWidth,
                mTextureHeight, Bitmap.Config.ARGB_8888));
    }
}