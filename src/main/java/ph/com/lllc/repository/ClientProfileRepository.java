package ph.com.lllc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ph.com.lllc.entity.user.client.AppClientProfile;
import ph.com.lllc.entity.user.common.AppUser;

import java.util.Optional;

@Repository
public interface ClientProfileRepository extends JpaRepository<AppClientProfile, Long> {

    Optional<AppClientProfile> findByClientId(String clientId);
    boolean existsByAppUser(AppUser appUser);
}
