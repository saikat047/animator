package com.fun.animator.image;

public class DepthImageFilter implements ImageFilter {

    private int minDiffWithBackGroundInCM = 1;

    @Override
    public DepthImage filterColorBasedOnBackground(DepthImage sourceImage, DepthImage backgroundDepthImage) {
        final int minDiffInKinectUnits = minDiffWithBackGroundInCM * DepthImageTransformer.DIFF_CENTIMETER;
        final DepthImage resultDepthImage = new DepthImageImpl(sourceImage.getWidth(), sourceImage.getHeight());
        for (int x = 0; x < sourceImage.getWidth(); x++) {
            for (int y = 0; y < sourceImage.getHeight(); y++) {
                final int originalDepth = sourceImage.getDepth(x, y);
                final int backgroundDepth = backgroundDepthImage.getDepth(x, y);

                int depthValue = DepthImageTransformer.DEPTH_MAX;
                if (originalDepth < DepthImageTransformer.DEPTH_MIN ||
                    originalDepth > DepthImageTransformer.DEPTH_MAX) {
                    // don't do anything
                } else if (Math.abs(backgroundDepth - originalDepth) <= minDiffInKinectUnits) {
                    // don't do anything
                } else {
                    depthValue = originalDepth;
                }
                resultDepthImage.setDepth(x, y, depthValue);
            }
        }
        return resultDepthImage;
    }

    public int getMinDiffWithBackGroundInCM() {
        return minDiffWithBackGroundInCM;
    }

    public void setMinDiffWithBackGroundInCM(int minDiffWithBackGroundInCM) {
        this.minDiffWithBackGroundInCM = minDiffWithBackGroundInCM;
    }
}
