package ph.com.lllc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ph.com.lllc.entity.user.client.schedule.ClientTherapySchedule;
import ph.com.lllc.enums.ScheduleStatus;
import ph.com.lllc.enums.TherapyScheduleStatus;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientTherapyScheduleRepository extends JpaRepository<ClientTherapySchedule, Long> {

    Optional<ClientTherapySchedule> findByTherapySessionId(String therapySessionId);
    List<ClientTherapySchedule> findByStatus(TherapyScheduleStatus scheduleStatus);
}
