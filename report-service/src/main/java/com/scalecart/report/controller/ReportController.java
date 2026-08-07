package com.scalecart.report.controller;

import com.scalecart.report.dto.InvoiceRequest;
import com.scalecart.report.service.InvoiceReportService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private static final Logger log =
            LoggerFactory.getLogger(ReportController.class);

    private final InvoiceReportService reportService;

    public ReportController(InvoiceReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping("/invoice")
    public ResponseEntity<byte[]> generateInvoice(
            @Valid @RequestBody InvoiceRequest request) {

        log.info("Invoice requested for orderId={}", request.getOrderId());

        byte[] pdfBytes = reportService.generateInvoicePdf(request);

        // Tell browser/client this is a PDF file download
        HttpHeaders headers = new HttpHeaders();

        // application/pdf - browser knows it's a PDF
        headers.setContentType(MediaType.APPLICATION_PDF);

        // attachment = download as file (not display inline)
        // filename = what the downloaded file will be named
        headers.setContentDispositionFormData(
                "attachment",
                "invoice-order-" + request.getOrderId() + ".pdf"
        );

        // Content-Length helps browser show download progress bar
        headers.setContentLength(pdfBytes.length);

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }

    // Health check endpoint for this service
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Report Service is running");
    }
}