package com.saveethataskdoor.app.model;

import java.util.ArrayList;

/**
 * Created on 18/2/19.
 */
public class User {
    public String name;
    public String type;
    public String userId;
    public String email;
    public String department;
    public ArrayList<Integer> yearList;
    public boolean classAdviser = false;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public ArrayList<Integer> getYearList() {
        return yearList;
    }

    public void setYearList(ArrayList<Integer> yearList) {
        this.yearList = yearList;
    }

    public boolean isClassAdviser() {
        return classAdviser;
    }

    public void setClassAdviser(boolean classAdviser) {
        this.classAdviser = classAdviser;
    }
}
