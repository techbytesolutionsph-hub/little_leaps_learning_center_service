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

    @GetMapping(value = "/staff-management/add-employee")
    public String addStaffPage(Model model) {
        this.setupPage(model, "clients", "Add Staff");

        return "staff/management/add-employee/index";
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


    private void setupPage(Model model, String nav, String pageTitle) {
        model.addAttribute("page", "seller");
        model.addAttribute("nav", nav);
        model.addAttribute("pageTitle", pageTitle);
    }
}
