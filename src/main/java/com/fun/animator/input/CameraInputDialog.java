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

    private static final int MAX_DIFF_IN_COLORS = 20;
    private ImagePanel cameraInputImage;
    private ImagePanel staticBackgroundImage;
    private ImagePanel mergedImage;
    private ImagePanel depthImage;
    private JButton startRecordingButton;
    private JButton stopRecordingButton;
    private JButton playRecordingButton;
    private JTextField delayBetweenTwoFramesField;
    private JButton snapBackgroundButton;

    private CachedRGBToColorMapper cachedRGBToColorMapper = new CachedRGBToColorMapper();
    private Camera camera;
    public long frameDelayInMillis = 10L;
    private Thread cameraThread;
    private boolean stopCameraAsap = false;
    private Image backgroundImage;

    private int minDepth = Integer.MAX_VALUE;
    private int maxDepth = Integer.MIN_VALUE;

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
        imagesPanel.add(wrapImagePanel(depthImage));

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
                backgroundImage = camera.getGrabbedImage().deepCopy();
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
                    staticBackgroundImage.setImage(backgroundImage == null ? null : backgroundImage.getColorImage());
                    staticBackgroundImage.repaint();
                    Image grabbedImage = camera.getGrabbedImage();
                    cameraInputImage.setImage(grabbedImage.getColorImage());
                    cameraInputImage.repaint();
                    mergedImage.setImage(filterImage(grabbedImage, backgroundImage));
                    mergedImage.repaint();
                    depthImage.setImage(convertDepthImageToColorImage(grabbedImage));
                    depthImage.repaint();
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

    private BufferedImage convertDepthImageToColorImage(Image grabbedImage) {
        // TODO saikat: convert depth int to a valid color object
        BufferedImage depthImage = grabbedImage.getDepthImage();
        for (int x = 0; x < depthImage.getWidth(); x++) {
            for (int y = 0; y < depthImage.getHeight(); y++) {
                int rgb = -1 * depthImage.getRGB(x, y);
                if (rgb < minDepth) {
                    minDepth = rgb;
                }
                if (rgb > maxDepth) {
                    maxDepth = rgb;
                }
                rgb = convertDepthToColor(rgb);
                depthImage.setRGB(x, y, rgb);
            }
        }
        return depthImage;
    }

    private int convertDepthToColor(int depth) {
        if (minDepth != Integer.MAX_VALUE && maxDepth != Integer.MIN_VALUE) {
            final int colorMin = 100;
            int colorR = (int) Math.min(255.0, colorMin + (255.0 - colorMin) * depth / maxDepth);
            depth = new Color(colorR, colorR, colorR).getRGB();
            /*final int baseColor = 100;
            final int baseDepth = (maxDepth - minDepth) / 3;
            if (depth < baseDepth) {
                final int divider = maxDepth - baseDepth;
                int r, g, b;
                r = Math.min(255, baseColor + (255 - baseColor) * (divider - depth) / divider);
                depth = new Color(r, r, r).getRGB();
            }*/

            return depth;
        }
        return depth;
    }

    private BufferedImage filterImage(Image source, Image background) {
        if (background == null) {
            return source.getColorImage();
        }

        BufferedImage result = new BufferedImage(source.getWidth(), source.getHeight(), source.getColorImageType());
        final int width = background.getWidth();
        final int height = background.getHeight();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Color backgroundColor = cachedRGBToColorMapper.map(background.getRGB(x, y));
                Color sourceColor = cachedRGBToColorMapper.map(source.getRGB(x, y));
                if (isColorsAlmostSame(backgroundColor, sourceColor)) {
                    result.setRGB(x, y, Color.BLACK.getRGB());
                } else {
                    result.setRGB(x, y, sourceColor.getRGB());
                }
            }
        }
        return result;
    }

    private boolean isColorsAlmostSame(Color backgroundColor, Color sourceColor) {
        final int diffBlue = Math.abs(sourceColor.getBlue() - backgroundColor.getBlue());
        final int diffRed = Math.abs(sourceColor.getRed() - backgroundColor.getRed());
        final int diffGreen = Math.abs(sourceColor.getGreen() - backgroundColor.getGreen());
        final int diffAlpha = Math.abs(sourceColor.getAlpha() - backgroundColor.getAlpha());

        return diffBlue < MAX_DIFF_IN_COLORS &&
               diffRed < MAX_DIFF_IN_COLORS &&
               diffGreen < MAX_DIFF_IN_COLORS &&
               diffAlpha < MAX_DIFF_IN_COLORS;
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
