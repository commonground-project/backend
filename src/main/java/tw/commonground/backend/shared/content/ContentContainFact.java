package tw.commonground.backend.shared.content;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class ContentContainFact {

    private String text;

    private List<UUID> facts;
}
