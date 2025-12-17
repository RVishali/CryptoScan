package com.example.demo.service;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.List; // <-- IMPORTANT
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;     // <-- java.util.List only

import com.example.demo.domain.ScanResult;
import com.example.demo.domain.WebsiteSubmission;
import com.example.demo.repo.ScanResultRepository;
import com.example.demo.repo.WebsiteSubmissionRepository;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.draw.LineSeparator; // <-- IMPORTANT

@Service
public class PDFReportService {

    private final ScanResultRepository resultRepo;
    private final WebsiteSubmissionRepository submissionRepo;

    @Value("${reports.dir:./reports}")
    private String reportsDir;

    public PDFReportService(
            ScanResultRepository resultRepo,
            WebsiteSubmissionRepository submissionRepo
    ) {
        this.resultRepo = resultRepo;
        this.submissionRepo = submissionRepo;
    }

    public File getOrGenerateReport(WebsiteSubmission s) {
        File dir = new File(reportsDir);
        if (!dir.exists()) dir.mkdirs();

        File file = new File(dir, "report_" + s.getId() + ".pdf");

        if (!file.exists()) {
            generatePdfInternal(s, file.getAbsolutePath());
            s.setPdfReportPath(file.getAbsolutePath());
            submissionRepo.save(s);
        }
        return file;
    }

    public String generatePdf(Long submissionId) {
        WebsiteSubmission s = submissionRepo.findById(submissionId).orElse(null);
        if (s == null) throw new RuntimeException("Submission not found");

        File dir = new File(reportsDir);
        if (!dir.exists()) dir.mkdirs();

        String path = reportsDir + "/report_" + submissionId + ".pdf";
        generatePdfInternal(s, path);

        s.setPdfReportPath(path);
        submissionRepo.save(s);

        return path;
    }

    private void generatePdfInternal(WebsiteSubmission submission, String outPath) {
        try {
            Document doc = new Document(PageSize.A4, 40, 40, 50, 50);
            PdfWriter.getInstance(doc, new FileOutputStream(outPath));
            doc.open();

            // ================= Title =================
            Paragraph title = new Paragraph(
                    "Security Scan Report",
                    new Font(Font.HELVETICA, 22, Font.BOLD)
            );
            title.setAlignment(Element.ALIGN_CENTER);
            doc.add(title);

            doc.add(new Paragraph(" "));
            doc.add(new Paragraph("Target: " + submission.getTargetUrl()));
            doc.add(new Paragraph("Scan Type: " + submission.getScanType()));
            doc.add(new Paragraph("Generated: " + new Date()));
            doc.add(new Paragraph(" "));

            // ================= Executive Summary =================
            Paragraph summaryHeader =
                    new Paragraph("Executive Summary", new Font(Font.HELVETICA, 18, Font.BOLD));
            summaryHeader.setSpacingBefore(10);
            summaryHeader.setSpacingAfter(10);

            doc.add(summaryHeader);

            doc.add(new Paragraph("High: " + submission.getHighCount()));
            doc.add(new Paragraph("Medium: " + submission.getMediumCount()));
            doc.add(new Paragraph("Low: " + submission.getLowCount()));
            doc.add(new Paragraph("Informational: " + submission.getInfoCount()));
            doc.add(new Paragraph(" "));

            // ================= Load & Group Results =================
            List<ScanResult> results = resultRepo.findBySubmissionOrderByRiskScoreDesc(submission);

            Map<String, List<ScanResult>> grouped =
                    results.stream().collect(Collectors.groupingBy(ScanResult::getVulnerabilityType));

            Paragraph findingsHeader =
                    new Paragraph("Detailed Findings", new Font(Font.HELVETICA, 18, Font.BOLD));
            findingsHeader.setSpacingBefore(20);
            findingsHeader.setSpacingAfter(15);

            doc.add(findingsHeader);

            for (String vulnName : grouped.keySet()) {
                List<ScanResult> items = grouped.get(vulnName);

                ScanResult sample = items.get(0);
                Color severityColor = getSeverityColor(sample.getSeverity());

                Paragraph vulnTitle = new Paragraph(
                        vulnName,
                        new Font(Font.HELVETICA, 16, Font.BOLD, severityColor)
                );
                vulnTitle.setSpacingBefore(10);
                vulnTitle.setSpacingAfter(5);
                doc.add(vulnTitle);

                for (ScanResult r : items) {

                    // Affected URLs
                    if (r.getAffectedUrls() != null && !r.getAffectedUrls().isBlank()) {
                        Paragraph urlLabel =
                                new Paragraph("Affected URLs:", new Font(Font.HELVETICA, 12, Font.BOLD));
                        doc.add(urlLabel);

                        Paragraph urls =
                                new Paragraph(r.getAffectedUrls(), new Font(Font.HELVETICA, 12));
                        urls.setIndentationLeft(20);
                        doc.add(urls);
                        doc.add(new Paragraph(" "));
                    }

                    // Description
                    Paragraph descLabel =
                            new Paragraph("Description:", new Font(Font.HELVETICA, 12, Font.BOLD));
                    doc.add(descLabel);

                    Paragraph descText =
                            new Paragraph(r.getDescription(), new Font(Font.HELVETICA, 12));
                    descText.setIndentationLeft(20);
                    doc.add(descText);
                    doc.add(new Paragraph(" "));

                    // Fix
                    Paragraph fixLabel =
                            new Paragraph("Recommended Fix:", new Font(Font.HELVETICA, 12, Font.BOLD));
                    doc.add(fixLabel);

                    Paragraph fixText =
                            new Paragraph(r.getFix(), new Font(Font.HELVETICA, 12));
                    fixText.setIndentationLeft(20);
                    doc.add(fixText);

                    doc.add(new Paragraph(" "));
                    doc.add(new LineSeparator());
                    doc.add(new Paragraph(" "));
                }
            }

            doc.close();

        } catch (Exception ex) {
            throw new RuntimeException("PDF generation failed: " + ex.getMessage(), ex);
        }
    }

    private Color getSeverityColor(String sev) {
        if (sev == null) return Color.GRAY;

        switch (sev.toLowerCase()) {
            case "high":
                return new Color(200, 0, 0);
            case "medium":
                return new Color(255, 140, 0);
            case "low":
                return new Color(255, 215, 0);
            default:
                return Color.GRAY;
        }
    }
}
