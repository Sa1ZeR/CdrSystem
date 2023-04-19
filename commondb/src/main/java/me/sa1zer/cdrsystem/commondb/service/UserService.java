package me.sa1zer.cdrsystem.commondb.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.sa1zer.cdrsystem.commondb.entity.User;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import me.sa1zer.cdrsystem.commondb.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    public User findUserByPhone(String phone) {
        return userRepository.findUserByPhone(phone).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        String.format("User with %s phone number not found!", phone)));
    }

    public List<User> findAllInSet(Set<String> phones) {
        return userRepository.findAllInSet(phones);
    }

    public List<User> findAllWithPositiveBalance(Set<String> phones) {
        return userRepository.findAllWithPositiveBalance(phones);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public boolean isUserExist(String phone) {
        Optional<User> userFromDb = userRepository.findUserByPhone(phone);
        return userFromDb.isPresent();
    }

    @Transactional
    public User save(User user) {
        return userRepository.save(user);
    }

    @Transactional
    public void updateUserBalance(String phone, double balance) {
        userRepository.updateUserBalance(phone, balance);
    }
}
