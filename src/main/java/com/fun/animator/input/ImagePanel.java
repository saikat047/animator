package com.fun.animator.input;

import java.awt.*;
import java.awt.image.BufferedImage;

import javax.swing.*;

public class ImagePanel extends JPanel {

    private BufferedImage image;

    private Color color;
    private String purpose;

    ImagePanel(String purpose, Color color) {
        this.color = color;
        this.purpose = purpose;
    }

    @Override
    public void paint(Graphics g) {
        if (image != null) {
            g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
        }

        g.setColor(color);
        g.drawString(purpose, 10, 20);
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }
}
