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
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.io.Serializable;
import java.util.HashMap;

public class ThirdActivity extends AppCompatActivity {

    private TextView tv;
    private TextView st_id;
    private String data;
    private ArrayList<Lecture> LectureList= new ArrayList<>();

    public HashMap<String, String> LectureMap = new HashMap<>();
    public HashMap<String, String> TimeMap = new HashMap<>();

    RecyclerView recyclerView;
    private String name;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_third);

        initMap();
        init();

        Intent intent = getIntent();
        data = intent.getExtras().getString("json");

        jsonParsing();

        if (data != null) {
            tv.setText(name);
            st_id.setText(id);
        }

        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(manager); // LayoutManager 등록
        recyclerView.setAdapter(new MyAdapter(LectureList));  // Adapter 등록

    }

    public void init(){
        tv = (TextView) findViewById(R.id.studentName);
        st_id = findViewById(R.id.studentID);
    }

    // json 파싱
    private void jsonParsing() {
        try {
            // json 을 JSONArray 로 형변환
            JSONArray jsonArray = new JSONArray(data);
            name = jsonArray.getJSONObject(0).getString("name");
            id = jsonArray.getJSONObject(0).getString("student_id");

            // ClassList: 이미 lectureList 에 있는지 확인하기 위한 리스트
            ArrayList<String> ClassList = new ArrayList<>();


            for (int i = 0; i<jsonArray.length(); i++) {

                JSONObject lectureObject = jsonArray.getJSONObject(i);
                String lectureCode = lectureObject.getString("lecture");

                // ClassList 에 없으면, ClassList 에 추가 후 Lecture 클래스 만들어서 LectureList 에 추가.
                if (! ClassList.contains(lectureCode)) {

                    ClassList.add( lectureCode );

                    Lecture lecture = new Lecture();
                    lecture.setCode( lectureCode );
                    lecture.setClassroom(lectureObject.getString("classroom"));

                    LectureList.add(lecture);

                }

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


    public class MyAdapter extends RecyclerView.Adapter<ViewHolder> {

        private ArrayList<Lecture> myDataList = null;

        MyAdapter(ArrayList<Lecture> dataList)
        {
            myDataList = dataList;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            Context context = parent.getContext();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            //전개자(Inflater)를 통해 얻은 참조 객체를 통해 뷰홀더 객체 생성
            View view = inflater.inflate(R.layout.recyclerview_item, parent, false);
            ViewHolder viewHolder = new ViewHolder(view);

            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int position)
        {

            //ViewHolder가 관리하는 View에 position에 해당하는 데이터 바인딩
            String temp_lecture = myDataList.get(position).getCode();
            viewHolder.TopText.setText(temp_lecture + " : " + LectureMap.get(temp_lecture));
            viewHolder.BottomText.setText(myDataList.get(position).getClassroom() + "    " + TimeMap.get(temp_lecture));

        }

        @Override
        public int getItemCount()
        {
            //Adapter가 관리하는 전체 데이터 개수 반환
            return myDataList.size();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView TopText;
        TextView BottomText;

        ViewHolder(View itemView)
        {
            super(itemView);

            TopText = itemView.findViewById(R.id.topText);
            BottomText = itemView.findViewById(R.id.bottomText);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        Lecture selectedLecture = LectureList.get(pos);
                        Intent intent2 = new Intent(getApplicationContext(), FourthActivity.class);
                        intent2.putExtra("lecture", selectedLecture.getCode());
                        intent2.putExtra("json", data);
                        startActivity(intent2);
                    }
                }
            });
        }
    }

    public void initMap(){
        LectureMap.put("CS496", " Mad Camp ");
        TimeMap.put("CS496", "Everyday : 20:30 - 22:00 ");
        LectureMap.put("CS320", " Programming Language");
        TimeMap.put("CS320", " Mon/Wed : 14:30 - 16:00 ");
    }



}
