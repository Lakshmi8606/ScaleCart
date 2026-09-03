package com.scalecart.notification.service;

import com.scalecart.notification.event.OrderCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.math.BigDecimal;

@Service
public class EmailNotificationService {

    private static final Logger log =
            LoggerFactory.getLogger(EmailNotificationService.class);

    private final JavaMailSender mailSender;

    @Value("${notification.email.from}")
    private String fromEmail;

    public EmailNotificationService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendOrderConfirmationEmail(OrderCreatedEvent event,
                                           String recipientEmail) {
        try {
            MimeMessage message = mailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(fromEmail);
            helper.setTo(recipientEmail);
            helper.setSubject("Order Confirmed! #" + event.getOrderId()
                    + " — ScaleCart");

            String htmlBody = buildOrderConfirmationHtml(event);
            helper.setText(htmlBody, true);

            mailSender.send(message);

            log.info("Order confirmation email sent for orderId={} to={}",
                    event.getOrderId(), recipientEmail);

        } catch (MessagingException e) {
            log.error("Failed to send email for orderId={}: {}",
                    event.getOrderId(), e.getMessage());
            throw new RuntimeException("Email sending failed", e);
        }
    }

    private String buildOrderConfirmationHtml(OrderCreatedEvent event) {
        StringBuilder items = new StringBuilder();

        for (OrderCreatedEvent.OrderItemDetail item : event.getItems()) {
            BigDecimal lineTotal = item.getPrice()
                    .multiply(BigDecimal.valueOf(item.getQuantity()));
            items.append(String.format("""
                    <tr>
                        <td style='padding:8px;border-bottom:1px solid #eee'>%s</td>
                        <td style='padding:8px;border-bottom:1px solid #eee;
                                   text-align:center'>%d</td>
                        <td style='padding:8px;border-bottom:1px solid #eee;
                                   text-align:right'>₹%.2f</td>
                    </tr>
                    """,
                    item.getProductName(),
                    item.getQuantity(),
                    lineTotal));
        }

        return String.format("""
                <html>
                <body style='font-family:Arial,sans-serif;max-width:600px;margin:0 auto'>
                    <div style='background:#2c3e50;padding:20px;text-align:center'>
                        <h1 style='color:white;margin:0'>ScaleCart</h1>
                    </div>
                    <div style='padding:30px'>
                        <h2>Order Confirmed! 🎉</h2>
                        <p>Your order <strong>#%d</strong> has been placed
                           successfully.</p>

                        <table style='width:100%%;border-collapse:collapse'>
                            <tr style='background:#f8f9fa'>
                                <th style='padding:10px;text-align:left'>Item</th>
                                <th style='padding:10px;text-align:center'>Qty</th>
                                <th style='padding:10px;text-align:right'>Total</th>
                            </tr>
                            %s
                        </table>

                        <div style='margin-top:20px;text-align:right'>
                            <strong>Order Total: ₹%.2f</strong>
                        </div>

                        <p style='margin-top:20px;color:#666'>
                            Shipping to: %s
                        </p>
                        <p style='color:#666'>
                            Expected delivery: 3-5 business days
                        </p>
                    </div>
                    <div style='background:#f8f9fa;padding:15px;
                                text-align:center;color:#999;font-size:12px'>
                        © 2024 ScaleCart. All rights reserved.
                    </div>
                </body>
                </html>
                """,
                event.getOrderId(),
                items.toString(),
                event.getTotalAmount(),
                event.getShippingAddress());
    }
}
