package com.example.commonframe;

import java.io.Serializable;

import icepick.State;

/**
 * Created by Tyrael on 6/9/16.
 */
public class Device implements Serializable {

    @State
    public int number;

    @Override
    public String toString() {
        return "device number " + number;
    }
}
