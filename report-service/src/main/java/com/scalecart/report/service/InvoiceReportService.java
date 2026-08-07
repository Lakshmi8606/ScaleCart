package com.scalecart.report.service;

import com.scalecart.report.dto.InvoiceRequest;
import net.sf.jasperreports.engine.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Service
public class InvoiceReportService {

    private static final Logger log =
            LoggerFactory.getLogger(InvoiceReportService.class);

    // Compiled report cached at field level — expensive to compile every request
    private JasperReport compiledReport;

    /**
     * Generates a PDF invoice as byte array.
     *
     * Three-step JasperReports pipeline:
     * 1. Compile .jrxml → JasperReport (cached after first call)
     * 2. Fill  JasperReport + data → JasperPrint
     * 3. Export JasperPrint → PDF bytes
     */
    public byte[] generateInvoicePdf(InvoiceRequest request) {
        try {
            // ── Step 1: Compile (cached after first call) ──────────
            JasperReport jasperReport = getCompiledReport();

            // ── Step 2: Build parameters map ──────────────────────
            // Parameters = single values passed to $P{...} in JRXML
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("orderId",         request.getOrderId());
            parameters.put("userId",          request.getUserId());
            parameters.put("orderDate",       request.getOrderDate());
            parameters.put("shippingAddress", request.getShippingAddress());
            parameters.put("totalAmount",     request.getTotalAmount());
            parameters.put("invoiceNumber",   "INV-" + request.getOrderId()
                    + "-" + System.currentTimeMillis());

            // ── Step 3: Build data source ─────────────────────────
            // Data source = repeating rows for $F{...} in detail band
            InvoiceItemDataSource dataSource =
                    new InvoiceItemDataSource(request.getItems());

            // ── Step 4: Fill report ───────────────────────────────
            JasperPrint jasperPrint = JasperFillManager.fillReport(
                    jasperReport, parameters, dataSource);

            // ── Step 5: Export to PDF bytes ───────────────────────
            byte[] pdfBytes = JasperExportManager.exportReportToPdf(jasperPrint);

            log.info("Generated PDF invoice for orderId={}, size={} bytes",
                    request.getOrderId(), pdfBytes.length);

            return pdfBytes;

        } catch (JRException e) {
            log.error("Failed to generate invoice for orderId={}: {}",
                    request.getOrderId(), e.getMessage());
            throw new RuntimeException("PDF generation failed", e);
        }
    }

    /**
     * Compiles the .jrxml template once and caches the result.
     *
     * Why cache? JasperCompileManager.compileReport() reads and parses
     * the XML template — expensive. The compiled JasperReport is
     * thread-safe and reusable. Caching it means the first request
     * pays the compilation cost, all subsequent requests get it free.
     */
    private synchronized JasperReport getCompiledReport() throws JRException {
        if (compiledReport == null) {
            log.info("Compiling JasperReport template (first request only)...");
            try {
                InputStream templateStream = new ClassPathResource(
                        "reports/invoice.jrxml").getInputStream();
                compiledReport = JasperCompileManager.compileReport(templateStream);
                log.info("JasperReport template compiled and cached successfully");
            } catch (Exception e) {
                throw new JRException("Failed to compile report template", e);
            }
        }
        return compiledReport;
    }
}