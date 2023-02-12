package numble.banking.core.user.query.dao;

import java.util.Optional;
import numble.banking.core.user.query.dto.UserData;
import numble.banking.core.user.query.dto.UserQueryDetailResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserDao extends JpaRepository<UserData, Long> {

  @Query("SELECT new numble.banking.core.user.query.dto.UserQueryDetailResponse(u.loginId, u.name, u.email, u.phone, u.address) FROM User u WHERE u.id = :id")
  Optional<UserQueryDetailResponse> findDetailById(@Param("id") Long id);

}
