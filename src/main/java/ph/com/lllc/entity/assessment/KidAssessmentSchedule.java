package ph.com.lllc.entity.assessment;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import ph.com.lllc.entity.user.client.AppClientProfile;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "lllc_app_kid_assessment_schedule")
@NoArgsConstructor
@AllArgsConstructor
public class KidAssessmentSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "assessment_id")
    private Long id;

    @Column(name = "student_fullname")
    private String studentFullName;

    @Column(name = "branch")
    private String branch;

    @Column(name = "assessment_date")
    private String assessmentDate;

    @JsonManagedReference("assessment-slot")
    @OneToMany(mappedBy = "assessmentSchedule", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AssessmentSlot> slots = new ArrayList<>();

    @JsonBackReference("client-assessment-schedule")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "app_client_id")
    private AppClientProfile appClientProfile;
}
