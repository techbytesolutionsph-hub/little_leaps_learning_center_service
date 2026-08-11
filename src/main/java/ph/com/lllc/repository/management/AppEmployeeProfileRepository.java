package ph.com.lllc.repository.management;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ph.com.lllc.entity.user.common.AppUser;
import ph.com.lllc.entity.user.staff.generalinfo.AppEmployeeProfile;
import ph.com.lllc.enums.EmploymentStatus;

import java.util.List;
import java.util.Optional;

@Repository
public interface AppEmployeeProfileRepository extends JpaRepository<AppEmployeeProfile, Long> {

    boolean existsByAppUser(AppUser appUser);
    Optional<AppEmployeeProfile> findByEmployeeId(String employeeId);
    List<AppEmployeeProfile> findByEmploymentInformation_PositionIn(List<String> positions);
    long countByEmploymentInformation_EmploymentStatus(EmploymentStatus employmentStatus);
}