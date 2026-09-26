package ph.com.lllc.dto.staff.clients;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProgressReportResponse {

    private Long id;
    private String progressReportId;
    private LocalDate reportingPeriodFrom;
    private LocalDate reportingPeriodTo;
    private String notes;
    private String fileUrl;
    private String fileName;
    private Long fileSize;
    private String assigneeId;
    private String assigneeProfileImageUrl;
    private String assigneeFullName;
    private String assigneePosition;
    private String clientId;
    private String clientProfileImageUrl;
    private String clientFullName;
    private String clientContactNumber;
    private String createdBy;
    private LocalDateTime creationDate;
}
