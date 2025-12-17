package com.example.demo.repo;

import com.example.demo.domain.AppUser;
import com.example.demo.domain.WebsiteSubmission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WebsiteSubmissionRepository extends JpaRepository<WebsiteSubmission, Long> {
    List<WebsiteSubmission> findByUserOrderByCreatedAtDesc(AppUser user);
}
