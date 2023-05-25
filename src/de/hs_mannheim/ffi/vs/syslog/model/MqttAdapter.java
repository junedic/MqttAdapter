package de.hs_mannheim.ffi.vs.syslog.model;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MqttAdapter {
    private MqttClient mqttClient;
    private MqttMessageHandler mqttMessageHandler;
    private String topic;

    public MqttAdapter(String mqttBroker, String topic, MqttMessageHandler messageHandler) throws MqttException {
        this.topic = topic;
        this.mqttMessageHandler = messageHandler;
        MemoryPersistence persistence = new MemoryPersistence();
        mqttClient = new MqttClient(mqttBroker, MqttClient.generateClientId(), persistence);
        mqttClient.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                try {
                    connectToBroker();
                    subscribeToTopic();
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                mqttMessageHandler.handleMessage(topic, message);
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                // Not used
            }
        });
        connectToBroker();
        subscribeToTopic();
    }

    private void connectToBroker() throws MqttException {
        mqttClient.connect();
    }

    private void subscribeToTopic() throws MqttException {
        mqttClient.subscribe(topic);
    }

    public void disconnect() throws MqttException {
        mqttClient.disconnect();
    }
}
