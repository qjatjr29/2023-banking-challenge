package numble.banking.core.user.command.application;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import numble.banking.core.user.command.domain.Address;
import numble.banking.core.user.command.domain.User;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserDetailResponse {

  private Long id;
  private String name;
  private String email;
  private String phone;
  private Address address;

  protected UserDetailResponse(final Long id, final String name, final String email, final String phone, final Address address) {
    this.id = id;
    this.name = name;
    this.email = email;
    this.phone = phone;
    this.address = address;
  }

  public static UserDetailResponse of(final User user) {
    return new UserDetailResponse(
        user.getId(),
        user.getName(),
        user.getEmail(),
        user.getPhone(),
        user.getAddress()
    );
  }
}
