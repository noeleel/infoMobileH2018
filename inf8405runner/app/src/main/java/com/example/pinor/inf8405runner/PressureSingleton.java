package com.example.pinor.inf8405runner;

public class PressureSingleton {
    private static PressureSingleton instance = null;

    private PressureSingleton() {};


    public static PressureSingleton getInstance() {
        if (instance == null) {
            instance = new PressureSingleton();
        }
        return(instance);
    }

    public float getPressure() {
        return pressure;
    }

    public void setPressure(float pressure) {
        this.pressure = pressure;
    }

    private float pressure;
}
