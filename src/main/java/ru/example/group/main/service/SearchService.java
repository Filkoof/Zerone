package ru.example.group.main.service;


import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import ru.example.group.main.dto.response.CommonListResponseDto;
import ru.example.group.main.dto.response.UserSearchResponseDto;
import ru.example.group.main.exception.PostsException;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.jooq.impl.DSL.*;

@Service
@RequiredArgsConstructor
public class SearchService {

    private static final String FIRSTNAME_SQL = "u.first_name";
    private static final String LASTNAME_SQL = "u.last_name";
    private final DSLContext dsl;
    private final PostService postService;

    public CommonListResponseDto<Object> userSearch(
            String firstName, String lastName, Long ageFrom,
            Long ageTo, String country, String city,
            Integer offset, Integer itemPerPage) {

        List<Object> userListResult = dsl.selectFrom(table("users"))
                .where(!firstName.equals("") ? field("first_name").likeIgnoreCase('%' + firstName + '%') : noCondition())
                .and(!lastName.equals("") ? field("last_name").likeIgnoreCase('%' + lastName + '%') : noCondition())
                .and(!country.equals("") ? field("Country").likeIgnoreCase('%' + country + '%') : noCondition())
                .and(!city.equals("") ? field("City").likeIgnoreCase('%' + city + '%') : noCondition())
                .and(ageTo != -1 ? field("birth_date").greaterThan(LocalDate.now().minusYears(ageTo)) : noCondition())
                .and(ageFrom != -1 ? field("birth_date").lessThan(LocalDate.now().minusYears(ageFrom)) : noCondition())
                .and(field("is_deleted").eq(false))
                .and(field("is_blocked").eq(false))
                .fetchInto(UserSearchResponseDto.class);

        return CommonListResponseDto.builder()
                .data(userListResult)
                .error("OK")
                .offset(offset)
                .perPage(itemPerPage)
                .total(userListResult.size())
                .timestamp(LocalDateTime.now())
                .build();
    }

    public CommonListResponseDto<Object> postSearch(String text, Long dateFrom, Long dateTo,
                                                    Integer offset, Integer itemPerPage, String author,
                                                    String tag) throws PostsException {
        LocalDateTime endDate =
                Instant.ofEpochMilli(dateTo).atZone(ZoneId.systemDefault()).toLocalDateTime().plusDays(1);
        LocalDateTime startDate =
                Instant.ofEpochMilli(dateFrom).atZone(ZoneId.systemDefault()).toLocalDateTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        String end = endDate.format(formatter);
        String start = startDate.format(formatter);

        List<Long> listPostId = dsl.select(field("p.id"))
                .from(table("posts").as("p"))
                .leftJoin(table("users").as("u")).on("p.author_id = u.id")
                .leftJoin(table("posts_to_tags").as("ptt")).on("ptt.post_id = p.id")
                .leftJoin(table("tags").as("t")).on("t.id = ptt.tag_id")
                .where(!author.equals("") ? field(FIRSTNAME_SQL).likeIgnoreCase('%' + author + '%')
                        .or(field(LASTNAME_SQL).likeIgnoreCase('%' + author + '%'))
                        .or(field(concat(field(FIRSTNAME_SQL), val(" "), field(LASTNAME_SQL))
                                .likeIgnoreCase('%' + author + '%')))
                        .or(field(concat(field(LASTNAME_SQL), val(" "), field(FIRSTNAME_SQL))
                                .likeIgnoreCase('%' + author + '%')))
                        : noCondition())
                .and(!text.equals("") ? field("p.post_text").likeIgnoreCase('%' + text + '%') : noCondition())
                .and(!tag.equals("") ? field("t.tag").likeIgnoreCase(tag) : noCondition())
                .and(field("p.time").between(start).and(end))
                .and(field("p.is_deleted").eq(false))
                .and(field("p.is_blocked").eq(false))
                .fetchInto(Long.class);

        return postService.getNewsByListUserId(listPostId, offset);
    }
}
