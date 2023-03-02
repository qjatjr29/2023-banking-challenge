
## NUMBLE 챌린지 - Spring으로 뱅킹 서버 구축하기 

## ✔️ 프로젝트 개요

### ⌛️ 프로젝트 기간

`2023/02/10(금)` ~ `2023/03/02(목)`

### 🛠 프로젝트 구조


#### ERD
<img width="867" alt="image" src="https://user-images.githubusercontent.com/74031333/222031671-ad5db3ad-3b5b-4db5-b95f-eebba3b9f720.png">

#### CI/CD
<img width="1063" alt="image" src="https://user-images.githubusercontent.com/74031333/222036195-bfcbade2-a07d-4ecd-840d-93cd59f9708d.png">

### 🔧 사용 기술

![Java](https://img.shields.io/badge/-Java%2011-007396?style=plastic&logo=Java&logoColor=white)
![SpringBoot](https://img.shields.io/badge/-Spring%20Boot%202.7.8-6DB33F?style=plastic&logo=Spring%20Boot&logoColor=white)
![SpringDataJPA](https://img.shields.io/badge/-Spring%20Data%20JPA%202.7.8-6D933F?style=plastic&logo=Spring&logoColor=white)
![JUnit5](https://img.shields.io/badge/-JUnit5-%2325A162?style=plastic&logo=JUnit5&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL%208-4479A1?style=plastic&logo=MySQL&logoColor=white)
![Redis](https://img.shields.io/badge/-Redis-%23DC382D?style=plastic&logo=Redis&logoColor=white)
![Apache Kafka](https://img.shields.io/badge/-Apache%20Kafka-%23231F20?style=plastic&logo=ApacheKafka&logoColor=white)


### 🧱 인프라
![Gradle](https://img.shields.io/badge/-Gradle%20-02303A?style=plastic&logo=Gradle&logoColor=white)
![AmazonAWS](https://img.shields.io/badge/AWS%20EC2-232F8E?style=plastic&logo=AmazonAWS&logoColor=white)
![GitHub Actions](https://img.shields.io/badge/-Github%20Actions-%232088FF?style=plastic&logo=GithubActions&logoColor=white)
![Docker](https://img.shields.io/badge/-Docker-%232496ED?style=plastic&logo=Docker&logoColor=white)

### 📠 협업툴
![GitHub](https://img.shields.io/badge/-GitHub-181717?style=plastic&logo=GitHub&logoColor=white)

## 🏃🏻 프로젝트 진행

### 📌 구현 필수 기능
- [x] 친구 추가 API
- [x] 내 친구 목록 조회 API
- [x] 계좌 이체 API
- [x] 계좌 조회 API
- [x] 회원가입 API

### 📌 사용 기술
### 1. REST API 사용
``이유``  
REST API만 보고도 이것이 어떤 역할을 하는지 쉽게 이해할 수 있도록 하기 위해

``사용 방법``
1. **URI** - 정보의 자원을 표현
2. 자원에 대한 행위는 **HTTP Method**를 통해 표현
3. HTTP 응답 상태 코드를 통해 응답

### 2. REST Docs 사용

``REST Docs: 테스트 코드 기반으로 Restful API 문서를 돕는 도구``

> Swagger vs REST Docs

**_swagger_** 는 로직에 애노테이션을 이용해 명세를 작성한다.  
-> 코드가 많이 붙게되어 가독성이 떨어지게 된다.

**_REST Docs_** 는 테스트 코드에서 명세를 작성해 비즈니스 로직의 가독성에 영향이 없다.

### 3. JWT && Redis
``JWT 사용 이유``
1. 쿠키 : 보안상 문제가 크다.
2. 세션 : 서버에 많은 부하

`` JWT 장점``  
**서버에 저장하지 않는다 -> 확장성이 높다.**
- 해당 토큰이 유효한지만 체크하면 어떤 서버로 요청을 보내도 상관이 없다.

**데이터 위변조를 막을 수 있다.**
- Header, PayLoad -> Signature 생성

``문제``  
JWT의 AccessToken만 사용하는 인증방식을 사용하게 되면 3자에게 탈취당할 경우 보안에 취약하다.
- AccessToken은 토큰이 만료될 때까지 토큰을 가지고 있는 사람은 누구나 접근이 가능하기 때문
- JWT 토큰의 유효시간을 부여!!

> JWT 토큰 유효시간
- 짧은 경우 : 자주 로그인을 해야한다. (불편)
- 긴 경우  : 토큰을 탈취당했을 때 보안에 취약

<br />

> Refresh Token

AccessToken의 유효시간을 짧게 하고, 긴 유효시간을 가지는 Refresh Token을 통해   
AccessToken이 만료되었다면, RefreshToken을 통해 AccessToken을 재발급한다.

<br />

> Redis로 JWT RefreshToken을 관리
- 레디스는 in-memory로 데이터를 관리하여 **빠른 엑세스 속도 + 휘발성** -> 캐시 용도
- 레디스는 기본적으로 **데이터의 유효시간**을 지정할 수 있다.
  - refresh token을 저장하기 적합
- refresh token의 경우 휘발성으로 삭제가 되더라도 치명적이지 않다.
  - 로그인을 다시 해야하는 정도

### 4. 동시성 - Redisson 분산락

``한 계좌에 동시에 접근하는 경우 동시성 문제가 발생할 수 있다.``
동시성을 해결할 여러 방법을 고민
- ~~비관적 락~~ 
- ~~낙관적 락~~
- **분산락 ⚡️**

<br />

> 분산락






### 5. Event
계좌 이체 로직에 알림 요청로직을 추가하게 되면 로직이 섞이고 복잡해지는 문제가 발생

이체에 성공한 것은 상태가 변경된 것 -> 이벤트 발생  
**이체 성공 이벤트를 활용해 알림 요청로직을 구현**

``장점``
- 이벤트를 사용해 계좌 도메인에서 알림을 요청하는 기능에 대한 의존을 제거
- 기능 확장에도 용이할 것이라 생각 
  - 이체 성공시 알림을 보내는 것 말고도 다른 기능을 추가할 수 있다.
  - ex) 이메일로 이체 내역을 보내는 기능 => 이메일 발송 처리하는 핸들러를 구현

### 6. Kafka
``현재 방법``
<img width="723" alt="image" src="https://user-images.githubusercontent.com/74031333/222041310-a7a52e3b-4aeb-4ea4-9a85-7b6c68e6ff63.png">

#### ❓ 카프카 사용 이유
``가정``  

1. 갑작스런 이체 사용률이 증가, 이에 따른 알림 생성 요청이 증가해 **알림서버에 부하**가 생길 수 있다.
   - 생성 서버의 모든 스레드가 고갈되고 응답시간이 매우 지연되는 상황
2. 알림 서버에 문제가 생기면 알림 생성을 요청하는 연계적인 시스템에서도 문제가 발생하게 될 것이라고 생각.
   - 부수적인 기능인 알림을 위해 핵심 비즈니스로직에 문제가 발생하는 상황이 발생 
3. 알림 서버가 다운되거나 문제가 발생됐을 경우 알림 요청이 유실될 수 있다.
   - 메시지 큐를 사용해 해결해보자.

<br />

> 메시지 큐를 도입하여 알림 생성의 장애나 지연이 뱅킹 서버로 전파되는 것을 막기위해 사용
- RabbitMQ와 달리 kafka는 토픽을 계속 유지해 특정상황이 발생해도 재생가능
- 갑작스런 이체 사용률이 증가하면 병렬처리가 중요하다고 생각 -> Kafka가 적합하다고 생각

##### ⚠️ 문제점 
```약간의 지연```
  - 알림 서비스 특성상 약간의 지연은 괜찮을 것 같다.

##### ❗ 사용하고든 생각 
- 뱅킹 서버에서 알림 생성에 대한 요청을 enqueue 하는것이 좋을지, 알림 서버 enqueue 하는 것이 좋을지? 
  - 뱅킹서버에서는 요청을 보내고 알림 서버에서 해당 요청을 queue에 넣는 방법
  
```
알림 서버에서 enqueue, dequeue하는 과정이 더 좋을 것 같다.  

[이유] 
뱅킹 서버에서 알림 생성 요청을 enqueue하는 것은 비즈니스 로직에만 집중하지 못하는 일인 것 같다.

 -> 리팩토링..! 
```
<img width="690" alt="image" src="https://user-images.githubusercontent.com/74031333/222046655-89230c36-bbb8-46cc-860c-877d8e0a4843.png">


### 7. DDD - Package Structure
* 비즈니스 도메인별로 나누어 설계..!

서로 다른 애그리거트를 하나의 패키지로 잡고 의존성을 없애려고 노력했다.  
=> 높은 응집력, 낮은 결합도로 변경과 확장에 용이한 설계를 만들고자...

#### 결과
DDD 패키지 구조를 사용해보고자 했다.
하지만 아직까진 어떠한 장단점이 있는지 잘 감이 잡히지 않는다.
더욱 명확히 공부를 해야겠다는 생각...

### 8. CQRS
명령 관련 로직과 조회 관련 로직을 분리하기 위해
command, query라는 패키지로 나누었다.

### 9. TEST Code
``테스트 코드 작성 이유``  
1. 작성한 코드가 의도한대로 작동하는지 검증하기 위함.
2. 코드 수정시 수정한 내용에 대해 어떤 영향이 발생하는지 확인하기 위함.
3. 예상치 못하는 문제를 발견하기 위함

<br />

#### 통합테스트를 통해 테스트를 진행
- 이유 : 스프링에서 실제 운영 환경과 같이 전체 플로우가 제대로 동작하는지 확인하기 위함.

<br/>

#### 기능을 검증하고자 할 땐 단위테스트를 진행
* ex) 이벤트 발행
* ex) 이체하는 로직 테스트


#### 테스트 코드를 작성하면서..
- 단위테스트를 작성하는 기준이 명확하지 않았다.
  - 좀 더 기준에 대해 스스로 많이 생각해봐야할 것 같다.
- 테스트 범위를 설정하는 것에 고민이 많았다.
  - Spring Data JPA를 사용하는데 repository에 대한 테스트도 작성을 해야하는지?
  - 모든 메소드(생성자 등)를 모두 테스트를 해야할 지?


### Branch Convention

보호되고 있는 브랜치는 `develop`과 `main` 이며,  
`develop`은 개발용 `main`은 배포용입니다.
    
### Commit Convention

```
feat : 새로운 기능에 대한 커밋
fix : 버그 수정에 대한 커밋
chore : 빌드 업무 수정, 패키지 매니저 수정
docs : 문서 수정에 대한 커밋
style : 코드 스타일 혹은 포맷 등에 관한 커밋
refactor :  코드 리팩토링에 대한 커밋
test : 테스트 코드 수정에 대한 커밋
rename : 파일 혹은 폴더명을 수정하거나 옮기는 작업에 대한 커밋
remove : 파일을 삭제하는 작업에 대한 커밋
```


## 💻 Code Convention

- 코드 스타일
    - google code style
  
- 접근제한자에 따른 코드 작성 순서
    - 필드: public -> private
    - 메서드: public -> private
    - 생성자: private -> public
  
- 어노테이션에 따른 코드 작성 순서
    - DB 관련 어노테이션 (ex: Entity, Table)
    - 객체 관련 어노테이션 (ex: Getter, ToString)
    - 생성 관련 어노테이션 (ex: Builder, RequiredArgsConstructor)
    

### 🔥 더 공부해볼 내용 && 궁금한 내용
1. 패키지 의존성
2. DDD 
   - 내가 적용한 방식이 맞는 방식인지
3. 단위테스트와 통합테스트
   - 테스트를 작성하는 기준을 좀 더 명확히 해야할듯