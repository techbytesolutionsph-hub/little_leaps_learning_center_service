package ph.com.lllc.entity.user.staff.employmentinfo;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ph.com.lllc.entity.user.staff.generalinfo.AppEmployeeProfile;
import ph.com.lllc.enums.EmploymentStatus;
import ph.com.lllc.enums.EmploymentType;
import ph.com.lllc.enums.StaffType;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "lllc_app_employment_information")
@NoArgsConstructor
@AllArgsConstructor
public class AppEmploymentInformation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "employee_id")
    private String employeeId;

    @Column(name = "position")
    private String position;

    @Column(name = "date_hired")
    private LocalDate dateHired;

    @Column(name = "regular_start_date")
    private LocalDate regularStartDate;

    @Column(name = "address")
    private String address;

    /**
     * Example:
     * EMPLOYEE
     * TRAINEE
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "staff_type")
    private StaffType staffType;

    @Enumerated(EnumType.STRING)
    @Column(name = "employment_type")
    private EmploymentType employmentType;

    @Enumerated(EnumType.STRING)
    @Column(name = "employment_status")
    private EmploymentStatus employmentStatus;

    /**
     * Example:
     * Main Branch
     * San Jose Branch
     */
    @Column(name = "branch_assign")
    private String branchAssign;

    @Column(name = "immediate_supervisor")
    private String immediateSupervisor;

    @JsonBackReference
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_profile_id", unique = true, nullable = false)
    private AppEmployeeProfile appEmployeeProfile;
}
