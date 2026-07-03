package com.miguel.localscreenserver.serverv2;

import com.miguel.localscreenserver.capturev2.LatestFrame;

import java.io.IOException;
import java.io.InputStream;

public class MJPEGStreamerV2 extends InputStream {

    private final LatestFrame latestFrame;

    private byte[] current = new byte[0];
    private int position = 0;

    public MJPEGStreamerV2(LatestFrame latestFrame) {

        this.latestFrame = latestFrame;

    }

    @Override
    public int read() throws IOException {

        if (position >= current.length) {

            byte[] jpeg;

            do {

                jpeg = latestFrame.getJpeg();

                if (jpeg == null) {

                    try {

                        Thread.sleep(5);

                    } catch (InterruptedException ignored) {
                    }

                }

            } while (jpeg == null);

            String header =
                    "--frame\r\n" +
                            "Content-Type: image/jpeg\r\n" +
                            "Content-Length: " +
                            jpeg.length +
                            "\r\n\r\n";

            byte[] h = header.getBytes();

            current = new byte[
                    h.length +
                            jpeg.length +
                            2
                    ];

            System.arraycopy(
                    h,
                    0,
                    current,
                    0,
                    h.length);

            System.arraycopy(
                    jpeg,
                    0,
                    current,
                    h.length,
                    jpeg.length);

            current[current.length - 2] = '\r';
            current[current.length - 1] = '\n';

            position = 0;

        }

        return current[position++] & 0xff;

    }

}