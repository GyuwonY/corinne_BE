# 코린이(corinne) - 코인 모의투자 연습사이트

![로고 예제1](https://user-images.githubusercontent.com/42165194/170652570-58f367d8-dd2a-4c6e-9ff1-880decb4eb3e.png)

### 도메인: [corinne](https://www.corinne.kr)

<br/>

## :calendar: 프로젝트 기간

2022년 4월 22일 ~ 2022년 6월 3일 (4주)

## :clipboard: 프로젝트 소개

코린이들을 위한 모의투자 corinne

corinne는 코인 초보자분들이 쉽고 재미있게 접할 수 있도록 다양한 기능들을 제공하는 코인 모의투자 서비스입니다.

### 👉 자세히 보기: [노션링크](https://silken-hip-c47.notion.site/corinne-dd258f60090745c9849f9462080514b2)

<br/>

## 서비스 아키텍처

![서비스 아키텍처](https://user-images.githubusercontent.com/93954839/170642303-5fc5675f-37a7-450b-9611-3710fcb410eb.PNG)

## 기술스택

#### :boom: Frontend

next.js, vercel, redux, tailwindCSS, PostCSS, sockjs, stomp, ApexChart, Chart.js

#### :boom: Back-end

SpringBoot, SpringSecurity, Socket.io, MySQL, Redis, AWS S3, Github Actions,  AWS CodeDeploy,  NGINX,  AWS EC2

## Trouble Shooting
<details>
<summary>N + 1 문제</summary>
  <div markdown="1">   
  문제<br>
  랭킹 리스트 조회 시 연산이 늦어 페이지 랜더링 속도 저하<br><br>

  원인<br>
  User Table과 Coin Table 간의 관계가 설정되어 있으나 랭킹 리스트를 연산하기 위해 회원이 가진 코인 정보를 조회할 경우 Coin 정보를 찾는 쿼리가 추가 발생되는 N+1 문제 발생<br>
![Untitled](https://user-images.githubusercontent.com/93954839/176990462-7b9607d8-5e47-4cb5-9901-2fe190cd9778.png)

  해결<br>
  Fetch join을 이용해 User 조회 시 Coin의 정보까지 영속할 수 있도록 수정하여 조회 성능 개선<br>
  ![Untitled (1)](https://user-images.githubusercontent.com/93954839/176990469-36276e64-ae36-4f39-a9d6-8a3a060deb58.png)
</div>
</details>

<details>
<summary>웹소켓 API 통신 Interval로 인한 수집 데이터 변질 문제</summary>
문제<br>
분봉 데이터를 추출하기 위해 1분간 가격 변동 데이터를 캐시(Redis)에 저장 후 *분 00초에   1분간에 대한 시작가, 최종가, 저가, 고가를 추출하였으나 정확하지 않은 데이터가 쌓이거나 시간 정보가 1분씩 늦춰지는 문제 발생<br><br>
원인<br>
Web Socket API를 통한 데이터 수신 시 통신 Interval로 인해 0분 59.***초에 발생된 가격 변동 데이터가 1분 00초에 도착하면서 추출한 데이터가 변질됨<br><br>
해결<br>
통신 Interval 고려하여 *분 01초에 분봉 데이터를 수집할 수 있도록 Scheduler를 변경하고 실시간 데이터 큐에 담겨있는 minute과 다르기 전까지의 데이터만 분봉의 데이터로써 활용하여 추출
</div>
</details>

<details>
<summary>레버리지 이용 시 코인이 하락한 경우 코인의 가치가 음수가 되는 문제</summary>
문제<br>
레버리지 기능을 이용하여 코인을 매수한 경우 가격 하락 시 25~100배 영향을 받아 보유 코인의 가치가 음수가 되는 경우 발생<br>
레버리지 : 매수 시 선택한 레버리지 배수만큼 등락 적용을 받을 수 있는 기능<br>
ex) 레버리지 25x 적용 매수 후 4% 하락시 100% 하락 적용되어 보유 코인 가치가 0원<br><br>

원인<br>
코인 가치가 0원이 되는 경우 청산 기능을 통해 보유 코인이 삭제되어야 하나 청산 기능이 정확하게 동작하지 않음<br><br>
해결<br>
레버리지를 이용한 매수 시 캐시(Redis)에 코인 가치가 0원이 될 수 있는 현재가(청산가)를 저장한 뒤 현재가가 청산가보다 낮아지는 경우 보유한 코인을 삭제 시키고 사용자가 인지할 수 있도록 소켓을 통한 알림을 보내주어 해결
</div>
</details>

## UI

<details>
<summary>여기를 눌러주세요</summary>
<div markdown="1">   

#### 메인페이지

![메인페이지](https://user-images.githubusercontent.com/93954839/170641474-02c4b7c7-5a94-450f-b026-a34d94643801.PNG)

#### 모의투자페이지

![모의투자화면](https://user-images.githubusercontent.com/93954839/170641555-55b3c709-ad0a-4475-a030-fa5c4871845e.PNG)

#### 랭킹페이지

![랭킹페이지](https://user-images.githubusercontent.com/93954839/170641525-ac36933e-cd80-4cf6-a462-f091431c2816.PNG)

#### 마이페이지

![마이페이지](https://user-images.githubusercontent.com/93954839/170641538-59df30c8-a305-4006-8b44-c2abade7a418.PNG)
  
</div>
</details>

## 팀원소개

| Name                 | GitHub / Contact                       | Position    |
| -------------------- | -------------------------------------- | ----------- |
| Frontend Github Link | https://github.com/suns2131/corinne_fe |
| 윤선식VL             | https://github.com/suns2131            | FE / React  |
| 원동환               | https://github.com/endol007            | FE / React  |
| Backend Github Link  | https://github.com/GyuwonY/corinne_BE  | API Repository |
|                      | https://github.com/GyuwonY/coin_data   | Socket client Repository |
| 유규원L              | https://github.com/GyuwonY             | BE / Spring |
| 정제무               | https://github.com/Jemoo1060           | BE / Spring |     |
