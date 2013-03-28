package com.fun.animator.event;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.fun.animator.image.CombinedImage;

public class InputImageEventBus {

    private List<FrameGrabbedListener> listeners = new CopyOnWriteArrayList<FrameGrabbedListener>();

    public void addFrameGrabbedListener(FrameGrabbedListener... listeners) {
        this.listeners.addAll(Arrays.asList(listeners));
    }

    public void removeFrameGrabbedListener(FrameGrabbedListener... listeners) {
        this.listeners.removeAll(Arrays.asList(listeners));
    }

    public void setImageGrabbed(CombinedImage image) {
        for (FrameGrabbedListener listener : listeners) {
            listener.inputFrameGrabbed(image);
        }
    }
}
