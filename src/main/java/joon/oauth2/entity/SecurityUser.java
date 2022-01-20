package joon.oauth2.entity;

import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;

public class SecurityUser extends User {

    public SecurityUser(joon.oauth2.entity.User user) {
        super(user.getEmail(), user.getEmail(), AuthorityUtils.createAuthorityList(user.getRole().toString()));
    }
}
