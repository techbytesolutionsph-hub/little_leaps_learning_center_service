package ph.com.lllc.controller.frontend;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ph.com.lllc.dto.admin.AppUserResponse;
import ph.com.lllc.dto.response.DashboardMetricsResponse;
import ph.com.lllc.dto.staff.EmployeeResponse;
import ph.com.lllc.dto.staff.clients.*;
import ph.com.lllc.entity.user.staff.generalinfo.AppEmployeeProfile;
import ph.com.lllc.exception.ServiceException;
import ph.com.lllc.service.api.front.PortalFrontService;
import ph.com.lllc.service.util.uuid.GenerateUUIDService;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping(path = "/app/portal")
public class PortalViewController {

    private final PortalFrontService portalFrontService;
    private final GenerateUUIDService generateUUIDService;

    @GetMapping(value = "/login")
    public String loginPage(HttpSession session, Model model){
        model.addAttribute("page", "login");
        return "staff/login/index";
    }

    @GetMapping(value = "/logout")
    public String logoutPage(HttpServletRequest request) {
        SecurityContextHolder.clearContext();
        request.getSession().invalidate();
        return "redirect:staff/login/index";
    }

    @GetMapping(value = "/dashboard")
    public String dashboardPage(Model model) {
        this.setupPage(model, "dashboard", "Dashboard");

        return "staff/dashboard/index";
    }

    @GetMapping(value = "/client-management/registry")
    public String clientRegistryPage(Model model) {
        this.setupPage(model, "clients", "Client Registry");

        List<ClientRegistrationResponse> clients = portalFrontService.getClientProfiles();
        model.addAttribute("clients", clients);

        return "staff/clients/registry/index";
    }

    @GetMapping(value = "/client-management/registry/add-client")
    public String addClientRegistryPage(Model model) {
        this.setupPage(model, "clients", "Add Client");

        return "staff/clients/registry/add/index";
    }

    @GetMapping(value = "/client-management/registry/view-client")
    public String viewClientRegistryPage(Model model, @RequestParam("id") String clientId) throws ServiceException {
        this.setupPage(model, "clients", "Client Profile");

        ClientRegistrationResponse client = portalFrontService.getClientProfileByClientId(clientId);
        model.addAttribute("client", client);

        return "staff/clients/registry/view/index";
    }

    @GetMapping(value = "/client-management/registry/edit-client")
    public String editClientRegistryPage(Model model, @RequestParam("id") String clientId) throws ServiceException {
        this.setupPage(model, "clients", "Edit Client");

        ClientRegistrationResponse client = portalFrontService.getClientProfileByClientId(clientId);
        model.addAttribute("client", client);

        return "staff/clients/registry/edit/index";
    }

    @GetMapping(value = "/client-management/assignment")
    public String clientAssignmentPage(Model model) {
        this.setupPage(model, "clients", "Client Assignment");

        List<AssignedClientResponse> assignments =  portalFrontService.getAssignedClients();
        model.addAttribute("assignments", assignments);

        return "staff/clients/assignment/index";
    }

    @GetMapping(value = "/client-management/assignment/view-assignment")
    public String viewClientAssignmentPage(Model model, @RequestParam("id") String assignmentId) throws ServiceException {
        String uuid = generateUUIDService.generateUUID();
        this.setupPage(model, "clients", "View Assign Client");

        AssignedClientResponse assignment = portalFrontService.findByAssignmentId(uuid, assignmentId);
        model.addAttribute("assignment", assignment);

        return "staff/clients/assignment/view/index";
    }

    @GetMapping(value = "/client-management/assignment/edit-assignment")
    public String editClientAssignmentPage(Model model, @RequestParam("id") String assignmentId) throws ServiceException {
        String uuid = generateUUIDService.generateUUID();
        this.setupPage(model, "clients", "Edit Assign Client");

        Map<String, String> caseManager = portalFrontService.mapEmployeesByPositionIn(List.of("Case Manager"));
        model.addAttribute("caseManager", caseManager);

        Map<String, String> therapist = portalFrontService.mapEmployeesByPositionIn(List.of("Case Manager", "Behavioral Therapist"));
        model.addAttribute("therapist", therapist);

        AssignedClientResponse assignment = portalFrontService.findByAssignmentId(uuid, assignmentId);
        model.addAttribute("assignment", assignment);

        return "staff/clients/assignment/edit/index";
    }

