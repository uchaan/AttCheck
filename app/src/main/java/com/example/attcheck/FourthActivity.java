package com.example.attcheck;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class FourthActivity extends AppCompatActivity {

    private ArrayList<Attendance> AttendanceList = new ArrayList<>();
    private String data;
    private String lecture;
    private TextView lectureT;

    public HashMap<String, String> LectureMap = new HashMap<>();
    public HashMap<String, String> TimeMap = new HashMap<>();
    public HashMap<String, String> ProfMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_fourth);
        getSupportActionBar().setIcon(R.drawable.tt2);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        final SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.swipe);
        initMap();

        Intent intent = getIntent();
        data = intent.getExtras().getString("json");
        lecture = intent.getExtras().getString("lecture");

        init();

        try {
            JSONArray jsonArray = new JSONArray(data);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        lectureT.setText(lecture + " : " + LectureMap.get(lecture));
        lectureT.setTypeface(null, Typeface.BOLD);

        TextView textView6 = findViewById(R.id.textView6);
        textView6.setText(TimeMap.get(lecture));
        TextView textView7 = findViewById(R.id.textView7);
        textView7.setText(ProfMap.get(lecture));

        jsonParsing();

        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.recycler);
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(manager); // LayoutManager 등록
        recyclerView.setAdapter(new mAdapter(AttendanceList));  // Adapter 등록

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
            }
        });


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

                    AttendanceList.add(attendace);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
            System.out.println("틀림!");
        }
    }

    public void init(){
        lectureT = findViewById(R.id.lecture);
    }

    public class mAdapter extends RecyclerView.Adapter<mViewHolder> {

        private ArrayList< Attendance > myDataList = null;

        mAdapter(ArrayList<Attendance> dataList)
        {
            myDataList = dataList;
        }

        @Override
        public mViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            Context context = parent.getContext();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            //전개자(Inflater)를 통해 얻은 참조 객체를 통해 뷰홀더 객체 생성
            View view = inflater.inflate(R.layout.recyclerview_item2, parent, false);
            mViewHolder viewHolder = new mViewHolder(view);

            return viewHolder;
        }

        @Override
        public void onBindViewHolder(mViewHolder viewHolder, int position)
        {
            //ViewHolder가 관리하는 View에 position에 해당하는 데이터 바인딩
            viewHolder.dayT.setText(myDataList.get(position).getDay());
            if(myDataList.get(position).getAtd_check().equals("N")){
                viewHolder.atdT.setText("결석");
            } else if(myDataList.get(position).getAtd_check().equals("Y")){
                viewHolder.atdT.setText("출석");
            } else if(myDataList.get(position).getAtd_check().equals("L")){
                viewHolder.atdT.setText("지각");
            }else {
                viewHolder.atdT.setText("   -");
            }

        }

        @Override
        public int getItemCount()
        {
            //Adapter가 관리하는 전체 데이터 개수 반환
            return myDataList.size();
        }
    }

    public class mViewHolder extends RecyclerView.ViewHolder{
        TextView dayT, atdT;

        mViewHolder(View itemView)
        {
            super(itemView);
            dayT = itemView.findViewById(R.id.day);
            atdT = itemView.findViewById(R.id.atd_check);
        }
    }
    public void initMap(){
        LectureMap.put("CS496", "Mad Camp");
        TimeMap.put("CS496", "Everyday | 20:30 ~ 22:00 ");
        LectureMap.put("CS320", "Programming Language");
        TimeMap.put("CS320", " Mon/Wed | 14:30 ~ 16:00 ");
        ProfMap.put("CS496", "장병규 교수, 류석영 교수");
        ProfMap.put("CS320", "류석영 교수");
    }
}
