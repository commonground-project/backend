package tw.commonground.backend.service.fact.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class LinkFactsRequest {
    private List<UUID> factIds;
}
