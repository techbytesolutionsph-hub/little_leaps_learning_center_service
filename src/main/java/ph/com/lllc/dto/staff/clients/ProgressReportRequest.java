package ph.com.lllc.dto.staff.clients;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProgressReportRequest {

    private LocalDate reportingPeriodFrom;
    private LocalDate reportingPeriodTo;
    private String notes;

    private String fileUrl;
    private String fileName;
    private Long fileSize;

    private String assigneeId;
    private String clientId;
}
