package ph.com.lllc.service.security.staff;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ph.com.lllc.entity.user.common.AppUser;
import ph.com.lllc.entity.user.staff.AppStaffProfile;
import ph.com.lllc.enums.Gender;

import java.util.Collection;

public class StaffUserDetails implements UserDetails {
    private final AppUser user;
    private final String staffFirstName;
    private final String profileImageUrl;

    public StaffUserDetails(AppUser user) {
        this.user = user;

        AppStaffProfile staff = (user.getAppStaffProfile() != null
                && user.getAppStaffProfile().isActive())
                ? user.getAppStaffProfile()
                : null;

        this.staffFirstName = (staff != null)
                ? staff.getFirstName()
                : "Staff";

        this.profileImageUrl = this.resolveProfileImage(staff);
    }

    private String resolveProfileImage(AppStaffProfile seller) {

        if (seller != null
                && seller.getProfileImageUrl() != null
                && !seller.getProfileImageUrl().isBlank()) {
            return seller.getProfileImageUrl();
        }

        if (seller != null && seller.getGender() != null) {

            if (seller.getGender() == Gender.MALE) {
                return "/img/user/default_male_user.svg";
            }

            if (seller.getGender() == Gender.FEMALE) {
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

    public String getStaffFirstName() {
        return staffFirstName;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }
}
