package com.example.demo.dto.zap;

public class ScanStatusResponse {
    private String status;
    private Integer spiderProgress;
    private Integer activeScanProgress;

    // getters/setters
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Integer getSpiderProgress() { return spiderProgress; }
    public void setSpiderProgress(Integer spiderProgress) { this.spiderProgress = spiderProgress; }
    public Integer getActiveScanProgress() { return activeScanProgress; }
    public void setActiveScanProgress(Integer activeScanProgress) { this.activeScanProgress = activeScanProgress; }
}
