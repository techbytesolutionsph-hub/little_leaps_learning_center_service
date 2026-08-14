package ph.com.lllc.service.api.clients;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ph.com.lllc.dto.response.CommonResponse;
import ph.com.lllc.dto.staff.clients.AssignClientRequest;
import ph.com.lllc.dto.staff.clients.AssignedClientResponse;
import ph.com.lllc.dto.staff.clients.ClientRegistrationRequest;
import ph.com.lllc.dto.staff.clients.ClientRegistrationResponse;
import ph.com.lllc.entity.user.client.AppClientProfile;
import ph.com.lllc.entity.user.client.AppParentGuardian;
import ph.com.lllc.entity.user.client.assignment.AppClientAssignment;
import ph.com.lllc.entity.user.client.assignment.AssignmentHistory;
import ph.com.lllc.entity.user.common.AppUser;
import ph.com.lllc.entity.user.staff.generalinfo.AppEmployeeProfile;
import ph.com.lllc.enums.AssignmentHistoryAction;
import ph.com.lllc.enums.AssignmentRole;
import ph.com.lllc.enums.AssignmentStatus;
import ph.com.lllc.exception.ServiceException;
import ph.com.lllc.repository.AppClientAssignmentRepository;
import ph.com.lllc.repository.AppUserRepository;
import ph.com.lllc.repository.ClientProfileRepository;
import ph.com.lllc.repository.management.AppEmployeeProfileRepository;
import ph.com.lllc.service.api.admin.UserAccountService;
import ph.com.lllc.service.db.SequenceGeneratorService;
import ph.com.lllc.service.util.IdGeneratorUtils;
import ph.com.lllc.service.util.logging.LoggingService;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Service
public class ClientManagementService {

    private final ClientProfileRepository clientProfileRepository;
    private final AppClientAssignmentRepository appClientAssignmentRepository;
    private final AppEmployeeProfileRepository appEmployeeProfileRepository;
    private final AppUserRepository appUserRepository;
    private final UserAccountService userAccountService;
    private final SequenceGeneratorService sequenceGeneratorService;
    private final IdGeneratorUtils idGeneratorUtils;
    private final LoggingService loggingService;

    @Value("${app.lllc.job-positions}")
    private String jobPositions;

