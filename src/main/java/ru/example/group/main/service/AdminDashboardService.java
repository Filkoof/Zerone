package ru.example.group.main.service;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.example.group.main.dto.response.LikeForStatsResponseDto;
import ru.example.group.main.dto.response.UserCommentsResponseDto;
import ru.example.group.main.entity.CommentEntity;
import ru.example.group.main.entity.PostEntity;
import ru.example.group.main.entity.UserEntity;
import ru.example.group.main.entity.UserRoleEntity;
import ru.example.group.main.repository.*;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.jooq.impl.DSL.*;

@RequiredArgsConstructor
@Service

public class AdminDashboardService {

    private final DSLContext dsl;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final CommentRepository commentRepository;
    private final LikesRepository likesRepository;


    public ZonedDateTime getZonedDateTime(long monthMinus) {
        Calendar cal = Calendar.getInstance();
        long dateAsLong = cal.getTimeInMillis() - monthMinus;
        ZoneId z = ZoneId.of("Europe/Moscow");
        Instant instant = Instant.ofEpochMilli(dateAsLong);
        return instant.atZone(z);
    }

    public JSONObject getMonthForAccountRetensionData() {
        long oneMonth = 2678400000L;
        long monthMinus = 0;
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        for (int i = 0; i < 6; i++) {
            Month m = getZonedDateTime(monthMinus).getMonth();
            jsonArray.put(m.name().substring(0, 3));
            monthMinus += oneMonth;
        }
        try {
            jsonObject.put("months", jsonArray);
            jsonObject.put("values", getMonthDataForAccountRetensionData());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public JSONArray getMonthDataForAccountRetensionData() {
        List<String> dateData = getLastSevenMonth();
        JSONArray result = new JSONArray();
        for (int i = 0; i <= dateData.size() - 2; i++) {
            List<Integer> listPostId = dsl.select(count(field("p.id")))
                    .from(table("posts").as("p"))
                    .where(field("time").between(dateData.get(i + 1)).and(dateData.get(i)))
                    .and(field("is_deleted").eq(false))
                    .and(field("is_blocked").eq(false))
                    .fetchInto(Integer.class);
            result.put(listPostId.get(0));
        }
        return result;
    }

    public Integer countOfgetMonthDataForAccountRetensionData() {
        JSONArray jsonArray = getMonthDataForAccountRetensionData();
        int count = 0;
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                count += Integer.parseInt(jsonArray.getString(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return count;
    }

    public String percentOfAllPostsAboutSixMonth() {
        double value = (double) countOfgetMonthDataForAccountRetensionData() * 100 / postRepository.findAll().size();
        return String.format("%.3f", value);
    }

    public List<String> getTwoLastMonth() {
        long oneMonth = 2678400000L;
        List<String> listOfMonth = new ArrayList<>();
        for (int i = 1; i <= 2; i++) {
            Month m = getZonedDateTime(oneMonth).getMonth();
            listOfMonth.add(m.name());
            oneMonth += oneMonth;
        }
        return listOfMonth;
    }

    public List<String> getLastSevenMonth() {
        long oneMonth = 2678400000L;
        long monthMinus = 0;
        List<String> months = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        long dateAsLong = cal.getTimeInMillis();
        ZoneId z = ZoneId.of("Europe/Moscow");
        for (int i = 0; i < 7; i++) {
            Instant instant = Instant.ofEpochMilli(dateAsLong);
            ZonedDateTime zdt = instant.atZone(z);
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            zdt.format(dateTimeFormatter);
            String dateToString = zdt.format(dateTimeFormatter);
            String dateResult;
            if (i == 0) {
                dateResult = zdt.plusDays(1).format(dateTimeFormatter);
            } else {
                dateResult = dateToString.substring(0, dateToString.length() - 2) + "01";
                dateAsLong -= oneMonth;
            }
            months.add(dateResult);
        }
        return months;
    }

    public JSONObject getPublicDaysAndData() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("days", getPublicationsMonthStats());
            jsonObject.put("current", getPublicationCurrentMonthInDay(2, 1));
            jsonObject.put("last", getPublicationCurrentMonthInDay(3, 2));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public JSONArray getPublicationsMonthStats() {
        JSONArray jsonArray = new JSONArray();
        int daysInCurrentMonth = LocalDate.now().lengthOfMonth();
        for (int i = 1; i <= daysInCurrentMonth; i++) {
            jsonArray.put(i);
        }
        return jsonArray;
    }

    public JSONArray getPublicationCurrentMonthInDay(Integer startMonth, Integer endMonth) {
        getPublicationsMonthStats();
        List<Integer> list = new ArrayList<>();
        List<String> listPostId = dsl.select(field("p.time"))
                .from(table("posts").as("p"))
                .where(field("time").between(getLastSevenMonth().get(startMonth)).and(getLastSevenMonth().get(endMonth)))
                .and(field("is_deleted").eq(false))
                .and(field("is_blocked").eq(false))
                .fetchInto(String.class);
        for (int z = 0; z < getPublicationsMonthStats().length(); z++) {
            list.add(0);
        }
        for (int i = 0; i < listPostId.size(); i++) {

            int id = Integer.parseInt(listPostId.get(i).split(" ")[0].substring(8, 10)) - 1;
            list.set(id, list.get(id) + 1);

        }
        JSONArray currentMonth = new JSONArray(list);
        return currentMonth;
    }

    public JSONObject getOldForUsers() {
        JSONObject object = new JSONObject();
        try {
            object.put("oldStats", oldStats());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    public JSONArray oldStats() {
        List<Integer> oldStatsList = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            List<Integer> listPostId = dsl.select(count(field("u.id")))
                    .from(table("users").as("u"))
                    .where(field("birth_date").between(listOfYears().get(i)).and(listOfYears().get(i + 1)))
                    .fetchInto(Integer.class);
            oldStatsList.add(listPostId.get(0));
        }
        return new JSONArray(oldStatsList);
    }

    private List<String> listOfYears() {
        List<String> yearsList = new ArrayList<>();
        yearsList.add("1800-01-01");
        yearsList.add("1962-01-01");
        yearsList.add("1972-01-01");
        yearsList.add("1982-01-01");
        yearsList.add("1992-01-01");
        yearsList.add("2002-01-01");
        yearsList.add("2022-01-01");
        return yearsList;
    }

    public static int calculateAge(LocalDate birthDate, LocalDate currentDate) {
        if ((birthDate != null) && (currentDate != null)) {
            return Period.between(birthDate, currentDate).getYears();
        } else {
            return 0;
        }
    }

    public Page<UserEntity> getAllUsersWithoutAdmin(Pageable pageable) {
        return userRepository.findAllUsersWithoutAdmins(pageable);
    }

    public JSONArray getCountriesStatistic() {
        JSONArray jsonArray = new JSONArray();
        List<UserEntity> userList = userRepository.findAll();
        Map<String, Long> couterMap = userList.stream().collect(Collectors.groupingBy(UserEntity::getCountry, Collectors.counting()));
        for (Map.Entry<String, Long> map : couterMap.entrySet()) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("name", map.getKey());
                jsonObject.put("y", map.getValue());
                jsonObject.put("z", map.getValue() >= 3 ? 110 : 60);
                jsonArray.put(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return jsonArray;
    }

    public void makeRoleAdminForUser(Boolean booleanAnswer, Long id) {
        if (booleanAnswer) {
            UserRoleEntity userRole = new UserRoleEntity();
            userRole.setUserRole("ROLE_ADMIN");
            userRole.setUserForRole(userRepository.findById(id).orElseThrow(IllegalStateException::new));
            userRoleRepository.save(userRole);
        }
    }

    public Boolean isAdmin(UserEntity user) {
        try {
            String role = userRoleRepository.findByUserForRole(user).getUserRole();
            return true;
        } catch (NullPointerException e) {
            e.getMessage();
        }
        return false;
    }

    public void makeRoleUserForUser(Boolean booleanAnswer, Long id) {
        UserEntity user = userRepository.findById(id).orElseThrow(IllegalStateException::new);
        if (!booleanAnswer) {
            UserRoleEntity userRole = userRoleRepository.findByUserForRole(user);
            userRoleRepository.delete(userRole);
        }
    }

    public String getAllCommentsAsString() {
        List<CommentEntity> commentList = commentRepository.findAll();
        String comments = "";
        for (CommentEntity comment : commentList) {
            String[] strings = comment.getCommentText().split(" ");
            for (String str : strings) {
                if (str.length() > 2) {
                    comments += " " + str;
                }
            }
        }
        return comments;
    }

    public JSONArray getAllUsersAndComments() {
        List<UserCommentsResponseDto> userCommentsList = userRepository.findAllUsersAndComments();
        JSONArray jsonUserCommentsList = new JSONArray();
        for (UserCommentsResponseDto user : userCommentsList) {
            JSONObject jsonObject = new JSONObject();
            try {
                if (user.getCountComments() > 13) {
                    jsonObject.put("name", user.getUser());
                    jsonObject.put("y", user.getCountComments());
                    jsonUserCommentsList.put(jsonObject);
                }
            } catch (JSONException e) {
                e.printStackTrace();

            }
        }
        return jsonUserCommentsList;
    }


    public Page<UserEntity> userFilter(Pageable pageable, String filter) {
        Page<UserEntity> users = userRepository.findAllByFilter(pageable, filter);
        if (filter.isEmpty()) {
            return userRepository.findAllUsersWithoutAdmins(pageable);
        }
        return users;
    }

    public Page<PostEntity> postFilter(Pageable pageable, String filter) {
        Page<PostEntity> posts = postRepository.findAllByFilter(pageable, filter);
        if (filter.isEmpty()) {
            return postRepository.findAll(pageable);
        }
        return posts;
    }

    public Page<CommentEntity> commentFilter(Pageable pageable, String filter) {
        Page<CommentEntity> comments = commentRepository.findAllByFilter(pageable, filter);
        if (filter.isEmpty()) {
            return commentRepository.findAll(pageable);
        }
        return comments;
    }

    public JSONArray likesStatistic() {
        JSONArray jsonArray = new JSONArray();
        List<LikeForStatsResponseDto> userListResult = dsl.select(concat(field("u1.first_name"), val(" "), field("u1.last_name")),
                        concat(field("u2.first_name"), val(" "), field("u2.last_name")), val(1).as("rate"))
                .from(table("likes").as("l"))
                .join(table("users").as("u1")).on("l.user_id = u1.id")
                .join(table("posts").as("p")).on("l.entity_id = p.id")
                .join(table("users").as("u2")).on("p.author_id = u2.id")
                .where(field("l.type").eq("1"))
                .fetchInto(LikeForStatsResponseDto.class);
        for (LikeForStatsResponseDto like : userListResult) {
            JSONArray jsonArray1 = new JSONArray();
            jsonArray1.put(like.getNameA());
            jsonArray1.put(like.getNameB());
            jsonArray1.put(like.getRate());
            jsonArray.put(jsonArray1);
        }
        return jsonArray;
    }

    public Integer getCountLikes(){
        return commentRepository.findAll().size();
    }

}
