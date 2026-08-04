package ph.com.lllc.entity.user.staff.employmentinfo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ph.com.lllc.entity.user.staff.generalinfo.AppEmployeeProfile;
import ph.com.lllc.enums.EmploymentStatus;
import ph.com.lllc.enums.EmploymentType;

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

    @Column(name = "position")
    private String position;

    @Column(name = "date_hired")
    private LocalDate dateHired;

    @Column(name = "regular_start_date")
    private LocalDate regularStartDate;

    @Column(name = "employee_id")
    private String employeeId;

    @Column(name = "job_title")
    private String jobTitle;

    @Column(name = "address")
    private String address;

    /**
     * Example:
     * Main Branch
     * San Jose Branch
     */
    @Column(name = "branch_assign")
    private String branchAssign;

    @Enumerated(EnumType.STRING)
    @Column(name = "employment_type")
    private EmploymentType employmentType;

    @Enumerated(EnumType.STRING)
    @Column(name = "employment_status")
    private EmploymentStatus employmentStatus;

    @Column(name = "immediate_supervisor")
    private String immediateSupervisor;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_profile_id", unique = true, nullable = false)
    private AppEmployeeProfile appEmployeeProfile;
}
