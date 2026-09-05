package ph.com.lllc.service.api.clients;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ph.com.lllc.dto.response.CommonResponse;
import ph.com.lllc.dto.staff.clients.*;
import ph.com.lllc.entity.user.client.AppClientProfile;
import ph.com.lllc.entity.user.client.AppParentGuardian;
import ph.com.lllc.entity.user.client.assessment.AssessmentSlot;
import ph.com.lllc.entity.user.client.assessment.ClientInitialAssessmentSchedule;
import ph.com.lllc.entity.user.client.assignment.AppClientAssignment;
import ph.com.lllc.entity.user.client.assignment.AssignmentHistory;
import ph.com.lllc.entity.user.client.neurodev.NeurodevelopmentalAssessmentSchedule;
import ph.com.lllc.entity.user.client.schedule.ClientTherapySchedule;
import ph.com.lllc.entity.user.client.schedule.TherapySlot;
import ph.com.lllc.entity.user.client.upgrading.ClientUpgradingProgramSchedule;
import ph.com.lllc.entity.user.client.upgrading.UpgradingProgramSlot;
import ph.com.lllc.entity.user.staff.generalinfo.AppEmployeeProfile;
import ph.com.lllc.enums.*;
import ph.com.lllc.exception.ServiceException;
import ph.com.lllc.repository.*;
import ph.com.lllc.service.api.admin.UserAccountService;
import ph.com.lllc.service.db.SequenceGeneratorService;
import ph.com.lllc.service.util.IdGeneratorUtils;
import ph.com.lllc.service.util.logging.LoggingService;
import ph.com.lllc.util.ObjectUtils;

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
    private final TherapySlotRepository therapySlotRepository;
    private final ClientUpgradingProgramScheduleRepository clientUpgradingProgramScheduleRepository;
    private final UpgradingProgramSlotRepository upgradingProgramSlotRepository;
    private final NeurodevelopmentalAssessmentRepository neurodevelopmentalAssessmentRepository;
    private final SequenceGeneratorService sequenceGeneratorService;
    private final IdGeneratorUtils idGeneratorUtils;
    private final LoggingService loggingService;

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");
    private final AppClientAssignmentRepository appClientAssignmentRepository;

    @Transactional
    public CommonResponse saveInitialAssessmentSchedule(String uuid, InitialAssessmentRequest request, HttpServletRequest httpRequest) throws ServiceException {

        AppClientProfile clientProfile = clientManagementService.findAppClientProfileByClientId(uuid, request.getClientId());
        AppEmployeeProfile appCaseManager = clientManagementService.findAppEmployeeProfileByEmployeeId(uuid, request.getEmployeeId());

        AppClientAssignment clientAssignment = clientProfile.getAssignments().get(0);

        String year = String.valueOf(request.getAssessmentDate().getYear());
        long nextUserSeq = sequenceGeneratorService.getInitialAssessmentIdNextSequence();
        String initialAssessmentId =  idGeneratorUtils.generateInitialAssessmentId(year, nextUserSeq);

        ClientInitialAssessmentSchedule schedule = new ClientInitialAssessmentSchedule();
        schedule.setInitialAssessmentId(initialAssessmentId);
        schedule.setAssessmentDate(request.getAssessmentDate());
        schedule.setStatus(request.getScheduleStatus());
        schedule.setNotes(request.getNotes());
        schedule.setCaseManager(appCaseManager);

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
        history.setDescription(this.createDescription(appCaseManager, request.getAssessmentDate(),
                request.getSlots().get(0).getStartTime(), request.getSlots().get(0).getEndTime()));
        history.setCaseManager(clientAssignment.getCaseManager());
        history.setCaseManagerRole(clientAssignment.getCaseManagerRole());
        history.setBehavioralTherapist(clientAssignment.getBehavioralTherapist());
        history.setBehavioralTherapistRole(clientAssignment.getBehavioralTherapistRole());
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
        AppEmployeeProfile appCaseManager = clientManagementService.findAppEmployeeProfileByEmployeeId(uuid, request.getEmployeeId());

        AppClientAssignment clientAssignment = clientProfile.getAssignments().get(0);

        ClientInitialAssessmentSchedule schedule = clientInitialAssessmentScheduleRepository.findByInitialAssessmentId(request.getInitialAssessmentId())
                        .orElseThrow(() -> new ServiceException(HttpStatus.NOT_FOUND.value(), "Initial assessment schedule not found: " + request.getInitialAssessmentId()));

        /*
         * Update schedule
         */
        schedule.setAssessmentDate(request.getAssessmentDate());
        schedule.setStatus(request.getScheduleStatus());
        schedule.setNotes(request.getNotes());
        schedule.setCaseManager(appCaseManager);

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
                appCaseManager,
                request.getAssessmentDate(),
                slotRequest.getStartTime(),
                slotRequest.getEndTime())
        );

        history.setCaseManager(clientAssignment.getCaseManager());
        history.setCaseManagerRole(clientAssignment.getCaseManagerRole());
        history.setBehavioralTherapist(clientAssignment.getBehavioralTherapist());
        history.setBehavioralTherapistRole(clientAssignment.getBehavioralTherapistRole());

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
        AppEmployeeProfile behavioralTherapist = clientManagementService.findAppEmployeeProfileByEmployeeId(uuid, request.getEmployeeId());

        AppClientAssignment clientAssignment = clientProfile.getAssignments().get(0);

        String year = String.valueOf(LocalDate.now().getYear());
        long nextUserSeq = sequenceGeneratorService.getAssessmentIdNextSequence();
        String therapySessionId =  idGeneratorUtils.generateAssessmentId(year, nextUserSeq);

        ClientTherapySchedule schedule = new ClientTherapySchedule();
        schedule.setTherapySessionId(therapySessionId);
        schedule.setAssignmentRole(request.getAssignmentRole());
        schedule.setFrequency(request.getFrequency());
        schedule.setStatus(request.getStatus());
        schedule.setNotes(request.getNotes());
        schedule.setTherapist(behavioralTherapist);

        schedule.setAppClientProfile(clientProfile);
        clientProfile.getSessionSchedules().add(schedule);

        /*
         * Create assignment history
         */
        AssignmentHistory history = new AssignmentHistory();
        history.setAction(AssignmentHistoryAction.ASSIGNED);
        history.setDescription("Therapy session assigned to " + behavioralTherapist.getFirstName() + " " + behavioralTherapist.getLastName());

        history.setCaseManager(clientAssignment.getCaseManager());
        history.setCaseManagerRole(clientAssignment.getCaseManagerRole());
        history.setBehavioralTherapist(clientAssignment.getBehavioralTherapist());
        history.setBehavioralTherapistRole(clientAssignment.getBehavioralTherapistRole());

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

    public CommonResponse saveTherapySlot(String uuid, TherapySlotRequest request) throws ServiceException {

        ClientTherapySchedule schedule = clientTherapyScheduleRepository.findByTherapySessionId(request.getTherapySessionId())
                .orElseThrow(() -> {
                    loggingService.error(uuid, getClass().getName(), "Therapy session schedule not found with ID: " + request.getTherapySessionId(), HttpStatus.NOT_FOUND.value());
                    return new ServiceException(HttpStatus.NOT_FOUND.value(), "Therapy session schedule not found with ID: " + request.getTherapySessionId());
                });

        TherapySlot slot = new TherapySlot();
        slot.setTherapyDate(request.getTherapyDate());
        slot.setDay(request.getDay());
        slot.setStartTime(request.getStartTime());
        slot.setEndTime(request.getEndTime());
        slot.setStatus(request.getStatus());
        slot.setNotes(request.getNotes());
        slot.setTherapySchedule(schedule);
        schedule.getScheduleSlots().add(slot);

        clientTherapyScheduleRepository.save(schedule);

        return CommonResponse.builder()
                .returnCode(HttpStatus.CREATED.value())
                .returnMessage("Therapy slot added successfully!")
                .build();
    }

    public TherapySessionResponse findByTherapySessionId(String uuid, String therapySessionId) throws ServiceException {
        ClientTherapySchedule schedule = clientTherapyScheduleRepository.findByTherapySessionId(therapySessionId)
                .orElseThrow(() -> {
                    loggingService.error(uuid, getClass().getName(), "Therapy session schedule not found with ID: " + therapySessionId, HttpStatus.NOT_FOUND.value());
                    return new ServiceException(HttpStatus.NOT_FOUND.value(), "Therapy session schedule not found with ID: " + therapySessionId);
                });

        AppClientProfile client = schedule.getAppClientProfile();
        List<AppClientAssignment> assignments = client.getAssignments();

        int num = 0;

        for (int i = 0; i < assignments.size(); i++) {
            AppClientAssignment assignment = assignments.get(i);
            if (assignment.getBehavioralTherapistRole() == schedule.getAssignmentRole()) {
                num = i;
                break;
            }
        }

        return this.buildTherapySessionResponse(schedule, num);
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

    public CommonResponse updateTherapySlot(String uuid, TherapySlotRequest request) throws ServiceException {

        TherapySlot slot = therapySlotRepository.findById(request.getId())
                .orElseThrow(() -> {
                    loggingService.error(uuid, getClass().getName(), "Therapy slot not found with ID: " + request.getId(), HttpStatus.NOT_FOUND.value());
                    return new ServiceException(HttpStatus.NOT_FOUND.value(), "Therapy slot not found with ID: " + request.getId());
                });

        ClientTherapySchedule schedule = clientTherapyScheduleRepository.findByTherapySessionId(request.getTherapySessionId())
                .orElseThrow(() -> {
                    loggingService.error(uuid, getClass().getName(), "Therapy session schedule not found with ID: " + request.getTherapySessionId(),
                            HttpStatus.NOT_FOUND.value());
                    return new ServiceException(HttpStatus.NOT_FOUND.value(), "Therapy session schedule not found with ID: " + request.getTherapySessionId());
                });

        slot.setTherapyDate(request.getTherapyDate());
        slot.setDay(request.getDay());
        slot.setStartTime(request.getStartTime());
        slot.setEndTime(request.getEndTime());
        slot.setStatus(request.getStatus());
        slot.setNotes(request.getNotes());
        slot.setTherapySchedule(schedule);

        therapySlotRepository.save(slot);

        return CommonResponse.builder()
                .returnCode(HttpStatus.OK.value())
                .returnMessage("Therapy slot updated successfully!")
                .build();
    }

    public List<TherapySessionResponse> getTherapySessionResponse() {
        List<TherapySessionResponse> responses = new ArrayList<>();
        List<ClientTherapySchedule> schedules = clientTherapyScheduleRepository.findAll();

        for (int num = 0; num < schedules.size(); num++) {
            ClientTherapySchedule schedule = schedules.get(num);
            TherapySessionResponse response = this.buildTherapySessionResponse(schedule, num);
            responses.add(response);
        }

        return responses;
    }

    public long findAllTherapySessionSchedules() {
        List<TherapySessionResponse> responses = new ArrayList<>();
        List<ClientTherapySchedule> schedules = clientTherapyScheduleRepository.findAll();

        for (int num = 0; num < schedules.size(); num++) {
            ClientTherapySchedule schedule = schedules.get(num);
            TherapySessionResponse response = this.buildTherapySessionResponse(schedule, num);
            responses.add(response);
        }

        return responses.size();
    }

    public CommonResponse saveUpgradingProgramDetails(String uuid, UpgradingProgramRequest request, HttpServletRequest httpRequest) throws ServiceException {

        AppClientProfile clientProfile = clientManagementService.findAppClientProfileByClientId(uuid, request.getClientId());
        AppEmployeeProfile caseManager = clientManagementService.findAppEmployeeProfileByEmployeeId(uuid, request.getEmployeeId());

        AppClientAssignment clientAssignment = clientProfile.getAssignments().get(0);

        String year = String.valueOf(LocalDate.now().getYear());
        long nextUserSeq = sequenceGeneratorService.getUpgradingProgramIdNextSequence();
        String upgradingProgramId =  idGeneratorUtils.generateUpgradingProgramId(year, nextUserSeq);

        ClientUpgradingProgramSchedule schedule = new ClientUpgradingProgramSchedule();
        schedule.setUpgradingProgramId(upgradingProgramId);
        schedule.setAssignmentRole(request.getAssignmentRole());
        schedule.setStatus(request.getStatus());
        schedule.setNotes(request.getNotes());
        schedule.setCaseManager(caseManager);

        schedule.setAppClientProfile(clientProfile);
        clientProfile.getUpgradingProgramSchedules().add(schedule);

        /*
         * Create assignment history
         */
        AssignmentHistory history = new AssignmentHistory();
        history.setAction(AssignmentHistoryAction.ASSIGNED);
        history.setDescription("Upgrading program session assigned to " + caseManager.getFirstName() + " " + caseManager.getLastName());

        history.setCaseManager(clientAssignment.getCaseManager());
        history.setCaseManagerRole(clientAssignment.getCaseManagerRole());
        history.setBehavioralTherapist(clientAssignment.getBehavioralTherapist());
        history.setBehavioralTherapistRole(clientAssignment.getBehavioralTherapistRole());

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
                .returnMessage("Upgrading program session assigned successfully!")
                .build();
    }

    public List<UpgradingProgramResponse> getUpgradingProgramResponse() {
        List<UpgradingProgramResponse> responses = new ArrayList<>();
        List<ClientUpgradingProgramSchedule> schedules = clientUpgradingProgramScheduleRepository.findAll();

        for (int num = 0; num < schedules.size(); num++) {
            ClientUpgradingProgramSchedule schedule = schedules.get(num);
            UpgradingProgramResponse response = this.buildUpgradingProgramResponse(schedule, num);
            responses.add(response);
        }

        return responses;
    }

    public long findAllUpgradingProgramSchedules() {
        List<UpgradingProgramResponse> responses = new ArrayList<>();
        List<ClientUpgradingProgramSchedule> schedules = clientUpgradingProgramScheduleRepository.findAll();

        for (int num = 0; num < schedules.size(); num++) {
            ClientUpgradingProgramSchedule schedule = schedules.get(num);
            UpgradingProgramResponse response = this.buildUpgradingProgramResponse(schedule, num);
            responses.add(response);
        }

        return responses.size();
    }

    public UpgradingProgramResponse findByUpgradingProgramId(String uuid, String upgradingProgramId) throws ServiceException {
        ClientUpgradingProgramSchedule schedule = clientUpgradingProgramScheduleRepository.findByUpgradingProgramId(upgradingProgramId)
                .orElseThrow(() -> {
                    loggingService.error(uuid, getClass().getName(), "Upgrading program session schedule not found with ID: " + upgradingProgramId, HttpStatus.NOT_FOUND.value());
                    return new ServiceException(HttpStatus.NOT_FOUND.value(), "Upgrading program session schedule not found with ID: " + upgradingProgramId);
                });

        AppClientProfile client = schedule.getAppClientProfile();
        List<AppClientAssignment> assignments = client.getAssignments();

        int num = 0;

        for (int i = 0; i < assignments.size(); i++) {
            AppClientAssignment assignment = assignments.get(i);
            if (assignment.getBehavioralTherapistRole() == schedule.getAssignmentRole()) {
                num = i;
                break;
            }
        }

        return this.buildUpgradingProgramResponse(schedule, num);
    }

    public CommonResponse saveUpgradingProgramSlot(String uuid, UpgradingProgramSlotRequest request) throws ServiceException {

        ClientUpgradingProgramSchedule schedule = clientUpgradingProgramScheduleRepository.findByUpgradingProgramId(request.getUpgradingProgramId())
                .orElseThrow(() -> {
                    loggingService.error(uuid, getClass().getName(), "Upgrading program session schedule not found with ID: " + request.getUpgradingProgramId(), HttpStatus.NOT_FOUND.value());
                    return new ServiceException(HttpStatus.NOT_FOUND.value(), "Upgrading program session schedule not found with ID: " +request.getUpgradingProgramId());
                });

        UpgradingProgramSlot slot = new UpgradingProgramSlot();
        slot.setTherapyDate(request.getTherapyDate());
        slot.setDay(request.getDay());
        slot.setStartTime(request.getStartTime());
        slot.setEndTime(request.getEndTime());
        slot.setStatus(request.getStatus());
        slot.setNotes(request.getNotes());
        slot.setUpgradingProgramSchedule(schedule);
        schedule.getUpgradingProgramSlots().add(slot);

        clientUpgradingProgramScheduleRepository.save(schedule);

        return CommonResponse.builder()
                .returnCode(HttpStatus.CREATED.value())
                .returnMessage("Therapy slot added successfully!")
                .build();
    }

    public CommonResponse updateUpgradingProgramSlot(String uuid, UpgradingProgramSlotRequest request) throws ServiceException {

        UpgradingProgramSlot slot = upgradingProgramSlotRepository.findById(request.getId())
                .orElseThrow(() -> {
                    loggingService.error(uuid, getClass().getName(), "Upgrading program slot not found with ID: " + request.getId(), HttpStatus.NOT_FOUND.value());
                    return new ServiceException(HttpStatus.NOT_FOUND.value(), "Upgrading program slot not found with ID: " + request.getId());
                });

        ClientUpgradingProgramSchedule schedule = clientUpgradingProgramScheduleRepository.findByUpgradingProgramId(request.getUpgradingProgramId())
                .orElseThrow(() -> {
                    loggingService.error(uuid, getClass().getName(), "Upgrading program session schedule not found with ID: " + request.getUpgradingProgramId(),
                            HttpStatus.NOT_FOUND.value());
                    return new ServiceException(HttpStatus.NOT_FOUND.value(), "Upgrading program session schedule not found with ID: " + request.getUpgradingProgramId());
                });

        slot.setTherapyDate(request.getTherapyDate());
        slot.setDay(request.getDay());
        slot.setStartTime(request.getStartTime());
        slot.setEndTime(request.getEndTime());
        slot.setStatus(request.getStatus());
        slot.setNotes(request.getNotes());
        slot.setUpgradingProgramSchedule(schedule);

        upgradingProgramSlotRepository.save(slot);

        return CommonResponse.builder()
                .returnCode(HttpStatus.OK.value())
                .returnMessage("Upgrading program updated successfully!")
                .build();
    }

    @Transactional
    public CommonResponse saveNeurodevAssessment(String uuid, NeurodevAssessmentRequest request) throws ServiceException {

        NeurodevelopmentalAssessmentSchedule assessment =
                NeurodevelopmentalAssessmentSchedule.builder()
                        .clientName(request.getClientName())
                        .age(request.getAge())
                        .gender(request.getGender())
                        .parentGuardian(request.getParentGuardian())
                        .contactNumber(request.getContactNumber())
                        .status(request.getStatus())
                        .assessmentDate(request.getAssessmentDate())
                        .neurodevFee(request.getNeurodevFee())
                        .paymentStatus(request.getPaymentStatus())
                        .notes(request.getNotes())
                        .build();

        neurodevelopmentalAssessmentRepository.save(assessment);

        loggingService.info(uuid, this.getClass().getName(), "", "Neurodev assessment schedule saved successfully!");

        return CommonResponse.builder()
                .returnCode(HttpStatus.CREATED.value())
                .returnMessage("Neurodev assessment schedule saved successfully!")
                .build();
    }

    public List<NeurodevAssessmentResponse> getNeurodevAssessments() throws ServiceException {
        List<NeurodevelopmentalAssessmentSchedule> neurodevAssessments = neurodevelopmentalAssessmentRepository.findAll(
                Sort.by(Sort.Direction.DESC, "assessmentDate")
        );
        return ObjectUtils.copyListAs(neurodevAssessments, NeurodevAssessmentResponse.class);
    }

    public long findAllNeurodevAssessments() throws ServiceException {
        List<NeurodevelopmentalAssessmentSchedule> neurodevAssessments = neurodevelopmentalAssessmentRepository.findAll(
                Sort.by(Sort.Direction.DESC, "assessmentDate")
        );
        return ObjectUtils.copyListAs(neurodevAssessments, NeurodevAssessmentResponse.class).size();
    }

    public NeurodevAssessmentResponse getNeurodevAssessments(String uuid, Long id) throws ServiceException {
        NeurodevelopmentalAssessmentSchedule neurodevAssessment = neurodevelopmentalAssessmentRepository.findById(id)
                .orElseThrow(() -> {
                    loggingService.error(uuid, getClass().getName(), "Neurodev assessment not found with ID: " + id, HttpStatus.NOT_FOUND.value());
                    return new ServiceException(HttpStatus.NOT_FOUND.value(), "Neurodev assessment not found with ID: " + id);
                });
        return ObjectUtils.copyAs(neurodevAssessment, NeurodevAssessmentResponse.class);
    }

    private TherapySessionResponse buildTherapySessionResponse(ClientTherapySchedule response, int num){

        AppParentGuardian guardian = response.getAppClientProfile().getAppParentGuardian().get(0);
        AppClientProfile client = response.getAppClientProfile();
        List<AssignmentHistory> assigmentHistory = response.getAppClientProfile().getAssignmentHistories();

        AppClientAssignment assignment = client.getAssignments().get(num);
        AppEmployeeProfile caseManager = assignment.getCaseManager();
        AppEmployeeProfile therapist = assignment.getBehavioralTherapist();

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
                                        .id(slot.getId())
                                        .therapyDate(slot.getTherapyDate())
                                        .day(slot.getDay())
                                        .startTime(slot.getStartTime())
                                        .endTime(slot.getEndTime())
                                        .status(slot.getStatus())
                                        .notes(slot.getNotes())
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
                        caseManager != null && caseManager.getEmployeeId() != null ?
                                caseManager.getEmployeeId()
                                : "-"
                )
                .caseManagerProfilePicture(
                        caseManager != null && caseManager.getProfileImageUrl() != null
                                ? caseManager.getProfileImageUrl()
                                : "/img/base/default-profile.png"
                )
                .caseManagerFullName(
                        caseManager != null
                                ? caseManager.getFirstName() + " "
                                + caseManager.getLastName()
                                : "-"
                )
                .caseManagerPosition(
                        caseManager != null ? caseManager.getEmploymentInformation()
                                .getPosition()
                                : "-"
                )
                .caseManagerRole(assignment.getCaseManagerRole())

                .behavioralTherapistId(therapist.getEmployeeId())
                .behavioralTherapistProfilePicture(therapist.getProfileImageUrl())
                .behavioralTherapistFullName(therapist.getFirstName() + " " + therapist.getLastName())
                .behavioralTherapistPosition(therapist.getEmploymentInformation() != null
                        ? therapist.getEmploymentInformation()
                        .getPosition()
                        : "-")
                .behavioralTherapistRole(assignment.getBehavioralTherapistRole())

                .assignedAt(assignment.getAssignedAt())
                .history(this.buildTSAssignmentHistories(assigmentHistory))
                .build();
    }

    private UpgradingProgramResponse buildUpgradingProgramResponse(ClientUpgradingProgramSchedule response, int num){

        AppParentGuardian guardian = response.getAppClientProfile().getAppParentGuardian().get(0);
        AppClientProfile client = response.getAppClientProfile();
        List<AssignmentHistory> assigmentHistory = response.getAppClientProfile().getAssignmentHistories();

        AppClientAssignment assignment = client.getAssignments().get(num);
        AppEmployeeProfile caseManager = assignment.getCaseManager();
        AppEmployeeProfile therapist = assignment.getBehavioralTherapist();

        AssignmentHistory updatedHistory =
                response.getAppClientProfile()
                        .getAssignmentHistories()
                        .stream()
                        .filter(history -> history.getAssignmentStatus() == AssignmentStatus.ASSIGNED)
                        .max(Comparator.comparing(AssignmentHistory::getEventDateTime))
                        .orElse(null);

        return UpgradingProgramResponse.builder()
                .upgradingProgramId(response.getUpgradingProgramId())
                .assignmentRole(response.getAssignmentRole())
                .status(response.getStatus())
                .notes(response.getNotes())
                .upgradingProgramSlots(
                        response.getUpgradingProgramSlots() == null
                                ? List.of()
                                : response.getUpgradingProgramSlots()
                                .stream()
                                .map(slot -> UpgradingProgramResponse.UpgradingProgramSlotRequest.builder()
                                        .id(slot.getId())
                                        .therapyDate(slot.getTherapyDate())
                                        .day(slot.getDay())
                                        .startTime(slot.getStartTime())
                                        .endTime(slot.getEndTime())
                                        .status(slot.getStatus())
                                        .notes(slot.getNotes())
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
                        caseManager != null && caseManager.getEmployeeId() != null ?
                                caseManager.getEmployeeId()
                                : "-"
                )
                .caseManagerProfilePicture(
                        caseManager != null && caseManager.getProfileImageUrl() != null
                                ? caseManager.getProfileImageUrl()
                                : "/img/base/default-profile.png"
                )
                .caseManagerFullName(
                        caseManager != null
                                ? caseManager.getFirstName() + " "
                                + caseManager.getLastName()
                                : "-"
                )
                .caseManagerPosition(
                        caseManager != null ? caseManager.getEmploymentInformation()
                                .getPosition()
                                : "-"
                )
                .caseManagerRole(assignment.getCaseManagerRole())

                .behavioralTherapistId(therapist.getEmployeeId())
                .behavioralTherapistProfilePicture(therapist.getProfileImageUrl())
                .behavioralTherapistFullName(therapist.getFirstName() + " " + therapist.getLastName())
                .behavioralTherapistPosition(therapist.getEmploymentInformation() != null
                        ? therapist.getEmploymentInformation()
                        .getPosition()
                        : "-")
                .behavioralTherapistRole(assignment.getBehavioralTherapistRole())

                .assignedAt(assignment.getAssignedAt())
                .history(this.buildUPAssignmentHistories(assigmentHistory))
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


        AppClientAssignment assignment = client.getAssignments().get(0);
        AppEmployeeProfile caseManager = assignment.getCaseManager();
        AppEmployeeProfile therapist = assignment.getBehavioralTherapist();


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
                        caseManager != null && caseManager.getEmployeeId() != null ?
                                caseManager.getEmployeeId()
                                : "-"
                )
                .caseManagerProfilePicture(
                        caseManager != null && caseManager.getProfileImageUrl() != null
                                ? caseManager.getProfileImageUrl()
                                : "/img/base/default-profile.png"
                )
                .caseManagerFullName(
                        caseManager != null
                                ? caseManager.getFirstName() + " "
                                + caseManager.getLastName()
                                : "-"
                )
                .caseManagerPosition(
                        caseManager != null ? caseManager.getEmploymentInformation()
                                .getPosition()
                                : "-"
                )
                .caseManagerRole(assignment.getCaseManagerRole())

                .behavioralTherapistId(therapist.getEmployeeId())
                .behavioralTherapistProfilePicture(therapist.getProfileImageUrl())
                .behavioralTherapistFullName(therapist.getFirstName() + " " + therapist.getLastName())
                .behavioralTherapistPosition(therapist.getEmploymentInformation() != null
                        ? therapist.getEmploymentInformation()
                        .getPosition()
                        : "-")
                .behavioralTherapistRole(assignment.getBehavioralTherapistRole())

                .assignedAt(assignment.getAssignedAt())

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
                        .caseManagerFullName(
                                item.getCaseManager() != null
                                        ? item.getCaseManager().getFirstName() + " "
                                        + item.getCaseManager().getLastName()
                                        : "-"
                        )
                        .caseManagerRole(item.getCaseManagerRole())

                        .behavioralTherapistFullName(
                                item.getCaseManager() != null
                                        ? item.getBehavioralTherapist().getFirstName() + " "
                                        + item.getBehavioralTherapist().getLastName()
                                        : "-"
                        )
                        .behavioralTherapistRole(item.getBehavioralTherapistRole())

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
                        .caseManagerFullName(
                                item.getCaseManager() != null
                                        ? item.getCaseManager().getFirstName() + " "
                                        + item.getCaseManager().getLastName()
                                        : "-"
                        )
                        .caseManagerRole(item.getCaseManagerRole())

                        .behavioralTherapistFullName(
                                item.getCaseManager() != null
                                        ? item.getBehavioralTherapist().getFirstName() + " "
                                        + item.getBehavioralTherapist().getLastName()
                                        : "-"
                        )
                        .behavioralTherapistRole(item.getBehavioralTherapistRole())

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

    public List<UpgradingProgramResponse.AssignmentHistoryResponse> buildUPAssignmentHistories(List<AssignmentHistory> assigmentHistory){
        return assigmentHistory.stream()
                .sorted(Comparator.comparing(
                        AssignmentHistory::getEventDateTime,
                        Comparator.nullsLast(Comparator.reverseOrder())
                ))
                .map(item -> UpgradingProgramResponse.AssignmentHistoryResponse.builder()
                        .description(item.getDescription())
                        .action(item.getAction())
                        .caseManagerFullName(
                                item.getCaseManager() != null
                                        ? item.getCaseManager().getFirstName() + " "
                                        + item.getCaseManager().getLastName()
                                        : "-"
                        )
                        .caseManagerRole(item.getCaseManagerRole())

                        .behavioralTherapistFullName(
                                item.getCaseManager() != null
                                        ? item.getBehavioralTherapist().getFirstName() + " "
                                        + item.getBehavioralTherapist().getLastName()
                                        : "-"
                        )
                        .behavioralTherapistRole(item.getBehavioralTherapistRole())

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
