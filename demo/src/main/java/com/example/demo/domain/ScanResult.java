
package com.example.demo.domain;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class ScanResult {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private WebsiteSubmission submission;

    private String vulnerabilityType;
    private String severity;

    @Column(length = 4000)
    private String description;

    @Column(length = 4000)
    private String fix;

    @Column(length = 8000)
    private String affectedUrls;   // NEW FIELD

    private Double riskScore = 0.0;

    private Instant createdAt = Instant.now();

    // ------- GETTERS & SETTERS -------

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public WebsiteSubmission getSubmission() { return submission; }
    public void setSubmission(WebsiteSubmission submission) { this.submission = submission; }

    public String getVulnerabilityType() { return vulnerabilityType; }
    public void setVulnerabilityType(String vulnerabilityType) { this.vulnerabilityType = vulnerabilityType; }

    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getFix() { return fix; }
    public void setFix(String fix) { this.fix = fix; }

    public String getAffectedUrls() { return affectedUrls; }
    public void setAffectedUrls(String affectedUrls) { this.affectedUrls = affectedUrls; }

    public Double getRiskScore() { return riskScore; }
    public void setRiskScore(Double riskScore) { this.riskScore = riskScore; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
