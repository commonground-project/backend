package tw.commonground.backend.service.read.entity;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@ToString
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class ReadKey implements Serializable {
    private Long userId;
    private UUID objectId;
    @Enumerated(EnumType.STRING)
    private ReadObjectType objectType;
}