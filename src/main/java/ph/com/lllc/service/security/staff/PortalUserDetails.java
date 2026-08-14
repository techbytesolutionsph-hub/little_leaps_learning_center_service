package ph.com.lllc.service.security.staff;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ph.com.lllc.entity.user.common.AppPermission;
import ph.com.lllc.entity.user.common.AppRolePermission;
import ph.com.lllc.entity.user.common.AppUser;
import ph.com.lllc.entity.user.common.AppUserRole;
import ph.com.lllc.entity.user.staff.generalinfo.AppEmployeeProfile;
import ph.com.lllc.enums.Gender;
import ph.com.lllc.enums.UserRole;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class PortalUserDetails implements UserDetails {
    private final AppUser user;
    private final String staffFirstName;
    private final String profileImageUrl;

    public PortalUserDetails(AppUser user) {
        this.user = user;

        AppEmployeeProfile staff = (user.getAppEmployeeProfile() != null
                && user.getAppEmployeeProfile().isActive())
                ? user.getAppEmployeeProfile()
                : null;

        this.staffFirstName = (staff != null)
                ? staff.getFirstName()
                : user.getUserRole().stream().anyMatch(role -> role.getUserRole() == UserRole.SUPER_ADMIN)
                ? "Super Admin" : "Staff";

        this.profileImageUrl = this.resolveProfileImage(staff);
    }

    public AppEmployeeProfile getAppEmployeeProfile() {
        return user.getAppEmployeeProfile();
    }

    private String resolveProfileImage(AppEmployeeProfile staff) {

        /* Super Admin profile image */
        if (user.getUserRole().stream()
                .anyMatch(role -> role.getUserRole() == UserRole.SUPER_ADMIN)
                && user.getProfileImageUrl() != null
                && !user.getProfileImageUrl().isBlank()) {

            return user.getProfileImageUrl();
        }

        /* Employee profile image */
        if (staff != null
                && staff.getProfileImageUrl() != null
                && !staff.getProfileImageUrl().isBlank()) {

            return staff.getProfileImageUrl();
        }

        /* Default avatar based on employee gender */
        if (staff != null && staff.getGender() != null) {

            if (staff.getGender() == Gender.MALE) {
                return "/img/user/default_male_user.svg";
            }

            if (staff.getGender() == Gender.FEMALE) {
                return "/img/user/default_female_user.svg";
            }
        }

        /* Generic default */
        return "/img/default.png";
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();

        for (AppUserRole role : user.getUserRole()) {

            /* Add Role */
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getUserRole().name()));

            /* Add Permissions */
            if (role.getRolePermissions() != null) {
                role.getRolePermissions().stream()
                        .map(AppRolePermission::getPermission)
                        .map(AppPermission::getPermissionCode)
                        .map(SimpleGrantedAuthority::new)
                        .forEach(authorities::add);
            }
        }

        return authorities;
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
