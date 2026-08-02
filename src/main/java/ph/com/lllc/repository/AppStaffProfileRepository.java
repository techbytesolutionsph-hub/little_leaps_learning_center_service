package ph.com.lllc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ph.com.lllc.entity.user.staff.generalinfo.AppEmployeeProfile;

@Repository
public interface AppStaffProfileRepository extends JpaRepository<AppEmployeeProfile, Long> {
}