package ph.com.lllc.dto.staff.management;

import lombok.*;

import java.time.LocalDate;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class WeeklyEndingRequest {

    private String employeeId;
    private LocalDate weekEnding;
}
