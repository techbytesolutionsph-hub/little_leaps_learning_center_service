package ph.com.lllc.service.api.front;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ph.com.lllc.dto.admin.AppUserResponse;
import ph.com.lllc.dto.hris.ApplicantResponse;
import ph.com.lllc.dto.response.DashboardCardResponse;
import ph.com.lllc.dto.response.DashboardMetricsResponse;
import ph.com.lllc.dto.staff.EmployeeResponse;
import ph.com.lllc.dto.staff.clients.*;
import ph.com.lllc.entity.user.staff.generalinfo.AppEmployeeProfile;
import ph.com.lllc.enums.EmployeePosition;
import ph.com.lllc.enums.hris.ApplicantStatus;
import ph.com.lllc.exception.ServiceException;
import ph.com.lllc.service.api.admin.UserAccountService;
import ph.com.lllc.service.api.clients.AssessmentScheduleService;
import ph.com.lllc.service.api.clients.ClientManagementService;
import ph.com.lllc.service.api.hris.RecruitmentService;
import ph.com.lllc.service.api.management.EmploymentRegistryService;
import ph.com.lllc.util.ObjectUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class PortalFrontService {

    private final UserAccountService userAccountService;
    private final ClientManagementService clientManagementService;
    private final AssessmentScheduleService assessmentScheduleService;
    private final EmploymentRegistryService employmentRegistryService;
    private final RecruitmentService recruitmentService;

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

    public DashboardMetricsResponse clientSchedulesKPIs() throws ServiceException {
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

        long upgradingPrograms = assessmentScheduleService.findAllUpgradingProgramSchedules();
        DashboardCardResponse totalUpgradingPrograms = DashboardCardResponse.builder()
                .value(String.valueOf(upgradingPrograms))
                .message("Total Scheduled")
                .build();

        long neurodevAssessments = assessmentScheduleService.findAllNeurodevAssessments();
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

    public Map<String, String> mapEmployeesByPositionIn(List<EmployeePosition> positions) {
        return clientManagementService.mapEmployeesByPositionIn(positions);
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

    public List<UpgradingProgramResponse> getUpgradingProgramResponse() {
        return assessmentScheduleService.getUpgradingProgramResponse();
    }

    public UpgradingProgramResponse findByUpgradingProgramId(String uuid, String upgradingProgramId) throws ServiceException {
        return assessmentScheduleService.findByUpgradingProgramId(uuid, upgradingProgramId);
    }

    public List<NeurodevAssessmentResponse> getNeurodevAssessments() throws ServiceException {
        return assessmentScheduleService.getNeurodevAssessments();
    }

    public NeurodevAssessmentResponse getNeurodevAssessments(String uuid, Long id) throws ServiceException {
        return assessmentScheduleService.getNeurodevAssessments(uuid, id);
    }

    public List<InitialAssessmentResponse> findAllClientInitialAssessmentScheduleByEmployeeId(String employeeId) {
        return assessmentScheduleService.findAllClientInitialAssessmentScheduleByEmployeeId(employeeId);
    }

    public List<TherapySessionResponse> findAllClientTherapyScheduleByEmployeeId(String employeeId) throws ServiceException {
        return assessmentScheduleService.findAllClientTherapyScheduleByEmployeeId(employeeId);
    }

    public List<CalendarResponse> findAllClientInitialAssessmentSlotByEmployeeId(String employeeId) {
        return assessmentScheduleService.findAllClientInitialAssessmentSlotByEmployeeId(employeeId);
    }

    public List<CalendarResponse> findAllTherapySlotsByEmployeeId(String employeeId) {
        return assessmentScheduleService.findAllTherapySlotsByEmployeeId(employeeId);
    }

    public List<CalendarResponse> findAllUpgradingProgramSlotsByEmployeeId(String employeeId) {
        return assessmentScheduleService.findAllUpgradingProgramSlotsByEmployeeId(employeeId);
    }

    public List<UpgradingProgramResponse> findAllClientUpgradingProgramScheduleByEmployeeId(String employeeId) throws ServiceException {
        return assessmentScheduleService.findAllClientUpgradingProgramScheduleByEmployeeId(employeeId);
    }

    public List<ProgressReportResponse> getProgressReportsByEmployeeId(String uuid, String employeeId) throws ServiceException {
        return clientManagementService.getProgressReportsByEmployeeId(uuid, employeeId);
    }

    public ProgressReportResponse findByProgressReportsId(String uuid, String progressReportId) throws ServiceException {
        return clientManagementService.findByProgressReportsId(uuid, progressReportId);
    }

    public List<ClientDetailsResponse> getClientDetails(String employeeId) throws ServiceException {
        return clientManagementService.getClientDetails(employeeId);
    }

    public List<ApplicantResponse> getAllApplicants() {
        return recruitmentService.getAllApplicants();
    }

    public ApplicantResponse getApplicant(Long applicantId) throws ServiceException {
        return ObjectUtils.copyAs(recruitmentService.getApplicant(applicantId), ApplicantResponse.class);
    }

    public List<ApplicantResponse> getAllAppliedAndScheduledApplicants() {
        return recruitmentService.getAllAppliedAndScheduledApplicants();
    }

    public List<ApplicantResponse> findByAppliedStage(ApplicantStatus stage) {
        return recruitmentService.findByAppliedStage(stage);
    }

    public DashboardMetricsResponse candidateKPIs() {
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.withDayOfMonth(1);
        LocalDate endDate = today.withDayOfMonth(today.lengthOfMonth());

        int quarter = (LocalDate.now().getMonthValue() - 1) / 3 + 1;
        int currentQuarter = (today.getMonthValue() - 1) / 3;
        LocalDate startQuarterDate = LocalDate.of(today.getYear(), currentQuarter * 3 + 1, 1);
        LocalDate endQuarterDate = startDate.plusMonths(3).minusDays(1);


        long candidates = recruitmentService.getAllApplicants().size();
        long totalCandidatesCount = recruitmentService.countByAppliedDateBetween(startDate, endDate);
        DashboardCardResponse totalCandidates = DashboardCardResponse.builder()
                .value(String.valueOf(candidates))
                .message("+ " + totalCandidatesCount + " this month")
                .build();

        long inInterview = recruitmentService.countAllByApplicantStatus(ApplicantStatus.INTERVIEW);
        long totalInInterviewCount = recruitmentService.countByApplicantStatusAndAppliedDateBetween(ApplicantStatus.INTERVIEW, startDate, endDate);
        DashboardCardResponse totalInInterviews = DashboardCardResponse.builder()
                .value(String.valueOf(inInterview))
                .message(totalInInterviewCount + " scheduled this month")
                .build();

        long offered = recruitmentService.countAllByApplicantStatus(ApplicantStatus.OFFERED);
        long totalOfferedCount = recruitmentService.countByApplicantStatusAndAppliedDateBetween(ApplicantStatus.OFFERED, startDate, endDate);
        DashboardCardResponse totalOffered = DashboardCardResponse.builder()
                .value(String.valueOf(offered))
                .message(totalOfferedCount + " offered this month")
                .build();

        long hired = recruitmentService.countAllByApplicantStatus(ApplicantStatus.HIRED);
        long totalHiredCount = recruitmentService.countByApplicantStatusAndAppliedDateBetween(ApplicantStatus.HIRED, startQuarterDate, endQuarterDate);
        DashboardCardResponse totalHired = DashboardCardResponse.builder()
                .value(String.valueOf(hired))
                .message("+ " + totalHiredCount + " vs Q" + quarter)
                .build();

        return DashboardMetricsResponse.builder()
                .totalCandidates(totalCandidates)
                .totalInInterviews(totalInInterviews)
                .totalOffered(totalOffered)
                .totalHiredThisQuarter(totalHired)
                .build();
    }
}
