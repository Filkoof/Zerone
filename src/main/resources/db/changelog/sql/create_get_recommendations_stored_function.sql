DROP FUNCTION IF EXISTS get_recommended_friends_for_user_id(bigint);
CREATE OR REPLACE FUNCTION get_recommended_friends_for_user_id(
    IN user_id bigint)
    returns table(ids bigint)
    LANGUAGE 'plpgsql'
AS
'
begin
    return query
        SELECT friendsOfFriendsWithCount.dst_person_id FROM (
                                                                SELECT friendships_1.dst_person_id
                                                                FROM friendships LEFT JOIN friendships AS friendships_1 ON friendships.src_person_id = friendships_1.src_person_id
                                                                WHERE (((friendships.dst_person_id)=user_id) AND ((friendships.status_id)=2) AND ((friendships_1.dst_person_id)<>user_id) AND ((friendships_1.status_id)=2))
                                                                UNION
                                                                SELECT friendships_1.src_person_id
                                                                FROM friendships LEFT JOIN friendships AS friendships_1 ON friendships.dst_person_id = friendships_1.dst_person_id
                                                                WHERE (((friendships.src_person_id)=user_id) AND ((friendships.status_id)=2) AND ((friendships_1.src_person_id)<>user_id) AND ((friendships_1.status_id)=2))
                                                                UNION
                                                                SELECT users_1.id
                                                                FROM users AS users_1 INNER JOIN users ON users_1.city = users.city
                                                                GROUP BY users.id, users_1.id
                                                                HAVING (((users.id)<>users_1.id And (users.id)=user_id) AND (users.is_approved=true) AND (users.is_deleted=false) AND (users.is_blocked=false))

                                                                UNION
                                                                (SELECT users.id
                                                                 FROM users WHERE users.id<>user_id AND (users.is_approved=true) AND (users.is_deleted=false) AND (users.is_blocked=false)
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
                          WHERE ((friendships.src_person_id)=user_id) and (friendships.status_id =2)
                          UNION
                          SELECT users.id FROM users WHERE (users.id = friendsOfFriendsWithCount.dst_person_id) AND ((users.is_approved=false) OR (users.is_deleted=true) or (users.is_blocked=true))
                      )
                  );
end;
';