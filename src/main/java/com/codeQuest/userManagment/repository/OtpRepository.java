package com.codeQuest.userManagment.repository;

import com.codeQuest.userManagment.entities.OtpCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface OtpRepository extends JpaRepository<OtpCode, Long> {
    Optional<OtpCode> findByEmail(String email);
}
