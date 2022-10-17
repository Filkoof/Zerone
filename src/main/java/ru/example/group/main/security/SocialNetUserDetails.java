package ru.example.group.main.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.example.group.main.entity.UserEntity;
import ru.example.group.main.entity.UserRoleEntity;

import java.util.*;

@Slf4j
public class SocialNetUserDetails implements UserDetails {
    private final UserEntity user;

    public SocialNetUserDetails(UserEntity user) {
        this.user = user;
    }

    public UserEntity getUser() {
        return user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> simpleGrantedAuthorityList = new ArrayList<>();
        simpleGrantedAuthorityList.add(new SimpleGrantedAuthority("ROLE_USER"));
        List<UserRoleEntity> userRoleEntityList;
        try {
            userRoleEntityList = new ArrayList<UserRoleEntity>(user.getUserRoleEntities());
        } catch (Exception e) {
            userRoleEntityList = null;
            log.info("ОШИБКА " + e.getMessage());
        }
        try {
            if (userRoleEntityList != null && userRoleEntityList.size() != 0) {
                for (UserRoleEntity userRole : userRoleEntityList) {
                    simpleGrantedAuthorityList.add(new SimpleGrantedAuthority(userRole.getUserRole()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Collections.unmodifiableList(simpleGrantedAuthorityList);
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return user.isApproved();
    }
}
