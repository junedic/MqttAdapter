package de.hs_mannheim.ffi.vs.syslog.model;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class Main {
    public static void main(String[] args) {
        String mqttBroker1 = "tcp://localhost:1883";
        String mqttBroker2 = "tcp://otherbroker:1883";
        String topic1 = "topic1";
        String topic2 = "topic2";
        String websocketUrl = "ws://localhost:8080/websocket";

        MqttMessageHandler messageHandler = new MqttMessageHandler() {
            @Override
            public void handleMessage(String topic, MqttMessage message) {
                byte[] payload = message.getPayload();
                try {
                    Object obj = deserializeObject(payload);
                    if (obj instanceof SyslogMessage) {
                        SyslogMessage syslogMessage = (SyslogMessage) obj;
                        if (syslogMessage.getSeverity() >= SyslogMessage.Severity.WARNING) {
                            WebSocketClientEndpoint websocketClient = new WebSocketClientEndpoint(websocketUrl);
                            websocketClient.sendMessage(syslogMessage);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            private Object deserializeObject(byte[] bytes) throws Exception {
                ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
                ObjectInputStream in = new ObjectInputStream(bis);
                return in.readObject();
            }
        };

        try {
            MqttAdapter adapter1 = new MqttAdapter(mqttBroker1, topic1, messageHandler);
            MqttAdapter adapter2 = new MqttAdapter(mqttBroker2, topic2, messageHandler);

            // Sleep to keep the program running
            Thread.sleep(Long.MAX_VALUE);
        } catch (InterruptedException | MqttException e) {
            e.printStackTrace();
        }
    }
}
