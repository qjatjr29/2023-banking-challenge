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
import numble.banking.core.user.command.application.LoginRequest;
import numble.banking.core.user.command.application.UserAuthService;
import numble.banking.core.user.command.domain.User;
import numble.banking.core.user.command.domain.UserRepository;
import numble.banking.support.controller.BaseControllerTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

@DisplayName("유저 Auth 컨트롤러 테스트")
class UserAuthControllerTest extends BaseControllerTest {

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

  @BeforeEach
  void setup() {
    loginId = "beomsic";
    password = "password12!";
    email = "test@gmail.com";
  }

  @Nested
  @DisplayName("로그인 테스트")
  class Login {

    @Test
    @DisplayName("로그인 성공 테스트")
    void login_success() throws Exception {
      // given
      LoginRequest loginRequest = new LoginRequest(loginId, password);
      generateUser(loginId, password, email);

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
      void login_mismatchId_ExceptionThrown() throws Exception {

        // given
        LoginRequest loginRequest = new LoginRequest("otherid", password);
        generateUser(loginId, password, email);

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
      void login_mismatchPassword_ExceptionThrown() throws Exception {

        // given
        LoginRequest loginRequest = new LoginRequest(loginId, "wrongpw12!");
        generateUser(loginId, password, email);

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

  private org.springframework.restdocs.payload.ResponseFieldsSnippet getErrorResponseField() {
    return responseFields(
        fieldWithPath("resultCode").type(JsonFieldType.STRING).description("결과 코드"),
        fieldWithPath("message").type(JsonFieldType.STRING).description("에러 메세지"),
        fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("HTTP 상태코드"),
        fieldWithPath("errors").type(JsonFieldType.ARRAY).description("에러 상세 정보")
    );
  }


  private User generateUser(String loginId, String password, String email) {

    User user = User.builder()
        .loginId(loginId)
        .password(password)
        .name("test")
        .email(email)
        .phone("010-0000-0000")
        .build();

    user.encryptPassword();

    userRepository.save(user);
    return user;
  }

}