package ph.com.lllc.repository.hris;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ph.com.lllc.entity.user.hris.AppApplicantInterview;

@Repository
public interface AppApplicantInterviewRepository extends JpaRepository<AppApplicantInterview, Long> {
}
