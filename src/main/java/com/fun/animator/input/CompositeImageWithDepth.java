package com.fun.animator.input;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.fun.animator.image.DepthImageTransformer;
import com.fun.animator.image.Image;
import com.fun.animator.image.ImageWithDepth;
import com.fun.animator.image.Images;

public class CompositeImageWithDepth implements Image {

    private final List<Image> images;
    private final int numOfImages;
    private final Lock imageListLock = new ReentrantLock();

    public CompositeImageWithDepth(final int numOfImages) {
        this.numOfImages = numOfImages;
        images = new LinkedList<Image>();
    }

    public void add(Image image) {
        imageListLock.lock();
        try {
            images.add(0, image);
            if (images.size() > numOfImages) {
                images.remove(numOfImages);
            }
        } finally {
            imageListLock.unlock();
        }
    }

    public boolean isEmpty() {
        return images.isEmpty();
    }

    @Override
    public int getDepth(int x, int y) {
        final int depth = images.get(0).getDepth(x, y);
        if (depth < DepthImageTransformer.DEPTH_MAX) {
            return depth;
        }
        for (Image image : images) {
            final int imageDepth = image.getDepth(x, y);
            if (imageDepth < DepthImageTransformer.DEPTH_MAX) {
                return imageDepth;
            }
        }
        return DepthImageTransformer.DEPTH_MAX;
    }

    @Override
    public int getWidth() {
        return images.get(0).getWidth();
    }

    @Override
    public int getHeight() {
        return images.get(0).getHeight();
    }

    @Override
    public int getColorImageType() {
        return images.get(0).getColorImageType();
    }

    @Override
    public BufferedImage getColorImage() {
        throw new RuntimeException("method not implemented");
    }

    @Override
    public BufferedImage getDepthImage() {
        throw new RuntimeException("method not implemented");
    }

    @Override
    public Image deepCopy() {
        imageListLock.lock();
        try {
            return new ImageWithDepth(Images.cloneImage(images.get(0).getColorImage()),
                                      Images.copyDepthImage(this));
        } finally {
            imageListLock.unlock();
        }
    }
}
