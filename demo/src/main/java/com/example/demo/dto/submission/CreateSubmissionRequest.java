package com.example.demo.dto.submission;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.URL;

public class CreateSubmissionRequest {
    @NotBlank
    private String name;

    @NotBlank @URL
    private String url;

    @Pattern(regexp = "^(FULL|SPIDER_ONLY)$")
    private String scanType = "FULL";

    // getters/setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public String getScanType() { return scanType; }
    public void setScanType(String scanType) { this.scanType = scanType; }
}
