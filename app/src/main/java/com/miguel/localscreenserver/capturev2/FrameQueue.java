package com.miguel.localscreenserver.capturev2;

import com.miguel.localscreenserver.capturev2.frame.Frame;

import java.util.concurrent.ArrayBlockingQueue;

public class FrameQueue {

    private final ArrayBlockingQueue<Frame> queue =
            new ArrayBlockingQueue<>(1);

    public synchronized void offer(Frame frame) {

        queue.clear();

        queue.offer(frame);

    }

    public Frame take() throws InterruptedException {

        return queue.take();

    }

}