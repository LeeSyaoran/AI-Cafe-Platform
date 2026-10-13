# Testing Documentation

## 1. Testing Strategy

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                               TESTING PYRAMID                                         │
├─────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                      │
│                                      ▲                                              │
│                                     ╱ ╲                                             │
│                                    ╱   ╲                                            │
│                                   ╱     ╲                                           │
│                                  ╱       ╲                                          │
│                                 ╱   E2E   ╲                                        │
│                                ╱  Tests   ╲                                        │
│                               ╱             ╲                                       │
│                              ╱───────────────╲                                      │
│                             ╱                 ╲                                     │
│                            ╱    Integration    ╲                                    │
│                           ╱      Tests         ╲                                   │
│                          ╱                     ╲                                   │
│                         ╱─────────────────────────╲                                 │
│                        ╱                           ╲                                │
│                       ╱        Unit Tests           ╲                               │
│                      ╱                             ╲                              │
│                                                                                      │
│   Level          Scope              Speed              Coverage                     │
│   ─────          ─────              ─────              ───────                      │
│   Unit           Functions          < 100ms            80%+                         │
│   Integration    APIs, DB           < 1s               70%+                        │
│   E2E            Full flow          Minutes            Critical paths                │
│                                                                                      │
└─────────────────────────────────────────────────────────────────────────────────────┘
```

---

## 2. Java Services Testing (Spring Boot)

### 2.1 Maven Dependencies

```xml
<!-- pom.xml -->
<dependencies>
    <!-- Test dependencies -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
    
    <!-- JUnit 5 -->
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter</artifactId>
        <scope>test</scope>
    </dependency>
    
    <!-- Mockito -->
    <dependency>
        <groupId>org.mockito</groupId>
        <artifactId>mockito-core</artifactId>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.mockito</groupId>
        <artifactId>mockito-junit-jupiter</artifactId>
        <scope>test</scope>
    </dependency>
    
    <!-- Testcontainers for integration tests -->
    <dependency>
        <groupId>org.testcontainers</groupId>
        <artifactId>testcontainers</artifactId>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.testcontainers</groupId>
        <artifactId>postgresql</artifactId>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.testcontainers</groupId>
        <artifactId>junit-jupiter</artifactId>
        <scope>test</scope>
    </dependency>
    
    <!-- Database -->
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <scope>test</scope>
    </dependency>
    
    <!-- REST Assured for API testing -->
    <dependency>
        <groupId>io.rest-assured</groupId>
        <artifactId>rest-assured</artifactId>
        <scope>test</scope>
    </dependency>
    
    <!-- Spring Security Test -->
    <dependency>
        <groupId>org.springframework.security</groupId>
        <artifactId>spring-security-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

### 2.2 Unit Test Example

```java
// UserServiceTest.java
package com.aicafe.user.service;

import com.aicafe.user.entity.User;
import com.aicafe.user.repository.UserRepository;
import com.aicafe.user.dto.RegisterRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("User Service Tests")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private OtpService otpService;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(UUID.randomUUID())
                .phone("+84912345678")
                .name("Test User")
                .tier("BASIC")
                .status("ACTIVE")
                .createdAt(LocalDateTime.now())
                .build();

        registerRequest = RegisterRequest.builder()
                .phone("+84912345678")
                .name("Test User")
                .build();
    }

    @Test
    @DisplayName("Should create new user successfully")
    void createUser_Success() {
        // Given
        when(userRepository.findByPhone(anyString())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        User result = userService.createUser(registerRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getPhone()).isEqualTo("+84912345678");
        assertThat(result.getTier()).isEqualTo("BASIC");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when phone already exists")
    void createUser_PhoneExists_ThrowsException() {
        // Given
        when(userRepository.findByPhone(anyString())).thenReturn(Optional.of(testUser));

        // When/Then
        assertThatThrownBy(() -> userService.createUser(registerRequest))
                .isInstanceOf(PhoneAlreadyExistsException.class)
                .hasMessageContaining("Phone number already registered");
        
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should find user by ID")
    void findById_Success() {
        // Given
        UUID userId = testUser.getId();
        when(userRepository.findByIdAndStatusNot(userId, "DELETED"))
                .thenReturn(Optional.of(testUser));

        // When
        Optional<User> result = userService.findById(userId);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(userId);
    }

    @Test
    @DisplayName("Should return empty when user not found")
    void findById_NotFound() {
        // Given
        UUID userId = UUID.randomUUID();
        when(userRepository.findByIdAndStatusNot(userId, "DELETED"))
                .thenReturn(Optional.empty());

        // When
        Optional<User> result = userService.findById(userId);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should update user profile")
    void updateProfile_Success() {
        // Given
        UUID userId = testUser.getId();
        String newName = "Updated Name";
        String newEmail = "new@example.com";

        when(userRepository.findByIdAndStatusNot(userId, "DELETED"))
                .thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        User result = userService.updateProfile(userId, newName, newEmail);

        // Then
        assertThat(result.getName()).isEqualTo(newName);
        assertThat(result.getEmail()).isEqualTo(newEmail);
    }
}
```

