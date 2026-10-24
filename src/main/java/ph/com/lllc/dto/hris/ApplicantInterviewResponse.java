package ph.com.lllc.dto.hris;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import ph.com.lllc.entity.user.staff.generalinfo.AppEmployeeProfile;
import ph.com.lllc.enums.ScheduleStatus;
import ph.com.lllc.enums.hris.InterviewMethod;
import ph.com.lllc.enums.hris.InterviewResult;
import ph.com.lllc.enums.hris.InterviewStage;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Applicant Interview Response")
public class ApplicantInterviewResponse {

    private Long id;
    private AppEmployeeProfile interviewer;
    private InterviewMethod interviewMethod;
    private InterviewStage interviewStage;
    private InterviewResult interviewResult;
    private LocalDate interviewDate;
    private LocalTime interviewTime;
    private ScheduleStatus scheduleStatus;
    private String meetingLink;
    private String notes;

}
