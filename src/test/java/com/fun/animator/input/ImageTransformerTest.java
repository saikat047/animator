package com.fun.animator.input;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.*;

import com.fun.animator.image.ImagePanel;

public class ImageTransformerTest {
    public static void main(String [] argv) throws Exception {
        InputStream goodStream = ImageTransformerTest.class.getResourceAsStream("/good-example.png");
        final BufferedImage goodImage = ImageIO.read(goodStream);
        InputStream badStream = ImageTransformerTest.class.getResourceAsStream("/bad-example.png");
        final BufferedImage badImage = ImageIO.read(badStream);
        System.out.println("Good Image");
        dumpImageInfo(goodImage);
        System.out.println("Bad Image");
        dumpImageInfo(badImage);
        MinMaxInfo goodMMI = new MinMaxInfo();
        MinMaxInfo badMMI = new MinMaxInfo();
        MinMaxInfo diffMMI = new MinMaxInfo();
        final BufferedImage diffImage = new BufferedImage(goodImage.getWidth(), goodImage.getHeight(), BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < goodImage.getWidth(); x++) {
            for (int y = 0; y < goodImage.getHeight(); y++) {
                int goodRGB = goodImage.getRGB(x, y);
                goodMMI.update(goodRGB);
                int badRGB = badImage.getRGB(x, y);
                badMMI.update(badRGB);
                int difference = Math.abs(goodRGB - badRGB);
                badMMI.update(difference);
                // diffImage.setRGB(x, y, difference == 0 ? 0 : Color.GREEN.getRGB());
                if (difference == 0) {
                    if (badRGB == 0xFFFFFFFF) {
                        // both read infinity.
                        diffImage.setRGB(x, y, Color.BLACK.getRGB());
                    } else if (badRGB == 0) {
                        // both read 0. That sucks.
                        diffImage.setRGB(x, y, Color.BLACK.getRGB());
                    } else {
                        // perfect read, both value equal. we need more of this.
                        diffImage.setRGB(x, y, Color.GREEN.getRGB());
                    }
                } else {
                    // some difference found
                    diffImage.setRGB(x, y, Color.RED.getRGB());
                }

            }
        }
        System.out.println(String.format("Good: min = %h, max = %h", goodMMI.minDepth, goodMMI.maxDepth));
        System.out.println(String.format("Bad: min = %h, max = %h", badMMI.minDepth, badMMI.maxDepth));
        System.out.println(String.format("Diff: min = %h, max = %h", diffMMI.minDepth, diffMMI.maxDepth));
        JFrame frame = new JFrame("ImageComparison");
        frame.getContentPane().setLayout(new BorderLayout());
        JPanel imageContainer = new JPanel();
        BoxLayout layout = new BoxLayout(imageContainer, BoxLayout.LINE_AXIS);
        imageContainer.setLayout(layout);
        frame.getContentPane().add(imageContainer, BorderLayout.CENTER);
        ImagePanel goodImagePanel = createImagePanelWithImage(goodImage, Color.GREEN, "GoodImage");
        imageContainer.add(goodImagePanel);
        ImagePanel badImagePanel = createImagePanelWithImage(badImage, Color.RED, "BadImage");
        imageContainer.add(badImagePanel);
        ImagePanel diffImagePanel = createImagePanelWithImage(diffImage, Color.WHITE, "DiffImage");
        imageContainer.add(diffImagePanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(new Dimension(640, 300));
        frame.setVisible(true);
    }

    static class MinMaxInfo {
        long minDepth = Integer.MAX_VALUE;
        long maxDepth = 0;

        void update(int value) {
            long convertedValue = 0x00000000FFFFFFFFL & value;
            if (convertedValue < minDepth) {
                minDepth = convertedValue;
            }
            if (convertedValue > maxDepth) {
                maxDepth = convertedValue;
            }
        }
    }

    private static ImagePanel createImagePanelWithImage(BufferedImage image, Color color, String title) {
        ImagePanel imagePanel = new ImagePanel(title, color);
        imagePanel.setImage(image);
        Dimension dimension = new Dimension(200, 200);
        imagePanel.setMinimumSize(dimension);
        imagePanel.setSize(dimension);
        return imagePanel;
    }

    private static void dumpImageInfo(BufferedImage image) {
        StringBuilder imageInfoBuilder = new StringBuilder();
        imageInfoBuilder.append("Image type: ").append(image.getType()).append("\n")
                .append("Width : ").append(image.getWidth()).append("\n")
                .append("Height : ").append(image.getHeight()).append("\n");
        if (image.getPropertyNames() != null) {
            imageInfoBuilder.append("Properties: ").append("\n");
            for (String propName : image.getPropertyNames()) {
                imageInfoBuilder.append(propName).append(" = ").append(image.getProperty(propName)).append("\n");
            }
        }
        System.out.println(imageInfoBuilder.toString());
    }
}
