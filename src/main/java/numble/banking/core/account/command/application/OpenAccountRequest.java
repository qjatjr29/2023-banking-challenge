package numble.banking.core.account.command.application;

import javax.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import numble.banking.core.account.command.domain.Bank;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class OpenAccountRequest {

  @NotBlank
  private String accountType;

  @NotBlank
  private String accountName;

  @NotBlank
  private Bank bank;

}
