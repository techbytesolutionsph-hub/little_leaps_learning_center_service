package ph.com.lllc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ph.com.lllc.entity.user.client.upgrading.ClientUpgradingProgramSchedule;
import ph.com.lllc.enums.ScheduleStatus;

import java.util.List;

@Repository
public interface ClientUpgradingProgramScheduleRepository extends JpaRepository<ClientUpgradingProgramSchedule, Long> {

    List<ClientUpgradingProgramSchedule> findByStatus(ScheduleStatus scheduleStatus);
}
