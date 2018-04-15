package com.example.pinor.inf8405runner.db;

public class DeviceInfo {

    private int _id;
    private int _batteryLevel;
    private int _txDepart;
    private int _rxDepart;

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public int get_batteryLevel() {
        return _batteryLevel;
    }

    public void set_batteryLevel(int _batteryLevel) {
        this._batteryLevel = _batteryLevel;
    }

    public int get_txDepart() {
        return _txDepart;
    }

    public void set_txDepart(int _txDepart) {
        this._txDepart = _txDepart;
    }

    public int get_rxDepart() {
        return _rxDepart;
    }

    public void set_rxDepart(int _rxDepart) {
        this._rxDepart = _rxDepart;
    }
}