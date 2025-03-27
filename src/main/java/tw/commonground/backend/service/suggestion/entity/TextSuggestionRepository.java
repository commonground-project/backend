package tw.commonground.backend.service.suggestion.entity;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TextSuggestionRepository extends JpaRepository<TextSuggestionEntity, Long> {
}
