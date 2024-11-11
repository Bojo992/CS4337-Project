package com.project.security;

import com.project.security.service.MyUserDetailService;
import io.jsonwebtoken.lang.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.TestPropertySource;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;

@TestPropertySource("classpath:application-test.properties")
@SpringBootTest
public class UserServiceTest {
    @Autowired
    public MyUserDetailService sut;

    @Test
    public void loadUserByUsernameTest() {
        HashMap request = new HashMap();
        request.put("username", "test");
        request.put("password", "test");
        request.put("email", "test@test.com");
        sut.registerUser(request);

        var result = sut.loadUserByUsername("test");

        Assert.notNull(result);
        Assert.isTrue(result.getUsername().equals("test"));
    }

    @Test
    public void loadUserByUsernameFailTest() {
        var result = assertThrows(UsernameNotFoundException.class, () -> sut.loadUserByUsername("test1"));

        Assert.notNull(result);
        Assert.isTrue(result.getMessage().equals("User test1 was not found"));
    }

    @Test
    public void registerUserTest() {
        HashMap request = new HashMap();
        request.put("username", "test");
        request.put("password", "test");
        request.put("email", "test@test.com");

        var result = sut.registerUser(request);

        Assert.notNull(result);
        Assert.isTrue(result.get("Success").toString().equals("User registered successfully"));
    }

    @Test
    public void registerUserFailUsernameTest() {
        HashMap request = new HashMap();
        request.put("username", "test");
        request.put("password", "test");
        request.put("email", "test@test.com");
        sut.registerUser(request);

        request = new HashMap();
        request.put("username", "test");
        request.put("password", "test");
        request.put("email", "test1@test.com");

        var result = sut.registerUser(request);

        Assert.notNull(result);
        Assert.isTrue(result.get("Error").equals("User with this username or email already exist"));
    }

    @Test
    public void registerUserFailEmailTest() {
        HashMap request = new HashMap();
        request.put("username", "test");
        request.put("password", "test");
        request.put("email", "test@test.com");
        sut.registerUser(request);

        request = new HashMap();
        request.put("username", "test1");
        request.put("password", "test");
        request.put("email", "test@test.com");

        var result = sut.registerUser(request);

        Assert.notNull(result);
        Assert.isTrue(result.get("Error").equals("User with this username or email already exist"));
    }

    @Test
    public void deleteUserByUsernameTest() {
        HashMap<String, String> deleteRequest = new HashMap<>();
        deleteRequest.put("username", "test");
        HashMap request = new HashMap();
        request.put("username", "test");
        request.put("password", "test");
        request.put("email", "test@test.com");
        sut.registerUser(request);

        var result = sut.deleteUser(deleteRequest);

        Assert.notNull(result);
        Assert.isTrue(result.get("Success").toString().equals("User deleted successfully"));
    }

    @Test
    public void deleteUserByEmailTest() {
        HashMap<String, String> deleteRequest = new HashMap<>();
        deleteRequest.put("email", "test@test.com");
        HashMap request = new HashMap();
        request.put("username", "test");
        request.put("password", "test");
        request.put("email", "test@test.com");
        sut.registerUser(request);

        var result = sut.deleteUser(deleteRequest);

        Assert.notNull(result);
        Assert.isTrue(result.get("Success").toString().equals("User deleted successfully"));
    }

    @Test
    public void deleteUserFailTest() {
        HashMap<String, String> deleteRequest = new HashMap<>();
        HashMap request = new HashMap();
        request.put("username", "test");
        request.put("password", "test");
        request.put("email", "test@test.com");
        sut.registerUser(request);

        var result = sut.deleteUser(deleteRequest);

        Assert.notNull(result);
        Assert.isTrue(result.get("Error").toString().equals("User not found"));
    }

    @Test
    public void deleteUserByUsernameFailTest() {
        HashMap<String, String> deleteRequest = new HashMap<>();
        deleteRequest.put("username", "test1");
        HashMap request = new HashMap();
        request.put("username", "test");
        request.put("password", "test");
        request.put("email", "test@test.com");
        sut.registerUser(request);

        var result = sut.deleteUser(deleteRequest);

        Assert.notNull(result);
        Assert.isTrue(result.get("Error").toString().equals("User not found"));
    }

    @Test
    public void deleteUserByEmailFailTest() {
        HashMap<String, String> deleteRequest = new HashMap<>();
        deleteRequest.put("email", "test1@test.com");
        HashMap request = new HashMap();
        request.put("username", "test");
        request.put("password", "test");
        request.put("email", "test@test.com");
        sut.registerUser(request);

        var result = sut.deleteUser(deleteRequest);

        Assert.notNull(result);
        Assert.isTrue(result.get("Error").toString().equals("User not found"));
    }
}
