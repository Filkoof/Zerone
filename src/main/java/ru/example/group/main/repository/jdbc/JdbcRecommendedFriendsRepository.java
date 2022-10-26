package ru.example.group.main.repository.jdbc;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.example.group.main.entity.UserEntity;

import java.sql.Connection;
import java.sql.JDBCType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class JdbcRecommendedFriendsRepository {

    private static final String USER_ID = "user_id";
    private final JdbcTemplate jdbcTemplate;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public JdbcRecommendedFriendsRepository(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Transactional
    public int[] updateBatchRecommendationsArray(Map<Long, Long[]> recommendedFriendsMapInt) {
        List<Map<String, Object>> batchValues = new ArrayList<>(recommendedFriendsMapInt.size());
        for (Map.Entry<Long, Long[]> entry : recommendedFriendsMapInt.entrySet()) {
            batchValues.add(
                    new MapSqlParameterSource(USER_ID, entry.getKey())
                            .addValue("recommended_friends",
                                    jdbcTemplate.execute((Connection c) -> c.createArrayOf(JDBCType.BIGINT.getName(),
                                            entry.getValue()))).getValues()
            );
        }
        return namedParameterJdbcTemplate.batchUpdate(
                "UPDATE recommended_friends SET recommended_friends = :recommended_friends WHERE user_id=:user_id"
                , batchValues.toArray(new Map[recommendedFriendsMapInt.size()]));
    }

    @Transactional
    public int[] insertBatchRecommendationsArray(Map<Long, Long[]> recommendedFriendsMapInt) {
        List<Map<String, Object>> batchValues = new ArrayList<>(recommendedFriendsMapInt.size());
        for (Map.Entry<Long, Long[]> entry : recommendedFriendsMapInt.entrySet()) {
            batchValues.add(new MapSqlParameterSource(USER_ID, entry.getKey())
                    .addValue("recommended_friends", jdbcTemplate.execute((Connection c) ->
                            c.createArrayOf(JDBCType.BIGINT.getName(),
                                    entry.getValue()))).getValues()
            );
        }
        jdbcTemplate.execute("CREATE UNIQUE INDEX unique_user_id ON recommended_friends (user_id)");
        int[] insertCounts = namedParameterJdbcTemplate.batchUpdate(
                "INSERT INTO recommended_friends (user_id, recommended_friends) VALUES (:user_id, :recommended_friends) ON CONFLICT DO NOTHING"
                , batchValues.toArray(new Map[recommendedFriendsMapInt.size()]));
        jdbcTemplate.execute("DROP INDEX IF EXISTS unique_user_id ");
        return insertCounts;
    }

    public int deleteAll() {
        return jdbcTemplate.update("TRUNCATE TABLE recommended_friends");
    }

    public int deleteInactive() {
        return jdbcTemplate.update("""
                DELETE FROM recommended_friends USING users
                WHERE recommended_friends.user_id = users.id AND ((((users.is_approved)=False)) 
                OR (((users.is_deleted)=True)) OR (((users.is_blocked)=True)))
                """);
    }

    public List<Long> getAllActiveUsersIds() {
        return jdbcTemplate.queryForList(
                """
                        SELECT users.id FROM users
                        WHERE (users.is_approved=True AND users.is_deleted=False AND users.is_blocked=False)
                        """
                , Long.class);
    }

    public List<UserEntity> getRecommendedFriendsForAPI(Long userId, Integer limit, Integer offset) {
        return jdbcTemplate.query(
                """
                        select users.* from users where users.id IN
                        (select unnest(recommended_friends.recommended_friends) as unnested_recs_id from recommended_friends where recommended_friends.user_id = ?)
                        LIMIT ? OFFSET ?
                        """,
                new BeanPropertyRowMapper<>(UserEntity.class), userId, limit, offset);
    }

    public List<Long> getRecommendedFriendsForUser(Long userId) {
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue(USER_ID, userId);
        return namedParameterJdbcTemplate.queryForList(
                SQL_GET_RECOMMENDED_FRIENDS_FOR_USER_ID, mapSqlParameterSource, Long.class);
    }

    private static final String SQL_GET_RECOMMENDED_FRIENDS_FOR_USER_ID =
            """
                    SELECT friendsOfFriendsWithCount.dst_person_id FROM (
                            SELECT friendships_1.dst_person_id
                            FROM friendships LEFT JOIN friendships AS friendships_1 ON friendships.src_person_id = friendships_1.src_person_id
                            WHERE (((friendships.dst_person_id)=:user_id) AND ((friendships.status_id)=2) AND ((friendships_1.dst_person_id)<>:user_id) AND ((friendships_1.status_id)=2))
                            UNION
                            SELECT friendships_1.src_person_id
                            FROM friendships LEFT JOIN friendships AS friendships_1 ON friendships.dst_person_id = friendships_1.dst_person_id
                            WHERE (((friendships.src_person_id)=:user_id) AND ((friendships.status_id)=2) AND ((friendships_1.src_person_id)<>:user_id) AND ((friendships_1.status_id)=2))
                            UNION
                            SELECT users_1.id
                            FROM users AS users_1 INNER JOIN users ON users_1.city = users.city
                            GROUP BY users.id, users_1.id
                            HAVING (((users.id)<>users_1.id And (users.id)=:user_id) AND (users.is_approved=true) AND (users.is_deleted=false) AND (users.is_blocked=false))
                    		UNION
                            (SELECT users.id
                    		FROM users WHERE users.id<>:user_id AND (users.is_approved=true) AND (users.is_deleted=false) AND (users.is_blocked=false)
                    		ORDER BY users.reg_date DESC limit 5)
                    		UNION
                    		(SELECT posts.author_id FROM posts GROUP BY posts.author_id ORDER BY COUNT(posts.author_id) DESC LIMIT 5)
                    		) AS friendsOfFriendsWithCount
                            WHERE EXISTS
                            (
                                    SELECT friendsOfFriendsWithCount.dst_person_id
                                    EXCEPT
                                    (
                                    SELECT friendships.dst_person_id
                            		FROM friendships
                            		WHERE ((friendships.src_person_id)=:user_id) and (friendships.status_id =2)
                                    UNION
                                    SELECT users.id FROM users WHERE (users.id = friendsOfFriendsWithCount.dst_person_id) AND ((users.is_approved=false) OR (users.is_deleted=true) or (users.is_blocked=true))
                                    )
                        	)
                    """;
}
