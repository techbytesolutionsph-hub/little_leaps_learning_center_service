package ph.com.lllc.controller.frontend;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ph.com.lllc.dto.admin.AppUserResponse;
import ph.com.lllc.dto.response.DashboardMetricsResponse;
import ph.com.lllc.dto.staff.EmployeeResponse;
import ph.com.lllc.service.api.front.PortalFrontService;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping(path = "/app/portal")
public class PortalViewController {

    private final PortalFrontService portalFrontService;

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

    @GetMapping(value = "/client-management")
    public String clientManagementPage(Model model) {
        this.setupPage(model, "clients", "Client Management");

        return "staff/clients/index";
    }

    @GetMapping(value = "/client-management/add-client")
    public String addClientPage(Model model) {
        this.setupPage(model, "clients", "Add Client");

        return "staff/clients/add/index";
    }

    @GetMapping(value = "/hr-management/employee-registry")
    public String employeeManagementPage(Model model) {
        this.setupPage(model, "staff", "Employee Registry");

        List<EmployeeResponse> employees = portalFrontService.getEmployees();
        model.addAttribute("employees", employees);

        DashboardMetricsResponse kpi = portalFrontService.employeeRegistryKPIs();
        model.addAttribute("kpi", kpi);

        return "staff/management/registry/index";
    }

    @GetMapping(value = "/hr-management/employee-registry/add-employee")
    public String addEmployeePage(Model model) {
        this.setupPage(model, "staff", "Add Employee");

        return "staff/management/registry/add-employee/index";
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
    public String adminViewUserPage(Model model, @PathVariable("username") String username) {
        this.setupPage(model, "admin", "View User");

        AppUserResponse user = portalFrontService.findByUsername(username);
        model.addAttribute("user", user);

        return "staff/admin/view-user/index";
    }

    @GetMapping(value = "/admin/user-account/edit-user/{username}")
    public String adminUpdateUserPage(Model model, @PathVariable("username") String username) {
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