### 2.3 Integration Test Example

```java
// UserServiceIntegrationTest.java
package com.aicafe.user.service;

import com.aicafe.user.entity.User;
import com.aicafe.user.repository.UserRepository;
import com.aicafe.user.dto.RegisterRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
@DisplayName("User Service Integration Tests")
class UserServiceIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("aicafe_test")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", "create-drop");
    }

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        
        registerRequest = RegisterRequest.builder()
                .phone("+84912345678")
                .name("Integration Test User")
                .build();
    }

    @Test
    @DisplayName("Should persist user to database")
    void createUser_PersistsToDatabase() {
        // When
        User result = userService.createUser(registerRequest);
        
        // Then
        assertThat(userRepository.findById(result.getId())).isPresent();
        assertThat(userRepository.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should find user by phone after creation")
    void findByPhone_AfterCreation() {
        // Given
        userService.createUser(registerRequest);

        // When
        Optional<User> result = userService.findByPhone("+84912345678");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getPhone()).isEqualTo("+84912345678");
    }
}
```

### 2.4 Controller Test Example

```java
// UserControllerTest.java
package com.aicafe.user.controller;

import com.aicafe.user.dto.UserResponse;
import com.aicafe.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@DisplayName("User Controller Tests")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    @DisplayName("Should return user profile when authenticated")
    @WithMockUser(username = "user@test.com")
    void getCurrentUser_ReturnsUser() throws Exception {
        // Given
        UserResponse response = UserResponse.builder()
                .id(UUID.randomUUID())
                .phone("+84912345678")
                .name("Test User")
                .tier("BASIC")
                .build();

        when(userService.getCurrentUser()).thenReturn(response);

        // When/Then
        mockMvc.perform(get("/api/v1/users/me")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.phone").value("+84912345678"))
                .andExpect(jsonPath("$.data.name").value("Test User"));
    }

    @Test
    @DisplayName("Should return 401 when not authenticated")
    void getCurrentUser_NotAuthenticated_Returns401() throws Exception {
        mockMvc.perform(get("/api/v1/users/me")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should update user profile")
    @WithMockUser(username = "user@test.com")
    void updateProfile_Success() throws Exception {
        // Given
        UserResponse response = UserResponse.builder()
                .id(UUID.randomUUID())
                .phone("+84912345678")
                .name("Updated Name")
                .email("new@test.com")
                .build();

        when(userService.updateProfile(any())).thenReturn(response);

        // When/Then
        mockMvc.perform(patch("/api/v1/users/me")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(response)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Updated Name"));
    }
}
```

---

## 3. Go Services Testing

### 3.1 Dependencies

```go
// go.mod
require (
    github.com/stretchr/testify v1.8.4
    github.com/DATA-DOG/go-sqlmock v1.5.0
    github.com/go-faker/faker/v4 v4.1.0
    github.com/alicebob/miniredis/v2 v2.30.0
)
```

### 3.2 Unit Test Example

