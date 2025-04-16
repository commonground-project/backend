package tw.commonground.backend.service.suggestion.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EditedTextSuggestionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String editedText;

    @OneToMany(mappedBy = "editedTextSuggestion", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<EditedTextSuggestionDetailEntity> suggestions  = new ArrayList<>();
}
