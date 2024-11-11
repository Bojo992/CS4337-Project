package com.project.security;

import com.project.security.model.JwtResponseDTO;
import com.project.security.repository.JwtRefreshRepository;
import com.project.security.repository.MyUserRepository;
import com.project.security.service.JwtService;
import com.project.security.service.MyUserDetailService;
import io.jsonwebtoken.lang.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@TestPropertySource("classpath:application-test.properties")
@SpringBootTest
public class JwtServiceTest {
    @Autowired
    private JwtService sut;
    @Autowired
    private MyUserDetailService userDetailService;

    @Test
    public void generateToken() {
        HashMap request = new HashMap();
        request.put("username", "test");
        request.put("password", "test");
        request.put("email", "test@test.com");
        userDetailService.registerUser(request);

        var result = sut.generateToken("test");

        Assert.notNull(result);
    }

    @Test
    public void initialGenerateTokenTest() {
        HashMap request = new HashMap();
        request.put("username", "test");
        request.put("password", "test");
        request.put("email", "test@test.com");
        userDetailService.registerUser(request);

        var result = sut.initialGenerateToken("test");

        Assert.notNull(result);
        Assert.isTrue(result.containsKey("Refresh"));
        Assert.isTrue(result.containsKey("jwt"));
    }

    @Test
    public void initialGenerateTokenRefreshTokenExistTest() {
        HashMap request = new HashMap();
        request.put("username", "test");
        request.put("password", "test");
        request.put("email", "test@test.com");
        userDetailService.registerUser(request);
        sut.initialGenerateToken("test");

        var result = sut.initialGenerateToken("test");

        Assert.notNull(result);
        Assert.isTrue(result.containsKey("Refresh"));
        Assert.isTrue(result.containsKey("jwt"));
    }

    @Test
    public void initialGenerateTokenUserDoesNotExistTest() {
        HashMap request = new HashMap();
        request.put("username", "test");
        request.put("password", "test");
        request.put("email", "test@test.com");
        userDetailService.registerUser(request);

        var result = sut.initialGenerateToken("test1");

        Assert.notNull(result);
        Assert.isTrue(result.containsKey("error"));
    }

    @Test
    public void refreshTest() {
        HashMap request = new HashMap();
        request.put("username", "test");
        request.put("password", "test");
        request.put("email", "test@test.com");
        userDetailService.registerUser(request);
        var initialResponse = sut.initialGenerateToken("test");

        var result = sut.refresh(initialResponse.get("Refresh").toString());

        Assert.notNull(result);
        Assert.isTrue(!result.getToken().isEmpty());
    }

    @Test
    public void refreshFailTest() {
        var result = sut.refresh("test");

        Assert.notNull(result);
        Assert.isTrue(result.getToken() == null);
    }

    @Test
    public void extractExpirationTest() {
        String token = sut.generateToken("test");
        Date expected = new Date(System.currentTimeMillis() + 1000 * 60 * 30);

        var result = sut.extractExpiration(token);

        System.out.println("Bojo");
        System.out.println(result);
        System.out.println(expected.getTime() - result.getTime());

        Assert.notNull(result);
        Assert.isTrue((expected.getTime() - result.getTime()) <= 10000);
    }
}
