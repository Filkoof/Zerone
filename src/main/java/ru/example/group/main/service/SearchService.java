package ru.example.group.main.service;


import lombok.RequiredArgsConstructor;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import ru.example.group.main.dto.response.CommonListResponseDto;
import ru.example.group.main.dto.response.UserSearchResponseDto;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.jooq.impl.DSL.*;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final DSLContext dsl;
    private final PostService postService;

    public CommonListResponseDto<Object> userSearch(
            String firstName, String lastName, Long ageFrom,
            Long ageTo, String country, String city,
            Integer offset, Integer itemPerPage) {

        Condition condition = trueCondition();

        condition = conditionName(condition, firstName, lastName);
        condition = conditionPlace(condition, country, city);
        condition = conditionAge(condition, ageTo, ageFrom);

//        List<Condition> conditionList = new ArrayList<>();
//        conditionList.add(conditionTemplate(condition, firstName, "first_name"));
//        conditionList.add(conditionTemplate(condition, lastName, "last_name"));
//        conditionList.add(conditionTemplate(condition, country, "country"));
//        conditionList.add(conditionTemplate(condition, city, "city"));



        List<Object> userListResult = dsl.selectFrom(table("users"))
                .where(condition)
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

    public CommonListResponseDto<Object> postSearch(String text, Long date_from, Long date_to,
                                                    Integer offset, Integer itemPerPage, String author,
                                                    String tag) {
        LocalDateTime endDate =
                Instant.ofEpochMilli(date_to).atZone(ZoneId.systemDefault()).toLocalDateTime().plusDays(1);
        LocalDateTime startDate =
                Instant.ofEpochMilli(date_from).atZone(ZoneId.systemDefault()).toLocalDateTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        String end = endDate.format(formatter);
        String start = startDate.format(formatter);

        Condition condition = trueCondition();
        condition = conditionPost(condition, author, text, start, end, tag);

        List<Long> listPostId = dsl.select(field("p.id"))
                .from(table("posts").as("p"))
                .leftJoin(table("users").as("u")).on("p.author_id = u.id")
                .leftJoin(table("posts_to_tags").as("ptt")).on("ptt.post_id = p.id")
                .leftJoin(table("tags").as("t")).on("t.id = ptt.tag_id")
                .where(condition)
                .fetchInto(Long.class);

        return postService.getNewsByListUserId(listPostId, offset);
    }
    //TODO доработать условия
    private Condition conditionPost(Condition condition, String author, String text, String start, String end, String tag) {
        if (!text.equals("")) {
            condition = condition.and(field("p.post_text").likeIgnoreCase('%' + text + '%'));
        }
        if (!author.equals("")) {
          //  condition = condition.and(field(concat("u.first_name" + " " + "u.last_name")).likeIgnoreCase(author));
            condition = condition.and(field("u.first_name").likeIgnoreCase('%' + author + '%').or(field("u.last_name").likeIgnoreCase('%' + author + '%')));
        }
        if (!tag.equals("")) {
            condition = condition.and(field("t.tag").likeIgnoreCase(tag));
        }
        condition = condition.and(field("p.time").between(start).and(end));
        condition = condition.and(field("p.is_deleted").eq(false));
        return condition;
    }

    private Condition conditionName(Condition condition, String propertyFirstName, String propertyLastName) {
        if (!propertyFirstName.equals("")) condition = condition
                .and(field("first_name").likeIgnoreCase('%' + propertyFirstName + '%'));
        if (!propertyLastName.equals("")) condition = condition
                .and(field("last_name").likeIgnoreCase('%' + propertyLastName + '%'));
        return condition;
    }

    private Condition conditionTemplate(Condition condition, String conditionName, String sqlName) {
        if (!conditionName.equals(""))
            condition = condition
                    .and(field(sqlName).likeIgnoreCase('%' + conditionName + '%'));
            return condition;
    }

    private Condition conditionPlace(Condition condition, String propertyCountry, String propertyCity) {
        if (!propertyCountry.equals("")) condition = condition
                .and(field("Country").likeIgnoreCase('%' + propertyCountry + '%'));
        if (!propertyCity.equals("")) condition = condition
                .and(field("City").likeIgnoreCase('%' + propertyCity + '%'));
        return condition;
        //trueCondition();
    }

    private Condition conditionAge(Condition condition, Long propertyAgeTo, Long propertyAgeFrom) {
        if (propertyAgeTo != -1) condition = condition
                .and(field("birth_date").greaterThan(LocalDate.now().minusYears(propertyAgeTo)));
        if (propertyAgeFrom != -1) condition = condition
                .and(field("birth_date").lessThan(LocalDate.now().minusYears(propertyAgeFrom)));
        return condition;
    }
}
