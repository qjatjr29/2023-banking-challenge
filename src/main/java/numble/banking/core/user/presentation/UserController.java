package numble.banking.core.user.presentation;

import java.net.URI;
import javax.validation.Valid;
import numble.banking.core.common.error.ErrorCode;
import numble.banking.core.common.error.exception.NotFoundException;
import numble.banking.core.common.presentation.Auth;
import numble.banking.core.common.presentation.LoginUser;
import numble.banking.core.user.command.application.FriendResponse;
import numble.banking.core.user.command.application.SignupRequest;
import numble.banking.core.user.command.application.UserDetailResponse;
import numble.banking.core.user.command.application.UserService;
import numble.banking.core.user.command.domain.Role;
import numble.banking.core.user.query.application.UserQueryService;
import numble.banking.core.user.query.dao.UserDao;
import numble.banking.core.user.query.dto.UserQueryDetailResponse;
import numble.banking.core.user.query.dto.UserSummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

  private final UserService userService;
//  private final UserDao userDao;
  private final UserQueryService userQueryService;

  public UserController(UserService userService,
      UserQueryService userQueryService) {
    this.userService = userService;
    this.userQueryService = userQueryService;
  }

  @PostMapping
  public ResponseEntity<UserDetailResponse> signup(@RequestBody @Valid SignupRequest request) {
    UserDetailResponse userDetail = userService.signup(request);

    return ResponseEntity.created(URI.create("")).body(userDetail);
  }

  @Auth(role = {Role.USER, Role.MANAGER})
  @PostMapping("/friend/{friendId}")
  public ResponseEntity<FriendResponse> follow(@PathVariable Long friendId, @LoginUser Long userId) {
    FriendResponse response = userService.follow(userId, friendId);
    return ResponseEntity.ok(response);
  }

  @Auth(role = {Role.USER, Role.MANAGER})
  @GetMapping("/me")
  public ResponseEntity<UserQueryDetailResponse> getMyInfo(@LoginUser Long loginId) {
    UserQueryDetailResponse userQueryDetailResponse = userQueryService.getMyInfo(loginId);

    return ResponseEntity.ok(userQueryDetailResponse);
  }

  @Auth(role = {Role.USER, Role.MANAGER})
  @GetMapping()
  public ResponseEntity<Page<UserSummaryResponse>> getUsers(
      @PageableDefault(page = 0, size = 15) Pageable pageable,
      @LoginUser Long userId) {
    Page<UserSummaryResponse> userList = userQueryService.getUserList(userId, pageable);
    return ResponseEntity.ok(userList);
  }

  @Auth(role = {Role.USER, Role.MANAGER})
  @GetMapping("/name")
  public ResponseEntity<Page<UserSummaryResponse>> getUsersByName(@RequestParam("name") String name,
      @LoginUser Long userId,
      @PageableDefault(page = 0, size = 15) Pageable pageable) {

    Page<UserSummaryResponse> summaryList = userQueryService.getUserListByName(userId, name, pageable);
    return ResponseEntity.ok(summaryList);
  }

  @Auth(role = {Role.USER, Role.MANAGER})
  @DeleteMapping()
  public ResponseEntity<Void> getUsersByName(@LoginUser Long userId) {

    userService.delete(userId);
    return ResponseEntity.noContent().build();
  }

}
