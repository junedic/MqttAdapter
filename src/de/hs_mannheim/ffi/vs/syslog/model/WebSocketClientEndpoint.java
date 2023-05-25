package de.hs_mannheim.ffi.vs.syslog.model;
    
import javax.websocket.*;
import java.io.IOException;

import de.hs_mannheim.ffi.vs.syslog.model.SyslogMessage;

@ClientEndpoint(encoders = { SyslogMessageEncoder.class }, decoders = { SyslogMessageDecoder.class })
public class WebSocketClientEndpoint {
    private Session session;

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
    }

    @OnMessage
    public void onMessage(Session session, SyslogMessage message) {
        // Handle WebSocket message
        System.out.println("Received message from WebSocket: " + message);
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        // WebSocket connection closed
        System.out.println("WebSocket connection closed: " + closeReason.getReasonPhrase());
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        // WebSocket error
        throwable.printStackTrace();
    }

    public void sendMessage(SyslogMessage message) {
        try {
            session.getBasicRemote().sendObject(message);
        } catch (IOException | EncodeException e) {
            e.printStackTrace();
        }
    }
}
