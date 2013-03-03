package com.fun.animator.input;

import java.awt.*;
import java.awt.image.BufferedImage;

import javax.swing.*;

public class ImagePanel extends JPanel {

    private static final String PLACE_HOLDER = "ImagePanel : ";

    private BufferedImage image;

    @Override
    public void paint(Graphics g) {
        if (image != null) {
            g.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
        } else {
            String placeHolder = PLACE_HOLDER + System.currentTimeMillis();
            g.clearRect(0, 0, getWidth(), getHeight());
            g.drawChars(placeHolder.toCharArray(), 0, placeHolder.length(), 30, 30);
        }
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }
}
