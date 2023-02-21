package numble.banking.core.account.query.application;

import numble.banking.core.account.query.dto.AccountQueryDetailResponse;
import numble.banking.core.account.query.dto.AccountSummaryResponse;
import numble.banking.core.account.command.domain.Account;
import numble.banking.core.account.query.dao.AccountDao;
import numble.banking.core.common.error.ErrorCode;
import numble.banking.core.common.error.exception.BadRequestException;
import numble.banking.core.common.error.exception.NotFoundException;
import numble.banking.core.user.command.domain.User;
import numble.banking.core.user.command.domain.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AccountQueryService {

  private final AccountDao accountDao;
  private final UserRepository userRepository;

  public AccountQueryService(AccountDao accountDao,
      UserRepository userRepository) {
    this.accountDao = accountDao;
    this.userRepository = userRepository;
  }

  public Page<AccountSummaryResponse> getAccountList(Long userId, Pageable pageable) {

    if(!userRepository.existsById(userId)) throw new NotFoundException(ErrorCode.USER_NOT_FOUND);

    Page<Account> myAccounts = accountDao.findMyAccounts(userId, pageable);

    return myAccounts.map(AccountSummaryResponse::of);
  }

  public AccountQueryDetailResponse getAccount(Long userId, Long accountId) {
    if(!userRepository.existsById(userId)) throw new NotFoundException(ErrorCode.USER_NOT_FOUND);

    AccountQueryDetailResponse myAccount = accountDao.findMyAccount(userId, accountId);

    return myAccount;
  }


  public Page<AccountSummaryResponse> getFriendAccountList(Long userId, Long friendId, Pageable pageable) {

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

    if(!user.areTheyFriend(friendId))  throw new BadRequestException(ErrorCode.INSUFFICIENT_QUALIFICATIONS_FRIEND);

    Page<Account> friendAccounts = accountDao.findFriendAccounts(friendId, pageable);

    return friendAccounts.map(AccountSummaryResponse::of);
  }


}
