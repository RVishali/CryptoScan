package com.example.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.domain.ScanResult;
import com.example.demo.domain.WebsiteSubmission;
import com.example.demo.repo.ScanResultRepository;

@Service
public class ScanResultService {
    private final ScanResultRepository repo;
    public ScanResultService(ScanResultRepository repo) { this.repo = repo; }

    public ScanResult save(ScanResult r) { return repo.save(r); }
    public List<ScanResult> findBySubmission(WebsiteSubmission sub) { return repo.findBySubmissionOrderByRiskScoreDesc(sub); }
    public void deleteBySubmission(WebsiteSubmission sub) { repo.deleteBySubmission(sub); }
}
