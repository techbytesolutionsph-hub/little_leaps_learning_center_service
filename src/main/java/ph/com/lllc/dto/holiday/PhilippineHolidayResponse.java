package ph.com.lllc.dto.holiday;

import lombok.*;
import ph.com.lllc.enums.holiday.HolidayType;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PhilippineHolidayResponse {

    private String date;
    private String day;
    private String tagalogName;
    private String englishName;
    private HolidayType type;
}
