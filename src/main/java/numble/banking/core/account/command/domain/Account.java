package numble.banking.core.account.command.domain;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import numble.banking.core.account.command.application.MoneyConverter;
import numble.banking.core.common.domain.BaseEntity;
import numble.banking.core.common.error.ErrorCode;
import numble.banking.core.common.error.exception.BadRequestException;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Table(name = "account")
@Where(clause = "is_deleted = false")
@SQLDelete(sql = "UPDATE account SET is_deleted = true WHERE id = ?")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Account extends BaseEntity {

  private static final Long ACCOUNT_NUMBER_PREFIX = 1000000000000L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "account_id")
  private Long id;

  @Column(name = "owner_id")
  private Long userId;

  @Column(name = "owner_name")
  private String ownerName;

  @Column(name = "account_number", unique = true)
  private String accountNumber;

  @Column(name = "amounts")
  @Convert(converter = MoneyConverter.class)
  @Builder.Default
  private Money balance = new Money(0L);

  @Column(name = "account_type")
  @Enumerated(EnumType.STRING)
  private AccountType accountType;

  @Column(name = "account_name")
  private String accountName;

  @Column(name = "bank")
  @Enumerated(EnumType.STRING)
  private Bank bank;

  @ElementCollection(fetch = FetchType.LAZY)
  @CollectionTable(name = "transfer_history", joinColumns = @JoinColumn(name = "account_id"))
  @OrderColumn(name = "history_index")
  @Builder.Default
  private List<TransferHistory> transferHistories = new ArrayList<>();

  @Column(name = "is_deleted")
  @Builder.Default
  private Boolean isDeleted = Boolean.FALSE;

  public void generateAccountNumber() {
    Long nextAccountNumber = ACCOUNT_NUMBER_PREFIX + id;

    StringBuilder nextNumber = new StringBuilder(Long.toString(nextAccountNumber));

    nextNumber.insert(4, "-");
    nextNumber.insert(8, "-");

    setAccountNumber(nextNumber.toString());
  }

  public void deposit(Money amounts) {
    if(amounts.getMoney() < 0) throw new BadRequestException(ErrorCode.WRONG_AMOUNT_INPUT_VALUE);
    setMoney(amounts.getMoney());
  }

  public void withdrawal(Money amounts) {
    if(amounts.getMoney() < 0) throw new BadRequestException(ErrorCode.WRONG_AMOUNT_INPUT_VALUE);
    if(!canSendMoney(amounts.getMoney())) throw new BadRequestException(ErrorCode.OVER_AMOUNT_CURRENT_VALUE);

    setMoney(-amounts.getMoney());
  }

  public void addTransferHistory(Money amounts, String fromName, String content, Boolean isDeposit) {

    TransferHistory history = TransferHistory.builder()
        .transferAmount(amounts)
        .balance(this.balance)
        .content(content)
        .transferPersonName(fromName)
        .isDeposit(isDeposit)
        .build();

    this.transferHistories.add(history);
  }

  public void addDepositHistory(Money amounts) {
    TransferHistory history = TransferHistory.builder()
        .transferAmount(amounts)
        .balance(this.balance)
        .transferPersonName(this.ownerName)
        .content("입금")
        .isDeposit(true)
        .build();

    this.transferHistories.add(history);
  }

  private void setAccountNumber(String accountNumber) {
    this.accountNumber = accountNumber;
  }

  private void setMoney(Long amount) {
    this.balance = this.balance.transfer(amount);
  }

  private boolean canSendMoney(Long withdrawalMoney) {
    return this.balance.getMoney() - withdrawalMoney >= 0;
  }
}
