package ph.com.lllc.service.api.management;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ph.com.lllc.dto.response.CommonResponse;
import ph.com.lllc.dto.staff.management.LeaveRequest;
import ph.com.lllc.entity.user.staff.benefits.AppEmployeeBenefits;
import ph.com.lllc.entity.user.staff.generalinfo.AppEmployeeProfile;
import ph.com.lllc.entity.user.staff.leave.AppLeaveRequest;
import ph.com.lllc.enums.EmployeePosition;
import ph.com.lllc.enums.LeaveRequestStatus;
import ph.com.lllc.enums.LeaveType;
import ph.com.lllc.exception.ServiceException;
import ph.com.lllc.repository.management.AppEmployeeProfileRepository;
import ph.com.lllc.repository.management.AppLeaveRequestRepository;
import ph.com.lllc.service.util.logging.LoggingService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class LeaveRequestService {

    private final AppEmployeeProfileRepository appEmployeeProfileRepository;
    private final AppLeaveRequestRepository appLeaveRequestRepository;
    private final LoggingService loggingService;

    public CommonResponse saveLeaveRequest(String uuid, LeaveRequest request) throws ServiceException {

        AppEmployeeProfile employee = this.findEmployeeByEmployeeId(uuid, request.getEmployeeId());
        AppEmployeeProfile hrManager = appEmployeeProfileRepository.findByEmploymentInformation_Position(EmployeePosition.HUMAN_RESOURCE_MANAGER);

        AppLeaveRequest leaveRequest = new AppLeaveRequest();
        leaveRequest.setEmployee(employee);
        leaveRequest.setHrApprover(hrManager);
        leaveRequest.setLeaveType(request.getLeaveType());
        leaveRequest.setAbsenceDuration(request.getAbsenceDuration());
        leaveRequest.setAvailableBalance(this.getAvailableLeaveBalance(employee, request.getLeaveType()));
        leaveRequest.setStartDate(request.getStartDate());
        leaveRequest.setEndDate(request.getEndDate());
        leaveRequest.setStartTime(request.getStartTime());
        leaveRequest.setEndTime(request.getEndTime());
        leaveRequest.setRequestedDays(request.getRequestedDays());
        leaveRequest.setReturningToWorkOn(request.getReturningToWorkOn());
        leaveRequest.setComment(request.getComment());
        leaveRequest.setStatus(LeaveRequestStatus.PENDING);

        AppLeaveRequest saved = appLeaveRequestRepository.save(leaveRequest);

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("leaveRequest", saved);

        return CommonResponse.builder()
                .returnCode(HttpStatus.OK.value())
                .returnMessage("Philippine holiday saved successfully.")
                .responseBody(responseBody)
                .build();
    }

    public Double getAvailableLeaveBalance(String uuid, String employeeId, LeaveType leaveType) throws ServiceException {
        AppEmployeeProfile employee = this.findEmployeeByEmployeeId(uuid, employeeId);
        return this.getAvailableLeaveBalance(employee, leaveType);
    }

    public Double getAvailableLeaveBalance(AppEmployeeProfile employee, LeaveType leaveType) {
        BigDecimal entitlement = getLeaveEntitlement(employee, leaveType);
        Double used = appLeaveRequestRepository.getUsedLeaveDays(employee, leaveType);

        double entitlementValue = entitlement != null
                ? entitlement.doubleValue()
                : 0.0;

        double usedValue = used != null
                ? used
                : 0.0;

        return Math.max(0.0, entitlementValue - usedValue);
    }

    private BigDecimal getLeaveEntitlement(AppEmployeeProfile employee, LeaveType leaveType) {
        AppEmployeeBenefits benefits = employee.getEmployeeBenefits();

        if (benefits == null) {
            return BigDecimal.ZERO;
        }

        return switch (leaveType) {
            case SICK -> defaultZero(benefits.getSickLeave());
            case VACATION -> defaultZero(benefits.getVacationLeave());
            case PATERNITY -> defaultZero(benefits.getPaternityLeave());
            case MATERNITY -> defaultZero(benefits.getMaternityLeave());
            default -> BigDecimal.ZERO;
        };
    }

    private BigDecimal defaultZero(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }

    private AppEmployeeProfile findEmployeeByEmployeeId(String uuid, String employeeId) throws ServiceException {
        return appEmployeeProfileRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> {
                    loggingService.error(uuid, getClass().getName(),
                            "Employee not found: " + employeeId, HttpStatus.NOT_FOUND.value());
                    return new ServiceException(HttpStatus.NOT_FOUND.value(), "Employee not found: " + employeeId);
                });
    }

    public boolean hasOverlappingLeave(String uuid, String employeeId, LocalDate startDate, LocalDate endDate) throws ServiceException {

        AppEmployeeProfile employee = this.findEmployeeByEmployeeId(uuid, employeeId);
        return appLeaveRequestRepository.existsByEmployeeAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                        employee, endDate, startDate);
    }
}
