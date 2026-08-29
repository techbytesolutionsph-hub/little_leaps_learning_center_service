package ph.com.lllc.service.api.clients;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ph.com.lllc.dto.response.CommonResponse;
import ph.com.lllc.dto.staff.clients.InitialAssessmentRequest;
import ph.com.lllc.dto.staff.clients.InitialAssessmentResponse;
import ph.com.lllc.dto.staff.clients.TherapySessionRequest;
import ph.com.lllc.dto.staff.clients.TherapySessionResponse;
import ph.com.lllc.entity.user.client.AppClientProfile;
import ph.com.lllc.entity.user.client.AppParentGuardian;
import ph.com.lllc.entity.user.client.assessment.AssessmentSlot;
import ph.com.lllc.entity.user.client.assessment.ClientInitialAssessmentSchedule;
import ph.com.lllc.entity.user.client.assignment.AppClientAssignment;
import ph.com.lllc.entity.user.client.assignment.AssignmentHistory;
import ph.com.lllc.entity.user.client.schedule.ClientTherapySchedule;
import ph.com.lllc.entity.user.staff.generalinfo.AppEmployeeProfile;
import ph.com.lllc.enums.AssignmentHistoryAction;
import ph.com.lllc.enums.AssignmentRole;
import ph.com.lllc.enums.AssignmentStatus;
import ph.com.lllc.enums.ScheduleStatus;
import ph.com.lllc.exception.ServiceException;
import ph.com.lllc.repository.*;
import ph.com.lllc.service.api.admin.UserAccountService;
import ph.com.lllc.service.db.SequenceGeneratorService;
import ph.com.lllc.service.util.IdGeneratorUtils;
import ph.com.lllc.service.util.logging.LoggingService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AssessmentScheduleService {

    private final ClientManagementService clientManagementService;
    private final UserAccountService userAccountService;
    private final ClientProfileRepository clientProfileRepository;
    private final ClientInitialAssessmentScheduleRepository clientInitialAssessmentScheduleRepository;
    private final ClientTherapyScheduleRepository clientTherapyScheduleRepository;
    private final ClientUpgradingProgramScheduleRepository clientUpgradingProgramScheduleRepository;
    private final NeurodevelopmentalAssessmentRepository neurodevelopmentalAssessmentRepository;
    private final SequenceGeneratorService sequenceGeneratorService;
    private final IdGeneratorUtils idGeneratorUtils;
    private final LoggingService loggingService;

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

    public List<InitialAssessmentResponse> getInitialAssessmentSchedules() {
        return clientInitialAssessmentScheduleRepository.findAll()
                .stream()
                .map(this::buildInitialAssessmentResponse)
                .toList();
    }

    public InitialAssessmentResponse getInitialAssessmentSchedule(String uuid, String initialAssessmentId) throws ServiceException {
        return this.findByInitialAssessmentId(uuid, initialAssessmentId);
    }

    public InitialAssessmentResponse findByInitialAssessmentId(String uuid, String initialAssessmentId) throws ServiceException {
        ClientInitialAssessmentSchedule schedule = clientInitialAssessmentScheduleRepository.findByInitialAssessmentId(initialAssessmentId)
                .orElseThrow(() -> {
                    loggingService.error(uuid, getClass().getName(), "Initial assessment schedule not found with ID: " + initialAssessmentId, HttpStatus.NOT_FOUND.value());
                    return new ServiceException(HttpStatus.NOT_FOUND.value(), "Initial assessment schedule not found with ID: " + initialAssessmentId);
                });
        return this.buildInitialAssessmentResponse(schedule);
    }

    @Transactional
    public CommonResponse updateInitialAssessmentSchedule(String uuid,
            InitialAssessmentRequest request, HttpServletRequest httpRequest) throws ServiceException {

        AppClientProfile clientProfile = clientManagementService.findAppClientProfileByClientId(uuid, request.getClientId());

        AppEmployeeProfile employeeProfile = clientManagementService.findAppEmployeeProfileByEmployeeId(uuid, request.getEmployeeId());

        ClientInitialAssessmentSchedule schedule = clientInitialAssessmentScheduleRepository.findByInitialAssessmentId(request.getInitialAssessmentId())
                        .orElseThrow(() -> new ServiceException(HttpStatus.NOT_FOUND.value(), "Initial assessment schedule not found: " + request.getInitialAssessmentId()));

        /*
         * Update schedule
         */
        schedule.setAssessmentDate(request.getAssessmentDate());
        schedule.setStatus(request.getScheduleStatus());
        schedule.setNotes(request.getNotes());
        schedule.setCaseManager(employeeProfile);

        /*
         * Update assessment slot
         */
        if (request.getSlots() == null || request.getSlots().isEmpty()) {
            throw new ServiceException("Assessment slot is required.");
        }

        InitialAssessmentRequest.AssessmentSlotRequest slotRequest = request.getSlots().get(0);
        AssessmentSlot slot = schedule.getSlots().get(0);

        slot.setStartTime(slotRequest.getStartTime());
        slot.setEndTime(slotRequest.getEndTime());

        slot.setAssessmentSchedule(schedule);
        schedule.getSlots().add(slot);

        /*
         * Make sure the schedule belongs to the requested client
         */
        if (schedule.getAppClientProfile() == null
                || !schedule.getAppClientProfile().equals(clientProfile)) {
            throw new ServiceException(
                    "Initial assessment schedule does not belong to the specified client.");
        }

        /*
         * Create assignment history
         */
        AssignmentHistory history = new AssignmentHistory();
        history.setAction(AssignmentHistoryAction.UPDATED);
        history.setDescription(this.createDescription(
                employeeProfile,
                request.getAssessmentDate(),
                slotRequest.getStartTime(),
                slotRequest.getEndTime())
        );

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

        /*
         * Save
         */
        clientInitialAssessmentScheduleRepository.save(schedule);
        clientProfileRepository.save(clientProfile);

        return CommonResponse.builder()
                .returnCode(HttpStatus.OK.value())
                .returnMessage("Initial assessment schedule updated successfully!")
                .build();
    }

    public CommonResponse saveTherapySessionDetails(String uuid, TherapySessionRequest request, HttpServletRequest httpRequest) throws ServiceException {

        AppClientProfile clientProfile = clientManagementService.findAppClientProfileByClientId(uuid, request.getClientId());

        AppEmployeeProfile employeeProfile = clientManagementService.findAppEmployeeProfileByEmployeeId(uuid, request.getEmployeeId());

        String year = String.valueOf(LocalDate.now().getYear());
        long nextUserSeq = sequenceGeneratorService.getAssessmentIdNextSequence();
        String therapySessionId =  idGeneratorUtils.generateAssessmentId(year, nextUserSeq);

        ClientTherapySchedule schedule = new ClientTherapySchedule();
        schedule.setTherapySessionId(therapySessionId);
        schedule.setAssignmentRole(request.getAssignmentRole());
        schedule.setFrequency(request.getFrequency());
        schedule.setStatus(request.getStatus());
        schedule.setNotes(request.getNotes());
        schedule.setTherapist(employeeProfile);

        schedule.setAppClientProfile(clientProfile);
        clientProfile.getSessionSchedules().add(schedule);

        /*
         * Create assignment history
         */
        AssignmentHistory history = new AssignmentHistory();
        history.setAction(AssignmentHistoryAction.ASSIGNED);
        history.setDescription("Therapy session assigned to " + employeeProfile.getFirstName() + " " + employeeProfile.getLastName());
        history.setAssignee(employeeProfile);
        history.setAssignmentRole(request.getAssignmentRole());
        history.setAssignmentStatus(AssignmentStatus.ASSIGNED);
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
                .returnMessage("Therapy session assigned successfully!")
                .build();
    }

    public TherapySessionResponse findByTherapySessionId(String uuid, String therapySessionId) throws ServiceException {
        ClientTherapySchedule schedule = clientTherapyScheduleRepository.findByTherapySessionId(therapySessionId)
                .orElseThrow(() -> {
                    loggingService.error(uuid, getClass().getName(), "Therapy session schedule not found with ID: " + therapySessionId, HttpStatus.NOT_FOUND.value());
                    return new ServiceException(HttpStatus.NOT_FOUND.value(), "Therapy session schedule not found with ID: " + therapySessionId);
                });
        return this.buildTherapySessionResponse(schedule);
    }

    public long findAllScheduledInitialAssessments() {
        List<InitialAssessmentResponse> responses = new ArrayList<>();
        List<ClientInitialAssessmentSchedule> schedules = clientInitialAssessmentScheduleRepository.findByStatus(ScheduleStatus.SCHEDULED);
        for (ClientInitialAssessmentSchedule schedule : schedules) {
            InitialAssessmentResponse response = this.buildInitialAssessmentResponse(schedule);
            responses.add(response);
        }
        return responses.size();
    }

    public List<TherapySessionResponse> getTherapySessionResponse() {
        return clientTherapyScheduleRepository.findAll()
                .stream()
                .map(this::buildTherapySessionResponse)
                .toList();
    }

    public long findAllTherapySessionSchedules() {
        List<TherapySessionResponse> responses = new ArrayList<>();
        List<ClientTherapySchedule> schedules = clientTherapyScheduleRepository.findByStatus(ScheduleStatus.SCHEDULED);
        for (ClientTherapySchedule schedule : schedules) {
            TherapySessionResponse response = this.buildTherapySessionResponse(schedule);
            responses.add(response);
        }
        return responses.size();
    }

    private TherapySessionResponse buildTherapySessionResponse(ClientTherapySchedule response){

        AppParentGuardian guardian = response.getAppClientProfile().getAppParentGuardian().get(0);
        AppClientProfile client = response.getAppClientProfile();
        List<AssignmentHistory> assigmentHistory = response.getAppClientProfile().getAssignmentHistories();

        AppClientAssignment caseManager = client.getAssignments()
                .stream()
                .filter(assignment -> assignment.getAssignmentRole() == AssignmentRole.PRIMARY_CASE_MANAGER)
                .filter(assignment -> assignment.getStatus() == AssignmentStatus.ASSIGNED)
                .findFirst()
                .orElse(null);

        AppEmployeeProfile therapist = response.getTherapist();

        AssignmentHistory updatedHistory =
                response.getAppClientProfile()
                        .getAssignmentHistories()
                        .stream()
                        .filter(history -> history.getAssignmentStatus() == AssignmentStatus.ASSIGNED)
                        .max(Comparator.comparing(AssignmentHistory::getEventDateTime))
                        .orElse(null);

        return TherapySessionResponse.builder()
                .therapySessionId(response.getTherapySessionId())
                .assignmentRole(response.getAssignmentRole())
                .frequency(response.getFrequency())
                .status(response.getStatus())
                .notes(response.getNotes())
                .scheduleSlots(
                        response.getScheduleSlots() == null
                                ? List.of()
                                : response.getScheduleSlots()
                                .stream()
                                .map(slot -> TherapySessionResponse.TherapySlotRequest.builder()
                                        .therapyDate(slot.getTherapyDate())
                                        .day(slot.getDay())
                                        .startTime(slot.getStartTime())
                                        .endTime(slot.getEndTime())
                                        .build()
                                )
                                .toList()
                )

                .clientStudentId(client.getClientStudentId())

                .clientId(client.getClientId())
                .clientProfilePicture(client.getProfileImageUrl())
                .clientFullName(client.getFirstName() + " " + client.getMiddleName() + " " + client.getLastName())
                .clientBirthDate(response.getAppClientProfile().getBirthDate())
                .clientAge(response.getAppClientProfile().getAge())
                .clientGender(response.getAppClientProfile().getGender())

                .guardianFullName(guardian.getFirstName() + " " + guardian.getMiddleName() + " " + guardian.getLastName())
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

                .caseManagerId(
                        caseManager != null && caseManager.getAssignee() != null
                                ? caseManager.getAssignee()
                                .getEmploymentInformation()
                                .getEmployeeId()
                                : null
                )
                .caseManagerProfilePicture(
                        caseManager != null && caseManager.getAssignee() != null
                                ? caseManager.getAssignee().getProfileImageUrl()
                                : "/img/base/default-profile.png"
                )
                .caseManagerFullName(
                        caseManager != null && caseManager.getAssignee() != null
                                ? caseManager.getAssignee().getFirstName() + " "
                                + caseManager.getAssignee().getLastName()
                                : "-"
                )
                .caseManagerPosition(
                        caseManager != null && caseManager.getAssignee() != null
                                ? caseManager.getAssignee()
                                .getEmploymentInformation()
                                .getPosition()
                                : "-"
                )
                .caseManagerAssignedAt(
                        caseManager != null
                                ? caseManager.getAssignedAt()
                                : null
                )
                .assignmentRole(AssignmentRole.PRIMARY_CASE_MANAGER)

                .therapistId(therapist.getEmployeeId())
                .therapistProfilePicture(therapist.getProfileImageUrl())
                .therapistFullName(therapist.getFirstName() + " " + therapist.getLastName())
                .therapistPosition(therapist.getEmploymentInformation() != null
                        ? therapist.getEmploymentInformation()
                        .getPosition()
                        : "-")

                .history(this.buildTSAssignmentHistories(assigmentHistory))
                .build();
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

        AppClientAssignment assigneeAssignment = client.getAssignments()
                .stream()
                .filter(assignment -> assignment.getAssignmentRole() == AssignmentRole.PRIMARY_CASE_MANAGER)
                .filter(assignment -> assignment.getStatus() == AssignmentStatus.ASSIGNED)
                .findFirst()
                .orElse(null);


        return InitialAssessmentResponse.builder()
                .initialAssessmentId(response.getInitialAssessmentId())
                .assessmentDate(response.getAssessmentDate())
                .scheduleStatus(response.getStatus())
                .notes(response.getNotes())
                .slots(
                        Optional.ofNullable(response.getSlots())
                                .orElseGet(List::of)
                                .stream()
                                .map(slot -> InitialAssessmentResponse.InitialAssessmentDto.builder()
                                        .startTime(slot.getStartTime())
                                        .endTime(slot.getEndTime())
                                        .build()
                                )
                                .toList()
                )
//                .slots(List.of(InitialAssessmentResponse.InitialAssessmentDto.builder()
//                                .startTime(response.getSlots().get(0).getStartTime())
//                                .endTime(response.getSlots().get(0).getEndTime())
//                        .build()))

                .clientStudentId(client.getClientStudentId())

                .clientId(client.getClientId())
                .clientProfilePicture(client.getProfileImageUrl())
                .clientFullName(client.getFirstName() + " " + client.getMiddleName() + " " + client.getLastName())
                .clientBirthDate(response.getAppClientProfile().getBirthDate())
                .clientAge(response.getAppClientProfile().getAge())
                .clientGender(response.getAppClientProfile().getGender())

                .guardianFullName(guardian.getFirstName() + " " + guardian.getMiddleName() + " " + guardian.getLastName())
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
                .employeeId(
                        assigneeAssignment != null && assigneeAssignment.getAssignee() != null
                                ? assigneeAssignment.getAssignee()
                                .getEmploymentInformation()
                                .getEmployeeId()
                                : null
                )
                .assigneeProfilePicture(
                        assigneeAssignment != null && assigneeAssignment.getAssignee() != null
                                ? assigneeAssignment.getAssignee().getProfileImageUrl()
                                : "/img/base/default-profile.png"
                )
                .assigneeFullName(
                        assigneeAssignment != null && assigneeAssignment.getAssignee() != null
                                ? assigneeAssignment.getAssignee().getFirstName() + " "
                                + assigneeAssignment.getAssignee().getLastName()
                                : "-"
                )
                .assigneePosition(
                        assigneeAssignment != null && assigneeAssignment.getAssignee() != null
                                ? assigneeAssignment.getAssignee()
                                .getEmploymentInformation()
                                .getPosition()
                                : "-"
                )
                .assignedAt(
                        assigneeAssignment != null
                                ? assigneeAssignment.getAssignedAt()
                                : null
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

    public List<TherapySessionResponse.AssignmentHistoryResponse> buildTSAssignmentHistories(List<AssignmentHistory> assigmentHistory){
        return assigmentHistory.stream()
                .sorted(Comparator.comparing(
                        AssignmentHistory::getEventDateTime,
                        Comparator.nullsLast(Comparator.reverseOrder())
                ))
                .map(item -> TherapySessionResponse.AssignmentHistoryResponse.builder()
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
