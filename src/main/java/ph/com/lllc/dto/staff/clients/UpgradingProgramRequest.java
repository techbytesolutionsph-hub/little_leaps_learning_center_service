package ph.com.lllc.dto.staff.clients;

import lombok.*;
import ph.com.lllc.enums.AssignmentRole;
import ph.com.lllc.enums.TherapyScheduleStatus;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpgradingProgramRequest {

    private String upgradingProgramId;
    private String clientId;
    private String employeeId;
    private AssignmentRole assignmentRole;
    private TherapyScheduleStatus status;
    private String notes;

    private List<UpgradingProgramSlotRequest> upgradingProgramSlots;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpgradingProgramSlotRequest {
        private LocalDate therapyDate;
        private DayOfWeek day;
        private LocalTime startTime;
        private LocalTime endTime;
    }
}
