package numble.banking.core.user.query.dto;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import numble.banking.core.common.domain.BaseEntity;
import numble.banking.core.user.command.domain.Address;
import numble.banking.core.user.command.domain.Role;

@Entity
@Table(name = "user")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserData extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "user_id")
  Long id;

  @Column(name = "login_id", unique = true)
  private String loginId;

  @Column(name = "password")
  private String password;

  @Column(name = "name")
  private String name;

  @Column(name = "email", unique = true)
  private String email;

  @Column(name = "phone", unique = true)
  private String phone;

  @Embedded
  private Address address;

  @Enumerated(value = EnumType.STRING)
  @Builder.Default
  private Role role = Role.USER;

  @Column(name = "is_deleted")
  @Builder.Default
  private Boolean isDeleted = Boolean.FALSE;

}
