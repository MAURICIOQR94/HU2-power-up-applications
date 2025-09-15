package co.com.pragma.util;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder(toBuilder = true)
public class PageResult<T> {

    private int size;
    private int totalPages;
    private int page;
    private Long totalElements;
    private List<T> data;

}
