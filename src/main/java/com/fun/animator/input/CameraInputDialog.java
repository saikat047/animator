package com.fun.animator.input;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;
import java.util.Timer;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.fun.animator.AnimatorInitializer;
import com.fun.animator.LifeCycle;

public class CameraInputDialog extends JDialog implements LifeCycle {

    private static final int CAMERA_TASK_PERIOD_IN_MILLIS = 100;
    private static final File USER_HOME_DIRECTORY = new File(System.getProperty("user.home"));
    private static final File SAVE_DIRECTORY = new File(USER_HOME_DIRECTORY, "kinect-animator");

    private ImagePanel cameraInputImage;
    private ImagePanel staticBackgroundImage;
    private ImagePanel mergedImage;
    private ImagePanel depthImage;
    private JPanel imagesPanel;
    private JButton startRecordingButton;
    private JButton stopRecordingButton;
    private JButton playRecordingButton;
    private JTextField delayBetweenTwoFramesField;
    private JTextField maxAllowedDifferenceInConsequentImageTextField;
    private JToggleButton toggleBackgroundButton;

    private DepthImageTransformer depthImageTransformer = new DefaultDepthImageTransformers();
    private ImageSequenceRecorder imageSequenceRecorder;
    private boolean recording = false;

    private Camera camera;
    private java.util.Timer cameraRunner;

    private Image backgroundImage;
    private long frameDelayInMillis = 20L;

    private int maxAllowedDifferenceInConsequentImagesInCM = 2;

    CameraInputDialog(JFrame parent) {
        super(parent, true);
        AnimatorInitializer.init(this);
    }

    @Override
    public void createComponents() {
        cameraInputImage = new ImagePanel("RealTime", Color.BLUE);
        staticBackgroundImage = new ImagePanel("Background", Color.BLACK);
        mergedImage = new ImagePanel("Difference in subsequent depth frames", Color.WHITE);
        depthImage = new ImagePanel("Depthscale", Color.RED);
        startRecordingButton = new JButton("Start Recording");
        stopRecordingButton = new JButton("Stop Recording");
        toggleBackgroundButton = new JToggleButton("Toggle Background");
        playRecordingButton = new JButton("Play");
        delayBetweenTwoFramesField = new JTextField(Long.toString(frameDelayInMillis));
        delayBetweenTwoFramesField.setColumns(5);
        maxAllowedDifferenceInConsequentImageTextField = new JTextField(Long.toString(maxAllowedDifferenceInConsequentImagesInCM));
        maxAllowedDifferenceInConsequentImageTextField.setColumns(6);
        camera = new OpenKinectCamera();
        cameraRunner = new Timer("Animator", true);
    }

    private JPanel wrapImagePanel(ImagePanel imagePanel) {
        JPanel panel = new JPanel();
        panel.setBorder(new CompoundBorder(new EmptyBorder(10, 10, 10, 10), new LineBorder(Color.BLACK, 2)));
        panel.add(imagePanel);
        return panel;
    }

    @Override
    public void createLayout() {
        getContentPane().setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(2, 2));
        inputPanel.add(new JLabel("Delay (in millis):"));
        inputPanel.add(delayBetweenTwoFramesField);
        inputPanel.add(new JLabel("Max diff (in cm) : "));
        inputPanel.add(maxAllowedDifferenceInConsequentImageTextField);

        JPanel leftPanel = new JPanel();
        BoxLayout boxLayout = new BoxLayout(leftPanel, BoxLayout.PAGE_AXIS);
        leftPanel.setLayout(boxLayout);
        leftPanel.add(inputPanel);
        leftPanel.add(startRecordingButton);
        leftPanel.add(stopRecordingButton);
        leftPanel.add(toggleBackgroundButton);
        leftPanel.add(playRecordingButton);
        leftPanel.add(new JPanel());
        getContentPane().add(leftPanel, BorderLayout.LINE_START);

        imagesPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        imagesPanel.add(wrapImagePanel(cameraInputImage));
        imagesPanel.add(wrapImagePanel(staticBackgroundImage));
        imagesPanel.add(wrapImagePanel(mergedImage));
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

