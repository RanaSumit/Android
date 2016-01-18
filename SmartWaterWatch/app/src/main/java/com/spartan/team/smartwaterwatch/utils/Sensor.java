package com.spartan.team.smartwaterwatch.utils;

/**
 * Created by ranaf on 12/9/2015.
 */
public class Sensor {

    private String sensorName;
    private String sensorType;
    private String sensorStatus;


    public String getSensorName() {
        return sensorName;
    }

    public void setSensorName(String sensorName) {
        this.sensorName = sensorName;
    }

    public String getSensorType() {
        return sensorType;
    }

    public void setSensorType(String sensorType) {
        this.sensorType = sensorType;
    }

    public String getSensorStatus() {
        return sensorStatus;
    }

    public void setSensorStatus(String sensorStatus) {
        this.sensorStatus = sensorStatus;
    }
}
