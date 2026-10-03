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
public class PortalUserDetailsService implements UserDetailsService {

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
                        r.getUserRole().name().equals(UserRole.SUPER_ADMIN.name())
                                || r.getUserRole().name().equals(UserRole.CHIEF_OPERATING_OFFICER.name())
                                || r.getUserRole().name().equals(UserRole.CASE_MANAGER.name())
                                || r.getUserRole().name().equals(UserRole.IT_ADMINISTRATOR.name())
                                || r.getUserRole().name().equals(UserRole.BEHAVIORAL_THERAPIST.name())
                                || r.getUserRole().name().equals(UserRole.ACCOUNTING_OFFICER.name())
                                || r.getUserRole().name().equals(UserRole.SECRETARY.name())
                                || r.getUserRole().name().equals(UserRole.HUMAN_RESOURCE_MANAGER.name())
                                || r.getUserRole().name().equals(UserRole.MAINTENANCE_STAFF.name())
                                || r.getUserRole().name().equals(UserRole.PSYCHOLOGIST.name())
                                || r.getUserRole().name().equals(UserRole.SPEECH_LANGUAGE_PATHOLOGIST.name())
                                || r.getUserRole().name().equals(UserRole.OCCUPATIONAL_THERAPIST.name())
                );

        if (!isStaff) {
            throw new UsernameNotFoundException("Not a staff account");
        }

        return new PortalUserDetails(user);
    }
}
