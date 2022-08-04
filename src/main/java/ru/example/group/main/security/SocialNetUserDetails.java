package ru.example.group.main.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.example.group.main.entity.UserEntity;
import ru.example.group.main.entity.UserRoleEntity;

import java.util.*;
import java.util.logging.Logger;

public class SocialNetUserDetails implements UserDetails {

    private final UserEntity user;

    private final Logger logger = Logger.getLogger(SocialNetUserDetails.class.getName());

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
        Set<UserRoleEntity> userRoleEntityList;
        try {
            userRoleEntityList = user.getUserRoleEntities();
        } catch (Exception e) {
            userRoleEntityList = new LinkedHashSet<>();
            logger.info("ОШИБКА " + e.getMessage());
        }
        if (userRoleEntityList != null && userRoleEntityList.size() != 0) {
            for (UserRoleEntity userRole : userRoleEntityList) {
                simpleGrantedAuthorityList.add(new SimpleGrantedAuthority(userRole.getUserRole()));
            }
        }
        return Collections.unmodifiableList(simpleGrantedAuthorityList);
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEMail();
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
        return true;
    }
}
