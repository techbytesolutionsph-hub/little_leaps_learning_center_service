package ph.com.lllc.entity.user.staff.generalinfo;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ph.com.lllc.entity.user.common.AppUser;
import ph.com.lllc.entity.user.staff.address.AppEmployeeAddress;
import ph.com.lllc.entity.user.staff.benefits.AppEmployeeBenefits;
import ph.com.lllc.entity.user.staff.employmentinfo.AppEmploymentInformation;
import ph.com.lllc.entity.user.staff.payrollinfo.AppPayrollInformation;
import ph.com.lllc.entity.user.staff.timesheet.AppWeeklyTimesheet;
import ph.com.lllc.enums.Gender;
import ph.com.lllc.enums.StaffType;
import ph.com.lllc.util.LocalDateUtils;

import java.io.Serializable;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "lllc_app_employee_profile")
@NoArgsConstructor
@AllArgsConstructor
public class AppEmployeeProfile implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "employee_profile_id")
    private Long id;

    @Column(name = "employee_id", unique = true)
    private String employeeId;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "middle_name")
    private String middleName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "age")
    private BigInteger age;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;

    @Column(name = "email")
    private String email;

    @Column(name = "phone_number")
    private String phoneNumber;

    /**
     * Example:
     * EMPLOYEE
     * TRAINEE
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "staff_type")
    private StaffType staffType;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Column(name = "is_active")
    private boolean isActive = true;

    @Column(name = "profile_completed")
    private boolean profileCompleted = false;

    @Column(name = "email_verified")
    private boolean emailVerified = false;

    @Column(name = "phone_verified")
    private boolean phoneVerified = false;

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

    @OneToMany(mappedBy = "appEmployeeProfile")
    private List<AppWeeklyTimesheet> timesheets;

    @OneToMany(mappedBy = "appEmployeeProfile")
    private List<AppEmployeeAddress> address;

    @OneToOne(mappedBy = "appEmployeeProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    private AppEmploymentInformation employmentInformation;

    @OneToOne(mappedBy = "appEmployeeProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    private AppPayrollInformation payrollInformation;

    @OneToOne(mappedBy = "appEmployeeProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    private AppEmployeeBenefits employeeBenefits;

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
