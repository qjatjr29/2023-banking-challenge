package numble.banking.core.account.presentation;

import java.net.URI;
import numble.banking.core.account.command.application.AccountDetailResponse;
import numble.banking.core.account.command.application.AccountService;
import numble.banking.core.account.command.application.DepositRequest;
import numble.banking.core.account.command.application.DepositResponse;
import numble.banking.core.account.command.application.TransferRequest;
import numble.banking.core.account.query.dto.AccountQueryDetailResponse;
import numble.banking.core.account.query.dto.AccountSummaryResponse;
import numble.banking.core.account.command.application.OpenAccountRequest;
import numble.banking.core.account.query.application.AccountQueryService;
import numble.banking.core.common.presentation.Auth;
import numble.banking.core.common.presentation.LoginUser;
import numble.banking.core.account.command.application.TransferResponse;
import numble.banking.core.user.command.domain.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/accounts")
public class AccountController {

  private final AccountService accountService;
  private final AccountQueryService accountQueryService;

  public AccountController(AccountService accountService,
      AccountQueryService accountQueryService) {
    this.accountService = accountService;
    this.accountQueryService = accountQueryService;
  }

  @Auth(role = {Role.USER, Role.MANAGER})
  @PostMapping
  public ResponseEntity<AccountDetailResponse> openAccount(@RequestBody OpenAccountRequest request, @LoginUser Long userId) {

    AccountDetailResponse response = accountService.openAccount(userId, request);

    return ResponseEntity.created(URI.create("")).body(response);
  }

  @Auth(role = {Role.USER, Role.MANAGER})
  @PostMapping("/deposit/me")
  public ResponseEntity<DepositResponse> deposit(@LoginUser Long userId, @RequestBody DepositRequest request) {

    DepositResponse response = accountService.deposit(userId, request);

    return ResponseEntity.ok().body(response);
  }

  @Auth(role = {Role.USER, Role.MANAGER})
  @PostMapping("/transfer")
  public ResponseEntity<TransferResponse> transferUsingAccountId(@LoginUser Long userId, @RequestBody TransferRequest request) {

    TransferResponse response = accountService.transfer(userId, request);

    return ResponseEntity.ok().body(response);
  }

  @Auth(role = {Role.USER, Role.MANAGER})
  @GetMapping("/me")
  public ResponseEntity<Page<AccountSummaryResponse>> getAccountList(@LoginUser Long userId,
      @PageableDefault(page = 0, size = 5) Pageable pageable) {

    Page<AccountSummaryResponse> response = accountQueryService.getAccountList(userId, pageable);

    return ResponseEntity.ok().body(response);
  }

  @Auth(role = {Role.USER, Role.MANAGER})
  @GetMapping("/me/{accountId}")
  public ResponseEntity<AccountQueryDetailResponse> getAccount(@LoginUser Long userId, @PathVariable Long accountId) {

    AccountQueryDetailResponse response = accountQueryService.getAccount(userId, accountId);

    return ResponseEntity.ok().body(response);
  }

  @Auth(role = {Role.USER, Role.MANAGER})
  @GetMapping("/{friendId}")
  public ResponseEntity<Page<AccountSummaryResponse>> getFriendAccounts(@LoginUser Long userId, @PathVariable Long friendId, @PageableDefault(page = 0, size = 5) Pageable pageable) {

    Page<AccountSummaryResponse> response = accountQueryService.getFriendAccountList(userId, friendId, pageable);

    return ResponseEntity.ok().body(response);
  }

}
