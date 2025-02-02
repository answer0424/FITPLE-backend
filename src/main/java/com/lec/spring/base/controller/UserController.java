package com.lec.spring.base.controller;

import com.lec.spring.base.DTO.UserRegistrationDTO;
import com.lec.spring.base.config.PrincipalDetails;
import com.lec.spring.base.domain.User;
import com.lec.spring.base.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

@RestController
@RequestMapping("/register")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/student")
    public ResponseEntity<?> registerStudent(@RequestBody UserRegistrationDTO registration) {
        System.out.println("student 권한으로 회원가입 요청");
        User user = userService.registerUser(registration, "ROLE_STUDENT");
        if(user == null){
            return new ResponseEntity<>("Student registered failed", HttpStatus.CONFLICT);
        }else {
            return new ResponseEntity<>("Student registered successfully with role: ROLE_STUDENT", HttpStatus.OK);
        }
    }

    @PostMapping("/trainer")
    public ResponseEntity<?> registerTrainer(@RequestBody UserRegistrationDTO registration) {
        System.out.println("trainer 권한으로 회원가입 요청");
        User user = userService.registerUser(registration, "ROLE_TRAINER");
        if(user == null){
            return new ResponseEntity<>("Trainer registered failed", HttpStatus.CONFLICT);
        }else {
            return new ResponseEntity<>("Trainer registered successfully with role: ROLE_TRAINER", HttpStatus.OK);
        }
    }

    @GetMapping("/auth")
    public ResponseEntity<?> auth(HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return ResponseEntity.ok(authentication);
    }

    //--------------------------------------------------------------------------------
    // 확인용
    @RequestMapping("/user")
    public User user(@AuthenticationPrincipal PrincipalDetails userDetails){
        return (userDetails != null) ? userDetails.getUser() : null;
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        System.out.println(users);
        return ResponseEntity.ok(users);
    }
}

