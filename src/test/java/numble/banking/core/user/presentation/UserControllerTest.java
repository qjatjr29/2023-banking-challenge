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
import numble.banking.core.token.JwtTokenProvider;
import numble.banking.core.token.TokenData;
import numble.banking.core.user.command.application.SignupRequest;
import numble.banking.core.user.command.application.UserService;
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

@DisplayName("?????? API ?????????")
class UserControllerTest extends BaseControllerTest {

  static final String AUTHORIZATION_HEADER = "Authorization";

  @Autowired
  UserService userService;

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
  @DisplayName("???????????? ?????? ?????????")
  void signupSuccess() throws Exception {
    // given
    SignupRequest signUpRequest = SignupRequest.builder()
        .loginId(loginId)
        .password(password)
        .name("beomsic")
        .email(email)
        .phone(phone)
        .zipCode("03333")
        .address("?????????")
        .roadAddress("?????????")
        .build();

    // when
    ResultActions result = mockMvc.perform(post("/users")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(signUpRequest)));

    // then
    result.andExpect(status().isCreated())
        .andExpect(jsonPath("email").value(email))
        .andExpect(jsonPath("phone").value(phone))
        .andDo(document("?????? - ???????????? ?????? API",
            getDocumentRequest(),
            getDocumentResponse(),
            requestFields(
                fieldWithPath("loginId").type(JsonFieldType.STRING).description("????????? ?????????"),
                fieldWithPath("password").type(JsonFieldType.STRING).description("????????????"),
                fieldWithPath("name").type(JsonFieldType.STRING).description("??????"),
                fieldWithPath("email").type(JsonFieldType.STRING).description("?????????"),
                fieldWithPath("phone").type(JsonFieldType.STRING).description("????????? ??????"),
                fieldWithPath("zipCode").type(JsonFieldType.STRING).description("????????????").optional(),
                fieldWithPath("address").type(JsonFieldType.STRING).description("??????").optional(),
                fieldWithPath("roadAddress").type(JsonFieldType.STRING).description("???????????????").optional()
            ),
            responseFields(
                fieldWithPath("id").type(JsonFieldType.NUMBER).description("?????? ?????????"),
                fieldWithPath("name").type(JsonFieldType.STRING).description("??????"),
                fieldWithPath("email").type(JsonFieldType.STRING).description("?????????"),
                fieldWithPath("phone").type(JsonFieldType.STRING).description("????????? ??????"),
                fieldWithPath("address").type(JsonFieldType.OBJECT).description("?????? ??????"),
                fieldWithPath("address.zipCode").type(JsonFieldType.STRING).description("????????????").optional(),
                fieldWithPath("address.address").type(JsonFieldType.STRING).description("??????").optional(),
                fieldWithPath("address.roadAddress").type(JsonFieldType.STRING).description("???????????????").optional()
            )
        ));
  }

  @ParameterizedTest(name = "empty field: {0}")
  @MethodSource("signupFailRequests")
  @DisplayName("SignupRequest dto ??? ????????? ????????? ????????? ??????")
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
            document("?????? - ???????????? ??????(request dto - " + field + " X)",
                getDocumentRequest(),
                getDocumentResponse(),
                requestFields(
                    fieldWithPath("loginId").type(JsonFieldType.STRING).description("????????? ?????????"),
                    fieldWithPath("password").type(JsonFieldType.STRING).description("????????????"),
                    fieldWithPath("name").type(JsonFieldType.STRING).description("??????"),
                    fieldWithPath("email").type(JsonFieldType.STRING).description("?????????"),
                    fieldWithPath("phone").type(JsonFieldType.STRING).description("????????? ??????"),
                    fieldWithPath("zipCode").type(JsonFieldType.STRING).description("????????????").optional(),
                    fieldWithPath("address").type(JsonFieldType.STRING).description("??????").optional(),
                    fieldWithPath("roadAddress").type(JsonFieldType.STRING).description("???????????????").optional()
                ),
                getErrorResponseField()
            )
        );
  }

  @Test
  @DisplayName("????????? ?????? ?????????")
  void getMyInfo() throws Exception {

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
        .andDo(document("?????? - ??? ?????? ?????? ??????",
            getDocumentRequest(),
            getDocumentResponse(),
            responseFields(
                fieldWithPath("id").type(JsonFieldType.NUMBER).description("?????? ?????????"),
                fieldWithPath("loginId").type(JsonFieldType.STRING).description("?????? ????????? ?????????"),
                fieldWithPath("name").type(JsonFieldType.STRING).description("??????"),
                fieldWithPath("email").type(JsonFieldType.STRING).description("?????????"),
                fieldWithPath("phone").type(JsonFieldType.STRING).description("????????? ??????"),
                fieldWithPath("address").type(JsonFieldType.OBJECT).description("?????? ??????"),
                fieldWithPath("address.zipCode").type(JsonFieldType.STRING).description("????????????").optional(),
                fieldWithPath("address.address").type(JsonFieldType.STRING).description("??????").optional(),
                fieldWithPath("address.roadAddress").type(JsonFieldType.STRING).description("???????????????").optional(),
                fieldWithPath("createdAt").type(JsonFieldType.VARIES).description("?????? ??????")
            )
        ));

  }

  @Test
  @DisplayName("?????? ?????? ?????? ?????????")
  void getUserInfo() throws Exception {

    // given
    User user = generateUser(loginId, password, email, phone);
    TokenData tokenData = TokenData.of(user);
    String accessToken = jwtTokenProvider.generateAccessToken(tokenData);

    // when
    ResultActions result = mockMvc.perform(get("/users/{userId}", user.getId())
        .contentType(MediaType.APPLICATION_JSON)
        .header(AUTHORIZATION_HEADER, accessToken));

    // then
    result.andExpect(status().isOk())
        .andExpect(jsonPath("email").value(email))
        .andExpect(jsonPath("phone").value(phone))
        .andDo(document("?????? - ?????? ?????? ?????? ?????? ??????",
            getDocumentRequest(),
            getDocumentResponse(),
            responseFields(
                fieldWithPath("id").type(JsonFieldType.NUMBER).description("?????? ?????????"),
                fieldWithPath("loginId").type(JsonFieldType.STRING).description("?????? ????????? ?????????"),
                fieldWithPath("name").type(JsonFieldType.STRING).description("??????"),
                fieldWithPath("email").type(JsonFieldType.STRING).description("?????????"),
                fieldWithPath("phone").type(JsonFieldType.STRING).description("????????? ??????"),
                fieldWithPath("address").type(JsonFieldType.OBJECT).description("?????? ??????"),
                fieldWithPath("address.zipCode").type(JsonFieldType.STRING).description("????????????").optional(),
                fieldWithPath("address.address").type(JsonFieldType.STRING).description("??????").optional(),
                fieldWithPath("address.roadAddress").type(JsonFieldType.STRING).description("???????????????").optional(),
                fieldWithPath("createdAt").type(JsonFieldType.VARIES).description("?????? ??????")
            )
        ));

  }


  @Test
  @DisplayName("?????? ?????? ?????? ?????????")
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
        .andDo(document("?????? - ?????? ?????? ?????? ??????",
            getDocumentRequest(),
            getDocumentResponse(),
            responseFields(
                fieldWithPath("content").type(JsonFieldType.ARRAY).description("?????? ?????? ?????????"),
                fieldWithPath("content.[].id").type(JsonFieldType.NUMBER).description("?????? ?????????"),
                fieldWithPath("content.[].name").type(JsonFieldType.STRING).description("??????"),
                fieldWithPath("content.[].email").type(JsonFieldType.STRING).description("?????????"),
                fieldWithPath("content.[].phone").type(JsonFieldType.STRING).description("????????? ??????"),
                fieldWithPath("content.[].isFriend").type(JsonFieldType.BOOLEAN).description("?????? ??????"),

                fieldWithPath("pageable").type(JsonFieldType.OBJECT).description("????????? ??????"),
                fieldWithPath("pageable.sort").type(JsonFieldType.OBJECT).description("????????? ?????? ??????"),
                fieldWithPath("pageable.sort.empty").type(JsonFieldType.BOOLEAN).description("?????? empty"),
                fieldWithPath("pageable.sort.unsorted").type(JsonFieldType.BOOLEAN).description("?????? X ??????"),
                fieldWithPath("pageable.sort.sorted").type(JsonFieldType.BOOLEAN).description("?????? O ??????"),

                fieldWithPath("pageable.pageNumber").type(JsonFieldType.NUMBER).description("????????? ??????"),
                fieldWithPath("pageable.pageSize").type(JsonFieldType.NUMBER).description("??? ???????????? ????????? ?????? ???"),
                fieldWithPath("pageable.offset").type(JsonFieldType.NUMBER).description(""),
                fieldWithPath("pageable.unpaged").type(JsonFieldType.BOOLEAN).description(""),
                fieldWithPath("pageable.paged").type(JsonFieldType.BOOLEAN).description(""),

                fieldWithPath("totalPages").type(JsonFieldType.NUMBER).description("?????? ????????? ???"),
                fieldWithPath("totalElements").type(JsonFieldType.NUMBER).description("?????? ?????? ??????"),
                fieldWithPath("last").type(JsonFieldType.BOOLEAN).description(""),
                fieldWithPath("size").type(JsonFieldType.NUMBER).description("??? ????????? ?????????"),
                fieldWithPath("number").type(JsonFieldType.NUMBER).description(""),
                fieldWithPath("sort.empty").type(JsonFieldType.BOOLEAN).description("?????? empty"),
                fieldWithPath("sort.unsorted").type(JsonFieldType.BOOLEAN).description("?????? X ??????"),
                fieldWithPath("sort.sorted").type(JsonFieldType.BOOLEAN).description("?????? O ??????"),
                fieldWithPath("first").type(JsonFieldType.BOOLEAN).description(""),
                fieldWithPath("empty").type(JsonFieldType.BOOLEAN).description(""),
                fieldWithPath("numberOfElements").type(JsonFieldType.NUMBER).description("??? ???????????? ?????? ???")
            )
        ));

  }

  @Test
  @DisplayName("???????????? ?????? ?????? ?????????")
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
        .andDo(document("?????? - ???????????? ?????? ??????",
            getDocumentRequest(),
            getDocumentResponse(),
            responseFields(
                fieldWithPath("content").type(JsonFieldType.ARRAY).description("?????? ?????? ?????????"),
                fieldWithPath("content.[].id").type(JsonFieldType.NUMBER).description("?????? ?????????"),
                fieldWithPath("content.[].name").type(JsonFieldType.STRING).description("??????"),
                fieldWithPath("content.[].email").type(JsonFieldType.STRING).description("?????????"),
                fieldWithPath("content.[].phone").type(JsonFieldType.STRING).description("????????? ??????"),
                fieldWithPath("content.[].isFriend").type(JsonFieldType.BOOLEAN).description("?????? ??????"),

                fieldWithPath("pageable").type(JsonFieldType.OBJECT).description("????????? ??????"),
                fieldWithPath("pageable.sort").type(JsonFieldType.OBJECT).description("????????? ?????? ??????"),
                fieldWithPath("pageable.sort.empty").type(JsonFieldType.BOOLEAN).description("?????? empty"),
                fieldWithPath("pageable.sort.unsorted").type(JsonFieldType.BOOLEAN).description("?????? X ??????"),
                fieldWithPath("pageable.sort.sorted").type(JsonFieldType.BOOLEAN).description("?????? O ??????"),

                fieldWithPath("pageable.pageNumber").type(JsonFieldType.NUMBER).description("????????? ??????"),
                fieldWithPath("pageable.pageSize").type(JsonFieldType.NUMBER).description("??? ???????????? ????????? ?????? ???"),
                fieldWithPath("pageable.offset").type(JsonFieldType.NUMBER).description(""),
                fieldWithPath("pageable.unpaged").type(JsonFieldType.BOOLEAN).description(""),
                fieldWithPath("pageable.paged").type(JsonFieldType.BOOLEAN).description(""),

                fieldWithPath("totalPages").type(JsonFieldType.NUMBER).description("?????? ????????? ???"),
                fieldWithPath("totalElements").type(JsonFieldType.NUMBER).description("?????? ?????? ??????"),
                fieldWithPath("last").type(JsonFieldType.BOOLEAN).description(""),
                fieldWithPath("size").type(JsonFieldType.NUMBER).description("??? ????????? ?????????"),
                fieldWithPath("number").type(JsonFieldType.NUMBER).description(""),
                fieldWithPath("sort.empty").type(JsonFieldType.BOOLEAN).description("?????? empty"),
                fieldWithPath("sort.unsorted").type(JsonFieldType.BOOLEAN).description("?????? X ??????"),
                fieldWithPath("sort.sorted").type(JsonFieldType.BOOLEAN).description("?????? O ??????"),
                fieldWithPath("first").type(JsonFieldType.BOOLEAN).description(""),
                fieldWithPath("empty").type(JsonFieldType.BOOLEAN).description(""),
                fieldWithPath("numberOfElements").type(JsonFieldType.NUMBER).description("??? ???????????? ?????? ???")
            )
        ));
  }

  @Test
  @DisplayName("?????? ?????? ?????????")
  void follow() throws Exception {

    // given
    User user = generateUser(loginId, password, email, phone);
    User friend = generateUser("friend2", "friend12!", "friend@gmail.com", "010-1234-1234");
    TokenData tokenData = TokenData.of(user);
    String accessToken = jwtTokenProvider.generateAccessToken(tokenData);

    // when
    ResultActions result = mockMvc.perform(post("/users/friends/{id}", friend.getId())
        .contentType(MediaType.APPLICATION_JSON)
        .header(AUTHORIZATION_HEADER, accessToken));

    // then
    result.andExpect(status().isOk())
        .andExpect(jsonPath("friendId").value(friend.getId()))
        .andDo(document("?????? - ?????? ?????? ??????",
            getDocumentRequest(),
            getDocumentResponse(),
            responseFields(
                fieldWithPath("friendId").type(JsonFieldType.NUMBER).description("?????? ?????????"),
                fieldWithPath("name").type(JsonFieldType.STRING).description("?????? ??????")
            )
        ));
  }

  @Test
  @DisplayName("?????? ?????? ?????????")
  void getFriendList() throws Exception {

    // given
    User user = generateUser(loginId, password, email, phone);
    User friend1 = generateUser("friend2", "friend12!", "friend@gmail.com", "010-1234-1234");
    User friend2 = generateUser("friend3", "friend123!", "friend3@gmail.com", "010-3234-1234");
    TokenData tokenData = TokenData.of(user);
    String accessToken = jwtTokenProvider.generateAccessToken(tokenData);

    // when
    userService.follow(user.getId(), friend1.getId());
    userService.follow(user.getId(), friend2.getId());
    ResultActions result = mockMvc.perform(get("/users/friends")
        .contentType(MediaType.APPLICATION_JSON)
        .header(AUTHORIZATION_HEADER, accessToken));

    // then
    result.andExpect(status().isOk())
        .andDo(document("?????? - ?????? ?????? ?????? ??????",
            getDocumentRequest(),
            getDocumentResponse(),
            responseFields(
                fieldWithPath("content").type(JsonFieldType.ARRAY).description("?????? ?????? ?????????"),
                fieldWithPath("content.[].id").type(JsonFieldType.NUMBER).description("?????? ?????????").optional(),
                fieldWithPath("content.[].name").type(JsonFieldType.STRING).description("?????? ??????").optional(),
                fieldWithPath("content.[].phone").type(JsonFieldType.STRING).description("?????? ????????? ??????").optional(),

                fieldWithPath("pageable").type(JsonFieldType.OBJECT).description("????????? ??????"),
                fieldWithPath("pageable.sort").type(JsonFieldType.OBJECT).description("????????? ?????? ??????"),
                fieldWithPath("pageable.sort.empty").type(JsonFieldType.BOOLEAN).description("?????? empty"),
                fieldWithPath("pageable.sort.unsorted").type(JsonFieldType.BOOLEAN).description("?????? X ??????"),
                fieldWithPath("pageable.sort.sorted").type(JsonFieldType.BOOLEAN).description("?????? O ??????"),

                fieldWithPath("pageable.pageNumber").type(JsonFieldType.NUMBER).description("????????? ??????"),
                fieldWithPath("pageable.pageSize").type(JsonFieldType.NUMBER).description("??? ???????????? ????????? ?????? ???"),
                fieldWithPath("pageable.offset").type(JsonFieldType.NUMBER).description(""),
                fieldWithPath("pageable.unpaged").type(JsonFieldType.BOOLEAN).description(""),
                fieldWithPath("pageable.paged").type(JsonFieldType.BOOLEAN).description(""),

                fieldWithPath("totalPages").type(JsonFieldType.NUMBER).description("?????? ????????? ???"),
                fieldWithPath("totalElements").type(JsonFieldType.NUMBER).description("?????? ?????? ??????"),
                fieldWithPath("last").type(JsonFieldType.BOOLEAN).description(""),
                fieldWithPath("size").type(JsonFieldType.NUMBER).description("??? ????????? ?????????"),
                fieldWithPath("number").type(JsonFieldType.NUMBER).description(""),
                fieldWithPath("sort.empty").type(JsonFieldType.BOOLEAN).description("?????? empty"),
                fieldWithPath("sort.unsorted").type(JsonFieldType.BOOLEAN).description("?????? X ??????"),
                fieldWithPath("sort.sorted").type(JsonFieldType.BOOLEAN).description("?????? O ??????"),
                fieldWithPath("first").type(JsonFieldType.BOOLEAN).description(""),
                fieldWithPath("empty").type(JsonFieldType.BOOLEAN).description(""),
                fieldWithPath("numberOfElements").type(JsonFieldType.NUMBER).description("??? ???????????? ?????? ???")
            )
        ));
  }


  @Test
  @DisplayName("?????? ?????? ?????? ?????????")
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
        .andDo(document("?????? - ?????? ?????? ??????",
            getDocumentRequest(),
            getDocumentResponse()
        ));

    Assertions.assertThatThrownBy(() -> userRepository.existsByLoginId(loginId))
        .isInstanceOf(InvalidDataAccessResourceUsageException.class);
  }

  @Test
  @DisplayName("?????? ?????? ?????? ?????? ????????? - ?????? ??????")
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
        .andDo(document("?????? - ?????? ?????? ??????(?????? ?????? ?????? ??????)",
            getDocumentRequest(),
            getDocumentResponse(),
            getErrorResponseField()
        ));
  }

  @Test
  @DisplayName("?????? ?????? ?????????")
  void deleteFriend() throws Exception {
    // given
    User user = generateUser(loginId, password, email, phone);
    User friend = generateUser("friend", "friend12!", "friend@gmail.com", "010-1111-1111");
    TokenData tokenData = TokenData.of(user);
    String accessToken = jwtTokenProvider.generateAccessToken(tokenData);

    // when
    userService.follow(user.getId(), friend.getId());
    ResultActions result = mockMvc.perform(MockMvcRequestBuilders.delete("/users/friends/{friendId}", friend.getId())
        .contentType(MediaType.APPLICATION_JSON)
        .header(AUTHORIZATION_HEADER, accessToken));

    // then
    result.andExpect(status().isNoContent())
        .andDo(document("?????? - ?????? ?????? ??????",
            getDocumentRequest(),
            getDocumentResponse()
        ));

    Assertions.assertThat(user.areTheyFriend(friend.getId())).isFalse();
    Assertions.assertThat(user.getFriendSet().size()).isEqualTo(0);
  }

  @Test
  @DisplayName("?????? ??? ?????? ?????????")
  void getFriendCount() throws Exception {
    // given
    User user = generateUser(loginId, password, email, phone);
    User friend = generateUser("friend", "friend12!", "friend@gmail.com", "010-1111-1111");
    User friend2 = generateUser("friend2", "friend123!", "friend2@gmail.com", "010-1221-1111");
    TokenData tokenData = TokenData.of(user);
    String accessToken = jwtTokenProvider.generateAccessToken(tokenData);

    // when
    userService.follow(user.getId(), friend.getId());
    userService.follow(user.getId(), friend2.getId());
    ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get("/users/friends/count")
        .contentType(MediaType.APPLICATION_JSON)
        .header(AUTHORIZATION_HEADER, accessToken));

    // then
    result.andExpect(status().isOk())
        .andDo(document("?????? - ?????? ??? ?????? ??????",
            getDocumentRequest(),
            getDocumentResponse(),
            responseFields(
                fieldWithPath("count").type(JsonFieldType.NUMBER).description("?????? ??? ")
            )
        ));

    Assertions.assertThat(user.areTheyFriend(friend.getId())).isTrue();
    Assertions.assertThat(user.getFriendSet().size()).isEqualTo(2);
  }


  static Stream<Arguments> signupFailRequests() {
    return Stream.of(

        Arguments.of("loginId", new SignupRequest("", "password12!", "beomsic", "010-0000-0000", "test@gmail.com", "08324", "?????????", "?????????")),
        Arguments.of("password", new SignupRequest("testid", "", "beomsic", "010-0000-0000", "test@gmail.com", "08324", "?????????", "?????????")),
        Arguments.of("name", new SignupRequest("testid", "password12!", "", "010-0000-0000", "test@gmail.com", "08324", "?????????", "?????????")),
        Arguments.of("phone", new SignupRequest("testid", "password12!", "beomsic", "", "test@gmail.com", "08324", "?????????", "?????????")),
        Arguments.of("email", new SignupRequest("testid", "password12!", "beomsic", "010-0000-0000", "", "08324", "?????????", "?????????"))
         );
  }

  private org.springframework.restdocs.payload.ResponseFieldsSnippet getErrorResponseField() {
    return responseFields(
        fieldWithPath("resultCode").type(JsonFieldType.STRING).description("?????? ??????"),
        fieldWithPath("message").type(JsonFieldType.STRING).description("?????? ?????????"),
        fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("HTTP ????????????"),
        fieldWithPath("errors").type(JsonFieldType.ARRAY).description("?????? ?????? ??????"),
        fieldWithPath("errors.[].field").type(JsonFieldType.STRING).description("?????? ?????? ??????").optional(),
        fieldWithPath("errors.[].value").type(JsonFieldType.STRING).description("???").optional(),
        fieldWithPath("errors.[].errorMessage").type(JsonFieldType.STRING).description("?????? ?????? ??????").optional()
    );
  }

  private User generateUser(String loginId, String password, String email, String phone) {

    User user = User.builder()
        .loginId(loginId)
        .password(password)
        .name("test")
        .email(email)
        .phone(phone)
        .address(Address.from("03333", "?????????", "?????????"))
        .build();

    user.encryptPassword();

    return userRepository.save(user);
  }


}