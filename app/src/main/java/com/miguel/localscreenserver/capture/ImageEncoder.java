package com.miguel.localscreenserver.capture;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;

public class ImageEncoder {

    public static byte[] toJpeg(Bitmap bitmap) {

        ByteArrayOutputStream output =
                new ByteArrayOutputStream();

        bitmap.compress(
                Bitmap.CompressFormat.JPEG,
                85,
                output);

        return output.toByteArray();

    }

}