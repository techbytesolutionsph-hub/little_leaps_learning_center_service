package ph.com.lllc.entity.user.client.assessment;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;

@Getter
@Setter
@Entity
@Table(name = "lllc_app_assessment_slot")
@NoArgsConstructor
@AllArgsConstructor
public class AssessmentSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "assessment_slot_id")
    private Long id;

    /**
     * 8:00 AM
     */
    @Column(name = "start_time")
    private LocalTime startTime;

    /**
     * 10:00 AM
     */
    @Column(name = "end_time")
    private LocalTime endTime;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assessment_schedule_id", nullable = false)
    private ClientInitialAssessmentSchedule assessmentSchedule;
}
