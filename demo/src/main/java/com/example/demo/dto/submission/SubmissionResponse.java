package com.example.demo.dto.submission;

import java.time.Instant;

public class SubmissionResponse {
    private Long id;
    private String name;
    private String url;
    private String status;
    private Instant createdAt;
    private boolean reportAvailable;

    // NEW: severity counts
    private Integer highCount;
    private Integer mediumCount;
    private Integer lowCount;
    private Integer infoCount;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public boolean isReportAvailable() { return reportAvailable; }
    public void setReportAvailable(boolean reportAvailable) { this.reportAvailable = reportAvailable; }

    public Integer getHighCount() { return highCount; }
    public void setHighCount(Integer highCount) { this.highCount = highCount; }

    public Integer getMediumCount() { return mediumCount; }
    public void setMediumCount(Integer mediumCount) { this.mediumCount = mediumCount; }

    public Integer getLowCount() { return lowCount; }
    public void setLowCount(Integer lowCount) { this.lowCount = lowCount; }

    public Integer getInfoCount() { return infoCount; }
    public void setInfoCount(Integer infoCount) { this.infoCount = infoCount; }
}
