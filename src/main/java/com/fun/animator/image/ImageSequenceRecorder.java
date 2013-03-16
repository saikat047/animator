package com.fun.animator.image;

public interface ImageSequenceRecorder {

    public void add(CombinedImage combinedImage);

    public void startRecording();

    public void stopRecording();
}