    @Transactional
    public CommonResponse registerClient(String uuid, ClientRegistrationRequest request) throws ServiceException {

        long nextUserSeq = sequenceGeneratorService.getClientIdNextSequence();
        String clientId =  idGeneratorUtils.generateClientId(nextUserSeq);

        /* Create Client Profile */
        AppClientProfile clientProfile = new AppClientProfile();
        clientProfile.setClientId(clientId);
        clientProfile.setFirstName(request.getFirstName());
        clientProfile.setMiddleName(request.getMiddleName());
        clientProfile.setLastName(request.getLastName());
        clientProfile.setAge(request.getAge());
        clientProfile.setBirthDate(request.getBirthDate());
        clientProfile.setGender(request.getGender());

        /* Enrollment Details */
        clientProfile.setDateEnrolled(request.getDateEnrolled());
        clientProfile.setDiagnosisConcerns(request.getDiagnosisConcerns());
        clientProfile.setProgramType(request.getProgramType());
        clientProfile.setAssignmentStatus(AssignmentStatus.INACTIVE);
        clientProfile.setBranch(request.getBranch());
        clientProfile.setEnrollmentStatus(request.getEnrollmentStatus());
        clientProfile.setProfileImageUrl(request.getProfileImageUrl());
        clientProfile.setActive(true);

        if (request.getParents() != null && !request.getParents().isEmpty()) {

            List<AppParentGuardian> parents = request.getParents()
                    .stream()
                    .map(parentRequest -> {

                        AppParentGuardian parent = new AppParentGuardian();
                        parent.setFirstName(parentRequest.getFirstName());
                        parent.setMiddleName(parentRequest.getMiddleName());
                        parent.setLastName(parentRequest.getLastName());
                        parent.setContactNumber(parentRequest.getContactNumber());
                        parent.setEmail(parentRequest.getEmail());
                        parent.setRelationshipToClient(parentRequest.getRelationshipToClient());
                        parent.setGender(parentRequest.getGender());
                        parent.setAddress(parentRequest.getAddress());
                        parent.setAppClientProfile(clientProfile);

                        return parent;
                    })
                    .collect(Collectors.toList());

            clientProfile.setAppParentGuardian(parents);
        }

        /*
         * Retrieve App User
         */
        String username = request.getAccountAccess() != null
                ? request.getAccountAccess().getUsername()
                : null;

        if (StringUtils.hasText(username)) {
            /* Find existing user */
            AppUser appUser = appUserRepository.findUserByUsername(username)
                    .orElseThrow(() -> new ServiceException(HttpStatus.NOT_FOUND.value(), "User not found: " + username));

            /*
             * Validate Existing Client Profile
             */
            if (clientProfileRepository.existsByAppUser(appUser)) {
                loggingService.error(uuid, getClass().getName(), "Client profile already exists for username: " + username, HttpStatus.CONFLICT.value());
                throw new ServiceException(HttpStatus.CONFLICT.value(), "Client profile already exists for username: " + username);
            }

            /*
             * Link AppUser <-> Client Profile
             */
            clientProfile.setAppUser(appUser);
            appUser.setAppClientProfile(clientProfile);
        }

        loggingService.info(uuid, this.getClass().getName(), "", "Saving new client profile...");
        AppClientProfile savedClient = clientProfileRepository.save(clientProfile);

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("client", savedClient);

        return CommonResponse.builder()
                .returnCode(HttpStatus.CREATED.value())
                .returnMessage("Client registered successfully!")
                .responseBody(responseBody)
                .build();
    }

    public List<ClientRegistrationResponse> getClientProfiles() {

        return clientProfileRepository
                .findAll(Sort.by(Sort.Direction.DESC, "id"))
                .stream()
                .map(this::mapToClientRegistrationResponse)
                .toList();
    }

    public ClientRegistrationResponse getClientProfileByClientId(String clientId) throws ServiceException {
        AppClientProfile clientProfile = this.findAppClientProfileByClientId("", clientId);
        return mapToClientRegistrationResponse(clientProfile);
    }

