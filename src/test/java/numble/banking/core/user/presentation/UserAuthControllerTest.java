package numble.banking.core.user.presentation;

import static numble.banking.support.ApiDocumentUtils.getDocumentRequest;
import static numble.banking.support.ApiDocumentUtils.getDocumentResponse;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import numble.banking.core.token.JwtTokenProvider;
import numble.banking.core.token.TokenData;
import numble.banking.core.user.command.application.LoginRequest;
import numble.banking.core.user.command.application.TokenReissueRequest;
import numble.banking.core.user.command.application.UserAuthService;
import numble.banking.core.user.command.domain.User;
import numble.banking.core.user.command.domain.UserRepository;
import numble.banking.support.controller.BaseControllerTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

@DisplayName("유저 Auth 컨트롤러 테스트")
class UserAuthControllerTest extends BaseControllerTest {

  static final Logger log = LoggerFactory.getLogger(UserAuthControllerTest.class);

  @Autowired
  UserAuthService userAuthService;

  @Autowired
  UserRepository userRepository;

  @Autowired
  JwtTokenProvider jwtTokenProvider;

  @Autowired
  RedisTemplate<String, Object> redisTemplate;

  static final String REFRESH_TOKEN_PREFIX = "REFRESH_TOKEN-";
  String loginId;
  String password;
  String email;
  String phone;

  @BeforeEach
  void setup() {
    loginId = "beomsic";
    password = "password12!";
    email = "test@gmail.com";
    phone = "010-0000-0000";
  }

  @Nested
  @DisplayName("로그인 테스트")
  class Login {

    @Test
    @DisplayName("로그인 성공 테스트")
    void loginSuccess() throws Exception {
      // given
      LoginRequest loginRequest = new LoginRequest(loginId, password);
      generateUser(loginId, password, email, phone);

      // when
      ResultActions result = mockMvc.perform(post("/authentication/login")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(loginRequest)));

      // then
      result.andExpect(status().isOk())
          .andDo(document("유저 - 로그인 성공",
              getDocumentRequest(),
              getDocumentResponse(),
              requestFields(
                  fieldWithPath("loginId").type(JsonFieldType.STRING).description("로그인 아이디"),
                  fieldWithPath("password").type(JsonFieldType.STRING).description("패스워드")
              ),
              responseFields(
                  fieldWithPath("accessToken").type(JsonFieldType.STRING).description("엑세스 토큰"),
                  fieldWithPath("refreshToken").type(JsonFieldType.STRING).description("리프레시 토큰")
              )
          ));
    }

    @Nested
    @DisplayName("로그인 실패 테스트")
    class LoginFail {

