package com.fun.animator.input;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import com.fun.animator.AnimatorInitializer;
import com.fun.animator.LifeCycle;
import com.fun.animator.event.InputImageEventBus;
import com.fun.animator.image.CombinedImage;
import com.fun.animator.image.simple.SimpleBackgroundImagePanel;
import com.fun.animator.image.simple.SimpleDepthImagePanel;
import com.fun.animator.image.simple.SimpleImagePanel;

public class SimpleCameraInputDialog extends JDialog implements LifeCycle {

    private static final int CAMERA_TASK_PERIOD_IN_MILLIS = 20;

    private SimpleImagePanel cameraInputImage;
    private SimpleBackgroundImagePanel backgroundImagePanel;
    private SimpleDepthImagePanel depthImage;
    private JPanel imagesPanel;

    private InputImageEventBus eventBus;

    private Camera camera;
    private Timer cameraRunner;

    SimpleCameraInputDialog(JFrame parent) {
        super(parent, true);
        AnimatorInitializer.init(this);
    }

    @Override
    public void createComponents() {
        cameraInputImage = new SimpleImagePanel("RealTime", Color.BLUE);
        backgroundImagePanel = new SimpleBackgroundImagePanel("Background", Color.BLACK);
        depthImage = new SimpleDepthImagePanel("Depthscale", Color.RED);
        camera = new OpenKinectCamera();
        cameraRunner = new Timer("Animator", true);
        eventBus = new InputImageEventBus();
    }

    private JPanel wrapImagePanel(SimpleImagePanel imagePanel) {
        JPanel panel = new JPanel();
        panel.setBorder(new CompoundBorder(new EmptyBorder(10, 10, 10, 10), new LineBorder(Color.BLACK, 2)));
        panel.add(imagePanel);
        return panel;
    }

    @Override
    public void createLayout() {
        getContentPane().setLayout(new BorderLayout());
        imagesPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        imagesPanel.add(wrapImagePanel(cameraInputImage));
        imagesPanel.add(wrapImagePanel(backgroundImagePanel));
        imagesPanel.add(wrapImagePanel(depthImage));
        getContentPane().add(imagesPanel, BorderLayout.CENTER);
    }

    @Override
    public void registerHandlers() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                cameraRunner.cancel();
                cameraRunner.purge();
            }

            @Override
            public void windowClosed(WindowEvent e) {
                stopCamera(camera);
                shutdownCamera(camera);
            }
        });
        eventBus.addFrameGrabbedListener(cameraInputImage, depthImage, backgroundImagePanel);
    }

    @Override
    public void initialize() {
        Dimension imagePanelDimension = new Dimension(400, 300);
        cameraInputImage.setMinimumSize(imagePanelDimension);
        cameraInputImage.setPreferredSize(imagePanelDimension);

        backgroundImagePanel.setMinimumSize(imagePanelDimension);
        backgroundImagePanel.setPreferredSize(imagePanelDimension);

        depthImage.setMinimumSize(imagePanelDimension);
        depthImage.setPreferredSize(imagePanelDimension);

        setResizable(true);
        pack();

        camera.initialize();
        camera.start();
        cameraRunner.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (!camera.isStarted()) {
                    return;
                }

                final CombinedImage grabbedCombinedImage = camera.getGrabbedImage();
                eventBus.setImageGrabbed(grabbedCombinedImage);
                imagesPanel.repaint();
            }
        }, 1000, CAMERA_TASK_PERIOD_IN_MILLIS);
    }

    private void shutdownCamera(Camera camera) {
        if (camera.isInitialized()) {
            try {
                camera.release();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void stopCamera(Camera camera) {
        if (camera.isStarted()) {
            try {
                camera.stop();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
