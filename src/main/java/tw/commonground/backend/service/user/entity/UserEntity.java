package tw.commonground.backend.service.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import javax.management.relation.Role;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
public class UserEntity {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    private String username;
    private String email;
    private String nickname;
    private byte[] profileImage;
    private String role;
}
