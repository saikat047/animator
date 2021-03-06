package com.fun.animator.image;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;

import javax.imageio.ImageIO;

public class DefaultImageSequenceRecorder implements ImageSequenceRecorder, Runnable {

    private static final String BACKGROUND_DEPTH_IMAGE_FILE_NAME = "original";
    private static final String BACKGROUND_DEPTH_CONVERTED_IMAGE_FILE_NAME = "converted-converted";
    private final File saveDirectory;

    private DepthImageTransformer depthImageTransformer = new DefaultDepthImageTransformers();
    private BlockingQueue<CombinedImage> combinedImageQueue;
    private Thread runnerThread;
    private boolean stopRequested = false;

    private final AtomicInteger imageToDiskNumber = new AtomicInteger(0);

    public DefaultImageSequenceRecorder(int maxNumOfImages, File saveDirectory) {
        this.saveDirectory = saveDirectory;
        combinedImageQueue = new LinkedBlockingDeque<CombinedImage>(maxNumOfImages);
    }

    @Override
    public void startRecording() {
        runnerThread = new Thread(this);
        runnerThread.start();
    }

    @Override
    public void run() {
        while (!stopRequested) {
            try {
                CombinedImage combinedImage = combinedImageQueue.take();
                writeImageToFile(combinedImage, imageToDiskNumber.incrementAndGet());
            } catch (InterruptedException e) {
            }
        }
    }

    @Override
    public void add(CombinedImage combinedImage) {
        combinedImageQueue.offer(combinedImage);
    }

    private void writeImageToFile(CombinedImage combinedImage, int imageNumber) {
        writeImageToFile(combinedImage.getColorImage(),
                         new File(saveDirectory, String.format("%d.%s.png", imageNumber, BACKGROUND_DEPTH_IMAGE_FILE_NAME)));
        writeImageToFile(depthImageTransformer.createColorImage(combinedImage.getDepthImage()),
                         new File(saveDirectory, String.format("%d.%s.png", imageNumber, BACKGROUND_DEPTH_CONVERTED_IMAGE_FILE_NAME)));
    }

    private void writeImageToFile(BufferedImage image, File outputFile) {
        System.out.println("writing image -> " + outputFile.getAbsolutePath());
        try {
            ImageIO.write(image, "png", outputFile);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(String.format("Unable to write to: %s", outputFile.getAbsolutePath()));
        }
    }

    @Override
    public void stopRecording() {
        stopRequested = true;
        try {
            combinedImageQueue.clear();
            runnerThread.join();
        } catch (InterruptedException e) {
        }
    }
}
