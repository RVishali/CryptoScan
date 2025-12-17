package com.example.demo.web;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.domain.WebsiteSubmission;
import com.example.demo.dto.zap.ScanStatusResponse;
import com.example.demo.service.WebsiteSubmissionService;
import com.example.demo.service.zap.ZapClient;

@RestController
@RequestMapping("/api/zap")
public class ZapStatusController {

    private static final Logger log = LoggerFactory.getLogger(ZapStatusController.class);

    private final WebsiteSubmissionService submissionService;
    private final ZapClient client;

    public ZapStatusController(WebsiteSubmissionService submissionService, ZapClient client) {
        this.submissionService = submissionService;
        this.client = client;
        log.info(">>> ZapStatusController LOADED <<<");
    }

    @GetMapping("/status/{submissionId}")
    public ResponseEntity<?> status(@PathVariable Long submissionId) {
        WebsiteSubmission s = submissionService.findById(submissionId);
        if (s == null) return ResponseEntity.notFound().build();
        ScanStatusResponse r = new ScanStatusResponse();
        r.setStatus(s.getStatus());
        r.setSpiderProgress(s.getSpiderProgress());
        r.setActiveScanProgress(s.getActiveScanProgress());
        return ResponseEntity.ok(r);
    }

    @GetMapping("/ping")
    public ResponseEntity<?> ping() {
        log.debug("Received /api/zap/ping request");
        try {
            String resp = client.ping();
            log.info("ZAP ping response: {}", resp);
            return ResponseEntity.ok(resp);
        } catch (IOException e) {
            log.error("ZAP ping failed (IOException): {}", e.toString(), e);
            return ResponseEntity.status(502).body(java.util.Map.of(
                    "error", "Failed to contact ZAP",
                    "message", e.getMessage()
            ));
        } catch (Exception e) {
            log.error("ZAP ping unexpected error: {}", e.toString(), e);
            return ResponseEntity.status(500).body(java.util.Map.of(
                    "error", "Unexpected error",
                    "message", e.getMessage()
            ));
        }
    }
}
