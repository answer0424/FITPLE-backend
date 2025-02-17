package com.lec.spring.base.controller;

import com.lec.spring.base.DTO.UserRegistrationDTO;
import com.lec.spring.base.config.PrincipalDetails;
import com.lec.spring.base.domain.User;
import com.lec.spring.base.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/register")
public class UserController {

    private final UserService userService;

    @PostMapping("/student")
    public ResponseEntity<?> registerStudent(@RequestBody UserRegistrationDTO registration) {
        System.out.println("회원가입 한 student 유저에 받아온 값 : " + registration);
        User user = userService.registerUser(registration, "ROLE_STUDENT");
        System.out.println("register user : " + user);
        if(user == null){
            return new ResponseEntity<>("Student registered failed", HttpStatus.CONFLICT);
        }else {
            return new ResponseEntity<>("Student registered successfully with role: ROLE_STUDENT", HttpStatus.OK);
        }
    }

    @PostMapping("/trainer")
    public ResponseEntity<?> registerTrainer(@RequestBody UserRegistrationDTO registration) {
        System.out.println("회원가입 한 trainer 유저에 받아온 값 : " + registration);
        User user = userService.registerUser(registration, "ROLE_TRAINER");
        System.out.println("register user : " + user);
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


    @GetMapping("/user")
    public ResponseEntity<User> getCurrentUser(@AuthenticationPrincipal PrincipalDetails userDetails){


        if (userDetails != null) {
            User user = userService.findByUsername(userDetails.getUsername());
            return new ResponseEntity<>(user, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
}

