package numble.banking.core.account.command.domain;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import numble.banking.core.account.command.application.MoneyConverter;

@Embeddable
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class TransferHistory {

  @Column(name = "transfer_amount")
  @Convert(converter = MoneyConverter.class)
  private Money transferAmount;

  @Column(name = "balance")
  @Convert(converter = MoneyConverter.class)
  private Money balance;

  @Column(name = "is_deposit")
  private Boolean isDeposit;

  @Column(name = "transfer_person_name")
  private String transferPersonName;

  @Column(name = "content")
  private String content;

  @Column(name = "transfer_time")
  @Builder.Default
  private LocalDateTime transferTime = LocalDateTime.now();

}
