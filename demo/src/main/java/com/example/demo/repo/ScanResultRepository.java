package com.example.demo.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.domain.ScanResult;
import com.example.demo.domain.WebsiteSubmission;

public interface ScanResultRepository extends JpaRepository<ScanResult, Long> {
    List<ScanResult> findBySubmissionOrderByRiskScoreDesc(WebsiteSubmission submission);
    void deleteBySubmission(WebsiteSubmission submission);

    public void deleteBySubmissionId(Long submissionId);
}
