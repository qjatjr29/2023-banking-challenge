package numble.banking.core.user.presentation;

import static numble.banking.support.ApiDocumentUtils.getDocumentRequest;
import static numble.banking.support.ApiDocumentUtils.getDocumentResponse;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.stream.Stream;
import numble.banking.core.common.error.ErrorCode;
import numble.banking.core.user.command.application.SignupRequest;
import numble.banking.core.user.command.domain.UserRepository;
import numble.banking.support.controller.BaseControllerTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

@DisplayName("유저 API 테스트")
class UserControllerTest extends BaseControllerTest {

  @Autowired
  UserRepository userRepository;

  String loginId;
  String password;
  String email;
  String phone;

  @BeforeEach
  void setup() {
    loginId = "beomsic";
    password = "beomsic12!";
    email = "beomsic@gmail.com";
    phone = "010-0000-0000";
  }

  @Test
  @DisplayName("회원가입 성공 테스트")
  void signup_success() throws Exception {
    // Given
    SignupRequest signUpRequest = SignupRequest.builder()
        .loginId(loginId)
        .password(password)
        .name("beomsic")
        .email(email)
        .phone(phone)
        .zipCode("03333")
        .address("서울시")
        .roadAddress("서울시")
        .build();

    // When
    ResultActions result = mockMvc.perform(post("/users")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(signUpRequest)));

    // Then
    result.andExpect(status().isCreated())
        .andExpect(jsonPath("email").value(email))
        .andExpect(jsonPath("phone").value(phone))
        .andDo(document("유저 - 회원가입 성공 API",
            getDocumentRequest(),
            getDocumentResponse(),
            requestFields(
                fieldWithPath("loginId").type(JsonFieldType.STRING).description("로그인 아이디"),
                fieldWithPath("password").type(JsonFieldType.STRING).description("패스워드"),
                fieldWithPath("name").type(JsonFieldType.STRING).description("이름"),
                fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
                fieldWithPath("phone").type(JsonFieldType.STRING).description("휴대폰 번호"),
                fieldWithPath("zipCode").type(JsonFieldType.STRING).description("우편번호").optional(),
                fieldWithPath("address").type(JsonFieldType.STRING).description("주소").optional(),
                fieldWithPath("roadAddress").type(JsonFieldType.STRING).description("도로명주소").optional()
            ),
            responseFields(
                fieldWithPath("id").type(JsonFieldType.NUMBER).description("유저 아이디"),
                fieldWithPath("name").type(JsonFieldType.STRING).description("이름"),
                fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
                fieldWithPath("phone").type(JsonFieldType.STRING).description("휴대폰 번호"),
                fieldWithPath("address").type(JsonFieldType.OBJECT).description("주소 정보"),
                fieldWithPath("address.zipCode").type(JsonFieldType.STRING).description("우편번호"),
                fieldWithPath("address.address").type(JsonFieldType.STRING).description("주소"),
                fieldWithPath("address.roadAddress").type(JsonFieldType.STRING).description("도로명주소")
            )
        ));
  }

  @ParameterizedTest(name = "empty field: {0}")
  @MethodSource("signupFailRequests")
  @DisplayName("SignupRequest dto 에 잘못된 값들이 들어온 경우")
  void signupInvalidValueExceptionThrown(String field, SignupRequest signRequest) throws Exception {

    // Given
    // When
    ResultActions result = mockMvc.perform(post("/users")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(signRequest)));

    // Then
    result.andExpect(status().isBadRequest())
        .andExpect(jsonPath("resultCode").value(ErrorCode.INVALID_INPUT_VALUE.getResultCode()))
        .andExpect(jsonPath("statusCode").value(400))
        .andExpect(jsonPath("errors[0].field").value(field))
        .andDo(
            document("유저 - 회원가입 실패(request dto - " + field + " X)",
                getDocumentRequest(),
                getDocumentResponse(),
                requestFields(
                    fieldWithPath("loginId").type(JsonFieldType.STRING).description("로그인 아이디"),
                    fieldWithPath("password").type(JsonFieldType.STRING).description("패스워드"),
                    fieldWithPath("name").type(JsonFieldType.STRING).description("이름"),
                    fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
                    fieldWithPath("phone").type(JsonFieldType.STRING).description("휴대폰 번호"),
                    fieldWithPath("zipCode").type(JsonFieldType.STRING).description("우편번호").optional(),
                    fieldWithPath("address").type(JsonFieldType.STRING).description("주소").optional(),
                    fieldWithPath("roadAddress").type(JsonFieldType.STRING).description("도로명주소").optional()
                ),
                getErrorResponseField()
            )
        );
  }

  static Stream<Arguments> signupFailRequests() {
    return Stream.of(

        Arguments.of("loginId", new SignupRequest("", "password12!", "beomsic", "010-0000-0000", "test@gmail.com", "08324", "서울시", "서울시")),
        Arguments.of("password", new SignupRequest("testid", "", "beomsic", "010-0000-0000", "test@gmail.com", "08324", "서울시", "서울시")),
        Arguments.of("name", new SignupRequest("testid", "password12!", "", "010-0000-0000", "test@gmail.com", "08324", "서울시", "서울시")),
        Arguments.of("phone", new SignupRequest("testid", "password12!", "beomsic", "", "test@gmail.com", "08324", "서울시", "서울시")),
        Arguments.of("email", new SignupRequest("testid", "password12!", "beomsic", "010-0000-0000", "", "08324", "서울시", "서울시"))
         );
  }

  private org.springframework.restdocs.payload.ResponseFieldsSnippet getErrorResponseField() {
    return responseFields(
        fieldWithPath("resultCode").type(JsonFieldType.STRING).description("결과 코드"),
        fieldWithPath("message").type(JsonFieldType.STRING).description("에러 메세지"),
        fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("HTTP 상태코드"),
        fieldWithPath("errors").type(JsonFieldType.ARRAY).description("에러 상세 정보"),
        fieldWithPath("errors.[].field").type(JsonFieldType.STRING).description("에러 발생 필드"),
        fieldWithPath("errors.[].value").type(JsonFieldType.STRING).description("값"),
        fieldWithPath("errors.[].errorMessage").type(JsonFieldType.STRING).description("에러 발생 이유")
    );
  }


}