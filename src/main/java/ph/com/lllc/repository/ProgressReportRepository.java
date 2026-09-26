package ph.com.lllc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ph.com.lllc.entity.user.client.progressreport.ProgressReport;

import java.util.List;

public interface ProgressReportRepository extends JpaRepository<ProgressReport, Long> {

    List<ProgressReport> findByAssigneeId(String employeeId);
    ProgressReport findByAssigneeIdAndClientId(String employeeId, String clientId);
}