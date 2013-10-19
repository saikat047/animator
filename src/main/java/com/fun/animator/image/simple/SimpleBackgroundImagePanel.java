package com.fun.animator.image.simple;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;

import javax.swing.*;

import com.fun.animator.image.CombinedImage;
import com.fun.animator.image.CombinedImageImpl;
import com.fun.animator.image.DefaultDepthImageTransformers;
import com.fun.animator.image.DepthImage;
import com.fun.animator.image.DepthImageIO;
import com.fun.animator.image.DepthImageTransformer;
import com.fun.animator.image.StabilizedDepthImage;

public class SimpleBackgroundImagePanel extends SimpleImagePanel {

    private StabilizedDepthImage backgroundStabilizerDepthImage = new StabilizedDepthImage();
    private DepthImageTransformer depthImageTransformer = new DefaultDepthImageTransformers();
    private File animatorDepthBackgroundFile = new File(System.getProperty("user.home"), "animator-depth-bg.png");
    private boolean frozen;

    public SimpleBackgroundImagePanel(String purpose, Color color) {
        super(purpose, color);
        final JPopupMenu popupMenu = createPopupMenu();
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    popupMenu.show(SimpleBackgroundImagePanel.this, e.getX(), e.getY());
                }
            }
        });
    }

    private JPopupMenu createPopupMenu() {
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem freezeBackground = new JMenuItem("Freeze background image");
        freezeBackground.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setFrozen(true);
            }
        });
        popupMenu.add(freezeBackground);
        JMenuItem saveItem = new JMenuItem("Save depth as image");
        saveItem.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DepthImageIO.writeToFile(combinedImage.getDepthImage(), animatorDepthBackgroundFile);
                JOptionPane.showMessageDialog(null, "Depth image saved as: " + animatorDepthBackgroundFile.getAbsolutePath(),
                                              "Saved!", JOptionPane.OK_OPTION);
            }
        });
        popupMenu.add(saveItem);
        JMenuItem loadItem = new JMenuItem("Load depth as image");
        loadItem.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FileDialog dialog = new FileDialog((Frame) null, "Select depth image background");
                dialog.setDirectory(System.getProperty("user.home"));
                dialog.setFilenameFilter(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        return name.toLowerCase().endsWith(".png");
                    }
                });
                dialog.setMode(FileDialog.LOAD);
                dialog.setMultipleMode(false);
                dialog.setVisible(true);
                if (dialog.getFiles().length > 0) {
                    DepthImage image = DepthImageIO.readFromFile(dialog.getFiles()[0]);
                    backgroundStabilizerDepthImage = new StabilizedDepthImage.Unmodifiable(image);
                }
            }
        });
        popupMenu.add(loadItem);
        JMenuItem clearItem = new JMenuItem("Clear depth image");
        clearItem.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                backgroundStabilizerDepthImage = new StabilizedDepthImage();
                setFrozen(false);
            }
        });
        popupMenu.add(clearItem);
        return popupMenu;
    }

    @Override
    public void inputFrameGrabbed(CombinedImage combinedImage) {
        if (frozen) {
            return;
        }
        backgroundStabilizerDepthImage.updateUnreadablePixels(combinedImage.getDepthImage());
        this.combinedImage = new CombinedImageImpl(combinedImage.getColorImage(), backgroundStabilizerDepthImage);
    }

    @Override
    public CombinedImage getCombinedImage() {
        CombinedImage combinedImage = super.getCombinedImage();
        return new CombinedImageImpl(combinedImage.getColorImage(), backgroundStabilizerDepthImage.createCopy());
    }

    @Override
    protected BufferedImage getColorImage() {
        if (combinedImage == null) {
            return null;
        }
        return depthImageTransformer.createColorImage(backgroundStabilizerDepthImage);
    }

    public void setFrozen(boolean frozen) {
        this.frozen = frozen;
    }
}
