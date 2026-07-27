package ph.com.lllc.entity.user.staff;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ph.com.lllc.entity.user.common.AppUser;
import ph.com.lllc.enums.Gender;
import ph.com.lllc.enums.StaffType;
import ph.com.lllc.enums.UserStatus;
import ph.com.lllc.util.LocalDateUtils;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "lllc_app_staff_profile")
@NoArgsConstructor
@AllArgsConstructor
public class AppStaffProfile implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "staff_id")
    private Long staffId;

    @Column(name = "employee_id", unique = true)
    private String employeeId;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "middle_name")
    private String middleName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "email")
    private String email;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Column(name = "complete_address")
    private String completeAddress;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    /**
     * Example:
     *
     * BUSINESS_OWNER
     * CASE_MANAGER
     * SECRETARY
     * BEHAVIORAL_THERAPIST
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "staff_type")
    private StaffType staffType;

    @Column(name = "date_hired")
    private LocalDate dateHired;

    @Column(name = "license_number")
    private String licenseNumber;

    @Column(name = "professional_title")
    private String professionalTitle;

    /**
     * Example:
     * Main Branch
     * San Jose Branch
     */
    @Column(name = "branch_assign")
    private String branchAssign;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private UserStatus status;

    @Column(name = "is_active")
    private boolean isActive = true;

    @Column(name = "profile_completed")
    private boolean profileCompleted = false;

    @Column(name = "email_verified")
    private boolean emailVerified = false;

    @Column(name = "phone_verified")
    private boolean phoneVerified = false;

    @Column(name = "specialization")
    private String specialization;

    @Column(name = "notes_remarks", length = 1000)
    private String notesRemarks;

    @Column(name = "creation_date")
    private LocalDate creationDate;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "last_modification_date")
    private LocalDate lastModificationDate;

    @Column(name = "last_modified_by")
    private String lastModifiedBy;

    @JsonBackReference("user-staff")
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "app_user_id", unique = true, nullable = false)
    private AppUser appUser;

    @PrePersist
    private void prePersist() {
        creationDate = LocalDateUtils.getLocalDate();
        createdBy = "SYSTEM";
    }

    @PreUpdate
    private void preUpdate() {
        lastModificationDate = LocalDateUtils.getLocalDate();
        lastModifiedBy = "SYSTEM";
    }

}