    @Transactional
    public CommonResponse updateClient(String uuid, ClientRegistrationRequest request) throws ServiceException {

        AppClientProfile clientProfile = this.findAppClientProfileByClientId(uuid, request.getClientId());

        clientProfile.setFirstName(request.getFirstName());
        clientProfile.setMiddleName(request.getMiddleName());
        clientProfile.setLastName(request.getLastName());
        clientProfile.setAge(request.getAge());
        clientProfile.setBirthDate(request.getBirthDate());
        clientProfile.setGender(request.getGender());

        clientProfile.setDateEnrolled(request.getDateEnrolled());
        clientProfile.setDiagnosisConcerns(request.getDiagnosisConcerns());
        clientProfile.setProgramType(request.getProgramType());
        clientProfile.setAssignmentStatus(AssignmentStatus.INACTIVE);
        clientProfile.setBranch(request.getBranch());
        clientProfile.setEnrollmentStatus(request.getEnrollmentStatus());

        if (StringUtils.hasText(request.getProfileImageUrl())) {
            clientProfile.setProfileImageUrl(request.getProfileImageUrl());
        }

        if (request.getParents() != null && !request.getParents().isEmpty()) {

            ClientRegistrationRequest.ParentGuardian parentRequest = request.getParents().get(0);

            List<AppParentGuardian> existingParents = clientProfile.getAppParentGuardian();

            AppParentGuardian parent;

            if (existingParents != null && !existingParents.isEmpty()) {

                /*
                 * Update existing parent/guardian
                 */
                parent = existingParents.get(0);

            } else {
                parent = new AppParentGuardian();
                parent.setAppClientProfile(clientProfile);

                if (existingParents == null) {
                    existingParents = new ArrayList<>();
                    clientProfile.setAppParentGuardian(existingParents);
                }

                existingParents.add(parent);
            }

            parent.setFirstName(parentRequest.getFirstName());
            parent.setMiddleName(parentRequest.getMiddleName());
            parent.setLastName(parentRequest.getLastName());
            parent.setContactNumber(parentRequest.getContactNumber());
            parent.setEmail(parentRequest.getEmail());
            parent.setRelationshipToClient(parentRequest.getRelationshipToClient());
            parent.setGender(parentRequest.getGender());
            parent.setAddress(parentRequest.getAddress());
        }

        /*
         * Account Access
         */
        String username = request.getAccountAccess() != null
                ? request.getAccountAccess().getUsername()
                : null;

        if (StringUtils.hasText(username)) {

            AppUser appUser = appUserRepository.findUserByUsername(username)
                    .orElseThrow(() -> new ServiceException(HttpStatus.NOT_FOUND.value(), "User not found: " + username));

            /*
             * If another client already owns this AppUser,
             * prevent the account from being reassigned.
             */
            if (appUser.getAppClientProfile() != null && appUser.getAppClientProfile().getId() != null
                    && !appUser.getAppClientProfile().getId().equals(clientProfile.getId())) {

                throw new ServiceException(HttpStatus.CONFLICT.value(), "User account already belongs to another client: " + username);
            }

            /*
             * Link AppUser <-> Client Profile
             */
            clientProfile.setAppUser(appUser);
            appUser.setAppClientProfile(clientProfile);
        }

        loggingService.info(uuid, this.getClass().getName(), "", "Updating client profile...");
        AppClientProfile savedClient = clientProfileRepository.save(clientProfile);

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("client", mapToClientRegistrationResponse(savedClient));

        return CommonResponse.builder()
                .returnCode(HttpStatus.OK.value())
                .returnMessage("Client updated successfully!")
                .responseBody(responseBody)
                .build();
    }

    public Map<String, String> mapCaseManagerBehavioralTherapist() {

        List<String> positions = Arrays.stream(jobPositions.split(","))
                .map(String::trim)
                .filter(position -> !position.isBlank())
                .toList();

        List<AppEmployeeProfile> employees = appEmployeeProfileRepository.findByEmploymentInformation_PositionIn(positions);

        Map<String, String> employeeMap = new LinkedHashMap<>();

        for (AppEmployeeProfile employee : employees) {

            String fullName = Stream.of(
                            employee.getFirstName(),
                            employee.getMiddleName(),
                            employee.getLastName()
                    )
                    .filter(Objects::nonNull)
                    .filter(name -> !name.isBlank())
                    .collect(Collectors.joining(" "));

            employeeMap.put(employee.getEmployeeId(), fullName);
        }

        return employeeMap;
    }

    @Transactional
    public CommonResponse assignClient(String uuid, AssignClientRequest request, HttpServletRequest httpRequest) throws ServiceException {

        AppClientProfile clientProfile = this.findAppClientProfileByClientId(uuid, request.getClientId());
        AppEmployeeProfile appEmployeeProfile = this.findAppEmployeeProfileByEmployeeId(uuid, request.getEmployeeId());

        String year = String.valueOf(request.getAssignedDate().getYear());
        long nextUserSeq = sequenceGeneratorService.getAssignmentIdNextSequence();
        String assignmentId =  idGeneratorUtils.generateAssignmentId(year, nextUserSeq);

        /*
         * Create assignment
         */
        AppClientAssignment clientAssignment = new AppClientAssignment();
        clientAssignment.setAssignmentId(assignmentId);
        clientAssignment.setAssignmentRole(request.getRole());
        clientAssignment.setDiagnosisConcerns(request.getDiagnosisConcerns());
        clientAssignment.setStatus(request.getAssignStatus());
        clientAssignment.setBranch(request.getAssignBranch());
        clientAssignment.setAssignedAt(request.getAssignedDate());
        clientAssignment.setNotes(request.getNotes());
        clientAssignment.setAppClientProfile(clientProfile);

        /*
         * Add assignment to client
         */
        if (clientProfile.getAssignments() == null) {
            clientProfile.setAssignments(new ArrayList<>());
        }

        clientProfile.getAssignments().add(clientAssignment);

        /* Update Assignment Status to ACTIVE */
        clientProfile.setAssignmentStatus(AssignmentStatus.ACTIVE);

        /*
         * Create assignment history
         */
        AssignmentHistory history = new AssignmentHistory();
        history.setAction(AssignmentHistoryAction.ASSIGNED);
        history.setDescription(this.createDescription(appEmployeeProfile, request));
        history.setAssignee(appEmployeeProfile);
        history.setAssignmentRole(request.getRole());
        history.setAssignmentStatus(request.getAssignStatus());
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
                .returnMessage("Client assigned successfully!")
                .build();
    }

