/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package dynetica.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.activation.*;
import javax.mail.*;
import javax.mail.internet.*;
/**
 *
 * @author chrismurphy
 */
public class BugReport {
    
    final Session session;
    final String host;
    final String to;
    final String user;
    final String auth;
    
    public BugReport(){
        to = "dynetica.bug.report@gmail.com"; 
        host = "smtp.gmail.com";
        user = "dynetica.bug.report@gmail.com";
        auth = "acitenyd";
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.user", user);
        props.put("mail.smtp.SocketFactory.port", "587");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.socketFactory.fallback", "false");
        // Get the default Session object.
        session = Session.getDefaultInstance(props, null);
        session.setDebug(true);
    }
    
    public boolean sendMessage(String name, String date, String from, String msg){
        try{
         // Create a default MimeMessage object.
         MimeMessage message = new MimeMessage(session);
         
         message.setFrom(new InternetAddress(from));

         message.addRecipient(Message.RecipientType.TO,
                                  new InternetAddress(to));

         message.setSubject("Dyentica Bug Report on " + date + " from " + name + " at " + from);

         String fullmessage = "On "+date+", "+name+" reported the following bug:\n"+msg;
         message.setText(fullmessage);
           
         Transport transport = session.getTransport("smtp");
         transport.connect(host, user, auth);
         transport.sendMessage(message, message.getAllRecipients());
         transport.close();
         return true;
         
      }catch (MessagingException mex) {
         mex.printStackTrace();
         return false;
      }
    }
}