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
  private Money amounts = new Money(0L);

  @Column(name = "account_type")
  @Enumerated(EnumType.STRING)
  private AccountType accountType;

  @Column(name = "account_name")
  private String accountName;

  @Column(name = "bank")
  @Enumerated(EnumType.STRING)
  private Bank bank;

  @ElementCollection
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

  private void setAccountNumber(String accountNumber) {
    this.accountNumber = accountNumber;
  }

}
