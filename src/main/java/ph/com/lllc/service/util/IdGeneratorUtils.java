package ph.com.lllc.service.util;

import org.springframework.stereotype.Component;

import java.time.Year;

@Component
public class IdGeneratorUtils {

    private static final String COMPANY_PREFIX = "3LC";
    private static final String CLIENT_ID_PREFIX = "CLI";
    private static final String ASSIGNMENT_ID_PREFIX = "CA";
    private static final String INITIAL_ASSESSMENT_ID_PREFIX = "IA";
    private static final String ASSESSMENT_ID_PREFIX = "ASCH";

    private IdGeneratorUtils() {
    }

    /**
     * =========================================================
     * USER ID FORMAT
     * =========================================================
     * 3LC-0326-0001
     * 3LC-0326-0002
     * 3LC-0326-0003
     */
    public String generateEmployeeId(String dateHired, long runningCode) {

        return String.format(
                "%s-%s-%04d",
                COMPANY_PREFIX,
                dateHired,
                runningCode
        );
    }

    /**
     * =========================================================
     * CLIENT ID FORMAT
     * =========================================================
     * CLI-000001
     * CLI-000002
     * CLI-000003
     */
    public String generateClientId(long runningCode) {

        return String.format(
                "%s-%06d",
                CLIENT_ID_PREFIX,
                runningCode
        );
    }

    /**
     * =========================================================
     * CLIENT ID FORMAT
     * =========================================================
     * CA-2026-00001
     * CA-2026-00002
     * CA-2026-00003
     */
    public String generateAssignmentId(String assignYear, long runningCode) {

        return String.format(
                "%s-%s-%05d",
                ASSIGNMENT_ID_PREFIX,
                assignYear,
                runningCode
        );
    }

    /**
     * =========================================================
     * CLIENT ID FORMAT
     * =========================================================
     * IA-2026-00001
     * IA-2026-00002
     * IA-2026-00003
     */
    public String generateInitialAssessmentId(String assignYear, long runningCode) {

        return String.format(
                "%s-%s-%05d",
                INITIAL_ASSESSMENT_ID_PREFIX,
                assignYear,
                runningCode
        );
    }

    /**
     * =========================================================
     * CLIENT ID FORMAT
     * =========================================================
     * ASCH-2026-00001
     * ASCH-2026-00002
     * ASCH-2026-00003
     */
    public String generateAssessmentId(String assignYear, long runningCode) {

        return String.format(
                "%s-%s-%05d",
                ASSESSMENT_ID_PREFIX,
                assignYear,
                runningCode
        );
    }

    /**
     * =========================================================
     * KID / CLIENT ID FORMAT
     * =========================================================
     * LLLC-2026-0001
     */
    public String generateKidId(long runningCode) {

        int year = Year.now().getValue();

        return String.format(
                "%s-%d-%06d",
                COMPANY_PREFIX,
                year,
                runningCode
        );
    }
}
