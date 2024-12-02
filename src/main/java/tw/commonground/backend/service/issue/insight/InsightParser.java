package tw.commonground.backend.service.issue.insight;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class InsightParser {

    public static final String INSIGHT_LINK_REGEX = "\\[([^]]+)]\\(([^)]+)\\)";

    public static final Pattern INSIGHT_LINK_PATTERN = Pattern.compile(INSIGHT_LINK_REGEX);

    private InsightParser() {
        // hide constructor
    }

    public static void main(String[] args) {
        String insight = "[San Francisco, CA](0) — In a significant leap forward for electric vehicle (EV) technology, researchers at [GreenTech Innovations](1) announced today the development of a [new battery](1,2) that could revolutionize the industry";
        System.out.println(insight);

        String text = convertLinkIntToUuid(insight, List.of(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID()));
        System.out.println(text);

        Insight insightObj = separateInsightAndFacts(text);
        System.out.println(insightObj.getText());
        System.out.println(insightObj.getFacts());
    }

    public static String convertLinkIntToUuid(String insight, List<UUID> facts) {
        StringBuilder replacedText = new StringBuilder();

        Matcher matcher = INSIGHT_LINK_PATTERN.matcher(insight);
        while (matcher.find()) {
            String linkText = matcher.group(1);
            String linkPositions = matcher.group(2);

            List<UUID> uuids = new ArrayList<>();
            for (String pos : linkPositions.split(",")) {
                pos = pos.trim();
                int posInt = Integer.parseInt(pos);
                uuids.add(facts.get(posInt));
            }

            List<String> uuidStrings = uuids.stream().map(UUID::toString).toList();
            String newLink = "[" + linkText + "](" + String.join(",", uuidStrings) + ")";
            matcher.appendReplacement(replacedText, newLink);
        }

        matcher.appendTail(replacedText);
        return replacedText.toString();
    }

    public static Insight separateInsightAndFacts(String text) {
        StringBuilder replacedText = new StringBuilder();

        Matcher matcher = INSIGHT_LINK_PATTERN.matcher(text);
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

            String newLink = "[" + linkText + "](" + String.join(",", positions.stream().map(Object::toString).toList()) + ")";
            matcher.appendReplacement(replacedText, newLink);
        }

        matcher.appendTail(replacedText);

        Insight insight = new Insight();
        insight.setText(replacedText.toString());
        insight.setFacts(uuids);

        return insight;
    }
}
