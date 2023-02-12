package numble.banking.core.user.presentation;

import java.net.URI;
import java.util.List;
import javax.validation.Valid;
import numble.banking.core.common.error.ErrorCode;
import numble.banking.core.common.error.exception.NotFoundException;
import numble.banking.core.common.presentation.Auth;
import numble.banking.core.common.presentation.LoginUser;
import numble.banking.core.user.command.application.SignupRequest;
import numble.banking.core.user.command.application.UserDetailResponse;
import numble.banking.core.user.command.application.UserService;
import numble.banking.core.user.command.domain.Role;
import numble.banking.core.user.query.dao.UserDao;
import numble.banking.core.user.query.dto.UserData;
import numble.banking.core.user.query.dto.UserQueryDetailResponse;
import numble.banking.core.user.query.dto.UserSummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

  private final UserService userService;
  private final UserDao userDao;

  public UserController(UserService userService, UserDao userDao) {
    this.userService = userService;
    this.userDao = userDao;
  }

  @PostMapping
  public ResponseEntity<UserDetailResponse> signup(@RequestBody @Valid SignupRequest request) {
    UserDetailResponse userDetail = userService.signup(request);

    return ResponseEntity.created(URI.create("")).body(userDetail);
  }

  @Auth(role = {Role.USER, Role.MANAGER})
  @GetMapping("/me")
  public ResponseEntity<UserQueryDetailResponse> getMyInfo(@LoginUser Long loginId) {
    UserQueryDetailResponse userQueryDetailResponse = userDao.findDetailById(loginId)
        .orElseThrow(() -> new NotFoundException(ErrorCode.ROLE_NOT_EXISTS));

    return ResponseEntity.ok(userQueryDetailResponse);
  }

  @Auth(role = {Role.USER, Role.MANAGER})
  @GetMapping()
  public ResponseEntity<Page<UserSummaryResponse>> getUsers(
      @PageableDefault(page = 0, size = 15) Pageable pageable,
      @LoginUser Long userId) {

    Page<UserSummaryResponse> summaryList = userDao.findSummaryList(userId, pageable);
    return ResponseEntity.ok(summaryList);
  }

//  @GetMapping("/")

}
