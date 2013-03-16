package com.fun.animator.input;

import com.fun.animator.image.CombinedImage;
import com.fun.animator.image.CombinedImageImpl;
import com.googlecode.javacv.FrameGrabber;
import com.googlecode.javacv.cpp.opencv_core;

public class Camera {

    private FrameGrabber grabber;
    private int width;
    private int height;
    private boolean initialized;
    private boolean started;

    Camera() {
    }

    public void initialize() throws RuntimeException {
        try {
            grabber = createFrameGrabber();
            grabber.setImageWidth(width);
            grabber.setImageHeight(height);
            grabber.setImageMode(FrameGrabber.ImageMode.COLOR);
            initialized = true;
        } catch (FrameGrabber.Exception e) {
            throw new RuntimeException("unable to initialize grabber", e);
        }
    }

    protected FrameGrabber createFrameGrabber() throws FrameGrabber.Exception {
        FrameGrabber grabber = FrameGrabber.createDefault(0);
        return grabber;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public boolean isStarted() {
        return started;
    }

    public FrameGrabber getGrabber() {
        return grabber;
    }

    public void start() {
        try {
            grabber.start();
            started = true;
        } catch (FrameGrabber.Exception e) {
            throw new RuntimeException("unable to start grabber", e);
        }
    }

    public final CombinedImage getGrabbedImage() {
        return getGrabbedImage(grabber.getGamma());
    }

    public CombinedImage getGrabbedImage(double gamma) {
        final opencv_core.IplImage grabbedImage;
        try {
            grabbedImage = grabber.grab();
        } catch (FrameGrabber.Exception e) {
            throw new RuntimeException("unable to grab image", e);
        }
        return new CombinedImageImpl(grabbedImage, null);
    }

    public void stop() {
        try {
            started = false;
            grabber.stop();
        } catch (FrameGrabber.Exception e) {
            throw new RuntimeException("unable to stop grabber", e);
        }
    }

    public void release() {
        try {
            grabber.release();
        } catch (FrameGrabber.Exception e) {
            throw new RuntimeException("unable to release grabber", e);
        }
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
