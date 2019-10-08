package com.example.supercolliderble;

public class Utility {
//    static public final String CCLAB_URL = "http://cclab.cbnu.ac.kr:8000/";
    static public final String CCLAB_URL = "http://192.168.0.67:5000/ble"; //아마존 서버 IP

    // 원래 값이 456이었는데 무슨 의미가 있는 값이지?? 어떤 예제에는 1로 되어있었음
    static final public int PERMISSION_REQUEST_COARSE_LOCATION = 1;

    static public String printByteArray(byte[] bytes) {
        // print BLE bytes to array
        String res = "[";
        for (Byte obj : bytes) {
            res += String.format("%02X", obj);
        }
        res += "]";
        return res;
    }
}
