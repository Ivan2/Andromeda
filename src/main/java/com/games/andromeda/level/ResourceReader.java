package com.games.andromeda.level;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ResourceReader extends BufferedReader {
    public ResourceReader(Context context, String file) throws IOException {
        super(new InputStreamReader(
                context.getResources().openRawResource(
                        context.getResources().getIdentifier(
                                file, "raw", context.getPackageName()))));
    }
}