```go
// wallet_test.go
package credit_test

import (
    "context"
    "testing"
    "time"

    "github.com/aicafe/credit-service/internal/service"
    "github.com/aicafe/credit-service/internal/repository"
    "github.com/aicafe/credit-service/pkg/model"
    "github.com/stretchr/testify/assert"
    "github.com/stretchr/testify/mock"
)

// MockWalletRepository implements repository.WalletRepository
type MockWalletRepository struct {
    mock.Mock
}

func (m *MockWalletRepository) GetByUserID(ctx context.Context, userID string) (*model.Wallet, error) {
    args := m.Called(ctx, userID)
    if args.Get(0) == nil {
        return nil, args.Error(1)
    }
    return args.Get(0).(*model.Wallet), args.Error(1)
}

func (m *MockWalletRepository) Update(ctx context.Context, wallet *model.Wallet) error {
    args := m.Called(ctx, wallet)
    return args.Error(0)
}

func (m *MockWalletRepository) Create(ctx context.Context, wallet *model.Wallet) error {
    args := m.Called(ctx, wallet)
    return args.Error(0)
}

func TestWalletService_GetBalance(t *testing.T) {
    t.Run("Should return balance for existing user", func(t *testing.T) {
        // Arrange
        mockRepo := new(MockWalletRepository)
        walletService := service.NewWalletService(mockRepo)
        
        expectedWallet := &model.Wallet{
            ID:         "wallet-123",
            UserID:     "user-456",
            Credits:    1500,
            Minutes:    45,
            Version:    1,
            UpdatedAt:  time.Now(),
        }
        
        mockRepo.On("GetByUserID", mock.Anything, "user-456").
            Return(expectedWallet, nil)
        
        // Act
        result, err := walletService.GetBalance(context.Background(), "user-456")
        
        // Assert
        assert.NoError(t, err)
        assert.Equal(t, int64(1500), result.Credits)
        assert.Equal(t, int64(45), result.Minutes)
        mockRepo.AssertExpectations(t)
    })

    t.Run("Should return error for non-existing user", func(t *testing.T) {
        // Arrange
        mockRepo := new(MockWalletRepository)
        walletService := service.NewWalletService(mockRepo)
        
        mockRepo.On("GetByUserID", mock.Anything, "non-existing").
            Return(nil, repository.ErrWalletNotFound)
        
        // Act
        result, err := walletService.GetBalance(context.Background(), "non-existing")
        
        // Assert
        assert.Error(t, err)
        assert.Equal(t, service.ErrUserNotFound, err)
        assert.Nil(t, result)
    })
}

func TestWalletService_DeductCredits(t *testing.T) {
    t.Run("Should deduct credits successfully", func(t *testing.T) {
        // Arrange
        mockRepo := new(MockWalletRepository)
        walletService := service.NewWalletService(mockRepo)
        
        currentWallet := &model.Wallet{
            ID:      "wallet-123",
            UserID:  "user-456",
            Credits: 1500,
            Version: 1,
        }
        
        updatedWallet := &model.Wallet{
            ID:      "wallet-123",
            UserID:  "user-456",
            Credits: 1000, // 500 deducted
            Version: 2,
        }
        
        mockRepo.On("GetByUserID", mock.Anything, "user-456").
            Return(currentWallet, nil)
        mockRepo.On("Update", mock.Anything, mock.AnythingOfType("*model.Wallet")).
            Return(nil).Run(func(args mock.Arguments) {
                // Verify the updated values
                wallet := args.Get(1).(*model.Wallet)
                assert.Equal(t, int64(1000), wallet.Credits)
            })
        
        // Act
        err := walletService.DeductCredits(context.Background(), "user-456", 500, "TEST")
        
        // Assert
        assert.NoError(t, err)
        mockRepo.AssertExpectations(t)
    })

    t.Run("Should fail when insufficient credits", func(t *testing.T) {
        // Arrange
        mockRepo := new(MockWalletRepository)
        walletService := service.NewWalletService(mockRepo)
        
        currentWallet := &model.Wallet{
            ID:      "wallet-123",
            UserID:  "user-456",
            Credits: 100,
            Version: 1,
        }
        
        mockRepo.On("GetByUserID", mock.Anything, "user-456").
            Return(currentWallet, nil)
        
        // Act
        err := walletService.DeductCredits(context.Background(), "user-456", 500, "TEST")
        
        // Assert
        assert.Error(t, err)
        assert.Equal(t, service.ErrInsufficientCredits, err)
    })
}
```

### 3.3 Integration Test with SQLMock

```go
// wallet_integration_test.go
package credit_test

import (
    "context"
    "database/sql"
    "testing"
    "time"

    "github.com/aicafe/credit-service/internal/repository"
    "github.com/DATA-DOG/go-sqlmock"
    "github.com/stretchr/testify/assert"
    "github.com/stretchr/testify/suite"
)

type WalletRepositorySuite struct {
    suite.Suite
    db   *sql.DB
    mock sqlmock.Sqlmock
    repo repository.WalletRepository
}

func (s *WalletRepositorySuite) SetupSuite() {
    var err error
    s.db, s.mock, err = sqlmock.New()
    assert.NoError(s.T(), err)
    s.repo = repository.NewWalletRepository(s.db)
}

func (s *WalletRepositorySuite) TearDownSuite() {
    s.db.Close()
}

func (s *WalletRepositorySuite) TestGetByUserID() {
    t := s.T()
    
    t.Run("Should return wallet when exists", func(t *testing.T) {
        // Arrange
        rows := sqlmock.NewRows([]string{"id", "user_id", "credits", "minutes", "version", "updated_at"}).
            AddRow("wallet-123", "user-456", 1500, 45, 1, time.Now())
        
        s.mock.ExpectQuery("SELECT .+ FROM wallets WHERE user_id").
            WithArgs("user-456").
            WillReturnRows(rows)
        
        // Act
        wallet, err := s.repo.GetByUserID(context.Background(), "user-456")
        
        // Assert
        assert.NoError(t, err)
        assert.NotNil(t, wallet)
        assert.Equal(t, int64(1500), wallet.Credits)
        assert.NoError(t, s.mock.ExpectationsWereMet())
    })
}

func TestWalletRepositorySuite(t *testing.T) {
    suite.Run(t, new(WalletRepositorySuite))
}
```

