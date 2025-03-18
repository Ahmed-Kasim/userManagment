package com.codeQuest.userManagment.repository;

import com.codeQuest.userManagment.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByPhoneNum(String phoneNum);
    User findByEmail(String email);
    boolean existsByPhoneNum(String phoneNum);
    boolean existsByEmail(String email);

    Optional<User> findById(Long accId);
}