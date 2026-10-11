package ph.com.lllc.enums.hris;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum InterviewStage {
    PHONE_SCREEN("Phone Screen"),
    HR_INTERVIEW("HR Interview"),
    TECHNICAL_INTERVIEW("Technical Interview"),
    FINAL_INTERVIEW("Final Interview");

    private final String label;
}
