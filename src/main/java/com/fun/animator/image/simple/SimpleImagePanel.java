package com.fun.animator.image.simple;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.*;

import com.fun.animator.event.FrameGrabbedListener;
import com.fun.animator.image.CombinedImage;
import com.fun.animator.input.FPSCalculator;

public class SimpleImagePanel extends JPanel implements FrameGrabbedListener {

    protected CombinedImage combinedImage;

    private Color color;
    private String purpose;
    private Font biggerFont;

    private FPSCalculator fpsCalculator = new FPSCalculator();

    public SimpleImagePanel(String purpose, Color color) {
        this.color = color;
        this.purpose = purpose;
        Font font = getFont();
        biggerFont = new Font(font.getName(), font.getStyle() | Font.BOLD, font.getSize());
    }

    @Override
    public void inputFrameGrabbed(CombinedImage combinedImage) {
        this.combinedImage = combinedImage;
    }

    public CombinedImage getCombinedImage() {
        return combinedImage;
    }

    protected BufferedImage getColorImage() {
        if (combinedImage == null) {
            return null;
        }
        return combinedImage.getColorImage();
    }

    @Override
    public void paint(Graphics g) {
        g.setColor(Color.BLACK);
        g.clearRect(0, 0, getWidth(), getHeight());
        BufferedImage image = getColorImage();
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
}
