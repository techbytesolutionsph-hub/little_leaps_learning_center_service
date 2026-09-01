package ph.com.lllc.dto.staff.clients;

import lombok.*;
import ph.com.lllc.enums.AssignmentRole;
import ph.com.lllc.enums.SessionFrequency;
import ph.com.lllc.enums.TherapyScheduleStatus;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TherapySessionRequest {

    private String therapySessionId;
    private String clientId;
    private String employeeId;
    private AssignmentRole assignmentRole;
    private SessionFrequency frequency;
    private TherapyScheduleStatus status;
    private String notes;

    private List<TherapySlotRequest> scheduleSlots;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TherapySlotRequest {
        private LocalDate therapyDate;
        private DayOfWeek day;
        private LocalTime startTime;
        private LocalTime endTime;
    }
}
