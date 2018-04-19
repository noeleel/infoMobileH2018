package com.example.pinor.inf8405runner.db;

import android.location.Location;

import java.util.ArrayList;
import java.util.Date;

public class Run {
    private int _id;
    private Date _date;
    private ArrayList<Location> _points;

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public Date get_date() {
        return _date;
    }

    public void set_date(Date _date) {this._date = _date;}

    public ArrayList<Location> get_Points() {
        return _points;
    }

    public void set_Points(ArrayList<Location> points) {this._points = points; }
}