package dev.dalame;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.FileReader;
import java.io.IOException;
import java.time.Instant;
import java.util.Date;
import java.util.Properties;
import java.util.function.BiConsumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    public static double temperature = 0;
    public static double humidity = 0;
    private static Logger logger;
    private static Properties config;
    private static InfluxDBClient client;
    private static String influxdbBucket;
    private static String influxdbOrg;

    private static boolean loadConfig() {
        config = new Properties();
        try {
            config.load(new FileReader("config.properties"));
            return true;
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error loading config file",e);
        }
        return false;
    }

    public static void main(String[] args) throws InterruptedException, MqttException {
        if (!loadConfig()) return;

        logger.info("Config file loaded");

        client = InfluxDBClientFactory.create(config.getProperty("influxdb-url"),
                config.getProperty("influxdb-token").toCharArray());

        influxdbBucket = config.getProperty("influxdb-bucket");
        influxdbOrg = config.getProperty("influxdb-org");

        Mqtt mqttClient = new Mqtt("tcp://cloud.tbz.ch:1883", "dalama");
        try {
            mqttClient.start();
            mqttClient.subscribe("Dalama/Subscribe");
            mqttClient.subscribe("Dalama/Temp");
            mqttClient.publish("Dalama/Publish", new Date().toString());
        } catch (MqttException e) {
            e.printStackTrace();
        }

//        mqttClient.addHandler((s, mqttMessage) -> System.out.printf("Received message from %s: %s%n", s, mqttMessage.toString()));

        mqttClient.addHandler(new BiConsumer<String, MqttMessage>() {
            @Override
            public void accept(String s, MqttMessage mqttMessage) {
                if (s.equals("m5core2/temp")) {
                    temperature = Double.parseDouble(mqttMessage.toString());
                }
                if (s.equals("m5core2/hum")) {
                    humidity = Double.parseDouble(mqttMessage.toString());
                }
            }
        });

        double lastTemperature = temperature;

        while (true) {
            if (Math.abs(lastTemperature - temperature) >= 1) {
                System.out.printf("Temperature changed. Current: %.2f%n", temperature);
                Point point = Point
                        .measurement("temperature")
                        .addTag("host", "host1")
                        .addField("temperature", temperature)
                        .time(Instant.now(), WritePrecision.NS);

                WriteApiBlocking writeApi = client.getWriteApiBlocking();
                writeApi.writePoint(influxdbBucket, influxdbOrg, point);

                lastTemperature = temperature;
            }
            Thread.sleep(1000);
        }
    }
}