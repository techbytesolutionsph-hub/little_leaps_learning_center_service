package ph.com.lllc.repository.management;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ph.com.lllc.entity.user.staff.generalinfo.AppEmployeeProfile;
import ph.com.lllc.entity.user.staff.leave.AppLeaveRequest;
import ph.com.lllc.enums.LeaveRequestStatus;
import ph.com.lllc.enums.LeaveType;

import java.util.List;

@Repository
public interface AppLeaveRequestRepository extends JpaRepository<AppLeaveRequest, Long> {

    List<AppLeaveRequest> findByEmployee(AppEmployeeProfile employee);

    @Query("""
        SELECT COALESCE(SUM(l.requestedDays), 0)
        FROM AppLeaveRequest l
        WHERE l.employee = :employee
          AND l.leaveType = :leaveType
          AND l.status = :status
    """)
    Double getUsedLeaveDays(
            @Param("employee") AppEmployeeProfile employee,
            @Param("leaveType") LeaveType leaveType,
            @Param("status") LeaveRequestStatus status
    );
}
