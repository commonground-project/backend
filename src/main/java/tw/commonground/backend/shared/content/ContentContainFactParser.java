package tw.commonground.backend.shared.content;

import tw.commonground.backend.exception.ValidationException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ContentContainFactParser {

    public static final String CONTENT_LINK_REGEX = "\\[([^]]+)]\\(([^)]+)\\)";

    public static final Pattern CONTENT_LINK_PATTERN = Pattern.compile(CONTENT_LINK_REGEX);

    private ContentContainFactParser() {
        // hide constructor
    }

    public static String convertLinkIntToUuid(String content, List<UUID> facts) {
        StringBuilder replacedText = new StringBuilder();

        Matcher matcher = CONTENT_LINK_PATTERN.matcher(content);
        while (matcher.find()) {
            String linkText = matcher.group(1);
            String linkPositions = matcher.group(2);

            List<UUID> uuids = new ArrayList<>();
            for (String pos : linkPositions.split(",")) {
                pos = pos.trim();
                int posInt = Integer.parseInt(pos);
                if (posInt < 0 || posInt >= facts.size()) {
                    throw new ValidationException("Invalid position: " + posInt + ", in link text: " + linkText);
                }

                uuids.add(facts.get(posInt));
            }

            List<String> uuidStrings = uuids.stream().map(UUID::toString).toList();
            String newLink = "[" + linkText + "](" + String.join(",", uuidStrings) + ")";
            matcher.appendReplacement(replacedText, newLink);
        }

        matcher.appendTail(replacedText);
        return replacedText.toString();
    }

    public static ContentContainFact separateContentAndFacts(String text) {
        StringBuilder replacedText = new StringBuilder();

        Matcher matcher = CONTENT_LINK_PATTERN.matcher(text);
        List<UUID> uuids = new ArrayList<>();
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

        ContentContainFact contentContainFact = new ContentContainFact();
        contentContainFact.setText(replacedText.toString());
        contentContainFact.setFacts(uuids);

        return contentContainFact;
    }
}
