package com.lingvosphere.model;

import java.util.Date;

public class CalendarItem {
    private String date;
    private int type;
    public final static int TYPE_SELECTED = 1; //1
    public final static int TYPE_FOCUSED = 2; //10
    public final static int TYPE_IN_MONTH = 4; //100
    public final static int TYPE_NULL = 0;

    public CalendarItem(String date, int type) {
        this.date = date;
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }
}
