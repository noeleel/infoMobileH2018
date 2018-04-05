package com.example.pinor.inf8405runner.db;

/**
 * Created by Pinor on 2018-04-05.
 */

public class Result {
    private int _id;
    private int _time;
    private int _distance;

    public Result() {
        
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public int get_time() {
        return _time;
    }

    public void set_time(int _time) {
        this._time = _time;
    }

    public int get_distance() {
        return _distance;
    }

    public void set_distance(int _distance) {
        this._distance = _distance;
    }
}
