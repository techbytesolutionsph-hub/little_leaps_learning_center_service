package ph.com.lllc.dto.staff.clients;

import lombok.*;
import ph.com.lllc.enums.TherapySlotStatus;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TherapySlotRequest {

    private Long id;
    private String therapySessionId;
    private LocalDate therapyDate;
    private DayOfWeek day;
    private LocalTime startTime;
    private LocalTime endTime;
    private TherapySlotStatus status;
    private String notes;
}
