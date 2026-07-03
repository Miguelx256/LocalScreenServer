package com.miguel.localscreenserver.capturev2;

import android.graphics.Bitmap;

public class BitmapPool {

    private Bitmap bitmap;

    private int width;
    private int height;

    public synchronized Bitmap get(int width, int height) {

        if (bitmap == null
                || this.width != width
                || this.height != height) {

            bitmap = Bitmap.createBitmap(
                    width,
                    height,
                    Bitmap.Config.ARGB_8888);

            this.width = width;
            this.height = height;

        }

        return bitmap;

    }

    public synchronized void clear() {

        if (bitmap != null) {

            bitmap.recycle();
            bitmap = null;

        }

    }

}