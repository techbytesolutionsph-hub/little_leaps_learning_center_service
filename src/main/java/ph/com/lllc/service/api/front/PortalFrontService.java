package ph.com.lllc.service.api.front;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ph.com.lllc.dto.admin.AppUserResponse;
import ph.com.lllc.dto.response.DashboardCardResponse;
import ph.com.lllc.dto.response.DashboardMetricsResponse;
import ph.com.lllc.dto.staff.EmployeeResponse;
import ph.com.lllc.dto.staff.clients.AssignedClientResponse;
import ph.com.lllc.dto.staff.clients.ClientRegistrationResponse;
import ph.com.lllc.dto.staff.clients.InitialAssessmentResponse;
import ph.com.lllc.dto.staff.clients.TherapySessionResponse;
import ph.com.lllc.entity.user.staff.generalinfo.AppEmployeeProfile;
import ph.com.lllc.exception.ServiceException;
import ph.com.lllc.service.api.admin.UserAccountService;
import ph.com.lllc.service.api.clients.AssessmentScheduleService;
import ph.com.lllc.service.api.clients.ClientManagementService;
import ph.com.lllc.service.api.management.EmploymentRegistryService;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class PortalFrontService {

    private final UserAccountService userAccountService;
    private final ClientManagementService clientManagementService;
    private final AssessmentScheduleService assessmentScheduleService;
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

    public DashboardMetricsResponse clientSchedulesKPIs(){
        long initialAssessments = assessmentScheduleService.findAllScheduledInitialAssessments();
        DashboardCardResponse totalInitialAssessments = DashboardCardResponse.builder()
                .value(String.valueOf(initialAssessments))
                .message("Total Scheduled")
                .build();

        long therapySessions = assessmentScheduleService.findAllTherapySessionSchedules();
        DashboardCardResponse totalTherapySessions = DashboardCardResponse.builder()
                .value(String.valueOf(therapySessions))
                .message("Total Scheduled")
                .build();

        long upgradingPrograms = 0L;
        DashboardCardResponse totalUpgradingPrograms = DashboardCardResponse.builder()
                .value(String.valueOf(upgradingPrograms))
                .message("Total Scheduled")
                .build();

        long neurodevAssessments = 0L;
        DashboardCardResponse totalNeurodevAssessments = DashboardCardResponse.builder()
                .value(String.valueOf(neurodevAssessments))
                .message("Total Scheduled")
                .build();

        return DashboardMetricsResponse.builder()
                .totalScheduledInitialAssessments(totalInitialAssessments)
                .totalScheduledTherapySessions(totalTherapySessions)
                .totalScheduledUpgradingPrograms(totalUpgradingPrograms)
                .totalScheduledNeurodevAssessments(totalNeurodevAssessments)
                .build();
    }

    public List<ClientRegistrationResponse> getClientProfiles(){
        return clientManagementService.getClientProfiles();
    }

    public ClientRegistrationResponse getClientProfileByClientId(String uuid) throws ServiceException {
        return clientManagementService.getClientProfileByClientId(uuid);
    }

    public Map<String, String> mapCaseManagerBehavioralTherapist() {
        return clientManagementService.mapCaseManagerBehavioralTherapist();
    }

    public Map<String, String> mapCaseManagers() {
        return clientManagementService.mapCaseManagers();
    }

    public List<AssignedClientResponse> getAssignedClients() {
        return clientManagementService.getAssignedClients();
    }

    public AssignedClientResponse findByAssignmentId(String uuid, String assignmentId) throws ServiceException {
        return clientManagementService.findByAssignmentId(uuid, assignmentId);
    }

    public List<InitialAssessmentResponse> getInitialAssessmentSchedules() {
        return assessmentScheduleService.getInitialAssessmentSchedules();
    }

    public InitialAssessmentResponse findByInitialAssessmentId(String uuid, String initialAssessmentId) throws ServiceException {
        return assessmentScheduleService.findByInitialAssessmentId(uuid, initialAssessmentId);
    }

    public TherapySessionResponse findByTherapySessionId(String uuid, String therapySessionId) throws ServiceException {
        return assessmentScheduleService.findByTherapySessionId(uuid, therapySessionId);
    }

    public List<TherapySessionResponse> getTherapySessionResponse() {
        return assessmentScheduleService.getTherapySessionResponse();
    }
}
