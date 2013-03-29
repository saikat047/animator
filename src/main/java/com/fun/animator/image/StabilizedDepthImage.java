package com.fun.animator.image;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class StabilizedDepthImage implements DepthImage {

    private final Lock imageListLock = new ReentrantLock();
    private DepthImage depthImage = new NullDepthImage();

    public StabilizedDepthImage() {
    }

    public void updateUnreadablePixels(DepthImage image) {
        imageListLock.lock();
        try {
            if (depthImage instanceof NullDepthImage) {
                depthImage = image;
                return;
            }
            fixUnreadablePixelsOnBackground(image);
        } finally {
            imageListLock.unlock();
        }
    }

    private void fixUnreadablePixelsOnBackground(DepthImage image) {
        for (int x = 0; x < depthImage.getWidth(); x++) {
            for (int y = 0; y < depthImage.getHeight(); y++) {
                final int oldDepth = depthImage.getDepth(x, y);
                final int newDepth = image.getDepth(x, y);
                if (oldDepth >= DepthImageTransformer.DEPTH_MAX &&
                    newDepth < DepthImageTransformer.DEPTH_MAX) {
                    depthImage.setDepth(x, y, newDepth);
                }
            }
        }
    }

    @Override
    public int getDepth(int x, int y) {
        return depthImage.getDepth(x, y);
    }

    @Override
    public int getWidth() {
        return depthImage.getWidth();
    }

    @Override
    public int getHeight() {
        return depthImage.getHeight();
    }

    @Override
    public void setDepth(int x, int y, int depth) {
        throw new RuntimeException("Method not implemented");
    }

    @Override
    public DepthImage createCopy() {
        DepthImage newImage = new DepthImageImpl(depthImage.getWidth(), depthImage.getHeight());
        for (int x = 0; x < depthImage.getWidth(); x++) {
            for (int y = 0; y < depthImage.getHeight(); y++) {
                newImage.setDepth(x, y, depthImage.getDepth(x, y));
            }
        }
        return newImage;
    }

    public static class Unmodifiable extends StabilizedDepthImage {
        public Unmodifiable(DepthImage depthImage) {
            super.updateUnreadablePixels(depthImage);
        }

        @Override
        public void updateUnreadablePixels(DepthImage image) {
        }
    }
}