    public List<AssignedClientResponse> getAssignedClients() {
        return appClientAssignmentRepository.findAll()
                .stream()
                .map(this::buildAssignedClientResponse)
                .toList();
    }

    private AssignedClientResponse buildAssignedClientResponse(AppClientAssignment response){

        AppParentGuardian guardian = response.getAppClientProfile().getAppParentGuardian().get(0);
        AppClientProfile client = response.getAppClientProfile();
        List<AssignmentHistory> assigmentHistory = response.getAppClientProfile().getAssignmentHistories();

        AssignmentHistory caseManager = assigmentHistory.stream()
                .filter(history ->
                        history.getAssignmentRole() == AssignmentRole.PRIMARY_CASE_MANAGER
                )
                .findFirst()
                .orElse(null);

        AppEmployeeProfile assignee = caseManager != null
                ? caseManager.getAssignee()
                : null;

         Integer activeCount = Math.toIntExact(assigmentHistory.stream()
                 .filter(history -> history.getAssignmentStatus() == AssignmentStatus.ACTIVE)
                 .count());

        Integer endedCount = Math.toIntExact(assigmentHistory.stream()
                .filter(history -> history.getAssignmentStatus() == AssignmentStatus.INACTIVE)
                .count());

        return AssignedClientResponse.builder()
                .id(response.getId())
                .assignmentId(response.getAssignmentId())

                .clientFullName(client.getFirstName() + " " + client.getLastName())
                .clientBirthDate(response.getAppClientProfile().getBirthDate())
                .clientAge(response.getAppClientProfile().getAge())
                .clientGender(response.getAppClientProfile().getGender())
                .dateEnrolled(response.getAppClientProfile().getDateEnrolled())

                .guardianFullName(guardian.getFirstName() + " " + guardian.getLastName())
                .guardianEmail(guardian.getEmail())
                .guardianContactNo(guardian.getContactNumber())

                .currentAssignmentCount(assigmentHistory.size())
                .currentActiveCount(activeCount)
                .currentEndedCount(endedCount)

                .caseManagerFullName(
                        caseManager != null && caseManager.getAssignee() != null
                                ? caseManager.getAssignee().getFirstName() + " "
                                + caseManager.getAssignee().getLastName()
                                : "-"
                )
                .caseManagerPosition(
                        caseManager != null && caseManager.getAssignee() != null
                                ? caseManager.getAssignee().getEmploymentInformation().getPosition()
                                : "-"
                )

                .diagnosisConcerns(response.getDiagnosisConcerns())
                .programType(client.getProgramType())
                .status(response.getStatus())
                .branch(response.getBranch())
                .notes(response.getNotes())
                .assignedAt(response.getAssignedAt())
                .unassignedAt(response.getUnassignedAt())

                .employeeId(
                        assignee != null
                                ? assignee.getEmploymentInformation().getEmployeeId()
                                : null
                )
                .assigneeFullName(
                        assignee != null
                                ? assignee.getFirstName() + " " + assignee.getLastName()
                                : "-"
                )
                .assigneePosition(
                        assignee != null
                                ? assignee.getEmploymentInformation().getPosition()
                                : "-"
                )
                .assignmentRole(response.getAssignmentRole())
                .history(buildAssignmentHistories(assigmentHistory))
                .build();
    }

