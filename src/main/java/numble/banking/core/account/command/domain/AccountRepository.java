package numble.banking.core.account.command.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {
  Optional<Account> findByAccountNumber(String accountNumber);

  Optional<Account> findById(Long myAccountId);
}
