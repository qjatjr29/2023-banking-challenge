package numble.banking.core.account.command.application;

import lombok.extern.slf4j.Slf4j;
import numble.banking.core.account.command.domain.Account;
import numble.banking.core.account.command.domain.AccountRepository;
import numble.banking.core.account.command.domain.AccountType;
import numble.banking.core.common.error.ErrorCode;
import numble.banking.core.common.error.exception.NotFoundException;
import numble.banking.core.user.command.domain.User;
import numble.banking.core.user.command.domain.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class AccountService {

  private final AccountRepository accountRepository;
  private final UserRepository userRepository;
  private final TransferService transferService;

  public AccountService(
      AccountRepository accountRepository,
      UserRepository userRepository,
      TransferService transferService) {
    this.accountRepository = accountRepository;
    this.userRepository = userRepository;
    this.transferService = transferService;
  }

  @Transactional
  public AccountDetailResponse openAccount(Long userId, OpenAccountRequest request) {

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

    Account account = Account.builder()
        .userId(userId)
        .ownerName(user.getName())
        .accountType(AccountType.getAccountType(request.getAccountType()))
        .accountName(request.getAccountName())
        .bank(request.getBank())
        .build();

    Account save = accountRepository.save(account);
    save.generateAccountNumber();

    return AccountDetailResponse.of(save);
  }

  public TransferResponse transfer(Long userId, TransferRequest request) {
    return transferService.transferMoney(userId, request);
  }

  public DepositResponse deposit(Long userId, DepositRequest request) {
    return transferService.deposit(userId, request);
  }
}
