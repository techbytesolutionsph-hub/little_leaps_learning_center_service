package ph.com.lllc.dto.staff.clients;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ph.com.lllc.enums.TherapySlotStatus;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpgradingProgramSlotRequest {

    private Long id;
    private String upgradingProgramId;
    private LocalDate therapyDate;
    private DayOfWeek day;
    private LocalTime startTime;
    private LocalTime endTime;
    private TherapySlotStatus status;
    private String notes;
}
