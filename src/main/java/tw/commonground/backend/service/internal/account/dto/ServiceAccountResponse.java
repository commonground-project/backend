package tw.commonground.backend.service.internal.account.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import tw.commonground.backend.security.UserRole;

import java.util.UUID;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class ServiceAccountResponse {

    private UUID id;

    private String name;

    private UserRole role;

}
