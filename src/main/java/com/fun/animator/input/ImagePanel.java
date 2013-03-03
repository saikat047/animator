package com.fun.animator.input;

import java.awt.*;
import java.awt.image.BufferedImage;

import javax.swing.*;

public class ImagePanel extends JPanel {

    private BufferedImage image;

    private Color color;
    private String purpose;
    private Font biggerFont;

    private FPSCalculator fpsCalculator = new FPSCalculator();

    ImagePanel(String purpose, Color color) {
        this.color = color;
        this.purpose = purpose;
        Font font = getFont();
        biggerFont = new Font(font.getName(), font.getStyle() | Font.BOLD, font.getSize());
    }

    @Override
    public void paint(Graphics g) {
        g.setColor(Color.BLACK);
        g.clearRect(0, 0, getWidth(), getHeight());
        if (image != null) {
            g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
        }

        g.setColor(color);
        if (g.getFont() != biggerFont) {
            g.setFont(biggerFont);
        }
        g.drawString(purpose, 10, 20);

        fpsCalculator.updateFPSRendered();
        String fpsString = "FPS : " + fpsCalculator.getFPS();
        g.drawString(fpsString, getWidth() - 100, 20);
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }
}
