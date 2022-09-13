CREATE INDEX friendships_dst_person_id ON friendships(dst_person_id);
CREATE INDEX friendships_src_person_id ON friendships(src_person_id);
CREATE INDEX friendships_status_id ON friendships(status_id);
CREATE INDEX users_id_index ON users(id);
CREATE INDEX users_is_approved ON users(is_approved);
CREATE INDEX users_is_deleted ON users(is_deleted);
CREATE INDEX users_is_blocked ON users(is_blocked);
CREATE INDEX users_id_on_recs ON recommended_friends(user_id);