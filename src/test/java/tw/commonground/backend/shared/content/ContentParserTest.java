package tw.commonground.backend.shared.content;

import org.junit.jupiter.api.Test;
import tw.commonground.backend.exception.ValidationException;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SuppressWarnings("MethodName")
class ContentParserTest {

    @Test
    void testConvertLinkIntToUuid() {
        String input = "[Example](0,1)";
        List<UUID> facts = List.of(UUID.fromString("11111111-1111-1111-1111-111111111111"),
                UUID.fromString("22222222-2222-2222-2222-222222222222"));


        String result = ContentParser.convertLinkIntToUuid(input, facts);

        String expectedOutput = "[Example](11111111-1111-1111-1111-111111111111,22222222-2222-2222-2222-222222222222)";
        assertThat(result).isEqualTo(expectedOutput);
    }

    @Test
    void testConvertLinkIntToUuid_withInvalidPosition() {
        String input = "[Example](0,3)";
        List<UUID> facts = List.of(UUID.fromString("11111111-1111-1111-1111-111111111111"),
                UUID.fromString("22222222-2222-2222-2222-222222222222"));

        assertThatThrownBy(() -> ContentParser.convertLinkIntToUuid(input, facts))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Invalid position: 3, in link text: Example");
    }

    @Test
    void testSeparateContentAndFacts() {
        String input = "[Example](11111111-1111-1111-1111-111111111111,22222222-2222-2222-2222-222222222222)";
        String expectedText = "[Example](0,1)";
        List<UUID> expectedFacts = List.of(UUID.fromString("11111111-1111-1111-1111-111111111111"),
                UUID.fromString("22222222-2222-2222-2222-222222222222"));

        ContentContainFact result = ContentParser.separateContentAndFacts(input);

        assertThat(result.getText()).isEqualTo(expectedText);
        assertThat(result.getFacts()).isEqualTo(expectedFacts);
    }

    @Test
    void testSeparateContentAndFacts_withDuplicateUUIDs() {
        String input = "[Example](11111111-1111-1111-1111-111111111111,11111111-1111-1111-1111-111111111111)";
        String expectedText = "[Example](0,0)";
        List<UUID> expectedFacts = List.of(UUID.fromString("11111111-1111-1111-1111-111111111111"));

        ContentContainFact result = ContentParser.separateContentAndFacts(input);

        assertThat(result.getText()).isEqualTo(expectedText);
        assertThat(result.getFacts()).isEqualTo(expectedFacts);
    }

    @Test
    void testSeparateContentAndFacts_withNoLinks() {
        String input = "This is a plain text without links.";
        String expectedText = "This is a plain text without links.";
        List<UUID> expectedFacts = List.of();

        ContentContainFact result = ContentParser.separateContentAndFacts(input);

        assertThat(result.getText()).isEqualTo(expectedText);
        assertThat(result.getFacts()).isEqualTo(expectedFacts);
    }

    @Test
    void testConvertLinkIntToUuid_withQuote() {
        String input = "[Example](0,1)[[Quote]](0)";
        List<UUID> facts = List.of(UUID.fromString("11111111-1111-1111-1111-111111111111"),
                UUID.fromString("22222222-2222-2222-2222-222222222222"));
        List<UUID> quotes = List.of(UUID.fromString("33333333-3333-3333-3333-333333333333"));


        String result = ContentParser.convertLinkIntToUuid(input, facts, quotes);

        String expectedOutput = "[Example](11111111-1111-1111-1111-111111111111,22222222-2222-2222-2222-222222222222)"
                + "[[Quote]](33333333-3333-3333-3333-333333333333)";
        assertThat(result).isEqualTo(expectedOutput);
    }

    @Test
    void testConvertLinkIntToUuid_withQuoteInvalidPosition() {
        String input = "[Example](0,1)[[Quote]](1)";
        List<UUID> facts = List.of(UUID.fromString("11111111-1111-1111-1111-111111111111"),
                UUID.fromString("22222222-2222-2222-2222-222222222222"));
        List<UUID> quotes = List.of(UUID.fromString("33333333-3333-3333-3333-333333333333"));

        assertThatThrownBy(() -> ContentParser.convertLinkIntToUuid(input, facts, quotes))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Invalid position: 1, in link text: Quote");
    }

    @Test
    void testSeparateContentAndReplies() {
        String input = "[Example](11111111-1111-1111-1111-111111111111,22222222-2222-2222-2222-222222222222)"
                +  "[[Quote]](33333333-3333-3333-3333-33333333)";
        String expectedText = "[Example](0,1)[[Quote]](0)";
        List<UUID> expectedFacts = List.of(UUID.fromString("11111111-1111-1111-1111-111111111111"),
                UUID.fromString("22222222-2222-2222-2222-222222222222"));
        List<UUID> expectedQuotes = List.of(UUID.fromString("33333333-3333-3333-3333-33333333"));

        ContentReply result = ContentParser.separateContentAndReplies(input);

        assertThat(result.getText()).isEqualTo(expectedText);
        assertThat(result.getFacts()).isEqualTo(expectedFacts);
        assertThat(result.getQuotes()).isEqualTo(expectedQuotes);
    }

    @Test
    void testSeparateContentAndReplies_withNoLinks() {
        String input = "This is a plain text without links.";
        String expectedText = "This is a plain text without links.";
        List<UUID> expectedFacts = List.of();
        List<UUID> expectedQuotes = List.of();

        ContentReply result = ContentParser.separateContentAndReplies(input);

        assertThat(result.getText()).isEqualTo(expectedText);
        assertThat(result.getFacts()).isEqualTo(expectedFacts);
        assertThat(result.getQuotes()).isEqualTo(expectedQuotes);
    }
}
