package ph.com.lllc.entity.user.client.assignment;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ph.com.lllc.entity.user.client.AppClientProfile;
import ph.com.lllc.entity.user.staff.generalinfo.AppEmployeeProfile;
import ph.com.lllc.enums.AssignmentHistoryAction;
import ph.com.lllc.enums.AssignmentRole;
import ph.com.lllc.enums.AssignmentStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(
        name = "lllc_app_client_assignment_history",
        indexes = {
                @Index(
                        name = "idx_assignment_history_client",
                        columnList = "client_student_id"
                ),
                @Index(
                        name = "idx_assignment_history_assignee",
                        columnList = "assignee_id"
                ),
                @Index(
                        name = "idx_assignment_history_event_date",
                        columnList = "event_date_time"
                )
        }
)
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Type of action/event.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false, length = 50)
    private AssignmentHistoryAction action;

    /**
     * Human-readable description of the event.
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * Assignee (Case Manager or Behavioral Therapist)
     *
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignee_id")
    private AppEmployeeProfile assignee;


    @Enumerated(EnumType.STRING)
    @Column(name = "assignment_role", nullable = false, length = 50)
    private AssignmentRole assignmentRole;

    /**
     * Assignment status.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private AssignmentStatus assignmentStatus;

    /**
     * User/employee who performed the action.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "changed_by_id")
    private AppEmployeeProfile changedBy;

    /**
     * When the event happened.
     */
    @Column(name = "event_date_time", nullable = false)
    private LocalDateTime eventDateTime;

    /**
     * Parent assignment.
     */
    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_student_id", nullable = false)
    private AppClientProfile appClientProfile;

    @PrePersist
    protected void onCreate() {
        if (eventDateTime == null) {
            eventDateTime = LocalDateTime.now();
        }
    }
}