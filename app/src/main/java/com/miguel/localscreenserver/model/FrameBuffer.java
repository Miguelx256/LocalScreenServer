package com.miguel.localscreenserver.model;

public class FrameBuffer {

    private static byte[] frame;

    public static synchronized void setFrame(byte[] jpeg) {
        frame = jpeg;
    }

    public static synchronized byte[] getFrame() {
        return frame;
    }

    public static synchronized void clear() {
        frame = null;
    }

}