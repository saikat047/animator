package com.fun.animator.input;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.Timer;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import com.fun.animator.LifeCycle;
import com.fun.animator.image.CombinedImage;
import com.fun.animator.image.DefaultDepthImageTransformers;
import com.fun.animator.image.ImagePanel;

public class ImageTransformerFrame extends JFrame implements LifeCycle {

    private ImagePanel depthImagePanel;
    private ImagePanel transformedImagePanel;
    private CombinedImage combinedImage;
    private BufferedImage transformedImage;
    private java.util.Timer taskRunner = new Timer();

    ImageTransformerFrame(CombinedImage combinedImage) {
        super("ImageTransformationTest");
        this.combinedImage = combinedImage;
    }

    @Override
    public void createComponents() {
        depthImagePanel = new ImagePanel("DepthImage", Color.GREEN) {
            @Override
            protected BufferedImage getColorImage() {
                return combinedImage.getColorImage();
            }
        };
        transformedImagePanel = new ImagePanel("TransformedImage", Color.GREEN) {
            @Override
            protected BufferedImage getColorImage() {
                return new DefaultDepthImageTransformers().createColorImage(combinedImage.getDepthImage());
            }
        };
    }

    @Override
    public void createLayout() {
        getContentPane().setLayout(new BorderLayout());
        JPanel imagesPanel = new JPanel();
        BoxLayout layout = new BoxLayout(imagesPanel, BoxLayout.LINE_AXIS);
        imagesPanel.setLayout(layout);
        imagesPanel.add(wrapComponent(depthImagePanel));
        imagesPanel.add(wrapComponent(transformedImagePanel));
        getContentPane().add(imagesPanel, BorderLayout.CENTER);

        JLabel infoLabel = new JLabel();
        infoLabel.setText(getImageInfo(combinedImage));
        getContentPane().add(wrapComponent(infoLabel), BorderLayout.LINE_END);
    }

    private String getImageInfo(CombinedImage combinedImage) {
        final BufferedImage colorImage = combinedImage.getColorImage();
        StringBuilder imageInfoBuilder = new StringBuilder("<html>");
        imageInfoBuilder.append("Width : ").append(colorImage.getWidth()).append("<br>")
                        .append("Height : ").append(colorImage.getHeight()).append("<br>");
        imageInfoBuilder.append("</html>");
        return imageInfoBuilder.toString();
    }

    @Override
    public void registerHandlers() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                taskRunner.cancel();
                taskRunner.purge();
            }
        });
    }

    @Override
    public void initialize() {
        taskRunner.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                depthImagePanel.repaint();
                transformedImagePanel.repaint();
            }
        }, 1000, 20);
    }

    private JPanel wrapComponent(JComponent component) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new CompoundBorder(new EmptyBorder(10, 10, 10, 10), new LineBorder(Color.BLACK, 2)));
        panel.add(component);
        return panel;
    }
}
