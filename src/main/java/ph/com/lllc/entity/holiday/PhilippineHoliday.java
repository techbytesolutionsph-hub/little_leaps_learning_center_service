package ph.com.lllc.entity.holiday;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ph.com.lllc.enums.holiday.HolidayType;

@Getter
@Setter
@Entity
@Table(name = "lllc_app_philippine_holiday")
@NoArgsConstructor
@AllArgsConstructor
public class PhilippineHoliday {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "date")
    private String date;

    @Column(name = "day")
    private String day;

    @Column(name = "tagalog_name")
    private String tagalogName;

    @Column(name = "english_name")
    private String englishName;

    @Column(name = "country_code")
    private String countryCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private HolidayType type;
}
