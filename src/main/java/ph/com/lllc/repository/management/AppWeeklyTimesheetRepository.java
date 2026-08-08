package ph.com.lllc.repository.management;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ph.com.lllc.entity.user.staff.timesheet.AppWeeklyTimesheet;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface AppWeeklyTimesheetRepository extends JpaRepository<AppWeeklyTimesheet, Long> {

    Optional<AppWeeklyTimesheet> findByAppEmployeeProfileEmployeeIdAndWeekEnding(String employeeId, LocalDate weekEnding);
}