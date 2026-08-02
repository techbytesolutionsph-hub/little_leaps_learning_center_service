package ph.com.lllc.entity.user.staff.timesheet;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ph.com.lllc.entity.user.staff.generalinfo.AppEmployeeProfile;
import ph.com.lllc.enums.TimesheetStatus;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "lllc_app_weekly_timesheet")
@NoArgsConstructor
@AllArgsConstructor
public class AppWeeklyTimesheet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "week_ending")
    private LocalDate weekEnding;

    @Enumerated(EnumType.STRING)
    private TimesheetStatus status;

    @OneToMany(mappedBy = "timesheet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AppTimesheetEntry> entries;

    @ManyToOne
    @JoinColumn(name = "employee_profile_id", nullable = false)
    private AppEmployeeProfile appEmployeeProfile;
}
