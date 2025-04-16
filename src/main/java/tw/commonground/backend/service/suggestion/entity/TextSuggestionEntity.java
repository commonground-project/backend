package tw.commonground.backend.service.suggestion.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TextSuggestionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String text;

    @OneToMany(mappedBy = "textSuggestion", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<TextSuggestionDetailEntity> suggestions  = new ArrayList<>();
}
