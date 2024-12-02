package tw.commonground.backend.pagination;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.domain.Sort;
import tw.commonground.backend.exception.EntityNotFoundException;

import java.util.*;

@AllArgsConstructor
public class PaginationValidator {

    private final Set<String> sortableColumn;

    private final int maxSize;

    public Pageable validatePaginationRequest(PaginationRequest paginationRequest) {
        if (paginationRequest.getSize() > maxSize) {
            throw new ValidationException("Page size must be less than or equal to " + maxSize);
        }

        List<Order> orders = new ArrayList<>();
        Arrays.stream(paginationRequest.getSort().split(",")).toList().forEach(order -> {
            List<String> sep = Arrays.stream(order.split(";")).toList();
            if (!sortableColumn.contains(sep.getFirst())) {
                throw new EntityNotFoundException("Invalid sort column");
            }
            if (Objects.equals(sep.getLast(), "desc")) {
                orders.add(Order.desc(sep.getFirst()));
            } else {
                orders.add(Order.asc(sep.getFirst()));
            }
        });

        return PageRequest.of(paginationRequest.getPage(), paginationRequest.getSize(), Sort.by(orders));
    }
}
