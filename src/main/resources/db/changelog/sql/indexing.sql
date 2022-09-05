CREATE INDEX friendships_dst_person_id CONCURRENTLY ON friendships(dst_person_id);
CREATE INDEX friendships_src_person_id CONCURRENTLY ON friendships(src_person_id);
CREATE INDEX friendships_status_id CONCURRENTLY ON friendships(status_id);
CREATE INDEX users_id CONCURRENTLY ON users(id);