      @Test
      @DisplayName("아이디가 틀린 경우")
      void loginMismatchIdExceptionThrown() throws Exception {

        // given
        LoginRequest loginRequest = new LoginRequest("otherid", password);
        generateUser(loginId, password, email, password);

        // when
        ResultActions result = mockMvc.perform(post("/authentication/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(loginRequest)));

        // then
        result.andExpect(status().isNotFound())
            .andDo(document("유저 - 로그인 실패(없는 아이디)",
                getDocumentRequest(),
                getDocumentResponse(),
                requestFields(
                    fieldWithPath("loginId").type(JsonFieldType.STRING).description("로그인 아이디"),
                    fieldWithPath("password").type(JsonFieldType.STRING).description("패스워드")
                ),
                getErrorResponseField()
            ));

      }

      @Test
      @DisplayName("비밀번호가 틀린 경우")
      void loginMismatchPasswordExceptionThrown() throws Exception {

        // given
        LoginRequest loginRequest = new LoginRequest(loginId, "wrongpw12!");
        generateUser(loginId, password, email, password);

        // when
        ResultActions result = mockMvc.perform(post("/authentication/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(loginRequest)));

        // then
        result.andExpect(status().isBadRequest())
            .andDo(document("유저 - 로그인 실패(비밀번호 틀린경우)",
                getDocumentRequest(),
                getDocumentResponse(),
                requestFields(
                    fieldWithPath("loginId").type(JsonFieldType.STRING).description("로그인 아이디"),
                    fieldWithPath("password").type(JsonFieldType.STRING).description("패스워드")
                ),
                getErrorResponseField()
            ));

      }
    }
  }

  @Nested
  @DisplayName("토큰 갱신 테스트")
  class Reissue {

    User user;
    String accessToken;
    String refreshToken;

    @BeforeEach
    void setup() {
      user = generateUser(loginId, password, email, password);
      TokenData tokenData = TokenData.of(user);

      accessToken = jwtTokenProvider.generateAccessToken(tokenData);
      refreshToken = jwtTokenProvider.generateRefreshToken(tokenData);
      redisTemplate.opsForValue()
          .set(REFRESH_TOKEN_PREFIX + user.getId(), refreshToken);
    }

    @AfterEach
    void clean() {
      redisTemplate.delete(REFRESH_TOKEN_PREFIX + user.getId());
    }

    @Test
    @DisplayName("토큰 재발급 성공 테스트")
    void reissueSuccess() throws Exception {
      // given
      TokenReissueRequest reissueRequest = new TokenReissueRequest(accessToken, refreshToken);

      // when
      ResultActions result = mockMvc.perform(post("/authentication/reissue")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(reissueRequest)));

      // then
      result.andExpect(status().isOk())
          .andDo(document("토큰 - 토큰 재발급 성공",
              getDocumentRequest(),
              getDocumentResponse(),
              requestFields(
                  fieldWithPath("accessToken").type(JsonFieldType.STRING).description("기존 엑세스 토큰"),
                  fieldWithPath("refreshToken").type(JsonFieldType.STRING).description("기존 리프레시 토큰")
              ),
              responseFields(
                  fieldWithPath("accessToken").type(JsonFieldType.STRING).description("새로운 엑세스 토큰"),
                  fieldWithPath("refreshToken").type(JsonFieldType.STRING).description("새로운 리프레시 토큰")
              )
          ));
    }

    @Nested
    @DisplayName("토큰 재발급 실패 테스트")
    class ReissueFail {

      @Test
      @DisplayName("엑세스 토큰이 잘못 입력된 경우")
      void reissueInvalidAccessTokenExceptionThrown() throws Exception {

        // given
        User other = generateUser("other", password, "other@gmail.com", "010-1111-1111");
        String otherToken = jwtTokenProvider.generateAccessToken(TokenData.of(other));

        TokenReissueRequest reissueRequest = new TokenReissueRequest(otherToken, refreshToken);
        // when
        ResultActions result = mockMvc.perform(post("/authentication/reissue")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(reissueRequest)));

        // then
        result.andExpect(status().isForbidden())
            .andDo(document("토큰 - 재발급 실패(accesstoken 잘못 입력한 경우)",
                getDocumentRequest(),
                getDocumentResponse(),
                requestFields(
                    fieldWithPath("accessToken").type(JsonFieldType.STRING).description("기존 엑세스 토큰"),
                    fieldWithPath("refreshToken").type(JsonFieldType.STRING).description("기존 리프레시 토큰")
                ),
                getErrorResponseField()
            ));
      }

      @Test
      @DisplayName("리프레시 토큰이 잘못된 경우")
      void reissueInvalidRefreshTokenExceptionThrown() throws Exception {

        // given
        User other = generateUser("other", password, "other@gmail.com", "010-1111-1111");
        String otherToken = jwtTokenProvider.generateRefreshToken(TokenData.of(other));
        TokenReissueRequest reissueRequest = new TokenReissueRequest(accessToken, otherToken);

        // when
        ResultActions result = mockMvc.perform(post("/authentication/reissue")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(reissueRequest)));

        // then
        result.andExpect(status().isForbidden())
            .andDo(document("토큰 - 재발급 실패(refreshtoken 잘못 입력한 경우)",
                getDocumentRequest(),
                getDocumentResponse(),
                requestFields(
                    fieldWithPath("accessToken").type(JsonFieldType.STRING).description("기존 엑세스 토큰"),
                    fieldWithPath("refreshToken").type(JsonFieldType.STRING).description("기존 리프레시 토큰")
                ),
                getErrorResponseField()
            ));
      }

      @Test
      @DisplayName("리프레시 토큰이 만료된 경우")
      void reissueExpiredRefreshTokenExceptionThrown() throws Exception {

        // given
        TokenReissueRequest reissueRequest = new TokenReissueRequest(accessToken, refreshToken);
        sleep(5000);

        // when
        ResultActions result = mockMvc.perform(post("/authentication/reissue")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(reissueRequest)));

        // then
        result.andExpect(status().isForbidden())
            .andDo(document("토큰 - 재발급 실패(refreshtoken가 만료된 경우)",
                getDocumentRequest(),
                getDocumentResponse(),
                requestFields(
                    fieldWithPath("accessToken").type(JsonFieldType.STRING).description("기존 엑세스 토큰"),
                    fieldWithPath("refreshToken").type(JsonFieldType.STRING).description("기존 리프레시 토큰")
                ),
                getErrorResponseField()
            ));
      }

    }
  }

  private org.springframework.restdocs.payload.ResponseFieldsSnippet getErrorResponseField() {
    return responseFields(
        fieldWithPath("resultCode").type(JsonFieldType.STRING).description("결과 코드"),
        fieldWithPath("message").type(JsonFieldType.STRING).description("에러 메세지"),
        fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("HTTP 상태코드"),
        fieldWithPath("errors").type(JsonFieldType.ARRAY).description("에러 상세 정보")
    );
  }


  private User generateUser(String loginId, String password, String email, String phone) {

    User user = User.builder()
        .loginId(loginId)
        .password(password)
        .name("test")
        .email(email)
        .phone(phone)
        .build();

    user.encryptPassword();

    return userRepository.save(user);
  }

  private void sleep(int millis) {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException e) {
      log.error(e.getMessage(), e);
    }
  }

}