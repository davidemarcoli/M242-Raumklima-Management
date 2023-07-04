package dev.dalama;

import com.google.gson.Gson;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import okhttp3.OkHttpClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.FileReader;
import java.io.IOException;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
//    public static double temperature = 0;
//    public static double humidity = 0;
    private static Logger logger = Logger.getLogger(Main.class.getName());
    private static Properties config;
    private static InfluxDBClient client;
    private static String influxdbBucket;
    private static String influxdbOrg;
    private static Integer numOfClients;

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

        numOfClients = config.getProperty("num-of-clients") != null ? Integer.parseInt(config.getProperty("num-of-clients")) : 0;

        Mqtt mqttClient = new Mqtt("tcp://cloud.tbz.ch:1883", "dalama-server");
        try {
            mqttClient.start();
            mqttClient.subscribe("Dalama/#");
        } catch (MqttException e) {
            e.printStackTrace();
        }

//        mqttClient.addHandler((s, mqttMessage) -> System.out.printf("Received message from %s: %s%n", s, mqttMessage.toString()));

        Map<String, SensorData> responses = new ConcurrentHashMap<>();

        mqttClient.addHandler(new BiConsumer<String, MqttMessage>() {
            @Override
            public void accept(String s, MqttMessage mqttMessage) {
                System.out.println(s);
                System.out.println(mqttMessage.toString());
//                double temperature = 0.0;
//                double humidity = 0.0;
                Gson gson = new Gson();
                SensorData sensorData = gson.fromJson(mqttMessage.toString(), SensorData.class);

//                if (s.startsWith("Dalama/") && s.endsWith("/temperature")) {
//                    temperature = Double.parseDouble(mqttMessage.toString());
//                }
//                if (s.startsWith("Dalama/") && s.endsWith("/humidity")) {
//                    humidity = Double.parseDouble(mqttMessage.toString());
//                }
//                SensorData data = new SensorData(temperature, humidity);
                responses.put(s.split("/")[1].split("/")[0], sensorData);
            }
        });

        double lastTemperature = 0;
        double lastHumidity = 0;
//        long lastRead = System.currentTimeMillis();

        while (true) {
            Double averageTemperature = responses.values().stream().map(SensorData::temperature).mapToDouble(Number::doubleValue).average().orElse(0);
            Double averageHumidity = responses.values().stream().map(SensorData::humidity).mapToDouble(Number::doubleValue).average().orElse(0);

//            System.out.println(responses.values());
//           System.out.println(responses.values().size() + " == " + numOfClients);
////            System.out.println(numOfClients);
//           System.out.println(System.currentTimeMillis() - 30000 > lastRead);
//           System.out.println("----------------");

            // TODO: change 0.01 to a more realistic value
            if (responses.values().size() > 0 && (/*(responses.values().size() == numOfClients || System.currentTimeMillis() - 30000 > lastRead) &&*/
                (Math.abs(lastTemperature - averageTemperature) >= 0.1 || Math.abs(lastHumidity - averageHumidity) >= 0.1))) {
                System.out.printf("Temperature changed. Current: %.2f%n", averageTemperature);
                System.out.printf("Humidity changed. Current: %.2f%n", averageHumidity);
                Point point = Point
                        .measurement("room-environment")
//                        .addTag("host", "host1")
                        .addField("temperature", averageTemperature)
                        .addField("humidity", averageHumidity)
                        .time(Instant.now(), WritePrecision.NS);

                WriteApiBlocking writeApi = client.getWriteApiBlocking();
                writeApi.writePoint(influxdbBucket, influxdbOrg, point);

                lastTemperature = averageTemperature;
                lastHumidity = averageHumidity;
//                lastRead = System.currentTimeMillis();
//                responses.clear();
            }
//            Thread.sleep(1000);
        }
    }
}