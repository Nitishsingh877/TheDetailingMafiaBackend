package com.thederailingmafia.carwash.notificationservice.service;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;

@Service
public class MailService {

    @Autowired
    private JavaMailSender mailSender;

    private final TemplateEngine templateEngine;

    public MailService(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public void sendTemplateEmail(String to,
                                  String subject,
                                  String templateName,
                                  Map<String,Object> variables){

        try {
            Context context = new Context();
            context.setVariables(variables);
            String htmlContent = templateEngine.process(templateName,context);


            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent,true);

            mailSender.send(message);





        }catch (Exception e){
            System.out.println("error while handling mail "  + e.getMessage());
        }
    }
}

