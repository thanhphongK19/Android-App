package com.example.smarthome;

import static java.nio.charset.StandardCharsets.UTF_16;
import static java.nio.charset.StandardCharsets.UTF_8;

import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient;

import java.nio.ByteBuffer;

public interface MQTTprotocol {
    String host = "a88a000e732a44a8a26e3fe771285fbe.s1.eu.hivemq.cloud";
    String username = "hivemq.webclient.1680854521781";
    String password = "92<kXaAGtul>bH;Y1Q0!";

    // create an MQTT client
    Mqtt5BlockingClient client = MqttClient.builder()
            .useMqttVersion5()
            .serverHost(host)
            .serverPort(8883)
            .sslWithDefaultConfig()
            .buildBlocking();

    static void setupConnectMQTT()
    {

        // connect to HiveMQ Cloud with TLS and username/pw
        client.connectWith()
                .simpleAuth()
                .username(username)
                .password(UTF_8.encode(password))
                .applySimpleAuth()
                .send();

        System.out.println("Connected successfully");

        // subscribe to the topic "my/test/topic"
        client.subscribeWith()
                .topicFilter("my/test/topic")
                .send();

    }


    static void publishData(String topic, String data)
    {
        client.publishWith()
                .topic(topic)
                .payload(UTF_8.encode(data))
                .qos(MqttQos.EXACTLY_ONCE)
                .send();
    }
}
