package de.hs_mannheim.ffi.vs.syslog.model;
    
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.net.URI;

import de.hs_mannheim.ffi.vs.syslog.model.SyslogMessage;

@ServerEndpoint("/websocket")
public class MqttAdapter {
    private MqttClient mqttClient;
    private Session websocketSession;
    private String mqttBroker1;
    private String mqttBroker2;
    private String topic1;
    private String topic2;
    private String websocketUrl;

    public MqttAdapter(String mqttBroker1, String mqttBroker2, String topic1, String topic2, String websocketUrl) {
        this.mqttBroker1 = mqttBroker1;
        this.mqttBroker2 = mqttBroker2;
        this.topic1 = topic1;
        this.topic2 = topic2;
        this.websocketUrl = websocketUrl;
    }

    public void connect() throws MqttException, Exception {
        MemoryPersistence persistence = new MemoryPersistence();
        mqttClient = new MqttClient(mqttBroker1, MqttClient.generateClientId(), persistence);
        mqttClient.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                // Reconnect to MQTT broker
                try {
                    connectToBroker();
                    subscribeToTopics();
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                // Check if the message contains a SyslogMessage object
                byte[] payload = message.getPayload();
                Object obj = deserializeObject(payload);
                if (obj instanceof SyslogMessage) {
                    SyslogMessage syslogMessage = (SyslogMessage) obj;
                    if (syslogMessage.getSeverity() >= SyslogMessage.Severity.WARNING) {
                        // Forward the SyslogMessage object to the WebSocket
                        try {
                            websocketSession.getBasicRemote().sendObject(syslogMessage);
                        } catch (EncodeException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                // Not used
            }
        });

        connectToBroker();
        subscribeToTopics();
        connectToWebSocket();
    }

    private void connectToBroker() throws MqttException {
        mqttClient.connect();
    }

    private void subscribeToTopics() throws MqttException {
        mqttClient.subscribe(topic1);
        mqttClient.subscribe(topic2);
    }

    private void connectToWebSocket() throws Exception {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        URI uri = new URI(websocketUrl);
        Session session = container.connectToServer(WebSocketClientEndpoint.class, uri);
        this.websocketSession = session;
    }

    private Object deserializeObject(byte[] bytes) throws Exception {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ObjectInputStream in = new ObjectInputStream(bis);
        return in.readObject();
    }

    public static void main(String[] args) {
        String mqttBroker1 = "tcp://localhost:1883";
        String mqttBroker2 = "tcp://otherbroker:1883";
        String topic1 = "topic1";
        String topic2 = "topic2";
        String websocketUrl = "ws://localhost:8080/websocket";

        MqttAdapter adapter = new MqttAdapter(mqttBroker1, mqttBroker2, topic1, topic2, websocketUrl);
        try {
            adapter.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

