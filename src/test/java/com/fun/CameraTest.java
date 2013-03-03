package com.fun;

import static com.googlecode.javacv.cpp.opencv_core.IPL_DEPTH_8U;
import static com.googlecode.javacv.cpp.opencv_core.cvClearMemStorage;
import static com.googlecode.javacv.cpp.opencv_core.cvGetSeqElem;
import static com.googlecode.javacv.cpp.opencv_core.cvLoad;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_BGR2GRAY;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_INTER_AREA;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvCvtColor;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvResize;
import static com.googlecode.javacv.cpp.opencv_objdetect.CV_HAAR_DO_CANNY_PRUNING;
import static com.googlecode.javacv.cpp.opencv_objdetect.cvHaarDetectObjects;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.swing.*;

import com.googlecode.javacpp.Loader;
import com.googlecode.javacv.FrameGrabber;
import com.googlecode.javacv.OpenCVFrameGrabber;
import com.googlecode.javacv.cpp.opencv_core;
import com.googlecode.javacv.cpp.opencv_objdetect;

public class CameraTest extends JFrame implements Runnable {
    private opencv_objdetect.CvHaarClassifierCascade classifier = null;
    private opencv_core.CvMemStorage storage = null;
    private FrameGrabber grabber = null;
    private opencv_core.IplImage grabbedImage = null, grayImage = null, smallImage = null;
    private opencv_core.CvSeq faces = null;
    private boolean stop = false;
    private Exception exception = null;

    public CameraTest() {
        init();
        setVisible(true);
        setSize(640, 380);
        start();
    }

    public void init() {
        try {
            // Load the classifier file from Java resources.
            String classiferName = "/com/fun/haarcascade_frontalface_alt.xml";
            URL frontalfaceURL = getClass().getResource(classiferName);
            File classifierFile = Loader.extractResource(frontalfaceURL, null, "classifier", ".xml");
            if (classifierFile == null || classifierFile.length() <= 0) {
                throw new IOException("Could not extract \"" + classiferName + "\" from Java resources.");
            }

            // Preload the opencv_objdetect module to work around a known bug.
            Loader.load(opencv_objdetect.class);
            classifier = new opencv_objdetect.CvHaarClassifierCascade(cvLoad(classifierFile.getAbsolutePath()));
            classifierFile.delete();
            if (classifier.isNull()) {
                throw new IOException("Could not load the classifier file.");
            }

            storage = opencv_core.CvMemStorage.create();
        } catch (Exception e) {
            e.printStackTrace();
            if (exception == null) {
                exception = e;
                repaint();
            }
        }
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
                if (faces == null) {
                    cvClearMemStorage(storage);
                    cvCvtColor(grabbedImage, grayImage, CV_BGR2GRAY);
                    cvResize(grayImage, smallImage, CV_INTER_AREA);
                    faces = cvHaarDetectObjects(smallImage, classifier, storage, 1.1, 3, CV_HAAR_DO_CANNY_PRUNING);
                    repaint();
                }
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
            Graphics2D g2 = image.createGraphics();
            if (faces != null) {
                g2.setColor(Color.RED);
                g2.setStroke(new BasicStroke(2));
                int total = faces.total();
                for (int i = 0; i < total; i++) {
                    opencv_core.CvRect r = new opencv_core.CvRect(cvGetSeqElem(faces, i));
                    g2.drawRect(r.x()*4, r.y()*4, r.width()*4, r.height()*4);
                }
                faces = null;
            }
            g.drawImage(image, 0, 0, null);
        }
        if (exception != null) {
            int y = 0, h = g.getFontMetrics().getHeight();
            g.drawString(exception.toString(), 5, y += h);
            for (StackTraceElement e : exception.getStackTrace()) {
                g.drawString("        at " + e.toString(), 5, y += h);
            }
        }
    }

    public static void main(String [] argv) {
        CameraTest app = new CameraTest();
        app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
