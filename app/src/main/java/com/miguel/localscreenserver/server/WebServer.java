package com.miguel.localscreenserver.server;

import com.miguel.localscreenserver.model.FrameBuffer;

import java.io.ByteArrayInputStream;

import fi.iki.elonen.NanoHTTPD;

public class WebServer extends NanoHTTPD {

    public WebServer() {
        super(8080);
    }

    @Override
    public Response serve(IHTTPSession session) {

        String uri = session.getUri();

        if (uri.equals("/frame")) {

            byte[] jpeg = FrameBuffer.getFrame();

            if (jpeg == null) {

                return newFixedLengthResponse(
                        Response.Status.NO_CONTENT,
                        "text/plain",
                        "Sin imagen"
                );

            }

            return newFixedLengthResponse(
                    Response.Status.OK,
                    "image/jpeg",
                    new ByteArrayInputStream(jpeg),
                    jpeg.length
            );

        }

        String html =
                "<html>" +
                        "<head>" +
                        "<script>" +
                        "setInterval(function(){" +
                        "document.getElementById('screen').src='/frame?t='+Date.now();" +
                        "},100);" +
                        "</script>" +
                        "</head>" +
                        "<body style='text-align:center;background:#202124;color:white'>" +
                        "<h2>Local Screen Server</h2>" +
                        "<img id='screen' src='/frame' width='360'>" +
                        "</body>" +
                        "</html>";

        return newFixedLengthResponse(
                Response.Status.OK,
                "text/html",
                html
        );
    }
}