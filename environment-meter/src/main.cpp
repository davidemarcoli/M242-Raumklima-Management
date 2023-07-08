#include <Arduino.h>
#include "networking.h"
#include "view.h"
#include "M5Core2.h"
#include <iostream>
#include <string>
#include "SHT85.h"
#include <bits/stdc++.h>
#include <ArduinoJson.h>

using namespace std;

#define SHT85_ADDRESS 0x44

void mqtt_callback(char *topic, byte *payload, unsigned int length);

unsigned long next_lv_task = 0;
unsigned long next_sensor_read = 0;

lv_obj_t *temperature_label;
lv_obj_t *humidity_label;

void mqtt_callback(char *topic, byte *payload, unsigned int length)
{
  /*// Parse Payload into String
  char *buf = (char *)malloc((sizeof(char) * (length + 1)));
  memcpy(buf, payload, length);
  buf[length] = '\0';
  String payloadS = String(buf);
  payloadS.trim();
  Serial.println(payloadS);
  StaticJsonDocument<200> doc;
  deserializeJson(doc, payloadS);
  JsonObject obj = doc.as<JsonObject>();
  float test = obj["temperature"];
  char *test2 = obj["temperature"];
  Serial.println(test2);
  lv_label_set_text(temperature_label, test2);
  // lv_label_set_text(temperature_label, "temperature");*/

  // Parse Payload into String
  char *buf = (char *)malloc((sizeof(char) * (length + 1)));
  memcpy(buf, payload, length);
  buf[length] = '\0';
  Serial.println(buf);
  StaticJsonDocument<200> doc;
  deserializeJson(doc, buf);
  JsonObject obj = doc.as<JsonObject>();

  serializeJsonPretty(obj, Serial);
  Serial.println();

  float temperature_value = obj["temperature"];
  Serial.println(temperature_value);

  float humidity_value = obj["humidity"];
  Serial.println(humidity_value);

  /*const char *test2 = obj["temperature"]; // Note that I changed this to a const char*

  if (test2 == nullptr)
  {
    Serial.println(F("Didn't find 'temperature' in the JSON"));
  }
  else
  {
    Serial.println(test2);
  }*/

  // Serial.println(test2);

  // Convert float to String
  String temperature_string = String(temperature_value);
  String humidity_string = String(humidity_value);

  // Get char* from the String
  const char *temperature_char = temperature_string.c_str();
  const char *humidity_char = humidity_string.c_str();

  lv_label_set_text(temperature_label, temperature_char);
  lv_label_set_text(humidity_label, humidity_char);
}

SHT85 sht;

void setup()
{
  init_m5();

  Wire.begin();
  sht.begin(SHT85_ADDRESS);
  Wire.setClock(100000);

  uint16_t stat = sht.readStatus();
  Serial.print(stat, HEX);
  Serial.println();

  uint32_t ser = sht.GetSerialNumber();
  Serial.print(ser, HEX);
  Serial.println();
  delay(1000);

  init_display();
  Serial.begin(115200);
  // Uncomment the following lines to enable WiFi and MQTT
  lv_obj_t *wifiConnectingBox = show_message_box_no_buttons("Connecting to WiFi...");
  lv_task_handler();
  delay(5);
  setup_wifi();
  mqtt_init(mqtt_callback);
  close_message_box(wifiConnectingBox);

  temperature_label = add_label("Temperature", 80, 50);
  humidity_label = add_label("Humidity", 80, 100);
  lv_obj_set_state(temperature_label, LV_STATE_DEFAULT);
}

void loop()
{
  if (next_lv_task < millis())
  {
    lv_task_handler();
    next_lv_task = millis() + 5;
  }
  if (next_sensor_read < millis())
  {
    uint32_t start = micros();
    sht.read(); // default = true/fast       slow = false
    uint32_t stop = micros();

    // std::string temperatureTopic = "Dalama/" + std::string(m5stackId) + "/temperature";
    // mqtt_publish(temperatureTopic.c_str(), std::to_string(sht.getTemperature()).c_str());
    // std::string humidityTopic = "Dalama/" + std::string(m5stackId) + "/humidity";
    // mqtt_publish(humidityTopic.c_str(), std::to_string(sht.getHumidity()).c_str());

    // Create a StaticJsonDocument.
    // The number (200) is a size estimate, increase if needed.
    StaticJsonDocument<200> doc;

    // Set the values.
    doc["temperature"] = sht.getTemperature();
    doc["humidity"] = sht.getHumidity();

    // Convert JSON object into a string.
    String payload;
    serializeJson(doc, payload);

    // Create topic string.
    std::string topic = "Dalama/" + std::string(m5stackId);

    // Publish the JSON string.
    mqtt_publish(topic.c_str(), payload.c_str());

    next_sensor_read = millis() + 5000;
  }

  mqtt_loop();
}
