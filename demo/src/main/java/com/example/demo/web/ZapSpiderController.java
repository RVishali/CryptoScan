package com.example.demo.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.zap.ZapClient;

@RestController
@RequestMapping("/api/zap/spider")
public class ZapSpiderController {

    private final ZapClient client;

    public ZapSpiderController(ZapClient client) {
        this.client = client;
    }

    @GetMapping("/start")
public ResponseEntity<?> start(@RequestParam String url) {
    System.out.println(">>> ENTERED CONTROLLER METHOD <<<");
    System.out.println(">>> URL param = " + url);

    try {
        String resp = client.startSpider(url);
        System.out.println(">>> ZAP RESP = " + resp);
        return ResponseEntity.ok(resp);
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(500).body("ERR: " + e.getMessage());
    }
}


    @GetMapping("/status")
    public ResponseEntity<?> status(@RequestParam String scanId) {
        try {
            String json = client.spiderStatus(scanId);
            return ResponseEntity.ok(json);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(java.util.Map.of("error", e.getMessage()));
        }
    }
}
