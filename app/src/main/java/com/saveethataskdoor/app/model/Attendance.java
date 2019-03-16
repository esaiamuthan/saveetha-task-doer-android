package com.saveethataskdoor.app.model;

import java.util.ArrayList;

public class Attendance {

    public String uId;

    public String date;

    public boolean isClosed;

    public ArrayList<Integer> absent;

    public ArrayList<Integer> present;

    public long createdAt;

    public long period;

    public long year;

    public String department;

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isClosed() {
        return isClosed;
    }

    public void setClosed(boolean closed) {
        isClosed = closed;
    }

    public ArrayList<Integer> getAbsent() {
        return absent;
    }

    public void setAbsent(ArrayList<Integer> absent) {
        this.absent = absent;
    }

    public ArrayList<Integer> getPresent() {
        return present;
    }

    public void setPresent(ArrayList<Integer> present) {
        this.present = present;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getPeriod() {
        return period;
    }

    public void setPeriod(long period) {
        this.period = period;
    }

    public long getYear() {
        return year;
    }

    public void setYear(long year) {
        this.year = year;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }
}
