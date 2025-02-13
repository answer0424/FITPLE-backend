package com.lec.spring.base.jwt;

import com.lec.spring.base.config.PrincipalDetails;
import com.lec.spring.base.domain.Gym;
import com.lec.spring.base.domain.HBTI;
import com.lec.spring.base.domain.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Date;

public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    public JWTFilter(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        //hjy
        String path = request.getServletPath();

        // 비밀번호 재설정 경로를 필터링하지 않도록 설정
        if (path.startsWith("/member/reset-password")) {
            filterChain.doFilter(request, response);
            System.out.println("jwt에서 제외됨");
            return;
        }
        //

        System.out.println("💜클라이언트에서 다시 여기로 왔음 JWT 확인해야 하거든");
        System.out.println("🔹 JWTFilter.doFilterInternal() 호출");

        // 요청의 Authorization 헤더 가져오기
        String authorization = request.getHeader("Authorization");
        System.out.println("🔹 받은 Authorization 헤더: " + authorization);

        // Authorization 헤더가 없거나 올바른 형식이 아닐 경우
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            System.out.println("❌ JWT 토큰이 없거나 올바른 형식이 아닙니다.");
            filterChain.doFilter(request, response); // 다음 필터로 전달
            return;
        }

        // "Bearer " 부분을 제거하고 토큰만 추출
        String token = authorization.substring(7);
        System.out.println("🔹 추출한 JWT 토큰: " + token);

        // ✅ 토큰이 비어 있는지 확인
        if (token.trim().isEmpty()) {
            System.out.println("❌ JWT 토큰이 비어 있습니다.");
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // JWT 파싱
            Long id = jwtUtil.getId(token);
            String username = jwtUtil.getUsername(token);
            String email = jwtUtil.getEmail(token);
            String nickname = jwtUtil.getNickname(token);
            String brith = jwtUtil.getBirth(token);
            Date birthDate = (brith != null) ? new Date(brith) : null;
            String provider = jwtUtil.getProvider(token);
            String address = jwtUtil.getAddress(token);
            String authority = jwtUtil.getAuthority(token);
            Gym gym = jwtUtil.getGym(token);
            HBTI hbti = jwtUtil.getHbti(token);
            String profileImage = jwtUtil.getProfileImage(token);

            System.out.println("✅ JWT 파싱 완료: userId=" + id + ", username=" + username + ", authority=" + authority);

            // User 객체 생성
            User user = User.builder()
                    .id(id)
                    .username(username)
                    .password("jwtwithfitple")    // 임시 비밀번호
                    .email(email)
                    .nickname(nickname)
                    .provider(provider)
                    .address(address)
                    .authority(authority)
                    .birth(birthDate)
                    .gym(gym)
                    .hbti(hbti)
                    .profileImage(profileImage)
                    .build();

            // UserDetails 생성
            PrincipalDetails userDetails = new PrincipalDetails(user);

            // Spring Security 인증 객체 생성
            Authentication authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

            // 인증 정보를 SecurityContext에 저장
            SecurityContextHolder.getContext().setAuthentication(authToken);



        } catch (Exception e) {
            System.err.println("❌ JWT 파싱 오류: " + e.getMessage());
        }
        System.out.println("🤎여기서 컨트롤러로 가야 함");
        // 다음 필터로 전달
        filterChain.doFilter(request, response);
    }


}
