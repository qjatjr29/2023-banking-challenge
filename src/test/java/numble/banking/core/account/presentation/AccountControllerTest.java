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

import numble.banking.core.account.command.application.DepositRequest;
import numble.banking.core.account.command.application.OpenAccountRequest;
import numble.banking.core.account.command.application.TransferRequest;
import numble.banking.core.account.command.application.TransferUsingAccountNumberRequest;
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
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

@DisplayName("?????? API ?????????")
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
    bank = Bank.????????????;
  }

  @Test
  @DisplayName("?????? ?????? ?????????")
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
        .andDo(document("?????? - ?????? ?????? ?????? API",
            getDocumentRequest(),
            getDocumentResponse(),
            requestFields(
                fieldWithPath("accountName").type(JsonFieldType.STRING).description("?????? ??????"),
                fieldWithPath("accountType").type(JsonFieldType.STRING).description("?????? ??????"),
                fieldWithPath("bank").type(JsonFieldType.STRING).description("??????")
            ),
            responseFields(
                fieldWithPath("accountId").type(JsonFieldType.NUMBER).description("?????? ?????????"),
                fieldWithPath("accountName").type(JsonFieldType.STRING).description("?????? ??????"),
                fieldWithPath("accountNumber").type(JsonFieldType.STRING).description("????????????"),
                fieldWithPath("balance").type(JsonFieldType.OBJECT).description("?????? ??????"),
                fieldWithPath("balance.money").type(JsonFieldType.NUMBER).description("??????"),
                fieldWithPath("accountType").type(JsonFieldType.STRING).description("?????? ??????"),
                fieldWithPath("bank").type(JsonFieldType.STRING).description("??????"),
                fieldWithPath("openDate").type(JsonFieldType.VARIES).description("?????? ??????"),
                fieldWithPath("transferHistories").type(JsonFieldType.ARRAY).description("?????? ??????"),
                fieldWithPath("transferHistories.[].transferAmount").type(JsonFieldType.STRING).description("?????? ??????").optional(),
                fieldWithPath("transferHistories.[].balance").type(JsonFieldType.STRING).description("??????").optional(),
                fieldWithPath("transferHistories.[].isDeposit").type(JsonFieldType.BOOLEAN).description("???????????? ????????????").optional(),
                fieldWithPath("transferHistories.[].transferPersonName").type(JsonFieldType.STRING).description("?????? ??????").optional(),
                fieldWithPath("transferHistories.[].content").type(JsonFieldType.STRING).description("?????? ??????").optional(),
                fieldWithPath("transferHistories.[].transferTime").type(JsonFieldType.VARIES).description("?????? ??????").optional()
            )
        ));


  }

  @Test
  @DisplayName("??? ?????? ????????? ?????? ?????????")
  void getMyAccounts() throws Exception {

    // given
    User user = generateUser("beomsic", "password12!", "beomsic@gmail.com", "010-0000-0000");
    String accessToken = jwtTokenProvider.generateAccessToken(TokenData.of(user));

    // when
    generateAccount(user.getId(), user.getName(), "testAccount1", "DEPOSIT", "????????????");
    generateAccount(user.getId(), user.getName(), "testAccount2", "SAVINGS", "????????????");
    generateAccount(user.getId(), user.getName(), "testAccount3", "STOCK", "????????????");

    ResultActions result = mockMvc.perform(get("/accounts/me")
        .contentType(MediaType.APPLICATION_JSON)
        .header("Authorization", accessToken));

    // then
    result.andExpect(status().isOk())
        .andDo(document("?????? - ??? ?????? ?????? ?????? API",
            getDocumentRequest(),
            getDocumentResponse(),
            responseFields(

                fieldWithPath("content").type(JsonFieldType.ARRAY).description("?????? ?????? ?????????"),
                fieldWithPath("content.[].id").type(JsonFieldType.NUMBER).description("?????? ?????????"),
                fieldWithPath("content.[].accountNumber").type(JsonFieldType.STRING).description("?????? ??????").optional(),
                fieldWithPath("content.[].accountName").type(JsonFieldType.STRING).description("?????? ??????").optional(),
                fieldWithPath("content.[].type").type(JsonFieldType.STRING).description("?????? ??????").optional(),
                fieldWithPath("content.[].bank").type(JsonFieldType.STRING).description("??????").optional(),
                fieldWithPath("content.[].createdAt").type(JsonFieldType.VARIES).description("?????? ??????").optional(),

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
  @DisplayName("?????? ?????? ?????? ?????? ?????????")
  void getMyAccount() throws Exception {

    // given
    User user = generateUser("beomsic", "password12!", "beomsic@gmail.com", "010-0000-0000");
    String accessToken = jwtTokenProvider.generateAccessToken(TokenData.of(user));
    Account account = generateAccount(user.getId(), user.getName(), "account1", "DEPOSIT", "????????????");

    // when
    ResultActions result = mockMvc.perform(get("/accounts/me/{accountId}", account.getId())
        .contentType(MediaType.APPLICATION_JSON)
        .header("Authorization", accessToken));

    // then
    result.andExpect(status().isOk())
        .andDo(document("?????? - ?????? ?????? ?????? ?????? API",
            getDocumentRequest(),
            getDocumentResponse(),
            responseFields(
                fieldWithPath("accountId").type(JsonFieldType.NUMBER).description("?????? ?????????"),
                fieldWithPath("accountName").type(JsonFieldType.STRING).description("?????? ??????"),
                fieldWithPath("accountNumber").type(JsonFieldType.STRING).description("????????????"),
                fieldWithPath("balance").type(JsonFieldType.NUMBER).description("??????"),
                fieldWithPath("accountType").type(JsonFieldType.STRING).description("?????? ??????"),
                fieldWithPath("bank").type(JsonFieldType.STRING).description("??????"),
                fieldWithPath("openDate").type(JsonFieldType.VARIES).description("?????? ??????"),
                fieldWithPath("transferHistories").type(JsonFieldType.ARRAY).description("?????? ??????"),
                fieldWithPath("transferHistories.[].transferAmount").type(JsonFieldType.STRING).description("?????? ??????").optional(),
                fieldWithPath("transferHistories.[].balance").type(JsonFieldType.STRING).description("??????").optional(),
                fieldWithPath("transferHistories.[].isDeposit").type(JsonFieldType.BOOLEAN).description("???????????? ????????????").optional(),
                fieldWithPath("transferHistories.[].transferPersonName").type(JsonFieldType.STRING).description("?????? ??????").optional(),
                fieldWithPath("transferHistories.[].content").type(JsonFieldType.STRING).description("?????? ??????").optional(),
                fieldWithPath("transferHistories.[].transferTime").type(JsonFieldType.VARIES).description("?????? ??????").optional()
            )
        ));

  }


  @Test
  @DisplayName("?????? ?????????")
  void transfer() throws Exception {
    // given
    User user = generateUser("beomsic", "password12!", "beomsic@gmail.com", "010-0000-0000");
    User friend = generateUser("friend1", "friendpwd12!", "friend@gmail.com", "010-1110-0000");
    String accessToken = jwtTokenProvider.generateAccessToken(TokenData.of(user));
    Account account = generateAccount(user.getId(), user.getName(), "account1", "DEPOSIT", "????????????");
    Account friendAccount = generateAccount(friend.getId(), friend.getName(), "account2", "DEPOSIT", "????????????");
    TransferRequest transferRequest = new TransferRequest(account.getId(), friendAccount.getId(), new Money(1000L), friendAccount.getAccountNumber(), "???????????????");
    // when
    account.deposit(new Money(10000L));
    userService.follow(user.getId(), friend.getId());
    ResultActions result = mockMvc.perform(post("/accounts/transfer")
        .contentType(MediaType.APPLICATION_JSON)
        .header("Authorization", accessToken)
        .content(objectMapper.writeValueAsString(transferRequest)));
    // then
    result.andExpect(status().isOk())
        .andDo(document("?????? - ?????? ???????????? ?????? ?????? API",
            getDocumentRequest(),
            getDocumentResponse(),
            requestFields(
                fieldWithPath("fromAccountId").type(JsonFieldType.NUMBER).description("?????? ?????? ?????????"),
                fieldWithPath("toAccountId").type(JsonFieldType.NUMBER).description("?????? ?????? ?????????"),
                fieldWithPath("accountNumber").type(JsonFieldType.STRING).description("????????????").optional(),
                fieldWithPath("amount").type(JsonFieldType.OBJECT).description("?????? ??????"),
                fieldWithPath("amount.money").type(JsonFieldType.NUMBER).description("?????? ??????"),
                fieldWithPath("content").type(JsonFieldType.STRING).description("?????? ??????").optional()
            ),
            responseFields(
                fieldWithPath("from").type(JsonFieldType.STRING).description("????????? ??????"),
                fieldWithPath("to").type(JsonFieldType.STRING).description("????????? ??????"),
                fieldWithPath("amount").type(JsonFieldType.OBJECT).description("?????? ?????? ??????"),
                fieldWithPath("amount.money").type(JsonFieldType.NUMBER).description("?????? ??????"),
                fieldWithPath("balance").type(JsonFieldType.OBJECT).description("?????? ??????"),
                fieldWithPath("balance.money").type(JsonFieldType.NUMBER).description("??????"),
                fieldWithPath("isDeposit").type(JsonFieldType.BOOLEAN).description("???????????? ???????????? ??????"),
                fieldWithPath("transferTime").type(JsonFieldType.VARIES).description("?????? ??????"),
                fieldWithPath("content").type(JsonFieldType.STRING).description("?????? ??????").optional()
            )
        ));
  }

  @Test
  @DisplayName("??????????????? ???????????? ?????????")
  void transferUsingAccountNumber() throws Exception {
    // given
    User user = generateUser("beomsic", "password12!", "beomsic@gmail.com", "010-0000-0000");
    User friend = generateUser("friend1", "friendpwd12!", "friend@gmail.com", "010-1110-0000");
    String accessToken = jwtTokenProvider.generateAccessToken(TokenData.of(user));
    Account account = generateAccount(user.getId(), user.getName(), "account1", "DEPOSIT", "????????????");
    Account friendAccount = generateAccount(friend.getId(), friend.getName(), "account2", "DEPOSIT", "????????????");
    TransferUsingAccountNumberRequest transferRequest = new TransferUsingAccountNumberRequest(account.getAccountNumber(), friendAccount.getAccountNumber(), "???????????????", new Money(1000L));
    // when
    account.deposit(new Money(10000L));
    userService.follow(user.getId(), friend.getId());
    ResultActions result = mockMvc.perform(post("/accounts/transfer/accountNumber")
        .contentType(MediaType.APPLICATION_JSON)
        .header("Authorization", accessToken)
        .content(objectMapper.writeValueAsString(transferRequest)));
    // then
    result.andExpect(status().isOk())
        .andDo(document("?????? - ??????????????? ?????? ?????? API",
            getDocumentRequest(),
            getDocumentResponse(),
            requestFields(
                fieldWithPath("fromAccountNumber").type(JsonFieldType.STRING).description("?????? ????????????"),
                fieldWithPath("toAccountNumber").type(JsonFieldType.STRING).description("?????? ????????????"),
                fieldWithPath("amount").type(JsonFieldType.OBJECT).description("?????? ??????"),
                fieldWithPath("amount.money").type(JsonFieldType.NUMBER).description("?????? ??????"),
                fieldWithPath("content").type(JsonFieldType.STRING).description("?????? ??????").optional()
            ),
            responseFields(
                fieldWithPath("from").type(JsonFieldType.STRING).description("????????? ??????"),
                fieldWithPath("to").type(JsonFieldType.STRING).description("????????? ??????"),
                fieldWithPath("amount").type(JsonFieldType.OBJECT).description("?????? ?????? ??????"),
                fieldWithPath("amount.money").type(JsonFieldType.NUMBER).description("?????? ??????"),
                fieldWithPath("balance").type(JsonFieldType.OBJECT).description("?????? ??????"),
                fieldWithPath("balance.money").type(JsonFieldType.NUMBER).description("??????"),
                fieldWithPath("isDeposit").type(JsonFieldType.BOOLEAN).description("???????????? ???????????? ??????"),
                fieldWithPath("transferTime").type(JsonFieldType.VARIES).description("?????? ??????"),
                fieldWithPath("content").type(JsonFieldType.STRING).description("?????? ??????").optional()
            )
        ));
  }

  @Test
  @DisplayName("??? ?????? ?????? ?????????")
  void deposit() throws Exception {
    // given
    User user = generateUser("beomsic", "password12!", "beomsic@gmail.com", "010-0000-0000");
    String accessToken = jwtTokenProvider.generateAccessToken(TokenData.of(user));
    Account account = generateAccount(user.getId(), user.getName(), "account1", "DEPOSIT", "????????????");
    DepositRequest depositRequest = new DepositRequest(account.getId(), new Money(1000L), account.getAccountNumber());

    // when
    ResultActions result = mockMvc.perform(post("/accounts/deposit/me")
        .contentType(MediaType.APPLICATION_JSON)
        .header("Authorization", accessToken)
        .content(objectMapper.writeValueAsString(depositRequest)));
    // then
    result.andExpect(status().isOk())
        .andDo(document("?????? - ?????? ????????? ?????? API",
            getDocumentRequest(),
            getDocumentResponse(),
            requestFields(
                fieldWithPath("accountId").type(JsonFieldType.NUMBER).description("?????? ?????? ?????????"),
                fieldWithPath("accountNumber").type(JsonFieldType.STRING).description("????????????").optional(),
                fieldWithPath("amount").type(JsonFieldType.OBJECT).description("?????? ??????"),
                fieldWithPath("amount.money").type(JsonFieldType.NUMBER).description("?????? ??????")
            ),
            responseFields(
                fieldWithPath("amount").type(JsonFieldType.OBJECT).description("?????? ?????? ??????"),
                fieldWithPath("amount.money").type(JsonFieldType.NUMBER).description("?????? ??????"),
                fieldWithPath("balance").type(JsonFieldType.OBJECT).description("?????? ??????"),
                fieldWithPath("balance.money").type(JsonFieldType.NUMBER).description("??????"),
                fieldWithPath("isDeposit").type(JsonFieldType.BOOLEAN).description("???????????? ???????????? ??????"),
                fieldWithPath("transferTime").type(JsonFieldType.VARIES).description("?????? ??????")
            )
        ));
  }


  @Test
  @DisplayName("?????? ?????? ????????? ?????? ?????????")
  void getFriendAccounts() throws Exception {

    // given
    User user = generateUser("beomsic", "password12!", "beomsic@gmail.com", "010-0000-0000");
    String accessToken = jwtTokenProvider.generateAccessToken(TokenData.of(user));

    User friend = generateUser("friend1", "friend123!", "friend@gmail.com", "010-0000-0002");

    // when
    userService.follow(user.getId(), friend.getId());
    generateAccount(friend.getId(), friend.getName(), "testAccount1", "DEPOSIT", "????????????");
    generateAccount(friend.getId(), friend.getName(), "testAccount2", "SAVINGS", "????????????");
    generateAccount(friend.getId(), friend.getName(), "testAccount3", "STOCK", "????????????");

    ResultActions result = mockMvc.perform(get("/accounts/{friendId}", friend.getId())
        .contentType(MediaType.APPLICATION_JSON)
        .header("Authorization", accessToken));

    // then
    result.andExpect(status().isOk())
        .andDo(document("?????? - ??? ?????? ?????? ?????? API",
            getDocumentRequest(),
            getDocumentResponse(),
            responseFields(

                fieldWithPath("content").type(JsonFieldType.ARRAY).description("?????? ?????? ?????????"),
                fieldWithPath("content.[].id").type(JsonFieldType.NUMBER).description("?????? ?????????"),
                fieldWithPath("content.[].accountNumber").type(JsonFieldType.STRING).description("?????? ??????").optional(),
                fieldWithPath("content.[].accountName").type(JsonFieldType.STRING).description("?????? ??????").optional(),
                fieldWithPath("content.[].type").type(JsonFieldType.STRING).description("?????? ??????").optional(),
                fieldWithPath("content.[].bank").type(JsonFieldType.STRING).description("??????").optional(),
                fieldWithPath("content.[].createdAt").type(JsonFieldType.VARIES).description("?????? ??????").optional(),

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
  @DisplayName("??? ?????? ?????? ?????? ?????????")
  void getAccountCount() throws Exception {

    // given
    User user = generateUser("beomsic", "password12!", "beomsic@gmail.com", "010-0000-0000");
    String accessToken = jwtTokenProvider.generateAccessToken(TokenData.of(user));

    // when
    generateAccount(user.getId(), user.getName(), "testAccount1", "DEPOSIT", "????????????");
    generateAccount(user.getId(), user.getName(), "testAccount2", "SAVINGS", "????????????");
    generateAccount(user.getId(), user.getName(), "testAccount3", "STOCK", "????????????");

    ResultActions result = mockMvc.perform(get("/accounts/me/count")
        .contentType(MediaType.APPLICATION_JSON)
        .header("Authorization", accessToken));

    // then
    result.andExpect(status().isOk())
        .andDo(document("?????? - ??? ?????? ?????? ?????? API",
            getDocumentRequest(),
            getDocumentResponse(),
            responseFields(
                fieldWithPath("count").type(JsonFieldType.NUMBER).description("?????? ??? ??????")
            )
        ));

  }

  @Test
  @DisplayName("?????? ?????? ?????? ?????? ?????? ?????????")
  void getHistoriesCount() throws Exception {

    // given
    User user = generateUser("beomsic", "password12!", "beomsic@gmail.com", "010-0000-0000");
    String accessToken = jwtTokenProvider.generateAccessToken(TokenData.of(user));

    // when
    Account account = generateAccount(user.getId(), user.getName(), "testAccount1", "DEPOSIT",
        "????????????");
    Account account1 = generateAccount(user.getId(), user.getName(), "testAccount2", "SAVINGS",
        "????????????");
    Account account2 = generateAccount(user.getId(), user.getName(), "testAccount3", "STOCK",
        "????????????");

    account.addDepositHistory(new Money(100L));
    account1.addDepositHistory(new Money(100L));
    account2.addDepositHistory(new Money(100L));
    account2.addDepositHistory(new Money(1000L));

    ResultActions result = mockMvc.perform(get("/accounts/me/history/count")
        .contentType(MediaType.APPLICATION_JSON)
        .header("Authorization", accessToken));

    // then
    result.andExpect(status().isOk())
        .andDo(document("?????? - ??? ?????? ?????? ??? ?????? API",
            getDocumentRequest(),
            getDocumentResponse(),
            responseFields(
                fieldWithPath("count").type(JsonFieldType.NUMBER).description("?????? ?????? ??? ??????")
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
        .address(Address.from("03333", "?????????", "?????????"))
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