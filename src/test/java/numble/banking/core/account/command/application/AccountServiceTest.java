package numble.banking.core.account.command.application;

import static org.junit.jupiter.api.Assertions.*;

import numble.banking.core.account.command.domain.Account;
import numble.banking.core.account.command.domain.AccountRepository;
import numble.banking.core.account.command.domain.AccountType;
import numble.banking.core.account.command.domain.Bank;
import numble.banking.core.account.command.domain.Money;
import numble.banking.core.common.error.exception.BadRequestException;
import numble.banking.core.common.error.exception.ConflictException;
import numble.banking.core.user.command.application.NotFriendException;
import numble.banking.core.user.command.application.UserService;
import numble.banking.core.user.command.domain.Address;
import numble.banking.core.user.command.domain.User;
import numble.banking.core.user.command.domain.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith({SpringExtension.class})
@DisplayName("계좌 서비스 테스트")
class AccountServiceTest {

  @Autowired
  AccountService accountService;

  @Autowired
  TransferService transferService;

  @Autowired
  UserRepository userRepository;

  @Autowired
  AccountRepository accountRepository;

  @Autowired
  UserService userService;

  User user;
  User friend;
  Account userAccount;
  Account friendAccount;

  @BeforeEach
  void setup() {

    Long userId = 1L;
    Long friendId = 2L;

    user = userRepository.save(User.builder()
        .id(userId)
        .name("beomsic")
        .email("beomsic@gmail.com")
        .phone("010-0000-0000")
        .build());

    friend = userRepository.save(User.builder()
        .id(friendId)
        .name("friend")
        .email("friend@gmail.com")
        .phone("010-1234-4456")
        .address(Address.from("03333", "서울시", "서울시"))
        .build());

    userAccount = accountRepository.save(Account.builder()
        .userId(user.getId())
        .ownerName(user.getName())
        .accountName("beomsic_account")
        .accountNumber("1000-000-000001")
        .balance(new Money(10000L))
        .bank(Bank.국민은행)
        .accountType(AccountType.DEPOSIT)
        .build());

    friendAccount = accountRepository.save(Account.builder()
        .userId(friend.getId())
        .ownerName(friend.getName())
        .accountName("friend_account")
        .accountNumber("1000-000-000002")
        .balance(new Money(10000L))
        .bank(Bank.우리은행)
        .accountType(AccountType.DEPOSIT)
        .build());
  }

  @Test
  @Transactional
  @DisplayName("이체 테스트")
  void transferMoney(){

    // given
    TransferRequest transferRequest = new TransferRequest(
        userAccount.getId(),
        friendAccount.getId(),
        new Money(1000L),
        friendAccount.getAccountNumber(),
        "transfer test");

    // when
    userService.follow(user.getId(), friend.getId());
    TransferResponse transferResponse = accountService.transfer(user.getId(), transferRequest);

    // then
    assertEquals(user.getName(), transferResponse.getFrom());
    assertEquals(friend.getName(), transferResponse.getTo());
    assertEquals(9000L, userAccount.getBalance().getMoney());
    assertEquals(11000L, friendAccount.getBalance().getMoney());
    assertEquals(1, userAccount.getTransferHistories().size());
  }

  @Test
  @DisplayName("이체시 돈이 부족한 경우")
  public void transferMoneyThrowInsufficientBalanceExceptionWhenNotEnoughBalance() {
    // given
    TransferRequest transferRequest = new TransferRequest(userAccount.getId(), friendAccount.getId(), new Money(11000L), friendAccount.getAccountNumber(), "test");

    // when
    userService.follow(user.getId(), friend.getId());

    // then
    Assertions.assertThatThrownBy(() ->accountService.transfer(user.getId(), transferRequest))
        .isInstanceOf(InsufficientBalanceException.class);
  }

  @Test
  @DisplayName("친구가 아닌 사람에게 이체하려는 경우")
  public void transferMoney_shouldThrowNotFriendException_whenNotFriends() {

    // given
    TransferRequest transferRequest = new TransferRequest(userAccount.getId(), friendAccount.getId(), new Money(1000L), friendAccount.getAccountName(), "test");

    // when & then
    Assertions.assertThatThrownBy(() ->accountService.transfer(1L, transferRequest))
        .isInstanceOf(NotFriendException.class);
  }
}