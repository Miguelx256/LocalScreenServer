package com.miguel.localscreenserver.capturev2;

import com.miguel.localscreenserver.capture.ImageEncoder;
import com.miguel.localscreenserver.capturev2.frame.Frame;
import com.miguel.localscreenserver.model.FrameBuffer;

public class EncoderThread extends Thread {

    private final FrameQueue queue;

    private volatile boolean running = true;

    private final LatestFrame latestFrame;

    public EncoderThread(FrameQueue queue,
                         LatestFrame latestFrame) {

        this.queue = queue;
        this.latestFrame = latestFrame;

        setName("EncoderThread");

    }

    @Override
    public void run() {

        while (running) {

            try {

                Frame frame = queue.take();

                if (frame == null)
                    continue;

                frame.jpeg = ImageEncoder.toJpeg(frame.bitmap);

                latestFrame.set(frame);

            } catch (InterruptedException e) {

                interrupt();

            }

        }

    }

    public void shutdown() {

        running = false;

        interrupt();

    }

}