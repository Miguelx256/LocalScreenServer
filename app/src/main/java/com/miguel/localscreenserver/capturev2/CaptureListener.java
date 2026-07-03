package com.miguel.localscreenserver.capturev2;

import com.miguel.localscreenserver.capturev2.frame.Frame;

public interface CaptureListener {

    void onFrameCaptured(Frame frame);

}