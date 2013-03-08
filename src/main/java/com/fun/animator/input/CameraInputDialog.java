package com.fun.animator.input;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.Timer;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import com.fun.animator.AnimatorInitializer;
import com.fun.animator.LifeCycle;

public class CameraInputDialog extends JDialog implements LifeCycle {

    private static final int CAMERA_TASK_PERIOD_IN_MILLIS = 20;
    private static final String BACKGROUND_DEPTH_IMAGE_FILE_NAME = "kinect-depth.png";
    private static final File USER_HOME_DIRECTORY = new File(System.getProperty("user.home"));

    private ImagePanel cameraInputImage;
    private ImagePanel staticBackgroundImage;
    private ImagePanel mergedImage;
    private ImagePanel depthImage;
    private JPanel imagesPanel;
    private JButton startRecordingButton;
    private JButton stopRecordingButton;
    private JButton playRecordingButton;
    private JTextField delayBetweenTwoFramesField;
    private JButton snapBackgroundButton;

    private DepthImageTransformer depthImageTransformer = new DefaultDepthImageTransformers();
    private Camera camera;
    private java.util.Timer cameraRunner;

    private Image backgroundImage;
    public long frameDelayInMillis = 10L;

    CameraInputDialog(JFrame parent) {
        super(parent, true);
        AnimatorInitializer.init(this);
    }

    @Override
    public void createComponents() {
        cameraInputImage = new ImagePanel("RealTime", Color.BLUE);
        staticBackgroundImage = new ImagePanel("Background", Color.BLACK);
        mergedImage = new ImagePanel("Merged display", Color.GREEN);
        depthImage = new ImagePanel("Depthscale", Color.RED);
        startRecordingButton = new JButton("Start Recording");
        stopRecordingButton = new JButton("Stop Recording");
        snapBackgroundButton = new JButton("Snap Background");
        playRecordingButton = new JButton("Play");
        delayBetweenTwoFramesField = new JTextField(Long.toString(frameDelayInMillis));
        delayBetweenTwoFramesField.setColumns(5);
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

        JPanel inputPanel = new JPanel(new FlowLayout());
        inputPanel.add(new JLabel("Delay (in millis):"));
        inputPanel.add(delayBetweenTwoFramesField);

        JPanel leftPanel = new JPanel();
        BoxLayout boxLayout = new BoxLayout(leftPanel, BoxLayout.PAGE_AXIS);
        leftPanel.setLayout(boxLayout);
        leftPanel.add(inputPanel);
        leftPanel.add(startRecordingButton);
        leftPanel.add(stopRecordingButton);
        leftPanel.add(snapBackgroundButton);
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
                stopCamera();
                shutdownCamera();
            }
        });

        snapBackgroundButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent event) {
                backgroundImage = camera.getGrabbedImage().deepCopy();
                final File outputFile = new File(USER_HOME_DIRECTORY, BACKGROUND_DEPTH_IMAGE_FILE_NAME);
                try {
                    ImageIO.write(backgroundImage.getDepthImage(), "png", outputFile);
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println(String.format("Unable to write to: %s", outputFile.getAbsolutePath()));
                }
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
    }

    @Override
    public void initialize() {
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

        camera.initialize();
        camera.start();
        cameraRunner.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    Thread.sleep(Math.max(1, frameDelayInMillis - CAMERA_TASK_PERIOD_IN_MILLIS));
                } catch (InterruptedException e) {
                }
                staticBackgroundImage.setImage(backgroundImage == null ? null : backgroundImage.getColorImage());
                Image grabbedImage = camera.getGrabbedImage();
                cameraInputImage.setImage(grabbedImage.getColorImage());
                // TODO saikat: do something using the background depth-image and foreground-image
                // mergedImage.setImage(filterImage(grabbedImage, backgroundImage));
                depthImage.setImage(depthImageTransformer.convertDepthImage(grabbedImage.getDepthImage()));
                imagesPanel.repaint();
            }
        }, 1000, CAMERA_TASK_PERIOD_IN_MILLIS);
    }

    private void shutdownCamera() {
        if (camera.isInitialized()) {
            try {
                camera.release();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void stopCamera() {
        if (camera.isStarted()) {
            try {
                camera.stop();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
