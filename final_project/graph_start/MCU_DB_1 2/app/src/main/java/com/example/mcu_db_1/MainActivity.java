package com.example.mcu_db_1;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.net.Socket;
import java.io.*;
import java.util.ArrayList;

import androidx.appcompat.app.AlertDialog;
import android.content.DialogInterface;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.utils.ColorTemplate;

public class MainActivity extends AppCompatActivity {

    String hostname;  //host 이름
    String response;    //응답
    String temperature = "0";   //온도 변수
    String airhumid = "0";  //대기습도 변수
    String soilhumid = "0"; //토양습도 변수
    Handler handler = new Handler();    //핸들러

    Button btn_select;  //습도 설정(0~100)
    Button btn_water;   //물조절 설정(0~100)
    Button btn_refresh; //새로고침
    TextView s_humi;    //interface 토양습도
    TextView a_humi;    //interface 대기습도
    TextView temp;      //interface 온도
    TextView ex;        //interface 상태 변화 텍스트(logcat 대신 사용) - 버튼 하단

    ArrayList<Double> jsonList = new ArrayList<>(); // ArrayList 선언(수치)
    ArrayList<String> labelList = new ArrayList<>(); // ArrayList 선언(라벨)
    HorizontalBarChart barChart;    //차트

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //id값 가져오기
        btn_select = findViewById(R.id.button);
        btn_water = findViewById(R.id.button2);
        btn_refresh = findViewById(R.id.button3);
        s_humi = findViewById(R.id.textView8);
        a_humi = findViewById(R.id.textView9);
        temp = findViewById(R.id.textView10);
        ex = findViewById(R.id.textView6);
        barChart = (HorizontalBarChart)findViewById(R.id.chart);

        //intent 객체 생성
        Intent intent = getIntent();

        //intent 객체를 이용하여 이전 페이지에서 입력한 호스트 주소값을 hostname 변수에 저장
        hostname = intent.getExtras().getString("host_");

        //초기 그래프 설정
        graphAppearance();

        //앱을 실행하자마자 바로 온습도 정보 받기
        ex.setText("I'm wake up");
        SocketThread thread = new SocketThread(hostname, "refresh*");
        thread.start();

        //안드로이드 스튜디오 기본 액션바 숨기기
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        //습도 설정 버튼
        btn_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //다이얼로그 ad 설정
                AlertDialog.Builder ad = new AlertDialog.Builder(MainActivity.this);
                ad.setTitle("Humidity Control");    //다이얼로그 제목 설정
                ad.setMessage("습도 설정입니다.");     //다이얼로그 내부 메세지 설정

                //다이얼로그 EditText
                EditText et = new EditText(MainActivity.this);  //다이얼로그 내부 edittext
                et.setRawInputType(InputType.TYPE_CLASS_NUMBER);    //editText의 입력을 넘패드로만 가능
                ad.setView(et);

