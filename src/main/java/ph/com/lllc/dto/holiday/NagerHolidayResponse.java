package ph.com.lllc.dto.holiday;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class NagerHolidayResponse {

    private String date;
    private String localName;
    private String name;
    private String countryCode;
    private boolean global;
    private List<String> types;
}