    @GetMapping(value = "/client-management/assignment/assign-client")
    public String assignClientPage(Model model) {
        this.setupPage(model, "clients", "Assign Client");

        Map<String, String> caseManager = portalFrontService.mapEmployeesByPositionIn(List.of("Case Manager"));
        model.addAttribute("caseManager", caseManager);

        Map<String, String> therapist = portalFrontService.mapEmployeesByPositionIn(List.of("Case Manager", "Behavioral Therapist"));
        model.addAttribute("therapist", therapist);

        return "staff/clients/assignment/assign/index";
    }

    @GetMapping(value = "/client-management/client-schedule")
    public String clientAssessmentSchedulePage(Model model) {
        this.setupPage(model, "clients", "Client Schedule");

        DashboardMetricsResponse kpi = portalFrontService.clientSchedulesKPIs();
        model.addAttribute("kpi", kpi);

        List<InitialAssessmentResponse> assessments = portalFrontService.getInitialAssessmentSchedules();
        model.addAttribute("assessments", assessments);

        List<TherapySessionResponse> therapies = portalFrontService.getTherapySessionResponse();
        model.addAttribute("therapies", therapies);

        List<UpgradingProgramResponse> upgradePrograms = portalFrontService.getUpgradingProgramResponse();
        model.addAttribute("upgradePrograms", upgradePrograms);

        return "staff/clients/client-schedule/index";
    }

    @GetMapping(value = "/client-management/client-schedule/add-initial-assessment")
    public String addInitialAssessmentSchedulePage(Model model) {
        this.setupPage(model, "clients", "Add Initial Assessment Schedule");

        Map<String, String> caseManagers = portalFrontService.mapCaseManagers();
        model.addAttribute("caseManagers", caseManagers);

        return "staff/clients/client-schedule/initial-assessment/add/index";
    }

    @GetMapping(value = "/client-management/client-schedule/view-initial-assessment")
    public String viewInitialAssessmentSchedulePage(Model model, @RequestParam("id") String initialAssessmentId) throws ServiceException {
        this.setupPage(model, "clients", "View Initial Assessment Schedule");

        InitialAssessmentResponse assessment = portalFrontService.findByInitialAssessmentId("", initialAssessmentId);
        model.addAttribute("assessment", assessment);

        return "staff/clients/client-schedule/initial-assessment/view/index";
    }

    @GetMapping(value = "/client-management/client-schedule/edit-initial-assessment")
    public String editInitialAssessmentSchedulePage(Model model, @RequestParam("id") String initialAssessmentId) throws ServiceException {
        this.setupPage(model, "clients", "Edit Initial Assessment Schedule");

        Map<String, String> caseManagers = portalFrontService.mapCaseManagers();
        model.addAttribute("caseManagers", caseManagers);

        InitialAssessmentResponse assessment = portalFrontService.findByInitialAssessmentId("", initialAssessmentId);
        model.addAttribute("assessment", assessment);

        return "staff/clients/client-schedule/initial-assessment/edit/index";
    }

    @GetMapping(value = "/client-management/client-schedule/add-therapy-session-assignee")
    public String addTherapySessionAssigneePage(Model model) {
        this.setupPage(model, "clients", "Add Therapy Session Assignee");

        Map<String, String> therapists = portalFrontService.mapEmployeesByPositionIn(List.of("Case Manager", "Behavioral Therapist"));
        model.addAttribute("therapists", therapists);

        return "staff/clients/client-schedule/therapy-session/add/assignee/index";
    }

    @GetMapping(value = "/client-management/client-schedule/add-upgrading-program-assignee")
    public String addUpgradingProgramAssigneePage(Model model) {
        this.setupPage(model, "clients", "Add Upgrading Program Assignee");

        Map<String, String> caseManagers = portalFrontService.mapEmployeesByPositionIn(List.of("Case Manager"));
        model.addAttribute("caseManagers", caseManagers);

        return "staff/clients/client-schedule/upgrading-program/add/assignee/index";
    }

    @GetMapping(value = "/client-management/client-schedule/add-therapy-session-schedule")
    public String addTherapySessionSchedulePage(Model model, @RequestParam("id") String therapySessionId) throws ServiceException {
        String uuid = generateUUIDService.generateUUID();
        this.setupPage(model, "clients", "Add Therapy Session Schedule");

        TherapySessionResponse therapy = portalFrontService.findByTherapySessionId(uuid, therapySessionId);
        model.addAttribute("therapy", therapy);

        return "staff/clients/client-schedule/therapy-session/add/schedule/index";
    }

