package ru.example.group.main.util;

import lombok.experimental.UtilityClass;
import org.springframework.data.domain.PageRequest;

@UtilityClass
public class PaginationForm {

    public static PageRequest getPagination(int itemPerPage, int offset) {
        return PageRequest.of(offset / itemPerPage, itemPerPage);
    }
}
