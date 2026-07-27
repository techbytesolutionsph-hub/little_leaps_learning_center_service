package ph.com.lllc.service.security.staff;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ph.com.lllc.entity.user.common.AppUser;
import ph.com.lllc.enums.UserRole;
import ph.com.lllc.repository.AppUserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class StaffUserDetailsService implements UserDetailsService {

    private final AppUserRepository appUserRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        AppUser user = appUserRepository.findUserByUsername(username)
                .orElseThrow(() -> {
                    log.warn("Staff not found with username: {}", username);
                    return new UsernameNotFoundException("Staff not found with username: " + username);
                });

        boolean isStaff = user.getUserRole()
                .stream()
                .anyMatch(r ->
                        r.getUserRole().name().equals(UserRole.ADMIN.name())
                                || r.getUserRole().name().equals(UserRole.CASE_MANAGER.name())
                                || r.getUserRole().name().equals(UserRole.THERAPIST.name())
                                || r.getUserRole().name().equals(UserRole.ACCOUNTING.name())
                                || r.getUserRole().name().equals(UserRole.EMPLOYEE.name())
                );

        if (!isStaff) {
            throw new UsernameNotFoundException("Not a staff account");
        }

        return new StaffUserDetails(user);
    }
}
