package de.hs_mannheim.ffi.vs.syslog.model;

import org.eclipse.paho.client.mqttv3.MqttMessage;

public interface MqttMessageHandler {
    void handleMessage(String topic, MqttMessage message);
}
