package ph.com.lllc.component.permission;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("permission")
@RequiredArgsConstructor
public class ThymeleafPermission {

    private final PermissionUtils permissionUtils;

    public boolean has(HttpSession session, String permission){
        return permissionUtils.hasPermission(session, permission);
    }
}