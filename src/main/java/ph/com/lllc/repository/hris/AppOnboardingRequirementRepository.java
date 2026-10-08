package ph.com.lllc.repository.hris;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ph.com.lllc.entity.user.hris.AppOnboardingRequirement;

import java.util.List;

@Repository
public interface AppOnboardingRequirementRepository extends JpaRepository<AppOnboardingRequirement, Long> {
    List<AppOnboardingRequirement> findByActiveTrue();
}
