package com.project.security.service;

import com.project.security.model.Users;
import com.project.security.repository.MyUserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class MyUserDetailService implements UserDetailsService {

    private final MyUserRepository myUserRepository;

    public MyUserDetailService(MyUserRepository myUserRepository) {
        this.myUserRepository = myUserRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Users> myUserOptional = myUserRepository.findByUsername(username);
        if (myUserOptional.isPresent()) {
            Users myUser = myUserOptional.get();
            return User.builder()
                    .username(myUser.getUsername())
                    .password(myUser.getPassword())
                    .roles(myUser.getRoles().split(","))
                    .build();
        } else {
            throw new UsernameNotFoundException("User " + username + " was not found");
        }
    }

    public Map<String, Object> registerUser(HashMap<String, String> request) {

        Optional<Users> userByUsername = myUserRepository.findByUsername(request.get("username"));
        Optional<Users> userByEmail = myUserRepository.findByEmail(request.get("email"));

        if (userByUsername.isPresent() || userByEmail.isPresent()) {
            return Map.of("Error", "User with this username or email already exist");
        }

        String password = new BCryptPasswordEncoder().encode(request.get("password"));
        Users user = new Users().builder()
                                .username(request.get("username"))
                                .password(password)
                                .email(request.get("email"))
                                .roles("USER")
                                .build();

        myUserRepository.save(user);

        return Map.of("Success", "User registered successfully");
    }

    public Map<String, Object> deleteUser(HashMap<String, String> request) {
        if (request.containsKey("username")) {
            var user = myUserRepository.findByUsername(request.get("username"));

            if (user.isPresent()) {
                myUserRepository.deleteById(user.get().getId());

                return Map.of("Success", "User deleted successfully");
            } else {
                return Map.of("Error", "User not found");
            }
        }

        if (request.containsKey("email")) {
            var user = myUserRepository.findByEmail(request.get("email"));

            if (user.isPresent()) {
                myUserRepository.deleteById(user.get().getId());

                return Map.of("Success", "User deleted successfully");
            } else {
                return Map.of("Error", "User not found");
            }
        }

        return Map.of("Error", "User not found");
    }
}
