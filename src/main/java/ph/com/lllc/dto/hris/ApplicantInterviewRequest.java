package ph.com.lllc.dto.hris;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import ph.com.lllc.enums.ScheduleStatus;
import ph.com.lllc.enums.hris.InterviewMethod;
import ph.com.lllc.enums.hris.InterviewStage;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Applicant Request")
public class ApplicantInterviewRequest {

    private Long applicantId;
    private String interviewerId;
    private InterviewMethod interviewMethod;
    private InterviewStage interviewStage;
    private LocalDate interviewDate;
    private LocalTime interviewTime;
    private ScheduleStatus scheduleStatus;
    private String meetingLink;
    private String notes;

}
