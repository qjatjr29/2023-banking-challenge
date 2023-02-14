package numble.banking.core.account.presentation;

import static numble.banking.support.ApiDocumentUtils.getDocumentRequest;
import static numble.banking.support.ApiDocumentUtils.getDocumentResponse;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import numble.banking.core.account.command.application.OpenAccountRequest;
import numble.banking.core.account.command.domain.AccountRepository;
import numble.banking.core.account.command.domain.AccountType;
import numble.banking.core.account.command.domain.Bank;
import numble.banking.core.token.JwtTokenProvider;
import numble.banking.core.token.TokenData;
import numble.banking.core.user.command.domain.Address;
import numble.banking.core.user.command.domain.User;
import numble.banking.core.user.command.domain.UserRepository;
import numble.banking.support.controller.BaseControllerTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

@DisplayName("계좌 API 테스트")
class AccountControllerTest extends BaseControllerTest {

  @Autowired
  AccountRepository accountRepository;

  @Autowired
  UserRepository userRepository;

  @Autowired
  JwtTokenProvider jwtTokenProvider;

  AccountType accountType;
  String accountName;
  Bank bank;

  @BeforeEach
  void setup() {
    accountName = "beomsic's account";
    accountType = AccountType.DEPOSIT;
    bank = Bank.우리은행;
  }

  @Test
  @DisplayName("계좌 개설 테스트")
  void openAccount() throws Exception {

    // given
    User user = generateUser("beomsic", "password12!", "beomsic@gmail.com", "010-0000-0000");
    String accessToken = jwtTokenProvider.generateAccessToken(TokenData.of(user));
    OpenAccountRequest openAccountRequest = new OpenAccountRequest(accountType, accountName, bank);

    // when
    ResultActions result = mockMvc.perform(post("/accounts")
        .contentType(MediaType.APPLICATION_JSON)
        .header("Authorization", accessToken)
        .content(objectMapper.writeValueAsString(openAccountRequest)));

    // then
    result.andExpect(status().isCreated())
        .andDo(document("계좌 - 계좌 개설 성공 API",
            getDocumentRequest(),
            getDocumentResponse(),
            requestFields(
                fieldWithPath("accountName").type(JsonFieldType.STRING).description("계좌 이름"),
                fieldWithPath("accountType").type(JsonFieldType.STRING).description("계좌 타입"),
                fieldWithPath("bank").type(JsonFieldType.STRING).description("은행")
            ),
            responseFields(
                fieldWithPath("accountId").type(JsonFieldType.NUMBER).description("계좌 아이디"),
                fieldWithPath("accountName").type(JsonFieldType.STRING).description("계좌 이름"),
                fieldWithPath("accountNumber").type(JsonFieldType.STRING).description("계좌번호"),
                fieldWithPath("accountType").type(JsonFieldType.STRING).description("계좌 타입"),
                fieldWithPath("bank").type(JsonFieldType.STRING).description("은행"),
                fieldWithPath("openDate").type(JsonFieldType.VARIES).description("개설 날짜"),
                fieldWithPath("transferHistories").type(JsonFieldType.ARRAY).description("이체 내역"),
                fieldWithPath("transferHistories.[].transferAmount").type(JsonFieldType.STRING).description("이체 금액").optional(),
                fieldWithPath("transferHistories.[].balance").type(JsonFieldType.STRING).description("잔금").optional(),
                fieldWithPath("transferHistories.[].isDeposit").type(JsonFieldType.BOOLEAN).description("입금인지 출금인지").optional(),
                fieldWithPath("transferHistories.[].transferPersonName").type(JsonFieldType.STRING).description("이체 상대").optional(),
                fieldWithPath("transferHistories.[].content").type(JsonFieldType.STRING).description("이체 내용").optional(),
                fieldWithPath("transferHistories.[].transferTime").type(JsonFieldType.VARIES).description("이체 시간").optional()
            )
        ));


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