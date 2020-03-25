package com.example.liam.mobdev_assignment;

/**
 * Created by Liam on 28/11/2017.
 */
//class to store sensor name and value when data is being sent/received as JSON
public class Sensor {

    String name,value;

    public Sensor(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
