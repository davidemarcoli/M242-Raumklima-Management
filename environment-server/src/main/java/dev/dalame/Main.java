package dev.dalame;

import com.influxdb.client.DeleteApi;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.DeletePredicateRequest;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import okhttp3.OkHttpClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.FileReader;
import java.io.IOException;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Properties;
import java.util.function.BiConsumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    public static double temperature = 0;
    public static double humidity = 0;
    private static Logger logger = Logger.getLogger(Main.class.getName());
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

    public static void main(String[] args) throws InterruptedException {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.hostnameVerifier((hostname, session) -> true);

        if (!loadConfig()) return;

        logger.info("Config file loaded");

//        InfluxDBClientOptions options = InfluxDBClientOptions.builder().url(config.getProperty("influxdb-url")).authenticateToken(config.getProperty("influxdb-token").toCharArray()).build();
//        client = InfluxDBClientFactory.create(options);

        client = InfluxDBClientFactory.create(config.getProperty("influxdb-url"),
                config.getProperty("influxdb-token").toCharArray());

        influxdbBucket = config.getProperty("influxdb-bucket");
        influxdbOrg = config.getProperty("influxdb-org");

        Mqtt mqttClient = new Mqtt("tcp://cloud.tbz.ch:1883", "dalama-server");
        try {
            mqttClient.start();
            mqttClient.subscribe("Dalama/#");
        } catch (MqttException e) {
            e.printStackTrace();
        }

//        mqttClient.addHandler((s, mqttMessage) -> System.out.printf("Received message from %s: %s%n", s, mqttMessage.toString()));

        mqttClient.addHandler(new BiConsumer<String, MqttMessage>() {
            @Override
            public void accept(String s, MqttMessage mqttMessage) {
                if (s.startsWith("Dalama/temperature/")) {
                    temperature = Double.parseDouble(mqttMessage.toString());
                }
                if (s.startsWith("Dalama/humidity/")) {
                    humidity = Double.parseDouble(mqttMessage.toString());
                }
            }
        });

        double lastTemperature = temperature;

        while (true) {
            // TODO: change 0.01 to a more realistic value
            if (Math.abs(lastTemperature - temperature) >= 0.01) {
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