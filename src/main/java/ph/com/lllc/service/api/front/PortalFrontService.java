package ph.com.lllc.service.api.front;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ph.com.lllc.dto.admin.AppUserResponse;
import ph.com.lllc.dto.response.DashboardCardResponse;
import ph.com.lllc.dto.response.DashboardMetricsResponse;
import ph.com.lllc.dto.staff.EmployeeResponse;
import ph.com.lllc.entity.user.staff.generalinfo.AppEmployeeProfile;
import ph.com.lllc.exception.ServiceException;
import ph.com.lllc.service.api.admin.UserAccountService;
import ph.com.lllc.service.api.management.EmploymentRegistryService;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class PortalFrontService {

    private final UserAccountService userAccountService;
    private final EmploymentRegistryService employmentRegistryService;

    public List<AppUserResponse> getAllUsers(){
        return userAccountService.getAllUsers();
    }

    public AppUserResponse findByUsername(String username) throws ServiceException {
        return userAccountService.findByUsername(username);
    }

    public List<EmployeeResponse> getEmployees() {
        return employmentRegistryService.getEmployees();
    }

    public AppEmployeeProfile getAppEmployeeProfile(String uuid, String employeeId) throws ServiceException {
        return employmentRegistryService.getAppEmployeeProfile(uuid, employeeId);
    }

    public Map<String, Object> getUserInfo(String username) throws ServiceException {
        return userAccountService.getUserInfo(username);
    }

    public DashboardMetricsResponse employeeRegistryKPIs(){

        long employees = employmentRegistryService.getEmployeesCount();
        DashboardCardResponse totalEmployees = DashboardCardResponse.builder()
                .value(String.valueOf(employees))
                .message("All Employees")
                .build();

        long activeEmployees = employmentRegistryService.getActiveEmployeeCount();
        DashboardCardResponse totalActiveEmployees = DashboardCardResponse.builder()
                .value(String.valueOf(activeEmployees))
                .message("Currently Active")
                .build();

        long onLeaveEmployees = employmentRegistryService.getOnLeaveEmployeeCount();
        DashboardCardResponse totalOnLeaveEmployees = DashboardCardResponse.builder()
                .value(String.valueOf(onLeaveEmployees))
                .message("On Leave")
                .build();

        long resignedEmployees = employmentRegistryService.getResignedEmployeeCount();
        DashboardCardResponse totalResignedEmployees = DashboardCardResponse.builder()
                .value(String.valueOf(resignedEmployees))
                .message("Resigned Employees")
                .build();

        return DashboardMetricsResponse.builder()
                .totalEmployees(totalEmployees)
                .totalActiveEmployees(totalActiveEmployees)
                .totalOnLeaveEmployees(totalOnLeaveEmployees)
                .totalResignedEmployees(totalResignedEmployees)
                .build();
    }
}
