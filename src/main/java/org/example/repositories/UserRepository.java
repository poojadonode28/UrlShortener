package org.example.repositories;

import org.example.models.User;

public interface UserRepository {

    void save(User user);

    User findById(Long userId);

    User findByEmail(String email);
}