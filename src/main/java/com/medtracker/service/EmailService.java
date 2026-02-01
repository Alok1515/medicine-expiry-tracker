package com.medtracker.service;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String senderEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendExpiryAlert(String recipientEmail, String medicineName, String expiryDate, String alertType) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(senderEmail);
            helper.setTo(recipientEmail);

            if ("EXPIRED".equals(alertType)) {
                helper.setSubject("⚠️ Medicine EXPIRED: " + medicineName);
                String body = """
                    <div style="font-family: Arial, sans-serif; max-width: 500px; margin: 0 auto;">
                        <h2 style="color: #c0392b;">⚠️ Medicine Expired</h2>
                        <p style="color: #333;">The following medicine has <strong style="color: #c0392b;">expired</strong> and should be disposed of safely:</p>
                        <ul style="background: #fadbd8; padding: 15px; border-left: 4px solid #c0392b;">
                            <li><strong>Medicine:</strong> %s</li>
                            <li><strong>Expiry date:</strong> %s</li>
                        </ul>
                        <p style="color: #666;">Please dispose of it according to local guidelines and replace if needed.</p>
                    </div>
                    """.formatted(escapeHtml(medicineName), escapeHtml(expiryDate));
                helper.setText(body, true);
            } else if ("EXPIRING_SOON".equals(alertType)) {
                helper.setSubject("🕐 Medicine Expiring Soon: " + medicineName);
                String body = """
                    <div style="font-family: Arial, sans-serif; max-width: 500px; margin: 0 auto;">
                        <h2 style="color: #d68910;">🕐 Medicine Expiring Soon</h2>
                        <p style="color: #333;">The following medicine is <strong style="color: #d68910;">expiring soon</strong>. Please check and replace if needed:</p>
                        <ul style="background: #fdebd0; padding: 15px; border-left: 4px solid #d68910;">
                            <li><strong>Medicine:</strong> %s</li>
                            <li><strong>Expiry date:</strong> %s</li>
                        </ul>
                        <p style="color: #666;">Consider using or replacing this medicine before it expires.</p>
                    </div>
                    """.formatted(escapeHtml(medicineName), escapeHtml(expiryDate));
                helper.setText(body, true);
            }

            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Failed to send expiry alert email: " + e.getMessage());
        }
    }

    private static String escapeHtml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }
}
