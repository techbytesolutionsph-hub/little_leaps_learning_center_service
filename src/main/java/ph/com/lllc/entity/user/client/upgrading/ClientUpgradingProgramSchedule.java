package ph.com.lllc.entity.user.client.upgrading;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ph.com.lllc.entity.user.client.AppClientProfile;
import ph.com.lllc.entity.user.staff.generalinfo.AppEmployeeProfile;
import ph.com.lllc.enums.AssignmentRole;
import ph.com.lllc.enums.ScheduleStatus;
import ph.com.lllc.enums.SessionFrequency;
import ph.com.lllc.enums.TherapyScheduleStatus;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "lllc_app_client_upgrading_program_schedule")
@NoArgsConstructor
@AllArgsConstructor
public class ClientUpgradingProgramSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "program_id")
    private Long id;

    @Column(name = "upgrading_program_id")
    private String upgradingProgramId;

    /**
     * Therapy Schedule status - ACTIVE, COMPLETED, CANCELLED, INACTIVE
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TherapyScheduleStatus status;
    /**
     * Notes/Remarks for client/kid
     */
    @Column(name = "notes", length = 1000)
    private String notes;

    @Enumerated(EnumType.STRING)
    @Column(name = "assignment_role", nullable = false)
    private AssignmentRole assignmentRole;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "case_manager_id")
    private AppEmployeeProfile caseManager;

    /**
     * Dynamic schedules
     * Example:
     * MONDAY 8-10
     * WEDNESDAY 1-3
     * FRIDAY 3-5
     */
    @JsonManagedReference
    @OneToMany(mappedBy = "upgradingProgramSchedule", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UpgradingProgramSlot> upgradingProgramSlots;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "app_client_id", nullable = false)
    private AppClientProfile appClientProfile;
}
