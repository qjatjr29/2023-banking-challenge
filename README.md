
## NUMBLE 챌린지 - Spring으로 뱅킹 서버 구축하기 

## ✔️ 프로젝트 개요

### ⌛️ 프로젝트 기간

`2023/02/10(금)` ~ `2023/03/02(목)`

### 🛠 프로젝트 구조


### 🔧 사용 기술

![Java](https://img.shields.io/badge/-Java%2011-007396?style=plastic&logo=java&logoColor=white)
![SpringBoot](https://img.shields.io/badge/-Spring%20Boot%202.7.8-6DB33F?style=plastic&logo=Spring%20Boot&logoColor=white)
![SpringDataJPA](https://img.shields.io/badge/-Spring%20Data%20JPA%202.7.8-6D933F?style=plastic&logo=Spring&logoColor=white)

### 🧱 인프라


## 프로젝트 진행

### Branch Convention

보호되고 있는 브랜치는 `develop`과 `main` 이며,  
`develop`은 개발용 `main`은 배포용입니다.

브랜치는 다음과 같이 명명합니다.

- 기능 개발 목적의 브랜치
    - feature/Jira-이슈번호
- 브랜치에서 발생한 버그 수정 목적의 브랜치
    - hotfix/Jira-이슈번호
    
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

### Code Convention

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

