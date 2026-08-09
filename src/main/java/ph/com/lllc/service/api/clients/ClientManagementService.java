package ph.com.lllc.service.api.clients;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ph.com.lllc.dto.response.CommonResponse;
import ph.com.lllc.dto.staff.clients.ClientRegistrationRequest;
import ph.com.lllc.dto.staff.clients.ClientRegistrationResponse;
import ph.com.lllc.entity.user.client.AppClientProfile;
import ph.com.lllc.entity.user.client.AppParentGuardian;
import ph.com.lllc.entity.user.common.AppUser;
import ph.com.lllc.exception.ServiceException;
import ph.com.lllc.repository.AppUserRepository;
import ph.com.lllc.repository.ClientProfileRepository;
import ph.com.lllc.service.util.logging.LoggingService;
import ph.com.lllc.service.util.uuid.GenerateUUIDService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ClientManagementService {

    private final ClientProfileRepository clientProfileRepository;
    private final AppUserRepository appUserRepository;
    private final GenerateUUIDService generateUUIDService;
    private final LoggingService loggingService;

    @Transactional
    public CommonResponse registerClient(String uuid, ClientRegistrationRequest request) throws ServiceException {

        String clientUUID = generateUUIDService.generateUUID();

        /* Create Client Profile */
        AppClientProfile clientProfile = new AppClientProfile();
        clientProfile.setUuid(clientUUID);
        clientProfile.setFirstName(request.getFirstName());
        clientProfile.setMiddleName(request.getMiddleName());
        clientProfile.setLastName(request.getLastName());
        clientProfile.setAge(request.getAge());
        clientProfile.setBirthDate(request.getBirthDate());
        clientProfile.setGender(request.getGender());

        /* Enrollment Details */
        clientProfile.setDateEnrolled(request.getDateEnrolled());
        clientProfile.setDiagnosisConcern(request.getDiagnosisConcern());
        clientProfile.setProgramType(request.getProgramType());
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

    public ClientRegistrationResponse getClientProfileByUUID(String uuid) {
        AppClientProfile clientProfile = clientProfileRepository.findByUuid(uuid);
        return mapToClientRegistrationResponse(clientProfile);
    }

    @Transactional
    public CommonResponse updateClient(String uuid, ClientRegistrationRequest request) throws ServiceException {

        AppClientProfile clientProfile = clientProfileRepository.findByUuid(request.getUuid());

        if (clientProfile == null) {
            loggingService.error(uuid, getClass().getName(), "Client not found: " + request.getUuid(), HttpStatus.NOT_FOUND.value() );
            throw new ServiceException(HttpStatus.NOT_FOUND.value(), "Client not found: " + request.getUuid());
        }

        clientProfile.setFirstName(request.getFirstName());
        clientProfile.setMiddleName(request.getMiddleName());
        clientProfile.setLastName(request.getLastName());
        clientProfile.setAge(request.getAge());
        clientProfile.setBirthDate(request.getBirthDate());
        clientProfile.setGender(request.getGender());

        clientProfile.setDateEnrolled(request.getDateEnrolled());
        clientProfile.setDiagnosisConcern(request.getDiagnosisConcern());
        clientProfile.setProgramType(request.getProgramType());
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
                .uuid(client.getUuid())
                .clientStudentId(client.getClientStudentId())
                .firstName(client.getFirstName())
                .middleName(client.getMiddleName())
                .lastName(client.getLastName())
                .age(client.getAge())
                .birthDate(client.getBirthDate())
                .gender(client.getGender())
                .dateEnrolled(client.getDateEnrolled())
                .diagnosisConcern(client.getDiagnosisConcern())
                .programType(client.getProgramType())
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

}