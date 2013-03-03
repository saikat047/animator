package com.fun;

import static com.googlecode.javacv.cpp.opencv_core.IPL_DEPTH_8U;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_BGR2GRAY;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_INTER_AREA;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvCvtColor;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvResize;

import java.awt.*;
import java.awt.image.BufferedImage;

import javax.swing.*;

import com.googlecode.javacv.FrameGrabber;
import com.googlecode.javacv.OpenCVFrameGrabber;
import com.googlecode.javacv.cpp.opencv_core;

public class MinimalCamera extends JFrame implements Runnable {

    private FrameGrabber grabber = null;
    private opencv_core.IplImage grabbedImage = null, grayImage = null, smallImage = null;
    private boolean stop = false;
    private Exception exception = null;

    public MinimalCamera() {
        setSize(640, 480);
        setVisible(true);
        start();
    }

    public void start() {
        try {
            new Thread(this).start();
        } catch (Exception e) {
            if (exception == null) {
                exception = e;
                repaint();
            }
        }
    }

    public void run() {
        try {
            try {
                grabber = FrameGrabber.createDefault(0);
                grabber.setImageWidth(getWidth());
                grabber.setImageHeight(getHeight());
                grabber.start();
                grabbedImage = grabber.grab();
            } catch (Exception e) {
                if (grabber != null) grabber.release();
                grabber = new OpenCVFrameGrabber(0);
                grabber.setImageWidth(getWidth());
                grabber.setImageHeight(getHeight());
                grabber.start();
                grabbedImage = grabber.grab();
            }
            grayImage  = opencv_core.IplImage.create(grabbedImage.width(), grabbedImage.height(), IPL_DEPTH_8U, 1);
            smallImage = opencv_core.IplImage.create(grabbedImage.width() / 4, grabbedImage.height() / 4, IPL_DEPTH_8U, 1);
            stop = false;
            while (!stop && (grabbedImage = grabber.grab()) != null) {
                cvCvtColor(grabbedImage, grayImage, CV_BGR2GRAY);
                cvResize(grayImage, smallImage, CV_INTER_AREA);
                repaint();
            }
            grabbedImage = grayImage = smallImage = null;
            grabber.stop();
            grabber.release();
            grabber = null;
        } catch (Exception e) {
            if (exception == null) {
                exception = e;
                repaint();
            }
        }
    }

    @Override public void update(Graphics g) {
        paint(g);
    }

    @Override public void paint(Graphics g) {
        if (grabbedImage != null) {
            BufferedImage image = grabbedImage.getBufferedImage(2.2/grabber.getGamma());
            g.drawImage(image, 0, 0, null);
        }
    }

    public static void main(String [] argv) {
        MinimalCamera app = new MinimalCamera();
        app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
