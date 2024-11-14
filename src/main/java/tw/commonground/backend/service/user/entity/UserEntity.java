package tw.commonground.backend.service.user.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;


@Entity
@Getter
@Setter
public class UserEntity {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    private String username;

    @Column(unique = true)
    private String email;
    private String nickname;
    private byte[] profileImage;
    private String role;

    public void setProfileImageUrl(String profileImageUrl) {
        try (InputStream in = new URL(profileImageUrl).openStream();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            this.profileImage = out.toByteArray();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
