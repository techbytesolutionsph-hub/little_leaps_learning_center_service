package ph.com.lllc.entity.schedule;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ph.com.lllc.entity.user.client.AppClientProfile;
import ph.com.lllc.entity.user.staff.AppStaffProfile;
import ph.com.lllc.enums.ScheduleStatus;
import ph.com.lllc.enums.SessionFrequency;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "lllc_app_client_therapy_schedule")
@NoArgsConstructor
@AllArgsConstructor
public class ClientTherapySchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "session_id")
    private Long id;

    /**
     * ONCE / TWICE / THRICE
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "frequency")
    private SessionFrequency frequency;

    @Enumerated(EnumType.STRING)
    private ScheduleStatus status;

    private LocalDate effectiveDate;

    private LocalDate expirationDate;

    /**
     * Dynamic schedules
     * Example:
     * MONDAY 8-10
     * WEDNESDAY 1-3
     * FRIDAY 3-5
     */
    @JsonManagedReference("session-slot")
    @OneToMany(mappedBy = "kidSessionSchedule", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TherapySlot> scheduleSlots;

    @JsonBackReference("client-session")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "app_client_id", nullable = false)
    private AppClientProfile appClientProfile;

    @JsonBackReference("user-staff")
    @ManyToOne
    @JoinColumn(name = "therapist_id")
    private AppStaffProfile therapist;
}
