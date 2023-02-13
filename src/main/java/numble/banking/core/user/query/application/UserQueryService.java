package numble.banking.core.user.query.application;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import numble.banking.core.common.error.ErrorCode;
import numble.banking.core.common.error.exception.NotFoundException;
import numble.banking.core.user.command.domain.Friend;
import numble.banking.core.user.command.domain.User;
import numble.banking.core.user.query.dao.UserDao;
import numble.banking.core.user.query.dto.FriendDetailResponse;
import numble.banking.core.user.query.dto.UserData;
import numble.banking.core.user.query.dto.UserQueryDetailResponse;
import numble.banking.core.user.query.dto.UserSummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class UserQueryService {

  private final UserDao userDao;

  public UserQueryService(UserDao userDao) {
    this.userDao = userDao;
  }

  public UserQueryDetailResponse getMyInfo(Long id) {
    return userDao.findDetailById(id)
        .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));
  }

  public Page<UserSummaryResponse> getUserList(Long id, Pageable pageable) {
    Page<User> userList = userDao.findUserAllById(id, pageable);
    UserData userData = userDao.findById(id)
        .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

    return userList.map(user ->
        UserSummaryResponse
            .from(user,
                userData.getFriendSet()
                    .stream()
                    .anyMatch(friend -> friend.getFriendId().equals(user.getId()))
    ));
  }

  public Page<UserSummaryResponse> getUserListByName(Long id, String name, Pageable pageable) {
    Page<User> userList = userDao.findAllByName(name, id, pageable);

    UserData userData = userDao.findById(id)
        .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

    return userList.map(user ->
        UserSummaryResponse
            .from(user,
                userData.getFriendSet()
                    .stream()
                    .anyMatch(friend -> friend.getFriendId().equals(user.getId()))
            ));
  }

  public Page<FriendDetailResponse> getFriendList(Long userId, Pageable pageable) {
    UserData userData = userDao.findById(userId)
        .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

    Set<Friend> friends = userData.getFriendSet();

    List<FriendDetailResponse> collect = friends.stream()
        .map(FriendDetailResponse::of)
        .collect(Collectors.toList());

    return new PageImpl<>(collect, pageable, collect.size());
  }
}