    @GetMapping(value = "/client-management/client-schedule/add-upgrading-program-schedule")
    public String addUpgradeProgramSchedulePage(Model model, @RequestParam("id") String upgradingProgramId) throws ServiceException {
        String uuid = generateUUIDService.generateUUID();
        this.setupPage(model, "clients", "Add Upgrading Program Schedule");

        UpgradingProgramResponse upgradeProgram = portalFrontService.findByUpgradingProgramId(uuid, upgradingProgramId);
        model.addAttribute("upgradeProgram", upgradeProgram);

        return "staff/clients/client-schedule/upgrading-program/add/schedule/index";
    }

    @GetMapping(value = "/attendance/my-attendance")
    public String employeeAttendancePage(Model model, Authentication authentication) throws ServiceException {
        this.setupPage(model, "attendance", "My Timesheet");
        String username = authentication.getName();

        Map<String, Object> userInfo = portalFrontService.getUserInfo(username);
        model.addAttribute("userInfo", userInfo);

        return "staff/common-util/index";
    }

    @GetMapping(value = "/hr-management/employee-registry")
    public String employeeManagementPage(Model model) {
        this.setupPage(model, "management", "Employee Registry");

        List<EmployeeResponse> employees = portalFrontService.getEmployees();
        model.addAttribute("employees", employees);

        DashboardMetricsResponse kpi = portalFrontService.employeeRegistryKPIs();
        model.addAttribute("kpi", kpi);

        return "staff/management/registry/index";
    }

    @GetMapping(value = "/hr-management/employee-registry/add-employee")
    public String addEmployeePage(Model model) {
        this.setupPage(model, "management", "Add Employee");

        return "staff/management/registry/add-employee/index";
    }

    @GetMapping(value = "/hr-management/employee-registry/view-employee/{employeeId}")
    public String viewEmployeePage(Model model, @PathVariable("employeeId") String employeeId) throws ServiceException {
        String uuid = generateUUIDService.generateUUID();
        this.setupPage(model, "management", "Employee Details");

        AppEmployeeProfile employee = portalFrontService.getAppEmployeeProfile(uuid, employeeId);
        model.addAttribute("employee", employee);

        return "staff/management/registry/view-employee/index";
    }

    @GetMapping(value = "/hr-management/employee-registry/edit-employee/{employeeId}")
    public String editEmployeePage(Model model, @PathVariable("employeeId") String employeeId) throws ServiceException {
        String uuid = generateUUIDService.generateUUID();
        this.setupPage(model, "management", "Edit Employee");

        AppEmployeeProfile employee = portalFrontService.getAppEmployeeProfile(uuid, employeeId);
        model.addAttribute("employee", employee);

        return "staff/management/registry/edit-employee/index";
    }

    @GetMapping(value = "/admin/user-account")
    public String administrationPage(Model model) {
        this.setupPage(model, "admin", "User Account");

        List<AppUserResponse> users = portalFrontService.getAllUsers();
        model.addAttribute("users", users);

        return "staff/admin/index";
    }

    @GetMapping(value = "/admin/user-account/add-user")
    public String adminAddUserPage(Model model) {
        this.setupPage(model, "admin", "Add User");

        return "staff/admin/add-user/index";
    }

    @GetMapping(value = "/admin/user-account/view-user/{username}")
    public String adminViewUserPage(Model model, @PathVariable("username") String username) throws ServiceException {
        this.setupPage(model, "admin", "View User");

        AppUserResponse user = portalFrontService.findByUsername(username);
        model.addAttribute("user", user);

        return "staff/admin/view-user/index";
    }

    @GetMapping(value = "/admin/user-account/edit-user/{username}")
    public String adminUpdateUserPage(Model model, @PathVariable("username") String username) throws ServiceException {
        this.setupPage(model, "admin", "Update User");

        AppUserResponse user = portalFrontService.findByUsername(username);
        model.addAttribute("user", user);

        return "staff/admin/update-user/index";
    }


    private void setupPage(Model model, String nav, String pageTitle) {
        model.addAttribute("page", "seller");
        model.addAttribute("nav", nav);
        model.addAttribute("pageTitle", pageTitle);
    }
}
