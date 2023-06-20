#include <Arduino.h>
#include "networking.h"
#include "view.h"
#include "M5Core2.h"
#include "iostream"

void mqtt_callback(char *topic, byte *payload, unsigned int length);

unsigned long next_lv_task = 0;

lv_obj_t *temperature_label;

void mqtt_callback(char *topic, byte *payload, unsigned int length)
{
  // Parse Payload into String
  char *buf = (char *)malloc((sizeof(char) * (length + 1)));
  memcpy(buf, payload, length);
  buf[length] = '\0';
  String payloadS = String(buf);
  payloadS.trim();
  Serial.println(payloadS);
}

void setup()
{
  init_m5();
  init_display();
  Serial.begin(115200);
  // Uncomment the following lines to enable WiFi and MQTT
  lv_obj_t *wifiConnectingBox = show_message_box_no_buttons("Connecting to WiFi...");
  lv_task_handler();
  delay(5);
  setup_wifi();
  mqtt_init(mqtt_callback);
  close_message_box(wifiConnectingBox);

  temperature_label = add_label("Test", 80, 50);
  lv_obj_set_state(temperature_label, LV_STATE_DEFAULT);
}

void loop()
{
  if (next_lv_task < millis())
  {
    lv_task_handler();
    next_lv_task = millis() + 5;
  }

  mqtt_loop();
}
