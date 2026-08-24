package ph.com.lllc.service.api.clients;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ph.com.lllc.dto.response.CommonResponse;
import ph.com.lllc.dto.staff.clients.AssignedClientResponse;
import ph.com.lllc.dto.staff.clients.InitialAssessmentRequest;
import ph.com.lllc.dto.staff.clients.InitialAssessmentResponse;
import ph.com.lllc.entity.user.client.AppClientProfile;
import ph.com.lllc.entity.user.client.AppParentGuardian;
import ph.com.lllc.entity.user.client.assessment.AssessmentSlot;
import ph.com.lllc.entity.user.client.assessment.ClientInitialAssessmentSchedule;
import ph.com.lllc.entity.user.client.assignment.AssignmentHistory;
import ph.com.lllc.entity.user.staff.generalinfo.AppEmployeeProfile;
import ph.com.lllc.enums.AssignmentHistoryAction;
import ph.com.lllc.enums.AssignmentRole;
import ph.com.lllc.enums.AssignmentStatus;
import ph.com.lllc.exception.ServiceException;
import ph.com.lllc.repository.ClientInitialAssessmentScheduleRepository;
import ph.com.lllc.repository.ClientProfileRepository;
import ph.com.lllc.service.api.admin.UserAccountService;
import ph.com.lllc.service.db.SequenceGeneratorService;
import ph.com.lllc.service.util.IdGeneratorUtils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@RequiredArgsConstructor
@Service
public class AssessmentScheduleService {

    private final ClientManagementService clientManagementService;
    private final UserAccountService userAccountService;
    private final ClientProfileRepository clientProfileRepository;
    private final ClientInitialAssessmentScheduleRepository clientInitialAssessmentScheduleRepository;
    private final SequenceGeneratorService sequenceGeneratorService;
    private final IdGeneratorUtils idGeneratorUtils;

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");

    @Transactional
    public CommonResponse saveInitialAssessmentSchedule(String uuid, InitialAssessmentRequest request, HttpServletRequest httpRequest) throws ServiceException {

        AppClientProfile clientProfile = clientManagementService.findAppClientProfileByClientId(uuid, request.getClientId());
        AppEmployeeProfile employeeProfile = clientManagementService.findAppEmployeeProfileByEmployeeId(uuid, request.getEmployeeId());

        String year = String.valueOf(request.getAssessmentDate().getYear());
        long nextUserSeq = sequenceGeneratorService.getInitialAssessmentIdNextSequence();
        String initialAssessmentId =  idGeneratorUtils.generateInitialAssessmentId(year, nextUserSeq);

        ClientInitialAssessmentSchedule schedule = new ClientInitialAssessmentSchedule();
        schedule.setInitialAssessmentId(initialAssessmentId);
        schedule.setAssessmentDate(request.getAssessmentDate());
        schedule.setStatus(request.getScheduleStatus());
        schedule.setNotes(request.getNotes());
        schedule.setCaseManager(employeeProfile);

        AssessmentSlot slot = new AssessmentSlot();
        slot.setStartTime(request.getSlots().get(0).getStartTime());
        slot.setEndTime(request.getSlots().get(0).getEndTime());
        slot.setAssessmentSchedule(schedule);
        schedule.getSlots().add(slot);

        schedule.setAppClientProfile(clientProfile);
        clientProfile.getAssessmentSchedules().add(schedule);

        /*
         * Create assignment history
         */
        AssignmentHistory history = new AssignmentHistory();
        history.setAction(AssignmentHistoryAction.UPDATED);
        history.setDescription(this.createDescription(employeeProfile, request.getAssessmentDate(),
                request.getSlots().get(0).getStartTime(), request.getSlots().get(0).getEndTime()));
        history.setAssignee(employeeProfile);
        history.setAssignmentRole(AssignmentRole.PRIMARY_CASE_MANAGER);
        history.setAssignmentStatus(AssignmentStatus.UPDATED);
        history.setChangedBy(userAccountService.getLoggedInEmployee(httpRequest));
        history.setAppClientProfile(clientProfile);

        /*
         * Add history to client
         */
        if (clientProfile.getAssignmentHistories() == null) {
            clientProfile.setAssignmentHistories(new ArrayList<>());
        }

        clientProfile.getAssignmentHistories().add(history);

        clientProfileRepository.save(clientProfile);

        return CommonResponse.builder()
                .returnCode(HttpStatus.CREATED.value())
                .returnMessage("Initial assessment schedule saved successfully!")
                .build();
    }

