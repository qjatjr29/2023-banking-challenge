package numble.banking.core.user.query.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import numble.banking.core.user.command.domain.Address;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserQueryDetailResponse {

  private String loginId;
  private String name;
  private String email;
  private String phone;
  private Address address;

}
