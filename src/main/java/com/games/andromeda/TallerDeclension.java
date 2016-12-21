package com.games.andromeda;

public class TallerDeclension {

    public static String getTallerStr(int count) {
        int mod = count % 100;
        if (mod >= 11 && mod <= 14)
            return count+" таллеров";
        mod = count % 10;
        if (mod == 1)
            return count+" таллер";
        if (mod == 2 || mod == 3 || mod == 4)
            return count+" таллера";
        return count+" таллеров";
    }

}
