# High파이브 팀
## 프로젝트명
> FITPLE - 나의 성향에 딱 맞는 트레이너, 스마트한 운동 플랜!

## 개요
> 단순한 헬스장 검색이 아니라, 사용자의 운동 성향(HBTI)을 분석하여 최적의 트레이너를 매칭하고, 효과적인 운동 계획을 세울 수 있도록 돕는 서비스입니다.

## member
 - 이경원
   - Spring Security를 활용한 OAuth2 로그인, 로그아웃, 회원가입
   - 세션 기반의 인증이 아닌 JWT를 통한 사용자 인증 구현
   - WebSocket과 Stomp 프로토콜을 활용한 실시간 메시지 전송 및 채팅 기능 구현
   - 관리자 페이지의 사용자의 정보를 직관적으로 데이터화
 - 김범순
   - My page BackEnd 구현
   - mapStruct를 통한 DTO 생성
   - DB 설계 및 JPA 구현
   - React 웹을 Nginx 웹 서버로 구동, EC2 서버로 배포
 - 이동희
   - Admin page CRUD
   - Review 로직 구현
   - HBTI(헬스 MBTI) api
   - Matching page CRUD
   - EC2, RDS 활용 백앤드  서버 배포
 - 현지윤
   - 자격증 CRUD 기능 (생성, 업데이트)
   - 일정 crud
 - 박준우
   - HBTI API 구현 및 백엔드 

## Git Flow

> main - 최종 배포 브렌치  
> development - feature 브랜치에서 작업이 끝났을 경우 기능이 합쳐지는 브랜치  
> feature - 각 기능 작업 브랜치

## 커밋 메시지 컨벤션

- `Struct` : 빌드 업무 수정, 패키지 매니저 수정
- `Feat` : 새로운 기능 추가
- `Fix` : 버그 수정
- `Docs` : 문서 수정
- `Style` : 코드 포맷팅, 세미콜론 누락, 코드 변경이 없는 경우
- `Refactor` : 코드 리펙토링
- `Test` : 테스트 코드, 리펙토링 테스트 코드 추가
- `Chore` : 빌드 업무 수정, 패키지 매니저 수정
- `Conflict`: 충돌 해결

예제: `Feat(#이슈번호): 커밋내용`

## 이슈 타이틀 컨벤션
- `Struct` : 빌드 업무 수정, 패키지 매니저 수정
- `Feat` : 새로운 기능 추가
- `Fix` : 버그 수정
- `Docs` : 문서 수정
- `Style` : 코드 포맷팅, 세미콜론 누락, 코드 변경이 없는 경우
- `Refactor` : 코드 리펙토링
- `Test` : 테스트 코드, 리펙토링 테스트 코드 추가
- `Chore` : 빌드 업무 수정, 패키지 매니저 수정
- `Conflict`: 충돌 해결

## 사용 기술
> org.json 라이브러리, WebSocket, JS, AJAX, GRADLE, Spring Security, Spring Boot3, Java mail sender, JPA, Map Struct
> JQuery, Mybatis, EC2, RDS, POSTMAN, JAVA-17, Lombok, NAVER OPEN API, OAuth2, MySQL, Redis, Stomp

## 유튜브 링크