### 3.4 Handler Test Example

```go
// handler_test.go
package api_test

import (
    "bytes"
    "encoding/json"
    "net/http"
    "net/http/httptest"
    "testing"

    "github.com/aicafe/credit-service/internal/api"
    "github.com/aicafe/credit-service/internal/service"
    "github.com/stretchr/testify/assert"
    "github.com/stretchr/testify/mock"
)

// MockWalletService implements service.WalletServiceInterface
type MockWalletService struct {
    mock.Mock
}

func (m *MockWalletService) GetBalance(ctx context.Context, userID string) (*service.BalanceResponse, error) {
    args := m.Called(ctx, userID)
    if args.Get(0) == nil {
        return nil, args.Error(1)
    }
    return args.Get(0).(*service.BalanceResponse), args.Error(1)
}

func TestHealthHandler(t *testing.T) {
    t.Run("Should return healthy status", func(t *testing.T) {
        // Arrange
        handler := api.NewHealthHandler()
        
        req, _ := http.NewRequest("GET", "/health", nil)
        rr := httptest.NewRecorder()
        
        // Act
        handler.ServeHTTP(rr, req)
        
        // Assert
        assert.Equal(t, http.StatusOK, rr.Code)
        
        var response map[string]interface{}
        json.Unmarshal(rr.Body.Bytes(), &response)
        assert.Equal(t, "healthy", response["status"])
    })
}

func TestBalanceHandler(t *testing.T) {
    t.Run("Should return balance for authenticated user", func(t *testing.T) {
        // Arrange
        mockService := new(MockWalletService)
        handler := api.NewBalanceHandler(mockService)
        
        expected := &service.BalanceResponse{
            Credits: 1500,
            Minutes: 45,
        }
        mockService.On("GetBalance", mock.Anything, "user-123").
            Return(expected, nil)
        
        // Create request with user context
        req, _ := http.NewRequest("GET", "/api/v1/users/me/credits", nil)
        req = setUserContext(req, "user-123")
        
        rr := httptest.NewRecorder()
        
        // Act
        handler.GetBalance(rr, req)
        
        // Assert
        assert.Equal(t, http.StatusOK, rr.Code)
        
        var response api.Response
        json.Unmarshal(rr.Body.Bytes(), &response)
        assert.True(t, response.Success)
        mockService.AssertExpectations(t)
    })
}

func TestDeductCreditsHandler(t *testing.T) {
    t.Run("Should deduct credits successfully", func(t *testing.T) {
        // Arrange
        mockService := new(MockWalletService)
        handler := api.NewBalanceHandler(mockService)
        
        reqBody := service.DeductRequest{
            Amount: 100,
            Source: "TEST",
        }
        body, _ := json.Marshal(reqBody)
        
        req, _ := http.NewRequest("POST", "/api/v1/credits/deduct", bytes.NewBuffer(body))
        req.Header.Set("Content-Type", "application/json")
        req = setUserContext(req, "user-123")
        
        mockService.On("DeductCredits", mock.Anything, "user-123", int64(100), "TEST").
            Return(nil)
        
        rr := httptest.NewRecorder()
        
        // Act
        handler.DeductCredits(rr, req)
        
        // Assert
        assert.Equal(t, http.StatusOK, rr.Code)
        mockService.AssertExpectations(t)
    })
}
```

---

## 4. API Testing (REST Assured)

### 4.1 API Test Framework

```java
// src/test/java/com/aicafe/api/BaseAPITest.java
package com.aicafe.api;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeAll;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public abstract class BaseAPITest {

    protected static String accessToken;
    protected static String baseUrl = "https://api.aicafe.vn";
    protected static String apiVersion = "/api/v1";

    @BeforeAll
    static void setup() {
        RestAssured.baseURI = baseUrl;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    protected RequestSpecification authRequest() {
        return given()
                .header("Authorization", "Bearer " + accessToken)
                .header("Content-Type", "application/json");
    }

    protected Response sendOTP(String phone) {
        return given()
                .contentType(ContentType.JSON)
                .body("{\"phone\": \"" + phone + "\"}")
                .when()
                .post(apiVersion + "/auth/send-otp");
    }

    protected Response verifyOTP(String phone, String code) {
        return given()
                .contentType(ContentType.JSON)
                .body("{\"phone\": \"" + phone + "\", \"code\": \"" + code + "\"}")
                .when()
                .post(apiVersion + "/auth/verify-otp");
    }

    protected Response getCurrentUser() {
        return authRequest()
                .when()
                .get(apiVersion + "/users/me");
    }

    protected Response getCredits() {
        return authRequest()
                .when()
                .get(apiVersion + "/users/me/credits");
    }
}
```

