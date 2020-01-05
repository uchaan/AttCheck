package com.example.attcheck;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class SecondActivity extends AppCompatActivity {

    String name;
    TextView tvData;
    Button btn;
    ArrayList<Lecture> LectureList=null;

    String SERVER = "http://192.249.19.252:1780/students/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        init();

        // MainActivity 로부터 사용자 이름 가져옴.
        Intent intent = getIntent();
        name = intent.getExtras().getString("name");

        // 서버 url 에 사용자 이름 추가
        SERVER = SERVER + name;

        // 버튼 클릭시 웹서버로 부터 JSONArray 가져옴.
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 데이터 요청 , 변수 json 에 저장.
                HttpGetRequest request = new HttpGetRequest();
                request.execute();
                jsonParsing(tvData.getText().toString());
            }
        });
    }

    // json 파싱
    private void jsonParsing(String json) {
        try {
            // json 을 JSONArray 로 형변환
            JSONArray jsonArray = new JSONArray(json);


            for (int i = 0; i<jsonArray.length(); i++) {

                JSONObject lectureObject = jsonArray.getJSONObject(i);

                Lecture lecture = new Lecture();
                lecture.setCode(lectureObject.getString("lecture"));
                lecture.setClassroom(lectureObject.getString("classroom"));

                LectureList.add(lecture);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            System.out.println("틀림!");
        }
    }

    // Lecture 클래스.
    public class Lecture {
        private String code;
        private String classroom;

        public String getCode() {
            return code;
        }

        public String getClassroom() {
            return classroom;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public void setClassroom(String classroom) {
            this.classroom = classroom;
        }

    }

    // 웹서버에서 사용자 수강과목 JSONArray 데이터 가져와주는 클래스
    public class HttpGetRequest extends AsyncTask<Void, Void, String> {

        static final String REQUEST_METHOD = "GET";
        static final int READ_TIMEOUT = 15000;
        static final int CONNECTION_TIMEOUT = 15000;

        @Override
        protected String doInBackground(Void... params){
            String op;
            String inputLine;

            try {
                // connect to the server
                URL myUrl = new URL(SERVER);
                HttpURLConnection connection =(HttpURLConnection) myUrl.openConnection();
                connection.setRequestMethod(REQUEST_METHOD);
                connection.setReadTimeout(READ_TIMEOUT);
                connection.setConnectTimeout(CONNECTION_TIMEOUT);
                connection.connect();

                // get the string from the input stream
                InputStreamReader streamReader = new InputStreamReader(connection.getInputStream());
                BufferedReader reader = new BufferedReader(streamReader);
                StringBuilder stringBuilder = new StringBuilder();
                while((inputLine = reader.readLine()) != null){
                    stringBuilder.append(inputLine);
                }
                reader.close();
                streamReader.close();
                op = stringBuilder.toString();

            } catch(IOException e) {
                e.printStackTrace();
                op = "error";
            }

            return op;
        }

        // 가져온 데이터 tvData 텍스뷰에 뿌
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            tvData.setText(result);
        }
    }

    public void init(){
        tvData = (TextView) findViewById(R.id.tvdata);
        btn = (Button)findViewById(R.id.httpTest);
    }

}
