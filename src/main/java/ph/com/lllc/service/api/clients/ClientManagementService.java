package ph.com.lllc.service.api.clients;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ph.com.lllc.dto.response.CommonResponse;
import ph.com.lllc.dto.staff.clients.ClientRegistrationRequest;
import ph.com.lllc.dto.staff.clients.ClientRegistrationResponse;
import ph.com.lllc.entity.user.client.AppClientProfile;
import ph.com.lllc.entity.user.client.AppParentGuardian;
import ph.com.lllc.repository.ClientProfileRepository;
import ph.com.lllc.service.util.logging.LoggingService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ClientManagementService {

    private final ClientProfileRepository clientProfileRepository;
    private final LoggingService loggingService;

    @Transactional
    public CommonResponse registerClient(String uuid, ClientRegistrationRequest request) {

        /* Create Client Profile */
        AppClientProfile clientProfile = new AppClientProfile();
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

        loggingService.info(uuid, this.getClass().getName(), "", "Saving new client profile...");
        AppClientProfile savedClient = clientProfileRepository.save(clientProfile);

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("client", savedClient);

        return CommonResponse.builder()
                .returnCode(HttpStatus.OK.value())
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

    private ClientRegistrationResponse mapToClientRegistrationResponse(
            AppClientProfile client) {

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