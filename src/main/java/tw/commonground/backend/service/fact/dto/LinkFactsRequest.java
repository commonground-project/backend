package tw.commonground.backend.service.fact.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@ToString
public class LinkFactsRequest {
    private List<UUID> factIds;
}
