package com.example.pinor.inf8405runner.db;

import java.util.Comparator;

/**
 * Created by Pinor on 2018-04-05.
 */

public class Result {
    private int _id;
    private long _time;
    private int _distance;

    public Result() {
        
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public long get_time() {
        return _time;
    }

    public void set_time(long _time) {
        this._time = _time;
    }

    public int get_distance() {
        return _distance;
    }

    public void set_distance(int _distance) {
        this._distance = _distance;
    }

    public static Comparator<Result> ResultComparator = new Comparator<Result>() {

        public int compare(Result r1, Result r2) {
            long resultTime1 = r1.get_time();
            long resultTime2 = r2.get_time();

            return (int) (resultTime1 - resultTime2);
        }
    };
}

