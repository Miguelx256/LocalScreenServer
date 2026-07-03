package com.miguel.localscreenserver.capturev2;

import com.miguel.localscreenserver.capturev2.frame.Frame;

public class LatestFrame {

    private volatile Frame frame;

    public synchronized void set(Frame frame) {

        this.frame = frame;

    }

    public synchronized Frame get() {

        return frame;

    }

    public synchronized byte[] getJpeg() {

        if (frame == null)
            return null;

        return frame.jpeg;

    }

    public synchronized void clear() {

        frame = null;

    }

}