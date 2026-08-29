package ph.com.lllc.entity.user.client.schedule;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@Entity
@Table(name = "lllc_app_therapy_slot")
@NoArgsConstructor
@AllArgsConstructor
public class TherapySlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "therapy_slot_id")
    private Long id;

    /**
     * Assessment Date
     */
    @Column(name = "therapy_date")
    private LocalDate therapyDate;

    /**
     * MONDAY / WEDNESDAY / FRIDAY
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week")
    private DayOfWeek day;

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

    /**
     * Parent schedule
     */
    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "therapy_session_schedule_id", nullable = false)
    private ClientTherapySchedule therapySchedule;
}
