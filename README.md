<img src="https://capsule-render.vercel.app/api?type=shark&color=FCF6BD&height=150&section=header" />

# FLETTER APP
### 인공지능을 활용한 이커머스 서비스 개발

[프로젝트 기술서](https://docs.google.com/presentation/d/1Hjp7XCUMIhym937i4X85ireXrZpCJRmZErmB6bO8PHI/edit#slide=id.g2ecd2bcd2c6_0_113)
| [앱 구현 영상](https://www.youtube.com/watch?v=twt-AMg5pG8)
| [Figma 화면 기획서](https://www.figma.com/design/sZIyaeA257cko90JTBealI/FLOWER-APP?node-id=545-2765&t=Zvjg9QKV2RNZPhYv-1) | [API 명세서 (Postman)](https://documenter.getpostman.com/view/35043308/2sA3e1CATt)



✏️ 작업순서
-

데이터 주제 선정 ➡︎ 데이터 가공(Teachable Machine, 웹 크롤링, 주피터노트북) ➡︎ 화면기획서 (Figma) ➡︎ 테이블 명세서 작성(ERD Cloud) ➡︎ github Organizations로 팀 레파지토리 생성 및 팀원 별 직무 할당 ➡︎ API 명세서 (postman) ➡︎ DB 인덱스 적용



✏️ 프로젝트 소개
-
### 프로젝트 명 : FLETTER APP
커스터마이징 시대에 발맞춰 꽃 조합을 커스터마이징 할 수 있으며, 꽃 조합이 어려울 때는 인공지능이 추천하는 꽃 조합으로도 구매가 가능한 이커머스 앱을 사용해보세요.

### ✉︎ 서비스 타켓 및 목표
#### 서비스 타켓 : 
- 꽃을 직접 조합해서 구매하고 싶은 사람
- 나의 상황에 맞게 알아서 꽃 조합해주면 어렵지 않게 구매하고 싶은 사람
- 선물 받은 꽃 이름을 알고 싶은 사람
#### 서비스 목표 :
꽃을 고객의 마음대로 쉽게 구매할 수 있는 서비스와 선물 받은 사람도 꽃 이름을 알고 싶을 때 바로 알 수 있는 서비스

> 특장점 :
>
> JWT 토큰을 활용해서 비 로그인 사용자 구별
>
> 고객의 데이터를 활용한 RDBMS 테이블 JOIN 데이터 전달
> 
> CHAT GPT openAI 적용한 서비스 구현
> 
> 실시간 위치를 기반으로한 유저 친화적 서비스
> 
> Kakao Map API를 활용한 길찾기 서비스
> 
> 버튼 챗봇과 CHAT GPT openAI을 더한 챗봇 서비스
> 
> 티처블 머신의 학습과 TensorFlow Lite로 사진 예측
> 
> github를 화용한 CI/CD 환경 구축


✏️ 핵심 기능
-
  <p align="center">
    <img src="https://github.com/user-attachments/assets/cde5b9c7-c0fd-41f7-b8df-5a8b581c017c">
  </p>

#### 1. 꽃 커스터마이징 주문
  <p align="center">
    <img src="https://github.com/user-attachments/assets/17dc05ac-dce0-4d30-b389-f6b2b646d053">
  </p>
- 사용자의 관심 등록 꽃은 꽃 이름 앞에 하트로 표시해서 알려줍니다. (제일 상단에 위치)
- AI에게 추천을 받은 기록이 있다면 꽃 조합을 확인 할 수 있습니다.

#### 2. 인공지능에게 추천받는 꽃 조합
<div>
  <p align="center">
    <img width="200" src="https://github.com/user-attachments/assets/ef39f1a4-f1e8-4967-93e2-e7616713b9ba">
  </p>
</div>
- 꽃을 사는 이유를 듣고 이유를 조합하여 어울리는 꽃을 추천하는 인공지능입니다.
- 추천받은 꽃을 바로 주문하고 싶으면 '추천받은 꽃 조합으로 주문하기'를 선택하면 바로 주문할 수 있습니다.

#### 3. 구매한 꽃 이름 사진으로 예측
<div>
  <p align="center">
    <img width="200" src="https://github.com/user-attachments/assets/1d54e696-de75-4633-82d5-c223e41bf1d5">
  </p>
</div>
- 꽃 사진을 찍어서 올리면, 꽃 이름을 예측하고 그 퍼센트를 알려줍니다.

#### 4. 사용자의 현재위치에서 꽃 매장 위치까지 바로 안내하는 길찾기 KAKAO MAP
<div>
  <p align="center">
    <img width="200" src="https://github.com/user-attachments/assets/acc29ae0-72ab-49bd-b925-305a544c7356">
  </p>
</div>
- 로케이션 매니저와 리스너를 사용해 사용자의 현재위치를 가져와서 KAKAO MAP으로 연결해 사용자의 위치에서 매장 위치까지의 대중교통 길찾기를 보여줍니다.

✏️ 구현 기능
-

- 사용자 관리
  - JWT 토큰을 활용한 로그인, 회원가입, 로그아웃 기능
    
- 판매 꽃 관리
  - 오늘 준비된 꽃 데이터를 DB에서 불러와서 정보 전달 기능 (외부 사용자 사용 가능)
  - 사용자가 관심 꽃을 등록, 취소 하는 기능과 관심 등록 된 꽃을 한번에 확인할 수 있는 기능

- 주문 (사용자 선택 시 가격 정보 저장)
  - 관리자가 등록한 포장데이터 DB에서 가져와서 정보 전달
  - 각 포장 데이터마다 옵션 데이터와 대표 사진을 DB에서 가져와서 정보 전달
  - Chat GPT openAI를 활용해서 버튼 챗봇으로 사용자의 선택에 따른 꽃 조합 추천 기능
  - Chat GPT openAI를 활용한 추천된 꽃 조합의 꽃 이름만 주문 데이터로 전달하는 기능
    
- 장바구니
  - 사용자가 주문 시 선택한 옵션들을 DB에 저장해서 장바구니 화면에서 선택된 옵션 제공 기능
  - 사용자의 장바구니에 상품이 존재하는지 확인해서 없으면 장바구니가 비어있음을 나타내는 화면 제공 기능
  - 장바구니에 있는 상품이 결제 완료 시 DB 데이터를 변경해서 장바구니 화면에서 사라지는 기능
  - 장바구니에 있는 상품 삭제하는 기능
  - 장바구니 상품 수량 1개씩 추가하고 빼는 기능

- 주문정보
  - 주문 정보를 DB에 저장하는 기능
  - 사용자의 주문 내역을 DB에서 가져와서 리스트로 정보 제공
  - 각 주문 내역 별 상세 정보 제공

- 꽃 이름 예측하는 기능
  - Teachable Machine으로 학습시킨 TFLite 인공지능이 꽃 사진을 보고 이름을 예측해주는 기능

- 길찾기
  - 로케이션 매니저와 리스너를 사용해 얻은 사용자의 현재위치에서 지정된 매장 위치까지의 길찾기를 Kakao Map 앱을 이용해서 바로 대중교통으로 오는 방법을 알려주는 기능

✏️ 개발 환경
-

<div>
  <p align="center">
    <img src="https://github.com/user-attachments/assets/a73830d7-1773-417c-b758-5a8b738d4ffe">
  </p>
</div>

✏️ 서버 아키텍처
-

<div>
  <p align="center">
    <img src="https://github.com/user-attachments/assets/eeafa036-3682-46e2-a43c-1a835ea739b3">
  </p>
</div>

✏️ ERD 다이어그램
-

<div>
  <p align="center">
    <img src="https://github.com/user-attachments/assets/537e7329-df60-482f-adcb-5552246cb50e">
  </p>
</div>


✏️ 사용한 프로그램
-

<a href="https://jupyter.org/"><img src="https://img.shields.io/badge/jupyter-F37626?style=flat-square&logo=jupyter&logoColor=white"/></a>
<img src="https://img.shields.io/badge/Amazon AWS-232F3E?style=flat-square&logo=amazonaws&logoColor=white"/>
<img src="https://img.shields.io/badge/Visual Studio Code-007ACC?style=flat-square&logo=Visual Studio Code&logoColor=white"/>
<img src="https://img.shields.io/badge/Docker-2496ED?style=flat-square&logo=Docker&logoColor=white"/>
<img src="https://img.shields.io/badge/MySQL-4479A1?style=flat-square&logo=MySQL&logoColor=white"/>
<img src="https://img.shields.io/badge/TensorFlow-FF6F00?style=flat-square&logo=tensorflow&logoColor=white"/>
<img src="https://img.shields.io/badge/Figma-F24E1E?style=flat-square&logo=figma&logoColor=white"/>
<img src="https://img.shields.io/badge/Android Studio-3DDC84?style=flat-square&logo=Android Studio&logoColor=white"/>


<img src="https://img.shields.io/badge/Flask-000000?style=flat-square&logo=flask&logoColor=white"/> <img src="https://img.shields.io/badge/serverless-FD5750?style=flat-square&logo=serverless&logoColor=white"/>



✏️ 사용한 언어
-

<img src="https://img.shields.io/badge/java-007396?style=flat-square&logo=java&logoColor=white"/> <img src="https://img.shields.io/badge/Python-3776AB?style=flat-square&logo=Python&logoColor=white"/>


<img src="https://capsule-render.vercel.app/api?type=shark&color=FCF6BD&height=150&section=footer" />
"# FLETTER-APP" 
