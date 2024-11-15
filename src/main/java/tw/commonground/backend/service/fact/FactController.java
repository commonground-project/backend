package tw.commonground.backend.service.fact;

import org.springframework.web.bind.annotation.*;
import tw.commonground.backend.service.fact.dao.FactRequest;
import tw.commonground.backend.service.fact.dao.FactResponse;

import java.util.List;
import java.util.UUID;

@RestController
public class FactController {
    private final FactService factService;

    public FactController(FactService factService) {
        this.factService = factService;
    }

    @GetMapping("/facts")
    public List<FactResponse> listFacts(@RequestParam("page") int page,
                                        @RequestParam("sort") String sort,
                                        @RequestParam("size") int size) {
        return factService.getFacts(page, size, sort.split(",")[0], sort.split(",")[1]);
    }

    @PostMapping("/facts")
    public FactResponse createFact(@RequestBody FactRequest factRequest) {
        return factService.createFact(factRequest);
    }

    @GetMapping("/fact/{id}")
    public FactResponse getFact(@PathVariable String id) {
        return factService.getFact(UUID.fromString(id));
    }

    @PutMapping("/fact/{id}")
    public FactResponse updateFact(@PathVariable String id, @RequestBody FactRequest factRequest) {
        return factService.updateFact(UUID.fromString(id), factRequest);
    }

    @DeleteMapping("/fact/{id}")
    public void deleteFact(@PathVariable String id) {
        factService.deleteFact(UUID.fromString(id));
    }
}
