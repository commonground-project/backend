package tw.commonground.backend.service.fact;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
public class FactController {
    @Autowired
    private FactService factService;

    @GetMapping("/testData")
    public String testData() {
        return factService.generateTestData();
    }

    @GetMapping("/facts")
    public List<FactResponse> listFacts(@RequestParam("page") int page, @RequestParam("sort") String sort, @RequestParam("size") int size) {
        return factService.getFacts(page, size, sort.split(",")[0], sort.split(",")[1]);
    }

    @GetMapping("/fact/{id}")
    public FactResponse getFact(@PathVariable String id) {
        return factService.getFact(UUID.fromString(id));
    }
}
