package com.project.security;

import com.project.security.controller.ContentController;
import com.project.security.model.AuthRequest;
import com.project.security.service.MyUserDetailService;
import io.jsonwebtoken.lang.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.TestPropertySource;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertThrows;

@TestPropertySource("classpath:application-test.properties")
@SpringBootTest
public class ContentControllerTest {
    @Autowired
    private ContentController sut;
    @Autowired
    public MyUserDetailService userDetailService;

    @Test
    public void authenticateAndGetTokenTest() {
        HashMap request = new HashMap();
        request.put("username", "test");
        request.put("password", "test");
        request.put("email", "test@test.com");
        userDetailService.registerUser(request);
        var authRequest = AuthRequest.builder().password("test").username("test").build();

        var result = sut.AuthenticateAndGetToken(authRequest);

        Assert.notNull(result);
        Assert.isTrue(result.containsKey("Refresh"));
        Assert.isTrue(result.containsKey("jwt"));
    }

    @Test
    public void authenticateAndGetTokenFailTest() {
        HashMap request = new HashMap();
        request.put("username", "test");
        request.put("password", "test");
        request.put("email", "test@test.com");
        userDetailService.registerUser(request);
        var authRequest = AuthRequest.builder().password("test1").username("test").build();

        var result = assertThrows(BadCredentialsException.class, () -> sut.AuthenticateAndGetToken(authRequest));

        Assert.notNull(result);
        Assert.isTrue(result.getMessage().equals("Bad credentials"));
    }

    @Test
    public void registerTest() {
        HashMap<String, String> request = new HashMap();
        request.put("username", "test");
        request.put("password", "test");
        request.put("email", "test@test.com");

        var result = sut.register(request);

        Assert.notNull(result);
        Assert.isTrue(result.get("Success").toString().equals("User registered successfully"));
    }

    @Test
    public void registerFailUsernameTest() {
        HashMap<String, String> request = new HashMap();
        request.put("username", "test");
        request.put("password", "test");
        request.put("email", "test@test.com");
        userDetailService.registerUser(request);

        request = new HashMap();
        request.put("username", "test");
        request.put("password", "test");
        request.put("email", "test1@test.com");

        var result = sut.register(request);

        Assert.notNull(result);
        Assert.isTrue(result.get("Error").equals("User with this username or email already exist"));
    }

    @Test
    public void registerFailEmailTest() {
        HashMap<String, String> request = new HashMap();
        request.put("username", "test");
        request.put("password", "test");
        request.put("email", "test@test.com");
        userDetailService.registerUser(request);

        request = new HashMap();
        request.put("username", "test1");
        request.put("password", "test");
        request.put("email", "test@test.com");

        var result = sut.register(request);

        Assert.notNull(result);
        Assert.isTrue(result.get("Error").equals("User with this username or email already exist"));
    }

    @Test
    public void deleteByUsernameTest() {
        HashMap<String, String> request = new HashMap();
        request.put("username", "test");
        request.put("password", "test");
        request.put("email", "test@test.com");
        userDetailService.registerUser(request);
        HashMap<String, String> deleteRequest = new HashMap();
        deleteRequest.put("username", "test");

        var result = sut.deleteUser(deleteRequest);

        Assert.notNull(result);
        Assert.isTrue(result.get("Success").toString().equals("User deleted successfully"));
    }

    @Test
    public void deleteByEmailTest() {
        HashMap<String, String> request = new HashMap();
        request.put("username", "test");
        request.put("password", "test");
        request.put("email", "test@test.com");
        var test = sut.register(request);
        HashMap<String, String> deleteRequest = new HashMap();
        deleteRequest.put("email", "test@test.com");

        var result = sut.deleteUser(deleteRequest);

        System.out.println("Bojo");
        System.out.println(result);
        System.out.println(test);

        Assert.notNull(result);
        Assert.isTrue(result.get("Success").toString().equals("User deleted successfully"));
    }

    @Test
    public void deleteByEmailFailTest() {
        HashMap<String, String> request = new HashMap();
        request.put("username", "test");
        request.put("password", "test");
        request.put("email", "test@test.com");
        userDetailService.registerUser(request);
        HashMap<String, String> deleteRequest = new HashMap();
        deleteRequest.put("email", "test1@test.com");

        var result = sut.deleteUser(deleteRequest);

        Assert.notNull(result);
        Assert.isTrue(result.get("Error").toString().equals("User not found"));
    }

    @Test
    public void deleteByUsernameFailTest() {
        HashMap<String, String> request = new HashMap();
        request.put("username", "test");
        request.put("password", "test");
        request.put("email", "test@test.com");
        userDetailService.registerUser(request);
        HashMap<String, String> deleteRequest = new HashMap();
        deleteRequest.put("email", "test1@test.com");

        var result = sut.deleteUser(deleteRequest);

        Assert.notNull(result);
        Assert.isTrue(result.get("Error").toString().equals("User not found"));
    }

    @Test
    public void deleteFailTest() {
        HashMap<String, String> request = new HashMap();
        request.put("username", "test");
        request.put("password", "test");
        request.put("email", "test@test.com");
        userDetailService.registerUser(request);
        HashMap<String, String> deleteRequest = new HashMap();

        var result = sut.deleteUser(deleteRequest);

        Assert.notNull(result);
        Assert.isTrue(result.get("Error").toString().equals("User not found"));
    }
}
