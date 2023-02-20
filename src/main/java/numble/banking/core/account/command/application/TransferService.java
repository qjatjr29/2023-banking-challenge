package numble.banking.core.account.command.application;

import lombok.extern.slf4j.Slf4j;
import numble.banking.core.account.command.domain.Account;
import numble.banking.core.account.command.domain.AccountRepository;
import numble.banking.core.common.error.ErrorCode;
import numble.banking.core.common.error.exception.BadRequestException;
import numble.banking.core.common.error.exception.NotFoundException;
import numble.banking.core.user.command.domain.User;
import numble.banking.core.user.command.domain.UserRepository;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

@Service
@Slf4j
public class TransferService {

  private static final String ACCOUNT_LOCK_KEY_PREFIX = "lock:account:";

  private final AccountRepository accountRepository;
  private final UserRepository userRepository;
  private final RedissonClient redissonClient;
  private final TransactionTemplate transactionTemplate;

  public TransferService(
      AccountRepository accountRepository,
      UserRepository userRepository,
      RedissonClient redissonClient,
      PlatformTransactionManager transactionTemplate) {
    this.accountRepository = accountRepository;
    this.userRepository = userRepository;
    this.redissonClient = redissonClient;
    this.transactionTemplate = new TransactionTemplate(transactionTemplate);
  }

  // todo : 알람을 위한 이벤트 추가
  public TransferResponse transferMoney(Long userId, TransferRequest request) {

    String key = ACCOUNT_LOCK_KEY_PREFIX + request.getFromAccountId() + "-" + request.getToAccountId();
    RLock lock = redissonClient.getLock(key);

    try {
      lock.lock();

      User user = userRepository.findById(userId)
          .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

      Account fromAccount = accountRepository.findById(request.getFromAccountId())
          .orElseThrow(() -> new NotFoundException(ErrorCode.ACCOUNT_NOT_FOUND));

      Account toAccount = accountRepository.findById(request.getToAccountId())
          .orElseThrow(() -> new NotFoundException(ErrorCode.ACCOUNT_NOT_FOUND));

      checkAreTheyFriend(user, toAccount.getUserId());

      transactionTemplate.execute(new TransactionCallbackWithoutResult() {
        @Override
        protected void doInTransactionWithoutResult(TransactionStatus status) {
          fromAccount.withdrawal(request.getAmount());
          toAccount.deposit(request.getAmount());

          fromAccount.addTransferHistory(request.getAmount(), toAccount.getOwnerName(), request.getContent(), true);
          toAccount.addTransferHistory(request.getAmount(), fromAccount.getOwnerName(), request.getContent(), false);

          accountRepository.save(fromAccount);
          accountRepository.save(toAccount);
        }
      });

      return TransferResponse.from(fromAccount.getOwnerName(),
          toAccount.getOwnerName(),
          fromAccount.getTransferHistories()
              .get(fromAccount.getTransferHistories().size() - 1));
    } finally {
      lock.unlock();
    }

  }

  private void checkAreTheyFriend(User user, Long otherUserId) {
    if(!user.areTheyFriend(otherUserId)) throw new BadRequestException(ErrorCode.INSUFFICIENT_QUALIFICATIONS_FRIEND);
  }

}
