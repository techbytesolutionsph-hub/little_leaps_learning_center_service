package ph.com.lllc.service.api.front;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ph.com.lllc.dto.admin.AppUserResponse;
import ph.com.lllc.service.api.admin.UserAccountService;

import java.util.List;

@RequiredArgsConstructor
@Service
public class PortalFrontService {

    private final UserAccountService userAccountService;

    public List<AppUserResponse> getAllUsers(){
        return userAccountService.getAllUsers();
    }

    public AppUserResponse findByUsername(String username){
        return userAccountService.findByUsername(username);
    }
}
