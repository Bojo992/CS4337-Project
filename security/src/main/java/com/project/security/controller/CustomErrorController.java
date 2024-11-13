package com.project.security.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping
public class CustomErrorController implements ErrorController {
    @GetMapping("/error")
    public String handleError(HttpServletRequest httpServletRequest) {
        Object statusCode = httpServletRequest.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        if (statusCode != null && Integer.parseInt(statusCode.toString()) == HttpStatus.FORBIDDEN.value()) {
            return "403";
        } else if (statusCode != null && Integer.parseInt(statusCode.toString()) == HttpStatus.NOT_FOUND.value()) {
            return "404";
        }
        return "error";
    }
}