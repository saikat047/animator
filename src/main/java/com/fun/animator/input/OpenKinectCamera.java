package com.fun.animator.input;

import com.googlecode.javacv.FrameGrabber;
import com.googlecode.javacv.OpenKinectFrameGrabber;
import com.googlecode.javacv.cpp.opencv_core;

public class OpenKinectCamera extends Camera {

    private OpenKinectFrameGrabber openKinectFrameGrabber;

    @Override
    protected FrameGrabber createFrameGrabber() throws FrameGrabber.Exception {
        openKinectFrameGrabber = new OpenKinectFrameGrabber(0);
        return openKinectFrameGrabber;
    }

    @Override
    public Image getGrabbedImage(double gamma) {
        try {
            opencv_core.IplImage depthImage = openKinectFrameGrabber.grabDepth();
            opencv_core.IplImage colorImage = openKinectFrameGrabber.grabVideo();
            return new ImageWithDepth(colorImage, gamma, depthImage);
        } catch (FrameGrabber.Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
