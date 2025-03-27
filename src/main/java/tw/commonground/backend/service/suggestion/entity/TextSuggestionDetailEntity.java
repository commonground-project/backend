package tw.commonground.backend.service.suggestion.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TextSuggestionDetailEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "text_suggestion_id", nullable = false)
    @ToString.Exclude
    private TextSuggestionEntity textSuggestion;

    private String message;

    private String feedback;

    private String replacement;
}
