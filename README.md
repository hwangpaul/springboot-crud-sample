# Spring Boot MyBatis CRUD Sample (DAO/VO Pattern)

Spring Boot 3.x with Java 21을 사용한 MyBatis 기반 REST API CRUD 샘플 프로젝트입니다.
DAO/VO 패턴과 Service 인터페이스/Impl 구현 패턴을 사용하여 전통적인 계층형 아키텍처를 구현했습니다.

## 기술 스택

- **Java 21**
- **Spring Boot 3.2.0**
- **MyBatis 3.0.3**
- **Oracle Database** (ojdbc11)
- **Lombok**
- **Spring Validation**
- **Maven**

## 프로젝트 구조

```
springboot-crud-sample/
├── src/
│   ├── main/
│   │   ├── java/com/sample/crud/
│   │   │   ├── CrudSampleApplication.java    # 메인 애플리케이션
│   │   │   ├── controller/
│   │   │   │   └── UserController.java       # REST API 컨트롤러
│   │   │   ├── service/
│   │   │   │   ├── UserService.java          # 서비스 인터페이스
│   │   │   │   └── impl/
│   │   │   │       └── UserServiceImpl.java  # 서비스 구현체
│   │   │   ├── dao/
│   │   │   │   └── UserDAO.java              # 데이터 접근 객체 (DAO)
│   │   │   ├── vo/
│   │   │   │   └── UserVO.java               # 값 객체 (VO)
│   │   │   ├── dto/
│   │   │   │   ├── UserRequest.java          # 요청 DTO
│   │   │   │   └── UserResponse.java         # 응답 DTO
│   │   │   ├── config/
│   │   │   │   └── MyBatisConfig.java        # MyBatis 설정
│   │   │   └── exception/
│   │   │       ├── GlobalExceptionHandler.java
│   │   │       ├── ErrorResponse.java
│   │   │       └── ValidationErrorResponse.java
│   │   └── resources/
│   │       ├── mapper/
│   │       │   └── UserMapper.xml            # MyBatis XML Mapper
│   │       ├── dbscript/
│   │       │   └── oracle/
│   │       │       └── schema.sql            # Oracle DDL 스크립트
│   │       └── application.properties        # 애플리케이션 설정
│   └── test/
│       └── java/com/sample/crud/
│           └── UserControllerTest.java       # 통합 테스트
└── pom.xml
```

## 아키텍처 (DAO/VO 패턴 + Service Interface/Impl)

```
┌─────────────────────────────────────────────────────────────┐
│                         Controller                         │
│  (REST API 엔드포인트 처리, DTO 변환)                        │
└─────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────┐
│                      UserService (Interface)                 │
│  (서비스 인터페이스 정의)                                    │
└─────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────┐
│                   UserServiceImpl (Implementation)           │
│  (비즈니스 로직, 트랜잭션 관리, DAO 호출)                    │
└─────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────┐
│                            DAO                               │
│  (데이터베이스 접근, MyBatis Mapper 인터페이스)             │
└─────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────┐
│                     MyBatis Mapper                          │
│  (SQL 매핑, UserMapper.xml)                                 │
└─────────────────────────────────────────────────────────────┘
                              ↓
                    Oracle Database
```

### 계층별 역할

- **Controller**: HTTP 요청을 받고 응답을 반환. DTO ↔ Service 간 데이터 변환
- **Service (Interface)**: 서비스 계층의 인터페이스 정의. 구현과 분리하여 유연성 확보
- **ServiceImpl (Implementation)**: 비즈니스 로직 실제 구현, @Transactional 트랜잭션 관리, DAO 호출
- **DAO**: 데이터베이스 CRUD 연산 수행, MyBatis와 연동
- **VO (Value Object)**: 데이터베이스 테이블과 매핑되는 순수 자바 객체
- **DTO (Data Transfer Object)**: API 요청/응답용 객체

### Service Interface/Impl 패턴의 장점

1. **느슨한 결합 (Loose Coupling)**: Controller는 구현체가 아닌 인터페이스에 의존
2. **다형성 (Polymorphism)**: 여러 구현체가 필요할 때 인터페이스를 통해 쉽게 교체 가능
3. **테스트 용이성**: Mock 객체를 사용하여 단위 테스트 작성이 쉬움
4. **코드 유지보수성**: 인터페이스를 통해 계약 사항이 명확하게 정의됨

## 데이터베이스 설정

### Oracle Database 준비

1. Oracle Database에 새로운 사용자를 생성합니다:

```sql
CREATE USER sample_user IDENTIFIED BY sample_password;
GRANT CONNECT, RESOURCE TO sample_user;
GRANT CREATE SESSION, CREATE TABLE, CREATE SEQUENCE TO sample_user;
```

