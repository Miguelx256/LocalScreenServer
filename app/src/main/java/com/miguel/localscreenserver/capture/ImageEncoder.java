package com.miguel.localscreenserver.capture;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;

public class ImageEncoder {

    private static final ByteArrayOutputStream output =
            new ByteArrayOutputStream(512 * 1024);

    public static synchronized byte[] toJpeg(Bitmap bitmap) {

        output.reset();

        bitmap.compress(
                Bitmap.CompressFormat.JPEG,
                85,
                output);

        return output.toByteArray();

    }

}