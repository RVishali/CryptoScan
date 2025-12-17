package com.example.demo.service;

import com.example.demo.domain.AppUser;
import com.example.demo.domain.WebsiteSubmission;
import com.example.demo.repo.WebsiteSubmissionRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class WebsiteSubmissionService {
    private final WebsiteSubmissionRepository repo;
    public WebsiteSubmissionService(WebsiteSubmissionRepository repo) { this.repo = repo; }

    public WebsiteSubmission create(AppUser user, String name, String url, String scanType) {
        WebsiteSubmission s = new WebsiteSubmission();
        s.setUser(user);
        s.setName(name);
        s.setTargetUrl(url);
        s.setScanType(scanType);
        s.setStatus("PENDING");
        s.setCreatedAt(Instant.now());
        return repo.save(s);
    }

    public WebsiteSubmission findById(Long id) { return repo.findById(id).orElse(null); }
    public WebsiteSubmission save(WebsiteSubmission s) { return repo.save(s); }
    public List<WebsiteSubmission> getHistory(AppUser user) { return repo.findByUserOrderByCreatedAtDesc(user); }
}
