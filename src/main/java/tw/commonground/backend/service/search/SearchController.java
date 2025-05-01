package tw.commonground.backend.service.search;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tw.commonground.backend.shared.pagination.PaginationMapper;
import tw.commonground.backend.shared.pagination.WrappedPaginationResponse;
import tw.commonground.backend.shared.tracing.Traced;

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
    public ResponseEntity<WrappedPaginationResponse<List<String>>> search(
            @RequestParam String query,
            Pageable pageable) {
        Page<String> resultPage = searchService.search(query, pageable);
        return ResponseEntity.ok(new WrappedPaginationResponse<>(
                resultPage.getContent(),
                PaginationMapper.toResponse(resultPage)
        ));
    }

}