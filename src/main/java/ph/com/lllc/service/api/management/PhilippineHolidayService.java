package ph.com.lllc.service.api.management;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import ph.com.lllc.config.properties.PhilippineHolidayPropertiesConfig;
import ph.com.lllc.dto.holiday.NagerHolidayResponse;
import ph.com.lllc.dto.holiday.PhilippineHolidayRequest;
import ph.com.lllc.dto.holiday.PhilippineHolidayResponse;
import ph.com.lllc.dto.response.CommonResponse;
import ph.com.lllc.entity.holiday.PhilippineHoliday;
import ph.com.lllc.enums.holiday.HolidayType;
import ph.com.lllc.repository.holiday.PhilippineHolidayRepository;
import ph.com.lllc.util.ObjectUtils;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PhilippineHolidayService {

    private final PhilippineHolidayRepository holidayRepository;
    private final RestClient restClient;
    private final PhilippineHolidayPropertiesConfig config;

    public CommonResponse saveHolidays(int year) {

        /* Check if holidays for the year already exist */
        if (holidayRepository.existsByDateStartingWith(year + "-")) {
            return CommonResponse.builder()
                    .returnCode(HttpStatus.OK.value())
                    .returnMessage("Philippine holidays for " + year + " already exist in the database.")
                    .build();
        }

        NagerHolidayResponse[] responses = restClient.get()
                .uri(config.getUrl(), year)
                .retrieve()
                .body(NagerHolidayResponse[].class);

        if (responses == null) {
            return CommonResponse.builder()
                    .returnCode(HttpStatus.OK.value())
                    .returnMessage("No holidays found.")
                    .build();
        }

        /* Map Nager holidays */
        List<PhilippineHoliday> holidays = new ArrayList<>(Arrays.stream(responses)
                .filter(response -> {
                    LocalDate date = LocalDate.parse(response.getDate());
                    /* Exclude Nager holidays not recognized as Philippine national holidays */
                    return !isExcludedHoliday(date, year);
                })
                .map(response -> {
                    LocalDate date = LocalDate.parse(response.getDate());

                    PhilippineHoliday holiday = new PhilippineHoliday();
                    holiday.setDate(response.getDate());
                    holiday.setDay(date.getDayOfWeek().toString());
                    holiday.setTagalogName(response.getLocalName());
                    holiday.setEnglishName(response.getName());
                    holiday.setCountryCode(response.getCountryCode());

                    /* Philippine holiday classification */
                    holiday.setType(getHolidayType(date, year));

                    /* Philippine-specific names */
                    setPhilippineHolidayName(holiday, date, year);

                    return holiday;
                })
                .toList());

        /* Add Philippine holidays missing from Nager */
        addMissingPhilippineHolidays(holidays, year);

        /* Save all holidays */
        List<PhilippineHoliday> savedHolidays = holidayRepository.saveAll(holidays);

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("holidays", savedHolidays);

        return CommonResponse.builder()
                .returnCode(HttpStatus.OK.value())
                .returnMessage("Philippine holidays saved successfully.")
                .responseBody(responseBody)
                .build();
    }

    public List<PhilippineHolidayResponse> getPhilippineHolidays(int year) {
        return ObjectUtils.copyListAs(
                holidayRepository.findAll().stream()
                        .filter(holiday -> LocalDate.parse(holiday.getDate()).getYear() == year)
                        .sorted(Comparator.comparing(PhilippineHoliday::getDate))
                        .toList(),
                PhilippineHolidayResponse.class
        );
    }

    public List<PhilippineHolidayResponse> getPhilippineHolidaysByMonth(int month, int year) {
        return ObjectUtils.copyListAs(
                holidayRepository.findAll().stream()
                        .filter(holiday -> {
                            LocalDate date = LocalDate.parse(holiday.getDate());
                            return date.getYear() == year && date.getMonthValue() == month;
                        })
                        .sorted(Comparator.comparing(PhilippineHoliday::getDate))
                        .toList(),
                PhilippineHolidayResponse.class
        );
    }

    public boolean isHoliday(String date) {
        return holidayRepository.existsByDate(date);
    }

    public CommonResponse addPhilippineHoliday(PhilippineHolidayRequest request){
        LocalDate date = LocalDate.parse(request.getDate());

        PhilippineHoliday holiday = new PhilippineHoliday();
        holiday.setDate(request.getDate());
        holiday.setDay(date.getDayOfWeek().toString());
        holiday.setTagalogName(request.getTagalogName());
        holiday.setEnglishName(request.getEnglishName());
        holiday.setCountryCode("PH");
        holiday.setType(request.getType());

        PhilippineHoliday savedHoliday = holidayRepository.save(holiday);

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("holiday", savedHoliday);

        return CommonResponse.builder()
                .returnCode(HttpStatus.CREATED.value())
                .returnMessage("Philippine holiday saved successfully.")
                .responseBody(responseBody)
                .build();
    }

    private HolidayType getHolidayType(LocalDate date, int year) {

        /* Special Non-Working */
        if (date.equals(LocalDate.of(year, 2, 17))
                || date.equals(LocalDate.of(year, 4, 4))
                || date.equals(LocalDate.of(year, 8, 21))
                || date.equals(LocalDate.of(year, 11, 1))
                || date.equals(LocalDate.of(year, 11, 2))
                || date.equals(LocalDate.of(year, 12, 8))
                || date.equals(LocalDate.of(year, 12, 24))
                || date.equals(LocalDate.of(year, 12, 31))) {

            return HolidayType.SPECIAL_NON_WORKING;
        }

        /* Regular Holiday */
        return HolidayType.REGULAR_HOLIDAY;
    }

    /**
     * Nager can contain dates that are not Philippine
     * national holidays.
     */
    private boolean isExcludedHoliday(LocalDate date, int year) {

        /*
         * Nager returns:
         * October 31 - All Saints' Day Eve
         * This is not a Philippine national holiday.
         */
        return date.equals(LocalDate.of(year, 10, 31));
    }

    private void setPhilippineHolidayName(PhilippineHoliday holiday, LocalDate date, int year) {

        if (date.equals(LocalDate.of(year, 2, 17))) {
            holiday.setEnglishName("Chinese New Year");
        }

        if (date.equals(LocalDate.of(year, 2, 25))) {
            holiday.setEnglishName("EDSA People Power Revolution Anniversary");
        }

        if (date.equals(LocalDate.of(year, 4, 4))) {
            holiday.setEnglishName("Black Saturday");
        }

        if (date.equals(LocalDate.of(year, 5, 1))) {
            holiday.setEnglishName("Labor Day");
        }

        if (date.equals(LocalDate.of(year, 8, 21))) {
            holiday.setTagalogName("Araw ng Kamatayan ni Senador Benigno Simeon Ninoy Aquino Jr.");
            holiday.setEnglishName("Ninoy Aquino Day");
        }

        if (date.equals(LocalDate.of(year, 11, 2))) {
            holiday.setEnglishName("All Souls' Day");
        }

        if (date.equals(LocalDate.of(year, 12, 8))) {
            holiday.setEnglishName("Feast of the Immaculate Conception of Mary");
        }

        if (date.equals(LocalDate.of(year, 12, 24))) {
            holiday.setEnglishName("Christmas Eve");
        }

        if (date.equals(LocalDate.of(year, 12, 31))) {
            holiday.setEnglishName("Last Day of the Year");
        }
    }

    private void addMissingPhilippineHolidays(List<PhilippineHoliday> holidays, int year) {

        /*
         * These are Philippine holidays that are not
         * present in the Nager response.
         */
        if (year == 2026) {

            /* All Souls' Day */
            addHoliday(
                    holidays,
                    LocalDate.of(2026, 11, 2),
                    "Araw ng mga Kaluluwa",
                    "All Souls' Day",
                    HolidayType.SPECIAL_NON_WORKING
            );
        }
    }

    private void addHoliday(List<PhilippineHoliday> holidays, LocalDate date,
            String tagalogName, String englishName, HolidayType type) {

        PhilippineHoliday holiday = new PhilippineHoliday();
        holiday.setDate(date.toString());
        holiday.setDay(date.getDayOfWeek().toString());
        holiday.setTagalogName(tagalogName);
        holiday.setEnglishName(englishName);
        holiday.setCountryCode("PH");
        holiday.setType(type);

        holidays.add(holiday);
    }
}