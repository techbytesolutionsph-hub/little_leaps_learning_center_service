package ph.com.lllc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ph.com.lllc.entity.user.client.assessment.ClientInitialAssessmentSchedule;

@Repository
public interface ClientInitialAssessmentScheduleRepository extends JpaRepository<ClientInitialAssessmentSchedule, Long> {
}