    private List<AssignedClientResponse.AssignmentHistoryResponse> buildAssignmentHistories(List<AssignmentHistory> assigmentHistory){
        return assigmentHistory.stream()
                .map(item -> AssignedClientResponse.AssignmentHistoryResponse.builder()
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

    private ClientRegistrationResponse mapToClientRegistrationResponse(
            AppClientProfile client) {

        ClientRegistrationResponse.AccountAccessDTO accountAccess = null;

        /*
         * Map App User account information only when linked
         */
        if (client.getAppUser() != null) {
            AppUser appUser = client.getAppUser();

            accountAccess = new ClientRegistrationResponse.AccountAccessDTO(
                    appUser.getUsername(),
                    appUser.getLastPassword(),
                    appUser.getEmail(),
                    appUser.getStatus() != null
                            ? appUser.getStatus().name()
                            : null
            );
        } else {
            accountAccess = new ClientRegistrationResponse.AccountAccessDTO(
                    null,
                    null,
                    null,
                    null
            );
        }

        return ClientRegistrationResponse.builder()
                .id(client.getId())
                .clientId(client.getClientId())
                .clientStudentId(client.getClientStudentId())
                .firstName(client.getFirstName())
                .middleName(client.getMiddleName())
                .lastName(client.getLastName())
                .age(client.getAge())
                .birthDate(client.getBirthDate())
                .gender(client.getGender())
                .dateEnrolled(client.getDateEnrolled())
                .diagnosisConcerns(client.getDiagnosisConcerns())
                .programType(client.getProgramType())
                .assignmentStatus(client.getAssignmentStatus())
                .branch(client.getBranch())
                .enrollmentStatus(client.getEnrollmentStatus())
                .profileImageUrl(client.getProfileImageUrl())
                .parents(mapParents(client))

                /* Account Access */
                .accountAccess(accountAccess)
                .build();
    }
    private List<ClientRegistrationResponse.ParentGuardian> mapParents(
            AppClientProfile client) {

        if (client.getAppParentGuardian() == null) {
            return List.of();
        }

        return client.getAppParentGuardian()
                .stream()
                .map(parent -> new ClientRegistrationResponse.ParentGuardian(
                        parent.getFirstName(),
                        parent.getMiddleName(),
                        parent.getLastName(),
                        parent.getContactNumber(),
                        parent.getEmail(),
                        parent.getRelationshipToClient(),
                        parent.getGender(),
                        parent.getAddress()
                ))
                .toList();
    }

    private String createDescription(AppEmployeeProfile appEmployeeProfile, AssignClientRequest request){
        return "Client assigned to "
                + appEmployeeProfile.getFirstName()
                + " "
                + appEmployeeProfile.getLastName()
                + " as "
                + request.getRole().getDisplayName()
                + ".";
    }

    private AppEmployeeProfile findAppEmployeeProfileByEmployeeId(String uuid, String employeeId) throws ServiceException {
        return appEmployeeProfileRepository
                .findByEmployeeId(employeeId)
                .orElseThrow(() -> {
                    loggingService.error(uuid, getClass().getName(), "Employee not found with ID: " + employeeId, HttpStatus.NOT_FOUND.value());
                    return new ServiceException(HttpStatus.NOT_FOUND.value(), "Employee not found with ID: " + employeeId);
                });
    }

    private AppClientProfile findAppClientProfileByClientId(String uuid, String clientId) throws ServiceException {
        return clientProfileRepository.findByClientId(clientId)
                .orElseThrow(() -> {
                    loggingService.error(uuid, getClass().getName(), "Client not found with ID: " + clientId, HttpStatus.NOT_FOUND.value());
                    return new ServiceException(HttpStatus.NOT_FOUND.value(), "Client not found with ID: " + clientId);
                });
    }

}