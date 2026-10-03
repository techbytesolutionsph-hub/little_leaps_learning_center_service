package ph.com.lllc.enums;

import lombok.Getter;

@Getter
public enum UserRole {
    KID("KID"),
    PARENT("PAR"),
    CHIEF_OPERATING_OFFICER("COO"),
    IT_ADMINISTRATOR("ITA"),
    HUMAN_RESOURCE_MANAGER("HRM"),
    SECRETARY("SEC"),
    ACCOUNTING_OFFICER("ACC"),
    BEHAVIORAL_THERAPIST("THR"),
    CASE_MANAGER("CSM"),
    MAINTENANCE_STAFF("MNT"),
    PSYCHOLOGIST("PSY"),
    SPEECH_LANGUAGE_PATHOLOGIST("SLP"),
    OCCUPATIONAL_THERAPIST("OTH"),
    SUPER_ADMIN("ADM");

    private final String code;

    UserRole(String code) {
        this.code = code;
    }
}