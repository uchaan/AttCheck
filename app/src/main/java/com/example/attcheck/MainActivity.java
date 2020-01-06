package com.example.attcheck;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    String in_student_id;
    String in_password;
    String right_student_id;
    String right_password;
    String name;
    String SERVER ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //클릭하면 사용자 입력 정보 in 으로 받아옴
                SERVER = "http://192.249.19.252:1780/logins/";
                EditText id = findViewById(R.id.editText2);
                EditText pw = findViewById(R.id.editText3);
                in_student_id = id.getText().toString();
                in_password = pw.getText().toString();
                SERVER = SERVER + in_student_id;
                //서버에 id 요청
                HttpGetRequest request = new HttpGetRequest();
                request.execute();

            }
        });
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
                Log.d("t", "------------------------------" + SERVER);
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

        protected void onPostExecute(String result){
            super.onPostExecute(result);
//            TextView textView = (TextView)findViewById(R.id.textView);
//            textView.setText(result);
            try {
                //서버에 해당 id 찾아서 result 받아오고 파싱
                JSONArray jsonArray = new JSONArray(result);
                right_student_id = jsonArray.getJSONObject(0).getString("student_id");
                right_password = jsonArray.getJSONObject(0).getString("password");
                if(right_password.equals(in_password)){
                    Toast.makeText(getApplicationContext(), "로그인 성공", Toast.LENGTH_SHORT).show();
                    name = jsonArray.getJSONObject(0).getString("name");
                    Intent intent = new Intent(getApplicationContext(), SecondActivity.class);
                    intent.putExtra("name", name);
                    startActivity(intent);



                } else if ( !right_student_id.equals(in_student_id)) {
                    Toast.makeText(getApplicationContext(), "잘못된 id 입니", Toast.LENGTH_SHORT).show();
                }

                else Toast.makeText(getApplicationContext(), "pw를 확인하세요.", Toast.LENGTH_SHORT).show();

            } catch (JSONException e){
                //핸들해줘요
            }
        }

    }

}



/* 페이스북 연동 코드

import android.content.Intent;
import android.os.Bundle;

import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;


public class MainActivity extends AppCompatActivity {

    private LoginButton btn_facebook_login;
    private TextView txtName,txtEmail;

    private CallbackManager callbackManager;

    // ************* 사용자 정보 저장 변수 ************
    String user_email, user_first_name, user_last_name, user_id;
    // *******************************************


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        callbackManager = CallbackManager.Factory.create();
        btn_facebook_login.setReadPermissions(Arrays.asList("email","public_profile"));

        checkLoginStatus();

        btn_facebook_login.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult)
            {
                Intent intent = new Intent(getApplicationContext(), SecondActivity.class);
                intent.putExtra("name", user_last_name+user_first_name);
                startActivity(intent);
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });


    }

    public void init() {
        btn_facebook_login = findViewById(R.id.btn_facebook_login);
        txtName = findViewById(R.id.profile_name);
        txtEmail = findViewById(R.id.profile_email);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    AccessTokenTracker tokenTracker = new AccessTokenTracker() {
        @Override
        protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken)
        {
            if(currentAccessToken==null)
            {
                txtEmail.setText("");
                txtName.setText("");

                Toast.makeText(MainActivity.this,"User Logged out",Toast.LENGTH_LONG).show();
            }
            else
                loadUserProfile(currentAccessToken);
        }
    };

    private void loadUserProfile(AccessToken newAccessToken)
    {
        GraphRequest request = GraphRequest.newMeRequest(newAccessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response)
            {
                try {
                    user_first_name = object.getString("first_name");
                    user_last_name = object.getString("last_name");
                    user_email = object.getString("email");
                    user_id = object.getString("id");

                    txtEmail.setText(user_email);
                    txtName.setText(user_last_name +user_first_name);


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

        Bundle parameters = new Bundle();
        parameters.putString("fields","first_name,last_name,email,id");
        request.setParameters(parameters);
        request.executeAsync();

    }

    private void checkLoginStatus()
    {
        if(AccessToken.getCurrentAccessToken()!=null)
        {
            loadUserProfile(AccessToken.getCurrentAccessToken());
        }
    }

}

 */