    public List<InitialAssessmentResponse> getInitialAssessmentSchedule() {
        return clientInitialAssessmentScheduleRepository.findAll()
                .stream()
                .map(this::buildInitialAssessmentResponse)
                .toList();
    }
    private InitialAssessmentResponse buildInitialAssessmentResponse(ClientInitialAssessmentSchedule response){

        AppParentGuardian guardian = response.getAppClientProfile().getAppParentGuardian().get(0);
        AppClientProfile client = response.getAppClientProfile();
        List<AssignmentHistory> assigmentHistory = response.getAppClientProfile().getAssignmentHistories();

        AssignmentHistory updatedHistory =
                response.getAppClientProfile()
                        .getAssignmentHistories()
                        .stream()
                        .filter(history -> history.getAssignmentStatus() == AssignmentStatus.UPDATED)
                        .max(Comparator.comparing(AssignmentHistory::getEventDateTime))
                        .orElse(null);


        return InitialAssessmentResponse.builder()
                .assessmentDate(response.getAssessmentDate())
                .scheduleStatus(response.getStatus())
                .notes(response.getNotes())
                .slots(List.of(InitialAssessmentResponse.InitialAssessmentDto.builder()
                                .startTime(response.getSlots().get(0).getStartTime())
                                .endTime(response.getSlots().get(0).getEndTime())
                        .build()))

                .clientStudentId(client.getClientStudentId())

                .clientId(client.getClientId())
                .clientProfilePicture(client.getProfileImageUrl())
                .clientFullName(client.getFirstName() + " " + client.getLastName())
                .clientBirthDate(response.getAppClientProfile().getBirthDate())
                .clientAge(response.getAppClientProfile().getAge())
                .clientGender(response.getAppClientProfile().getGender())

                .guardianFullName(guardian.getFirstName() + " " + guardian.getLastName())
                .guardianEmail(guardian.getEmail())
                .guardianContactNo(guardian.getContactNumber())

                .dateEnrolled(response.getAppClientProfile().getDateEnrolled())
                .diagnosisConcerns(response.getAppClientProfile().getDiagnosisConcerns())
                .programType(client.getProgramType())
                .assignmentStatus(
                        updatedHistory != null
                                ? updatedHistory.getAssignmentStatus()
                                : null
                )
                .branch(client.getBranch())
                .enrollmentStatus(response.getAppClientProfile().getEnrollmentStatus())

                .employeeId(response.getCaseManager().getEmployeeId())
                .assigneeProfilePicture(response.getCaseManager().getProfileImageUrl())
                .assigneeFullName(response.getCaseManager().getFirstName() + " " + response.getCaseManager().getLastName())
                .assigneePosition(
                        response.getCaseManager() != null
                                ? response.getCaseManager().getEmploymentInformation().getPosition()
                                : "-"
                )
                .assignmentRole(AssignmentRole.PRIMARY_CASE_MANAGER)
                .history(this.buildAssignmentHistories(assigmentHistory))
                .build();
    }

    public List<InitialAssessmentResponse.AssignmentHistoryResponse> buildAssignmentHistories(List<AssignmentHistory> assigmentHistory){
        return assigmentHistory.stream()
                .sorted(Comparator.comparing(
                        AssignmentHistory::getEventDateTime,
                        Comparator.nullsLast(Comparator.reverseOrder())
                ))
                .map(item -> InitialAssessmentResponse.AssignmentHistoryResponse.builder()
                        .description(item.getDescription())
                        .action(item.getAction())
                        .assigneeFullName(
                                item.getAssignee() != null
                                        ? item.getAssignee().getFirstName() + " "
                                        + item.getAssignee().getLastName()
                                        : "-"
                        )
                        .assignmentRole(item.getAssignmentRole())
                        .assignmentStatus(item.getAssignmentStatus())
                        .assignedByFullName(
                                item.getChangedBy() != null
                                        ? item.getChangedBy().getFirstName() + " "
                                        + item.getChangedBy().getLastName()
                                        : "-"
                        )
                        .eventDateTime(item.getEventDateTime())
                        .build()
                )
                .toList();
    }


    public String createDescription(AppEmployeeProfile appEmployeeProfile,
            LocalDate assessmentDate, LocalTime startTime, LocalTime endTime) {

        return "Initial assessment scheduled with "
                + appEmployeeProfile.getFirstName()
                + " "
                + appEmployeeProfile.getLastName()
                + " on "
                + assessmentDate.format(dateFormatter)
                + " from "
                + startTime.format(timeFormatter)
                + " to "
                + endTime.format(timeFormatter)
                + ".";
    }
}
