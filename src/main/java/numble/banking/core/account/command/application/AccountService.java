package numble.banking.core.account.command.application;

import numble.banking.core.account.command.domain.Account;
import numble.banking.core.account.command.domain.AccountRepository;
import numble.banking.core.common.error.ErrorCode;
import numble.banking.core.common.error.exception.NotFoundException;
import numble.banking.core.user.command.domain.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AccountService {

  private final AccountRepository accountRepository;
  private final UserRepository userRepository;

  public AccountService(
      AccountRepository accountRepository,
      UserRepository userRepository) {
    this.accountRepository = accountRepository;
    this.userRepository = userRepository;
  }

  public AccountDetailResponse openAccount(Long userId, OpenAccountRequest request) {

    if(!userRepository.existsById(userId)) throw new NotFoundException(ErrorCode.USER_NOT_FOUND);

    Account account = Account.builder()
        .userId(userId)
        .accountType(request.getAccountType())
        .accountName(request.getAccountName())
        .bank(request.getBank())
        .build();

    Account save = accountRepository.save(account);
    save.generateAccountNumber();

    return AccountDetailResponse.of(save);
  }
}
