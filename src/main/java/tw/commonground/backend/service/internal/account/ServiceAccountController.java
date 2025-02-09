package tw.commonground.backend.service.internal.account;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import tw.commonground.backend.service.internal.account.dto.*;
import tw.commonground.backend.service.internal.account.entity.ServiceAccountEntity;
import tw.commonground.backend.service.user.entity.FullUserEntity;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/internal")
public class ServiceAccountController {

    private final ServiceAccountService serviceAccountService;

    public ServiceAccountController(ServiceAccountService serviceAccountService) {
        this.serviceAccountService = serviceAccountService;
    }

    @GetMapping("/service-account/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ServiceAccountResponse> getServiceAccountMe(@AuthenticationPrincipal FullUserEntity user) {
        if (user instanceof TokenUserDetails) {
            return ResponseEntity.ok(ServiceAccountMapper.toResponse((TokenUserDetails) user));
        } else {
            throw new IllegalArgumentException("User is not a service account");
        }
    }

    @GetMapping("/service-accounts")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ServiceAccountResponse>> getServiceAccounts() {
        List<ServiceAccountResponse> response = ServiceAccountMapper
                .toResponses(serviceAccountService.getServiceAccounts());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/service-accounts")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CreatedServiceAccountResponse> createServiceAccount(
            @Valid @RequestBody ServiceAccountRequest request) {

        UUID token = UUID.randomUUID();

        ServiceAccountEntity serviceAccountEntity = serviceAccountService.createServiceAccount(request.getName(),
                request.getRole(), token.toString());

        return ResponseEntity.ok(ServiceAccountMapper.toCreatedResponse(serviceAccountEntity));
    }

    @DeleteMapping("/service-account/{serviceId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteServiceAccount(@PathVariable UUID serviceId) {
        serviceAccountService.deleteServiceAccount(serviceId);
        return ResponseEntity.noContent().build();
    }
}
