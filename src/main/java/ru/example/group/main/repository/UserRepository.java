package ru.example.group.main.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.example.group.main.dto.response.UserAdminResponseDto;
import ru.example.group.main.dto.response.UserCommentsResponseDto;
import ru.example.group.main.entity.UserEntity;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    @Query("""
            SELECT u from UserEntity u
            WHERE u.id NOT IN (SELECT ur.userForRole.id FROM UserRoleEntity as ur)
            """)
    Page<UserEntity> findAllUsersWithoutAdmins(Pageable pageable);

    @Query("""
             SELECT u FROM UserEntity u WHERE u.id NOT IN (SELECT ur.userForRole.id FROM UserRoleEntity as ur)
             AND (LOWER(u.firstName) LIKE CONCAT('%', LOWER(:filter), '%')
             OR LOWER(u.lastName) LIKE CONCAT('%', LOWER(:filter), '%')
             OR UPPER(u.firstName) LIKE CONCAT('%', UPPER(:filter), '%')
             OR UPPER(u.lastName) LIKE CONCAT('%', UPPER(:filter), '%')
             OR LOWER(u.email) LIKE CONCAT('%', LOWER(:filter), '%'))""")
    Page<UserEntity> findAllByFilter(Pageable pageable, String filter);

    @Query("""
            SELECT new ru.example.group.main.dto.response.UserAdminResponseDto(
            u.id, u.firstName, u.lastName, u.email, u.isDeleted, u.isBlocked, u.photo, ur.userRole)
            FROM UserEntity as u
            JOIN UserRoleEntity as ur ON ur.userForRole.id = u.id
            """)
    Page<UserAdminResponseDto> findAllUserAdmin(Pageable pageable);

    @Query("""
            SELECT new ru.example.group.main.dto.response.UserAdminResponseDto(
            u.id, u.firstName, u.lastName, u.email, u.isDeleted, u.isBlocked, u.photo, ur.userRole)
            FROM UserEntity as u
            JOIN UserRoleEntity as ur ON ur.userForRole.id = u.id where u.id IN :id
            """)
    UserAdminResponseDto findUserAdminById(@Param("id") Long id);

    @Query("""
            SELECT u from UserEntity u
            WHERE u.isDeleted = true
            """)
    List<UserEntity> findAllByDeleted();

    @Query("""
            SELECT u from UserEntity u
            WHERE u.isBlocked = true
            """)
    List<UserEntity> findAllByBlocked();

    @Query("""
            SELECT new ru.example.group.main.dto.response.UserCommentsResponseDto(CONCAT(u.firstName, ' ', u.lastName), COUNT(p.id))
            FROM PostEntity as p
            JOIN UserEntity as u ON u.id = p.user.id
            GROUP BY u.id
            """)
    List<UserCommentsResponseDto> findAllUsersAndComments();

    UserEntity findByEmail(String eMail);

    boolean existsByEmail(String eMail);

    UserEntity findByConfirmationCode(String code);

    @Query(value = """
            SELECT users.* FROM users
            WHERE (users.is_approved = true AND users.is_blocked = false AND users.is_deleted = false) AND users.id
            IN (SELECT friendships.src_person_id FROM friendships WHERE friendships.dst_person_id = :id AND ((friendships.status_id) = :status))
            """
            , nativeQuery = true)
    List<UserEntity> getAllRelationsOfUser(Long id, int status, Pageable pageable);
}

