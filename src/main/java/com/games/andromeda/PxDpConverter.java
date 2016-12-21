package com.games.andromeda;

import android.content.Context;

public class PxDpConverter {

    private static Context context;

    public static void createInstance(Context context) {
        PxDpConverter.context = context;
    }

    public static float dpToPx(float dp) {
        return dp * context.getResources().getDisplayMetrics().xdpi/160.0f;
    }

}
