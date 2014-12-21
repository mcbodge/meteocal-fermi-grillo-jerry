/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meteocal.business.boundary;

import com.sun.mail.iap.Protocol;
import java.util.Date;
import java.util.Properties;
import javax.ejb.Stateless;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 *
 * @author Francesco
 */
@Stateless
public class EmailManager {

    private final int PORT = 465;
    private final String HOST = "smtp.mail.com";
    private final String FROM = "meteocal@mail.com";
    private boolean auth = true;
    private final String USERNAME = "meteocal@mail.com";
    private final String PASSWORD = "D235X2uu";
    
    private boolean debug = true;

    public void sendEmail(String to, String subject, String body) {
        Properties props = new Properties();
        props.put("mail.smtp.host", HOST);
        props.put("mail.smtp.port", PORT);
        props.put("mail.smtp.ssl.enable", true);
        
        Authenticator authenticator = null;
        if (auth) {
            props.put("mail.smtp.auth", true);
            authenticator = new Authenticator() {
                private PasswordAuthentication pa = new PasswordAuthentication(USERNAME, PASSWORD);
                @Override
                public PasswordAuthentication getPasswordAuthentication() {
                    return pa;
                }
            };
        }

        Session session = Session.getInstance(props, authenticator);
        session.setDebug(debug);

        MimeMessage message = new MimeMessage(session);
        try {
            message.setFrom(new InternetAddress(FROM));
            InternetAddress[] address = {new InternetAddress(to)};
            message.setRecipients(Message.RecipientType.TO, address);
            message.setSubject(subject);
            message.setSentDate(new Date());
            message.setText(body);
//            Multipart multipart = new MimeMultipart("alternative");
//            
//            MimeBodyPart textPart = new MimeBodyPart();
//            String textContent = "Hi, Nice to meet you!";
//            textPart.setText(textContent);
//
//            MimeBodyPart htmlPart = new MimeBodyPart();
//            String htmlContent = "<html><h1>Hi</h1><p>Nice to meet you!</p></html>";
//            htmlPart.setContent(htmlContent, "text/html");
//
//            multipart.addBodyPart(textPart);
//            multipart.addBodyPart(htmlPart);
//            message.setContent(multipart);
            Transport.send(message);
        } catch (MessagingException ex) {
            ex.printStackTrace();
        }
    }
}