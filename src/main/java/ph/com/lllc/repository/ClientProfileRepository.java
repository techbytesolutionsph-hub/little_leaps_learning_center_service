package ph.com.lllc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ph.com.lllc.entity.user.client.AppClientProfile;
import ph.com.lllc.entity.user.common.AppUser;

@Repository
public interface ClientProfileRepository extends JpaRepository<AppClientProfile, Long> {

    AppClientProfile findByUuid(String uuid);
    boolean existsByAppUser(AppUser appUser);
}
