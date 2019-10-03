package com.example.supercolliderble;

import android.app.Activity;
import android.content.ContentValues;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;


/** 2019.10.03 17:47
 *
 * 수정사항 : 비콘 3개 세팅.
 *
 */


public class MainActivity extends BLEActivity {

    private static EditText edit_main_cell;
    private EditText edit_main_set;
    //    private EditText edit_main_input; // 입력셀의 갯수
//    private EditText edit_main_output; // 출력셀의 갯수
    private Button btn_main_query;
    private Button btn_main_save;
    private Button btn_main_learn;
    private Button btn_main_reset;

    private Button btn_model_a;
    private Button btn_model_b;

    private static TextView textv_main_message;
    private static LinearLayout message_LinearLayout;
    private static Activity mainActivityContext;
    private static ScrollView scrollView;
    private static TextView tv_main_accuracy;  // 정확도 표기


    private boolean toggle = false; // false:off, true:on
    private String cellNumber = null;
    private static int urlSendCount = 0; // 쿼리전송시 전송한 URL 갯수
    private static int correctCount = 0; // 맞은 횟수 카운트
    private static String accuracyPercent = null; // 정확도 확률(%)
    private static int responseCell = 0; // 응답으로 온 셀번호

    //버튼을 상수화
    private final static int BTN_RESET = 1;
    private final static int BTN_QUERY = 2;
    private final static int BTN_SAVE  = 3;
    private final static int BTN_LEARN = 4;
    private final static int BTN_MODEL_A = 5;
    private final static int BTN_MODEL_B = 6;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initScreen();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void initScreen() {
//        edit_main_input = findViewById(R.id.edit_main_input);
//        edit_main_output = findViewById(R.id.edit_main_output);
        edit_main_cell = findViewById(R.id.edit_main_cell);
        edit_main_set = findViewById(R.id.edit_main_set);

        btn_main_reset = findViewById(R.id.btn_main_reset);
        btn_main_query = findViewById(R.id.btn_main_query);
        btn_main_save = findViewById(R.id.btn_main_save);
        btn_main_learn = findViewById(R.id.btn_main_learn);

        btn_model_a = findViewById(R.id.btn_model_a);
        btn_model_b = findViewById(R.id.btn_model_b);

        message_LinearLayout = findViewById(R.id.message_LinearLayout);
        scrollView = findViewById(R.id.ScrollView);
        tv_main_accuracy = findViewById(R.id.tv_main_accuracy);

        // clickListener
        btn_main_reset.setOnClickListener(clickListener);
        btn_main_query.setOnClickListener(clickListener);
        btn_main_save.setOnClickListener(clickListener);
        btn_main_learn.setOnClickListener(clickListener);
        btn_model_a.setOnClickListener(clickListener);
        btn_model_b.setOnClickListener(clickListener);

        defaultButtonColor();

        mainActivityContext = this;
    }

    // 버튼타입에 따라 정수형으로 리턴
    private int returnButtonType(View v){
        if     (v == btn_main_reset) return BTN_RESET;
        else if(v == btn_main_query) return BTN_QUERY;
        else if(v == btn_main_save ) return BTN_SAVE;
        else if(v == btn_main_learn) return BTN_LEARN;
        else if(v == btn_model_a)    return BTN_MODEL_A;
        else                         return BTN_MODEL_B;
    }

    // button color default
    private void defaultButtonColor(){
        btn_main_reset.setBackgroundColor(Color.GRAY);
        btn_main_query.setBackgroundColor(Color.GRAY);
        btn_main_save.setBackgroundColor(Color.GRAY);
        btn_main_learn.setBackgroundColor(Color.GRAY);
        btn_model_a.setBackgroundColor(Color.GRAY);
        btn_model_b.setBackgroundColor(Color.GRAY);
    }

    // button color setting
    private void setButtonColor(View view){
        defaultButtonColor();
        if(getModelType() == 5){
            btn_model_a.setBackgroundColor(Color.RED);
        }

        if(getModelType() == 6){
            btn_model_b.setBackgroundColor(Color.RED);
        }

        switch(returnButtonType(view)){
            case BTN_RESET:{
                btn_main_reset.setBackgroundColor(Color.RED);
                break;
            }

            case BTN_QUERY:{
                btn_main_query.setBackgroundColor(Color.RED);
                break;
            }

            case BTN_SAVE:{
                btn_main_save.setBackgroundColor(Color.RED);
                break;
            }

            case BTN_LEARN:{
                btn_main_learn.setBackgroundColor(Color.RED);
                break;
            }
        }
    }



