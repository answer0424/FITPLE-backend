package com.lec.spring.base.repository;


import com.lec.spring.base.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // 회원 가입 시 필요
    Boolean existsByUsername(String username);

    User findByUsername(String username);
    User findByNickname(String nickname);

    User findByEmail(String email);
    // hjy
    @Query("SELECT u FROM User u WHERE :role = u.authority")
    List<User> findByAuthorities(@Param("role") String role);

    //hjy
    @Query("SELECT u FROM User u WHERE :role = u.authority AND u.nickname LIKE %:nickname%")
    List<User> findByRoleAndNickname(@Param("role") String role, @Param("nickname") String nickname);

    Page<User> findByAuthority(String authority, Pageable pageable);


}
