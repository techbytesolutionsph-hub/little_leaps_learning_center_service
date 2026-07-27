package ph.com.lllc.service.security.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ph.com.lllc.entity.user.common.AppUser;
import ph.com.lllc.enums.UserRole;
import ph.com.lllc.repository.AppUserRepository;
import ph.com.lllc.service.security.staff.StaffUserDetails;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClientUserDetailsService implements UserDetailsService {

    private final AppUserRepository appUserRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        AppUser user = appUserRepository.findUserByUsername(username)
                .orElseThrow(() -> {
                    log.warn("Client not found with username: {}", username);
                    return new UsernameNotFoundException("Client not found with username: " + username);
                });

        boolean isClient = user.getUserRole()
                .stream()
                .anyMatch(r ->
                        r.getUserRole().name().equals(UserRole.KID.name()) || r.getUserRole().name().equals(UserRole.PARENT.name())
                );

        if (!isClient) {
            throw new UsernameNotFoundException("Not a client account");
        }

        return new ClientUserDetails(user);
    }
}
