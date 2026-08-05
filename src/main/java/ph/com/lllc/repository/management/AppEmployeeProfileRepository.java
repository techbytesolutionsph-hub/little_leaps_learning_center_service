package ph.com.lllc.repository.management;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ph.com.lllc.entity.user.common.AppUser;
import ph.com.lllc.entity.user.staff.generalinfo.AppEmployeeProfile;

@Repository
public interface AppEmployeeProfileRepository extends JpaRepository<AppEmployeeProfile, Long> {

    boolean existsByAppUser(AppUser appUser);
}