package numble.banking.core.user.query.dao;

import java.util.Optional;
import numble.banking.core.user.query.dto.UserData;
import numble.banking.core.user.query.dto.UserQueryDetailResponse;
import numble.banking.core.user.query.dto.UserSummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserDao extends JpaRepository<UserData, Long> {

  @Query("SELECT new numble.banking.core.user.query.dto.UserQueryDetailResponse(u.loginId, u.name, u.email, u.phone, u.address) FROM User u WHERE u.id = :id")
  Optional<UserQueryDetailResponse> findDetailById(@Param("id") Long id);

  @Query("SELECT new numble.banking.core.user.query.dto.UserSummaryResponse(u.name, u.email, u.phone) "
      + "FROM User u WHERE u.id <> :id")
  Page<UserSummaryResponse> findSummaryList(@Param("id") Long id, Pageable pageable);

  @Query("SELECT new numble.banking.core.user.query.dto.UserSummaryResponse(u.name, u.email, u.phone) "
      + "FROM User u WHERE u.id <> :id and u.name = :name")
  Page<UserSummaryResponse> findAllByName(@Param("name") String name, @Param("id") Long userId, Pageable pageable);
}
