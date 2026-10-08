package ph.com.lllc.repository.hris;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ph.com.lllc.entity.user.hris.AppApplicantOnboarding;

import java.util.Optional;

@Repository
public interface AppApplicantOnboardingRepository extends JpaRepository<AppApplicantOnboarding, Long> {
    Optional<AppApplicantOnboarding> findByApplicantId(Long applicantId);
}
