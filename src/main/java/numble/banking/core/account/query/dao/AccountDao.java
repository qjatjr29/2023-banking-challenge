package numble.banking.core.account.query.dao;

import numble.banking.core.account.command.domain.Account;
import numble.banking.core.account.query.dto.AccountData;
import numble.banking.core.account.query.dto.AccountQueryDetailResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AccountDao extends JpaRepository<AccountData, Long> {

  @Query("SELECT ac FROM Account ac WHERE ac.userId = :userId")
  Page<Account> findMyAccounts(@Param("userId") Long userId, Pageable pageable);

  @Query("SELECT ac FROM Account ac WHERE ac.userId = :userId and ac.id = :accountId")
  AccountQueryDetailResponse findMyAccount(@Param("userId") Long userId, @Param("accountId") Long accountId);
}
