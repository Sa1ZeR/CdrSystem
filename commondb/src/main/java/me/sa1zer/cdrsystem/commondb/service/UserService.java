package me.sa1zer.cdrsystem.commondb.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.sa1zer.cdrsystem.commondb.entity.User;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import me.sa1zer.cdrsystem.commondb.repository.UserRepository;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class UserService {

    private static final Map<String, User> USER_CACHE = new HashMap<>();

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
    public List<User> saveAll(Collection<User> users) {
        return userRepository.saveAll(users);
    }

    @Transactional
    public void updateUserBalance(String phone, double balance) {
        userRepository.updateUserBalance(phone, balance);
    }

    //get user from cache, if not exist - get from db
    public User getUserByPhone(String phone) {
        User user = USER_CACHE.get(phone);
        if(user == null) {
            user = findUserByPhone(phone);
            USER_CACHE.put(user.getPhone(), user);
        }

        return user;
    }

    /*  update user in cache*/
    public void updateUserCache(String phoneNumber) {
        User user = findUserByPhone(phoneNumber);
        USER_CACHE.put(user.getPhone(), user);
    }
}
