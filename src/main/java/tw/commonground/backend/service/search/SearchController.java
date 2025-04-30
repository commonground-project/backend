package tw.commonground.backend.service.search;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tw.commonground.backend.shared.tracing.Traced;

import java.util.Collections;
import java.util.List;

@Traced
@RestController
public class SearchController {

    private final SearchService searchService;

    @Autowired
    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping("/searchFact")
    public List<String> search(@RequestParam String query) {
        try {
            return searchService.search(query);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}
