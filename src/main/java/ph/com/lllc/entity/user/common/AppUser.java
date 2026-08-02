package ph.com.lllc.entity.user.common;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ph.com.lllc.entity.user.client.AppClientProfile;
import ph.com.lllc.entity.user.staff.generalinfo.AppEmployeeProfile;
import ph.com.lllc.enums.UserStatus;
import ph.com.lllc.util.LocalDateUtils;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "lllc_app_user")
@NoArgsConstructor
@AllArgsConstructor
public class AppUser implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "app_user_id")
    private Long appUserId;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "last_password")
    private String lastPassword;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Enumerated(EnumType.STRING)
    private UserStatus status;

    @Column(name = "is_active")
    private boolean isActive = true;

    @Column(name = "attempts")
    private int attempts;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @Column(name = "creation_date")
    private LocalDateTime creationDate;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "last_modification_date")
    private LocalDateTime lastModificationDate;

    @Column(name = "last_modified_by")
    private String lastModifiedBy;

    @JsonManagedReference("user-role")
    @OneToMany(mappedBy = "appUser", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AppUserRole> userRole;

    @JsonManagedReference("user-client")
    @OneToOne(mappedBy = "appUser", cascade = CascadeType.ALL, orphanRemoval = true)
    private AppClientProfile appClientProfile;

    @JsonManagedReference("user-staff")
    @OneToOne(mappedBy = "appUser", cascade = CascadeType.ALL, orphanRemoval = true)
    private AppEmployeeProfile appEmployeeProfile;

    @PrePersist
    private void prePersist() {
        creationDate = LocalDateUtils.getLocalDateTime();
        createdBy = "SYSTEM";
    }

    @PreUpdate
    private void preUpdate() {
        lastModificationDate = LocalDateUtils.getLocalDateTime();
        lastModifiedBy = "SYSTEM";
    }

}
