package tw.commonground.backend.shared.content;

import tw.commonground.backend.exception.ValidationException;
import tw.commonground.backend.service.reply.dto.QuoteReply;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ContentParser {

    public static final String CONTENT_FACT_LINK_REGEX = "\\[([^]]+)]\\(([^)]+)\\)";

    public static final String CONTENT_QUOTE_LINK_REGEX = "\\[\\[Quote]]\\(([^)]+)\\)";

    public static final Pattern CONTENT_FACT_LINK_PATTERN = Pattern.compile(CONTENT_FACT_LINK_REGEX);

    public static final Pattern CONTENT_QUOTE_LINK_PATTERN = Pattern.compile(CONTENT_QUOTE_LINK_REGEX);

    private ContentParser() {
        // hide constructor
    }

    public static String convertLinkIntToUuid(String content, List<UUID> facts) {
        StringBuilder replacedText = new StringBuilder();

        Matcher matcher = CONTENT_FACT_LINK_PATTERN.matcher(content);

        replaceIntToUuidForFact(facts, replacedText, matcher);

        return replacedText.toString();
    }

    public static String convertLinkIntToUuid(String content, List<UUID> facts, List<QuoteReply> quotes) {
        StringBuilder replacedFactText = new StringBuilder();
        StringBuilder replacedQuoteText = new StringBuilder();

        Matcher factMatcher = CONTENT_FACT_LINK_PATTERN.matcher(content);
        replaceIntToUuidForFact(facts, replacedFactText, factMatcher);

        Matcher quoteMatcher = CONTENT_QUOTE_LINK_PATTERN.matcher(replacedFactText.toString());
        replaceIntToUuidForReply(quotes, replacedQuoteText, quoteMatcher);

        return replacedQuoteText.toString();
    }

    private static void replaceIntToUuidForFact(List<UUID> linkId, StringBuilder replacedText, Matcher matcher) {
        while (matcher.find()) {
            String linkText = matcher.group(1);
            String linkPositions = matcher.group(2);

            List<UUID> uuids = new ArrayList<>();
            for (String pos : linkPositions.split(",")) {
                pos = pos.trim();
                int posInt = Integer.parseInt(pos);
                if (posInt < 0 || posInt >= linkId.size()) {
                    throw new ValidationException("Invalid position: " + posInt + ", in link text: " + linkText);
                }

                uuids.add(linkId.get(posInt));
            }

            List<String> uuidStrings = uuids.stream().map(UUID::toString).toList();
            String newLink = "[" + linkText + "](" + String.join(",", uuidStrings) + ")";

            matcher.appendReplacement(replacedText, newLink);
        }

        matcher.appendTail(replacedText);
    }

    private static void replaceIntToUuidForReply(List<QuoteReply> link, StringBuilder replacedText, Matcher matcher) {

        while (matcher.find()) {
            String linkPositions = matcher.group(1).trim();

            int posInt = Integer.parseInt(linkPositions);
            if (posInt < 0 || posInt >= link.size()) {
                throw new ValidationException("Invalid position: " + posInt);
            }

            QuoteReply quote = link.get(posInt);
            String newLink = "[[Quote]](" + quote.getReplyId() + "," + quote.getStart() + "," + quote.getEnd() + ")";

            matcher.appendReplacement(replacedText, newLink);
        }

        matcher.appendTail(replacedText);
    }

    public static ContentContainFact separateContentAndFacts(String text) {
        StringBuilder replacedText = new StringBuilder();

        Matcher matcher = CONTENT_FACT_LINK_PATTERN.matcher(text);
        List<UUID> uuids = new ArrayList<>();

        replaceUuidToIntForFact(uuids, replacedText, matcher);

        ContentContainFact contentContainFact = new ContentContainFact();
        contentContainFact.setText(replacedText.toString());
        contentContainFact.setFacts(uuids);

        return contentContainFact;
    }

    public static ContentContainFact separateContentAndFacts(String text, List<UUID> existingFacts) {
        StringBuilder replacedText = new StringBuilder();

        Matcher matcher = CONTENT_FACT_LINK_PATTERN.matcher(text);
        List<UUID> uuids = new ArrayList<>(existingFacts);

        replaceUuidToIntForFact(uuids, replacedText, matcher);

        ContentContainFact contentContainFact = new ContentContainFact();
        contentContainFact.setText(replacedText.toString());
        contentContainFact.setFacts(uuids);

        return contentContainFact;
    }

    public static ContentReply separateContentAndReplies(String text) {
        StringBuilder replacedFactText = new StringBuilder();
        StringBuilder replacedQuoteText = new StringBuilder();

        Matcher factMatcher = CONTENT_FACT_LINK_PATTERN.matcher(text);
        List<UUID> factUuids = new ArrayList<>();
        replaceUuidToIntForFact(factUuids, replacedFactText, factMatcher);

        Matcher quoteMatcher = CONTENT_QUOTE_LINK_PATTERN.matcher(replacedFactText.toString());
        List<QuoteReply> quotes = new ArrayList<>();
        replaceUuidToIntForReply(quotes, replacedQuoteText, quoteMatcher);

        ContentReply contentReply = new ContentReply();
        contentReply.setText(replacedQuoteText.toString());
        contentReply.setFacts(factUuids);
        contentReply.setQuotes(quotes);
        return contentReply;
    }

    public static ContentReply separateContentAndReplies(String text,
                                                       List<UUID> existingFactUuids) {
        StringBuilder replacedFactText = new StringBuilder();
        StringBuilder replacedQuoteText = new StringBuilder();

        Matcher factMatcher = CONTENT_FACT_LINK_PATTERN.matcher(text);
        List<UUID> factUuids = new ArrayList<>(existingFactUuids);
        replaceUuidToIntForFact(factUuids, replacedFactText, factMatcher);

        Matcher quoteMatcher = CONTENT_QUOTE_LINK_PATTERN.matcher(replacedFactText.toString());
        List<QuoteReply> quotes = new ArrayList<>();
        replaceUuidToIntForReply(quotes, replacedQuoteText, quoteMatcher);

        ContentReply contentReply = new ContentReply();
        contentReply.setText(replacedQuoteText.toString());
        contentReply.setFacts(factUuids);
        contentReply.setQuotes(quotes);

        return contentReply;
    }



    private static void replaceUuidToIntForFact(List<UUID> uuids, StringBuilder replacedText, Matcher matcher) {
        while (matcher.find()) {
            String linkText = matcher.group(1);
            String linkPositions = matcher.group(2);

            List<Integer> positions = new ArrayList<>();
            for (String uid : linkPositions.split(",")) {
                uid = uid.trim();
                UUID uuid = UUID.fromString(uid);

                if (!uuids.contains(uuid)) {
                    uuids.add(uuid);
                }

                positions.add(uuids.indexOf(uuid));
            }

            String newLink = "[" + linkText + "]("
                    + String.join(",", positions.stream().map(Object::toString).toList()) + ")";
            matcher.appendReplacement(replacedText, newLink);
        }

        matcher.appendTail(replacedText);
    }

    private static void replaceUuidToIntForReply(List<QuoteReply> quotes, StringBuilder replacedText, Matcher matcher) {
        while (matcher.find()) {
            String linkPositions = matcher.group(1);


            String[] quote = linkPositions.split(",");
            QuoteReply quoteReply = QuoteReply.builder()
                    .replyId(UUID.fromString(quote[0]))
                    .start(Integer.parseInt(quote[1]))
                    .end(Integer.parseInt(quote[2])).build();

            if (!quotes.contains(quoteReply)) {
                quotes.add(quoteReply);
            }

            String newLink = "[[Quote]](" + quotes.indexOf(quoteReply) + ")";
            matcher.appendReplacement(replacedText, newLink);
        }

        matcher.appendTail(replacedText);
    }
}

