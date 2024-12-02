package tw.commonground.backend.service.issue.insight;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class Insight {

    private String text;

    private List<UUID> facts;
}
