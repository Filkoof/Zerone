package ru.example.group.main.repository.jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.example.group.main.dto.response.UserDataResponseDto;
import ru.example.group.main.entity.enumerated.MessagesPermission;

import java.util.List;

@Repository
public class JdbcFriendsRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public List<UserDataResponseDto> getFriendsOfUser(Long userId) {
        return jdbcTemplate.query("""
                        SELECT users.* FROM users 
                        WHERE (users.is_approved=true AND users.is_blocked=false AND users.is_deleted=false) 
                        AND users.id IN (SELECT friendships.dst_person_id FROM friendships 
                        WHERE friendships.src_person_id=? AND ((friendships.status_id)=2)
                        UNION
                        SELECT friendships.src_person_id FROM friendships
                        WHERE friendships.dst_person_id=? AND ((friendships.status_id)=2))
                        """,
                (rs, rowNum) ->
                        new UserDataResponseDto(
                                rs.getLong("id"),
                                rs.getString("first_name"),
                                rs.getString("last_name"),
                                rs.getTimestamp("reg_date").toLocalDateTime(),
                                rs.getDate("birth_date") != null ? rs.getDate("birth_date").toLocalDate() : null,
                                rs.getString("e_mail"),
                                rs.getString("phone"),
                                rs.getString("photo"),
                                rs.getString("about"),
                                rs.getBoolean("is_approved"),
                                rs.getString("city"),
                                "",
                                rs.getString("country"),
                                MessagesPermission.getFromBoolean(rs.getBoolean("message_permissions")),
                                rs.getTimestamp("last_online_time").toLocalDateTime(),
                                rs.getBoolean("is_blocked"),
                                rs.getBoolean("is_deleted")
                        )
                , userId, userId);
    }

}
