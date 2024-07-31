<img src="https://capsule-render.vercel.app/api?type=shark&color=FCF6BD&height=150&section=header" />

# FLETTER APP
### 인공지능을 활용한 이커머스 서비스 개발

[프로젝트 기술서](https://docs.google.com/presentation/d/1Hjp7XCUMIhym937i4X85ireXrZpCJRmZErmB6bO8PHI/edit#slide=id.g2ecd2bcd2c6_0_113)
| [앱 구현 영상](https://www.youtube.com/watch?v=twt-AMg5pG8)
| [Figma 화면 기획서](https://www.figma.com/design/sZIyaeA257cko90JTBealI/FLOWER-APP?node-id=545-2765&t=Zvjg9QKV2RNZPhYv-1) | [API 명세서 (Postman)](https://documenter.getpostman.com/view/35043308/2sA3e1CATt)



✏️ 작업순서
-

데이터 주제 선정 ➡︎ 데이터 가공(Teachable Machine, 웹 크롤링, 주피터노트북) ➡︎ 화면기획서 (Figma) ➡︎ 테이블 명세서 작성(ERD Cloud) ➡︎ github Organizations로 팀 레파지토리 생성 및 팀원 별 직무 할당 ➡︎ API 명세서 (postman) ➡︎ DB 인덱스 적용




✏️ 데이터 가공 [FLETTER-dateAnalysis]
-

티처블 머신을 통한 탠서플로우 라이트 학습시킨 머신 사용 
- labels.txt 에 작성되어 있는 예측 명을 영어에서 한글로 변경하여 예측 값 한글로 나올 수 있도록 데이터 가공했습니다.

웹 크롤링을 통한 꽃 데이터 수집



✏️ Server 개발 [aws-FLETTER-server]
-

Visual Studio Code를 사용해서 서버 개발 (Python)
- Framwork는 Flask, Serverless 사용했습니다.

서버 아키텍처
- AWS IAM, LAMBDA, RDS(MySQL)로 구성했습니다. 

VSC 폴더명으로 정리했습니다.
✉︎ APP.PY
- 환경변수 세팅
- JWT 토큰 사용하기 위한 함수 및 코드
- Entry Point. api.add_resource 입력
- 도커사용을 위한 핸들러 코드

✉︎ CART.PY
- 0

✉︎ CHAT GPT.PY 
- 0

✉︎ FLOWERS.PY
- 0

✉︎ ORDER.PY
- 0

✉︎ ORDERLIST.PY
- 0

✉︎ USER.PY
- 0

✉︎ WISH.PY
- 0





✏️ 배포
-

serverless, Docker, AWS LAMBDA 사용해서 배포했습니다.

github Actions로 git pull 자동화했습니다.



✏️ 화면 개발 [FLETTER-AndroidStudio]
-


파일명으로 정리했습니다.

✉︎ JAVA
- ✉︎ ADAPTER
  - 0
- ✉︎ API
  - 0
- ✉︎ CONFIG
  - 0
- ✉︎ MODEL
  - 0

✉︎ ML
- model.tflite




✏️ 사용한 프로그램
-

<a href="https://jupyter.org/"><img src="https://img.shields.io/badge/jupyter-F37626?style=flat-square&logo=jupyter&logoColor=white"/></a>
<img src="https://img.shields.io/badge/Amazon AWS-232F3E?style=flat-square&logo=amazonaws&logoColor=white"/>
<img src="https://img.shields.io/badge/Visual Studio Code-007ACC?style=flat-square&logo=Visual Studio Code&logoColor=white"/>
<img src="https://img.shields.io/badge/Docker-2496ED?style=flat-square&logo=Docker&logoColor=white"/>
<img src="https://img.shields.io/badge/MariaDB-003545?style=flat-square&logo=mariaDB&logoColor=white"/>
<img src="https://img.shields.io/badge/TensorFlow-FF6F00?style=flat-square&logo=tensorflow&logoColor=white"/>
<img src="https://img.shields.io/badge/Figma-F24E1E?style=flat-square&logo=figma&logoColor=white"/>
<img src="https://img.shields.io/badge/Android Studio-3DDC84?style=flat-square&logo=Android Studio&logoColor=white"/>


<img src="https://img.shields.io/badge/Flask-000000?style=flat-square&logo=flask&logoColor=white"/> <img src="https://img.shields.io/badge/serverless-FD5750?style=flat-square&logo=serverless&logoColor=white"/>



✏️ 사용한 언어
-

<img src="https://img.shields.io/badge/java-007396?style=flat-square&logo=java&logoColor=white"/> <img src="https://img.shields.io/badge/JSON-000000?style=flat-square&logo=json&logoColor=white"/> <img src="https://img.shields.io/badge/Python-3776AB?style=flat-square&logo=Python&logoColor=white"/>


<img src="https://capsule-render.vercel.app/api?type=shark&color=FCF6BD&height=150&section=footer" />
"# FLETTER-APP" 
