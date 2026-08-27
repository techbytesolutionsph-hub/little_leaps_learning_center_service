package ph.com.lllc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ph.com.lllc.entity.user.client.schedule.ClientTherapySchedule;
import ph.com.lllc.enums.ScheduleStatus;

import java.util.List;

@Repository
public interface ClientTherapyScheduleRepository extends JpaRepository<ClientTherapySchedule, Long> {

    List<ClientTherapySchedule> findByStatus(ScheduleStatus scheduleStatus);
}
