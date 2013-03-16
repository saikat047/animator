package com.fun.animator.image;

import com.fun.animator.image.Image;

public interface ImageSequenceRecorder {

    public void add(Image image);

    public void startRecording();

    public void stopRecording();
}
