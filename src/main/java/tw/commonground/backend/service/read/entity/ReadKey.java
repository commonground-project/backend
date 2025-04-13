package tw.commonground.backend.service.read.entity;

import jakarta.persistence.Embeddable;
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
}