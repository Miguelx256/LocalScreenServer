package com.miguel.localscreenserver.server;

import com.miguel.localscreenserver.model.FrameBuffer;

import java.io.IOException;
import java.io.InputStream;

public class MJPEGStreamer extends InputStream {

    private byte[] currentFrame = new byte[0];
    private int position = 0;

    @Override
    public int read() throws IOException {

        if (position >= currentFrame.length) {

            byte[] jpeg;

            do {

                jpeg = FrameBuffer.getFrame();

                if (jpeg == null) {

                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException ignored) {
                    }

                }

            } while (jpeg == null);

            String header =
                    "--frame\r\n" +
                            "Content-Type: image/jpeg\r\n" +
                            "Content-Length: " + jpeg.length + "\r\n\r\n";

            byte[] headerBytes = header.getBytes();

            byte[] footerBytes = "\r\n".getBytes();

            currentFrame = new byte[
                    headerBytes.length +
                            jpeg.length +
                            footerBytes.length
                    ];

            System.arraycopy(
                    headerBytes,
                    0,
                    currentFrame,
                    0,
                    headerBytes.length);

            System.arraycopy(
                    jpeg,
                    0,
                    currentFrame,
                    headerBytes.length,
                    jpeg.length);

            System.arraycopy(
                    footerBytes,
                    0,
                    currentFrame,
                    headerBytes.length + jpeg.length,
                    footerBytes.length);

            position = 0;
        }

        return currentFrame[position++] & 0xFF;
    }
}