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
import com.fun.animator.image.CombinedImage;
import com.fun.animator.image.CompositeDepthImage;
import com.fun.animator.image.DefaultDepthImageTransformers;
import com.fun.animator.image.DefaultImageSequenceRecorder;
import com.fun.animator.image.DepthImage;
import com.fun.animator.image.DepthImageFilter;
import com.fun.animator.image.DepthImageTransformer;
import com.fun.animator.image.ImagePanel;
import com.fun.animator.image.ImageSequenceRecorder;

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

    private DepthImage backgroundDepthImage;
    private long frameDelayInMillis = 20L;

    private int maxAllowedDifferenceInConsequentImagesInCM = 2;
    private CompositeDepthImage compositeDepthImage;
    private DepthImageFilter depthImageFilter = new DepthImageFilter();

    private JTextArea imageRegionInfo = new JTextArea("CombinedImage Region Info", 10, 10);

    CameraInputDialog(JFrame parent) {
        super(parent, true);
        AnimatorInitializer.init(this);
    }

    @Override
    public void createComponents() {
        cameraInputImage = new ImagePanel("RealTime", Color.BLUE);
        staticBackgroundImage = new ImagePanel("Background", Color.BLACK);
        mergedImage = new ImagePanel("Difference in subsequent depth frames", Color.WHITE, Color.YELLOW);
        depthImage = new ImagePanel("Depthscale", Color.RED);
        startRecordingButton = new JButton("Start Recording");
        stopRecordingButton = new JButton("Stop Recording");
        toggleBackgroundButton = new JToggleButton("Toggle Background");
        playRecordingButton = new JButton("Play");
        delayBetweenTwoFramesField = new JTextField(Long.toString(frameDelayInMillis));
        delayBetweenTwoFramesField.setColumns(5);
        maxAllowedDifferenceInConsequentImageTextField = new JTextField(Long.toString(maxAllowedDifferenceInConsequentImagesInCM));
        maxAllowedDifferenceInConsequentImageTextField.setColumns(6);
        compositeDepthImage = new CompositeDepthImage(3);
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
        leftPanel.add(imageRegionInfo);
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
                    backgroundDepthImage = null;
                    return;
                }
                backgroundDepthImage = compositeDepthImage.createCopy();
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
                depthImageFilter.setMinDiffWithBackGroundInCM(maxAllowedDifferenceInConsequentImagesInCM);
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

        cameraInputImage.addRegionSelectionListener(new ImagePanel.RegionSelectionListener() {
            @Override
            public void regionSelected(BufferedImage depthImage, Point start, Rectangle rectangle) {
            }
        });

        cameraInputImage.addRegionSelectionListener(new RegionSelectionListenerImpl("CameraInput"));
        mergedImage.addRegionSelectionListener(new RegionSelectionListenerImpl("MergedImage"));
        depthImage.addRegionSelectionListener(new RegionSelectionListenerImpl("DepthImage"));
        staticBackgroundImage.addRegionSelectionListener(new RegionSelectionListenerImpl("BackgroundImage"));
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

                staticBackgroundImage.setImage(
                        backgroundDepthImage == null ? null : depthImageTransformer.createColorImage(backgroundDepthImage));

                final CombinedImage grabbedCombinedImage = camera.getGrabbedImage();
                BufferedImage colorImage = grabbedCombinedImage.getColorImage();
                if (backgroundDepthImage != null) {
                    colorImage = depthImageFilter.filterColorBasedOnBackground(grabbedCombinedImage, backgroundDepthImage);
                }
                cameraInputImage.setImage(colorImage);

                if (backgroundDepthImage != null) {
                    mergedImage.setImage(diffDepthImage(grabbedCombinedImage, backgroundDepthImage));
                } else {
                    mergedImage.setImage(diffDepthImage(grabbedCombinedImage, compositeDepthImage.isEmpty() ? null : compositeDepthImage));
                }

                final DepthImage grabbedDepthImageCopy = grabbedCombinedImage.getDepthImage().createCopy();
                compositeDepthImage.add(grabbedDepthImageCopy);
                depthImage.setImage(depthImageTransformer.createColorImage(grabbedDepthImageCopy));

                imagesPanel.repaint();

                if (recording) {
                    imageSequenceRecorder.add(grabbedCombinedImage);
                }
            }

            private BufferedImage diffDepthImage(CombinedImage combinedImage, DepthImage backgroundDepthImage) {
                final DepthImage sourceDepthImage = combinedImage.getDepthImage();
                if (backgroundDepthImage == null) {
                    return depthImageTransformer.createColorImage(sourceDepthImage);
                }
                return depthImageFilter.filterColorBasedOnBackground(combinedImage, backgroundDepthImage);
            }
        }, 1000, CAMERA_TASK_PERIOD_IN_MILLIS);
    }

    private class RegionSelectionListenerImpl implements ImagePanel.RegionSelectionListener {
        private final String regionName;

        public RegionSelectionListenerImpl(String regionName) {
            this.regionName = regionName;
        }

        @Override
        public void regionSelected(BufferedImage depthImage, Point start, Rectangle rectangle) {
            StringBuilder infoBuilder = new StringBuilder();
            infoBuilder.append("ImagePanel : " + regionName).append("\n");
            int minValue = Integer.MAX_VALUE;
            int maxValue = 0;
            for (int x = (int) start.getX(); x <= start.getX() + rectangle.getWidth(); x++) {
                for (int y = (int) start.getY(); y <= start.getY() + rectangle.getHeight(); y++) {
                    final int value = depthImage.getRGB(x, y);
                    if (value < minValue) {
                        minValue = value;
                    }
                    if (value > maxValue) {
                        maxValue = value;
                    }
                }
            }
            infoBuilder.append("Min : 0x" + Integer.toHexString(minValue)).append("\n");
            infoBuilder.append("Max : 0x" + Integer.toHexString(maxValue)).append("\n");
            infoBuilder.append("Selection (x, y) : (" + start.getX() + ", " + start.getY() + ")").append("\n");
            infoBuilder.append("Selection (width, height) : (" + rectangle.getWidth() + ", " + rectangle.getHeight() + ")").append("\n");
            imageRegionInfo.setText(infoBuilder.toString());
        }
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
