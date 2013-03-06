package com.fun.animator.input;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import com.fun.animator.AnimatorInitializer;
import com.fun.animator.LifeCycle;

public class ImageTransformerFrame extends JFrame implements LifeCycle {

    private ImagePanel depthImagePanel;
    private ImagePanel transformedImagePanel;
    private BufferedImage depthImage;
    private BufferedImage transformedImage;

    ImageTransformerFrame() {
        super("ImageTransformationTest");
    }

    @Override
    public void createComponents() {
        depthImagePanel = new ImagePanel("DepthImage", Color.GREEN);
        transformedImagePanel = new ImagePanel("TransformedImage", Color.GREEN);
    }

    @Override
    public void createLayout() {
        getContentPane().setLayout(new BorderLayout());
        JPanel imagesPanel = new JPanel();
        BoxLayout layout = new BoxLayout(imagesPanel, BoxLayout.LINE_AXIS);
        imagesPanel.setLayout(layout);
        imagesPanel.add(wrapImagePanel(depthImagePanel));
        imagesPanel.add(wrapImagePanel(transformedImagePanel));
        getContentPane().add(imagesPanel, BorderLayout.CENTER);
    }

    @Override
    public void registerHandlers() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    @Override
    public void initialize() {
        InputStream resourceAsStream = getClass().getResourceAsStream("/kinect-depth.png");
        try {
            depthImage = ImageIO.read(resourceAsStream);
            resourceAsStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        transformedImage = new DefaultDepthImageTransformers().convertDepthImage(depthImage);
        depthImagePanel.setImage(depthImage);
        transformedImagePanel.setImage(transformedImage);
    }

    private JPanel wrapImagePanel(ImagePanel imagePanel) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new CompoundBorder(new EmptyBorder(10, 10, 10, 10), new LineBorder(Color.BLACK, 2)));
        panel.add(imagePanel);
        return panel;
    }

    public static void main(String [] argv) {
        ImageTransformerFrame frame = new ImageTransformerFrame();
        AnimatorInitializer.init(frame);
        frame.pack();
        frame.setSize(800, 320);
        frame.setVisible(true);
    }
}
