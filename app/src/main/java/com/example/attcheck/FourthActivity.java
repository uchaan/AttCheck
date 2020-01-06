package com.example.attcheck;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

        jsonParsing();

        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.recycler);
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(manager); // LayoutManager 등록
        recyclerView.setAdapter(new mAdapter(AttendaceList));  // Adapter 등록


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
            viewHolder.atdT.setText(myDataList.get(position).getAtd_check());

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
}