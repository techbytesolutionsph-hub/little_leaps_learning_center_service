package ph.com.lllc.dto.hris;

import lombok.*;
import ph.com.lllc.enums.hris.InterviewResult;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class InterviewResultRequest {

    private Long applicantId;
    private Long interviewId;
    private InterviewResult interviewResult;
}
