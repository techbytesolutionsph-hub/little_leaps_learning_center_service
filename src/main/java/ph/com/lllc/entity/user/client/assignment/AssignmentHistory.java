package ph.com.lllc.entity.user.client.assignment;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ph.com.lllc.entity.user.staff.generalinfo.AppEmployeeProfile;
import ph.com.lllc.enums.AssignmentHistoryAction;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "lllc_app_client_assignment_history",
        indexes = {
                @Index(
                        name = "idx_assignment_history_assignment",
                        columnList = "assignment_id"
                ),
                @Index(
                        name = "idx_assignment_history_event_date",
                        columnList = "event_date_time"
                )
        }
)
@Getter
@Setter
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
     * Previous assignee.
     *
     * Useful when an assignment is reassigned.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "previous_assignee_id")
    private AppEmployeeProfile previousAssignee;

    /**
     * New assignee.
     *
     * Useful when an assignment is created or reassigned.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "new_assignee_id")
    private AppEmployeeProfile newAssignee;

    /**
     * Previous role.
     */
    @Column(name = "previous_role", length = 50)
    private String previousRole;

    /**
     * New role.
     */
    @Column(name = "new_role", length = 50)
    private String newRole;

    /**
     * Previous status.
     */
    @Column(name = "previous_status", length = 30)
    private String previousStatus;

    /**
     * New status.
     */
    @Column(name = "new_status", length = 30)
    private String newStatus;

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
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "assignment_id", nullable = false)
    @JsonBackReference("assignment-history")
    private AppClientAssignment assignment;

    @PrePersist
    protected void onCreate() {
        if (eventDateTime == null) {
            eventDateTime = LocalDateTime.now();
        }
    }
}