package com.fun.animator.input;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class CachedRGBToColorMapper {

    private Map<Integer, Color> rgbColorMap =  new HashMap<Integer, Color>();

    public Color map(int RGB) {
        Color color = rgbColorMap.get(RGB);
        if (color == null) {
            color = new Color(RGB);
            rgbColorMap.put(RGB, color);
        }
        return color;
    }
}
