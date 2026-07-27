package ph.com.lllc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ph.com.lllc.entity.schedule.KidSessionSchedule;

@Repository
public interface KidSessionScheduleRepository extends JpaRepository<KidSessionSchedule, Long> {
}
