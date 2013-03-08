package com.fun.animator.input;

import java.awt.image.BufferedImage;
import java.io.InputStream;

import javax.imageio.ImageIO;

import com.fun.animator.AnimatorInitializer;

public class ImageTransformerRunner {
    public static void main(String [] argv) throws Exception {
        InputStream resourceAsStream = ImageTransformerRunner.class.getResourceAsStream("/kinect-depth.png");
        BufferedImage image = ImageIO.read(resourceAsStream);
        ImageTransformerFrame frame = new ImageTransformerFrame(image);
        AnimatorInitializer.init(frame);
        frame.pack();
        frame.setSize(800, 320);
        frame.setVisible(true);
        resourceAsStream.close();
    }
}
