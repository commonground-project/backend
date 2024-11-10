package tw.commonground.backend.service.fact;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static java.util.stream.Collectors.toList;

@Service
public class FactService {

    @Autowired
    private FactRepository factRepository;
    private final Mapper mapper = new Mapper();

    public String generateTestData() {
        List<FactEntity> listFacts = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            listFacts.add(
                FactEntity.builder()
                    .title(String.valueOf(100 - i))
                    .authorId(i + 1L)
                    .authorName(String.valueOf(i))
                    .build()
            );
        }

        factRepository.saveAll(listFacts);
        return "Test Data Generated";
    }

    public List<FactResponse> getFacts(int page, int size, String column, String mode) {
        Sort sorted = Sort.by((Objects.equals(mode, "asc") ? Sort.Order.by(column) : Sort.Order.desc(column)));
        Pageable pageable = PageRequest.of(page, size, sorted);
        Page<FactEntity> pageFacts = factRepository.findAll(pageable);
        return pageFacts.getContent()
                .stream()
                .map(mapper::toResponse)
                .collect(toList());
    }

    public FactResponse getFact(UUID id) {
        return factRepository.findById(id).map(mapper::toResponse).orElse(null);
    }

    public FactEntity createFact(String title) {
        FactEntity factEntity = FactEntity.builder()
                .title(title)
                .build();

        FactEntity result = factRepository.save(factEntity);

        return result;
    }
}
