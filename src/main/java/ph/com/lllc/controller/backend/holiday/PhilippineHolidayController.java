package ph.com.lllc.controller.backend.holiday;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ph.com.lllc.dto.holiday.PhilippineHolidayRequest;
import ph.com.lllc.dto.holiday.PhilippineHolidayResponse;
import ph.com.lllc.dto.response.CommonResponse;
import ph.com.lllc.service.api.management.PhilippineHolidayService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Philippine Holiday Controller")
@RequestMapping("/api/v1/ph-holiday")
public class PhilippineHolidayController {

    private final PhilippineHolidayService philippineHolidayService;

    @PostMapping("/save-holiday")
    public ResponseEntity<CommonResponse> addPhilippineHoliday(PhilippineHolidayRequest request) {
        return new ResponseEntity<>(philippineHolidayService.addPhilippineHoliday(request), HttpStatus.CREATED);
    }

    @PostMapping("/save-by-year")
    public ResponseEntity<CommonResponse> saveHolidays(@RequestParam int year) {
        return new ResponseEntity<>(philippineHolidayService.saveHolidays(year), HttpStatus.CREATED);
    }

    @GetMapping("/get-holidays-by-year")
    public ResponseEntity<List<PhilippineHolidayResponse>> getPhilippineHolidays(@RequestParam int year) {
        return new ResponseEntity<>(philippineHolidayService.getPhilippineHolidays(year), HttpStatus.OK);
    }

    @GetMapping("/get-holidays-by-month-year")
    public ResponseEntity<List<PhilippineHolidayResponse>> getPhilippineHolidaysByMonth(@RequestParam int month, @RequestParam int year) {
        return new ResponseEntity<>(philippineHolidayService.getPhilippineHolidaysByMonth(month, year), HttpStatus.OK);
    }

    @GetMapping("/is-holiday")
    public ResponseEntity<Boolean> isHoliday(@RequestParam @Parameter(description = "Date to check", example = "2026-02-25") String date) {
        return new ResponseEntity<>(philippineHolidayService.isHoliday(date), HttpStatus.OK);
    }
}
