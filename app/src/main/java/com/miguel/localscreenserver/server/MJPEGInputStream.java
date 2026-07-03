package com.miguel.localscreenserver.server;

import com.miguel.localscreenserver.model.FrameBuffer;

import java.io.IOException;
import java.io.InputStream;

public class MJPEGInputStream extends InputStream {

    private byte[] current = new byte[0];
    private int index = 0;

    @Override
    public int read() throws IOException {

        if (index >= current.length) {

            byte[] jpeg = FrameBuffer.getFrame();

            if (jpeg == null) {

                try {
                    Thread.sleep(50);
                } catch (InterruptedException ignored) {}

                return read();
            }

            String header =
                    "--frame\r\n" +
                            "Content-Type: image/jpeg\r\n" +
                            "Content-Length: " + jpeg.length + "\r\n\r\n";

            byte[] head = header.getBytes();

            byte[] end = "\r\n".getBytes();

            current = new byte[
                    head.length +
                            jpeg.length +
                            end.length
                    ];

            System.arraycopy(head,0,current,0,head.length);

            System.arraycopy(
                    jpeg,
                    0,
                    current,
                    head.length,
                    jpeg.length
            );

            System.arraycopy(
                    end,
                    0,
                    current,
                    head.length + jpeg.length,
                    end.length
            );

            index = 0;
        }

        return current[index++] & 0xff;
    }
}