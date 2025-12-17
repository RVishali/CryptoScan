package com.example.demo.web;

import java.util.Collections;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.domain.ScanResult;
import com.example.demo.domain.WebsiteSubmission;
import com.example.demo.service.ScanResultService;
import com.example.demo.service.WebsiteSubmissionService;

@RestController
@RequestMapping("/api/results")
public class ScanResultController {

    private final ScanResultService scanResultService;
    private final WebsiteSubmissionService submissionService;

    public ScanResultController(
            ScanResultService scanResultService,
            WebsiteSubmissionService submissionService
    ) {
        this.scanResultService = scanResultService;
        this.submissionService = submissionService;
    }

    /**
     * GET /api/results/{submissionId}
     * Returns all vulnerabilities for a given scan submission.
     * If submission not found → returns 404.
     */
    @GetMapping("/{submissionId}")
    public ResponseEntity<List<ScanResult>> getResults(@PathVariable Long submissionId) {
        WebsiteSubmission sub = submissionService.findById(submissionId);
        if (sub == null) {
            return ResponseEntity.notFound().build();
        }

        List<ScanResult> results = scanResultService.findBySubmission(sub);
        if (results == null) results = Collections.emptyList();
        return ResponseEntity.ok(results);
    }
}
