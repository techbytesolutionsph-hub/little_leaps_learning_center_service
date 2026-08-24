package ph.com.lllc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ph.com.lllc.entity.user.client.schedule.ClientTherapySchedule;

@Repository
public interface ClientTherapyScheduleRepository extends JpaRepository<ClientTherapySchedule, Long> {
}
