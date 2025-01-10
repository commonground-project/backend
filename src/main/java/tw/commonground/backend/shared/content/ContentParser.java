package tw.commonground.backend.shared.content;

import tw.commonground.backend.exception.ValidationException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ContentParser {

    public static final String CONTENT_FACT_LINK_REGEX = "\\[([^]]+)]\\(([^)]+)\\)";

    public static final String CONTENT_QUOTE_LINK_REGEX = "\\[\\[([^]]+)]]\\(([^)]+)\\)";

    public static final Pattern CONTENT_FACT_LINK_PATTERN = Pattern.compile(CONTENT_FACT_LINK_REGEX);

    public static final Pattern CONTENT_QUOTE_LINK_PATTERN = Pattern.compile(CONTENT_QUOTE_LINK_REGEX);

    private ContentParser() {
        // hide constructor
    }

    public static String convertLinkIntToUuid(String content, List<UUID> facts) {
        StringBuilder replacedText = new StringBuilder();

        Matcher matcher = CONTENT_FACT_LINK_PATTERN.matcher(content);

        replaceIntToUuid(facts, replacedText, matcher, false);

        return replacedText.toString();
    }

    public static String convertLinkIntToUuid(String content, List<UUID> facts, List<UUID> quotes) {
        StringBuilder replacedFactText = new StringBuilder();
        StringBuilder replacedQuoteText = new StringBuilder();

        Matcher factMatcher = CONTENT_FACT_LINK_PATTERN.matcher(content);
        replaceIntToUuid(facts, replacedFactText, factMatcher, false);

        Matcher quoteMatcher = CONTENT_QUOTE_LINK_PATTERN.matcher(replacedFactText.toString());
        replaceIntToUuid(quotes, replacedQuoteText, quoteMatcher, true);

        return replacedQuoteText.toString();
    }

    private static void replaceIntToUuid(List<UUID> linkId, StringBuilder replacedText, Matcher matcher,
                                         Boolean isQuote) {
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
            String newLink;
            if (isQuote) {
                newLink = "[[" + linkText + "]](" + String.join(",", uuidStrings) + ")";
            } else {
                newLink = "[" + linkText + "](" + String.join(",", uuidStrings) + ")";
            }

            matcher.appendReplacement(replacedText, newLink);
        }

        matcher.appendTail(replacedText);
    }

    public static ContentContainFact separateContentAndFacts(String text) {
        StringBuilder replacedText = new StringBuilder();

        Matcher matcher = CONTENT_FACT_LINK_PATTERN.matcher(text);
        List<UUID> uuids = new ArrayList<>();

        replaceUuidToInt(uuids, replacedText, matcher, false);

        ContentContainFact contentContainFact = new ContentContainFact();
        contentContainFact.setText(replacedText.toString());
        contentContainFact.setFacts(uuids);

        return contentContainFact;
    }

    public static ContentContainFact separateContentAndFacts(String text, List<UUID> existingFacts) {
        StringBuilder replacedText = new StringBuilder();

        Matcher matcher = CONTENT_FACT_LINK_PATTERN.matcher(text);
        List<UUID> uuids = new ArrayList<>(existingFacts);

        replaceUuidToInt(uuids, replacedText, matcher, false);

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
        replaceUuidToInt(factUuids, replacedFactText, factMatcher, false);

        Matcher quoteMatcher = CONTENT_QUOTE_LINK_PATTERN.matcher(replacedFactText.toString());
        List<UUID> quoteUuids = new ArrayList<>();
        replaceUuidToInt(quoteUuids, replacedQuoteText, quoteMatcher, true);

        ContentReply contentReply = new ContentReply();
        contentReply.setText(replacedQuoteText.toString());
        contentReply.setFacts(factUuids);
        contentReply.setQuotes(quoteUuids);

        return contentReply;
    }

    public static ContentReply separateContentAndReplies(String text,
                                                       List<UUID> existingFactUuids,
                                                       List<UUID> existingQuoteUuids) {
        StringBuilder replacedFactText = new StringBuilder();
        StringBuilder replacedQuoteText = new StringBuilder();

        Matcher factMatcher = CONTENT_FACT_LINK_PATTERN.matcher(text);
        List<UUID> factUuids = new ArrayList<>(existingFactUuids);
        replaceUuidToInt(factUuids, replacedFactText, factMatcher, false);

        Matcher quoteMatcher = CONTENT_QUOTE_LINK_PATTERN.matcher(replacedFactText.toString());
        List<UUID> quoteUuids = new ArrayList<>(existingQuoteUuids);
        replaceUuidToInt(quoteUuids, replacedQuoteText, quoteMatcher, true);

        ContentReply contentReply = new ContentReply();
        contentReply.setText(replacedQuoteText.toString());
        contentReply.setFacts(factUuids);
        contentReply.setQuotes(quoteUuids);

        return contentReply;
    }



    private static void replaceUuidToInt(List<UUID> uuids, StringBuilder replacedText, Matcher matcher,
                                         Boolean isQuotes) {
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

            String newLink;
            if (isQuotes) {
                newLink = "[[" + linkText + "]]("
                        + String.join(",", positions.stream().map(Object::toString).toList()) + ")";
            } else {
                newLink = "[" + linkText + "]("
                        + String.join(",", positions.stream().map(Object::toString).toList()) + ")";
            }
            matcher.appendReplacement(replacedText, newLink);
        }

        matcher.appendTail(replacedText);
    }
}