2. 테이블 스크립트를 실행합니다:

```bash
sqlplus sample_user/sample_password @src/main/resources/dbscript/oracle/schema.sql
```

### application.properties 설정

```properties
# Oracle Database Configuration
spring.datasource.url=jdbc:oracle:thin:@localhost:1521:xe
spring.datasource.username=sample_user
spring.datasource.password=sample_password
```

실제 환경에 맞게 Oracle 연결 정보를 수정하세요.

## 빌드 및 실행

### 빌드

```bash
mvn clean install
```

### 실행

```bash
mvn spring-boot:run
```

애플리케이션이 `http://localhost:8080`에서 실행됩니다.

## API 엔드포인트

### 1. 사용자 생성

**POST** `/api/users`

```json
{
  "name": "John Doe",
  "email": "john@example.com",
  "phone": "1234567890",
  "address": "123 Main St"
}
```

### 2. 사용자 조회 (ID별)

**GET** `/api/users/{id}`

### 3. 전체 사용자 조회

**GET** `/api/users`

### 4. 사용자 수정

**PUT** `/api/users/{id}`

```json
{
  "name": "Updated Name",
  "email": "updated@example.com",
  "phone": "0987654321",
  "address": "456 Oak Ave"
}
```

### 5. 사용자 삭제

**DELETE** `/api/users/{id}`

## MyBatis 설정

MyBatis XML Mapper는 `src/main/resources/mapper/UserMapper.xml`에 위치합니다.

주요 설정:
- **mapper-locations**: `classpath:mapper/*.xml`
- **type-aliases-package**: `com.sample.crud.vo` (VO 패키지)
- **map-underscore-to-camel-case**: `true` (DB 컬럼명 snake_case → Java camelCase 자동 매핑)

## Service Interface/Impl 패턴 예제

### UserService.java (Interface)
```java
public interface UserService {
    UserResponse createUser(UserRequest request);
    UserResponse getUserById(Long id);
    List<UserResponse> getAllUsers();
    UserResponse updateUser(Long id, UserRequest request);
    void deleteUser(Long id);
}
```

### UserServiceImpl.java (Implementation)
```java
@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserDAO userDAO;

    @Override
    public UserResponse createUser(UserRequest request) {
        // 비즈니스 로직 구현
        UserVO userVO = UserVO.builder()
                .name(request.getName())
                .email(request.getEmail())
                .build();
        userDAO.insert(userVO);
        return UserResponse.from(userVO);
    }
}
```

### UserController.java
```java
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService; // 인터페이스에 의존

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequest request) {
        UserResponse response = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
```

## DAO/VO 패턴 예제

### UserDAO.java
```java
@Mapper
public interface UserDAO {
    UserVO findById(@Param("id") Long id);
    List<UserVO> findAll();
    int insert(UserVO userVO);
    int update(UserVO userVO);
    int deleteById(@Param("id") Long id);
}
```

### UserVO.java
```java
@Data
@Builder
public class UserVO {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String address;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

### UserMapper.xml
```xml
<select id="findById" resultMap="UserResultMap">
    SELECT id, name, email, phone, address, created_at, updated_at
    FROM users
    WHERE id = #{id}
</select>
```

## 테스트

```bash
mvn test
```

## 주요 기능

- ✅ DAO/VO 패턴 기반 계층형 아키텍처
- ✅ Service Interface/Impl 패턴 적용
- ✅ CRUD 전체 기능 구현 (MyBatis 기반)
- ✅ 입력 데이터 검증 (Validation)
- ✅ 중복 이메일 체크
- ✅ 전역 예외 처리
- ✅ 통합 테스트
- ✅ Oracle Sequence를 통한 자동 ID 생성
- ✅ 자동 생성/수정 시간 관리 (SYSDATE)
- ✅ RESTful API 설계
- ✅ Snake_case ↔ CamelCase 자동 매핑
- ✅ 느슨한 결합과 다형성 지원

## 요구사항

- Java 21 이상
- Maven 3.6 이상
- Oracle Database 11g 이상

## 참고

- Oracle JDBC Driver: `ojdbc11` (Java 11+ 지원)
- MyBatis Spring Boot Starter: `3.0.3` (Spring Boot 3.x 호환)
- Oracle Connection Pooling: HikariCP (Spring Boot 기본 제공)
- DAO/VO 패턴: 전통적인 계층형 아키텍처 적용
- Service Interface/Impl 패턴: SOLID 원칙 준수, 테스트 용이성 및 유지보수성 향상
