package com.example.map.service;

import com.example.map.model.Pin;
import java.util.ArrayList;
import java.util.List;

public class PinService {
    private List<Pin> pins = new ArrayList<>();

    public List<Pin> getPins() {
        return pins;
    }

    public void addPin(Pin pin) {
        pins.add(pin);
    }
}