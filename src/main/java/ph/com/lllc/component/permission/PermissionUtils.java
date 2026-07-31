package ph.com.lllc.component.permission;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PermissionUtils {

    public boolean hasPermission(HttpSession session, String permission) {

        List<String> permissions = (List<String>) session.getAttribute("PERMISSIONS");

        if (permissions == null) {
            return false;
        }

        /* SUPER ADMIN bypass */
        if (permissions.contains("FULL_ACCESS")) {
            return true;
        }

        return permissions.contains(permission);
    }
}