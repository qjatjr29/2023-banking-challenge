package numble.banking.core.account.presentation;

import static numble.banking.support.ApiDocumentUtils.getDocumentRequest;
import static numble.banking.support.ApiDocumentUtils.getDocumentResponse;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import numble.banking.core.account.command.application.OpenAccountRequest;
import numble.banking.core.account.command.application.TransferRequest;
import numble.banking.core.account.command.domain.Account;
import numble.banking.core.account.command.domain.AccountRepository;
import numble.banking.core.account.command.domain.AccountType;
import numble.banking.core.account.command.domain.Bank;
import numble.banking.core.account.command.domain.Money;
import numble.banking.core.token.JwtTokenProvider;
import numble.banking.core.token.TokenData;
import numble.banking.core.user.command.application.UserService;
import numble.banking.core.user.command.domain.Address;
import numble.banking.core.user.command.domain.User;
import numble.banking.core.user.command.domain.UserRepository;
import numble.banking.support.controller.BaseControllerTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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
  UserService userService;

  @Autowired
  JwtTokenProvider jwtTokenProvider;

  String accountType;
  String accountName;
  Bank bank;

  @BeforeEach
  void setup() {
    accountName = "beomsic's account";
    accountType = "DEPOSIT";
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
                fieldWithPath("balance").type(JsonFieldType.OBJECT).description("잔액 정보"),
                fieldWithPath("balance.money").type(JsonFieldType.NUMBER).description("잔액"),
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

  @Test
  @DisplayName("내 계좌 리스트 조회 테스트")
  void getMyAccounts() throws Exception {

    // given
    User user = generateUser("beomsic", "password12!", "beomsic@gmail.com", "010-0000-0000");
    String accessToken = jwtTokenProvider.generateAccessToken(TokenData.of(user));

    // when
    generateAccount(user.getId(), user.getName(), "testAccount1", "DEPOSIT", "우리은행");
    generateAccount(user.getId(), user.getName(), "testAccount2", "SAVINGS", "국민은행");
    generateAccount(user.getId(), user.getName(), "testAccount3", "STOCK", "신한은행");

    ResultActions result = mockMvc.perform(get("/accounts/me")
        .contentType(MediaType.APPLICATION_JSON)
        .header("Authorization", accessToken));

    // then
    result.andExpect(status().isOk())
        .andDo(document("계좌 - 내 전체 계좌 조회 API",
            getDocumentRequest(),
            getDocumentResponse(),
            responseFields(

                fieldWithPath("content").type(JsonFieldType.ARRAY).description("검색 결과 리스트"),
                fieldWithPath("content.[].accountNumber").type(JsonFieldType.STRING).description("계좌 번호").optional(),
                fieldWithPath("content.[].accountName").type(JsonFieldType.STRING).description("계좌 이름").optional(),
                fieldWithPath("content.[].type").type(JsonFieldType.STRING).description("계좌 타입").optional(),
                fieldWithPath("content.[].bank").type(JsonFieldType.STRING).description("은행").optional(),

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
  @DisplayName("계좌 상세 정보 조회 테스트")
  void getMyAccount() throws Exception {

    // given
    User user = generateUser("beomsic", "password12!", "beomsic@gmail.com", "010-0000-0000");
    String accessToken = jwtTokenProvider.generateAccessToken(TokenData.of(user));
    Account account = generateAccount(user.getId(), user.getName(), "account1", "DEPOSIT", "우리은행");

    // when
    ResultActions result = mockMvc.perform(get("/accounts/me/{accountId}", account.getId())
        .contentType(MediaType.APPLICATION_JSON)
        .header("Authorization", accessToken));

    // then
    result.andExpect(status().isOk())
        .andDo(document("계좌 - 계좌 상세 정보 조회 API",
            getDocumentRequest(),
            getDocumentResponse(),
            responseFields(
                fieldWithPath("accountId").type(JsonFieldType.NUMBER).description("계좌 아이디"),
                fieldWithPath("accountName").type(JsonFieldType.STRING).description("계좌 이름"),
                fieldWithPath("accountNumber").type(JsonFieldType.STRING).description("계좌번호"),
                fieldWithPath("balance").type(JsonFieldType.OBJECT).description("잔액 정보"),
                fieldWithPath("balance.money").type(JsonFieldType.NUMBER).description("잔액"),
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


  @Test
  @DisplayName("이체 테스트")
  void transfer() throws Exception {
    // given
    User user = generateUser("beomsic", "password12!", "beomsic@gmail.com", "010-0000-0000");
    User friend = generateUser("friend1", "friendpwd12!", "friend@gmail.com", "010-1110-0000");
    String accessToken = jwtTokenProvider.generateAccessToken(TokenData.of(user));
    Account account = generateAccount(user.getId(), user.getName(), "account1", "DEPOSIT", "우리은행");
    Account friendAccount = generateAccount(friend.getId(), friend.getName(), "account2", "DEPOSIT", "국민은행");
    TransferRequest transferRequest = new TransferRequest(account.getId(), friendAccount.getId(), new Money(1000L), friendAccount.getAccountNumber(), "이체테스트");
    // when
    account.deposit(new Money(10000L));
    userService.follow(user.getId(), friend.getId());
    ResultActions result = mockMvc.perform(post("/accounts/transfer")
        .contentType(MediaType.APPLICATION_JSON)
        .header("Authorization", accessToken)
        .content(objectMapper.writeValueAsString(transferRequest)));
    // then
    result.andExpect(status().isOk())
        .andDo(document("계좌 - 계좌 아이디를 통한 이체 API",
            getDocumentRequest(),
            getDocumentResponse(),
            requestFields(
                fieldWithPath("fromAccountId").type(JsonFieldType.NUMBER).description("나의 계좌 아이디"),
                fieldWithPath("toAccountId").type(JsonFieldType.NUMBER).description("상대 계좌 아이디"),
                fieldWithPath("accountNumber").type(JsonFieldType.STRING).description("계좌번호").optional(),
                fieldWithPath("amount").type(JsonFieldType.OBJECT).description("이체 금액"),
                fieldWithPath("amount.money").type(JsonFieldType.NUMBER).description("이체 금액"),
                fieldWithPath("content").type(JsonFieldType.STRING).description("이체 내용").optional()
            ),
            responseFields(
                fieldWithPath("from").type(JsonFieldType.STRING).description("송신자 이름"),
                fieldWithPath("to").type(JsonFieldType.STRING).description("수신자 이름"),
                fieldWithPath("amount").type(JsonFieldType.OBJECT).description("이체 금액 정보"),
                fieldWithPath("amount.money").type(JsonFieldType.NUMBER).description("이체 금액"),
                fieldWithPath("balance").type(JsonFieldType.OBJECT).description("잔액 정보"),
                fieldWithPath("balance.money").type(JsonFieldType.NUMBER).description("잔액"),
                fieldWithPath("isDeposit").type(JsonFieldType.BOOLEAN).description("입금인지 출금인지 확인"),
                fieldWithPath("transferTime").type(JsonFieldType.VARIES).description("이체 시간"),
                fieldWithPath("content").type(JsonFieldType.STRING).description("이체 내용").optional()
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

  private Account generateAccount(Long userId, String ownerName, String accountName, String type, String bank) {
    Account account = Account.builder()
        .userId(userId)
        .ownerName(ownerName)
        .accountName(accountName)
        .accountType(AccountType.valueOf(type))
        .balance(new Money(10000L))
        .bank(Bank.valueOf(bank))
        .build();

    Account save = accountRepository.save(account);
    save.generateAccountNumber();

    return save;
  }

}