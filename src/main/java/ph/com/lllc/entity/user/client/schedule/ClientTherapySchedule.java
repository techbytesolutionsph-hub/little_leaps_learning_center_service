package ph.com.lllc.entity.user.client.schedule;

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
@Table(name = "lllc_app_client_therapy_schedule")
@NoArgsConstructor
@AllArgsConstructor
public class ClientTherapySchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "session_id")
    private Long id;

    @Column(name = "therapy_session_id")
    private String therapySessionId;

    /**
     * ONCE / TWICE / THRICE
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "frequency")
    private SessionFrequency frequency;

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
    @JoinColumn(name = "therapist_id")
    private AppEmployeeProfile therapist;

    /**
     * Dynamic schedules
     * Example:
     * MONDAY 8-10
     * WEDNESDAY 1-3
     * FRIDAY 3-5
     */
    @JsonManagedReference
    @OneToMany(mappedBy = "therapySchedule", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TherapySlot> scheduleSlots;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "app_client_id", nullable = false)
    private AppClientProfile appClientProfile;
}
