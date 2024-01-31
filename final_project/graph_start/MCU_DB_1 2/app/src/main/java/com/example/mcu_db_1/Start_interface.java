package com.example.mcu_db_1;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class Start_interface extends AppCompatActivity{
    //호스트 주소 입력란
    EditText editText;
    //페이지를 넘기는 버튼
    Button enterbtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_interface);

        //액션바 숨기기
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        //id값 가져오기
        enterbtn = findViewById(R.id.host_botton);
        editText = (EditText) findViewById(R.id.textInputEditText);

        //버튼 클릭시
        enterbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //intent 객체 생성(현재 페이지에서 다음 페이지(mainactivity 클래스)로 이동)
                Intent intent = new Intent(Start_interface.this, MainActivity.class);

                //입력한 호스트 주소를 다음 페이지로 전달
                intent.putExtra("host_", editText.getText().toString());

                //다음 페이지로 넘기기
                startActivity(intent);
            }
        });
    }
}
