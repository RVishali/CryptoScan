package com.example.demo.domain;

import java.time.Instant;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class WebsiteSubmission {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private AppUser user;

    private String name;
    private String targetUrl;
    private String scanType;
    private String status = "PENDING";
    private Instant createdAt = Instant.now();
    private Instant startedAt;
    private Instant finishedAt;
    private Integer spiderProgress = 0;
    private Integer activeScanProgress = 0;
    private String pdfReportPath;

    // NEW: severity counters
    private Integer highCount = 0;
    private Integer mediumCount = 0;
    private Integer lowCount = 0;
    private Integer infoCount = 0;

    // getters / setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public AppUser getUser() { return user; }
    public void setUser(AppUser user) { this.user = user; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getTargetUrl() { return targetUrl; }
    public void setTargetUrl(String targetUrl) { this.targetUrl = targetUrl; }

    public String getScanType() { return scanType; }
    public void setScanType(String scanType) { this.scanType = scanType; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getStartedAt() { return startedAt; }
    public void setStartedAt(Instant startedAt) { this.startedAt = startedAt; }

    public Instant getFinishedAt() { return finishedAt; }
    public void setFinishedAt(Instant finishedAt) { this.finishedAt = finishedAt; }

    public Integer getSpiderProgress() { return spiderProgress; }
    public void setSpiderProgress(Integer spiderProgress) { this.spiderProgress = spiderProgress; }

    public Integer getActiveScanProgress() { return activeScanProgress; }
    public void setActiveScanProgress(Integer activeScanProgress) { this.activeScanProgress = activeScanProgress; }

    public String getPdfReportPath() { return pdfReportPath; }
    public void setPdfReportPath(String pdfReportPath) { this.pdfReportPath = pdfReportPath; }

    // new severity count getters/setters
    public Integer getHighCount() { return highCount; }
    public void setHighCount(Integer highCount) { this.highCount = highCount; }

    public Integer getMediumCount() { return mediumCount; }
    public void setMediumCount(Integer mediumCount) { this.mediumCount = mediumCount; }

    public Integer getLowCount() { return lowCount; }
    public void setLowCount(Integer lowCount) { this.lowCount = lowCount; }

    public Integer getInfoCount() { return infoCount; }
    public void setInfoCount(Integer infoCount) { this.infoCount = infoCount; }
}
