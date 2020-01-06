package com.example.attcheck;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FourthActivity extends AppCompatActivity {

    private ArrayList<Attendance> AttendaceList = new ArrayList<>();
    private String data;
    private String lecture;
    private TextView lectureT, dataT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fourth);

        Intent intent = getIntent();
        data = intent.getExtras().getString("json");
        lecture = intent.getExtras().getString("lecture");

        init();

        dataT.setText(data);
        lectureT.setText(lecture);


    }

    // Attendance 클래스.
    public class Attendance {
        private String code;
        private String day;
        private String atd_check;

        public String getCode() { return code; }
        public String getDay()  { return day;  }
        public String getAtd_check() { return atd_check; }

        public void setCode(String code) { this.code = code;}
        public void setDay(String day) { this.day = day;}
        public void setAtd_check(String atd_check) { this.atd_check = atd_check;}

    }

    // json 파싱
    private void jsonParsing() {
        try {
            // json 을 JSONArray 로 형변환
            JSONArray jsonArray = new JSONArray(data);

            for (int i = 0; i<jsonArray.length(); i++) {

                JSONObject attendanceObject = jsonArray.getJSONObject(i);

                if ( attendanceObject.getString("lecture").equals(lecture)) {

                    // 모든 수업의 출첵 기록 (Class Attendance) 이 담긴 AttendanceList 에 추가
                    Attendance attendace = new Attendance();
                    attendace.setCode( lecture );
                    attendace.setDay( attendanceObject.getString("day"));
                    attendace.setAtd_check( attendanceObject.getString("atd_check"));

                    AttendaceList.add(attendace);
                }



            }

        } catch (JSONException e) {
            e.printStackTrace();
            System.out.println("틀림!");
        }
    }

    public void init(){
        lectureT = findViewById(R.id.lecture);
        dataT = findViewById(R.id.data);
    }
}
