package ph.com.lllc.service.util;

import org.springframework.stereotype.Component;

import java.time.Year;

@Component
public class IdGeneratorUtils {

    private static final String COMPANY_PREFIX = "3LC";

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
