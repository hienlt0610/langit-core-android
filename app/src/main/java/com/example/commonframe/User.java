package com.example.commonframe;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import icepick.State;

/**
 * Created by Tyrael on 6/9/16.
 */
public class User implements Serializable {

    @State
    public String name;

    @State
    public int age;


    @State
    ArrayList<Device> devices;

    public User() {
        devices = new ArrayList<>();
    }


    @Override
    public String toString() {
        return "User: " + name + " " + age + " years old" + " has " + devices.size() + " devices including " + devices.toString();
    }
}
