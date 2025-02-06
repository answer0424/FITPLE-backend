package com.lec.spring.base.config;

import com.lec.spring.base.domain.User;
import com.lec.spring.base.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PrincipalDetailService implements UserDetailsService {

    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("loadUserByUsername : " + username);

        // DB 조회
        User user = userService.findByUsername(username);
        System.out.println("Loaded User: " + user);

        if(user != null) {
            // UserDetails 에 담아서 return 하면 AuthenticationManager 가 검증함
            return new PrincipalDetails(user);
        }

        // 해당 username 의 user 가 없다면
        throw new UsernameNotFoundException(username);
    }
}
