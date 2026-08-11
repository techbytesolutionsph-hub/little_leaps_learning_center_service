package ph.com.lllc.entity.user.client.assignment;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import ph.com.lllc.entity.user.client.AppClientProfile;
import ph.com.lllc.entity.user.staff.generalinfo.AppEmployeeProfile;
import ph.com.lllc.enums.AssignmentRole;
import ph.com.lllc.enums.AssignmentStatus;
import ph.com.lllc.enums.DiagnosisConcern;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "lllc_app_client_assignment")
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

    @Enumerated(EnumType.STRING)
    @Column(name = "assignment_role", nullable = false)
    private AssignmentRole assignmentRole;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "diagnosis_concerns", columnDefinition = "jsonb")
    private Set<DiagnosisConcern> diagnosisConcerns;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AssignmentStatus status;

    @Column(name = "branch")
    private String branch;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "assigned_at", nullable = false)
    private LocalDate assignedAt;

    @Column(name = "unassigned_at")
    private LocalDate unassignedAt;

    @OneToMany(mappedBy = "assignment", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("eventDateTime DESC")
    @JsonManagedReference("assignment-history")
    private List<AssignmentHistory> history;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_student_id", nullable = false)
    private AppClientProfile appClientProfile;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "employee_id", nullable = false)
    private AppEmployeeProfile appEmployeeProfile;

}
