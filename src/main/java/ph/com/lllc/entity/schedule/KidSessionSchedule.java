package ph.com.lllc.entity.schedule;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ph.com.lllc.entity.user.client.AppClientProfile;
import ph.com.lllc.enums.SessionFrequency;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "lllc_app_kid_session_schedule")
@NoArgsConstructor
@AllArgsConstructor
public class KidSessionSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "session_id")
    private Long id;

    /**
     * Kid / Student
     */
    @Column(name = "student_fullname")
    private String studentFullName;

    /**
     * Assigned Therapist / Teacher
     */
    @Column(name = "therapist_fullname")
    private String therapistFullName;

    /**
     * Therapy Center Branch
     */
    @Column(name = "branch")
    private String branch;

    /**
     * ONCE / TWICE / THRICE
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "frequency")
    private SessionFrequency frequency;

    /**
     * Dynamic schedules
     * Example:
     * MONDAY 8-10
     * WEDNESDAY 1-3
     * FRIDAY 3-5
     */
    @JsonManagedReference("session-slot")
    @OneToMany(mappedBy = "kidSessionSchedule", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ScheduleSlot> scheduleSlots;

    @JsonBackReference("client-session")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "app_client_id", nullable = false)
    private AppClientProfile appClientProfile;

}
