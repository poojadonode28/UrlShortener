package org.example.repositories;

import org.example.exceptions.UserAlreadyExistsException;
import org.example.models.User;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryUserRepository implements UserRepository {

    private final Map<Long, User> usersById = new ConcurrentHashMap<>();
    private final Map<String, User> usersByEmail = new ConcurrentHashMap<>();

    @Override
    public void save(User user) {
        User existing = usersByEmail.putIfAbsent(
                user.getEmail(),
                user
        );

        if (existing != null) {
            throw new UserAlreadyExistsException(
                    "User email already exists: " + user.getEmail()
            );
        }

        usersById.put(user.getId(), user);
    }

    @Override
    public User findById(Long userId) {
        return usersById.get(userId);
    }

    @Override
    public User findByEmail(String email) {
        return usersByEmail.get(email);
    }
}