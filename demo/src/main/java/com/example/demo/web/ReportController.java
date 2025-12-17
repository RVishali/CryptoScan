package com.example.demo.web;

import com.example.demo.domain.WebsiteSubmission;
import com.example.demo.service.PDFReportService;
import com.example.demo.service.WebsiteSubmissionService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;

@RestController
@RequestMapping("/api/report")
public class ReportController {
    private final WebsiteSubmissionService submissionService;
    private final PDFReportService pdfService;

    public ReportController(WebsiteSubmissionService submissionService, PDFReportService pdfService) {
        this.submissionService = submissionService; this.pdfService = pdfService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getReport(@PathVariable Long id) {
        WebsiteSubmission s = submissionService.findById(id);
        if (s == null) return ResponseEntity.notFound().build();
        File f = pdfService.getOrGenerateReport(s);
        if (!f.exists()) return ResponseEntity.notFound().build();
        FileSystemResource fr = new FileSystemResource(f);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + f.getName() + "\"")
                .contentLength(f.length())
                .contentType(MediaType.APPLICATION_PDF)
                .body(fr);
    }
}