        toggleBackgroundButton.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent event) {
                if (!toggleBackgroundButton.isSelected()) {
                    backgroundImage = null;
                    return;
                }
                backgroundImage = camera.getGrabbedImage().deepCopy();
            }
        });

        delayBetweenTwoFramesField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                try {
                    frameDelayInMillis = Long.parseLong(delayBetweenTwoFramesField.getText());
                } catch (NumberFormatException numEx) {
                }
            }
        });

        maxAllowedDifferenceInConsequentImageTextField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                maxAllowedDifferenceInConsequentImagesInCM = Integer.parseInt(maxAllowedDifferenceInConsequentImageTextField.getText());
            }
        });

        startRecordingButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                imageSequenceRecorder.startRecording();
                recording = true;
                mediateRecordingButtons(recording);
            }
        });

        stopRecordingButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                imageSequenceRecorder.stopRecording();
                recording = false;
                mediateRecordingButtons(recording);
            }
        });
    }

    private void mediateRecordingButtons(boolean recording) {
        startRecordingButton.setEnabled(!recording);
        stopRecordingButton.setEnabled(recording);
    }

    @Override
    public void initialize() {
        stopRecordingButton.setEnabled(false);

        Dimension imagePanelDimension = new Dimension(400, 300);
        cameraInputImage.setMinimumSize(imagePanelDimension);
        cameraInputImage.setPreferredSize(imagePanelDimension);

        staticBackgroundImage.setMinimumSize(imagePanelDimension);
        staticBackgroundImage.setPreferredSize(imagePanelDimension);

        mergedImage.setMinimumSize(imagePanelDimension);
        mergedImage.setPreferredSize(imagePanelDimension);

        depthImage.setMinimumSize(imagePanelDimension);
        depthImage.setPreferredSize(imagePanelDimension);

        setResizable(true);
        pack();

        imageSequenceRecorder = new DefaultImageSequenceRecorder(5, SAVE_DIRECTORY);
        camera.initialize();
        camera.start();
        final CompositeImageWithDepth compositeImage = new CompositeImageWithDepth(3);
        cameraRunner.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    Thread.sleep(Math.max(1, frameDelayInMillis - CAMERA_TASK_PERIOD_IN_MILLIS));
                } catch (InterruptedException e) {
                }

                if (!camera.isStarted()) {
                    return;
                }

                staticBackgroundImage.setImage(backgroundImage == null ? null : depthImageTransformer.convertDepthImage(backgroundImage));

                final Image grabbedImage = camera.getGrabbedImage();
                cameraInputImage.setImage(grabbedImage.getColorImage());
                mergedImage.setImage(diffDepthImage(grabbedImage, compositeImage.isEmpty() ? null : compositeImage));

                final Image grabbedImageCopy = grabbedImage.deepCopy();
                compositeImage.add(grabbedImageCopy);
                depthImage.setImage(depthImageTransformer.convertDepthImage(compositeImage));

                imagesPanel.repaint();

                if (recording) {
                    imageSequenceRecorder.add(grabbedImage);
                }
            }

            private BufferedImage diffDepthImage(Image image, Image previousImage) {
                if (previousImage == null) {
                    return image.getDepthImage();
                }
                final BufferedImage diffImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
                for (int x = 0; x < image.getWidth(); x++) {
                    for (int y = 0; y < image.getHeight(); y++) {
                        int goodRGB = image.getDepth(x, y);
                        int badRGB = previousImage.getDepth(x, y);
                        int difference = Math.abs(goodRGB - badRGB);
                        if (difference <= maxAllowedDifferenceInConsequentImagesInCM * DepthImageTransformer.DIFF_CENTIMETER) {
                            diffImage.setRGB(x, y, Color.BLACK.getRGB());
                        } else {
                            // some difference found
                            diffImage.setRGB(x, y, Color.RED.getRGB());
                        }

                    }
                }
                return diffImage;
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
