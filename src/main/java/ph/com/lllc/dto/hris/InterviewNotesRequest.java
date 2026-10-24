package ph.com.lllc.dto.hris;

import lombok.*;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class InterviewNotesRequest {

    private Long applicantId;
    private Long interviewId;
    private String notes;
}
