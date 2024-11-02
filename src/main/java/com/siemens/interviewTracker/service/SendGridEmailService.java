package com.siemens.interviewTracker.service;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import org.springframework.stereotype.Service;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Content;
import org.springframework.beans.factory.annotation.Value;


@Service
public class SendGridEmailService {

    @Value("${sendgrid.api.key}")
    private String sendGridApiKey;

    public String sendEmail(String to, String subject, String body) {
        Email from = new Email("interviewtracker@outlook.com");
        Email recipient = new Email(to);
        Content content = new Content("text/plain", body);
        Mail mail = new Mail(from, subject, recipient, content);

        SendGrid sg = new SendGrid(sendGridApiKey);
        Request request = new Request();

        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
            return "Status Code: " + response.getStatusCode() + ", Response Body: " + response.getBody();
        } catch (Exception ex) {
            ex.printStackTrace();
            return "Error sending email: " + ex.getMessage();
        }
    }
}
