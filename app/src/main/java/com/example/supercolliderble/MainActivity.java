package com.example.supercolliderble;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends BLEActivity {

    TextView esti_number;
    EditText edit_filename, edit_casename;
    private static String fileName, caseName;

    Button btnStart, btnStop;
    private final static int BTN_START = 1;
    private final static int BTN_STOP = 2;
    private int isSystemRun;
    private static int esti_count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnStart = (Button) findViewById(R.id.btn_start);
        btnStop = (Button) findViewById(R.id.btn_stop);

        esti_number = (TextView) findViewById(R.id.esti_number);

        edit_filename = (EditText) findViewById(R.id.edit_filename);
        edit_casename = (EditText) findViewById(R.id.edit_casename);


        btnStart.setOnClickListener(mClickListener);
        btnStop.setOnClickListener(mClickListener);
    }

    private Button.OnClickListener mClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (returnButtonType(v)) {
                case BTN_START: {

//                    // 공백체크
//                    if(edit_inputcase.toString().replace(" ", "").equals("")){
//                        Toast.makeText(MainActivity.this,"파일명을 채워주세요(공백x)",Toast.LENGTH_SHORT).show();
//                        onPause();
//                        break;
//                    }
                    scanLeDevice(true);
                    fileName = edit_filename.getText().toString();
                    caseName = edit_casename.getText().toString();

                    countEstimation();    //Start sensor estimation
                    printCount();

                    break;
                }

                case BTN_STOP: {
                    scanLeDevice(false);
                    onPause();  //Stop sensor estimation
//                    networkTask.onCancelled();
                    break;
                }


            }
        }
    };

    private int returnButtonType(View v) {
        if (v == btnStart) {
            isSystemRun = BTN_START;
            return isSystemRun;
        } else {
            isSystemRun = BTN_STOP;
            return isSystemRun;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e("LOG", "onPause()");

    }

    private void countEstimation() {
        final Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                NetworkTask networkTask = new NetworkTask();

                if (isSystemRun == BTN_STOP) {
                    timer.cancel();
                    esti_count = 0;
                } else if (esti_count == 1000) {
                    onPause();
                    timer.cancel();
                    esti_count = 0;
                } else {
                    networkTask.execute();
                    esti_count++;
                }
            }
        };

        // 1초에 1번씩 전송
        timer.scheduleAtFixedRate(timerTask, 1000, 1000);
    }

    private void printCount() {
        esti_number.setText(String.valueOf(esti_count));
    }

    public class NetworkTask extends AsyncTask<Void, Void, Integer> {
        int res;

        @Override
        protected Integer doInBackground(Void... params) {
            try {
                String aws_url = "http://192.168.43.90:5000/ble"; //아마존 서버 IP

                URL url = new URL(aws_url);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setRequestProperty("Cache-Control", "no-cache");
                conn.setRequestProperty("Accept", "application/json");
                conn.setRequestProperty("Content-Type", "application/json");    //MIME 타입 설정. 이 설정을 통해 서버에서 http 요청에서 온 데이터의 타입 식별 가능
                conn.connect();

                String array = "";
                for(int i=0 ; i<bleList.size() ; i++){
                    if(i == bleList.size()-1){
                        array += "{\"MAC\":"+bleList.get(i).getMac()+",\"rssi\":"+bleList.get(i).avgRssi()+"}";
                    }
                    else{
                        array += "{\"MAC\":"+bleList.get(i).getMac()+",\"rssi\":"+bleList.get(i).avgRssi()+"},";
                    }
                }

                String jsonData = "{\"BLE\":[";
                jsonData += array + "]}";

                Log.d("jsonData",jsonData);
//                JSONArray jsonArray = new JSONArray();
//
//                jsonData1 = new JsonData(fileName, caseName, b1_mac, b1_rssi, b1_vendor);
//                JSONObject jsonObject1 = JsonData.setJsonObject(jsonData1.jsonObject);
//                jsonArray.put(jsonObject1);
//
//                jsonData2 = new JsonData(fileName, caseName, b2_mac, b2_rssi, b2_vendor);
//                JSONObject jsonObject2 = JsonData.setJsonObject(jsonData2.jsonObject);
//                jsonArray.put(jsonObject2);
//
//                jsonData3 = new JsonData(fileName, caseName, b3_mac, b3_rssi, b3_vendor);
//                JSONObject jsonObject3 = JsonData.setJsonObject(jsonData3.jsonObject);
//                jsonArray.put(jsonObject3);
//
//                JSONObject jsonObject_total = new JSONObject();
//                try{
//                    jsonObject_total.put("BLE", jsonArray);
//
//                    Log.i("jsonArray","json");
//                    System.out.println(jsonObject_total);
//
//                } catch (JSONException e1){
//                    e1.printStackTrace();
//                }
//
//                DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
//                dos.writeBytes(jsonObject_total.toString());
//                dos.flush();
//                dos.close();

                DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
                dos.writeBytes(jsonData);
                dos.flush();
                dos.close();

                res = conn.getResponseCode();


            } catch (Exception e) {
                Log.i("signal_no", "exception");
                e.printStackTrace();
            }

            return res;
        }

        @Override
        protected void onPostExecute(Integer res) {
            super.onPostExecute(res);
        }
    }

}
