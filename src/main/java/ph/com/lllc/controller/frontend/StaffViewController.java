package ph.com.lllc.controller.frontend;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping(path = "/lllc/staff")
public class StaffViewController {


    @GetMapping(value = "/login")
    public String loginPage(HttpSession session, Model model){
        model.addAttribute("page", "login");
        return "staff/login/index";
    }

    @GetMapping(value = "/logout")
    public String logoutPage(HttpServletRequest request) {
        SecurityContextHolder.clearContext();
        request.getSession().invalidate();
        return "redirect:/telatak/seller/login";
    }

    @GetMapping(value = "/dashboard")
    public String dashboardPage(Model model) {
        this.setupPage(model, "dashboard", "Dashboard");

        return "staff/dashboard/index";
    }


    private void setupPage(Model model, String nav, String pageTitle) {
        model.addAttribute("page", "seller");
        model.addAttribute("nav", nav);
        model.addAttribute("pageTitle", pageTitle);
    }
}
