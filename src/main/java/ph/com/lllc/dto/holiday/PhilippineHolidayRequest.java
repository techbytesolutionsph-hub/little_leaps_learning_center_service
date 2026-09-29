package ph.com.lllc.dto.holiday;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import ph.com.lllc.enums.holiday.HolidayType;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request object for Philippine holiday")
public class PhilippineHolidayRequest {

    @Schema(
            description = "Holiday date",
            example = "2026-02-25",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String date;

    @Schema(
            description = "Day of the week",
            example = "Wednesday",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String day;

    @Schema(
            description = "Tagalog name of the holiday",
            example = "Anibersaryo ng EDSA People Power",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String tagalogName;

    @Schema(
            description = "English name of the holiday",
            example = "EDSA People Power Revolution Anniversary",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String englishName;

    @Schema(
            description = "Type of holiday",
            example = "SPECIAL_NON_WORKING",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private HolidayType type;
}
