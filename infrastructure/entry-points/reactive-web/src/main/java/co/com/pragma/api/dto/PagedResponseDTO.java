package co.com.pragma.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PagedResponseDTO <T> {

    private int size;
    private int totalPages;
    private int page;
    private Long totalElements;
    private List<T> data;

}
