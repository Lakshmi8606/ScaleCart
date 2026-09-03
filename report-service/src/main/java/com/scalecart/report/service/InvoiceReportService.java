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

    public byte[] generateInvoicePdf(InvoiceRequest request) {
        try {
            JasperReport jasperReport = getCompiledReport();

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("orderId",         request.getOrderId());
            parameters.put("userId",          request.getUserId());
            parameters.put("orderDate",       request.getOrderDate());
            parameters.put("shippingAddress", request.getShippingAddress());
            parameters.put("totalAmount",     request.getTotalAmount());
            parameters.put("invoiceNumber",   "INV-" + request.getOrderId()
                    + "-" + System.currentTimeMillis());

            InvoiceItemDataSource dataSource =
                    new InvoiceItemDataSource(request.getItems());

            JasperPrint jasperPrint = JasperFillManager.fillReport(
                    jasperReport, parameters, dataSource);

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
