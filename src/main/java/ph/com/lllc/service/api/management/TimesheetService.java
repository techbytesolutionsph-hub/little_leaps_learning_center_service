package ph.com.lllc.service.api.management;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ph.com.lllc.dto.response.CommonResponse;
import ph.com.lllc.dto.staff.management.TimesheetEntryResponse;
import ph.com.lllc.dto.staff.management.WeeklyEndingRequest;
import ph.com.lllc.dto.staff.management.WeeklyTimesheetRequest;
import ph.com.lllc.dto.staff.management.WeeklyTimesheetResponse;
import ph.com.lllc.entity.user.staff.generalinfo.AppEmployeeProfile;
import ph.com.lllc.entity.user.staff.timesheet.AppTimesheetEntry;
import ph.com.lllc.entity.user.staff.timesheet.AppWeeklyTimesheet;
import ph.com.lllc.enums.TimesheetStatus;
import ph.com.lllc.exception.ServiceException;
import ph.com.lllc.repository.management.AppEmployeeProfileRepository;
import ph.com.lllc.repository.management.AppWeeklyTimesheetRepository;
import ph.com.lllc.service.util.logging.LoggingService;
import ph.com.lllc.util.LocalDateUtils;

import java.math.BigDecimal;
import java.util.*;

@Service
@RequiredArgsConstructor
public class TimesheetService {

    private final AppWeeklyTimesheetRepository appWeeklyTimesheetRepository;
    private final AppEmployeeProfileRepository appEmployeeProfileRepository;
    private final LoggingService loggingService;

    @Transactional
    public CommonResponse saveTimesheet(String uuid, WeeklyTimesheetRequest request) throws ServiceException {

        AppEmployeeProfile employeeProfile = appEmployeeProfileRepository.findByEmployeeId(request.getEmployeeId())
                .orElseThrow(() -> {
                    loggingService.error(uuid, getClass().getName(),
                            "Employee not found: " + request.getEmployeeId(), HttpStatus.NOT_FOUND.value());
                    return new ServiceException(HttpStatus.NOT_FOUND.value(), "Employee not found: " + request.getEmployeeId());
                });

        /* Find existing timesheet or create new */
        AppWeeklyTimesheet timesheet =
                appWeeklyTimesheetRepository
                        .findByAppEmployeeProfileEmployeeIdAndWeekEnding(
                                request.getEmployeeId(),
                                request.getWeekEnding()
                        )
                        .orElseGet(() -> {
                            AppWeeklyTimesheet newTimesheet = new AppWeeklyTimesheet();
                            newTimesheet.setAppEmployeeProfile(employeeProfile);
                            newTimesheet.setWeekEnding(request.getWeekEnding());
                            return newTimesheet;
                        });

        /* Basic information */
        timesheet.setAppEmployeeProfile(employeeProfile);
        timesheet.setWeekEnding(request.getWeekEnding());
        timesheet.setStatus(request.getStatus());

        /* Approver */
        timesheet.setApproverName(employeeProfile.getEmploymentInformation().getImmediateSupervisor());

        /* Submitted timestamp */
        if (TimesheetStatus.SUBMITTED.equals(request.getStatus())) {

            timesheet.setSubmittedAt(LocalDateUtils.getLocalDateTime());

            /* Clear previous approval/rejection information */
            timesheet.setApprovedAt(null);
            timesheet.setRejectedAt(null);
            timesheet.setApprovalRemarks(null);
        }


        /* Create entries */
        List<AppTimesheetEntry> entries =
                request.getEntries()
                        .stream()
                        .map(entryRequest -> {
                            AppTimesheetEntry entry = new AppTimesheetEntry();
                            entry.setMonday(entryRequest.getMonday());
                            entry.setTuesday(entryRequest.getTuesday());
                            entry.setWednesday(entryRequest.getWednesday());
                            entry.setThursday(entryRequest.getThursday());
                            entry.setFriday(entryRequest.getFriday());
                            entry.setSaturday(entryRequest.getSaturday());
                            entry.setSunday(entryRequest.getSunday());
                            entry.setTotalHours(entryRequest.getTotalHours());

                            entry.setTimesheet(timesheet);

                            return entry;
                        })
                        .toList();

        /*
         * Replace existing entries.
         *
         * orphanRemoval = true will remove
         * the old entries from the database.
         */
        if (timesheet.getEntries() == null) {
            timesheet.setEntries(new ArrayList<>());
        }

        timesheet.getEntries().clear();
        timesheet.getEntries().addAll(entries);

        AppWeeklyTimesheet saved = appWeeklyTimesheetRepository.save(timesheet);

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("timesheet", saved);

        String returnMessage = request.getStatus() == TimesheetStatus.SAVED
                ? "Timesheet saved successfully."
                : "Timesheet submitted successfully.";

        return CommonResponse.builder()
                .returnCode(HttpStatus.OK.value())
                .returnMessage(returnMessage)
                .responseBody(responseBody)
                .build();
    }

    public WeeklyTimesheetResponse getTimesheet(String uuid, WeeklyEndingRequest request) {

        Optional<AppWeeklyTimesheet> optional = appWeeklyTimesheetRepository
                .findByAppEmployeeProfileEmployeeIdAndWeekEnding(request.getEmployeeId(), request.getWeekEnding());

        if (optional.isEmpty()) {
            return WeeklyTimesheetResponse.builder()
                    .employeeId(request.getEmployeeId())
                    .weekEnding(request.getWeekEnding())
                    .status(TimesheetStatus.OPEN)
                    .entries(List.of(
                        TimesheetEntryResponse.builder()
                                .monday(BigDecimal.ZERO)
                                .tuesday(BigDecimal.ZERO)
                                .wednesday(BigDecimal.ZERO)
                                .thursday(BigDecimal.ZERO)
                                .friday(BigDecimal.ZERO)
                                .saturday(BigDecimal.ZERO)
                                .sunday(BigDecimal.ZERO)
                                .totalHours(BigDecimal.ZERO)
                                .build()
                    ))
                    .build();
        } else{
            AppWeeklyTimesheet timesheet = optional.get();

            return WeeklyTimesheetResponse.builder()
                    .id(timesheet.getId())
                    .employeeId(timesheet.getAppEmployeeProfile().getEmployeeId())
                    .employeeName(timesheet.getAppEmployeeProfile().getFirstName() + " "
                            + timesheet.getAppEmployeeProfile().getLastName())
                    .weekEnding(timesheet.getWeekEnding())
                    .status(timesheet.getStatus())
                    .submittedAt(timesheet.getSubmittedAt())
                    .approvedAt(timesheet.getApprovedAt())
                    .rejectedAt(timesheet.getRejectedAt())
                    .approverName(timesheet.getApproverName())
                    .approvalRemarks(timesheet.getApprovalRemarks())
                    .entries(
                        timesheet.getEntries()
                            .stream()
                            .map(entry -> TimesheetEntryResponse.builder()
                                .id(entry.getId())
                                .monday(entry.getMonday())
                                .tuesday(entry.getTuesday())
                                .wednesday(entry.getWednesday())
                                .thursday(entry.getThursday())
                                .friday(entry.getFriday())
                                .saturday(entry.getSaturday())
                                .sunday(entry.getSunday())
                                .totalHours(entry.getTotalHours())
                                .build())
                            .toList()
                    )
                    .build();
        }
    }
}
