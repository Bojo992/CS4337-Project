package com.project.security.controller;

import com.project.security.model.AuthRequest;
import com.project.security.model.JwtCheckResponse;
import com.project.security.model.JwtRefreshReqest;
import com.project.security.model.JwtResponseDTO;
import com.project.security.service.JwtService;
import com.project.security.service.MyUserDetailService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;

import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin
public class ContentController {

    MyUserDetailService myUserDetailService;
    private JwtService jwtService;
    private AuthenticationManager authenticationManager;

    public ContentController(MyUserDetailService myUserDetailService, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.myUserDetailService = myUserDetailService;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/login")
    public Map<String, Object> AuthenticateAndGetToken(@RequestBody AuthRequest authRequestDTO){
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequestDTO.getUsername(), authRequestDTO.getPassword()));
        if(authentication.isAuthenticated()){
            return jwtService.initialGenerateToken(authRequestDTO.getUsername());
        } else {
            throw new UsernameNotFoundException("invalid user request!");
        }
    }

    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody HashMap<String, String> request) {
        var result = myUserDetailService.registerUser(request);
        return result;
    }

    @PostMapping("/refreshJwt")
    public JwtResponseDTO refreshJwt(@RequestBody JwtRefreshReqest authRequest) {
         return jwtService.refresh(authRequest.getToken());
    }

    @PostMapping("/checkJwtOutside")
    public JwtCheckResponse checkJwt(@RequestBody JwtRefreshReqest authRequest) {
        return jwtService.checkJwt(authRequest);
    }
}