import de.hs_mannheim.ffi.vs.syslog.model.SyslogMessage;

import javax.websocket.*;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.net.URI;

@ClientEndpoint
public class WebSocketClientEndpoint {
    private Session websocketSession;

    public WebSocketClientEndpoint(String websocketUrl) {
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            URI uri = new URI(websocketUrl);
            Session session = container.connectToServer(this, uri);
            this.websocketSession = session;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("WebSocket connection opened");
    }

    @OnMessage
    public void onMessage(Session session, Object message) {
        // Handle WebSocket message if needed
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        System.out.println("WebSocket connection closed: " + closeReason.getReasonPhrase());
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.out.println("WebSocket error: " + throwable.getMessage());
    }

    public void sendMessage(Object message) throws Exception {
        websocketSession.getBasicRemote().sendObject(message);
    }
}
