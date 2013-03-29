package com.fun.animator.input;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import com.fun.animator.AnimatorInitializer;
import com.fun.animator.LifeCycle;
import com.fun.animator.event.InputImageEventBus;
import com.fun.animator.image.CombinedImage;
import com.fun.animator.image.DepthImagePanel;
import com.fun.animator.image.ImagePanel;

public class KinectCameraCalibration extends JDialog implements LifeCycle {

    private JTextArea colorImagePanelInfo;
    private JTextArea depthImagePanelInfo;
    private Camera camera;
    private ImagePanel colorImagePanel;
    private DepthImagePanel depthImagePanel;
    private JPanel imagesPanel;
    private Thread imageGrabberThread;
    private InputImageEventBus eventBus;
    private boolean systemExiting;
    private JToggleButton snapBackgroundButton;

    @Override
    public void createComponents() {
        camera = new OpenKinectCamera();
        colorImagePanel = new ImagePanel("Color", Color.BLACK);
        depthImagePanel = new DepthImagePanel("Depth", Color.RED);
        eventBus = new InputImageEventBus();
        snapBackgroundButton = new JToggleButton("Take Snapshot");
        colorImagePanelInfo = createImageInfoTextArea();
        depthImagePanelInfo = createImageInfoTextArea();
    }

    private JTextArea createImageInfoTextArea() {
        JTextArea textArea = new JTextArea();
        textArea.setColumns(30);
        textArea.setRows(10);
        textArea.setBorder(new LineBorder(Color.BLACK, 2, true));
        return textArea;
    }

    @Override
    public void createLayout() {
        getContentPane().setLayout(new BorderLayout());
        imagesPanel = new JPanel();
        imagesPanel.add(wrapImagePanel(colorImagePanel));
        imagesPanel.add(wrapImagePanel(depthImagePanel));
        getContentPane().add(imagesPanel, BorderLayout.CENTER);
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(snapBackgroundButton, BorderLayout.PAGE_START);
        leftPanel.add(colorImagePanelInfo, BorderLayout.CENTER);
        leftPanel.add(depthImagePanelInfo, BorderLayout.PAGE_END);
        getContentPane().add(leftPanel, BorderLayout.LINE_START);
    }

    private JPanel wrapImagePanel(ImagePanel imagePanel) {
        JPanel panel = new JPanel();
        panel.setBorder(new CompoundBorder(new EmptyBorder(10, 10, 10, 10), new LineBorder(Color.BLACK, 2)));
        panel.add(imagePanel);
        return panel;
    }

    @Override
    public void registerHandlers() {
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                systemExiting = true;
                try {
                    imageGrabberThread.join();
                } catch (InterruptedException e1) {
                }
                camera.stop();
                camera.release();
            }
        });
        eventBus.addFrameGrabbedListener(colorImagePanel, depthImagePanel);
        snapBackgroundButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (snapBackgroundButton.isSelected()) {
                    eventBus.removeFrameGrabbedListener(colorImagePanel, depthImagePanel);
                } else {
                    eventBus.addFrameGrabbedListener(colorImagePanel, depthImagePanel);
                }
            }
        });
    }

    @Override
    public void initialize() {
        Dimension imagePanelDimension = new Dimension(400, 300);
        colorImagePanel.setMinimumSize(imagePanelDimension);
        colorImagePanel.setPreferredSize(imagePanelDimension);

        depthImagePanel.setMinimumSize(imagePanelDimension);
        depthImagePanel.setPreferredSize(imagePanelDimension);

        setResizable(true);
        pack();

        camera.initialize();
        camera.start();
        imageGrabberThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!systemExiting) {
                    CombinedImage grabbedImage = camera.getGrabbedImage();
                    eventBus.setImageGrabbed(grabbedImage);
                    imagesPanel.repaint();
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                    }
                }
            }
        });
        imageGrabberThread.start();
    }

    public static void main(String [] argv) {
        KinectCameraCalibration dialog = new KinectCameraCalibration();
        AnimatorInitializer.init(dialog);
        dialog.setVisible(true);
    }
}
