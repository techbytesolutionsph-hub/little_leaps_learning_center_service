package ph.com.lllc.enums.hris;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum InterviewMethod {
    PHONE("Phone"),
    VIDEO("Video"),
    ONSITE("Onsite");

    private final String label;
}