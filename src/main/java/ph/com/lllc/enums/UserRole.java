package ph.com.lllc.enums;

import lombok.Getter;

@Getter
public enum UserRole {
    KID("KID"),
    PARENT("PAR"),
    CASE_MANAGER("CSM"),
    THERAPIST("THR"),
    ACCOUNTING("ACC"),
    SECRETARY("SEC"),
    EMPLOYEE("EMP"),
    HR("HR"),
    SUPER_ADMIN("ADM");

    private final String code;

    UserRole(String code) {
        this.code = code;
    }
}