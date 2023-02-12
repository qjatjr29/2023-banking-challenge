package numble.banking.core.user.presentation;

import javax.validation.Valid;
import numble.banking.core.user.command.application.LoginRequest;
import numble.banking.core.user.command.application.TokenReissueRequest;
import numble.banking.core.user.command.application.TokenResponse;
import numble.banking.core.user.command.application.UserAuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/authentication")
public class UserAuthController {

  private final UserAuthService userAuthService;

  public UserAuthController(UserAuthService userAuthService) {
    this.userAuthService = userAuthService;
  }

  @PostMapping("/login")
  public ResponseEntity<TokenResponse> login(@RequestBody @Valid LoginRequest request) {
    TokenResponse tokenInfo = userAuthService.login(request);
    return ResponseEntity.ok().body(tokenInfo);
  }

  @PostMapping("/reissue")
  public ResponseEntity<TokenResponse> reissue(@RequestBody TokenReissueRequest request) {
    TokenResponse tokenInfo = userAuthService.reissue(request);
    return ResponseEntity.ok().body(tokenInfo);
  }
}
