package com.abhishek.authify.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {
    private final JavaMailSender javaMailSender;

    private final String fromemail = "abhispeaking612@gmail.com";

    public void welcomemail(String toemail, String name){
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(fromemail);
        mailMessage.setTo(toemail);
        mailMessage.setSubject("Welcome to the Platform");
        mailMessage.setText("hello "+name+",\n\nThanks for regestering with us! \n\nRegards team Authify");
        javaMailSender.send(mailMessage);
    }

    public void sendresetotp(String tomail,String otp) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(fromemail);
        mailMessage.setTo(tomail);
        mailMessage.setSubject("Reset OTP");
        mailMessage.setText("your password reset OTP is "+ otp+" this will reset your password ");
        javaMailSender.send(mailMessage);
    }

    public void sendverifyotp(String tomail,String otp) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(tomail);
        mailMessage.setSubject("Verify OTP");
        mailMessage.setText("your verify mail  OTP is "+ otp+" this will verfiy your mail ");
        System.out.println("Sending email to: " + tomail);
        System.out.println("From: " + mailMessage.getFrom());
        javaMailSender.send(mailMessage);


    }


}
