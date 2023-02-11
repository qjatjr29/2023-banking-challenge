package numble.banking.core.user.presentation;

import java.net.URI;
import javax.validation.Valid;
import numble.banking.core.user.command.application.SignupRequest;
import numble.banking.core.user.command.application.UserDetailResponse;
import numble.banking.core.user.command.application.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @PostMapping
  public ResponseEntity<UserDetailResponse> signup(@RequestBody @Valid SignupRequest request) {
    UserDetailResponse userDetail = userService.signup(request);

    return ResponseEntity.created(URI.create("")).body(userDetail);
  }

}
