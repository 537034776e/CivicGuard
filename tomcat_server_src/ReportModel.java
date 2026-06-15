package com.example.civic;

/**
 * Modello ad oggetti del Server per serialize/deserialize dei dati JSON.
 */
public class ReportModel {
    private int id;
    private String title;
    private String description;
    private String category;
    private String severity;
    private String sender;
    private long timestamp;
    private double latitude;
    private double longitude;
    private float ambientLight;
    private int batteryLevel;

    // Costruttori
    public ReportModel() {}

    public ReportModel(int id, String title, String description, String category, 
                       String severity, String sender, long timestamp, 
                       double latitude, double longitude, float ambientLight, int batteryLevel) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.severity = severity;
        this.sender = sender;
        this.timestamp = timestamp;
        this.latitude = latitude;
        this.longitude = longitude;
        this.ambientLight = ambientLight;
        this.batteryLevel = batteryLevel;
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }

    public String getSender() { return sender; }
    public void setSender(String sender) { this.sender = sender; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public float getAmbientLight() { return ambientLight; }
    public void setAmbientLight(float ambientLight) { this.ambientLight = ambientLight; }

    public int getBatteryLevel() { return batteryLevel; }
    public void setBatteryLevel(int batteryLevel) { this.batteryLevel = batteryLevel; }
}
