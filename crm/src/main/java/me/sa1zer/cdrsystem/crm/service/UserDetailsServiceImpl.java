package me.sa1zer.cdrsystem.crm.service;

import lombok.RequiredArgsConstructor;
import me.sa1zer.cdrsystem.commondb.entity.User;
import me.sa1zer.cdrsystem.commondb.repository.UserRepository;
import me.sa1zer.cdrsystem.crm.security.jwt.JwtUserFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userByPhone = userRepository.findUserByPhone(username);
        if(userByPhone.isEmpty())
            throw new UsernameNotFoundException(String.format("User with phone %s not found", username));

        return JwtUserFactory.createJwtUser(userByPhone.get());
    }
}