### 4.2 API Test Examples

```java
// AuthAPITest.java
package com.aicafe.api;

import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Authentication API Tests")
class AuthAPITest extends BaseAPITest {

    @Test
    @DisplayName("Should send OTP successfully")
    void sendOTP_Success() {
        sendOTP("+84912345679")
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("message", containsString("OTP sent"));
    }

    @Test
    @DisplayName("Should verify OTP and return tokens")
    void verifyOTP_Success() {
        // First send OTP
        String phone = "+84912345680";
        sendOTP(phone);
        
        // Simulate receiving OTP (in real test, use test SMS gateway)
        String testCode = "123456";
        
        // Verify OTP
        Response response = verifyOTP(phone, testCode);
        
        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("data.tokens.access_token", notNullValue())
                .body("data.tokens.refresh_token", notNullValue())
                .body("data.user.phone", equalTo(phone));
        
        // Save token for other tests
        accessToken = response.jsonPath().getString("data.tokens.access_token");
    }

    @Test
    @DisplayName("Should return 400 for invalid OTP")
    void verifyOTP_InvalidCode() {
        String phone = "+84912345681";
        sendOTP(phone);
        
        verifyOTP(phone, "000000")
                .then()
                .statusCode(400)
                .body("success", equalTo(false))
                .body("error.code", equalTo("AUTH_OTP_INVALID"));
    }

    @Test
    @DisplayName("Should refresh token successfully")
    void refreshToken_Success() {
        String phone = "+84912345682";
        sendOTP(phone);
        
        Response verifyResponse = verifyOTP(phone, "123456");
        String refreshToken = verifyResponse.jsonPath()
                .getString("data.tokens.refresh_token");
        
        // Refresh token
        given()
                .contentType(ContentType.JSON)
                .body("{\"refresh_token\": \"" + refreshToken + "\"}")
                .when()
                .post(apiVersion + "/auth/refresh")
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("data.access_token", notNullValue());
    }
}

// UserAPITest.java
package com.aicafe.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@DisplayName("User API Tests")
class UserAPITest extends BaseAPITest {

    @Test
    @DisplayName("Should return user profile")
    void getCurrentUser_Success() {
        getCurrentUser()
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("data.id", notNullValue())
                .body("data.phone", notNullValue())
                .body("data.tier", notNullValue());
    }

    @Test
    @DisplayName("Should return 401 without auth token")
    void getCurrentUser_Unauthorized() {
        given()
                .contentType(ContentType.JSON)
                .when()
                .get(baseUrl + apiVersion + "/users/me")
                .then()
                .statusCode(401);
    }

    @Test
    @DisplayName("Should update user profile")
    void updateProfile_Success() {
        String newName = "Updated Test User";
        
        authRequest()
                .body("{\"name\": \"" + newName + "\"}")
                .when()
                .patch(apiVersion + "/users/me")
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("data.name", equalTo(newName));
    }
}

// CreditAPITest.java
package com.aicafe.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@DisplayName("Credit API Tests")
class CreditAPITest extends BaseAPITest {

    @Test
    @DisplayName("Should return credit balance")
    void getCredits_Success() {
        getCredits()
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("data.credits", greaterThanOrEqualTo(0))
                .body("data.workspace_minutes", greaterThanOrEqualTo(0))
                .body("data.daily_usage", notNullValue());
    }

    @Test
    @DisplayName("Should deduct credits on AI request")
    void chatRequest_DeductsCredits() {
        // Get initial balance
        Integer initialCredits = getCredits()
                .jsonPath()
                .getInt("data.credits");
        
        // Send chat request
        String requestBody = """
            {
                "model": "gpt-4o",
                "messages": [
                    {"role": "user", "content": "Hello"}
                ]
            }
            """;
        
        authRequest()
                .body(requestBody)
                .when()
                .post(apiVersion + "/ai/chat")
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("data.credits_used", greaterThan(0))
                .body("data.remaining_balance", equalTo(initialCredits - 
                        getCredits().jsonPath().getInt("data.credits")));
    }
}
```

---

## 5. E2E Testing (Playwright)

