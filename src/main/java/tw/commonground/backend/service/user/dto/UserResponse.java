package tw.commonground.backend.service.user.dto;


import lombok.*;
import tw.commonground.backend.service.user.entity.UserGender;
import tw.commonground.backend.service.user.entity.UserOccupation;

import java.time.LocalDate;

@Getter
@Setter
@ToString
public class UserResponse {
    private String username;
    private String nickname;
    private String email;
    private String role;
    private String occupation;
    private String gender;
    private String birthdate;

    public UserResponse(String username,
                        String nickname,
                        String email,
                        String role,
                        UserOccupation occupation,
                        UserGender gender,
                        LocalDate birthdate) {
        this.username = username == null ? "" : username;
        this.nickname = nickname == null ? "" : nickname;
        this.email = email == null ? "" : email;
        this.role = role == null ? "" : role;
        this.occupation = occupation == null ? "" : occupation.name();
        this.gender = gender == null ? "" : gender.name();
        this.birthdate = birthdate == null ? "" : birthdate.toString();
    }
}
