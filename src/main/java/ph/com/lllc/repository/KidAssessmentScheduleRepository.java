package ph.com.lllc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ph.com.lllc.entity.assessment.ClientInitialAssessmentSchedule;

@Repository
public interface KidAssessmentScheduleRepository extends JpaRepository<ClientInitialAssessmentSchedule, Long> {
}
