package com.example.supercolliderble;

public class Useful {

    static public String LOG_COMM_SERVER = "LOG_COMM_SERVER";

    static public String URL_QUERY = "http://13.209.19.155:5000/data/query/";
    static public String URL_SAVE = "http://13.209.19.155:5000/data/saving/";
    static public String URL_LEARN = "http://13.209.19.155:5000/data/learning/";

    static final public int PERMISSION_REQUEST_COARSE_LOCATION = 456;

    static public String printByteArray(byte[] bytes) {
        // print BLE bytes to array
        String re = "[";
        for (Byte obj : bytes) {
            re += String.format("%02X,", obj);
        }
        re += "]";
        return re;
    }

}
