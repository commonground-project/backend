package tw.commonground.backend.shared.pagination;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.domain.Sort;
import tw.commonground.backend.exception.ValidationException;
import tw.commonground.backend.shared.tracing.Traced;

import java.util.*;

@Traced
@AllArgsConstructor
public class PaginationParser {

    private final Set<String> sortableColumn;

    private final int maxSize;

    public Pageable parsePageable(PaginationRequest paginationRequest) {
        validatePaginationRequest(paginationRequest);

        if (paginationRequest.getSort() == null || paginationRequest.getSort().isBlank()) {
            return PageRequest.of(paginationRequest.getPage(),
                    paginationRequest.getSize(),
                    Sort.by(Order.desc("createdAt")));
        }

        List<Order> orders = parseSortOrders(paginationRequest.getSort());
        return PageRequest.of(paginationRequest.getPage(), paginationRequest.getSize(), Sort.by(orders));
    }

    private void validatePaginationRequest(PaginationRequest paginationRequest) {
        if (paginationRequest.getSize() > maxSize) {
            throw new ValidationException("Page size must be less than or equal to " + maxSize);
        }

        if (paginationRequest.getPage() < 0) {
            throw new ValidationException("Page number must not be negative.");
        }
    }

    private List<Order> parseSortOrders(String sort) {
        List<Order> orders = new ArrayList<>();

        Arrays.stream(sort.split(","))
                .map(String::trim)
                .filter(order -> !order.isBlank())
                .forEach(order -> {
                    String[] parts = order.split(";");
                    if (parts.length != 2) {
                        throw new ValidationException("Invalid sort format, expected 'column;direction'.");
                    }

                    String column = parts[0].trim();
                    String direction = parts[1].trim();

                    if (!sortableColumn.contains(column)) {
                        throw new ValidationException("Invalid sort column, must be one of " + sortableColumn);
                    }

                    if ("desc".equalsIgnoreCase(direction)) {
                        orders.add(Order.desc(column));
                    } else if ("asc".equalsIgnoreCase(direction)) {
                        orders.add(Order.asc(column));
                    } else {
                        throw new ValidationException("Invalid sort direction, must be 'asc' or 'desc'.");
                    }
                });

        return orders;
    }
}
