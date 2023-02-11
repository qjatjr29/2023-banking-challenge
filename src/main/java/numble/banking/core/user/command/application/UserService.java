package numble.banking.core.user.command.application;

import java.util.ArrayList;
import java.util.List;
import numble.banking.core.common.error.ErrorField;
import numble.banking.core.common.error.exception.ConflictException;
import numble.banking.core.common.error.ErrorCode;
import numble.banking.core.user.command.domain.Address;
import numble.banking.core.user.command.domain.User;
import numble.banking.core.user.command.domain.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService {

  private final UserRepository userRepository;

  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public UserDetailResponse signup(SignupRequest request) {

    validateDuplication(
        request.getLoginId(),
        request.getEmail(),
        request.getPhone()
    );

    User user = User.builder()
        .loginId(request.getLoginId())
        .password(request.getPassword())
        .name(request.getName())
        .email(request.getEmail())
        .phone(request.getPhone())
        .address(Address.from(request.getZipCode(), request.getAddress(), request.getRoadAddress()))
        .build();

    user.encryptPassword();

    userRepository.save(user);

    return UserDetailResponse.of(user);
  }


  private void validateDuplication(String loginId, String email, String phone) {
    List<ErrorField> errors = new ArrayList<>();
    if(checkLoginIdDuplication(loginId)) errors.add(ErrorField.from("loginId", loginId, ErrorCode.DUPLICATE_LOGIN_ID.getMessage()));
    if(checkEmailDuplication(email)) errors.add(ErrorField.from("email", email, ErrorCode.DUPLICATE_EMAIL.getMessage()));
    if(checkPhoneDuplication(phone)) errors.add(ErrorField.from("phone", phone, ErrorCode.DUPLICATE_PHONE_NUMBER.getMessage()));

    if(!errors.isEmpty()) throw new ConflictException(errors);
  }

  private boolean checkLoginIdDuplication(String loginId) {
    return userRepository.existsByLoginId(loginId);
  }

  private boolean checkEmailDuplication(String email) {
    return userRepository.existsByEmail(email);
  }

  private boolean checkPhoneDuplication(String phone) {
    return userRepository.existsByPhone(phone);
  }

}
