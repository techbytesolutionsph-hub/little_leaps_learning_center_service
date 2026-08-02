package ph.com.lllc.entity.user.staff.timesheet;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "lllc_app_timesheet_entry")
@NoArgsConstructor
@AllArgsConstructor
public class AppTimesheetEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "timesheet_id")
    private AppWeeklyTimesheet timesheet;

    private BigDecimal monday;
    private BigDecimal tuesday;
    private BigDecimal wednesday;
    private BigDecimal thursday;
    private BigDecimal friday;
    private BigDecimal saturday;
    private BigDecimal sunday;

    private BigDecimal totalHours;
}