### 5.1 Setup

```typescript
// e2e/playwright.config.ts
import { defineConfig, devices } from '@playwright/test';

export default defineConfig({
  testDir: './e2e/tests',
  fullyParallel: true,
  forbidOnly: !!process.env.CI,
  retries: process.env.CI ? 2 : 0,
  workers: process.env.CI ? 1 : undefined,
  reporter: 'html',
  
  use: {
    baseURL: 'https://app.aicafe.vn',
    trace: 'on-first-retry',
    screenshot: 'only-on-failure',
  },

  projects: [
    {
      name: 'chromium',
      use: { ...devices['Desktop Chrome'] },
    },
    {
      name: 'firefox',
      use: { ...devices['Desktop Firefox'] },
    },
    {
      name: 'webkit',
      use: { ...devices['Desktop Safari'] },
    },
    {
      name: 'Mobile Chrome',
      use: { ...devices['Pixel 5'] },
    },
  ],

  webServer: {
    command: 'npm run dev',
    url: 'http://localhost:3000',
    reuseExistingServer: !process.env.CI,
  },
});
```

### 5.2 E2E Test Examples

```typescript
// e2e/tests/auth.spec.ts
import { test, expect } from '@playwright/test';

test.describe('Authentication Flow', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/');
  });

  test('should register and login successfully', async ({ page }) => {
    // Click login button
    await page.click('text=Đăng nhập');
    
    // Enter phone number
    await page.fill('input[name="phone"]', '+84912345678');
    await page.click('text=Gửi mã OTP');
    
    // Wait for OTP input
    await expect(page.locator('input[name="otp"]')).toBeVisible({ timeout: 10000 });
    
    // Enter OTP (use test OTP from mock)
    await page.fill('input[name="otp"]', '123456');
    await page.click('text=Xác minh');
    
    // Should redirect to home and show user menu
    await expect(page.locator('text=Tài khoản')).toBeVisible({ timeout: 10000 });
  });

  test('should show error for invalid OTP', async ({ page }) => {
    await page.click('text=Đăng nhập');
    await page.fill('input[name="phone"]', '+84912345679');
    await page.click('text=Gửi mã OTP');
    
    await page.fill('input[name="otp"]', '000000');
    await page.click('text=Xác minh');
    
    await expect(page.locator('text=Mã OTP không hợp lệ')).toBeVisible();
  });
});

// e2e/tests/chat.spec.ts
import { test, expect } from '@playwright/test';

test.describe('AI Chat', () => {
  test.beforeEach(async ({ page }) => {
    // Login first
    await page.goto('/');
    await page.click('text=Đăng nhập');
    await page.fill('input[name="phone"]', process.env.TEST_PHONE!);
    await page.click('text=Gửi mã OTP');
    await page.fill('input[name="otp"]', process.env.TEST_OTP!);
    await page.click('text=Xác minh');
    await page.waitForURL('**/');
  });

  test('should send message and receive response', async ({ page }) => {
    // Go to chat
    await page.click('text=Trò chuyện AI');
    
    // Select model
    await page.selectOption('select[name="model"]', 'gpt-4o');
    
    // Type message
    const message = 'Xin chào, bạn là ai?';
    await page.fill('textarea[name="message"]', message);
    await page.click('text=Gửi');
    
    // Wait for response
    const response = page.locator('div.message-assistant').last();
    await expect(response).toBeVisible({ timeout: 30000 });
    await expect(response).not.toHaveText('');
    
    // Verify credits were deducted
    await page.click('text=Tài khoản');
    const balance = page.locator('text=/\\d+ Credits/');
    await expect(balance).toBeVisible();
  });

  test('should show streaming response', async ({ page }) => {
    await page.click('text=Trò chuyện AI');
    
    // Send long message for streaming
    await page.fill('textarea[name="message"]', 'Viết một bài văn 500 từ về lập trình');
    await page.click('text=Gửi');
    
    // Check for streaming indicator
    await expect(page.locator('.streaming-indicator')).toBeVisible({ timeout: 5000 });
    
    // Wait for completion
    await expect(page.locator('.streaming-indicator')).not.toBeVisible({ timeout: 60000 });
  });
});

// e2e/tests/purchase.spec.ts
import { test, expect } from '@playwright/test';

test.describe('Credit Purchase', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/');
    // Login
    await page.click('text=Đăng nhập');
    await page.fill('input[name="phone"]', process.env.TEST_PHONE!);
    await page.click('text=Gửi mã OTP');
    await page.fill('input[name="otp"]', process.env.TEST_OTP!);
    await page.click('text=Xác minh');
    await page.waitForURL('**/');
  });

  test('should purchase credits successfully', async ({ page }) => {
    // Go to purchase page
    await page.click('text=Nạp Credits');
    
    // Select package
    await page.click('text=1000 Credits');
    
    // Apply coupon if available
    await page.fill('input[name="coupon"]', 'WELCOME10');
    await page.click('text=Áp dụng');
    await expect(page.locator('text=Mã giảm giá đã được áp dụng')).toBeVisible();
    
    // Proceed to payment
    await page.click('text=Thanh toán');
    
    // Select VNPay
    await page.click('text=VNPay');
    await page.click('text=Xác nhận thanh toán');
    
    // Should redirect to VNPay
    await expect(page).toHaveURL(/vnpay/);
  });
});
```

