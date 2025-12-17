package com.example.demo.web;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.domain.AppUser;
import com.example.demo.domain.WebsiteSubmission;
import com.example.demo.dto.submission.CreateSubmissionRequest;
import com.example.demo.dto.submission.SubmissionResponse;
import com.example.demo.service.UserService;
import com.example.demo.service.WebsiteSubmissionService;
import com.example.demo.service.ZapAsyncExecutor;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/submission")
public class WebsiteSubmissionController {
    private final UserService userService;
    private final WebsiteSubmissionService submissionService;
    private final ZapAsyncExecutor zapAsyncExecutor;

    public WebsiteSubmissionController(UserService userService,
                                   WebsiteSubmissionService submissionService,
                                   ZapAsyncExecutor zapAsyncExecutor) {
        this.userService = userService;
        this.submissionService = submissionService;
        this.zapAsyncExecutor = zapAsyncExecutor;
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@Valid @RequestBody CreateSubmissionRequest req) {
        AppUser u = userService.getCurrentUser();
        if (u == null) return ResponseEntity.status(401).build();

        WebsiteSubmission s = submissionService.create(u, req.getName(), req.getUrl(), req.getScanType());

        // start scan async
        zapAsyncExecutor.runScanAsync(s);

        SubmissionResponse resp = new SubmissionResponse();
        resp.setId(s.getId());
        resp.setName(s.getName());
        resp.setUrl(s.getTargetUrl());
        resp.setStatus(s.getStatus());
        resp.setCreatedAt(s.getCreatedAt());
        resp.setReportAvailable(s.getPdfReportPath() != null);

        // set counts (may be zero initially)
        resp.setHighCount(s.getHighCount());
        resp.setMediumCount(s.getMediumCount());
        resp.setLowCount(s.getLowCount());
        resp.setInfoCount(s.getInfoCount());

        return ResponseEntity.ok(resp);
    }

    @GetMapping("/me")
    public ResponseEntity<?> mySubs() {
        AppUser u = userService.getCurrentUser();
        if (u == null) return ResponseEntity.status(401).build();
        List<SubmissionResponse> list = new ArrayList<>();
        for (WebsiteSubmission s : submissionService.getHistory(u)) {
            SubmissionResponse r = new SubmissionResponse();
            r.setId(s.getId());
            r.setName(s.getName());
            r.setUrl(s.getTargetUrl());
            r.setStatus(s.getStatus());
            r.setCreatedAt(s.getCreatedAt());
            r.setReportAvailable(s.getPdfReportPath() != null);

            r.setHighCount(s.getHighCount());
            r.setMediumCount(s.getMediumCount());
            r.setLowCount(s.getLowCount());
            r.setInfoCount(s.getInfoCount());

            list.add(r);
        }
        return ResponseEntity.ok(list);
    }
}
