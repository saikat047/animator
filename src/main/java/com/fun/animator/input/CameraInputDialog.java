package com.fun.animator.input;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import com.fun.animator.AnimatorInitializer;
import com.fun.animator.LifeCycle;

public class CameraInputDialog extends JDialog implements LifeCycle {

    private ImagePanel cameraInputImage;
    private ImagePanel staticBackgroundImage;
    private ImagePanel mergedImage;
    private ImagePanel grayScaleImage;
    private JButton startRecordingButton;
    private JButton stopRecordingButton;
    private JButton playRecordingButton;
    private JTextField delayBetweenTwoFramesField;
    private JButton snapBackgroundButton;

    private Camera camera;
    public long frameDelayInMillis = 80L;
    private Thread cameraThread;
    private boolean stopCameraAsap = false;

    CameraInputDialog(JFrame parent) {
        super(parent, true);
        AnimatorInitializer.init(this);
    }

    @Override
    public void createComponents() {
        cameraInputImage = new ImagePanel("RealTime", Color.BLUE);
        staticBackgroundImage = new ImagePanel("Background", Color.BLACK);
        mergedImage = new ImagePanel("Merged display", Color.GREEN);
        grayScaleImage = new ImagePanel("Grayscale", Color.RED);
        startRecordingButton = new JButton("Start Recording");
        stopRecordingButton = new JButton("Stop Recording");
        snapBackgroundButton = new JButton("Snap Background");
        playRecordingButton = new JButton("Play");
        delayBetweenTwoFramesField = new JTextField(Long.toString(frameDelayInMillis));
        delayBetweenTwoFramesField.setColumns(5);
        camera = new Camera();
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

        JPanel imagesPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        imagesPanel.add(wrapImagePanel(cameraInputImage));
        imagesPanel.add(wrapImagePanel(staticBackgroundImage));
        imagesPanel.add(wrapImagePanel(mergedImage));
        imagesPanel.add(wrapImagePanel(grayScaleImage));

        getContentPane().add(imagesPanel, BorderLayout.CENTER);
    }

    @Override
    public void registerHandlers() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                stopCameraAsap = true;
                try {
                    cameraThread.join();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void windowClosed(WindowEvent e) {
                stopCamera();
                shutdownCamera();
            }
        });

        snapBackgroundButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                staticBackgroundImage.setImage(camera.getGrabbedImage());
            }
        });

        cameraThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!stopCameraAsap) {
                    try {
                        Thread.sleep(frameDelayInMillis);
                    } catch (InterruptedException e) {
                    }
                    staticBackgroundImage.repaint();
                    BufferedImage grabbedImage = camera.getGrabbedImage();
                    cameraInputImage.setImage(grabbedImage);
                    cameraInputImage.repaint();
                    mergedImage.setImage(grabbedImage);
                    mergedImage.repaint();
                    grayScaleImage.setImage(grabbedImage);
                    grayScaleImage.repaint();
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

        grayScaleImage.setMinimumSize(imagePanelDimension);
        grayScaleImage.setPreferredSize(imagePanelDimension);


        setResizable(true);
        pack();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                camera.initialize();
                camera.start();
                cameraThread.start();
            }
        });
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
