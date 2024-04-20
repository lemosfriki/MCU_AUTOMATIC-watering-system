# MCU 프로젝트

## 프로젝트 개요

별다른 노동 없이도, 자동으로 화분에 물을 급수하는 시스템을 제작하고자 한다.
식물을 키우며 가장 중요한 것 중 하나는 적절한 양의 물을 급수하는 것이다. 
그러나 비전문가인 사람이 급수를 진행하게 될 경우, 토양의 습도와 온도를 고려하지 않거나 물의 양을 지나치게 주어 식물을 해칠 염려가 있다. 
혹은 물을 주는 것을 잊어버리거나 직접 급수하는 것이 귀찮다는 이유로 식물을 말라죽게 하는 경우도 빈번하다.
본 프로젝트에서는 위와 같은 상황이 일어나지 않도록 방지하고, 아두이노를 이용한 자동화 시스템을 통해 식물에 대한 급수를 원활하게 진행하도록 돕는 것을 목표로 한다.

### 사전조사 결과

프로젝트를 구체화 하기 위해 시장 조사 결과, 현재 주로 사용되는 급수 시스템은 드리퍼를 이용하여 적정 습도, 사용자 정의 습도를 유지하는 방향으로 사용되고 있다.
드리퍼 방식이 아닌 스마트 기기 연동 시스템의 경우, 큰 농원과 같은 곳에 사용되거나 또는 너무나도 비싼 가격대를 형성하여 일반 사용자들이 쉽게 접근할 수 없는 구조이다.
따라서 이것을 아두이노를 이용하여, 저렴하고 단순하게 구현하고자 자동 식물 급수기를 만들고 스마트폰을 이용하여 자동으로 물을 주는 시스템을 구현하는 프로젝트를 구현하였다.

### 프로젝트 목표

1. FC-28 토양 수분 센서를 사용해 토양 습도 측정 센서를 이용하여서 토양의 습도를 측정
2. DHT11 혹은 DHT22 온습도 센서를 이용하여 대기 중의 온도와 습도를 측정해 모바일에서 습도 값을 설정
3. 식물의 습도를 확인해서 모바일에서 설정한 습도보다 아래의 경우 모터 드라이브(L298N 모터 드라이버)를 통한 수중 펌프 모터가 작동하여서 식물에 물을 주고, 모바일에서 설정한 습도보다 위의 습도 값을 가지는 경우에는 수중 펌프 모터가 작동하지 않는 것
4. 성능 목표치로는 우선 지정 용량 ±20% 이하로 오차 범위내 센서값의 결과가 도출되는 것
5. 토양 습도 센서 지원이 토양의 습도 측정 및 수치값을 가공(%) 하고 온습도 센서 지원은 온도와 습도 측정 및 수치값을 가공
6. GUI 화면 구성 부분은 안드로이드 스튜디오를 이용해 센서로 측정한 값을 출력
7. 버튼 구현 및 이미지와 텍스트 사용하고 사용자가 기준을 숫자로 입력(단위는 %)하여 토양 수분 수치를 결정할 수 있도록 하고 사용자가 수동으로 제어 가능하게 만들며 사용자가 버튼을 누르면 습도와 상관없이 급수가 가능하도록 구현
8. 이를 위해 TCP/IP 통신과 시리얼 통신을 구현
9. 추가적으로 각 시스템의 결과값을 파일로 저장하여, 실제 시스템이 구동되는 방식을 확인
10. C#과 안드로이드 부문의 GUI가 사용자가 직관적으로 보기 편하게 구성이 되도록 구현



## 프로젝트 구현 및 실행 영상 

<iframe width="1261" height="719" src="https://www.youtube.com/embed/pJDVBZhRzws" title="AUTOMATIC watering system with arduino 프로젝트 결과" frameborder="0" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin" allowfullscreen></iframe>



## 프로젝트 추진 일정(간트 차트)

![image](https://github.com/lemosfriki/MCU_AUTOMATIC-watering-system/assets/115825244/622965ee-3c6a-4709-8d92-69132e9e8d63)

## 프로젝트 구성

### 시스템 구성 (하드웨어 설계 회로도)

![image](https://github.com/lemosfriki/MCU_AUTOMATIC-watering-system/assets/115825244/a35bfdab-430b-4586-90c5-8d969d052741)

### 시스템 구성도

![image](https://github.com/lemosfriki/MCU_AUTOMATIC-watering-system/assets/115825244/3b793237-fc11-4be7-9f66-8c4614cebe04)



## C# GUI 및 안드로이드 GUI

C# PC 클라이언트 GUI 
![image](https://github.com/lemosfriki/MCU_AUTOMATIC-watering-system/assets/115825244/8d4ef35f-9d93-4567-989d-a71de582d072)


안드로이드 GUI
![image](https://github.com/lemosfriki/MCU_AUTOMATIC-watering-system/assets/115825244/95f340fa-52d4-4da8-a9c4-fd7ad825809e)

## 실행 유의사항
다운로드 시 final_project내 안드로이드 파일 실행 시 [design editor is unavailable until next gradle sync] 오류가 발생하면 하위 파일 MCU_DB_1 2로 폴더를 오픈하면 해결
