package ph.com.lllc.entity.assessment;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import ph.com.lllc.entity.user.client.AppClientProfile;
import ph.com.lllc.entity.user.staff.generalinfo.AppEmployeeProfile;
import ph.com.lllc.enums.ScheduleStatus;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "lllc_app_client_initial_assessment_schedule")
@NoArgsConstructor
@AllArgsConstructor
public class ClientInitialAssessmentSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "assessment_id")
    private Long id;

    /**
     * Assessment Date
     */
    @Column(name = "assessment_date")
    private LocalDate assessmentDate;

    /**
     * Branch / Venue
     */
    @Column(name = "venue")
    private String venue;

    /**
     * Schedule status - ACTIVE, COMPLETED, CANCELLED, ON HOLD
     */
    @Enumerated(EnumType.STRING)
    private ScheduleStatus status;

    /**
     * Remarks for client/kid
     */
    @Column(name = "remarks", length = 1000)
    private String remarks;

    @JsonManagedReference
    @OneToMany(mappedBy = "assessmentSchedule", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AssessmentSlot> slots = new ArrayList<>();

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "app_client_id")
    private AppClientProfile appClientProfile;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "therapist_id")
    private AppEmployeeProfile therapist;
}
