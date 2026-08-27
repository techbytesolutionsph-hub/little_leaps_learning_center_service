package ph.com.lllc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ph.com.lllc.entity.user.client.assessment.ClientInitialAssessmentSchedule;
import ph.com.lllc.enums.ScheduleStatus;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientInitialAssessmentScheduleRepository extends JpaRepository<ClientInitialAssessmentSchedule, Long> {

    Optional<ClientInitialAssessmentSchedule> findByInitialAssessmentId(String initialAssessmentId);
    List<ClientInitialAssessmentSchedule> findByStatus(ScheduleStatus scheduleStatus);
}