---

## 6. Test Coverage

### 6.1 Coverage Configuration

```xml
<!-- jacoco-maven-plugin configuration -->
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.11</version>
    <executions>
        <execution>
            <id>prepare-agent</id>
            <goals>
                <goal>prepare-agent</goal>
            </goals>
            <configuration>
                <dataFile>${project.build.directory}/jacoco.exec</dataFile>
            </configuration>
        </execution>
        <execution>
            <id>report</id>
            <phase>test</phase>
            <goals>
                <goal>report</goal>
            </goals>
        </execution>
        <execution>
            <id>check</id>
            <goals>
                <goal>check</goal>
            </goals>
            <configuration>
                <rules>
                    <rule>
                        <element>BUNDLE</element>
                        <limits>
                            <limit>
                                <counter>LINE</counter>
                                <value>COVEREDRATIO</value>
                                <minimum>0.80</minimum>
                            </limit>
                            <limit>
                                <counter>BRANCH</counter>
                                <value>COVEREDRATIO</value>
                                <minimum>0.70</minimum>
                            </limit>
                        </limits>
                    </rule>
                </rules>
            </configuration>
        </execution>
    </executions>
</plugin>
```

### 6.2 Coverage Goals

| Service | Package | Line Coverage | Branch Coverage |
|---------|---------|---------------|-----------------|
| user-service | controller | 90% | 80% |
| user-service | service | 85% | 75% |
| user-service | repository | 80% | 70% |
| credit-service | handler | 90% | 85% |
| credit-service | service | 85% | 80% |
| credit-service | repository | 80% | 70% |
| order-service | controller | 85% | 75% |
| order-service | service | 80% | 70% |

---

## 7. Test Data Management

### 7.1 Test Data Builder Pattern

```java
// TestDataBuilder.java
package com.aicafe.test.builder;

import com.aicafe.user.entity.User;
import com.aicafe.user.dto.RegisterRequest;

public class UserBuilder {
    private UUID id = UUID.randomUUID();
    private String phone = "+84912345678";
    private String name = "Test User";
    private String email = "test@example.com";
    private String tier = "BASIC";
    private String status = "ACTIVE";

    public UserBuilder phone(String phone) {
        this.phone = phone;
        return this;
    }

    public UserBuilder name(String name) {
        this.name = name;
        return this;
    }

    public UserBuilder withBasicTier() {
        this.tier = "BASIC";
        return this;
    }

    public UserBuilder withDeveloperTier() {
        this.tier = "DEVELOPER";
        return this;
    }

    public UserBuilder suspended() {
        this.status = "SUSPENDED";
        return this;
    }

    public User buildEntity() {
        return User.builder()
                .id(id)
                .phone(phone)
                .name(name)
                .email(email)
                .tier(tier)
                .status(status)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public RegisterRequest buildRequest() {
        return RegisterRequest.builder()
                .phone(phone)
                .name(name)
                .email(email)
                .build();
    }
}

// Usage
User user = new UserBuilder()
        .phone("+84987654321")
        .withDeveloperTier()
        .buildEntity();
```

### 7.2 TestContainers Setup

```java
// TestContainersConfig.java
package com.aicafe.test.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration
public class TestContainersConfig {

    @Bean
    public Network network() {
        return Network.newNetwork();
    }

    @Bean
    public PostgreSQLContainer<?> postgresContainer(Network network) {
        PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(DockerImageName.parse("postgres:15"))
                .withDatabaseName("aicafe_test")
                .withUsername("test")
                .withPassword("test")
                .withNetwork(network)
                .withNetworkAliases("postgres");
        
        postgres.start();
        return postgres;
    }

    @Bean
    public GenericContainer<?> redisContainer(Network network) {
        GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:7-alpine"))
                .withNetwork(network)
                .withNetworkAliases("redis")
                .withExposedPorts(6379);
        
        redis.start();
        return redis;
    }
}
```

---