                //다이얼로그 확인 버튼 설정
                ad.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String str = et.getText().toString();   //editText로 받은 값을 String 값으로 변환(통신 데이터, int값 변환 원천 데이터)
                        //paraseInt 예외처리
                        try{
                            //조건 판별을 위한 int 변환
                            int input = Integer.parseInt(str);

                            //editText에 들어가는 값은 0~100사이의 값
                            if (input >= 0 && input <= 100){
                                str = "Humid " + str + "*"; //통신을 위한 포맷

                                //소켓통신
                                SocketThread thread = new SocketThread(hostname, str);
                                thread.start();
                                ex.setText("Really??");
                                dialogInterface.dismiss();
                            }

                            //조건을 만족하지 못한다면(0~100 사이의 값을 벗어난다면), 스트림에 값을 전달하지 않음
                            else{
                                ex.setText("It's not valid value");
                            }
                        }catch (Exception e){
                            ex.setText("Select when you clicked button!!!!!");
                        }
                    }
                });
                ad.show();  //다이얼로그 출력

            }
        });

        //물조절 설정 버튼
        btn_water.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //다이얼로그 ad 설정
                AlertDialog.Builder ad = new AlertDialog.Builder(MainActivity.this);
                ad.setTitle("Watering Setting");    //다이얼로그 제목 설정
                ad.setMessage("급수 설정입니다.");     //다이얼로그 내부 메세지 설정

                EditText et = new EditText(MainActivity.this);  //다이얼로그 내부 edittext
                et.setRawInputType(InputType.TYPE_CLASS_NUMBER);    //editText의 입력을 넘패드로만 가능
                ad.setView(et);

                //다이얼로그 확인 버튼 설정
                ad.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String str = et.getText().toString();   //editText로 받은 값을 String 값으로 변환(통신 데이터, int값 변환 원천 데이터)
                        try {
                            int input = Integer.parseInt(str);  //조건 판별을 위한 int 변환

                            //editText에 들어가는 값은 0~100사이의 값
                            if (input >= 0 && input <= 100) {
                                str = "water " + str + "*";     //통신을 위한 포맷

                                //소켓통신
                                SocketThread thread = new SocketThread(hostname, str);
                                thread.start();
                                ex.setText("I hope he doesn't die");
                                dialogInterface.dismiss();
                            }

                            //조건을 만족하지 못한다면(0~100 사이의 값을 벗어난다면), 스트림에 값을 전달하짛않음
                            else {
                                ex.setText("What's that suppose to mean???");
                            }
                        }catch (Exception e){
                            ex.setText("You have to enter the value when you clicked button!!");
                        }
                    }
                });
                ad.show();  //다이얼로그 출력
            }
        });

        //새로 고침 버튼
        btn_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //버튼을 클릭하였다는 상태 메세지
                ex.setText("Check_Check_Check");

                //소켓통신
                SocketThread thread = new SocketThread(hostname, "refresh*");
                thread.start();
            }
        });
    }


    class SocketThread extends Thread {

        private String host;    //호스트
        private String data;    //사용자가 입력한 값 + 메인 onCreate에서 받는 데이터

        //생성자 함수 - onCreate에서 소켓 통신을 위해 객체 생성시 호스트와 data(입력값과 명령어)로 초기화
        public SocketThread(String host, String data) {
            this.host = host;
            this.data = data;
        }

        public void run() {

            try{
                int port = 10000; //포트 번호는 서버측과 똑같이
                Socket socket = new Socket(host, port); // 소켓 열어주기

                //data에 water 명령어 포함 시
                if(data.contains("water")) {
                    BufferedWriter outstream = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())); //소켓의 출력 스트림 참조
                    outstream.write(data); // 출력 스트림에 데이터 넣기
                    outstream.flush(); // 출력
                }

                //data의 값이 refresh와 같을 때
                else if(data == "refresh*"){
                    BufferedWriter outstream = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())); //소켓의 출력 스트림 참조
                    outstream.write(data); // 출력 스트림에 데이터 넣기
                    outstream.flush();
                    BufferedReader instream = new BufferedReader( new InputStreamReader(socket.getInputStream())); // 소켓의 입력 스트림 참조

                    response = instream.readLine(); // 응답 가져오기
                    String res[] = response.split(" "); //응답메시지 분리하여 온도, 습도, 토양습도 넣기

                    //분리값 배열에 저장
                    temperature = res[0];
                    airhumid = res[1];
                    soilhumid = res[2];
                }

                //이외
                else{
                    BufferedWriter outstream = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())); //소켓의 출력 스트림 참조
                    outstream.write(data); // 출력 스트림에 데이터 넣기
                    outstream.flush(); // 출력
                }

                //핸들러
                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        //interface TextView에 값을 세팅, 조회(refresh 버튼 클릭 시)
                        s_humi.setText(soilhumid);
                        a_humi.setText(airhumid);
                        temp.setText(temperature);

                        //그래프 값 세팅
                        graphInitSetting();
                        BarChartGraph(labelList, jsonList);
                    }
                });

                socket.close(); // 소켓 해제

            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    //그래프 외형 설정
    public void graphAppearance(){

        //차트 터치 설정 해제
        barChart.setTouchEnabled(false);

        //x축
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        //y축
        YAxis axisLeft = barChart.getAxisLeft();
        YAxis axisRight = barChart.getAxisRight();

        //y축 라인, grid 노출 해제
        axisLeft.setDrawGridLines(false);
        axisLeft.setDrawAxisLine(false);
        axisRight.setDrawGridLines(false);
        axisRight.setDrawAxisLine(false);

        //y축 최고값, 최저값 세팅
        axisRight.setAxisMinValue(0);
        axisRight.setAxisMaxValue(100);
        axisLeft.setAxisMinValue(0);
        axisLeft.setAxisMaxValue(100);
    }

    //그래프 내부 초기값 설정
    public void graphInitSetting(){

        //그래프에 표시할 라벨 설정
        labelList.add("대기습도");
        labelList.add("토양습도");

        try {
            //입력 데이터(String) -> int값으로 변환
            double i_airhumid = Double.parseDouble(airhumid);
            double i_soilhumid = Double.parseDouble(soilhumid);

            //그래프 설정 수치값
            jsonList.add(i_airhumid);
            jsonList.add(i_soilhumid);
        }catch (Exception e){
            ex.setText("I can't draw a graph..");
        }

    }

    //그래프에 값 세팅
    private void BarChartGraph(ArrayList<String> labelList, ArrayList<Double> valList){
        //엔트리 arraylist
        ArrayList<BarEntry> entries = new ArrayList<>();

        //엔트리 리스트에 수치값
        for (int i = 0; i < valList.size(); i++) {
            entries.add(new BarEntry(valList.get(i).floatValue(), i));
        }

        //수차값들의 데이터 세팅
        BarDataSet depenses = new BarDataSet(entries, "습도"); // 변수로 받아서 넣어줘도 됨
        depenses.setAxisDependency(YAxis.AxisDependency.LEFT);
        barChart.setDescription(" ");

        //라벨 arraylist
        ArrayList<String> labels = new ArrayList<String>();

        //라벨 arraylist에 라벨값
        for (int i = 0; i < labelList.size(); i++) {
            labels.add((String) labelList.get(i));
        }

        //BarData 객체에 라벨과 수치값 세팅
        BarData data = new BarData(labels, depenses);

        //그래프 색상
        depenses.setColors(ColorTemplate.VORDIPLOM_COLORS );

        //그래프에 데이터를 실제로 세팅 및 보여주기
        barChart.setData(data);
        barChart.animateXY(1000, 1000); // 그래프 표시시 애니메이션
        labelList.clear();
        valList.clear();
        barChart.invalidate();
    }
}

