package tw.commonground.backend.service.fact.dao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tw.commonground.backend.service.reference.ReferenceRequest;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FactRequest {

    private String title;
    private List<ReferenceRequest> references;

}
