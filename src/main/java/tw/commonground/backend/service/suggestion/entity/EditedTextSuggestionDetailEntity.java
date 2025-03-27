package tw.commonground.backend.service.suggestion.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class EditedTextSuggestionDetailEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "edited_text_suggestion_id", nullable = false)
    @ToString.Exclude
    private EditedTextSuggestionEntity editedTextSuggestion;

    private String editedMessage;

    private String feedback;

    private String replacement;
}
