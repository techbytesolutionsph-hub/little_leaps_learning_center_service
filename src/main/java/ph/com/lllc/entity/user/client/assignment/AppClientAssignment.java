package ph.com.lllc.entity.user.client.assignment;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ph.com.lllc.entity.user.client.AppClientProfile;
import ph.com.lllc.entity.user.staff.generalinfo.AppEmployeeProfile;
import ph.com.lllc.enums.AssignmentRole;
import ph.com.lllc.enums.AssignmentStatus;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(
        name = "lllc_app_client_assignment",
        indexes = {
                @Index(
                        name = "idx_client_assignment_client",
                        columnList = "client_student_id"
                ),
                @Index(
                        name = "idx_client_assignment_client_status",
                        columnList = "client_student_id, status"
                ),
                @Index(
                        name = "idx_client_assignment_assigned_at",
                        columnList = "assigned_at"
                )
        }
)
@NoArgsConstructor
@AllArgsConstructor
public class AppClientAssignment implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "assign_id", nullable = false)
    private Long id;

    @Column(name = "assignment_id", nullable = false)
    private String assignmentId;

    /**
     * Assigned Case Manager
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_manager_id")
    private AppEmployeeProfile caseManager;

    @Enumerated(EnumType.STRING)
    @Column(name = "case_manager_role", nullable = false, length = 50)
    private AssignmentRole caseManagerRole;

    /**
     * Assigned Behavioral Therapist
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "behavioral_therapist_id")
    private AppEmployeeProfile behavioralTherapist;

    @Enumerated(EnumType.STRING)
    @Column(name = "behavioral_therapist_role", nullable = false, length = 50)
    private AssignmentRole behavioralTherapistRole;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AssignmentStatus status;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "assigned_at", nullable = false)
    private LocalDate assignedAt;

    @Column(name = "unassigned_at")
    private LocalDate unassignedAt;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_student_id", nullable = false)
    private AppClientProfile appClientProfile;

}
