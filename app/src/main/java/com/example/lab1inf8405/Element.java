package com.example.lab1inf8405;


/**
 * Created by Melchor on 2018-02-04.
 */

public class Element {
    private String title;
    private String subtitle;

    public Element(String t, String s) {
        this.title = t;
        this.subtitle = s;
    }

    public String getTitle() {
        return this.title;
    }

    public String getSubtitle() {
        return this.subtitle;
    }
}
