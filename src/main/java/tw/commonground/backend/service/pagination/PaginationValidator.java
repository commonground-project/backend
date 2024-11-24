package tw.commonground.backend.service.pagination;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.domain.Sort;
import tw.commonground.backend.exception.EntityNotFoundException;

import java.util.*;

public class PaginationValidator {
    public Pageable validatePaginationRequest(PaginationRequest paginationRequest, Set<String> sortableColumn) {
        List<Order> orders = new ArrayList<>();
        Arrays.stream(paginationRequest.getSort().split(",")).toList().forEach(order -> {
            List<String> sep = Arrays.stream(order.split(";")).toList();
            if (!sortableColumn.contains(sep.getFirst())) {
                throw new EntityNotFoundException("Invalid sort column");
            }
            if (Objects.equals(sep.getLast(), "desc")) {
                orders.add(Order.desc(sep.getFirst()));
            }
            else {
                orders.add(Order.asc(sep.getFirst()));
            }
        });

        return PageRequest.of(paginationRequest.getPage(), paginationRequest.getSize(), Sort.by(orders));
    }
}
