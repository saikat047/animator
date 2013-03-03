package com.fun.animator.input;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.FutureTask;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import com.fun.animator.AnimatorInitializer;
import com.fun.animator.LifeCycle;

public class CameraInputDialog extends JDialog implements LifeCycle {

    private JPanel imagePanel;
    private JButton startRecordingButton;
    private JButton stopRecordingButton;
    private JButton playRecordingButton;
    private JTextField delayBetweenTwoFramesField;

    private Camera camera;

    CameraInputDialog(JFrame parent) {
        super(parent, true);
        AnimatorInitializer.init(this);
    }

    @Override
    public void createComponents() {
        imagePanel = new JPanel();
        imagePanel.setBorder(new CompoundBorder(new EmptyBorder(10, 10, 10, 10), new LineBorder(Color.BLACK, 2)));
        startRecordingButton = new JButton("Start Recording");
        stopRecordingButton = new JButton("Stop Recording");
        playRecordingButton = new JButton("Play");
        delayBetweenTwoFramesField = new JTextField("0.2");
        delayBetweenTwoFramesField.setColumns(5);
        camera = new Camera();
    }

    @Override
    public void createLayout() {
        getContentPane().setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new FlowLayout());
        inputPanel.add(new JLabel("Delay:"));
        inputPanel.add(delayBetweenTwoFramesField);

        JPanel leftPanel = new JPanel();
        BoxLayout boxLayout = new BoxLayout(leftPanel, BoxLayout.PAGE_AXIS);
        leftPanel.setLayout(boxLayout);
        leftPanel.add(inputPanel);
        leftPanel.add(startRecordingButton);
        leftPanel.add(stopRecordingButton);
        leftPanel.add(playRecordingButton);
        leftPanel.add(new JPanel());
        getContentPane().add(leftPanel, BorderLayout.LINE_START);

        getContentPane().add(imagePanel, BorderLayout.CENTER);
    }

    @Override
    public void registerHandlers() {
        addWindowStateListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (camera.isStarted()) {
                    camera.stop();
                }
                if (camera.isInitialized()) {
                    camera.release();
                }
            }
        });
    }

    @Override
    public void initialize() {
        Dimension imagePanelDimension = new Dimension(400, 300);
        imagePanel.setMinimumSize(imagePanelDimension);
        imagePanel.setPreferredSize(imagePanelDimension);
        imagePanel.add(new JLabel("saikat"));
        setResizable(true);
        pack();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                camera.initialize();
                camera.start();
            }
        });
    }
}
