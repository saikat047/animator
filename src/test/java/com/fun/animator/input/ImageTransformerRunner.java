package com.fun.animator.input;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import com.fun.animator.AnimatorInitializer;
import com.fun.animator.image.CombinedImageImpl;
import com.fun.animator.image.DepthImage;
import com.fun.animator.image.DepthImageImpl;

public class ImageTransformerRunner {
    public static void main(String [] argv) throws Exception {
        InputStream resourceAsStream = ImageTransformerRunner.class.getResourceAsStream("/good-example.png");
        BufferedImage image = ImageIO.read(resourceAsStream);
        resourceAsStream.close();
        createFrame("good-example", image);

        resourceAsStream = ImageTransformerRunner.class.getResourceAsStream("/bad-example.png");
        image = ImageIO.read(resourceAsStream);
        resourceAsStream.close();
        createFrame("bad-example", image);
    }

    private static void createFrame(String frameName, BufferedImage image) throws IOException {
        ImageTransformerFrame frame = new ImageTransformerFrame(new CombinedImageImpl(image, TestUtils.createDepthImage(image)));
        frame.setTitle(frameName);
        AnimatorInitializer.init(frame);
        frame.pack();
        frame.setSize(800, 320);
        frame.setVisible(true);
    }
}
