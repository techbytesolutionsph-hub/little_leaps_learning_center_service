package ph.com.lllc.service.util;

import org.springframework.stereotype.Component;

import java.time.Year;

@Component
public class IdGeneratorUtils {

    private static final String COMPANY_PREFIX = "LLLC";

    private IdGeneratorUtils() {
    }

    /**
     * =========================================================
     * USER ID FORMAT
     * =========================================================
     * LLLC-ADM-0001
     * LLLC-THR-0001
     * LLLC-CSM-0001
     */
    public String generateEmployeeId(String roleCode, long runningCode) {

        return String.format(
                "%s-%s-%04d",
                COMPANY_PREFIX,
                roleCode.toUpperCase(),
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
                "%s-%d-%04d",
                COMPANY_PREFIX,
                year,
                runningCode
        );
    }
}
