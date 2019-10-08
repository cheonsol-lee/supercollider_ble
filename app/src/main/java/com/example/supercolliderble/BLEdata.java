package com.example.supercolliderble;

import java.util.ArrayList;

public class BLEdata {
    private String mac;
    private ArrayList<Integer> rssi;

    public BLEdata(String mac, int rssi) {
        this.rssi = new ArrayList<>();
        this.mac = mac;
        this.rssi.add(rssi);
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public ArrayList<Integer> getRssi() {
        return rssi;
    }

    public void setRssi(ArrayList<Integer> rssi) {
        this.rssi = rssi;
    }

    public void addRssi(int rssi){
        this.rssi.add(rssi);
    }

    public int avgRssi(){
        int sum = 0;
        for(Integer data : this.rssi){
            sum += data;
        }
        return sum / rssi.size();
    }
}
