package com.example.supercolliderble;

public class SignalDTO {
    private int modelName;
    private int signal1;
    private int signal2;
    private int signal3;
//    private int signal4;

    public SignalDTO() {
        this.modelName = 0;
        this.signal1 = 0;
        this.signal2 = 0;
        this.signal3 = 0;
//        this.signal4 = 0;
    }

    public int getModelName() {
        return modelName;
    }

    public void setModelName(int modelName) {
        this.modelName = modelName;
    }

//    public int getSignal4() {
//        return signal4;
//    }
//
//    public void setSignal4(int signal4) {
//        this.signal4 = signal4;
//    }

    public int getSignal1() {
        return signal1;
    }

    public void setSignal1(int signal1) {
        this.signal1 = signal1;
    }

    public int getSignal2() {
        return signal2;
    }

    public void setSignal2(int signal2) {
        this.signal2 = signal2;
    }

    public int getSignal3() {
        return signal3;
    }

    public void setSignal3(int signal3) {
        this.signal3 = signal3;
    }

    public boolean isFull() {
        return (this.signal1 != 0 && this.signal2 != 0 && this.signal3 != 0);
    }

    public void empty(){
        this.modelName = 0;
        this.signal1 = 0;
        this.signal2 = 0;
        this.signal3 = 0;
//        this.signal4 = 0;
    }
}
