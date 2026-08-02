package ph.com.lllc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ph.com.lllc.entity.user.common.AppUser;

import java.util.Optional;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    Optional<AppUser> findUserByEmail(@Param("email") String email);
    Optional<AppUser> findUserByUsername(@Param("email") String email);
    AppUser findByUsername(@Param("username") String username);
    AppUser findByAppUserId(@Param("appUserId") Long appUserId);

    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
}
