package com.example.demo.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.domain.WebsiteSubmission;
import com.example.demo.service.WebsiteSubmissionService;
import com.example.demo.service.ZapAsyncExecutor;

@RestController
@RequestMapping("/api/zap")
public class ZapScanController {

    private final WebsiteSubmissionService submissionService;
    private final ZapAsyncExecutor executor;

    public ZapScanController(WebsiteSubmissionService submissionService, ZapAsyncExecutor executor) {
        this.submissionService = submissionService;
        this.executor = executor;
    }

    @PostMapping("/start")
    public ResponseEntity<?> start(@RequestParam Long submissionId) {
        WebsiteSubmission s = submissionService.findById(submissionId);
        if (s == null) return ResponseEntity.badRequest().body("submission not found");

        executor.runScanAsync(s);
        return ResponseEntity.ok(java.util.Map.of("message", "Scan started for: " + s.getTargetUrl()));
    }
}