    private View.OnClickListener clickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            switch (returnButtonType(v)){

                case BTN_RESET:{
                    setButtonColor(v);

                    btn_main_reset.setBackgroundColor(Color.RED);
                    setButtonType(BTN_RESET);
                    urlSendCount = 0; // url 전송갯수 초기화
                    correctCount = 0; // 정확도 갯수 초기화
                    tv_main_accuracy.setText("0%");

                    if ((message_LinearLayout).getChildCount() > 0)
                        (message_LinearLayout).removeAllViews();
                    break;
                }

                case BTN_QUERY:{
                    setButtonColor(v);

                    if (toggle) {
                        Toast.makeText(MainActivity.this, "Query : Off", Toast.LENGTH_SHORT).show();
                        toggle = false;
                    } else {
                        Toast.makeText(MainActivity.this, "Query : On", Toast.LENGTH_SHORT).show();
                        toggle = true;
                        setButtonType(BTN_QUERY);
                        Log.i("BTN_QUERY", String.valueOf(getButtonType()));
                    }
                    scanLeDevice(toggle);
                    break;
                }

                case BTN_SAVE :{



                    setButtonColor(v);

                    if (toggle) {
                        Toast.makeText(MainActivity.this, "Save : Off", Toast.LENGTH_SHORT).show();
                        toggle = false;
                    } else {
                        Toast.makeText(MainActivity.this, "Save : On", Toast.LENGTH_SHORT).show();
                        toggle = true;
                        cellNumber = edit_main_cell.getText().toString();
                        int setNumber = Integer.parseInt(edit_main_set.getText().toString());

                        setButtonType(BTN_SAVE);
                        Log.i("BTN_SAVE", String.valueOf(getButtonType()));
                        setCellandSetNumber(cellNumber, setNumber);
                    }

                    if (Integer.parseInt(cellNumber) == 0) {
                        Log.i("cellNumber", "cellNumber:0");
                        Toast.makeText(MainActivity.this, "0을 제외한 숫자를 입력해주세요!", Toast.LENGTH_SHORT).show();
                    } else {
                        // cell번호가 0이 아닐때만 스캔 진행
                        scanLeDevice(toggle);
                    }

                    tv_main_accuracy.setText(save_number);

                    if(complete_number == 1)
                        tv_main_accuracy.setText("Complete!!!");

                    break;
                }

                case BTN_LEARN:{
                    setButtonColor(v);

                    setButtonType(BTN_LEARN);
                    Log.i("BTN_LEARN", String.valueOf(getButtonType()));
                    Toast.makeText(MainActivity.this, "Learning Start", Toast.LENGTH_SHORT).show();

                    int modelNumber = getModelType();

                    int setNumberForLearning = Integer.parseInt(edit_main_set.getText().toString());
                    String learnUrl = Useful.URL_LEARN + modelNumber + "/" + setNumberForLearning + "/";
                    new CNN4IPSNetworkTask(learnUrl, null).execute();
                    break;
                }

                case BTN_MODEL_A:{
                    setModelType(BTN_MODEL_A);
                    setButtonColor(v);
                    Toast.makeText(MainActivity.this,"Model A selected!",Toast.LENGTH_SHORT).show();

                    break;
                }

                case BTN_MODEL_B:{
                    setModelType(BTN_MODEL_B);
                    setButtonColor(v);
                    Toast.makeText(MainActivity.this,"Model B selected!",Toast.LENGTH_SHORT).show();

                    break;
                }
            }

        }
    };

    // 자동스크롤 기능
    public static void AutoScrollBottom() {
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

    // 백그라운드에서 스레드로 비콘신호 수신
    public static class CNN4IPSNetworkTask extends AsyncTask<Void, Void, String> {

        private String url;
        private ContentValues values;

        public CNN4IPSNetworkTask(String url, ContentValues values) {
            this.url = url;
            this.values = values;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... voids) {
            String result;
            RequestHttpURLConnection requestHttpURLConnection = new RequestHttpURLConnection();
            result = requestHttpURLConnection.request(url, values);
            return parser(result);
        }

        @Override
        protected void onPostExecute(String aString) {
            super.onPostExecute(aString);

            //save버튼을 눌렀을때만 카운트횟수와 'Success:save' 문구 출력
            if (getSetNumberForLearning() != 0 && getButtonType() == BTN_SAVE) {
                int num = getSetNumberForLearning() - getSetNumber();
                Log.i("setNumberLearning", "setNumberLearning:" + getSetNumberForLearning());
                Log.i("setNumber", "setNumber:" + getSetNumber());

                textv_main_message = new TextView(mainActivityContext);
                textv_main_message.setText(String.format("%05d:%s\n", num, aString));
                message_LinearLayout.addView(textv_main_message);
                AutoScrollBottom();
            } else {
                Log.i("setNumberLearning", "setNumberLearning:" + getSetNumberForLearning());
                if (getButtonType() == BTN_QUERY) {
                    //query버튼 누를시 정확도 표기를 목적으로함

                    urlSendCount++;

                    responseCell = Integer.parseInt(aString);
                    int cellNumber = Integer.parseInt(edit_main_cell.getText().toString());

                    if (responseCell == cellNumber) {
                        correctCount++;
                    }

                    double value = (double) correctCount / (double) urlSendCount;

                    accuracyPercent = String.valueOf(Double.parseDouble(String.format("%.2f", value)) * 100);
                    //정확도 표기
                    Log.i(Useful.LOG_COMM_SERVER, "accuracy:" + accuracyPercent + "%");
                    Log.i(Useful.LOG_COMM_SERVER, "urlSendCount:" + urlSendCount);
                    Log.i(Useful.LOG_COMM_SERVER, "correctCount:" + correctCount);
                    Log.i(Useful.LOG_COMM_SERVER, "responseCell:" + responseCell);
                    Log.i(Useful.LOG_COMM_SERVER, "cellNumber:" + cellNumber);
                    tv_main_accuracy.setText(accuracyPercent + "%");
                }

                textv_main_message = new TextView(mainActivityContext);
                textv_main_message.setText(aString + "\n");
                message_LinearLayout.addView(textv_main_message);
                AutoScrollBottom();
            }
        }

        private String parser(String aString) {
            String parse = aString;
            Log.d("TAG", "Web: " + parse);

            return parse;
        }
    }
}
