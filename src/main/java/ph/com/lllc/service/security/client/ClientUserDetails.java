package ph.com.lllc.service.security.client;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ph.com.lllc.entity.user.client.AppClientProfile;
import ph.com.lllc.entity.user.common.AppUser;
import ph.com.lllc.entity.user.staff.AppStaffProfile;
import ph.com.lllc.enums.Gender;

import java.util.Collection;

public class ClientUserDetails implements UserDetails {
    private final AppUser user;
    private final String clientFirstName;
    private final String profileImageUrl;

    public ClientUserDetails(AppUser user) {
        this.user = user;

        AppClientProfile client = (user.getAppClientProfile() != null
                && user.getAppClientProfile().isActive())
                ? user.getAppClientProfile()
                : null;

        this.clientFirstName = (client != null)
                ? client.getFirstName()
                : "Client";

        this.profileImageUrl = this.resolveProfileImage(client);
    }

    private String resolveProfileImage(AppClientProfile client) {

        if (client != null
                && client.getProfileImageUrl() != null
                && !client.getProfileImageUrl().isBlank()) {
            return client.getProfileImageUrl();
        }

        if (client != null && client.getGender() != null) {

            if (client.getGender() == Gender.MALE) {
                return "/img/user/default_male_user.svg";
            }

            if (client.getGender() == Gender.FEMALE) {
                return "/img/user/default_female_user.svg";
            }
        }

        return "/img/default.png";
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getUserRole()
                .stream()
                .map(role -> new SimpleGrantedAuthority(
                        "ROLE_" + role.getUserRole().name()
                ))
                .toList();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return user.isActive();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return user.isActive();
    }

    public String getClientFirstName() {
        return clientFirstName;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }
}
