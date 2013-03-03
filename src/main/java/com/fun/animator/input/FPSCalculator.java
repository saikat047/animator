package com.fun.animator.input;

public class FPSCalculator {

    private long currentTime;
    private long previousTime = -1;
    private int numOfFrames = 0;
    private int fps = 0;

    public void updateFPSRendered() {
        if (previousTime == -1) {
            previousTime = System.currentTimeMillis();
            return;
        }
        currentTime = System.currentTimeMillis();
        numOfFrames += 1;
    }

    public int getFPS() {
        long diffInMillis = currentTime - previousTime;
        if (diffInMillis > 1000) {
            fps = (int) (numOfFrames * 1000 / (diffInMillis));
            previousTime = currentTime;
            numOfFrames = 0;
        }
        return fps;
    }
}
