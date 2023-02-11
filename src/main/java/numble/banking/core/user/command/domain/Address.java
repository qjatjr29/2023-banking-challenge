package numble.banking.core.user.command.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Address {

  @Column(name = "zip_code")
  private String zipCode;

  @Column(name = "address")
  private String address;

  @Column(name = "road_address")
  private String roadAddress;

  protected Address(String zipCode, String address, String roadAddress) {
    this.zipCode = zipCode;
    this.address = address;
    this.roadAddress = roadAddress;
  }

  public static Address from(String zipCode, String address, String roadAddress) {
    return new Address(zipCode, address, roadAddress);
  }

}
