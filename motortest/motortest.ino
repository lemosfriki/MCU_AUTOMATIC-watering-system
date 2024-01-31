#include <Adafruit_Sensor.h>
#include <DHT.h>
#include <DHT_U.h>
//센서 좌측부터 VCC, DATA, NOT USED, GND
//결선 시 VCC와 DATA 사이에 1킬로 옴 저항 연결
#define DHTPIN 10 //DATA 핀 : 10번 핀
#define DHTTYPE    DHT22 
DHT_Unified dht(DHTPIN, DHTTYPE);

String temp;
String humid;
String tempstr;
int settedhumid = 0;
int currentsoilhumid;


void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);
  dht.begin();
  pinMode(2, OUTPUT);             // 제어 1번핀 출력모드 설정
  pinMode(3, OUTPUT);             // 제어 2번핀 출력모드 설정

}

void loop() {
  // put your main code here, to run repeatedly:
    currentsoilhumid = map(analogRead(A0), 0, 1023, 143, 0);
    if(Serial.available())
    {
      tempstr = Serial.readStringUntil('*'); //종료문자는 '*'
      int index1 = tempstr.indexOf(' '); 
      int index2 = tempstr.length();
      String command = tempstr.substring(0, index1);
      command.trim(); //명령어 분리
      String valstr = tempstr.substring(index1 +1, index2);
      valstr.trim(); //수치 분리
      
      if(command.equals("refresh")) //센서 정보 조회
      {
        getTempHumid();
        command = temp + " " + humid + " " + currentsoilhumid;
        Serial.println(command);
      }
      else if(command.equals("water")) //수동급수 경우
      {
        runMotor((valstr.toInt())*1000);
      }
      else if(command.equals("Humid"))
      {
        settedhumid = valstr.toInt();
      }
     
    }
     if(currentsoilhumid<settedhumid) //설정습도보다 토양습도가 낮을 때
      {
        runMotor(3000);
      }
}



void getTempHumid() //온도와 습도를 측정하는 함수입니다.
{
  sensors_event_t event;
    dht.temperature().getEvent(&event);
    isnan(event.temperature);
    temp = (event.temperature);;
    dht.humidity().getEvent(&event);
    isnan(event.relative_humidity);
    humid = (event.relative_humidity);
}

void runMotor(int motortime)
{
  digitalWrite(2, HIGH);         //모터 작동
  digitalWrite(3, LOW);
  delay(motortime);
  digitalWrite(2, LOW);
  digitalWrite(3, LOW);
}
