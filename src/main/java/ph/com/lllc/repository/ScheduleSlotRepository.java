package ph.com.lllc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ph.com.lllc.entity.schedule.TherapySlot;

@Repository
public interface ScheduleSlotRepository extends JpaRepository<TherapySlot, Long> {
}
