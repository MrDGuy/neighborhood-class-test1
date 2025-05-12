package org.code.neighborhood.support;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

public class ColorHelpers {
    private static final Map<String, Color> colorMap = new HashMap<>();

    static {
        colorMap.put("black", Color.BLACK);
        colorMap.put("blue", Color.BLUE);
        colorMap.put("cyan", Color.CYAN);
        colorMap.put("gray", Color.GRAY);
        colorMap.put("green", Color.GREEN);
        colorMap.put("magenta", Color.MAGENTA);
        colorMap.put("orange", Color.ORANGE);
        colorMap.put("pink", Color.PINK);
        colorMap.put("red", Color.RED);
        colorMap.put("white", Color.WHITE);
        colorMap.put("yellow", Color.YELLOW);
    }

    public static Color fromName(String name) {
        return colorMap.getOrDefault(name.toLowerCase(), null);
    }

    public static boolean isRecognizedColor(String name) {
        return colorMap.containsKey(name.toLowerCase());
    }
}