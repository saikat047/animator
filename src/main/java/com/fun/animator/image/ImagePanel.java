package com.fun.animator.image;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.*;

import javax.swing.*;

import com.fun.animator.event.FrameGrabbedListener;
import com.fun.animator.input.FPSCalculator;

public class ImagePanel extends JPanel implements FrameGrabbedListener {

    private final java.util.List<RegionSelectionListener> regionSelectionListeners = new ArrayList<RegionSelectionListener>();
    private final Color selectionColor;

    protected CombinedImage combinedImage;

    private Color color;
    private String purpose;
    private Font biggerFont;

    private FPSCalculator fpsCalculator = new FPSCalculator();
    private Point selectedRegionStart;
    private Rectangle selectedRegion;

    public ImagePanel(String purpose, Color color) {
        this(purpose, color, Color.RED);
    }

    public ImagePanel(String purpose, Color color, Color selectionColor) {
        this.color = color;
        this.purpose = purpose;
        this.selectionColor = selectionColor;
        Font font = getFont();
        biggerFont = new Font(font.getName(), font.getStyle() | Font.BOLD, font.getSize());
        final MouseRegionSelectionAdapter mouseListener = new MouseRegionSelectionAdapter();
        this.addMouseListener(mouseListener);
        this.addMouseMotionListener(mouseListener);
    }

    public void setRegionInfo(final Point selectedRegionStart, final Rectangle selectedRegion) {
        this.selectedRegionStart = selectedRegionStart;
        this.selectedRegion = selectedRegion;
    }

    private void fireRegionSelectedEvent() {
        BufferedImage colorImage = combinedImage.getColorImage();
        final int panelWidth = getWidth();
        final int imageWidth = colorImage.getWidth();
        double image2panelRatio = (double) imageWidth / panelWidth;

        Point regionStart = new Point((int) (image2panelRatio * selectedRegionStart.getX()),
                                      (int) (image2panelRatio * selectedRegionStart.getY()));
        Rectangle region = new Rectangle((int) (image2panelRatio * selectedRegion.getWidth()),
                                         (int) (image2panelRatio * selectedRegion.getHeight()));

        for (RegionSelectionListener regionSelectionListener : regionSelectionListeners) {
            regionSelectionListener.regionSelected(combinedImage, regionStart, region);
        }
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

        if (selectedRegionStart != null) {
            g.setColor(selectionColor);
            g.drawRect((int) selectedRegionStart.getX(), (int) selectedRegionStart.getY(),
                       (int) selectedRegion.getWidth(), (int) selectedRegion.getHeight());
        }
    }

    public void addRegionSelectionListener(RegionSelectionListener listener) {
        regionSelectionListeners.add(listener);
    }

    public void removeRegionSelectionListener(RegionSelectionListener listener) {
        regionSelectionListeners.remove(listener);
    }

    public interface RegionSelectionListener {
        /**
         * The given region has co-ordinates relative to the image panel.
         * @param rectangle
         */
        public void regionSelected(final CombinedImage image, final Point start, final Rectangle rectangle);
    }

    private class MouseRegionSelectionAdapter extends MouseAdapter {

        private boolean selecting = false;
        private Point start, stop;

        @Override
        public void mousePressed(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON1) {
                selecting = true;
                start = e.getPoint();
            }
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            if (selecting) {
                updateSelectedRegion(start, e.getPoint());
            }
        }

        @Override
        public void mouseDragged(MouseEvent event) {
            if (selecting) {
                updateSelectedRegion(start, event.getPoint());
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON1) {
                selecting = false;
                stop = e.getPoint();
                updateSelectedRegion(start, stop);
                fireRegionSelectedEvent();
            }
        }

        @Override
        public void mouseExited(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON1) {
                selecting = false;
                stop = e.getPoint();
                updateSelectedRegion(start, stop);
                fireRegionSelectedEvent();
            }
        }

        private void updateSelectedRegion(final Point start, final Point end) {
            final int width = (int) Math.abs(start.getX() - end.getX());
            final int height = (int) Math.abs(start.getY() - end.getY());
            final int leftTopX = (int) Math.min(start.getX(), end.getX());
            final int leftTopY = (int) Math.min(start.getY(), end.getY());
            final Point leftTopPoint = new Point(leftTopX, leftTopY);
            final Rectangle region = new Rectangle(width, height);
            setRegionInfo(leftTopPoint, region);
        }
    }
}
