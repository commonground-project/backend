package tw.commonground.backend.shared.content;

import org.junit.jupiter.api.Test;
import tw.commonground.backend.exception.ValidationException;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class ContentContainFactParserTest {

    @Test
    void testConvertLinkIntToUuid() {
        String input = "[Example](0,1)";
        List<UUID> facts = List.of(UUID.fromString("11111111-1111-1111-1111-111111111111"),
                UUID.fromString("22222222-2222-2222-2222-222222222222"));


        String result = ContentContainFactParser.convertLinkIntToUuid(input, facts);

        String expectedOutput = "[Example](11111111-1111-1111-1111-111111111111,22222222-2222-2222-2222-222222222222)";
        assertThat(result).isEqualTo(expectedOutput);
    }

    @Test
    void testConvertLinkIntToUuid_withInvalidPosition() {
        String input = "[Example](0,3)";
        List<UUID> facts = List.of(UUID.fromString("11111111-1111-1111-1111-111111111111"),
                UUID.fromString("22222222-2222-2222-2222-222222222222"));

        assertThatThrownBy(() -> ContentContainFactParser.convertLinkIntToUuid(input, facts))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Invalid position: 3, in link text: Example");
    }

    @Test
    void testSeparateContentAndFacts() {
        String input = "[Example](11111111-1111-1111-1111-111111111111,22222222-2222-2222-2222-222222222222)";
        String expectedText = "[Example](0,1)";
        List<UUID> expectedFacts = List.of(UUID.fromString("11111111-1111-1111-1111-111111111111"),
                UUID.fromString("22222222-2222-2222-2222-222222222222"));

        ContentContainFact result = ContentContainFactParser.separateContentAndFacts(input);

        assertThat(result.getText()).isEqualTo(expectedText);
        assertThat(result.getFacts()).isEqualTo(expectedFacts);
    }

    @Test
    void testSeparateContentAndFacts_withDuplicateUUIDs() {
        String input = "[Example](11111111-1111-1111-1111-111111111111,11111111-1111-1111-1111-111111111111)";
        String expectedText = "[Example](0,0)";
        List<UUID> expectedFacts = List.of(UUID.fromString("11111111-1111-1111-1111-111111111111"));

        ContentContainFact result = ContentContainFactParser.separateContentAndFacts(input);

        assertThat(result.getText()).isEqualTo(expectedText);
        assertThat(result.getFacts()).isEqualTo(expectedFacts);
    }

    @Test
    void testSeparateContentAndFacts_withNoLinks() {
        String input = "This is a plain text without links.";
        String expectedText = "This is a plain text without links.";
        List<UUID> expectedFacts = List.of();

        ContentContainFact result = ContentContainFactParser.separateContentAndFacts(input);

        assertThat(result.getText()).isEqualTo(expectedText);
        assertThat(result.getFacts()).isEqualTo(expectedFacts);
    }
}