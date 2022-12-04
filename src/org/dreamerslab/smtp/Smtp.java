package org.dreamerslab.smtp;

import android.app.Activity;
import com.google.appinventor.components.annotations.SimpleEvent;
import com.google.appinventor.components.annotations.SimpleFunction;
import com.google.appinventor.components.runtime.AndroidNonvisibleComponent;
import com.google.appinventor.components.runtime.ComponentContainer;
import com.google.appinventor.components.runtime.EventDispatcher;
import com.google.appinventor.components.runtime.util.AsynchUtil;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;

public class Smtp extends AndroidNonvisibleComponent {

    private final Activity activity;

    public Smtp(ComponentContainer container) {
        super(container.$form());
        activity = container.$context();
    }

    @SimpleFunction(description = "Send message using smtp.")
    public void SendMessage(
            final String smtpHost,
            final String smtpPort,
            final String user,
            final String password,
            final String senderEmail,
            final String receiverEmail,
            final String emailCc,
            final String subject,
            final String content
    ) {
        final Properties properties = new Properties();
        properties.putIfAbsent("mail.smtp.host", smtpHost);
        properties.putIfAbsent("mail.smtp.port", smtpPort);
        properties.putIfAbsent("mail.smtp.auth", "true");
        properties.putIfAbsent("mail.smtp.starttls.enable", "true");

        final Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, password);
            }
        });

        try {
            final MimeMessage mimeMessage = new MimeMessage(session);
            mimeMessage.setFrom(senderEmail);
            mimeMessage.addRecipients(Message.RecipientType.TO, InternetAddress.parse(receiverEmail));
            if (!emailCc.isEmpty()) {
                mimeMessage.addRecipients(Message.RecipientType.CC, InternetAddress.parse(emailCc));
            }

            mimeMessage.setSubject(subject);
            mimeMessage.setText(content);
            mimeMessage.setSentDate(new Date());

            final Runnable runnable = () -> {
                try {
                    final Transport transport = session.getTransport("smtp");
                    transport.connect();
                    transport.sendMessage(mimeMessage, mimeMessage.getAllRecipients());
                    transport.close();

                    activity.runOnUiThread(this::OnSuccess);
                } catch (MessagingException e) {
                    OnError(e.getMessage());
                }
            };

            AsynchUtil.runAsynchronously(runnable);
        } catch (MessagingException e) {
            OnError(e.toString());
        }
    }

    @SimpleEvent(description = "Event raised when message sending fails.")
    public final void OnError(String msg) {
        EventDispatcher.dispatchEvent(this, "OnError", msg);
    }

    @SimpleEvent(description = "Email sent successfully")
    public final void OnSuccess() {
        EventDispatcher.dispatchEvent(this, "OnSuccess");
    }
}
