# Coderend Project

이 프로젝트는 개발 과정에서 발생한 문제와 해결 방법을 공유하는 뉴스피드입니다.
개발자들이 서로의 경험을 배우고 공동체 의식을 함양하는 데 도움이 되는 공간을 제공하는 것을 목표로 합니다.

## 👥 Team
<details>
<summary>팀장 : 조현아</summary>
<div markdown="1">
    
- 뉴스피드 CRUD
- 뉴스피드 페이지네이션
</div>
</details>

<details>
<summary>팀원 : 김원기</summary>
<div markdown="1">
    
- 회원가입, 회원 탈퇴
- 이메일 인증
</div>
</details>

<details>
<summary>팀원 : 이윤성</summary>
<div markdown="1">
    
- Token, 인증/인가 필터
- 로그인, 로그아웃
</div>
</details>

<details>
<summary>팀원 : 정효진</summary>
<div markdown="1">
    
- 댓글 CRUD
</div>
</details>

<details>
<summary>팀원 : 홍용근</summary>
<div markdown="1">
    
- 유저 프로필
- 좋아요 기능
</div>
</details>

## 🛠️ Tech Stack
- 언어 : Java - JDK 17, Groovy
- 프레임워크 : Spring Boot 3.2.5
- 데이터베이스 : Mysql - 8.0.28

## 🔖 Features

- 사용자 인증 기능:
    - Spring Security와 JWT를 사용하여 Access Token, Refresh Token을 구현했습니다.
    - 사용자 ID, 비밀번호를 입력하여 회원가입, 로그인할 수 있습니다.
    - 비밀번호를 입력하여 회원 탈퇴를 할 수 있습니다.
    - 로그아웃 시, 발행한 토큰을 초기화하여 재로그인하도록 합니다.
    - 이메일로 가입했을 때, 이메일 인증 기능을 추가하였습니다.
- 프로필 관리 기능:
    - 사용자 ID, 이름, 한 줄 소개, 이메일을 조회할 수 있습니다.
    - 이름, 이메일, 한 줄 소개, 비밀번호를 수정할 수 있습니다.
- 뉴스피드 CRUD 기능:
    - 뉴스피드를 조회할 수 있습니다.
        - 페이지네이션 기능을 구현하여 각 페이지 당 뉴스피드 데이터가 10개씩 조회할 수 있습니다.
        - 생성일자 기준 최신순, 좋아요 많은 순으로 정렬할 수 있습니다.
        - 선택한 기간 별로 조회할 수 있습니다.
    - 뉴스피드의 작성, 수정, 삭제 기능은 인가가 필요합니다.
        - 뉴스피드의 내용을 입력하여 작성할 수 있습니다.
        - 자신이 작성한 뉴스피드만을 수정, 삭제할 수 있습니다.
    - 본인이 작성하지 않은 뉴스피드에 좋아요를 누를 수 있습니다.
    
- 댓글 CRUD 기능:
    - 각 뉴스피드 별로 댓글을 조회할 수 있습니다.
    - 댓글의 작성, 수정, 삭제 기능은 인가가 필요합니다.
        - 댓글의 내용을 입력하여 작성할 수 있습니다.
        - 자신이 작성한 댓글만을 수정, 삭제할 수 있습니다.
    - 본인이 작성하지 않은 댓글에 좋아요를 누를 수 있습니다.

## 📕 기획 명세서

<details>
<summary>ERD Diagram</summary>
<div markdown="1">

![newsFeed](https://github.com/finestra771/Coderend/assets/110015752/67484b05-fc98-48d4-a002-037ae7653d69)
</div>
</details>

<details>
<summary>API 명세서</summary>
<div markdown="1">

![image](https://github.com/hyojjin-jeong/TodoAppServer/assets/64136923/dfb27511-6a30-4db8-9ad8-7136833d0071)
![image](https://github.com/hyojjin-jeong/TodoAppServer/assets/64136923/d2a7a69c-e727-4450-a541-dac9c9171fb2)
![image](https://github.com/hyojjin-jeong/TodoAppServer/assets/64136923/0e3ca431-cee2-43f7-9235-623f6479a5ea)

</div>
</details>

<details>
<summary>와이어프레임</summary>
<div markdown="1">

![image](https://github.com/hyojjin-jeong/TodoAppServer/assets/64136923/0a9098a9-25be-4cc0-ad2d-57e7e0d280b8)
</div>
</details>

## 🐱 Rules

<details>
<summary>Code Convention</summary>
<div markdown="1">
  
- 패키지 이름 : 소문자(기능으로 묶어서)

- 클래스 이름 : 첫 알파벳은 대문자

- 상수 이름 : 모두 대문자 { *SCREAM_*SNAKE_CASE }

- 탭은 4칸으로

- 변수 이름 : 카멜 케이스(camelCase)

- 메서드 이름 : 카멜 케이스(camelCase)
</div>
</details>

<details>
<summary>Github Rules</summary>
<div markdown="1">

|작업 타입| 작업 내용  |
|-------|--------|
|setting|환경 설정|
| add|새로운 기능 추가|
|fix|코드 수정|
|refactor|코드 리팩토링|
|del|기능/파일 삭제|
|test|test code 작성|
|gitfix|gitignore 수정|
</div>
</details>
