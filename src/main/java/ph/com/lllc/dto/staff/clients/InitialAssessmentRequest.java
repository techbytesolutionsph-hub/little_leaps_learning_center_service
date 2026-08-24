package ph.com.lllc.dto.staff.clients;


import lombok.*;
import ph.com.lllc.enums.ScheduleStatus;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InitialAssessmentRequest {

    private String clientId;
    private String employeeId;
    private LocalDate assessmentDate;
    private ScheduleStatus scheduleStatus;
    private String notes;

    private List<AssessmentSlotRequest> slots;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AssessmentSlotRequest {
        private LocalTime startTime;
        private LocalTime endTime;
    }
}
