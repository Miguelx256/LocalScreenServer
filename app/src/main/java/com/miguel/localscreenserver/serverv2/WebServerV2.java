package com.miguel.localscreenserver.serverv2;

import com.miguel.localscreenserver.capturev2.LatestFrame;

import fi.iki.elonen.NanoHTTPD;

public class WebServerV2 extends NanoHTTPD {

    private final LatestFrame latestFrame;

    public WebServerV2(LatestFrame latestFrame) {

        super(8080);

        this.latestFrame = latestFrame;

    }

    @Override
    public Response serve(IHTTPSession session) {

        String uri = session.getUri();

        if (uri.equals("/frame")) {

            byte[] jpeg = latestFrame.getJpeg();

            if (jpeg == null) {

                return newFixedLengthResponse(
                        Response.Status.NO_CONTENT,
                        "text/plain",
                        "No Frame");

            }

            return newFixedLengthResponse(
                    Response.Status.OK,
                    "image/jpeg",
                    new java.io.ByteArrayInputStream(jpeg),
                    jpeg.length);

        }

        if (uri.equals("/stream")) {

            return newChunkedResponse(
                    Response.Status.OK,
                    "multipart/x-mixed-replace; boundary=frame",
                    new MJPEGStreamerV2(latestFrame));

        }

        String html =
                "<html>" +
                        "<head>" +
                        "<meta name='viewport' content='width=device-width,initial-scale=1'>" +
                        "<style>" +
                        "html,body{" +
                        "margin:0;" +
                        "padding:0;" +
                        "background:#000;" +
                        "overflow:hidden;" +
                        "}" +
                        "#screen{" +
                        "width:100vw;" +
                        "height:100vh;" +
                        "object-fit:contain;" +
                        "}" +
                        "</style>" +
                        "</head>" +
                        "<body>" +
                        "<img id='screen' src='/stream'>" +
                        "</body>" +
                        "</html>";

        return newFixedLengthResponse(html);

    }

}