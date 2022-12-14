package ru.example.group.main.util;

import lombok.Getter;
import lombok.experimental.UtilityClass;
import org.springframework.data.domain.PageRequest;

@UtilityClass
@Getter
public class UtilZerone {

    public static PageRequest getPagination(int itemPerPage, int offset) {
        return PageRequest.of(offset / itemPerPage, itemPerPage);
    }
}
