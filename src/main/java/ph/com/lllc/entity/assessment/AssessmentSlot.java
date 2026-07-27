package ph.com.lllc.entity.assessment;

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
    private Long id;

    private String teacherName;

    private LocalTime startTime;

    private LocalTime endTime;

    @JsonBackReference("assessment-slot")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assessment_schedule_id", nullable = false)
    private KidAssessmentSchedule assessmentSchedule;
}
