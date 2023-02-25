package numble.banking.core.user.query.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import numble.banking.core.user.command.domain.Address;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserQueryDetailResponse {

  private Long id;
  private String loginId;
  private String name;
  private String email;
  private String phone;
  private Address address;
  private LocalDate createdAt;

  public UserQueryDetailResponse(Long id, String loginId, String name, String email, String phone,
      Address address, LocalDateTime createdAt) {
    this.id = id;
    this.loginId = loginId;
    this.name = name;
    this.email = email;
    this.phone = phone;
    this.address = address;
    this.createdAt = LocalDate.from(createdAt);
  }

  public UserQueryDetailResponse(final UserData userData) {
    this.id = userData.getId();
    this.loginId = userData.getLoginId();
    this.name = userData.getName();
    this.email = userData.getEmail();
    this.phone = userData.getPhone();
    this.address = userData.getAddress();
    this.createdAt = LocalDate.from(userData.getCreatedAt());
  }

  public static UserQueryDetailResponse of(final UserData userData) {
    return new UserQueryDetailResponse(userData);
  }
}
