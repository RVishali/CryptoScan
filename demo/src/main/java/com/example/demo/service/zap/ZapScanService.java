package com.example.demo.service.zap;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.example.demo.domain.ScanResult;
import com.example.demo.domain.WebsiteSubmission;
import com.example.demo.service.EmailService;
import com.example.demo.service.PDFReportService;
import com.example.demo.service.ScanResultService;
import com.example.demo.service.WebsiteSubmissionService;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

@Service
public class ZapScanService {

    private final ZapClient client;
    private final WebsiteSubmissionService submissionService;
    private final ScanResultService scanResultService;
    private final PDFReportService pdfService;
    private final EmailService emailService;
    

    public ZapScanService(
            ZapClient client,
            WebsiteSubmissionService submissionService,
            ScanResultService scanResultService,
            PDFReportService pdfService, EmailService emailService
    ) {
        this.client = client;
        this.submissionService = submissionService;
        this.scanResultService = scanResultService;
        this.pdfService = pdfService;
        this.emailService = emailService;
    }

    /**
     * Runs Spider + Active Scan (if FULL) + Alert parsing + PDF generation
     */
    public void runFullScan(WebsiteSubmission submission) {
        submission.setStatus("RUNNING");
        submission.setStartedAt(Instant.now());
        submissionService.save(submission);

        try {
            // ---- Step 1: Spider Scan ----
            String spiderResp = client.startSpider(submission.getTargetUrl());
            String spiderScanId = ZapClient.parseScanId(spiderResp);

            pollSpider(submission, spiderScanId);

            // ---- Step 2: Active Scan (for FULL scans only) ----
            if ("FULL".equalsIgnoreCase(submission.getScanType())) {
                String asResp = client.startActiveScan(submission.getTargetUrl());
                String asId = ZapClient.parseScanId(asResp);

                pollActiveScan(submission, asId);
            }

            // ---- Step 3: Parse Alerts ----
            String alertsJson = client.getAlerts(submission.getTargetUrl());
            saveAlerts(alertsJson, submission);

            // ---- Step 4: PDF report ----
            // after PDF creation:
submission.setStatus("COMPLETED");
submission.setFinishedAt(Instant.now());
submissionService.save(submission);

String path = pdfService.generatePdf(submission.getId());
submission.setPdfReportPath(path);
submissionService.save(submission);


// SEND EMAIL
emailService.sendReportWithAttachment(
    submission.getUser().getEmail(),
    "Your Vulnerability Scan Report is Ready",
    "Hello " + submission.getUser().getUsername() + 
    ",\n\nYour scan for " + submission.getTargetUrl() + " is completed.\nAttached is your security report.\n",
    new File(path)
);


        } catch (Exception ex) {
            submission.setStatus("FAILED");
            submission.setFinishedAt(Instant.now());
            submissionService.save(submission);
            throw new RuntimeException("Scan failed: " + ex.getMessage(), ex);
        }
    }

    /**
     * Spider polling with exponential backoff
     */
    private void pollSpider(WebsiteSubmission submission, String scanId)
            throws IOException, InterruptedException {

        long delay = 500;
        final long maxDelay = 10000;

        while (true) {
            String json = client.spiderStatus(scanId);
            int progress = ZapClient.parseProgressFromStatusJson(json);

            submission.setSpiderProgress(progress);
            submissionService.save(submission);

            if (progress >= 100) break;

            Thread.sleep(delay);
            delay = Math.min(maxDelay, delay * 2);
        }
    }

    /**
     * Active scan polling with exponential backoff
     */
    private void pollActiveScan(WebsiteSubmission submission, String scanId)
            throws IOException, InterruptedException {

        long delay = 1000;
        final long maxDelay = 15000;

        while (true) {
            String json = client.activeScanStatus(scanId);
            int progress = ZapClient.parseProgressFromStatusJson(json);

            submission.setActiveScanProgress(progress);
            submissionService.save(submission);

            if (progress >= 100) break;

            Thread.sleep(delay);
            delay = Math.min(maxDelay, delay * 2);
        }
    }

    /**
     * Parse alerts JSON from ZAP and save to DB, also update severity counts on submission.
     */
    private void saveAlerts(String alertsJson, WebsiteSubmission submission) {

    if (alertsJson == null || alertsJson.isBlank()) {
        submission.setHighCount(0);
        submission.setMediumCount(0);
        submission.setLowCount(0);
        submission.setInfoCount(0);
        submissionService.save(submission);
        return;
    }

    // Clear old results
    scanResultService.deleteBySubmission(submission);

    Map<String, GroupedAlert> grouped = new HashMap<>();

    try (JsonReader jr = Json.createReader(new StringReader(alertsJson))) {
        JsonObject root = jr.readObject();
        JsonArray alerts = root.getJsonArray("alerts");

        if (alerts == null) return;

        for (int i = 0; i < alerts.size(); i++) {
            JsonObject a = alerts.getJsonObject(i);

            String name = a.getString("name", "Unknown");
            String severity = a.getString("risk", "Informational");
            String desc = a.getString("description", "");
            String fix = a.getString("solution", "");
            String url = a.getString("url", "");

            grouped.putIfAbsent(name, new GroupedAlert(name, severity, desc, fix));
            grouped.get(name).urls.add(url);
        }
    }

    int high = 0, medium = 0, low = 0, info = 0;

    for (GroupedAlert g : grouped.values()) {

        ScanResult r = new ScanResult();
        r.setSubmission(submission);
        r.setVulnerabilityType(g.name);
        r.setSeverity(g.severity);
        r.setDescription(g.description);
        r.setFix(g.fix);

        // Join URLs with newline
        StringBuilder sb = new StringBuilder();
        for (String u : g.urls) sb.append(u).append("\n");
        r.setAffectedUrls(sb.toString());

        // Score
        double score;
        switch (g.severity.toLowerCase()) {
            case "high":
                score = 8.0;
                break;
            case "medium":
                score = 5.0;
                break;
            case "low":
                score = 2.0;
                break;
            default:
                score = 1.0;
        }
        r.setRiskScore(score);

        scanResultService.save(r);

        switch (g.severity.toLowerCase()) {
            case "high":
                high++;
                break;
            case "medium":
                medium++;
                break;
            case "low":
                low++;
                break;
            default:
                info++;
        }
    }

    submission.setHighCount(high);
    submission.setMediumCount(medium);
    submission.setLowCount(low);
    submission.setInfoCount(info);
    submissionService.save(submission);
}

private static class GroupedAlert {
    String name, severity, description, fix;
    List<String> urls = new ArrayList<>();

    GroupedAlert(String n, String s, String d, String f) {
        name = n; severity = s; description = d; fix = f;
    }
}


}
