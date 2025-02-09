package tw.commonground.backend.shared.pagination;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import tw.commonground.backend.exception.ValidationException;

import java.util.Objects;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SuppressWarnings("MagicNumber")
class PaginationParserTest {

    private Validator validator;

    private final int maxSize = 50;

    private final Set<String> sortableColumn = Set.of("name", "age", "createdAt");

    private final PaginationParser parser = new PaginationParser(sortableColumn, maxSize);

    @BeforeEach
    void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void testValidPaginationRequest() {
        PaginationRequest request = new PaginationRequest();
        request.setPage(0);
        request.setSize(10);
        request.setSort("name;asc");

        Set<ConstraintViolation<PaginationRequest>> violations = validator.validate(request);
        assertThat(violations).isEmpty();
    }

    @Test
    void testInvalidPage() {
        PaginationRequest request = new PaginationRequest();
        request.setPage(-1); // invalid page
        request.setSize(10);

        Set<ConstraintViolation<PaginationRequest>> violations = validator.validate(request);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Page must be more than 0");
    }

    @Test
    void testInvalidSize() {
        PaginationRequest request = new PaginationRequest();
        request.setPage(0);
        request.setSize(0); // invalid size

        Set<ConstraintViolation<PaginationRequest>> violations = validator.validate(request);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Size must be more than 1");
    }

    @Test
    void testMultipleViolations() {
        PaginationRequest request = new PaginationRequest();
        request.setPage(-1); // invalid page
        request.setSize(0);  // invalid size

        Set<ConstraintViolation<PaginationRequest>> violations = validator.validate(request);
        assertThat(violations).hasSize(2);
    }

    @Test
    void testGreaterThanMaxSize() {
        PaginationRequest request = new PaginationRequest(0, 51, null);
        assertThatThrownBy(() -> parser.parsePageable(request))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Page size must be less than or equal to " + maxSize);
    }

    @Test
    void testMultipleValidSort() {
        PaginationRequest request = new PaginationRequest(0, 10, "name;asc,age;desc");
        Pageable pageable = parser.parsePageable(request);

        assertThat(Objects.requireNonNull(pageable.getSort().getOrderFor("name"))
                .getDirection()).isEqualTo(Sort.Direction.ASC);
        assertThat(Objects.requireNonNull(pageable.getSort().getOrderFor("age"))
                .getDirection()).isEqualTo(Sort.Direction.DESC);
    }

    @Test
    void testNullSort() {
        PaginationRequest request = new PaginationRequest(0, 10, null);
        Pageable pageable = parser.parsePageable(request);

        assertThat(Objects.requireNonNull(pageable.getSort().getOrderFor("createdAt"))
                .getDirection()).isEqualTo(Sort.Direction.DESC);
    }

    @Test
    void testBlankSort() {
        PaginationRequest request = new PaginationRequest(0, 10, " ");
        Pageable pageable = parser.parsePageable(request);

        assertThat(Objects.requireNonNull(pageable.getSort().getOrderFor("createdAt"))
                .getDirection()).isEqualTo(Sort.Direction.DESC);
    }

    @Test
    void testInvalidSortFormatMissingSemicolon() {
        PaginationRequest request = new PaginationRequest(0, 10, "name");
        assertThatThrownBy(() -> parser.parsePageable(request))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Invalid sort format, expected 'column;direction'.");
    }

    @Test
    void testInvalidSortDirection() {
        PaginationRequest request = new PaginationRequest(0, 10, "name;invalidDirection");
        assertThatThrownBy(() -> parser.parsePageable(request))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Invalid sort direction");
    }

    @Test
    void testInvalidSortColumn() {
        PaginationRequest request = new PaginationRequest(0, 10, "invalidColumn;asc");
        assertThatThrownBy(() -> parser.parsePageable(request))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Invalid sort column");
    }

    @Test
    void testMultipleInvalidSortColumns() {
        PaginationRequest request = new PaginationRequest(0, 10, "invalidColumn1;asc");
        assertThatThrownBy(() -> parser.parsePageable(request))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Invalid sort column");
    }

    @Test
    void testMixedValidAndInvalidSortColumns() {
        PaginationRequest request = new PaginationRequest(0, 10, "name;asc,invalidColumn;desc");
        assertThatThrownBy(() -> parser.parsePageable(request))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Invalid sort column");
    }

    @Test
    void testDuplicateSortColumns() {
        PaginationRequest request = new PaginationRequest(0, 10, "name;asc,name;desc");
        Pageable pageable = parser.parsePageable(request);

        // duplicate sort columns are allowed, but only the first one is used
        assertThat(Objects.requireNonNull(pageable.getSort().getOrderFor("name"))
                .getDirection()).isEqualTo(Sort.Direction.ASC);
    }

    @Test
    void testCaseInsensitiveSortDirection() {
        PaginationRequest request = new PaginationRequest(0, 10, "name;ASC,age;DeSc");
        Pageable pageable = parser.parsePageable(request);

        assertThat(Objects.requireNonNull(pageable.getSort().getOrderFor("name"))
                .getDirection()).isEqualTo(Sort.Direction.ASC);
        assertThat(Objects.requireNonNull(pageable.getSort().getOrderFor("age"))
                .getDirection()).isEqualTo(Sort.Direction.DESC);
    }
}
