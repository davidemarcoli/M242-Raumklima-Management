package dev.dalama;

public record SensorData(Double temperature, Double humidity) {
    public SensorData() {
        this(0.0, 0.0);
    }
}
