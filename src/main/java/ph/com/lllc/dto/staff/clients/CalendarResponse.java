package ph.com.lllc.dto.staff.clients;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ph.com.lllc.enums.ScheduleStatus;
import ph.com.lllc.enums.SessionFrequency;
import ph.com.lllc.enums.TherapySlotStatus;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalendarResponse {

    private Long id;
    private String therapySessionId;
    private String upgradingProgramId;
    private String initialAssessmentId;
    private String clientId;
    private String employeeId;
    private String title;
    private String client;
    private String therapist;
    private String caseManager;
    private SessionFrequency frequency;
    private LocalDate date;
    private DayOfWeek day;
    private LocalTime start;
    private LocalTime end;
    private TherapySlotStatus status;
    private ScheduleStatus scheduleStatus;
    private String notes;
}
