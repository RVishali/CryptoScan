package com.example.demo.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.example.demo.domain.WebsiteSubmission;
import com.example.demo.service.zap.ZapScanService;

@Service
public class ZapAsyncExecutor {

    private final ZapScanService zapScanService;
    private final WebsiteSubmissionService submissionService;

    public ZapAsyncExecutor(ZapScanService zapScanService,
                            WebsiteSubmissionService submissionService) {
        this.zapScanService = zapScanService;
        this.submissionService = submissionService;
    }

    @Async
    public void runScanAsync(WebsiteSubmission submission) {
        try {
            // Reload submission (recommended for async)
            WebsiteSubmission fresh = submissionService.findById(submission.getId());

            if (fresh == null) {
                System.err.println("Submission not found id=" + submission.getId());
                return;
            }

            System.out.println("Starting async scan for: " + fresh.getTargetUrl());

            // THE MAIN SCAN — everything happens inside ZapScanService
            zapScanService.runFullScan(fresh);

            System.out.println("Async scan finished for: " + fresh.getTargetUrl());

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
