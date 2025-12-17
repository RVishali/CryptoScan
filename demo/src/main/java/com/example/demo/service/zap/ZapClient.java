package com.example.demo.service.zap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ZapClient {

    @Value("${zap.base-url}")
    private String zapBase;

    @Value("${zap.api.key}")
    private String apiKey;

    private String buildUrl(String path) {
        try {
            String full = zapBase + path;
            if (full.contains("?")) {
                return full + "&apikey=" + URLEncoder.encode(apiKey, StandardCharsets.UTF_8.toString());
            } else {
                return full + "?apikey=" + URLEncoder.encode(apiKey, StandardCharsets.UTF_8.toString());
            }
        } catch (Exception e) {
            return zapBase + path;
        }
    }

    private String readBody(ClassicHttpResponse response) throws IOException {
        if (response.getEntity() == null) return "";
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent(), StandardCharsets.UTF_8)
        )) {
            return br.lines().collect(Collectors.joining("\n"));
        }
    }

    private String GET(String path) throws IOException {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet req = new HttpGet(buildUrl(path));
            ClassicHttpResponse res = (ClassicHttpResponse) client.execute(req);
            return readBody(res);
        }
    }

    // ---------- Public API ----------

    public String ping() throws IOException {
        return GET("/JSON/core/view/version/");
    }

    public String startSpider(String target) throws IOException {
        return GET("/JSON/spider/action/scan/?url=" + URLEncoder.encode(target, StandardCharsets.UTF_8));
    }

    public String spiderStatus(String scanId) throws IOException {
        return GET("/JSON/spider/view/status/?scanId=" + scanId);
    }

    public String startActiveScan(String target) throws IOException {
        return GET("/JSON/ascan/action/scan/?url=" + URLEncoder.encode(target, StandardCharsets.UTF_8));
    }

    public String activeScanStatus(String scanId) throws IOException {
        return GET("/JSON/ascan/view/status/?scanId=" + scanId);
    }

    public String getAlerts(String target) throws IOException {
        return GET("/JSON/core/view/alerts/?baseurl=" + URLEncoder.encode(target, StandardCharsets.UTF_8) + "&start=0&count=9999");
    }

    public static int parseProgressFromStatusJson(String json) {
    if (json == null || json.isBlank()) return 100;

    // Expected: {"status":"43"}
    int idx = json.indexOf("\"status\"");
    if (idx == -1) return 100;

    int start = json.indexOf(":", idx) + 1;
    int end = json.indexOf("}", start);

    String number = json.substring(start, end).replaceAll("[^0-9]", "");
    try {
        return Integer.parseInt(number);
    } catch (Exception e) {
        return 100;
    }
}


    public static String parseScanId(String json) {
    // Expected: {"scan":"0"}
    if (json == null || json.isBlank()) return "0";
    json = json.trim();
    int idx = json.indexOf("\"scan\"");
    if (idx == -1) return "0";
    int start = json.indexOf(":", idx) + 1;
    int end = json.indexOf("}", start);
    String raw = json.substring(start, end).replaceAll("[^0-9]", "");
    return raw.isBlank() ? "0" : raw;
}

}
