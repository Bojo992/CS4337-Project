package com.project.service;

import com.project.model.Users;
import com.project.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsersService {

    @Autowired
    private UsersRepository usersRepository;

    public Optional<Users> getUserById(long id) {
       return usersRepository.findById(id);
    }

    public Optional<Users> getUserByEmail(String email) {
        return usersRepository.findByEmail(email);
    }

    public void createUser(Users user) {
        usersRepository.save(user);
    }

    public void updateUser(long id, Users user) {
        usersRepository.findById(id).ifPresent(_ -> usersRepository.save(user));
    }

    public void deleteUser(long id) {
        usersRepository.deleteById(id);
    }
}
