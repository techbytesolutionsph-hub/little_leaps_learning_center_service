package ph.com.lllc.controller.backend.management;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ph.com.lllc.dto.response.CommonResponse;
import ph.com.lllc.dto.staff.EmployeeRequest;
import ph.com.lllc.dto.staff.EmployeeResponse;
import ph.com.lllc.exception.ServiceException;
import ph.com.lllc.service.api.management.EmploymentRegistryService;
import ph.com.lllc.service.util.logging.LoggingService;
import ph.com.lllc.service.util.uuid.GenerateUUIDService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Employment Registry Controller")
@RequestMapping("/api/v1/management")
public class EmploymentRegistryController {

    private final EmploymentRegistryService employmentRegistryService;
    private final LoggingService loggingService;
    private final GenerateUUIDService generateUUIDService;

    @PostMapping("/create-employee")
    @Operation(summary = "Create Employee")
    public ResponseEntity<CommonResponse> createEmployee(@RequestBody EmployeeRequest request) throws ServiceException {
        String uuid = generateUUIDService.generateUUID();
        loggingService.info(uuid, this.getClass().getName(), "", "EmployeeRequest : " + request.toString());
        return new ResponseEntity<>(employmentRegistryService.createEmployee(uuid, request), HttpStatus.CREATED);
    }

    @PostMapping("/get-employees")
    @Operation(summary = "Get Employees")
    public ResponseEntity<List<EmployeeResponse>> getEmployees(){
        return new ResponseEntity<>(employmentRegistryService.getEmployees(), HttpStatus.OK);
    }
}
