package numble.banking.core.user.presentation;

import static numble.banking.support.ApiDocumentUtils.getDocumentRequest;
import static numble.banking.support.ApiDocumentUtils.getDocumentResponse;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.stream.Stream;
import numble.banking.core.common.error.ErrorCode;
import numble.banking.core.common.error.exception.NotFoundException;
import numble.banking.core.token.JwtTokenProvider;
import numble.banking.core.token.TokenData;
import numble.banking.core.user.command.application.SignupRequest;
import numble.banking.core.user.command.domain.Address;
import numble.banking.core.user.command.domain.Role;
import numble.banking.core.user.command.domain.User;
import numble.banking.core.user.command.domain.UserRepository;
import numble.banking.support.controller.BaseControllerTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@DisplayName("유저 API 테스트")
class UserControllerTest extends BaseControllerTest {

  static final String AUTHORIZATION_HEADER = "Authorization";

  @Autowired
  UserRepository userRepository;

  @Autowired
  JwtTokenProvider jwtTokenProvider;

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
  void signupSuccess() throws Exception {
    // given
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

    // when
    ResultActions result = mockMvc.perform(post("/users")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(signUpRequest)));

    // then
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
                fieldWithPath("address.zipCode").type(JsonFieldType.STRING).description("우편번호").optional(),
                fieldWithPath("address.address").type(JsonFieldType.STRING).description("주소").optional(),
                fieldWithPath("address.roadAddress").type(JsonFieldType.STRING).description("도로명주소").optional()
            )
        ));
  }

  @ParameterizedTest(name = "empty field: {0}")
  @MethodSource("signupFailRequests")
  @DisplayName("SignupRequest dto 에 잘못된 값들이 들어온 경우")
  void signupInvalidValueExceptionThrown(String field, SignupRequest signRequest) throws Exception {

    // given
    // when
    ResultActions result = mockMvc.perform(post("/users")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(signRequest)));

    // then
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

  @Test
  @DisplayName("내정보 조회 테스트")
  void getUserInfo() throws Exception {

    // given
    User user = generateUser(loginId, password, email, phone);
    TokenData tokenData = TokenData.of(user);
    String accessToken = jwtTokenProvider.generateAccessToken(tokenData);

    // when
    ResultActions result = mockMvc.perform(get("/users/me")
        .contentType(MediaType.APPLICATION_JSON)
        .header(AUTHORIZATION_HEADER, accessToken));

    // then
    result.andExpect(status().isOk())
        .andExpect(jsonPath("email").value(email))
        .andExpect(jsonPath("phone").value(phone))
        .andDo(document("유저 - 내 정보 조회 성공",
            getDocumentRequest(),
            getDocumentResponse(),
            responseFields(
                fieldWithPath("loginId").type(JsonFieldType.STRING).description("유저 로그인 아이디"),
                fieldWithPath("name").type(JsonFieldType.STRING).description("이름"),
                fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
                fieldWithPath("phone").type(JsonFieldType.STRING).description("휴대폰 번호"),
                fieldWithPath("address").type(JsonFieldType.OBJECT).description("주소 정보"),
                fieldWithPath("address.zipCode").type(JsonFieldType.STRING).description("우편번호").optional(),
                fieldWithPath("address.address").type(JsonFieldType.STRING).description("주소").optional(),
                fieldWithPath("address.roadAddress").type(JsonFieldType.STRING).description("도로명주소").optional()
            )
        ));

  }

  @Test
  @DisplayName("유저 전체 조회 테스트")
  void getUserSummaryInfo() throws Exception {

    // given
    User user = generateUser(loginId, password, email, phone);
    for(int i = 0; i < 10; i++) {
      generateUser(loginId + i, password + i, email + i, phone + i);
    }
    TokenData tokenData = TokenData.of(user);
    String accessToken = jwtTokenProvider.generateAccessToken(tokenData);

    // when
    ResultActions result = mockMvc.perform(get("/users")
        .contentType(MediaType.APPLICATION_JSON)
        .header(AUTHORIZATION_HEADER, accessToken));

    // then
    result.andExpect(status().isOk())
        .andExpect(jsonPath("totalElements").value(10))
        .andDo(document("유저 - 유저 전체 조회 성공",
            getDocumentRequest(),
            getDocumentResponse(),
            responseFields(
                fieldWithPath("content").type(JsonFieldType.ARRAY).description("검색 결과 리스트"),
                fieldWithPath("content.[].id").type(JsonFieldType.NUMBER).description("유저 아이디"),
                fieldWithPath("content.[].name").type(JsonFieldType.STRING).description("이름"),
                fieldWithPath("content.[].email").type(JsonFieldType.STRING).description("이메일"),
                fieldWithPath("content.[].phone").type(JsonFieldType.STRING).description("휴대폰 번호"),
                fieldWithPath("content.[].isFriend").type(JsonFieldType.BOOLEAN).description("친구 여부"),

                fieldWithPath("pageable").type(JsonFieldType.OBJECT).description("페이징 정보"),
                fieldWithPath("pageable.sort").type(JsonFieldType.OBJECT).description("페이지 정렬 정보"),
                fieldWithPath("pageable.sort.empty").type(JsonFieldType.BOOLEAN).description("정렬 empty"),
                fieldWithPath("pageable.sort.unsorted").type(JsonFieldType.BOOLEAN).description("정렬 X 여부"),
                fieldWithPath("pageable.sort.sorted").type(JsonFieldType.BOOLEAN).description("정렬 O 여부"),

                fieldWithPath("pageable.pageNumber").type(JsonFieldType.NUMBER).description("페이지 번호"),
                fieldWithPath("pageable.pageSize").type(JsonFieldType.NUMBER).description("한 페이지에 나오는 원소 수"),
                fieldWithPath("pageable.offset").type(JsonFieldType.NUMBER).description(""),
                fieldWithPath("pageable.unpaged").type(JsonFieldType.BOOLEAN).description(""),
                fieldWithPath("pageable.paged").type(JsonFieldType.BOOLEAN).description(""),

                fieldWithPath("totalPages").type(JsonFieldType.NUMBER).description("전체 페이지 수"),
                fieldWithPath("totalElements").type(JsonFieldType.NUMBER).description("전체 검색 갯수"),
                fieldWithPath("last").type(JsonFieldType.BOOLEAN).description(""),
                fieldWithPath("size").type(JsonFieldType.NUMBER).description("한 페이지 사이즈"),
                fieldWithPath("number").type(JsonFieldType.NUMBER).description(""),
                fieldWithPath("sort.empty").type(JsonFieldType.BOOLEAN).description("정렬 empty"),
                fieldWithPath("sort.unsorted").type(JsonFieldType.BOOLEAN).description("정렬 X 여부"),
                fieldWithPath("sort.sorted").type(JsonFieldType.BOOLEAN).description("정렬 O 여부"),
                fieldWithPath("first").type(JsonFieldType.BOOLEAN).description(""),
                fieldWithPath("empty").type(JsonFieldType.BOOLEAN).description(""),
                fieldWithPath("numberOfElements").type(JsonFieldType.NUMBER).description("한 페이지의 원소 수")
            )
        ));

  }

  @Test
  @DisplayName("이름으로 유저 조회 테스트")
  void getUserSummaryByNameInfo() throws Exception {

    // given
    User user = generateUser(loginId, password, email, phone);
    for(int i = 0; i < 10; i++) {
      generateUser(loginId + i, password + i, email + i, phone + i);
    }
    TokenData tokenData = TokenData.of(user);
    String accessToken = jwtTokenProvider.generateAccessToken(tokenData);

    // when
    ResultActions result = mockMvc.perform(get("/users/name")
        .contentType(MediaType.APPLICATION_JSON)
        .param("name", "test")
        .header(AUTHORIZATION_HEADER, accessToken));

    // then
    result.andExpect(status().isOk())
        .andExpect(jsonPath("totalElements").value(10))
        .andDo(document("유저 - 이름으로 조회 성공",
            getDocumentRequest(),
            getDocumentResponse(),
            responseFields(
                fieldWithPath("content").type(JsonFieldType.ARRAY).description("검색 결과 리스트"),
                fieldWithPath("content.[].id").type(JsonFieldType.NUMBER).description("유저 아이디"),
                fieldWithPath("content.[].name").type(JsonFieldType.STRING).description("이름"),
                fieldWithPath("content.[].email").type(JsonFieldType.STRING).description("이메일"),
                fieldWithPath("content.[].phone").type(JsonFieldType.STRING).description("휴대폰 번호"),
                fieldWithPath("content.[].isFriend").type(JsonFieldType.BOOLEAN).description("친구 여부"),

                fieldWithPath("pageable").type(JsonFieldType.OBJECT).description("페이징 정보"),
                fieldWithPath("pageable.sort").type(JsonFieldType.OBJECT).description("페이지 정렬 정보"),
                fieldWithPath("pageable.sort.empty").type(JsonFieldType.BOOLEAN).description("정렬 empty"),
                fieldWithPath("pageable.sort.unsorted").type(JsonFieldType.BOOLEAN).description("정렬 X 여부"),
                fieldWithPath("pageable.sort.sorted").type(JsonFieldType.BOOLEAN).description("정렬 O 여부"),

                fieldWithPath("pageable.pageNumber").type(JsonFieldType.NUMBER).description("페이지 번호"),
                fieldWithPath("pageable.pageSize").type(JsonFieldType.NUMBER).description("한 페이지에 나오는 원소 수"),
                fieldWithPath("pageable.offset").type(JsonFieldType.NUMBER).description(""),
                fieldWithPath("pageable.unpaged").type(JsonFieldType.BOOLEAN).description(""),
                fieldWithPath("pageable.paged").type(JsonFieldType.BOOLEAN).description(""),

                fieldWithPath("totalPages").type(JsonFieldType.NUMBER).description("전체 페이지 수"),
                fieldWithPath("totalElements").type(JsonFieldType.NUMBER).description("전체 검색 갯수"),
                fieldWithPath("last").type(JsonFieldType.BOOLEAN).description(""),
                fieldWithPath("size").type(JsonFieldType.NUMBER).description("한 페이지 사이즈"),
                fieldWithPath("number").type(JsonFieldType.NUMBER).description(""),
                fieldWithPath("sort.empty").type(JsonFieldType.BOOLEAN).description("정렬 empty"),
                fieldWithPath("sort.unsorted").type(JsonFieldType.BOOLEAN).description("정렬 X 여부"),
                fieldWithPath("sort.sorted").type(JsonFieldType.BOOLEAN).description("정렬 O 여부"),
                fieldWithPath("first").type(JsonFieldType.BOOLEAN).description(""),
                fieldWithPath("empty").type(JsonFieldType.BOOLEAN).description(""),
                fieldWithPath("numberOfElements").type(JsonFieldType.NUMBER).description("한 페이지의 원소 수")
            )
        ));
  }

  @Test
  @DisplayName("유저 정보 삭제 테스트")
  void delete() throws Exception {
    // given
    User user = generateUser(loginId, password, email, phone);
    TokenData tokenData = TokenData.of(user);
    String accessToken = jwtTokenProvider.generateAccessToken(tokenData);

    // when
    ResultActions result = mockMvc.perform(MockMvcRequestBuilders.delete("/users")
        .contentType(MediaType.APPLICATION_JSON)
        .header(AUTHORIZATION_HEADER, accessToken));

    // then
    result.andExpect(status().isNoContent())
        .andDo(document("유저 - 유저 삭제 성공",
            getDocumentRequest(),
            getDocumentResponse()
        ));

    Assertions.assertThatThrownBy(() -> userRepository.existsByLoginId(loginId))
        .isInstanceOf(InvalidDataAccessResourceUsageException.class);
  }

  @Test
  @DisplayName("유저 정보 삭제 실패 테스트 - 없는 유저")
  void deleteNotExistUser() throws Exception {
    // given
    TokenData tokenData = TokenData.from(2L, email, Role.USER.name());
    String accessToken = jwtTokenProvider.generateAccessToken(tokenData);

    // when
    ResultActions result = mockMvc.perform(MockMvcRequestBuilders.delete("/users")
        .contentType(MediaType.APPLICATION_JSON)
        .header(AUTHORIZATION_HEADER, accessToken));

    // then
    result.andExpect(status().isNotFound())
        .andDo(document("유저 - 유저 삭제 실패(없는 유저 삭제 요청)",
            getDocumentRequest(),
            getDocumentResponse(),
            getErrorResponseField()
        ));
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
        fieldWithPath("errors.[].field").type(JsonFieldType.STRING).description("에러 발생 필드").optional(),
        fieldWithPath("errors.[].value").type(JsonFieldType.STRING).description("값").optional(),
        fieldWithPath("errors.[].errorMessage").type(JsonFieldType.STRING).description("에러 발생 이유").optional()
    );
  }

  private User generateUser(String loginId, String password, String email, String phone) {

    User user = User.builder()
        .loginId(loginId)
        .password(password)
        .name("test")
        .email(email)
        .phone(phone)
        .address(Address.from("03333", "서울시", "서울시"))
        .build();

    user.encryptPassword();

    return userRepository.save(user);
  }


}