## 8. CI/CD Testing

### 8.1 GitHub Actions

```yaml
# .github/workflows/test.yml
name: Tests

on:
  push:
    branches: [main, develop]
  pull_request:
    branches: [main, develop]

jobs:
  unit-tests:
    name: Unit Tests
    runs-on: ubuntu-latest
    
    services:
      postgres:
        image: postgres:15
        env:
          POSTGRES_DB: aicafe_test
          POSTGRES_USER: test
          POSTGRES_PASSWORD: test
        options: >-
          --health-cmd pg_isready
          --health-interval 10s
          --health-timeout 5s
          --health-retries 5
        ports:
          - 5432:5432
      
      redis:
        image: redis:7-alpine
        options: >-
          --health-cmd "redis-cli ping"
          --health-interval 10s
          --health-timeout 5s
          --health-retries 5
        ports:
          - 6379:6379

    steps:
      - uses: actions/checkout@v4
      
      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'
          cache: 'maven'
      
      - name: Run Unit Tests
        env:
          SPRING_DATASOURCE_URL: jdbc:postgresql://localhost:5432/aicafe_test
          SPRING_DATASOURCE_USERNAME: test
          SPRING_DATASOURCE_PASSWORD: test
          SPRING_REDIS_HOST: localhost
        run: mvn test -Dtest=*UnitTest -DfailIfNoTests=false
      
      - name: Run Integration Tests
        env:
          SPRING_DATASOURCE_URL: jdbc:postgresql://localhost:5432/aicafe_test
          SPRING_REDIS_HOST: localhost
        run: mvn verify -Dtest=*IntegrationTest -DfailIfNoTests=false
      
      - name: Upload coverage to Codecov
        uses: codecov/codecov-action@v3
        with:
          files: '**/target/site/jacoco/jacoco.xml'
          fail_ci_if_error: false

  api-tests:
    name: API Tests
    runs-on: ubuntu-latest
    needs: unit-tests
    
    steps:
      - uses: actions/checkout@v4
      
      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'
          cache: 'maven'
      
      - name: Build application
        run: mvn package -DskipTests
      
      - name: Start application
        run: java -jar target/*.jar &
      
      - name: Run API Tests
        run: mvn test -Dtest=*APITest
      
      - name: Stop application
        run: pkill -f 'java.*\.jar'

  e2e-tests:
    name: E2E Tests
    runs-on: ubuntu-latest
    needs: api-tests
    
    steps:
      - uses: actions/checkout@v4
      
      - name: Set up Node.js
        uses: actions/setup-node@v4
        with:
          node-version: '20'
          cache: 'npm'
      
      - name: Install dependencies
        run: npm ci
      
      - name: Install Playwright browsers
        run: npx playwright install --with-deps chromium
      
      - name: Run E2E tests
        env:
          TEST_PHONE: ${{ secrets.TEST_PHONE }}
          TEST_OTP: ${{ secrets.TEST_OTP }}
        run: npx playwright test
      
      - name: Upload test results
        uses: actions/upload-artifact@v4
        if: failure()
        with:
          name: playwright-report
          path: playwright-report/
```

### 8.2 Maven Test Configuration

```xml
<!-- pom.xml test configuration -->
<profiles>
    <profile>
        <id>unit-test</id>
        <activation>
            <activeByDefault>true</activeByDefault>
        </activation>
        <properties>
            <testIncludes>**/*UnitTest.java</testIncludes>
            <testExcludes>**/*IntegrationTest.java,**/*APITest.java</testExcludes>
        </properties>
    </profile>
    
    <profile>
        <id>integration-test</id>
        <properties>
            <testIncludes>**/*IntegrationTest.java</testIncludes>
            <testExcludes>**/*UnitTest.java,**/*APITest.java</testExcludes>
        </properties>
        <dependencies>
            <dependency>
                <groupId>org.testcontainers</groupId>
                <artifactId>postgresql</artifactId>
                <scope>test</scope>
            </dependency>
        </dependencies>
    </profile>
</profiles>

<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-surefire-plugin</artifactId>
            <version>3.2.2</version>
            <configuration>
                <includes>
                    <include>${testIncludes}</include>
                </includes>
                <excludes>
                    <exclude>${testExcludes}</exclude>
                </excludes>
                <parallel>methods</parallel>
                <threadCount>4</threadCount>
                <forkCount>1C</forkCount>
                <reuseForks>true</reuseForks>
                <argLine>
                    -Xmx1024m
                    -XX:+UseG1GC
                    -XX:MaxGCPauseMillis=200
                </argLine>
            </configuration>
        </plugin>
    </plugins>
</build>
```
