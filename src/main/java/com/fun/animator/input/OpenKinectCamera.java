package com.fun.animator.input;

import com.fun.animator.image.CombinedImage;
import com.fun.animator.image.CombinedImageImpl;
import com.googlecode.javacv.FrameGrabber;
import com.googlecode.javacv.OpenKinectFrameGrabber;
import com.googlecode.javacv.cpp.freenect;
import com.googlecode.javacv.cpp.opencv_core;

public class OpenKinectCamera extends Camera {

    private OpenKinectFrameGrabber openKinectFrameGrabber;

    @Override
    protected FrameGrabber createFrameGrabber() throws FrameGrabber.Exception {
        openKinectFrameGrabber = new OpenKinectFrameGrabber(0);
        openKinectFrameGrabber.setDepthFormat(freenect.FREENECT_DEPTH_11BIT);
        openKinectFrameGrabber.setImageMode(FrameGrabber.ImageMode.RAW);
        return openKinectFrameGrabber;
    }

    @Override
    public CombinedImage getGrabbedImage(double gamma) {
        try {
            opencv_core.IplImage depthImage = openKinectFrameGrabber.grabDepth();
            opencv_core.IplImage colorImage = openKinectFrameGrabber.grabVideo();
            return new CombinedImageImpl(colorImage, depthImage);
        } catch (FrameGrabber.Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
