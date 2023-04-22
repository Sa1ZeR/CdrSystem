package me.sa1zer.cdrsystem.crm.security.jwt;

import me.sa1zer.cdrsystem.commondb.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.stream.Collectors;

public class JwtUserFactory {

    private JwtUserFactory() {

    }

    public static JwtUser createJwtUser(User user) {
        return new JwtUser(user.getId(), user.getPhone(), user.getPassword(), true, getAuthorities(user));
    }

    private static List<GrantedAuthority> getAuthorities(User user) {
        return user.getRoles().stream().map(r -> new SimpleGrantedAuthority(r.name()))
                .collect(Collectors.toList());
    }

}
