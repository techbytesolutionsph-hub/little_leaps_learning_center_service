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
     * LLLC-ADM-000001
     * LLLC-THR-000001
     * LLLC-CSM-000001
     */
    public String generateEmployeeId(String roleCode, long runningCode) {

        return String.format(
                "%s-%s-%06d",
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
                "%s-%d-%06d",
                COMPANY_PREFIX,
                year,
                runningCode
        );
    }
}
