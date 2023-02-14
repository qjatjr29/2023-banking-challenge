package numble.banking.core.account.presentation;

import java.net.URI;
import numble.banking.core.account.command.application.AccountDetailResponse;
import numble.banking.core.account.command.application.AccountService;
import numble.banking.core.account.command.application.OpenAccountRequest;
import numble.banking.core.common.presentation.Auth;
import numble.banking.core.common.presentation.LoginUser;
import numble.banking.core.user.command.domain.Role;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/accounts")
public class AccountController {

  private final AccountService accountService;

  public AccountController(AccountService accountService) {
    this.accountService = accountService;
  }

  @Auth(role = {Role.USER, Role.MANAGER})
  @PostMapping
  public ResponseEntity<AccountDetailResponse> openAccount(@RequestBody OpenAccountRequest request, @LoginUser Long userId) {

    AccountDetailResponse response = accountService.openAccount(userId, request);

    return ResponseEntity.created(URI.create("")).body(response);
  }

}
